package com.mcs.emkn.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import javax.inject.Inject

class AuthFragment : Fragment() {
    @Inject
    lateinit var authComponent: AuthComponent

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return authComponent.createView(requireContext(), container)
    }
}