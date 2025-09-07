package com.finalworksystem.presentation.ui.component

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf

val LocalDrawerState = compositionLocalOf<DrawerState?> { null }

@Composable
fun ProvideDrawerState(
    drawerState: DrawerState,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalDrawerState provides drawerState) {
        content()
    }
}
