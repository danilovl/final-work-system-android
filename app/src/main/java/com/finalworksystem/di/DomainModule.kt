package com.finalworksystem.di

import com.finalworksystem.application.use_case.auth.LoginUseCase
import com.finalworksystem.application.use_case.auth.LogoutUseCase
import com.finalworksystem.application.use_case.conversation.CreateMessageUseCase
import com.finalworksystem.application.use_case.conversation.GetConversationDetailUseCase
import com.finalworksystem.application.use_case.conversation.GetConversationMessagesUseCase
import com.finalworksystem.application.use_case.conversation.GetConversationWorkUseCase
import com.finalworksystem.application.use_case.conversation.GetConversationsWithPaginationUseCase
import com.finalworksystem.application.use_case.conversation.GetWorkConversationMessagesUseCase
import com.finalworksystem.application.use_case.conversation.MarkAllMessagesAsReadUseCase
import com.finalworksystem.application.use_case.conversation.MarkMessageAsReadUseCase
import com.finalworksystem.application.use_case.event.GetEventByIdUseCase
import com.finalworksystem.application.use_case.event.GetEventsForWorkUseCase
import com.finalworksystem.application.use_case.event_calendar.DeleteEventUseCase
import com.finalworksystem.application.use_case.event_calendar.GetEventCalendarManageCreateDataUseCase
import com.finalworksystem.application.use_case.event_calendar.GetEventCalendarUseCase
import com.finalworksystem.application.use_case.event_calendar.GetEventCalendarUserWorksUseCase
import com.finalworksystem.application.use_case.event_calendar.PostEventCalendarCreateUseCase
import com.finalworksystem.application.use_case.event_calendar.PostEventCalendarReservationUseCase
import com.finalworksystem.application.use_case.system_event.GetSystemEventsUseCase
import com.finalworksystem.application.use_case.system_event.GetSystemEventsWithPaginationUseCase
import com.finalworksystem.application.use_case.system_event.MarkAllSystemEventsAsViewedUseCase
import com.finalworksystem.application.use_case.system_event.MarkSystemEventAsViewedUseCase
import com.finalworksystem.application.use_case.task.ChangeTaskStatusUseCase
import com.finalworksystem.application.use_case.task.DeleteTaskUseCase
import com.finalworksystem.application.use_case.task.GetTaskDetailUseCase
import com.finalworksystem.application.use_case.task.GetTasksForOwnerUseCase
import com.finalworksystem.application.use_case.task.GetTasksForSolverUseCase
import com.finalworksystem.application.use_case.task.GetTasksForWorkUseCase
import com.finalworksystem.application.use_case.task.NotifyTaskCompleteUseCase
import com.finalworksystem.application.use_case.user.GetCachedProfileImageUseCase
import com.finalworksystem.application.use_case.user.GetUserListUseCase
import com.finalworksystem.application.use_case.user.GetUserProfileImageUseCase
import com.finalworksystem.application.use_case.user.RefreshUserDataOnStartupUseCase
import com.finalworksystem.application.use_case.version.GetVersionsForWorkUseCase
import com.finalworksystem.application.use_case.version.GetVersionsForWorkWithPaginationUseCase
import com.finalworksystem.application.use_case.work.GetWorkDetailUseCase
import com.finalworksystem.application.use_case.work.GetWorkListUseCase
import com.finalworksystem.application.use_case.work.GetWorkListWithPaginationUseCase
import com.finalworksystem.infrastructure.cache.ProfileImageCacheManager
import org.koin.dsl.module

val domainModule = module {
    single { LoginUseCase(get()) }
    single { LogoutUseCase(get()) }
    single { GetConversationsWithPaginationUseCase(get()) }
    single { GetConversationDetailUseCase(get()) }
    single { GetConversationMessagesUseCase(get()) }
    single { GetConversationWorkUseCase(get()) }
    single { GetWorkConversationMessagesUseCase(get()) }
    single { CreateMessageUseCase(get()) }
    single { MarkMessageAsReadUseCase(get()) }
    single { MarkAllMessagesAsReadUseCase(get()) }
    single { GetSystemEventsUseCase(get()) }
    single { GetSystemEventsWithPaginationUseCase(get()) }
    single { MarkSystemEventAsViewedUseCase(get()) }
    single { MarkAllSystemEventsAsViewedUseCase(get()) }
    single { GetTasksForWorkUseCase(get()) }
    single { GetTasksForOwnerUseCase(get()) }
    single { GetTasksForSolverUseCase(get()) }
    single { GetTaskDetailUseCase(get()) }
    single { ChangeTaskStatusUseCase(get()) }
    single { DeleteTaskUseCase(get()) }
    single { NotifyTaskCompleteUseCase(get()) }
    single { GetEventsForWorkUseCase(get()) }
    single { GetEventByIdUseCase(get()) }
    single { GetEventCalendarUseCase(get()) }
    single { GetEventCalendarUserWorksUseCase(get()) }
    single { PostEventCalendarReservationUseCase(get()) }
    single { GetEventCalendarManageCreateDataUseCase(get()) }
    single { PostEventCalendarCreateUseCase(get()) }
    single { DeleteEventUseCase(get()) }
    single { GetUserListUseCase(get()) }
    single { GetUserProfileImageUseCase(get()) }
    single { ProfileImageCacheManager(get()) }
    single { GetCachedProfileImageUseCase(get(), get()) }
    single { RefreshUserDataOnStartupUseCase(get(), get(), get()) }
    single { GetVersionsForWorkUseCase(get()) }
    single { GetVersionsForWorkWithPaginationUseCase(get()) }
    single { GetWorkListUseCase(get()) }
    single { GetWorkListWithPaginationUseCase(get()) }
    single { GetWorkDetailUseCase(get()) }
}
