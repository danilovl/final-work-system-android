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
import com.finalworksystem.application.use_case.task.PostTaskCreateUseCase
import com.finalworksystem.application.use_case.user.GetUserListUseCase
import com.finalworksystem.application.use_case.user.RefreshUserDataOnStartupUseCase
import com.finalworksystem.application.use_case.version.GetVersionsForWorkUseCase
import com.finalworksystem.application.use_case.version.GetVersionsForWorkWithPaginationUseCase
import com.finalworksystem.application.use_case.work.GetWorkCategoriesUseCase
import com.finalworksystem.application.use_case.work.GetWorkDetailUseCase
import com.finalworksystem.application.use_case.work.GetWorkListUseCase
import com.finalworksystem.application.use_case.work.GetWorkListWithPaginationUseCase
import com.finalworksystem.application.use_case.work.GetWorkStatusesUseCase
import com.finalworksystem.application.use_case.work.GetWorkTypesUseCase
import com.finalworksystem.application.use_case.work.PostWorkCreateUseCase
import com.finalworksystem.application.use_case.user.ChangePasswordUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val domainModule = module {
    singleOf(::LoginUseCase)
    singleOf(::LogoutUseCase)
    singleOf(::GetConversationsWithPaginationUseCase)
    singleOf(::GetConversationDetailUseCase)
    singleOf(::GetConversationMessagesUseCase)
    singleOf(::GetConversationWorkUseCase)
    singleOf(::GetWorkConversationMessagesUseCase)
    singleOf(::CreateMessageUseCase)
    singleOf(::MarkMessageAsReadUseCase)
    singleOf(::MarkAllMessagesAsReadUseCase)
    singleOf(::GetSystemEventsUseCase)
    singleOf(::GetSystemEventsWithPaginationUseCase)
    singleOf(::MarkSystemEventAsViewedUseCase)
    singleOf(::MarkAllSystemEventsAsViewedUseCase)
    singleOf(::GetTasksForWorkUseCase)
    singleOf(::GetTasksForOwnerUseCase)
    singleOf(::GetTasksForSolverUseCase)
    singleOf(::GetTaskDetailUseCase)
    singleOf(::ChangeTaskStatusUseCase)
    singleOf(::DeleteTaskUseCase)
    singleOf(::NotifyTaskCompleteUseCase)
    singleOf(::PostTaskCreateUseCase)
    singleOf(::GetEventsForWorkUseCase)
    singleOf(::GetEventByIdUseCase)
    singleOf(::GetEventCalendarUseCase)
    singleOf(::GetEventCalendarUserWorksUseCase)
    singleOf(::PostEventCalendarReservationUseCase)
    singleOf(::GetEventCalendarManageCreateDataUseCase)
    singleOf(::PostEventCalendarCreateUseCase)
    singleOf(::DeleteEventUseCase)
    singleOf(::GetUserListUseCase)
    singleOf(::ChangePasswordUseCase)
    singleOf(::RefreshUserDataOnStartupUseCase)
    singleOf(::GetVersionsForWorkUseCase)
    singleOf(::GetVersionsForWorkWithPaginationUseCase)
    singleOf(::GetWorkListUseCase)
    singleOf(::GetWorkListWithPaginationUseCase)
    singleOf(::GetWorkDetailUseCase)
    singleOf(::GetWorkTypesUseCase)
    singleOf(::GetWorkStatusesUseCase)
    singleOf(::GetWorkCategoriesUseCase)
    singleOf(::PostWorkCreateUseCase)
}
