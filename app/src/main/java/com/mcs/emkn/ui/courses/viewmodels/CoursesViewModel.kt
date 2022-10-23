package com.mcs.emkn.ui.courses.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haroldadmin.cnradapter.NetworkResponse
import com.mcs.emkn.core.State
import com.mcs.emkn.database.entities.CheckBoxesStateEntity
import com.mcs.emkn.database.entities.CourseEntity
import com.mcs.emkn.database.entities.PeriodEntity
import com.mcs.emkn.database.entities.ProfileEntity
import com.mcs.emkn.network.Api
import com.mcs.emkn.network.dto.request.CoursesEnrollUnenrollRequestDto
import com.mcs.emkn.network.dto.request.CoursesListRequestDto
import com.mcs.emkn.ui.profile.viewmodels.ProfileLoader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import javax.inject.Inject

@HiltViewModel
class CoursesViewModel @Inject constructor(
    private val api: Api,
    private val dbInteractor: DbInteractor,
    private val profileLoader: ProfileLoader
) : CoursesInteractor, ViewModel() {
    override val courses: Flow<State<Map<Int, List<Course>>>>
        get() = _coursesFlow
    override val periods: Flow<State<PeriodsData>>
        get() = _periodsFlow
    override val navEventsFlow: Flow<CoursesNavEvents>
        get() = _navEvents
    override val errorsFlow: Flow<CoursesError>
        get() = _errorsFlow

    private val _coursesFlow = MutableSharedFlow<State<Map<Int, List<Course>>>>()
    private val _periodsFlow = MutableSharedFlow<State<PeriodsData>>()
    private val _navEvents = MutableSharedFlow<CoursesNavEvents>()
    private val _errorsFlow = MutableSharedFlow<CoursesError>()


    private val localStorage = LocalStorage()

    private val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    override fun onPeriodChosen(periodIds: List<Int>) {
        viewModelScope.launch(dispatcher) {
            _coursesFlow.emit(State(null, true))
            val idsNeededForUpdate: MutableList<Pair<Int, Boolean>> = mutableListOf()
            localStorage.periodsStorage?.forEach { (id, period) ->
                if (id in periodIds && !period.checked) {
                    idsNeededForUpdate.add(id to true)
                    period.checked = true
                } else if (id !in periodIds && period.checked) {
                    idsNeededForUpdate.add(id to false)
                    period.checked = false
                }
            }
            dbInteractor.updatePeriodsCheckedStateInDb(idsNeededForUpdate)
            val chosenIdsSet =
                if (periodIds.isNotEmpty()) periodIds.toSet() else setOf(localStorage.defaultPeriodId)
            val idsNeededForLoading =
                (chosenIdsSet - (localStorage.coursesStorage?.keys ?: setOf()).toSet()).toList()

            if (idsNeededForLoading.isNotEmpty())
                loadCoursesByPeriods(idsNeededForLoading)
            emitCourses(periodIds)
        }
    }

    override fun loadCourses() {
        viewModelScope.launch(dispatcher) {
            if (localStorage.periodsStorage == null)
                return@launch
            if (localStorage.coursesStorage == null) {
                localStorage.periodsStorage?.let { storage ->
                    _coursesFlow.emit(State(null, true))
                    val requiredPeriods =
                        storage.entries.filter { entry -> entry.value.checked }
                            .map { entry -> entry.key }

                    loadCoursesByPeriods(requiredPeriods)
                    emitCourses(requiredPeriods)
                }
            }
        }
    }

    override fun loadPeriods() {
        viewModelScope.launch(dispatcher) {
            _periodsFlow.emit(State(null, true))

            if (localStorage.periodsStorage == null) {
                dbInteractor.loadPeriodsFromDb(localStorage)
                try {
                    when (val response = api.coursesPeriods()) {
                        is NetworkResponse.Success -> {
                            dbInteractor.updatePeriodsInDb(response.body.periods, localStorage)
                        }
                        is NetworkResponse.ServerError -> {
                            _errorsFlow.emit(
                                CoursesError.UnknownError(
                                    coursesPeriodsUnknownErrorDefaultMessage
                                )
                            )
                        }
                        is NetworkResponse.NetworkError -> {
                            _errorsFlow.emit(CoursesError.NetworkError)
                        }
                        is NetworkResponse.UnknownError -> {
                            _errorsFlow.emit(
                                CoursesError.UnknownError(
                                    response.error.message
                                        ?: coursesPeriodsUnknownErrorDefaultMessage
                                )
                            )
                        }
                    }
                } catch (e: Throwable) {
                    _errorsFlow.emit(
                        CoursesError.UnknownError(
                            e.message ?: coursesPeriodsUnknownErrorDefaultMessage
                        )
                    )
                }
            }
            localStorage.periodsStorage?.let {
                if (it.isNotEmpty())
                    localStorage.defaultPeriodId =
                        it.entries.find { entry -> entry.value.isDefault }?.key ?: it.keys.first()
            }

            _periodsFlow.emit(
                State(
                    data = PeriodsData(
                        localStorage.periodsStorage?.values?.toList() ?: listOf()
                    ),
                    hasMore = false
                )
            )
        }
    }

    private suspend fun loadCoursesByPeriods(periodsIds: List<Int>) {
        if (periodsIds.isNotEmpty())
            dbInteractor.loadCoursesFromDb(periodsIds, localStorage)
        else
            dbInteractor.loadCoursesFromDb(listOf(localStorage.defaultPeriodId), localStorage)
        try {
            when (val response = api.coursesList(CoursesListRequestDto(periodsIds))) {
                is NetworkResponse.Success -> {
                    dbInteractor.updateCoursesInDb(response.body.coursesByPeriodDto, periodsIds, localStorage)
                }
                is NetworkResponse.ServerError -> {
                    response.body?.invalid_period_id?.let {
                        _errorsFlow.emit(CoursesError.CoursesListInvalidPeriodId)
                    }
                }
                is NetworkResponse.NetworkError -> {
                    _errorsFlow.emit(CoursesError.NetworkError)
                }
                is NetworkResponse.UnknownError -> {
                    _errorsFlow.emit(
                        CoursesError.UnknownError(
                            response.error.message ?: coursesListUnknownErrorDefaultMessage
                        )
                    )
                }
            }
        } catch (e: Throwable) {
            _errorsFlow.emit(
                CoursesError.UnknownError(
                    e.message ?: coursesListUnknownErrorDefaultMessage
                )
            )
        }

        localStorage.coursesStorage?.let { storage ->
            val notSavedProfileIds =
                storage.values.flatMap { courses -> courses.flatMap { it.teachersProfiles } }
                    .toSet() - (localStorage.profilesStorage?.keys ?: setOf()).toSet()
            when(val result = profileLoader.loadProfiles(notSavedProfileIds.toList(), localStorage.profilesStorage)) {
                is ProfileLoader.LoadProfilesResult.Success -> {
                   localStorage.profilesStorage = result.storage?.toMutableMap()
                }
                ProfileLoader.LoadProfilesResult.NetworkError -> {
                    _errorsFlow.emit(CoursesError.NetworkError)
                }
                is ProfileLoader.LoadProfilesResult.UnknownError -> {
                    _errorsFlow.emit(CoursesError.UnknownError(result.msg ?: getProfilesUnknownErrorDefaultMessage))
                }
            }
        }
    }

    private suspend fun emitCourses(periodsIds: List<Int>) {
        val periodsIdsSet = if(periodsIds.isNotEmpty()) periodsIds.toSet() else setOf(localStorage.defaultPeriodId)
       _coursesFlow.emit(
            State(
                data =
                localStorage.coursesStorage?.filterKeys { it in periodsIdsSet }?.mapValues { entry ->
                    entry.value.filter {
                        it.enrolled && (localStorage.checkBoxesState?.isExcludingEnroll?.not() ?: true)
                                || !it.enrolled && (localStorage.checkBoxesState?.isExcludingUnenroll?.not()
                            ?: true)
                    }.map {
                        Course(
                            entry.key,
                            it.id,
                            it.title,
                            it.enrolled,
                            it.shortDescription,
                            it.teachersProfiles.map { id ->
                                localStorage.profilesStorage?.get(id)
                                    ?: ProfileEntity(id, "", "", "")
                            }
                        )
                    }
                },
                hasMore = false
            )
        )
    }

    override fun loadCheckBoxesState(): Deferred<CheckBoxesStateEntity> {
        return viewModelScope.async(dispatcher) {
            localStorage.checkBoxesState?.let {
                return@async it
            }

            dbInteractor.loadCheckBoxesStateFromDb(localStorage)
            localStorage.checkBoxesState?.let {
                return@async it
            }
            CheckBoxesStateEntity(false, false)
        }
    }

    override fun putCheckBoxesState(state: CheckBoxesStateEntity) {
        viewModelScope.launch(dispatcher) {
            localStorage.checkBoxesState = state
            localStorage.periodsStorage?.let { storage ->
                emitCourses(storage.filterValues { period -> period.checked }.keys.toList())
            }
            dbInteractor.updateCheckBoxesStateInDb(state)
        }
    }

    override fun enrollCourse(periodId: Int, courseId: Int) {
        viewModelScope.launch(dispatcher) {
            try {
                when (val response = api.coursesEnroll(
                    CoursesEnrollUnenrollRequestDto(courseId)
                )) {
                    is NetworkResponse.Success -> {
                        val course = localStorage.coursesStorage?.get(periodId)?.find { it.id == courseId }
                        course?.let {
                            it.enrolled = true
                            dbInteractor.updateCoursesEnrolledStateInDb(courseId, true)
                        }
                    }
                    is NetworkResponse.ServerError -> {
                        response.body?.invalidCourseId?.let {
                            _errorsFlow.emit(CoursesError.CoursesEnrollInvalidCourseId)
                        }
                        response.body?.alreadyEnrolled?.let {
                            _errorsFlow.emit(CoursesError.CoursesEnrollAlreadyEnrolled)
                        }
                    }
                    is NetworkResponse.NetworkError -> {
                        _errorsFlow.emit(CoursesError.NetworkError)
                    }
                    is NetworkResponse.UnknownError -> {
                        _errorsFlow.emit(
                            CoursesError.UnknownError(
                                response.error.message ?: coursesEnrollUnknownErrorDefaultMessage
                            )
                        )
                    }
                }
            } catch (e: Throwable) {
                _errorsFlow.emit(
                    CoursesError.UnknownError(
                        e.message ?: coursesEnrollUnknownErrorDefaultMessage
                    )
                )
            }
        }
    }

    override fun unenrollCourse(periodId: Int, courseId: Int) {
        viewModelScope.launch(dispatcher) {
            try {
                when (val response = api.coursesUnenroll(
                    CoursesEnrollUnenrollRequestDto(courseId)
                )) {
                    is NetworkResponse.Success -> {
                        val course = localStorage.coursesStorage?.get(periodId)?.find { it.id == courseId }
                        course?.let {
                            it.enrolled = false
                            dbInteractor.updateCoursesEnrolledStateInDb(courseId, false)
                        }
                    }
                    is NetworkResponse.ServerError -> {
                        response.body?.invalidCourseId?.let {
                            _errorsFlow.emit(CoursesError.CoursesUnenrollInvalidCourseId)
                        }
                        response.body?.courseIsNotEnrolled?.let {
                            _errorsFlow.emit(CoursesError.CoursesUnenrollCourseIsNotEnrolled)
                        }
                    }
                    is NetworkResponse.NetworkError -> {
                        _errorsFlow.emit(CoursesError.NetworkError)
                    }
                    is NetworkResponse.UnknownError -> {
                        _errorsFlow.emit(
                            CoursesError.UnknownError(
                                response.error.message ?: coursesUnenrollUnknownErrorDefaultMessage
                            )
                        )
                    }
                }
            } catch (e: Throwable) {
                _errorsFlow.emit(
                    CoursesError.UnknownError(
                        e.message ?: coursesUnenrollUnknownErrorDefaultMessage
                    )
                )
            }
        }
    }



    companion object {
        const val coursesListUnknownErrorDefaultMessage =
            "Неизвестная ошибка во время загрузки списка курсов"
        const val coursesPeriodsUnknownErrorDefaultMessage =
            "Неизвестная ошибка во время загрузки списка периодов"
        const val getProfilesUnknownErrorDefaultMessage =
            "Неизвестная ошибка во время загрузки профилей"
        const val coursesEnrollUnknownErrorDefaultMessage =
            "Неизвестная ошибка во время попытки подписаться на курс"
        const val coursesUnenrollUnknownErrorDefaultMessage =
            "Неизвестная ошибка во время попытки отписаться от курса"
        const val dbUnknownErrorDefaultMessage =
            "Неизвестная ошибка во время чтения/записи из хранилища"
    }

    data class LocalStorage(
        var coursesStorage: MutableMap<Int, List<CourseEntity>>? = null,
        var periodsStorage: MutableMap<Int, PeriodEntity>? = null,
        var profilesStorage: MutableMap<Int, ProfileEntity>? = null,
        var checkBoxesState: CheckBoxesStateEntity? = null,
        var defaultPeriodId: Int = 0
    )
}
