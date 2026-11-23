package com.finalworksystem.di

import com.finalworksystem.infrastructure.cache.GlobalCacheManager
import com.finalworksystem.presentation.view_model.auth.AuthViewModel
import com.finalworksystem.presentation.view_model.conversation.ConversationDetailViewModel
import com.finalworksystem.presentation.view_model.conversation.ConversationListViewModel
import com.finalworksystem.presentation.view_model.event.EventDetailViewModel
import com.finalworksystem.presentation.view_model.system_event.SystemEventViewModel
import com.finalworksystem.presentation.view_model.task.TaskCreateViewModel
import com.finalworksystem.presentation.view_model.task.TaskDetailViewModel
import com.finalworksystem.presentation.view_model.task.TaskListViewModel
import com.finalworksystem.presentation.view_model.user.ChangePasswordViewModel
import com.finalworksystem.presentation.view_model.user.UserViewModel
import com.finalworksystem.presentation.view_model.work.WorkCreateViewModel
import com.finalworksystem.presentation.view_model.work.WorkDetailViewModel
import com.finalworksystem.presentation.view_model.work.WorkListViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    singleOf(::GlobalCacheManager)
    viewModelOf(::AuthViewModel)
    viewModelOf(::ConversationListViewModel)
    viewModelOf(::ConversationDetailViewModel)
    viewModelOf(::EventDetailViewModel)
    viewModelOf(::SystemEventViewModel)
    viewModelOf(::WorkListViewModel)
    viewModelOf(::WorkDetailViewModel)
    viewModelOf(::WorkCreateViewModel)
    viewModelOf(::TaskListViewModel)
    viewModelOf(::TaskCreateViewModel)
    viewModelOf(::TaskDetailViewModel)
    viewModelOf(::UserViewModel)
    viewModelOf(::ChangePasswordViewModel)
}
