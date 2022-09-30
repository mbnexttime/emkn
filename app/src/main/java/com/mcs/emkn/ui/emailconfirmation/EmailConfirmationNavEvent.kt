package com.mcs.emkn.ui.emailconfirmation

sealed class EmailConfirmationNavEvent {
    object ContinueConfirmation : EmailConfirmationNavEvent()
}