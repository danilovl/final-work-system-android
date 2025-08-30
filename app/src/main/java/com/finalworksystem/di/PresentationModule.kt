package com.finalworksystem.di

import com.finalworksystem.infrastructure.cache.GlobalCacheManager
import com.finalworksystem.presentation.view_model.auth.AuthViewModel
import com.finalworksystem.presentation.view_model.conversation.ConversationViewModel
import com.finalworksystem.presentation.view_model.event.EventDetailViewModel
import com.finalworksystem.presentation.view_model.system_event.SystemEventViewModel
import com.finalworksystem.presentation.view_model.task.TaskViewModel
import com.finalworksystem.presentation.view_model.user.UserViewModel
import com.finalworksystem.presentation.view_model.work.WorkViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    single { GlobalCacheManager(get()) }
    viewModel { AuthViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { ConversationViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { EventDetailViewModel(get(), get()) }
    viewModel { SystemEventViewModel(get(), get(), get(), get(), get()) }
    viewModel { WorkViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { TaskViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { UserViewModel(get(), get(), get()) }
}
