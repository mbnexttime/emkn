package com.mcs.emkn.ui.courses

import android.view.ViewGroup
import com.mcs.emkn.core.rv.Item
import com.mcs.emkn.core.rv.RecyclerDelegate

class CoursesAdapter : RecyclerDelegate<CourseViewHolder, CourseItem> {
    override fun onBindViewHolder(viewHolder: CourseViewHolder, item: CourseItem) {
        viewHolder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup): CourseViewHolder {
        return CourseViewHolder(parent)
    }

    override fun matchesItem(item: Item): Boolean {
        return item is CourseItem
    }
}