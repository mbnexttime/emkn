package com.mcs.emkn.ui.emailconfirmation.viewmodels

sealed class EmailConfirmationError {
    object InvalidCode : EmailConfirmationError()
    object CodeExpired : EmailConfirmationError()
    object BadNetwork : EmailConfirmationError()
}