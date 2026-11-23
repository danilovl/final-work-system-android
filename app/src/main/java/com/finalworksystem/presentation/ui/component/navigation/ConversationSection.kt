package com.finalworksystem.presentation.ui.component.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import com.finalworksystem.R
import com.finalworksystem.ui.designsystem.AppDrawerItem
import com.finalworksystem.ui.designsystem.AppDrawerState
import com.finalworksystem.ui.theme.AppIcons
import kotlinx.coroutines.launch

@Composable
fun ConversationSection(
    drawerState: AppDrawerState,
    onNavigateToConversationList: () -> Unit
) {
    val scope = rememberCoroutineScope()

    AppDrawerItem(
        label = stringResource(R.string.conversation),
        icon = AppIcons.Email,
        onClick = {
            scope.launch {
                drawerState.close()
                onNavigateToConversationList()
            }
        }
    )
}
