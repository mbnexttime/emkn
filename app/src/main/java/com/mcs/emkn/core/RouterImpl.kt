package com.mcs.emkn.core

import androidx.navigation.NavController
import com.mcs.emkn.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RouterImpl @Inject constructor(): Router {
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
        navController?.navigate(R.id.action_authFragment_to_emailConfirmationFragment)
    }

    override fun goToForgotPasswordScreen() {
        navController?.navigate(R.id.action_authFragment_to_forgotPasswordFragment)
    }

    override fun goToChangePasswordConfirmationScreen() {
        navController?.navigate(R.id.action_forgotPasswordFragment_to_changePasswordConfirmationFragment)
    }

    override fun goToCommitChangePasswordScreen() {
        navController?.navigate(R.id.action_changePasswordConfirmationFragment_to_commitChangePasswordFragment)
    }

    override fun goToRegistrationNavGraph() {
        navController?.navigate(R.id.action_registrationNavGraph)
    }

    override fun goToUserNavGraph() {
        navController?.navigate(R.id.action_userNavGraph)
    }
}