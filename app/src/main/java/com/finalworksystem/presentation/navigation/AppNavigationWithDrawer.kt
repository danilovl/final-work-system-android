package com.finalworksystem.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.finalworksystem.infrastructure.network.NetworkConnectivityService
import com.finalworksystem.presentation.ui.component.GlobalNavigationDrawer
import com.finalworksystem.presentation.view_model.auth.AuthViewModel
import com.finalworksystem.presentation.view_model.conversation.ConversationListViewModel
import com.finalworksystem.presentation.view_model.conversation.ConversationDetailViewModel
import com.finalworksystem.presentation.view_model.system_event.SystemEventViewModel
import com.finalworksystem.presentation.view_model.task.TaskViewModel
import com.finalworksystem.presentation.view_model.user.UserViewModel
import com.finalworksystem.presentation.view_model.work.WorkViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun AppNavigationWithDrawer(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = koinViewModel(),
    systemEventViewModel: SystemEventViewModel = koinViewModel(),
    workViewModel: WorkViewModel = koinViewModel(),
    taskViewModel: TaskViewModel = koinViewModel(),
    conversationListViewModel: ConversationListViewModel = koinViewModel(),
    conversationDetailViewModel: ConversationDetailViewModel = koinViewModel(),
    userViewModel: UserViewModel = koinViewModel(),
    onLogoutNavigation: (() -> Unit)? = null,
    startDestination: String = AppRoutes.LOGIN
) {
    val networkConnectivityService = koinInject<NetworkConnectivityService>()
    val actions = remember(navController) { AppActions(navController, workViewModel, networkConnectivityService) }

    GlobalNavigationDrawer(
        onNavigateToHome = actions.navigateToHome,
        onNavigateToUserDetail = actions.navigateToUserDetail,
        onNavigateToWorkList = actions.navigateToWorkList,
        onNavigateToTaskListOwner = actions.navigateToTaskListOwner,
        onNavigateToTaskListSolver = actions.navigateToTaskListSolver,
        onNavigateToConversationList = actions.navigateToConversationList,
        onNavigateToCalendarReservation = actions.navigateToCalendarReservation,
        onNavigateToCalendarManage = actions.navigateToCalendarManage,
        onNavigateToUserList = actions.navigateToUserList,
        onLogout = { 
            authViewModel.logout()
            onLogoutNavigation?.invoke() ?: actions.navigateToLogin()
        },
        content = { drawerState ->
            AppNavigation(
                navController = navController,
                authViewModel = authViewModel,
                systemEventViewModel = systemEventViewModel,
                workViewModel = workViewModel,
                taskViewModel = taskViewModel,
                conversationListViewModel = conversationListViewModel,
                conversationDetailViewModel = conversationDetailViewModel,
                userViewModel = userViewModel,
                startDestination = startDestination
            )
        }
    )
}
