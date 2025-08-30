package com.finalworksystem.presentation.navigation

import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import com.finalworksystem.infrastructure.network.NetworkConnectivityService

class ConnectivityAwareNavController(
    private val navController: NavHostController,
    private val networkConnectivityService: NetworkConnectivityService
) {
    private val bypassConnectivityRoutes = setOf(
        AppRoutes.LOGIN,
        AppRoutes.NO_CONNECTION
    )
    
    fun navigate(route: String, navOptions: NavOptions? = null) {
        if (shouldBypassConnectivityCheck(route)) {
            navController.navigate(route, navOptions)
        } else {
            if (networkConnectivityService.hasNetworkConnection()) {
                navController.navigate(route, navOptions)
            } else {
                navController.navigate(AppRoutes.NO_CONNECTION)
            }
        }
    }
    
    fun navigate(route: String, builder: androidx.navigation.NavOptionsBuilder.() -> Unit) {
        if (shouldBypassConnectivityCheck(route)) {
            navController.navigate(route, builder)
        } else {
            if (networkConnectivityService.hasNetworkConnection()) {
                navController.navigate(route, builder)
            } else {
                navController.navigate(AppRoutes.NO_CONNECTION)
            }
        }
    }
    
    fun popBackStack(): Boolean = navController.popBackStack()

    fun popBackStack(destinationId: Int, inclusive: Boolean, saveState: Boolean = false): Boolean {
        return navController.popBackStack(destinationId, inclusive, saveState)
    }
    
    fun popBackStack(route: String, inclusive: Boolean, saveState: Boolean = false): Boolean {
        return navController.popBackStack(route, inclusive, saveState)
    }
    
    fun getNavController(): NavHostController = navController
    
    val currentDestination: NavDestination?
        get() = navController.currentDestination
    
    val currentBackStackEntry
        get() = navController.currentBackStackEntry
    
    private fun shouldBypassConnectivityCheck(route: String): Boolean {
        return bypassConnectivityRoutes.any { bypassRoute ->
            route == bypassRoute || route.startsWith("$bypassRoute/")
        }
    }
}