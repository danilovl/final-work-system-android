package com.finalworksystem.presentation.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import com.finalworksystem.ui.designsystem.AppDrawerState

val LocalDrawerState = compositionLocalOf<AppDrawerState?> { null }

@Composable
fun ProvideDrawerState(
    drawerState: AppDrawerState,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalDrawerState provides drawerState) {
        content()
    }
}
