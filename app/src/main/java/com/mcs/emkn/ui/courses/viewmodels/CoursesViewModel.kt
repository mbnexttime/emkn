package com.mcs.emkn.ui.courses.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haroldadmin.cnradapter.NetworkResponse
import com.mcs.emkn.core.State
import com.mcs.emkn.database.Database
import com.mcs.emkn.database.entities.CheckBoxesStateEntity
import com.mcs.emkn.database.entities.CourseEntity
import com.mcs.emkn.database.entities.PeriodEntity
import com.mcs.emkn.database.entities.ProfileEntity
import com.mcs.emkn.network.Api
import com.mcs.emkn.network.dto.request.CoursesEnrollUnenrollRequestDto
import com.mcs.emkn.network.dto.request.CoursesListRequestDto
import com.mcs.emkn.network.dto.request.ProfilesGetRequestDto
import com.mcs.emkn.network.dto.response.PeriodCoursesDto
import com.mcs.emkn.network.dto.response.PeriodDto
import com.mcs.emkn.network.dto.response.ProfileDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class CoursesViewModel @Inject constructor(
    private val api: Api,
    private val db: Database
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
    private val isLoadingAtomic = AtomicBoolean(false)

    private var coursesStorage: MutableMap<Int, List<CourseEntity>>? = null
    private var periodsStorage: MutableMap<Int, PeriodEntity>? = null
    private var profilesStorage: MutableMap<Int, ProfileEntity>? = null
    private var checkBoxesState: CheckBoxesStateEntity? = null

    private var defaultPeriodId: Int = 0

    override fun onPeriodChosen(periodIds: List<Int>) {
        val idsNeededForUpdate: MutableList<Pair<Int, Boolean>> = mutableListOf()
        periodsStorage?.forEach { (id, period) ->
            if (id in periodIds && !period.checked) {
                idsNeededForUpdate.add(id to true)
                period.checked = true
            } else if (id !in periodIds && period.checked) {
                idsNeededForUpdate.add(id to false)
                period.checked = false
            }
        }
        updatePeriodsCheckedStateInDb(idsNeededForUpdate)
        val chosenIdsSet = if (periodIds.isNotEmpty()) periodIds.toSet() else setOf(defaultPeriodId)
        val idsNeededForLoading =
            (chosenIdsSet - (coursesStorage?.keys ?: setOf()).toSet()).toList()

        viewModelScope.launch {
            if (idsNeededForLoading.isNotEmpty())
                loadCoursesByPeriods(idsNeededForLoading)
            emitCourses(periodIds)
        }
    }

    override fun loadCourses() {
        if (periodsStorage == null)
            return
        periodsStorage?.let { storage ->
            val requiredPeriods =
                storage.entries.filter { entry -> entry.value.checked }.map { entry -> entry.key }

            if (coursesStorage == null) {
                viewModelScope.launch(Dispatchers.IO) {
                    loadCoursesByPeriods(requiredPeriods)
                    emitCourses(requiredPeriods)
                }
            }
        }
    }

    override fun loadPeriods() {
        if (isLoadingAtomic.get())
            return
        viewModelScope.launch(Dispatchers.IO) {
            if (periodsStorage == null) {
                if (!isLoadingAtomic.compareAndSet(false, true))
                    return@launch
                _periodsFlow.emit(State(null, true))
                loadPeriodsFromDb()
                try {
                    when (val response = api.coursesPeriods()) {
                        is NetworkResponse.Success -> {
                            updatePeriodsInDb(response.body.periods)
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
                isLoadingAtomic.compareAndSet(true, false)
            }
            periodsStorage?.let {
                if (it.isNotEmpty())
                    defaultPeriodId =
                        it.entries.find { entry -> entry.value.isDefault }?.key ?: it.keys.first()
            }

            _periodsFlow.emit(
                State(
                    data = PeriodsData(
                        periodsStorage?.values?.toList() ?: listOf()
                    ),
                    hasMore = false
                )
            )
        }
    }

    private suspend fun loadCoursesByPeriods(periodsIds: List<Int>) {
        if (isLoadingAtomic.get())
            return
        if (!isLoadingAtomic.compareAndSet(false, true))
            return
        _coursesFlow.emit(State(null, true))
        if (periodsIds.isNotEmpty())
            loadCoursesFromDb(periodsIds)
        else
            loadCoursesFromDb(listOf(defaultPeriodId))
        try {
            when (val response = api.coursesList(CoursesListRequestDto(periodsIds))) {
                is NetworkResponse.Success -> {
                    updateCoursesInDb(response.body.coursesByPeriodDto, periodsIds)
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

        coursesStorage?.let { storage ->
            val notSavedProfileIds =
                storage.values.flatMap { courses -> courses.flatMap { it.teachersProfiles } }
                    .toSet() - (profilesStorage?.keys ?: setOf()).toSet()
            loadProfiles(notSavedProfileIds.toList())
        }
        isLoadingAtomic.compareAndSet(true, false)

    }

    private suspend fun emitCourses(periodsIds: List<Int>) {
        _coursesFlow.emit(
            State(
                data =
                coursesStorage?.filterKeys { it in periodsIds }?.mapValues { entry ->
                    entry.value.filter {
                        it.enrolled && (checkBoxesState?.isExcludingEnroll?.not() ?: true)
                                || !it.enrolled && (checkBoxesState?.isExcludingUnenroll?.not()
                            ?: true)
                    }.map {
                        Course(
                            entry.key,
                            it.id,
                            it.title,
                            it.enrolled,
                            it.shortDescription,
                            it.teachersProfiles.map { id ->
                                profilesStorage?.get(id)
                                    ?: ProfileEntity(id, "", "", "")
                            }
                        )
                    }
                },
                hasMore = false
            )
        )
    }

    private suspend fun loadProfiles(profilesIds: List<Int>) {
        loadProfilesFromDb(profilesIds)
        try {
            when (val response = api.profilesGet(ProfilesGetRequestDto(profilesIds))) {
                is NetworkResponse.Success -> {
                    updateProfilesInDb(response.body.profiles, profilesIds)
                }
                is NetworkResponse.ServerError -> {
                    _errorsFlow.emit(CoursesError.UnknownError(getProfilesUnknownErrorDefaultMessage))
                }
                is NetworkResponse.NetworkError -> {
                    _errorsFlow.emit(CoursesError.NetworkError)
                }
                is NetworkResponse.UnknownError -> {
                    _errorsFlow.emit(
                        CoursesError.UnknownError(
                            response.error.message ?: getProfilesUnknownErrorDefaultMessage
                        )
                    )
                }
            }
        } catch (e: Throwable) {
            _errorsFlow.emit(
                CoursesError.UnknownError(
                    e.message ?: getProfilesUnknownErrorDefaultMessage
                )
            )
        }
    }

    override fun loadCheckBoxesState(): Deferred<CheckBoxesStateEntity> {
        return viewModelScope.async(Dispatchers.IO) {
            checkBoxesState?.let {
                return@async it
            }
            try {
                val newState =
                    db.coursesDao().getCheckBoxState().firstOrNull() ?: CheckBoxesStateEntity(
                        false,
                        false
                    )
                checkBoxesState = newState
                newState
            } catch (e: Throwable) {
                CheckBoxesStateEntity(false, false)
            }
        }

    }

    override fun putCheckBoxesState(state: CheckBoxesStateEntity) {
        checkBoxesState = state
        viewModelScope.launch(Dispatchers.IO) {
            periodsStorage?.let { storage ->
                emitCourses(storage.filterValues { period -> period.checked }.keys.toList())
            }
            try {
                db.coursesDao().deleteCheckBoxState()
                db.coursesDao().putCheckBoxState(state)
            } catch (e: Throwable) {
                _errorsFlow.emit(CoursesError.DbError(e.message ?: dbUnknownErrorDefaultMessage))
            }
        }
    }

    override fun enrollCourse(periodId: Int, courseId: Int) {
        if (isLoadingAtomic.get())
            return
        viewModelScope.launch(Dispatchers.IO) {
            if (!isLoadingAtomic.compareAndSet(false, true))
                return@launch
            try {
                when (val response = api.coursesEnroll(
                    CoursesEnrollUnenrollRequestDto(courseId)
                )) {
                    is NetworkResponse.Success -> {
                        val course = coursesStorage?.get(periodId)?.find { it.id == courseId }
                        course?.let {
                            it.enrolled = true
                            updateCoursesEnrolledStateInDb(courseId, true)
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
            isLoadingAtomic.compareAndSet(true, false)
        }
    }

    override fun unenrollCourse(periodId: Int, courseId: Int) {
        if (isLoadingAtomic.get())
            return
        viewModelScope.launch(Dispatchers.IO) {
            if (!isLoadingAtomic.compareAndSet(false, true))
                return@launch
            try {
                when (val response = api.coursesUnenroll(
                    CoursesEnrollUnenrollRequestDto(courseId)
                )) {
                    is NetworkResponse.Success -> {
                        val course = coursesStorage?.get(periodId)?.find { it.id == courseId }
                        course?.let {
                            it.enrolled = false
                            updateCoursesEnrolledStateInDb(courseId, false)
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
            isLoadingAtomic.compareAndSet(true, false)
        }
    }

    private suspend fun loadCoursesFromDb(periodsIds: List<Int>) {
        try {
            coursesStorage = coursesStorage ?: mutableMapOf()
            coursesStorage?.plusAssign(
                db.coursesDao().getCoursesByPeriods(periodsIds).groupBy { it.periodId })
        } catch (e: Throwable) {
            _errorsFlow.emit(CoursesError.DbError(e.message ?: dbUnknownErrorDefaultMessage))
        }
    }


    private suspend fun updateCoursesInDb(courses: List<PeriodCoursesDto>, periodsIds: List<Int>) {
        coursesStorage = coursesStorage ?: mutableMapOf()
        courses.forEach { periodCoursesDto ->
            coursesStorage?.put(periodCoursesDto.periodId, periodCoursesDto.courses.map {
                CourseEntity(
                    periodCoursesDto.periodId,
                    it.id,
                    it.title,
                    it.enrolled,
                    it.shortDescription,
                    it.teachersProfiles
                )
            })
        }

        try {
            coursesStorage?.let { storage ->
                db.coursesDao()
                    .putCourses(storage.entries.filter { entry -> entry.key in periodsIds }
                        .flatMap { entry -> entry.value })
            }
        } catch (e: Throwable) {
            _errorsFlow.emit(CoursesError.DbError(e.message ?: dbUnknownErrorDefaultMessage))
        }
    }

    private suspend fun loadPeriodsFromDb() {
        try {
            periodsStorage =
                db.coursesDao().getPeriods().groupBy { it.id }.mapValues { (_, v) -> v.first() }
                    .toMutableMap()
        } catch (e: Throwable) {
            _errorsFlow.emit(CoursesError.DbError(e.message ?: dbUnknownErrorDefaultMessage))
        }
    }

    private suspend fun updatePeriodsInDb(periods: List<PeriodDto>) {
        val newPeriods = mutableMapOf<Int, PeriodEntity>()
        periods.forEachIndexed { i, periodDto ->
            periodsStorage?.also { storage ->
                if (periodDto.id in storage.keys) {
                    val oldPeriod = storage.getValue(periodDto.id)
                    newPeriods[periodDto.id] =
                        PeriodEntity(oldPeriod.id, periodDto.text, oldPeriod.checked, i == 0)
                } else {
                    newPeriods[periodDto.id] =
                        PeriodEntity(periodDto.id, periodDto.text, false, i == 0)
                }
            } ?: run {
                newPeriods[periodDto.id] = PeriodEntity(periodDto.id, periodDto.text, false, i == 0)
            }
        }
        periodsStorage = newPeriods

        try {
            periodsStorage?.let {
                db.coursesDao().deletePeriods()
                db.coursesDao().putPeriods(it.values.toList())
            }
        } catch (e: Throwable) {
            _errorsFlow.emit(CoursesError.DbError(e.message ?: dbUnknownErrorDefaultMessage))
        }
    }

    private suspend fun loadProfilesFromDb(periodsIds: List<Int>) {
        try {
            profilesStorage = profilesStorage ?: mutableMapOf()
            profilesStorage?.plusAssign(
                db.coursesDao().getProfilesByIds(periodsIds).groupBy { it.id }
                    .mapValues { (_, v) -> v.first() })
        } catch (e: Throwable) {
            _errorsFlow.emit(CoursesError.DbError(e.message ?: dbUnknownErrorDefaultMessage))
        }
    }

    private suspend fun updateProfilesInDb(profiles: List<ProfileDto>, profilesIds: List<Int>) {
        profilesStorage = profilesStorage ?: mutableMapOf()
        profiles.forEach { profileDto ->
            profilesStorage?.put(
                profileDto.id,
                ProfileEntity(
                    profileDto.id,
                    profileDto.avatarUrl,
                    profileDto.firstName,
                    profileDto.secondName
                )
            )
        }

        try {
            coursesStorage?.let { storage ->
                db.coursesDao()
                    .putCourses(storage.entries.filter { entry -> entry.key in profilesIds }
                        .flatMap { entry -> entry.value })
            }
        } catch (e: Throwable) {
            _errorsFlow.emit(CoursesError.DbError(e.message ?: dbUnknownErrorDefaultMessage))
        }
    }

    private fun updatePeriodsCheckedStateInDb(periodsIds: List<Pair<Int, Boolean>>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                periodsIds.forEach { (id, checked) ->
                    db.coursesDao().updatePeriodCheckedState(id, checked)
                }
            } catch (e: Throwable) {
                _errorsFlow.emit(CoursesError.DbError(e.message ?: dbUnknownErrorDefaultMessage))
            }
        }
    }

    private fun updateCoursesEnrolledStateInDb(id: Int, enrolled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                db.coursesDao().updateCourseEnrolledState(id, enrolled)
            } catch (e: Throwable) {
                _errorsFlow.emit(CoursesError.DbError(e.message ?: dbUnknownErrorDefaultMessage))
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
}
