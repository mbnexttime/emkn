package com.mcs.emkn.core

import androidx.navigation.NavController
import com.mcs.emkn.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RouterImpl @Inject constructor() : Router {
    private var navController: NavController? = null

    fun attachNavController(navController: NavController) {
        this.navController = navController
    }

    fun releaseNavController() {
        this.navController = null
    }


    override fun back() {
        navController?.popBackStack()
    }

    override fun goToEmailConfirmationScreen() {
        try {
            navController?.navigate(R.id.action_authFragment_to_emailConfirmationFragment)
        } catch (_: Throwable) { }
    }

    override fun goToForgotPasswordScreen() {
        try {
            navController?.navigate(R.id.action_authFragment_to_forgotPasswordFragment)
        } catch(_: Throwable) { }
    }

    override fun goToChangePasswordConfirmationScreen() {
        try {
            navController?.navigate(R.id.action_forgotPasswordFragment_to_changePasswordConfirmationFragment)
        } catch(_: Throwable) { }
    }

    override fun goToCommitChangePasswordScreen() {
        try {
            navController?.navigate(R.id.action_changePasswordConfirmationFragment_to_commitChangePasswordFragment)
        } catch(_: Throwable) { }
    }

    override fun goToRegistrationNavGraph() {
        try {
            navController?.navigate(R.id.action_registrationNavGraph)
        } catch(_: Throwable) { }
    }

    override fun goToUserNavGraph() {
        try {
            navController?.navigate(R.id.action_userNavGraph)
        } catch(_: Throwable) { }
    }
}