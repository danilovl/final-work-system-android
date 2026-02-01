package com.finalworksystem.presentation.ui.component.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Article
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.finalworksystem.R
import com.finalworksystem.domain.user.model.User
import com.finalworksystem.domain.user.model.UserRole
import kotlinx.coroutines.launch

@Composable
fun SupervisorSection(
    user: User,
    drawerState: DrawerState,
    onNavigateToWorkList: (String) -> Unit,
    onNavigateToCalendarManage: () -> Unit,
    onNavigateToTaskListOwner: () -> Unit,
    onNavigateToUserList: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    var isUsersMenuExpanded by remember { mutableStateOf(false) }

    if (user.roles.contains(UserRole.SUPERVISOR.value)) {
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.consultant_work)) },
            icon = { 
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Article,
                    contentDescription = stringResource(R.string.consultant_work)
                )
            },
            selected = false,
            onClick = {
                scope.launch {
                    drawerState.close()
                    onNavigateToWorkList("consultant")
                }
            }
        )

        NavigationDrawerItem(
            label = { Text(stringResource(R.string.supervisor_works)) },
            icon = { 
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Article,
                    contentDescription = stringResource(R.string.supervisor_works)
                )
            },
            selected = false,
            onClick = {
                scope.launch {
                    drawerState.close()
                    onNavigateToWorkList("supervisor")
                }
            }
        )

        NavigationDrawerItem(
            label = { Text(stringResource(R.string.calendar_manage)) },
            icon = { 
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = stringResource(R.string.calendar_manage)
                )
            },
            selected = false,
            onClick = {
                scope.launch {
                    drawerState.close()
                    onNavigateToCalendarManage()
                }
            }
        )

        NavigationDrawerItem(
            label = { Text(stringResource(R.string.task)) },
            icon = { 
                Icon(
                    imageVector = Icons.Default.Task,
                    contentDescription = stringResource(R.string.task)
                )
            },
            selected = false,
            onClick = {
                scope.launch {
                    drawerState.close()
                    onNavigateToTaskListOwner()
                }
            }
        )

        NavigationDrawerItem(
            label = { Text(stringResource(R.string.users)) },
            icon = { 
                Icon(
                    imageVector = Icons.Default.Groups,
                    contentDescription = stringResource(R.string.users)
                )
            },
            selected = false,
            onClick = {
                isUsersMenuExpanded = !isUsersMenuExpanded
            },
            badge = {
                Icon(
                    imageVector = if (isUsersMenuExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isUsersMenuExpanded) "Collapse" else "Expand"
                )
            }
        )

        if (isUsersMenuExpanded) {
            NavigationDrawerItem(
                label = { Text("  " + stringResource(R.string.author)) },
                icon = { 
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = stringResource(R.string.author)
                    )
                },
                selected = false,
                onClick = {
                    scope.launch {
                        drawerState.close()
                        onNavigateToUserList("author")
                    }
                }
            )

            NavigationDrawerItem(
                label = { Text("  " + stringResource(R.string.opponent)) },
                icon = { 
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = stringResource(R.string.opponent)
                    )
                },
                selected = false,
                onClick = {
                    scope.launch {
                        drawerState.close()
                        onNavigateToUserList("opponent")
                    }
                }
            )

            NavigationDrawerItem(
                label = { Text("  " + stringResource(R.string.consultant)) },
                icon = { 
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = stringResource(R.string.consultant)
                    )
                },
                selected = false,
                onClick = {
                    scope.launch {
                        drawerState.close()
                        onNavigateToUserList("consultant")
                    }
                }
            )

            NavigationDrawerItem(
                label = { Text("  " + stringResource(R.string.supervisor)) },
                icon = { 
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = stringResource(R.string.supervisor)
                    )
                },
                selected = false,
                onClick = {
                    scope.launch {
                        drawerState.close()
                        onNavigateToUserList("supervisor")
                    }
                }
            )
        }
    }
}