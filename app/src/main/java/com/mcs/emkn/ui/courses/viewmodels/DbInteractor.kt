package com.mcs.emkn.ui.courses.viewmodels

import com.mcs.emkn.database.Database
import com.mcs.emkn.database.entities.CheckBoxesStateEntity
import com.mcs.emkn.database.entities.CourseEntity
import com.mcs.emkn.database.entities.PeriodEntity
import com.mcs.emkn.database.entities.ProfileEntity
import com.mcs.emkn.network.dto.response.PeriodCoursesDto
import com.mcs.emkn.network.dto.response.PeriodDto
import com.mcs.emkn.network.dto.response.ProfileDto
import javax.inject.Inject

class DbInteractor @Inject constructor(
    val db: Database
) {
    fun loadCoursesFromDb(periodsIds: List<Int>, localStorage: CoursesViewModel.LocalStorage) {
        try {
            localStorage.coursesStorage = localStorage.coursesStorage ?: mutableMapOf()
            localStorage.coursesStorage?.plusAssign(
                db.coursesDao().getCoursesByPeriods(periodsIds).groupBy { it.periodId })
        } catch (_: Throwable) {
        }
    }


    fun updateCoursesInDb(
        courses: List<PeriodCoursesDto>,
        periodsIds: List<Int>,
        localStorage: CoursesViewModel.LocalStorage
    ) {
        localStorage.coursesStorage = localStorage.coursesStorage ?: mutableMapOf()
        courses.forEach { periodCoursesDto ->
            localStorage.coursesStorage?.put(
                periodCoursesDto.periodId,
                periodCoursesDto.courses.map {
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
            localStorage.coursesStorage?.let { storage ->
                db.coursesDao()
                    .putCourses(storage.entries.filter { entry -> entry.key in periodsIds }
                        .flatMap { entry -> entry.value })
            }
        } catch (_: Throwable) {

        }
    }

    fun loadPeriodsFromDb(localStorage: CoursesViewModel.LocalStorage) {
        try {
            localStorage.periodsStorage =
                db.coursesDao().getPeriods().groupBy { it.id }.mapValues { (_, v) -> v.first() }
                    .toMutableMap()
        } catch (_: Throwable) {

        }
    }

    fun updatePeriodsInDb(
        periods: List<PeriodDto>,
        localStorage: CoursesViewModel.LocalStorage
    ) {
        val newPeriods = mutableMapOf<Int, PeriodEntity>()
        periods.forEachIndexed { i, periodDto ->
            localStorage.periodsStorage?.also { storage ->
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
        localStorage.periodsStorage = newPeriods

        try {
            localStorage.periodsStorage?.let {
                db.coursesDao().deletePeriods()
                db.coursesDao().putPeriods(it.values.toList())
            }
        } catch (_: Throwable) {

        }
    }


    fun updatePeriodsCheckedStateInDb(
        periodsIds: List<Pair<Int, Boolean>>,
    ) {
        try {
            periodsIds.forEach { (id, checked) ->
                db.coursesDao().updatePeriodCheckedState(id, checked)
            }
        } catch (_: Throwable) {

        }
    }

    fun updateCoursesEnrolledStateInDb(
        id: Int,
        enrolled: Boolean,
    ) {
        try {
            db.coursesDao().updateCourseEnrolledState(id, enrolled)
        } catch (_: Throwable) {

        }
    }

    fun loadCheckBoxesStateFromDb(localStorage: CoursesViewModel.LocalStorage) {
        try {
            localStorage.checkBoxesState =
                db.coursesDao().getCheckBoxState().firstOrNull() ?: CheckBoxesStateEntity(
                    false,
                    false
                )
        } catch (_: Throwable) {

        }
    }

    fun updateCheckBoxesStateInDb(
        newState: CheckBoxesStateEntity,
    ) {
        try {
            db.coursesDao().deleteCheckBoxState()
            db.coursesDao().putCheckBoxState(newState)
        } catch (_: Throwable) {

        }
    }
}