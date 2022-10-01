package com.mcs.emkn.core

interface Router {
    fun back()

    fun goToEmailConfirmationScreen()

    fun goToForgotPasswordScreen()

    fun goToChangePasswordConfirmationScreen()

    fun goToCommitChangePasswordScreen()

    fun goToRegistrationNavGraph()

    fun goToUserNavGraph()
}