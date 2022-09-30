package com.mcs.emkn.ui.emailconfirmation

import kotlinx.coroutines.flow.Flow

interface EmailConfirmationInteractor {
    val errors: Flow<EmailConfirmationError>

    val navEvents: Flow<EmailConfirmationNavEvent>

    fun submitCode(code: String)
}