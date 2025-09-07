package com.finalworksystem.presentation.ui.component

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.finalworksystem.infrastructure.user.UserService
import com.finalworksystem.presentation.ui.component.navigation.ConversationSection
import com.finalworksystem.presentation.ui.component.navigation.Header
import com.finalworksystem.presentation.ui.component.navigation.HomeSection
import com.finalworksystem.presentation.ui.component.navigation.LogoutSection
import com.finalworksystem.presentation.ui.component.navigation.OpponentSection
import com.finalworksystem.presentation.ui.component.navigation.StudentSection
import com.finalworksystem.presentation.ui.component.navigation.SupervisorSection
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
                    Header(
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

                HomeSection(
                    drawerState = drawerState,
                    onNavigateToHome = onNavigateToHome
                )

                HorizontalDivider()

                user?.let { currentUser ->
                    StudentSection(
                        user = currentUser,
                        drawerState = drawerState,
                        onNavigateToWorkList = onNavigateToWorkList,
                        onNavigateToCalendarReservation = onNavigateToCalendarReservation,
                        onNavigateToTaskListSolver = onNavigateToTaskListSolver
                    )

                    OpponentSection(
                        user = currentUser,
                        drawerState = drawerState,
                        onNavigateToWorkList = onNavigateToWorkList
                    )

                    SupervisorSection(
                        user = currentUser,
                        drawerState = drawerState,
                        onNavigateToWorkList = onNavigateToWorkList,
                        onNavigateToCalendarManage = onNavigateToCalendarManage,
                        onNavigateToTaskListOwner = onNavigateToTaskListOwner,
                        onNavigateToUserList = onNavigateToUserList
                    )
                }

                ConversationSection(
                    drawerState = drawerState,
                    onNavigateToConversationList = onNavigateToConversationList
                )

                LogoutSection(
                    drawerState = drawerState,
                    onLogout = onLogout
                )
            }
        }
    ) {
        ProvideDrawerState(drawerState = drawerState) {
            content(drawerState)
        }
    }
}

