package com.finalworksystem.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.finalworksystem.infrastructure.network.NetworkConnectivityService
import com.finalworksystem.infrastructure.popup.PopupMessageService
import com.finalworksystem.infrastructure.user.UserService
import com.finalworksystem.presentation.ui.auth.LoginScreen
import com.finalworksystem.presentation.ui.conversation.ConversationDetailScreen
import com.finalworksystem.presentation.ui.conversation.ConversationListScreen
import com.finalworksystem.presentation.ui.event.EventDetailScreen
import com.finalworksystem.presentation.ui.event_calendar.CalendarScreen
import com.finalworksystem.presentation.ui.home.HomeScreen
import com.finalworksystem.presentation.ui.task.TaskDetailScreen
import com.finalworksystem.presentation.ui.task.TaskListOwnerScreen
import com.finalworksystem.presentation.ui.task.TaskListSolverScreen
import com.finalworksystem.presentation.ui.user.UserDetailScreen
import com.finalworksystem.presentation.ui.user.UserListScreen
import com.finalworksystem.presentation.ui.work.WorkDetailScreen
import com.finalworksystem.presentation.ui.work.WorkListScreen
import com.finalworksystem.presentation.ui.network.NoConnectionScreen
import com.finalworksystem.presentation.view_model.auth.AuthViewModel
import com.finalworksystem.presentation.view_model.conversation.ConversationListViewModel
import com.finalworksystem.presentation.view_model.conversation.ConversationDetailViewModel
import com.finalworksystem.presentation.view_model.system_event.SystemEventViewModel
import com.finalworksystem.presentation.view_model.task.TaskViewModel
import com.finalworksystem.presentation.view_model.user.UserViewModel
import com.finalworksystem.presentation.view_model.work.WorkViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

