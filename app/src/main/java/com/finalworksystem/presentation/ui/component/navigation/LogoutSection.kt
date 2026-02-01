package com.finalworksystem.presentation.ui.component.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import com.finalworksystem.R
import kotlinx.coroutines.launch

@Composable
fun LogoutSection(
    drawerState: DrawerState,
    onLogout: () -> Unit
) {
    val scope = rememberCoroutineScope()

    NavigationDrawerItem(
        label = { Text(stringResource(R.string.logout)) },
        icon = { 
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = stringResource(R.string.logout)
            )
        },
        selected = false,
        onClick = {
            scope.launch {
                drawerState.close()
                onLogout()
            }
        }
    )
}