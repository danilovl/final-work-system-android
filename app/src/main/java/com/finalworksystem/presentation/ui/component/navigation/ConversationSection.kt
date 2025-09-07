package com.finalworksystem.presentation.ui.component.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ConversationSection(
    drawerState: DrawerState,
    onNavigateToConversationList: () -> Unit
) {
    val scope = rememberCoroutineScope()

    NavigationDrawerItem(
        label = { Text("Conversation") },
        icon = { 
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = "Conversation"
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