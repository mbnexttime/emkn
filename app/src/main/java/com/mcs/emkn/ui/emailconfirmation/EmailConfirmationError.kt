package com.mcs.emkn.ui.emailconfirmation

sealed class EmailConfirmationError {
    object InvalidCode : EmailConfirmationError()
    object CodeExpired : EmailConfirmationError()
}