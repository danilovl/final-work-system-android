package com.finalworksystem.presentation.navigation

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.toRoute
import com.finalworksystem.domain.user.model.UserRole
import com.finalworksystem.infrastructure.network.NetworkConnectivityService
import com.finalworksystem.infrastructure.popup.PopupMessageService
import com.finalworksystem.infrastructure.user.UserService
import com.finalworksystem.presentation.ui.auth.LoginScreen
import com.finalworksystem.presentation.ui.conversation.ConversationDetailScreen
import com.finalworksystem.presentation.ui.conversation.ConversationListScreen
import com.finalworksystem.presentation.ui.event.EventDetailScreen
import com.finalworksystem.presentation.ui.event_calendar.CalendarScreen
import com.finalworksystem.presentation.ui.home.HomeScreen
import com.finalworksystem.presentation.ui.network.NoConnectionScreen
import com.finalworksystem.presentation.ui.task.TaskCreateScreen
import com.finalworksystem.presentation.ui.task.TaskDetailScreen
import com.finalworksystem.presentation.ui.task.TaskListOwnerScreen
import com.finalworksystem.presentation.ui.task.TaskListSolverScreen
import com.finalworksystem.presentation.ui.user.UserDetailScreen
import com.finalworksystem.presentation.ui.user.UserListScreen
import com.finalworksystem.presentation.ui.work.WorkCreateScreen
import com.finalworksystem.presentation.ui.work.WorkDetailScreen
import com.finalworksystem.presentation.ui.work.WorkListScreen
import com.finalworksystem.presentation.view_model.auth.AuthViewModel
import com.finalworksystem.presentation.view_model.conversation.ConversationDetailViewModel
import com.finalworksystem.presentation.view_model.conversation.ConversationListViewModel
import com.finalworksystem.presentation.view_model.system_event.SystemEventViewModel
import com.finalworksystem.presentation.view_model.task.TaskCreateViewModel
import com.finalworksystem.presentation.view_model.task.TaskDetailViewModel
import com.finalworksystem.presentation.view_model.task.TaskListViewModel
import com.finalworksystem.presentation.view_model.user.UserViewModel
import com.finalworksystem.presentation.view_model.work.WorkCreateViewModel
import com.finalworksystem.presentation.view_model.work.WorkDetailViewModel
import com.finalworksystem.presentation.view_model.work.WorkListViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = koinViewModel(),
    systemEventViewModel: SystemEventViewModel = koinViewModel(),
    workListViewModel: WorkListViewModel = koinViewModel(),
    workDetailViewModel: WorkDetailViewModel = koinViewModel(),
    taskListViewModel: TaskListViewModel = koinViewModel(),
    taskDetailViewModel: TaskDetailViewModel = koinViewModel(),
    conversationListViewModel: ConversationListViewModel = koinViewModel(),
    conversationDetailViewModel: ConversationDetailViewModel = koinViewModel(),
    userViewModel: UserViewModel = koinViewModel(),
    startDestination: AppRoute = AppRoute.Login
) {
    val networkConnectivityService = koinInject<NetworkConnectivityService>()
    val actions = remember(navController) { AppActions(navController, networkConnectivityService) }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<AppRoute.Login> {
            val popupMessageService = koinInject<PopupMessageService>()
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = actions.navigateToHome,
                popupMessageService = popupMessageService
            )
        }

        composable<AppRoute.Home> {
            HomeScreen(systemEventViewModel = systemEventViewModel)
        }

        composable<AppRoute.UserDetail> {
            UserDetailScreen(
                onNavigateBack = actions.navigateBack
            )
        }

        composable<AppRoute.UserList> { backStackEntry ->
            val userType = backStackEntry.toRoute<AppRoute.UserList>().type
            UserListScreen(
                userType = userType,
                userViewModel = userViewModel,
                onNavigateBack = actions.navigateBack,
                onNavigateToWorkDetail = actions.navigateToWorkDetail
            )
        }

        composable<AppRoute.WorkList> { backStackEntry ->
            val workListType = backStackEntry.toRoute<AppRoute.WorkList>().type
            val userService = koinInject<UserService>()
            val currentUser = userService.getUserFlowSafe().collectAsStateWithLifecycle(initialValue = null).value
            val isSupervisor = currentUser?.roles?.contains(UserRole.SUPERVISOR.value) == true
            WorkListScreen(
                workListType = workListType,
                workListViewModel = workListViewModel,
                onNavigateBack = actions.navigateBack,
                onNavigateToWorkDetail = actions.navigateToWorkDetail,
                onNavigateToWorkCreate = if (isSupervisor) actions.navigateToWorkCreate else null
            )
        }

        composable<AppRoute.WorkCreate> {
            val workCreateViewModel: WorkCreateViewModel = koinViewModel()
            WorkCreateScreen(
                viewModel = workCreateViewModel,
                onNavigateBack = actions.navigateBack,
                onWorkCreated = { workId ->
                    actions.navigateToWorkDetail(workId)
                }
            )
        }

        composable<AppRoute.WorkDetail> { backStackEntry ->
            val workId = backStackEntry.toRoute<AppRoute.WorkDetail>().workId
            val userService = koinInject<UserService>()
            val currentUser = userService.getUserFlowSafe().collectAsStateWithLifecycle(initialValue = null).value
            WorkDetailScreen(
                workId = workId,
                currentUserId = currentUser?.id ?: 0,
                workDetailViewModel = workDetailViewModel,
                onNavigateBack = actions.navigateBack,
                onNavigateToTaskDetail = actions.navigateToTaskDetail,
                onNavigateToTaskCreate = actions.navigateToTaskCreate,
                onNavigateToConversationDetail = actions.navigateToConversationDetail,
                onNavigateToEventDetail = actions.navigateToEventDetail
            )
        }

        composable<AppRoute.TaskListOwner> {
            TaskListOwnerScreen(
                taskListViewModel = taskListViewModel,
                onNavigateBack = actions.navigateBack,
                onTaskClick = actions.navigateToTaskDetail,
                onNavigateToTaskCreate = actions.navigateToTaskCreate
            )
        }

        composable<AppRoute.TaskListSolver> {
            TaskListSolverScreen(
                taskListViewModel = taskListViewModel,
                onNavigateBack = actions.navigateBack,
                onTaskClick = actions.navigateToTaskDetail,
                onNavigateToTaskCreate = actions.navigateToTaskCreate
            )
        }

        composable<AppRoute.TaskDetail> { backStackEntry ->
            val (workId, taskId) = backStackEntry.toRoute<AppRoute.TaskDetail>()
            val userService = koinInject<UserService>()
            val previousDestination = navController.previousBackStackEntry?.destination
            val isOwner = previousDestination?.hasRoute<AppRoute.TaskListOwner>() == true ||
                previousDestination?.hasRoute<AppRoute.WorkDetail>() == true
            TaskDetailScreen(
                workId = workId,
                taskId = taskId,
                taskDetailViewModel = taskDetailViewModel,
                userService = userService,
                isOwner = isOwner,
                onNavigateBack = actions.navigateBack,
                onNavigateToWorkDetail = actions.navigateToWorkDetail
            )
        }

        composable<AppRoute.TaskCreate> { backStackEntry ->
            val workId = backStackEntry.toRoute<AppRoute.TaskCreate>().workId
            val taskCreateViewModel: TaskCreateViewModel = koinViewModel()
            TaskCreateScreen(
                viewModel = taskCreateViewModel,
                workId = workId,
                onNavigateBack = actions.navigateBack,
                onTaskCreated = {
                    actions.navigateBack()
                }
            )
        }

        composable<AppRoute.ConversationList> {
            val popupMessageService = koinInject<PopupMessageService>()
            ConversationListScreen(
                conversationListViewModel = conversationListViewModel,
                popupMessageService = popupMessageService,
                onNavigateBack = actions.navigateBack,
                onNavigateToConversationDetail = actions.navigateToConversationDetail
            )
        }

        composable<AppRoute.ConversationDetail> { backStackEntry ->
            val conversationId = backStackEntry.toRoute<AppRoute.ConversationDetail>().conversationId
            val userService = koinInject<UserService>()
            val currentUser = userService.getUserFlowSafe().collectAsStateWithLifecycle(initialValue = null).value
            ConversationDetailScreen(
                conversationId = conversationId,
                currentUserId = currentUser?.id ?: 0,
                currentUser = currentUser,
                viewModel = conversationDetailViewModel,
                onNavigateBack = actions.navigateBack
            )
        }

        composable<AppRoute.CalendarReservation> {
            CalendarScreen(
                calendarType = "reservation",
                onNavigateBack = actions.navigateBack,
                onEventClick = { calendarEvent -> actions.navigateToEventDetail(calendarEvent.id) }
            )
        }

        composable<AppRoute.CalendarManage> {
            CalendarScreen(
                calendarType = "manage",
                onNavigateBack = actions.navigateBack,
                onEventClick = { calendarEvent -> actions.navigateToEventDetail(calendarEvent.id) }
            )
        }

        composable<AppRoute.EventDetail> { backStackEntry ->
            val eventId = backStackEntry.toRoute<AppRoute.EventDetail>().eventId
            EventDetailScreen(
                eventId = eventId,
                onNavigateBack = actions.navigateBack,
                onNavigateToCalendar = actions.navigateToCalendarManage
            )
        }

        composable<AppRoute.NoConnection> {
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
    networkConnectivityService: NetworkConnectivityService
) {
    private val connectivityAwareNavController = ConnectivityAwareNavController(navController, networkConnectivityService)

    val navigateToHome: () -> Unit = {
        connectivityAwareNavController.navigate(AppRoute.Home) {
            popUpTo<AppRoute.Login> { inclusive = true }
        }
    }

    val navigateToLogin: () -> Unit = {
        navController.navigate(AppRoute.Login) {
            popUpTo<AppRoute.Home> { inclusive = true }
        }
    }

    val navigateToUserDetail: () -> Unit = {
        connectivityAwareNavController.navigate(AppRoute.UserDetail)
    }

    val navigateToWorkList: (String) -> Unit = { type ->
        connectivityAwareNavController.navigate(AppRoute.WorkList(type))
    }

    val navigateToTaskListOwner: () -> Unit = {
        connectivityAwareNavController.navigate(AppRoute.TaskListOwner)
    }

    val navigateToTaskListSolver: () -> Unit = {
        connectivityAwareNavController.navigate(AppRoute.TaskListSolver)
    }

    val navigateToConversationList: () -> Unit = {
        connectivityAwareNavController.navigate(AppRoute.ConversationList)
    }

    val navigateToUserList: (String) -> Unit = { type ->
        connectivityAwareNavController.navigate(AppRoute.UserList(type))
    }

    val navigateToWorkDetail: (Int) -> Unit = { workId ->
        connectivityAwareNavController.navigate(AppRoute.WorkDetail(workId))
    }

    val navigateToWorkCreate: () -> Unit = {
        connectivityAwareNavController.navigate(AppRoute.WorkCreate)
    }

    val navigateToTaskDetail: (Int, Int) -> Unit = { workId, taskId ->
        connectivityAwareNavController.navigate(AppRoute.TaskDetail(workId, taskId))
    }

    val navigateToTaskCreate: (Int) -> Unit = { workId ->
        connectivityAwareNavController.navigate(AppRoute.TaskCreate(workId))
    }

    val navigateToConversationDetail: (Int) -> Unit = { conversationId ->
        connectivityAwareNavController.navigate(AppRoute.ConversationDetail(conversationId))
    }

    val navigateToCalendarReservation: () -> Unit = {
        connectivityAwareNavController.navigate(AppRoute.CalendarReservation)
    }

    val navigateToCalendarManage: () -> Unit = {
        connectivityAwareNavController.navigate(AppRoute.CalendarManage)
    }

    val navigateToEventDetail: (Int) -> Unit = { eventId ->
        connectivityAwareNavController.navigate(AppRoute.EventDetail(eventId))
    }

    val navigateBack: () -> Unit = {
        connectivityAwareNavController.popBackStack()
    }
}
