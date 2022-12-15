package com.mcs.emkn.ui.coursehomeworks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mcs.emkn.R
import com.mcs.emkn.core.Router
import com.mcs.emkn.core.rv.RecyclerAdapterWithDelegates
import com.mcs.emkn.core.rv.VerticalSpaceDecorator
import com.mcs.emkn.databinding.FragmentCourseHomeworksBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CourseHomeworksFragment : Fragment(R.layout.fragment_course_homeworks) {
    @Inject
    lateinit var router: Router

    private var _binding: FragmentCourseHomeworksBinding? = null
    private val binding get() = _binding!!
    private val adapter = RecyclerAdapterWithDelegates(
        listOf(
            CourseHomeworksTaskAdapter(),
            CourseHomeworksSectionAdapter()
        ),
        listOf(
            CourseHomeworksSectionItem(1, "Открытые задания"),
            CourseHomeworksTaskItem(
                2,
                "Лабораторная работа №3\nРазмещения Дирихле",
                "10 декабря 2022",
                "23:59",
                CourseHomeworksTaskItem.HomeworkStatus.NotSubmitted
            ),
            CourseHomeworksTaskItem(
                3,
                "Теоретическое задание № 2:\nВариационный вывод",
                "01 октября 2022",
                "23:59",
                CourseHomeworksTaskItem.HomeworkStatus.NotChecked
            ),
            CourseHomeworksSectionItem(4, "Архив"),
            CourseHomeworksTaskItem(
                5,
                "Практическое задание № 1:\nБайесовские рассуждения",
                "23 сентября 2022",
                "23:59",
                CourseHomeworksTaskItem.HomeworkStatus.Checked(10, 10)
            )
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCourseHomeworksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.coursesRecycler.adapter = adapter
        binding.coursesRecycler.addItemDecoration(
            VerticalSpaceDecorator(
                view.context.resources.getDimensionPixelSize(
                    R.dimen.courses_items_offset
                )
            )
        )
        binding.toCourseDescriptionButtonArrow.setOnClickListener {
            router.back()
        }
    }
}