package com.mcs.emkn.ui.courses.viewmodels

import com.mcs.emkn.core.State
import com.mcs.emkn.database.entities.CheckBoxesStateEntity
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow

interface CoursesInteractor {
    val courses: Flow<State<Map<Int, List<Course>>>>

    val periods: Flow<State<PeriodsData>>

    val navEvents: Flow<CoursesNavEvents>

    fun onPeriodChosen(periodIds: List<Int>)

    fun loadCourses()

    fun loadPeriods()

    fun loadCheckBoxesState(): Deferred<CheckBoxesStateEntity>

    fun putCheckBoxesState(state: CheckBoxesStateEntity)

    fun enrollCourse(periodId: Int, courseId: Int)

    fun unenrollCourse(periodId: Int, courseId: Int)
}