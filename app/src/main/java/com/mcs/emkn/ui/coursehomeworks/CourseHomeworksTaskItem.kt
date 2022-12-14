package com.mcs.emkn.ui.coursehomeworks

import com.mcs.emkn.core.rv.Item


data class CourseHomeworksTaskItem(
    val id: Int,
    val title: String,
    val dayMonth: String,
    val time: String,
    val status: HomeworkStatus
) : Item {
    sealed interface HomeworkStatus {
        object NotSubmitted: HomeworkStatus
        object NotChecked: HomeworkStatus
        data class Checked(val score: Int, val maximum: Int): HomeworkStatus
    }

    override fun getItemId(): Long {
        return id.toLong()
    }
}