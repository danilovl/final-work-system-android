package com.finalworksystem.presentation.navigation

import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import com.finalworksystem.infrastructure.network.NetworkConnectivityService

/**
 * Navigates to [AppRoute]s, but shows [AppRoute.NoConnection] instead of screens
 * that need the network while the device is offline.
 */
class ConnectivityAwareNavController(
    private val navController: NavHostController,
    private val networkConnectivityService: NetworkConnectivityService
) {
    fun navigate(route: AppRoute, builder: NavOptionsBuilder.() -> Unit = {}) {
        if (route.worksOffline() || networkConnectivityService.hasNetworkConnection()) {
            navController.navigate(route, builder)
        } else {
            navController.navigate(AppRoute.NoConnection)
        }
    }

    fun popBackStack(): Boolean = navController.popBackStack()

    private fun AppRoute.worksOffline(): Boolean {
        return this == AppRoute.Login || this == AppRoute.NoConnection
    }
}
