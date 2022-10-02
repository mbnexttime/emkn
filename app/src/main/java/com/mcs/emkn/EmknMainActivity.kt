package com.mcs.emkn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.mcs.emkn.auth.AuthComponent
import com.mcs.emkn.core.RouterImpl
import com.mcs.emkn.database.Database
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@AndroidEntryPoint
class EmknMainActivity : AppCompatActivity() {
    @Inject
    lateinit var routerImpl: RouterImpl
    @Inject
    lateinit var component: AuthComponent
    @Inject
    lateinit var db: Database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        routerImpl.attachNavController(findNavController(R.id.main_host_fragment))
        chooseNavGraph()
    }

    override fun onDestroy() {
        super.onDestroy()
        routerImpl.releaseNavController()
    }

    private fun chooseNavGraph() {
        lifecycleScope.launch(Dispatchers.IO) {
            val isLogged = db.accountsDao().getCredentials().firstOrNull()?.isAuthorized ?: false
            withContext(Dispatchers.Main) {
                if(isLogged) {
                    routerImpl.goToUserNavGraph()
                } else {
                    routerImpl.goToRegistrationNavGraph()
                }
            }
        }
    }
}