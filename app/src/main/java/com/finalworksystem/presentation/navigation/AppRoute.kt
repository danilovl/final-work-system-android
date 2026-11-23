package com.finalworksystem.presentation.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation destinations (Navigation Compose with kotlinx.serialization).
 * Arguments are typed properties instead of string templates like `"work_detail/{workId}"`.
 */
sealed interface AppRoute {
    @Serializable
    data object Login : AppRoute

    @Serializable
    data object Home : AppRoute

    @Serializable
    data object UserDetail : AppRoute

    @Serializable
    data class UserList(val type: String) : AppRoute

    @Serializable
    data class WorkList(val type: String = "author") : AppRoute

    @Serializable
    data class WorkDetail(val workId: Int) : AppRoute

    @Serializable
    data object WorkCreate : AppRoute

    @Serializable
    data object TaskListOwner : AppRoute

    @Serializable
    data object TaskListSolver : AppRoute

    @Serializable
    data class TaskDetail(val workId: Int, val taskId: Int) : AppRoute

    /** [workId] is `-1` when the work is chosen in the form. */
    @Serializable
    data class TaskCreate(val workId: Int = -1) : AppRoute

    @Serializable
    data object ConversationList : AppRoute

    @Serializable
    data class ConversationDetail(val conversationId: Int) : AppRoute

    @Serializable
    data object CalendarReservation : AppRoute

    @Serializable
    data object CalendarManage : AppRoute

    @Serializable
    data class EventDetail(val eventId: Int) : AppRoute

    @Serializable
    data object NoConnection : AppRoute
}
