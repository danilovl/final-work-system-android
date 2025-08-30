package com.finalworksystem

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.finalworksystem.infrastructure.network.NetworkConnectivityService
import com.finalworksystem.infrastructure.popup.PopupMessageHandler
import com.finalworksystem.infrastructure.popup.PopupMessageService
import com.finalworksystem.infrastructure.session.AppLifecycleManager
import com.finalworksystem.infrastructure.user.UserService
import com.finalworksystem.presentation.navigation.AppNavigationWithDrawer
import com.finalworksystem.presentation.navigation.AppRoutes
import com.finalworksystem.presentation.ui.loading.LoadingScreen
import com.finalworksystem.presentation.ui.network.NoConnectionScreen
import com.finalworksystem.ui.theme.FinalWorkSystemTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val popupMessageService: PopupMessageService by inject()
    private val appLifecycleManager: AppLifecycleManager by inject()
    private val networkConnectivityService: NetworkConnectivityService by inject()
    private val userService: UserService by inject()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appLifecycleManager.onAppCreated()

        enableEdgeToEdge()
        setContent {
            FinalWorkSystemTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val coroutineScope = rememberCoroutineScope()
                val navController = rememberNavController()

                var isConnected by remember { mutableStateOf<Boolean?>(null) }
                var isInitialLoading by remember { mutableStateOf(true) }
                val connectivityState by networkConnectivityService.isConnected.collectAsState()
                val isLoggedIn by userService.isLoggedInFlow().collectAsState(initial = false)
                
                LaunchedEffect(Unit) {
                    isConnected = networkConnectivityService.checkInternetConnectivity()
                    isInitialLoading = false
                }
                
                LaunchedEffect(connectivityState) {
                    if (!isInitialLoading) {
                        isConnected = connectivityState
                    }
                }

                val logoutEvent by appLifecycleManager.logoutEvent.collectAsState()

                LaunchedEffect(logoutEvent) {
                    logoutEvent?.let {
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                        appLifecycleManager.clearLogoutEvent()
                    }
                }

                Scaffold(
                    contentWindowInsets = WindowInsets.systemBars,
                    snackbarHost = {
                        SnackbarHost(
                            hostState = snackbarHostState,
                            snackbar = { data ->
                                val level = popupMessageService.currentMessage.value?.level ?: PopupMessageService.MessageLevel.INFO
                                val backgroundColor = popupMessageService.getColorForLevel(level)
                                Snackbar(
                                    snackbarData = data,
                                    containerColor = backgroundColor
                                )
                            }
                        )
                    }
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.background
                    ) {
                        PopupMessageHandler(
                            popupMessageService = popupMessageService,
                            snackbarHostState = snackbarHostState,
                            coroutineScope = coroutineScope
                        )

                        when {
                            isInitialLoading || isConnected == null -> {
                                LoadingScreen()
                            }
                            isConnected == false -> {
                                NoConnectionScreen(
                                    onRetry = {
                                        isConnected = networkConnectivityService.checkInternetConnectivity()
                                    }
                                )
                            }
                            isConnected == true -> {
                                AppNavigationWithDrawer(
                                    navController = navController,
                                    onLogoutNavigation = {
                                        navController.navigate(AppRoutes.LOGIN) {
                                            popUpTo(AppRoutes.HOME) { inclusive = true }
                                        }
                                    },
                                    startDestination = if (isLoggedIn) AppRoutes.HOME else AppRoutes.LOGIN
                                )
                            }
                        }
                    }
                }
            }
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
