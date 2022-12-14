package com.mcs.emkn.ui.coursepage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mcs.emkn.R
import com.mcs.emkn.core.Router
import com.mcs.emkn.core.rv.RecyclerAdapterWithDelegates
import com.mcs.emkn.core.rv.VerticalSpaceDecorator
import com.mcs.emkn.databinding.FragmentCoursePageBinding
import com.mcs.emkn.ui.profile.viewmodels.Profile
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CoursePageFragment : Fragment(R.layout.fragment_course_page) {
    @Inject
    lateinit var router: Router

    private var _binding: FragmentCoursePageBinding? = null
    private val binding get() = _binding!!
    private val adapter = RecyclerAdapterWithDelegates(
        listOf(
            CoursePageAvatarAdapter(), CoursePageDescriptionAdapter()

        ),
        listOf()
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
        binding.title.text = arguments?.getString("title") ?: "..."
        val avatars =  arguments?.getParcelableArray("profiles")?.mapIndexed { id, parcel ->
            val profile = parcel as Profile
            CoursePageAvatarItem(id, profile.avatarUri.toString(), profile.firstName + "\n" + profile.secondName)
        }
        avatars?.let {
            adapter.items += it
        }
        val description = arguments?.getString("description")?.let {
            CoursePageDescriptionItem(adapter.items.size, it)
        }
        description?.let {
            adapter.items += it
        }
        binding.coursePageRecycler.adapter = adapter
        binding.coursePageRecycler.addItemDecoration(
            VerticalSpaceDecorator(
                view.context.resources.getDimensionPixelSize(
                    R.dimen.course_page_items_offset
                )
            )
        )
        binding.homeWorksButtonArrow.setOnClickListener {
            router.goToCourseHomeworks()
        }
    }
}