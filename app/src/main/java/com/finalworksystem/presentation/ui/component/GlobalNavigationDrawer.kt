package com.finalworksystem.presentation.ui.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.finalworksystem.R
import com.finalworksystem.infrastructure.user.UserService
import com.finalworksystem.presentation.ui.component.navigation.ConversationSection
import com.finalworksystem.presentation.ui.component.navigation.Header
import com.finalworksystem.presentation.ui.component.navigation.HomeSection
import com.finalworksystem.presentation.ui.component.navigation.LogoutSection
import com.finalworksystem.presentation.ui.component.navigation.OpponentSection
import com.finalworksystem.presentation.ui.component.navigation.StudentSection
import com.finalworksystem.presentation.ui.component.navigation.SupervisorSection
import com.finalworksystem.ui.designsystem.AppDivider
import com.finalworksystem.ui.designsystem.AppDrawerSectionTitle
import com.finalworksystem.ui.designsystem.AppDrawerState
import com.finalworksystem.ui.designsystem.AppModalDrawer
import com.finalworksystem.ui.designsystem.rememberAppDrawerState
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun GlobalNavigationDrawer(
    content: @Composable (AppDrawerState) -> Unit,
    modifier: Modifier = Modifier,
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
    val drawerState = rememberAppDrawerState()
    val scope = rememberCoroutineScope()
    val isLoggedIn by userService.isLoggedInFlow().collectAsStateWithLifecycle(initialValue = false)
    val user by userService.getUserFlowSafe().collectAsStateWithLifecycle(initialValue = null)

    if (!isLoggedIn) {
        content(drawerState)

        return
    }

    AppModalDrawer(
        drawerState = drawerState,
        modifier = modifier,
        drawerContent = {
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
                Spacer(Modifier.height(2.dp))
            }

            AppDrawerSectionTitle(stringResource(R.string.drawer_section_general))

            HomeSection(
                drawerState = drawerState,
                onNavigateToHome = onNavigateToHome
            )

            ConversationSection(
                drawerState = drawerState,
                onNavigateToConversationList = onNavigateToConversationList
            )

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

            Spacer(Modifier.height(8.dp))
            AppDivider(Modifier.padding(horizontal = 24.dp))
            Spacer(Modifier.height(4.dp))

            LogoutSection(
                drawerState = drawerState,
                onLogout = onLogout
            )
        }
    ) {
        ProvideDrawerState(drawerState = drawerState) {
            content(drawerState)
        }
    }
}
