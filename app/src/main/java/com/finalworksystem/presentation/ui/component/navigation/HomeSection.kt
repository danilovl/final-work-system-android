package com.finalworksystem.presentation.ui.component.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun HomeSection(
    drawerState: DrawerState,
    onNavigateToHome: () -> Unit
) {
    val scope = rememberCoroutineScope()

    NavigationDrawerItem(
        label = { Text("Home") },
        icon = { 
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Home"
            )
        },
        selected = false,
        onClick = {
            scope.launch {
                drawerState.close()
                onNavigateToHome()
            }
        }
    )
}