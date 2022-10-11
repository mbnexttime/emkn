package com.mcs.emkn.ui.courses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.mcs.emkn.R
import com.mcs.emkn.core.rv.RecyclerAdapterWithDelegates
import com.mcs.emkn.core.rv.VerticalSpaceDecorator
import com.mcs.emkn.databinding.FragmentCoursesBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CoursesFragment : Fragment(R.layout.fragment_courses) {
    private var _binding: FragmentCoursesBinding? = null
    private val binding get() = _binding!!
    private val adapter = RecyclerAdapterWithDelegates(
        listOf(CoursesAdapter()), listOf()
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCoursesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter.setHasStableIds(true)
        binding.coursesRecycler.adapter = adapter
        binding.coursesRecycler.addItemDecoration(
            VerticalSpaceDecorator(
                view.context.resources.getDimensionPixelSize(
                    R.dimen.courses_items_offset
                )
            )
        )
        val catNames: MutableList<String> = ArrayList()
        catNames.add("Барсик")
        catNames.add("Мурзик")
        catNames.add("Рыжик")

        val spinnerAdapter: ArrayAdapter<String> = ArrayAdapter(
            this.requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            catNames
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.coursesSettings.periodChooser.adapter = spinnerAdapter

        adapter.items = listOf(
            CourseItem(
                0,
                "Математический анализ",
                "Фёдор Бахарев",
                "Я вас любил любовь еще быть может в моей душе угасла несовсем Я вас любил любовь еще быть может в моей душе угасла несовсем Я вас любил любовь еще быть может в моей душе угасла несовсем",
                CourseItem.ButtonState.Absence,
            ),
            CourseItem(
                1,
                "Математический анализ",
                "Фёдор Бахарев",
                "Я вас любил любовь еще быть может в моей душе угасла несовсем Я вас любил любовь еще быть может в моей душе угасла несовсем Я вас любил любовь еще быть может в моей душе угасла несовсем",
                CourseItem.ButtonState.Enroll,
            ),
            CourseItem(
                2,
                "Математический анализ",
                "Фёдор Бахарев",
                "Я вас любил любовь еще быть может в моей душе угасла несовсем Я вас любил любовь еще быть может в моей душе угасла несовсем Я вас любил любовь еще быть может в моей душе угасла несовсем",
                CourseItem.ButtonState.Unenroll,
            ),
            CourseItem(
                3,
                "Математический анализ",
                "Фёдор Бахарев",
                "Я вас любил любовь еще быть может в моей душе угасла несовсем Я вас любил любовь еще быть может в моей душе угасла несовсем Я вас любил любовь еще быть может в моей душе угасла несовсем",
                CourseItem.ButtonState.Unenroll,
            ),
            CourseItem(
                4,
                "Математический анализ",
                "Фёдор Бахарев",
                "Я вас любил любовь еще быть может в моей душе угасла несовсем Я вас любил любовь еще быть может в моей душе угасла несовсем Я вас любил любовь еще быть может в моей душе угасла несовсем",
                CourseItem.ButtonState.Unenroll,
            ),
            CourseItem(
                5,
                "Математический анализ",
                "Фёдор Бахарев",
                "Я вас любил любовь еще быть может в моей душе угасла несовсем Я вас любил любовь еще быть может в моей душе угасла несовсем Я вас любил любовь еще быть может в моей душе угасла несовсем",
                CourseItem.ButtonState.Unenroll,
            ),
            CourseItem(
                6,
                "Математический анализ",
                "Фёдор Бахарев",
                "Я вас любил любовь еще быть может в моей душе угасла несовсем Я вас любил любовь еще быть может в моей душе угасла несовсем Я вас любил любовь еще быть может в моей душе угасла несовсем",
                CourseItem.ButtonState.Unenroll,
            ),
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}