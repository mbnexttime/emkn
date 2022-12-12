package com.mcs.emkn.ui.coursepage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mcs.emkn.R
import com.mcs.emkn.core.rv.RecyclerAdapterWithDelegates
import com.mcs.emkn.core.rv.VerticalSpaceDecorator
import com.mcs.emkn.databinding.FragmentCoursePageBinding
import com.mcs.emkn.databinding.FragmentCoursesBinding
import com.mcs.emkn.ui.courses.CoursesAdapter

class CoursePageFragment : Fragment(R.layout.fragment_course_page) {
    private var _binding: FragmentCoursePageBinding? = null
    private val binding get() = _binding!!
    private val adapter = RecyclerAdapterWithDelegates(
        listOf(
            CoursePageAvatarAdapter(), CoursePageDescriptionAdapter()

        ),
        listOf(
            CoursePageAvatarItem(1, "", "Федор Львович Бахарев"),
            CoursePageAvatarItem(2, "", "Басок Михаил Константинович"),
            CoursePageDescriptionItem(1, "Будет больно, но вам понравится")
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCoursePageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.title.text = "Математический анализ"
        binding.coursesRecycler.adapter = adapter
        binding.coursesRecycler.addItemDecoration(
            VerticalSpaceDecorator(
                view.context.resources.getDimensionPixelSize(
                    R.dimen.course_page_items_offset
                )
            )
        )
    }
}