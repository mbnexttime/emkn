package com.mcs.emkn.ui.profile.viewmodels

import com.mcs.emkn.core.State
import kotlinx.coroutines.flow.Flow

interface ProfileInteractor {
    val profile: Flow<State<Profile>>

    fun loadSelfProfile()
}