object AppRoutes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val USER_DETAIL = "user_detail"
    const val USER_LIST = "user_list/{type}"
    const val WORK_LIST = "work_list/{type}"
    const val WORK_DETAIL = "work_detail/{workId}"
    const val TASK_LIST_OWNER = "task_list_owner"
    const val TASK_LIST_SOLVER = "task_list_solver"
    const val TASK_DETAIL = "task_detail/{workId}/{taskId}"
    const val CONVERSATION_LIST = "conversation_list"
    const val CONVERSATION_DETAIL = "conversation_detail/{conversationId}"
    const val CALENDAR_RESERVATION = "calendar_reservation"
    const val CALENDAR_MANAGE = "calendar_manage"
    const val EVENT_DETAIL = "event_detail/{eventId}"
    const val NO_CONNECTION = "no_connection"

    fun workDetailRoute(workId: Int) = "work_detail/$workId"
    fun eventDetailRoute(eventId: Int) = "event_detail/$eventId"
    fun workListRoute(type: String) = "work_list/$type"
    fun taskDetailRoute(workId: Int, taskId: Int) = "task_detail/$workId/$taskId"
    fun conversationDetailRoute(conversationId: Int) = "conversation_detail/$conversationId"
    fun userListRoute(type: String) = "user_list/$type"
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = koinViewModel(),
    systemEventViewModel: SystemEventViewModel = koinViewModel(),
    workViewModel: WorkViewModel = koinViewModel(),
    taskViewModel: TaskViewModel = koinViewModel(),
    conversationListViewModel: ConversationListViewModel = koinViewModel(),
    conversationDetailViewModel: ConversationDetailViewModel = koinViewModel(),
    userViewModel: UserViewModel = koinViewModel(),
    startDestination: String = AppRoutes.LOGIN
) {
    val networkConnectivityService = koinInject<NetworkConnectivityService>()
    val actions = remember(navController) { AppActions(navController, workViewModel, networkConnectivityService) }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(AppRoutes.LOGIN) {
            val popupMessageService = koinInject<PopupMessageService>()
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = actions.navigateToHome,
                popupMessageService = popupMessageService
            )
        }

        composable(AppRoutes.HOME) {
            HomeScreen(systemEventViewModel = systemEventViewModel)
        }

        composable(AppRoutes.USER_DETAIL) {
            UserDetailScreen(
                onNavigateBack = actions.navigateBack
            )
        }

        composable(AppRoutes.USER_LIST) { backStackEntry ->
            val userType = backStackEntry.arguments?.getString("type") ?: ""
            UserListScreen(
                userType = userType,
                userViewModel = userViewModel,
                onNavigateBack = actions.navigateBack,
                onNavigateToWorkDetail = actions.navigateToWorkDetail
            )
        }

        composable(AppRoutes.WORK_LIST) { backStackEntry ->
            val workListType = backStackEntry.arguments?.getString("type") ?: "author"
            WorkListScreen(
                workListType = workListType,
                workViewModel = workViewModel,
                onNavigateBack = actions.navigateBack,
                onNavigateToWorkDetail = actions.navigateToWorkDetail
            )
        }

        composable(AppRoutes.WORK_DETAIL) { backStackEntry ->
            val workId = backStackEntry.arguments?.getString("workId")?.toIntOrNull() ?: 0
            val userService = koinInject<UserService>()
            val currentUser = userService.getUserFlowSafe().collectAsState(initial = null).value
            WorkDetailScreen(
                workId = workId,
                currentUserId = currentUser?.id ?: 0,
                workViewModel = workViewModel,
                onNavigateBack = actions.navigateBack,
                onNavigateToTaskDetail = actions.navigateToTaskDetail,
                onNavigateToConversationDetail = actions.navigateToConversationDetail,
                onNavigateToEventDetail = actions.navigateToEventDetail
            )
        }

        composable(AppRoutes.TASK_LIST_OWNER) {
            TaskListOwnerScreen(
                taskViewModel = taskViewModel,
                onNavigateBack = actions.navigateBack,
                onTaskClick = actions.navigateToTaskDetail
            )
        }

        composable(AppRoutes.TASK_LIST_SOLVER) {
            TaskListSolverScreen(
                taskViewModel = taskViewModel,
                onNavigateBack = actions.navigateBack,
                onTaskClick = actions.navigateToTaskDetail
            )
        }

        composable(AppRoutes.TASK_DETAIL) { backStackEntry ->
            val workId = backStackEntry.arguments?.getString("workId")?.toIntOrNull() ?: 0
            val taskId = backStackEntry.arguments?.getString("taskId")?.toIntOrNull() ?: 0
            val userService = koinInject<UserService>()
            val previousRoute = navController.previousBackStackEntry?.destination?.route
            val isOwner = previousRoute?.contains("task_list_owner") == true || 
                         previousRoute?.contains("work_detail") == true
            TaskDetailScreen(
                workId = workId,
                taskId = taskId,
                taskViewModel = taskViewModel,
                userService = userService,
                isOwner = isOwner,
                onNavigateBack = actions.navigateBack,
                onNavigateToWorkDetail = actions.navigateToWorkDetail
            )
        }

        composable(AppRoutes.CONVERSATION_LIST) {
            val popupMessageService = koinInject<PopupMessageService>()
            ConversationListScreen(
                conversationListViewModel = conversationListViewModel,
                popupMessageService = popupMessageService,
                onNavigateBack = actions.navigateBack,
                onNavigateToConversationDetail = actions.navigateToConversationDetail
            )
        }

        composable(AppRoutes.CONVERSATION_DETAIL) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString("conversationId")?.toIntOrNull() ?: 0
            val userService = koinInject<UserService>()
            val currentUser = userService.getUserFlowSafe().collectAsState(initial = null).value
            ConversationDetailScreen(
                conversationId = conversationId,
                currentUserId = currentUser?.id ?: 0,
                currentUser = currentUser,
                viewModel = conversationDetailViewModel,
                onNavigateBack = actions.navigateBack
            )
        }

        composable(AppRoutes.CALENDAR_RESERVATION) {
            CalendarScreen(
                calendarType = "reservation",
                onNavigateBack = actions.navigateBack,
                onEventClick = { calendarEvent -> actions.navigateToEventDetail(calendarEvent.id) }
            )
        }

        composable(AppRoutes.CALENDAR_MANAGE) {
            CalendarScreen(
                calendarType = "manage",
                onNavigateBack = actions.navigateBack,
                onEventClick = { calendarEvent -> actions.navigateToEventDetail(calendarEvent.id) }
            )
        }

        composable(AppRoutes.EVENT_DETAIL) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")?.toIntOrNull() ?: 0
            EventDetailScreen(
                eventId = eventId,
                onNavigateBack = actions.navigateBack,
                onNavigateToCalendar = actions.navigateToCalendarManage
            )
        }

        composable(AppRoutes.NO_CONNECTION) {
            NoConnectionScreen(
                onRetry = {
                    val isConnected = networkConnectivityService.checkInternetConnectivity()
                    if (isConnected) {
                        actions.navigateBack()
                    }
                }
            )
        }
    }
}

