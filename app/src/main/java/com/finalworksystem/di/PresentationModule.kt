package com.finalworksystem.di

import com.finalworksystem.infrastructure.cache.GlobalCacheManager
import com.finalworksystem.presentation.view_model.auth.AuthViewModel
import com.finalworksystem.presentation.view_model.conversation.ConversationListViewModel
import com.finalworksystem.presentation.view_model.conversation.ConversationDetailViewModel
import com.finalworksystem.presentation.view_model.event.EventDetailViewModel
import com.finalworksystem.presentation.view_model.system_event.SystemEventViewModel
import com.finalworksystem.presentation.view_model.task.TaskListViewModel
import com.finalworksystem.presentation.view_model.task.TaskDetailViewModel
import com.finalworksystem.presentation.view_model.user.UserViewModel
import com.finalworksystem.presentation.view_model.work.WorkViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    single { GlobalCacheManager(get()) }
    viewModel { AuthViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { ConversationListViewModel(get(), get(), get(), get()) }
    viewModel { ConversationDetailViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { EventDetailViewModel(get(), get()) }
    viewModel { SystemEventViewModel(get(), get(), get(), get(), get()) }
    viewModel { WorkViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { TaskListViewModel(get(), get(), get(), get()) }
    viewModel { TaskDetailViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { UserViewModel(get(), get(), get()) }
}
