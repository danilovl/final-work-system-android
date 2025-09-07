package com.finalworksystem.presentation.ui.component.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Article
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.finalworksystem.domain.user.model.User
import com.finalworksystem.domain.user.model.UserRole
import kotlinx.coroutines.launch

@Composable
fun OpponentSection(
    user: User,
    drawerState: DrawerState,
    onNavigateToWorkList: (String) -> Unit
) {
    val scope = rememberCoroutineScope()

    if (user.roles.contains(UserRole.OPPONENT.value)) {
        NavigationDrawerItem(
            label = { Text("Opponent work") },
            icon = { 
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Article,
                    contentDescription = "Opponent work"
                )
            },
            selected = false,
            onClick = {
                scope.launch {
                    drawerState.close()
                    onNavigateToWorkList("opponent")
                }
            }
        )
    }
}