class AppActions(
    private val navController: NavHostController,
    private val workViewModel: WorkViewModel,
    networkConnectivityService: NetworkConnectivityService
) {
    private val connectivityAwareNavController = ConnectivityAwareNavController(navController, networkConnectivityService)
    
    val navigateToHome: () -> Unit = {
        workViewModel.markLeftWorkListSection()
        connectivityAwareNavController.navigate(AppRoutes.HOME) {
            popUpTo(AppRoutes.LOGIN) { inclusive = true }
        }
    }

    val navigateToLogin: () -> Unit = {
        workViewModel.markLeftWorkListSection()
        navController.navigate(AppRoutes.LOGIN) {
            popUpTo(AppRoutes.HOME) { inclusive = true }
        }
    }

    val navigateToUserDetail: () -> Unit = {
        workViewModel.markLeftWorkListSection()
        connectivityAwareNavController.navigate(AppRoutes.USER_DETAIL)
    }

    val navigateToWorkList: (String) -> Unit = { type ->
        workViewModel.markEnteredWorkListSection()
        connectivityAwareNavController.navigate(AppRoutes.workListRoute(type))
    }

    val navigateToTaskListOwner: () -> Unit = {
        connectivityAwareNavController.navigate(AppRoutes.TASK_LIST_OWNER)
    }

    val navigateToTaskListSolver: () -> Unit = {
        connectivityAwareNavController.navigate(AppRoutes.TASK_LIST_SOLVER)
    }

    val navigateToConversationList: () -> Unit = {
        connectivityAwareNavController.navigate(AppRoutes.CONVERSATION_LIST)
    }

    val navigateToUserList: (String) -> Unit = { type ->
        connectivityAwareNavController.navigate(AppRoutes.userListRoute(type))
    }

    val navigateToWorkDetail: (Int) -> Unit = { workId ->
        workViewModel.clearWorkCache()
        connectivityAwareNavController.navigate(AppRoutes.workDetailRoute(workId))
    }

    val navigateToTaskDetail: (Int, Int) -> Unit = { workId, taskId ->
        connectivityAwareNavController.navigate(AppRoutes.taskDetailRoute(workId, taskId))
    }

    val navigateToConversationDetail: (Int) -> Unit = { conversationId ->
        connectivityAwareNavController.navigate(AppRoutes.conversationDetailRoute(conversationId))
    }

    val navigateToCalendarReservation: () -> Unit = {
        connectivityAwareNavController.navigate(AppRoutes.CALENDAR_RESERVATION)
    }

    val navigateToCalendarManage: () -> Unit = {
        connectivityAwareNavController.navigate(AppRoutes.CALENDAR_MANAGE)
    }

    val navigateToEventDetail: (Int) -> Unit = { eventId ->
        connectivityAwareNavController.navigate(AppRoutes.eventDetailRoute(eventId))
    }

    val navigateBack: () -> Unit = {
        connectivityAwareNavController.popBackStack()
    }
}
