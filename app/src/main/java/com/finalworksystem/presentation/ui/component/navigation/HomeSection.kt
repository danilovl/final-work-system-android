package com.finalworksystem.presentation.ui.component.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
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
fun HomeSection(
    drawerState: DrawerState,
    onNavigateToHome: () -> Unit
) {
    val scope = rememberCoroutineScope()

    NavigationDrawerItem(
        label = { Text(stringResource(R.string.home)) },
        icon = { 
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = stringResource(R.string.home)
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