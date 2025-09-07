package com.finalworksystem.presentation.ui.component.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Article
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Task
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
fun StudentSection(
    user: User,
    drawerState: DrawerState,
    onNavigateToWorkList: (String) -> Unit,
    onNavigateToCalendarReservation: () -> Unit,
    onNavigateToTaskListSolver: () -> Unit
) {
    val scope = rememberCoroutineScope()

    if (user.roles.contains(UserRole.STUDENT.value)) {
        NavigationDrawerItem(
            label = { Text("Author work") },
            icon = { 
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Article,
                    contentDescription = "Author work"
                )
            },
            selected = false,
            onClick = {
                scope.launch {
                    drawerState.close()
                    onNavigateToWorkList("author")
                }
            }
        )

        NavigationDrawerItem(
            label = { Text("Calendar reservation") },
            icon = { 
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Calendar reservation"
                )
            },
            selected = false,
            onClick = {
                scope.launch {
                    drawerState.close()
                    onNavigateToCalendarReservation()
                }
            }
        )

        NavigationDrawerItem(
            label = { Text("Assigned task") },
            icon = { 
                Icon(
                    imageVector = Icons.Default.Task,
                    contentDescription = "Assigned task"
                )
            },
            selected = false,
            onClick = {
                scope.launch {
                    drawerState.close()
                    onNavigateToTaskListSolver()
                }
            }
        )
    }
}