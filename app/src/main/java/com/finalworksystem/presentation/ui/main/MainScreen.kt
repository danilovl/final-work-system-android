package com.finalworksystem.presentation.ui.main

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.finalworksystem.infrastructure.network.NetworkConnectivityService
import com.finalworksystem.infrastructure.popup.PopupMessageHandler
import com.finalworksystem.infrastructure.popup.PopupMessageService
import com.finalworksystem.infrastructure.session.AppLifecycleManager
import com.finalworksystem.infrastructure.session.SessionManager
import com.finalworksystem.infrastructure.user.UserService
import com.finalworksystem.presentation.navigation.AppNavigationWithDrawer
import com.finalworksystem.presentation.navigation.AppRoutes
import com.finalworksystem.presentation.ui.loading.LoadingScreen
import com.finalworksystem.presentation.ui.network.NoConnectionScreen
import com.finalworksystem.ui.theme.FinalWorkSystemTheme
import java.util.Locale

@Composable
fun MainScreen(
    userService: UserService,
    sessionManager: SessionManager,
    popupMessageService: PopupMessageService,
    appLifecycleManager: AppLifecycleManager,
    networkConnectivityService: NetworkConnectivityService
) {
    val languageState = sessionManager.languageFlow.collectAsState(initial = "INITIAL_LOADING")
    val isLoggedInState = userService.isLoggedInFlow().collectAsState(initial = null)

    val language = languageState.value
    val isLoggedIn = isLoggedInState.value

    if (language == "INITIAL_LOADING" || isLoggedIn == null) {
        LoadingScreen()
    } else {
        LocaleWrapper(language = language ?: "en") {
            MainContent(
                isLoggedIn = isLoggedIn,
                popupMessageService = popupMessageService,
                appLifecycleManager = appLifecycleManager,
                networkConnectivityService = networkConnectivityService
            )
        }
    }
}

@Composable
private fun LocaleWrapper(
    language: String,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val localeContext = remember(language) {
        val locale = Locale.forLanguageTag(language)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocales(android.os.LocaleList(locale))
        @Suppress("DEPRECATION")
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        context.createConfigurationContext(config)
    }

    LaunchedEffect(language) {
        val currentLocale = AppCompatDelegate.getApplicationLocales().toLanguageTags()
        if (language != currentLocale) {
            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(language)
            AppCompatDelegate.setApplicationLocales(appLocale)
        }
    }

    CompositionLocalProvider(
        LocalContext provides localeContext,
        LocalConfiguration provides localeContext.resources.configuration
    ) {
        content()
    }
}

@Composable
private fun MainContent(
    isLoggedIn: Boolean,
    popupMessageService: PopupMessageService,
    appLifecycleManager: AppLifecycleManager,
    networkConnectivityService: NetworkConnectivityService
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    FinalWorkSystemTheme {
        var isConnected by remember { mutableStateOf<Boolean?>(null) }
        var isInitialLoading by remember { mutableStateOf(true) }
        val connectivityState by networkConnectivityService.isConnected.collectAsState()

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
                navController.navigate(AppRoutes.LOGIN) {
                    popUpTo(0) { inclusive = true }
                }
                appLifecycleManager.clearLogoutEvent()
            }
        }

        val currentLocale = LocalContext.current.resources.configuration.locales[0]
        key(currentLocale) {
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
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun AppScaffold(
    isLoggedIn: Boolean,
    isConnected: Boolean?,
    isInitialLoading: Boolean,
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    popupMessageService: PopupMessageService,
    networkConnectivityService: NetworkConnectivityService,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    onRetryConnection: suspend () -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets.systemBars,
        snackbarHost = {
            AppSnackbarHost(
                hostState = snackbarHostState,
                popupMessageService = popupMessageService
            )
        }
    ) { _ ->
        Surface(
            color = MaterialTheme.colorScheme.background
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
        }
    }
}

@Composable
private fun AppSnackbarHost(
    hostState: SnackbarHostState,
    popupMessageService: PopupMessageService
) {
    SnackbarHost(
        hostState = hostState,
        snackbar = { data ->
            val level = popupMessageService.currentMessage.value?.level
                ?: PopupMessageService.MessageLevel.INFO
            val backgroundColor = popupMessageService.getColorForLevel(level)
            Snackbar(
                snackbarData = data,
                containerColor = backgroundColor
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
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                startDestination = if (isLoggedIn) AppRoutes.HOME else AppRoutes.LOGIN
            )
        }
    }
}
