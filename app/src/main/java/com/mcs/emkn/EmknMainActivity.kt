package com.mcs.emkn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.mcs.emkn.auth.AuthComponent
import com.mcs.emkn.core.RouterImpl
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class EmknMainActivity : AppCompatActivity() {
    @Inject
    lateinit var routerImpl: RouterImpl
    @Inject
    lateinit var component: AuthComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        routerImpl.attachNavController(findNavController(R.id.main_host_fragment))
    }

    override fun onDestroy() {
        super.onDestroy()
        routerImpl.releaseNavController()
    }
}