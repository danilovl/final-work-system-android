package com.finalworksystem.presentation.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.rounded.Article
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finalworksystem.domain.user.model.User
import com.finalworksystem.domain.user.model.UserRole
import com.finalworksystem.infrastructure.user.UserService
import com.finalworksystem.presentation.ui.user.component.UserAvatar
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun GlobalNavigationDrawer(
    content: @Composable (DrawerState) -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToUserDetail: () -> Unit = {},
    onNavigateToWorkList: (String) -> Unit = {},
    onNavigateToTaskListOwner: () -> Unit = {},
    onNavigateToTaskListSolver: () -> Unit = {},
    onNavigateToConversationList: () -> Unit = {},
    onNavigateToCalendarReservation: () -> Unit = {},
    onNavigateToCalendarManage: () -> Unit = {},
    onNavigateToUserList: (String) -> Unit = {},
    onLogout: () -> Unit = {},
    userService: UserService = koinInject(),
) {
    var isUsersMenuExpanded by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val isLoggedIn by userService.isLoggedInFlow().collectAsState(initial = false)
    val user by userService.getUserFlowSafe().collectAsState(initial = null)

    if (!isLoggedIn) {
        content(drawerState)

        return
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                user?.let { currentUser ->
                    DrawerHeader(
                        user = currentUser,
                        onUserClick = {
                            scope.launch {
                                drawerState.close()
                                onNavigateToUserDetail()
                            }
                        }
                    )
                    HorizontalDivider()
                }

                NavigationDrawerItem(
                    label = { Text("Home") },
                    icon = { 
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home"
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

                HorizontalDivider()

                user?.let { currentUser ->
                    if (currentUser.roles.contains(UserRole.STUDENT.value)) {
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
                    }

                    if (currentUser.roles.contains(UserRole.OPPONENT.value)) {
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

                    if (currentUser.roles.contains(UserRole.SUPERVISOR.value)) {
                        NavigationDrawerItem(
                            label = { Text("Consultant work") },
                            icon = { 
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.Article,
                                    contentDescription = "Consultant work"
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
                            label = { Text("Supervisor works") },
                            icon = { 
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.Article,
                                    contentDescription = "Supervisor work"
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
                    }
                }

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

                user?.let { currentUser ->
                    if (currentUser.roles.contains(UserRole.STUDENT.value)) {
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
                    }

                    if (currentUser.roles.contains(UserRole.SUPERVISOR.value)) {
                        NavigationDrawerItem(
                            label = { Text("Calendar manage") },
                            icon = { 
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Calendar manage"
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
                    }
                }

                user?.let { currentUser ->
                    if (currentUser.roles.contains(UserRole.SUPERVISOR.value)) {
                        NavigationDrawerItem(
                            label = { Text("Task") },
                            icon = { 
                                Icon(
                                    imageVector = Icons.Default.Task,
                                    contentDescription = "Task"
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
                    }

                    if (currentUser.roles.contains(UserRole.STUDENT.value)) {
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

                    if (currentUser.roles.contains(UserRole.SUPERVISOR.value)) {
                        NavigationDrawerItem(
                            label = { Text("Users") },
                            icon = { 
                                Icon(
                                    imageVector = Icons.Default.Groups,
                                    contentDescription = "Users"
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
                                label = { Text("  Author") },
                                icon = { 
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Author"
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
                                label = { Text("  Opponent") },
                                icon = { 
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Opponent"
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
                                label = { Text("  Consultant") },
                                icon = { 
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Consultant"
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
                                label = { Text("  Supervisor") },
                                icon = { 
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Supervisor"
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

                NavigationDrawerItem(
                    label = { Text("Logout") },
                    icon = { 
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout"
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
        }
    ) {
        ProvideDrawerState(drawerState = drawerState) {
            content(drawerState)
        }
    }
}

@Composable
fun DrawerHeader(
    user: User,
    onUserClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onUserClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            UserAvatar(
                firstname = user.firstname,
                lastname = user.lastname,
                userId = user.id,
                modifier = Modifier.padding(end = 12.dp)
            )

            Column {
                Text(
                    text = "${user.firstname} ${user.lastname}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
