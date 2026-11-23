package com.finalworksystem.presentation.ui.main

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.finalworksystem.infrastructure.network.NetworkConnectivityService
import com.finalworksystem.infrastructure.popup.PopupMessageHandler
import com.finalworksystem.infrastructure.popup.PopupMessageService
import com.finalworksystem.infrastructure.session.AppLifecycleManager
import com.finalworksystem.infrastructure.user.UserService
import com.finalworksystem.presentation.navigation.AppNavigationWithDrawer
import com.finalworksystem.presentation.navigation.AppRoute
import com.finalworksystem.presentation.ui.loading.LoadingScreen
import com.finalworksystem.presentation.ui.network.NoConnectionScreen
import com.finalworksystem.ui.theme.FinalWorkSystemTheme
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme
import com.finalworksystem.ui.designsystem.AppSnackbar
import com.finalworksystem.ui.designsystem.AppSnackbarHost
import com.finalworksystem.ui.designsystem.AppSnackbarHostState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.Alignment

@Composable
fun MainScreen(
    userService: UserService,
    popupMessageService: PopupMessageService,
    appLifecycleManager: AppLifecycleManager,
    networkConnectivityService: NetworkConnectivityService,
    onContentReady: () -> Unit = {}
) {
    // The language is applied by the system / AppCompat (per-app language API),
    // a change recreates the activity, so no locale handling is needed here.
    val isLoggedIn by userService.isLoggedInFlow().collectAsStateWithLifecycle(initialValue = null)

    val loggedIn = isLoggedIn
    if (loggedIn == null) {
        FinalWorkSystemTheme { LoadingScreen() }
    } else {
        MainContent(
            isLoggedIn = loggedIn,
            popupMessageService = popupMessageService,
            appLifecycleManager = appLifecycleManager,
            networkConnectivityService = networkConnectivityService,
            onContentReady = onContentReady
        )
    }
}

@Composable
private fun MainContent(
    isLoggedIn: Boolean,
    popupMessageService: PopupMessageService,
    appLifecycleManager: AppLifecycleManager,
    networkConnectivityService: NetworkConnectivityService,
    onContentReady: () -> Unit
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { AppSnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    FinalWorkSystemTheme {
        var isConnected by remember { mutableStateOf<Boolean?>(null) }
        var isInitialLoading by remember { mutableStateOf(true) }
        val connectivityState by networkConnectivityService.isConnected.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            isConnected = networkConnectivityService.checkInternetConnectivity()
            isInitialLoading = false
            onContentReady()
        }

        LaunchedEffect(connectivityState) {
            if (!isInitialLoading) {
                isConnected = connectivityState
            }
        }

        val logoutEvent by appLifecycleManager.logoutEvent.collectAsStateWithLifecycle()
        LaunchedEffect(logoutEvent) {
            logoutEvent?.let {
                navController.navigate(AppRoute.Login) {
                    popUpTo(0) { inclusive = true }
                }
                appLifecycleManager.clearLogoutEvent()
            }
        }

        AppScaffold(
            isLoggedIn = isLoggedIn,
            isConnected = isConnected,
            isInitialLoading = isInitialLoading,
            navController = navController,
            snackbarHostState = snackbarHostState,
            popupMessageService = popupMessageService,
            networkConnectivityService = networkConnectivityService,
            coroutineScope = coroutineScope,
            onRetryConnection = {
                isConnected = networkConnectivityService.checkInternetConnectivity()
            }
        )
    }
}

@Composable
private fun AppScaffold(
    isLoggedIn: Boolean,
    isConnected: Boolean?,
    isInitialLoading: Boolean,
    navController: NavHostController,
    snackbarHostState: AppSnackbarHostState,
    popupMessageService: PopupMessageService,
    networkConnectivityService: NetworkConnectivityService,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    onRetryConnection: suspend () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
    ) {
        PopupMessageHandler(
            popupMessageService = popupMessageService,
            snackbarHostState = snackbarHostState,
            coroutineScope = coroutineScope
        )

        AppNavigationContent(
            isLoggedIn = isLoggedIn,
            isConnected = isConnected,
            isInitialLoading = isInitialLoading,
            navController = navController,
            onRetryConnection = onRetryConnection
        )

        PopupSnackbarHost(
            hostState = snackbarHostState,
            popupMessageService = popupMessageService,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .imePadding()
        )
    }
}

@Composable
private fun PopupSnackbarHost(
    hostState: AppSnackbarHostState,
    popupMessageService: PopupMessageService,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    AppSnackbarHost(
        hostState = hostState,
        modifier = modifier,
        snackbar = { data ->
            val level = popupMessageService.currentMessage.value?.level
                ?: PopupMessageService.MessageLevel.INFO
            val (accent, icon) = when (level) {
                PopupMessageService.MessageLevel.ERROR -> colors.error to AppIcons.Close
                PopupMessageService.MessageLevel.WARNING -> colors.warning to AppIcons.Info
                PopupMessageService.MessageLevel.SUCCESS -> colors.success to AppIcons.Check
                PopupMessageService.MessageLevel.INFO -> colors.secondary to AppIcons.Info
            }
            AppSnackbar(
                data = data,
                accentColor = accent,
                icon = icon
            )
        }
    )
}

@Composable
private fun AppNavigationContent(
    isLoggedIn: Boolean,
    isConnected: Boolean?,
    isInitialLoading: Boolean,
    navController: NavHostController,
    onRetryConnection: suspend () -> Unit
) {
    when {
        isInitialLoading || isConnected == null -> {
            LoadingScreen()
        }

        !isConnected -> {
            NoConnectionScreen(
                onRetry = onRetryConnection
            )
        }

        isConnected -> {
            AppNavigationWithDrawer(
                navController = navController,
                onLogoutNavigation = {
                    navController.navigate(AppRoute.Login) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                startDestination = if (isLoggedIn) AppRoute.Home else AppRoute.Login
            )
        }
    }
}
