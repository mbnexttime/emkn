package com.mcs.emkn.ui.courses.viewmodels

import android.util.Log
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
    override val courses: Flow<State<Map<Int, PeriodCourses>>>
        get() = _coursesFlow
    override val periods: Flow<State<PeriodsData>>
        get() = _periodsFlow
    override val navEventsFlow: Flow<CoursesNavEvents>
        get() = _navEvents
    override val errorsFlow: Flow<CoursesError>
        get() = _errorsFlow

    private val _coursesFlow = MutableSharedFlow<State<Map<Int, PeriodCourses>>>()
    private val _periodsFlow = MutableSharedFlow<State<PeriodsData>>()
    private val _navEvents = MutableSharedFlow<CoursesNavEvents>()
    private val _errorsFlow = MutableSharedFlow<CoursesError>()


    private var isPeriodsUpdated = false
    private var isCoursesUpdated = false
    private var isProfilesUpdated = false

    private val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    override fun onPeriodChosen(periodIds: List<Int>) {
        viewModelScope.launch(dispatcher) {
            _coursesFlow.emit(State(null, true))
            val localStorage = LocalStorage()
            dbInteractor.loadPeriodsFromDb(localStorage)
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
            dbInteractor.loadCoursesFromDb(periodIds, localStorage)
            val chosenIdsSet =
                if (periodIds.isNotEmpty()) periodIds.toSet() else setOf(localStorage.defaultPeriodId)
            val idsNeededForLoading =
                (chosenIdsSet - (localStorage.coursesStorage?.keys ?: setOf()).toSet()).toList()

            if (idsNeededForLoading.isNotEmpty())
                loadCoursesByPeriods(idsNeededForLoading, localStorage)
            emitCourses(periodIds, localStorage)
        }
    }

    override fun loadPeriodsAndCourses() {
        viewModelScope.launch(dispatcher) {
            val localStorage = LocalStorage()
            loadPeriods(localStorage)
            loadCourses(localStorage)
        }
    }

    private suspend fun loadCourses(localStorage: LocalStorage) {
        localStorage.periodsStorage?.let { storage ->
            val requiredPeriods =
                storage.entries.filter { entry -> entry.value.checked }
                    .map { entry -> entry.key }

            _coursesFlow.emit(State(null, true))
            if (!isCoursesUpdated) {
                dbInteractor.loadCoursesFromDb(requiredPeriods, localStorage)
                loadCoursesByPeriods(requiredPeriods, localStorage)
            } else {
                dbInteractor.loadCoursesFromDb(requiredPeriods, localStorage)
            }
            emitCourses(requiredPeriods, localStorage)
        }
    }

    private suspend fun loadPeriods(localStorage: LocalStorage) {
        _periodsFlow.emit(State(null, true))

        if (!isPeriodsUpdated) {
            dbInteractor.loadPeriodsFromDb(localStorage)
            try {
                when (val response = api.coursesPeriods()) {
                    is NetworkResponse.Success -> {
                        isPeriodsUpdated =
                            dbInteractor.updatePeriodsInDb(response.body.periods, localStorage)
                        isCoursesUpdated = false
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
        } else {
            dbInteractor.loadPeriodsFromDb(localStorage)
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

    private suspend fun loadCoursesByPeriods(periodsIds: List<Int>, localStorage: LocalStorage) {
        try {
            when (val response = api.coursesList(CoursesListRequestDto(periodsIds))) {
                is NetworkResponse.Success -> {
                    isCoursesUpdated = dbInteractor.updateCoursesInDb(
                        response.body.coursesByPeriodDto,
                        periodsIds,
                        localStorage
                    )
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
            val profilesIds =
                storage.values.flatMap { courses -> courses.flatMap { it.teachersProfiles } }
                    .toSet()

            localStorage.profilesStorage =
                profileLoader.loadProfilesFromDb(profilesIds.toList())?.toMutableMap()

            val requiredProfileIds = if (isProfilesUpdated)
                profilesIds - (localStorage.profilesStorage?.keys
                    ?: setOf()).toSet() else profilesIds

            if (requiredProfileIds.isNotEmpty()) {
                when (val result = profileLoader.loadProfiles(
                    requiredProfileIds.toList(),
                    localStorage.profilesStorage
                )) {
                    is ProfileLoader.LoadProfilesResult.Success -> {
                        val (newProfileStorage, updateResult) = result.storageAndUpdateResult
                        localStorage.profilesStorage = newProfileStorage?.toMutableMap()
                        isProfilesUpdated = updateResult
                    }
                    ProfileLoader.LoadProfilesResult.NetworkError -> {
                        _errorsFlow.emit(CoursesError.NetworkError)
                    }
                    is ProfileLoader.LoadProfilesResult.UnknownError -> {
                        _errorsFlow.emit(
                            CoursesError.UnknownError(
                                result.msg ?: getProfilesUnknownErrorDefaultMessage
                            )
                        )
                    }
                }
            }
        }
    }

    private suspend fun emitCourses(periodsIds: List<Int>, localStorage: LocalStorage) {
        val periodsIdsSet =
            if (periodsIds.isNotEmpty()) periodsIds.toSet() else setOf(localStorage.defaultPeriodId)
        dbInteractor.loadCheckBoxesStateFromDb(localStorage)
        _coursesFlow.emit(
            State(
                data =
                localStorage.coursesStorage?.filterKeys { it in periodsIdsSet }
                    ?.mapValues { entry ->
                        PeriodCourses(
                            period = localStorage.periodsStorage?.get(entry.key) ?: PeriodEntity(
                                entry.key,
                                "",
                                false,
                                false
                            ),
                            courses = entry.value.filter {
                                it.enrolled && (localStorage.checkBoxesState?.isExcludingEnroll?.not()
                                    ?: true)
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
                        )
                    },
                hasMore = false
            )
        )
    }

    override fun loadCheckBoxesState(): Deferred<CheckBoxesStateEntity> {
        return viewModelScope.async(dispatcher) {
            val localStorage = LocalStorage()
            dbInteractor.loadCheckBoxesStateFromDb(localStorage)
            localStorage.checkBoxesState?.let {
                return@async it
            }
            CheckBoxesStateEntity(false, false)
        }
    }

    override fun putCheckBoxesState(state: CheckBoxesStateEntity) {
        viewModelScope.launch(dispatcher) {
            val localStorage = LocalStorage()
            localStorage.checkBoxesState = state
            dbInteractor.loadPeriodsFromDb(localStorage)
            localStorage.periodsStorage?.let { storage ->
                val periodIds = storage.filterValues { period -> period.checked }.keys.toList()
                dbInteractor.loadCoursesFromDb(periodIds, localStorage)
                emitCourses(periodIds, localStorage)
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
                        dbInteractor.updateCoursesEnrolledStateInDb(courseId, true)
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
                        dbInteractor.updateCoursesEnrolledStateInDb(courseId, false)
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
