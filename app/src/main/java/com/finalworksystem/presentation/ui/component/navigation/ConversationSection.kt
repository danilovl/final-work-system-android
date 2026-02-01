package com.finalworksystem.presentation.ui.component.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
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
fun ConversationSection(
    drawerState: DrawerState,
    onNavigateToConversationList: () -> Unit
) {
    val scope = rememberCoroutineScope()

    NavigationDrawerItem(
        label = { Text(stringResource(R.string.conversation)) },
        icon = { 
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = stringResource(R.string.conversation)
            )
        },
        selected = false,
        onClick = {
            scope.launch {
                drawerState.close()
                onNavigateToConversationList()
            }
        }
    )
}