package com.finalworksystem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.finalworksystem.infrastructure.network.NetworkConnectivityService
import com.finalworksystem.infrastructure.popup.PopupMessageService
import com.finalworksystem.infrastructure.session.AppLifecycleManager
import com.finalworksystem.infrastructure.session.SessionManager
import com.finalworksystem.infrastructure.user.UserService
import com.finalworksystem.presentation.ui.main.MainScreen
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val popupMessageService: PopupMessageService by inject()
    private val appLifecycleManager: AppLifecycleManager by inject()
    private val networkConnectivityService: NetworkConnectivityService by inject()
    private val userService: UserService by inject()
    private val sessionManager: SessionManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appLifecycleManager.onAppCreated()

        enableEdgeToEdge()
        setContent {
            MainScreen(
                userService = userService,
                sessionManager = sessionManager,
                popupMessageService = popupMessageService,
                appLifecycleManager = appLifecycleManager,
                networkConnectivityService = networkConnectivityService
            )
        }
    }

    override fun onResume() {
        super.onResume()
        appLifecycleManager.onAppResumed()
    }

    override fun onPause() {
        super.onPause()
        appLifecycleManager.onAppPaused()
    }
}
