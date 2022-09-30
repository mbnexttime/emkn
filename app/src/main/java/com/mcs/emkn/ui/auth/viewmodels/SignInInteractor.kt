package com.mcs.emkn.ui.auth.viewmodels

import kotlinx.coroutines.flow.Flow

interface SignInInteractor {
    val isLoadingFlow: Flow<Boolean>

    val errorsFlow: Flow<SignInError>

    fun onSignInClick(login: String, password: String)
}