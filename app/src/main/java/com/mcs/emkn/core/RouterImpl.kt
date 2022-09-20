package com.mcs.emkn.core

import androidx.navigation.NavController
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

    override fun goToRegistrationScreen() = Unit

    override fun back() = Unit

    override fun goToLoginScreen() = Unit
}