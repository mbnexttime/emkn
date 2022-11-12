package com.mcs.emkn.ui.courses.viewmodels

import com.mcs.emkn.core.State
import com.mcs.emkn.database.entities.CheckBoxesStateEntity
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow

interface CoursesInteractor {
    val courses: Flow<State<Map<Int, PeriodCourses>>>

    val periods: Flow<State<PeriodsData>>

    val navEventsFlow: Flow<CoursesNavEvents>

    val errorsFlow: Flow<CoursesError>

    fun onPeriodChosen(periodIds: List<Int>)


    fun loadPeriodsAndCourses()

    fun loadCheckBoxesState(): Deferred<CheckBoxesStateEntity>

    fun putCheckBoxesState(state: CheckBoxesStateEntity)

    fun enrollCourse(periodId: Int, courseId: Int)

    fun unenrollCourse(periodId: Int, courseId: Int)
}