package com.finalworksystem.presentation.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DrawerMenuButton(
    drawerState: DrawerState? = rememberGlobalDrawerState()
) {
    val scope = rememberCoroutineScope()

    drawerState?.let { state ->
        IconButton(onClick = {
            scope.launch {
                state.open()
            }
        }) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu"
            )
        }
    }
}
