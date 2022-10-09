package com.mcs.emkn.ui.courses.viewmodels

import com.mcs.emkn.core.State
import kotlinx.coroutines.flow.Flow

interface CoursesInteractor {
    val courses: Flow<State<List<Course>>>

    val periods: Flow<State<List<Period>>>

    val navEvents: Flow<CoursesNavEvents>

    fun onPeriodChosen(periodId: Int)

    fun loadCourses()

    fun loadPeriods()
}