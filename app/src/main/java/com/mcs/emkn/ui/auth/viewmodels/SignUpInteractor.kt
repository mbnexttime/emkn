package com.mcs.emkn.ui.auth.viewmodels

import kotlinx.coroutines.flow.Flow

interface SignUpInteractor {
    val isLoadingFlow: Flow<Boolean>

    val errorsFlow: Flow<SignUpError>

    fun onSignUpClick(
        email: String,
        login: String,
        password: String,
        name: String,
        surname: String,
    )
}