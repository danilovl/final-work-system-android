package com.finalworksystem

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.finalworksystem.infrastructure.locale.AppLanguage
import com.finalworksystem.infrastructure.network.NetworkConnectivityService
import com.finalworksystem.infrastructure.popup.PopupMessageService
import com.finalworksystem.infrastructure.session.AppLifecycleManager
import com.finalworksystem.infrastructure.session.SessionManager
import com.finalworksystem.infrastructure.user.UserService
import com.finalworksystem.presentation.ui.main.MainScreen
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

// AppCompatActivity is required for the per-app language API on Android 12 and older.
class MainActivity : AppCompatActivity() {
    private val popupMessageService: PopupMessageService by inject()
    private val appLifecycleManager: AppLifecycleManager by inject()
    private val networkConnectivityService: NetworkConnectivityService by inject()
    private val userService: UserService by inject()
    private val sessionManager: SessionManager by inject()

    private var isContentReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // The system splash screen stays until the session and connectivity are known.
        splashScreen.setKeepOnScreenCondition { !isContentReady }

        appLifecycleManager.onAppCreated()

        lifecycleScope.launch {
            AppLanguage.applyLegacy(sessionManager.consumeLegacyLanguage())
        }

        enableEdgeToEdge()
        setContent {
            MainScreen(
                userService = userService,
                popupMessageService = popupMessageService,
                appLifecycleManager = appLifecycleManager,
                networkConnectivityService = networkConnectivityService,
                onContentReady = { isContentReady = true }
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
