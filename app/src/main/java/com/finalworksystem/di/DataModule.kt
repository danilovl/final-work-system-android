package com.finalworksystem.di

import com.finalworksystem.data.conversation.repository.GetConversationRepositoryImpl
import com.finalworksystem.data.conversation.repository.PostConversationRepositoryImpl
import com.finalworksystem.data.conversation.repository.PutConversationRepositoryImpl
import com.finalworksystem.data.event.repository.GetEventRepositoryImpl
import com.finalworksystem.data.event_calendar.repository.DeleteEventCalendarRepositoryImpl
import com.finalworksystem.data.event_calendar.repository.GetEventCalendarRepositoryImpl
import com.finalworksystem.data.event_calendar.repository.PostEventCalendarRepositoryImpl
import com.finalworksystem.data.system_event.repository.GetSystemEventRepositoryImpl
import com.finalworksystem.data.system_event.repository.PutSystemEventRepositoryImpl
import com.finalworksystem.data.task.repository.DeleteTaskRepositoryImpl
import com.finalworksystem.data.task.repository.GetTaskRepositoryImpl
import com.finalworksystem.data.task.repository.PutTaskRepositoryImpl
import com.finalworksystem.data.version.repository.GetVersionRepositoryImpl
import com.finalworksystem.data.work.repository.GetWorkRepositoryImpl
import com.finalworksystem.domain.auth.repository.AuthRepository
import com.finalworksystem.domain.conversation.repository.GetConversationRepository
import com.finalworksystem.domain.conversation.repository.PostConversationRepository
import com.finalworksystem.domain.conversation.repository.PutConversationRepository
import com.finalworksystem.domain.event.repository.GetEventRepository
import com.finalworksystem.domain.event_calendar.repository.DeleteEventCalendarRepository
import com.finalworksystem.domain.event_calendar.repository.GetEventCalendarRepository
import com.finalworksystem.domain.event_calendar.repository.PostEventCalendarRepository
import com.finalworksystem.domain.system_event.repository.GetSystemEventRepository
import com.finalworksystem.domain.system_event.repository.PutSystemEventRepository
import com.finalworksystem.domain.task.repository.DeleteTaskRepository
import com.finalworksystem.domain.task.repository.GetTaskRepository
import com.finalworksystem.domain.task.repository.PutTaskRepository
import com.finalworksystem.domain.user.repository.GetUserRepository
import com.finalworksystem.domain.user.repository.SessionUserRepository
import com.finalworksystem.domain.version.repository.GetVersionRepository
import com.finalworksystem.domain.work.repository.GetWorkRepository
import com.finalworksystem.infrastructure.api.ApiClient
import com.finalworksystem.infrastructure.network.NetworkConnectivityService
import com.finalworksystem.infrastructure.popup.PopupMessageService
import com.finalworksystem.infrastructure.session.AppLifecycleManager
import com.finalworksystem.infrastructure.session.SessionManager
import com.finalworksystem.infrastructure.user.UserService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import com.finalworksystem.data.auth.repository.AuthRepository as DataAuthRepository
import com.finalworksystem.data.user.repository.GetUserRepository as DataGetUserRepository
import com.finalworksystem.data.user.repository.SessionUserRepository as DataSessionUserRepository

val dataModule = module {
    single { SessionManager(androidContext()) }
    single { CoroutineScope(SupervisorJob() + Dispatchers.Main) }
    single { AppLifecycleManager(get(), get(), get(), get()) }
    single { ApiClient(get(), androidContext(), get()) }
    single { get<ApiClient>().apiAuthService }
    single { get<ApiClient>().apiUserService }
    single { get<ApiClient>().apiWorkService }
    single { get<ApiClient>().apiTaskService }
    single { get<ApiClient>().apiSystemEventService }
    single { get<ApiClient>().apiConversationService }
    single { get<ApiClient>().apiVersionService }
    single { get<ApiClient>().apiEventService }
    single { PopupMessageService() }
    single<AuthRepository> {
        DataAuthRepository(get(), get(), get())
    }

    single<GetSystemEventRepository> {
        GetSystemEventRepositoryImpl(get())
    }

    single<PutSystemEventRepository> {
        PutSystemEventRepositoryImpl(get())
    }

    single<GetUserRepository> {
        DataGetUserRepository(get(), get())
    }

    single<SessionUserRepository> {
        DataSessionUserRepository(get())
    }

    single { UserService(get()) }
    single { NetworkConnectivityService(androidContext()) }

    single<GetWorkRepository> {
        GetWorkRepositoryImpl(get())
    }

    single<GetTaskRepository> {
        GetTaskRepositoryImpl(get())
    }

    single<PutTaskRepository> {
        PutTaskRepositoryImpl(get())
    }

    single<DeleteTaskRepository> {
        DeleteTaskRepositoryImpl(get())
    }

    single<GetConversationRepository> {
        GetConversationRepositoryImpl(get())
    }

    single<PostConversationRepository> {
        PostConversationRepositoryImpl(get())
    }

    single<PutConversationRepository> {
        PutConversationRepositoryImpl(get())
    }

    single<GetVersionRepository> {
        GetVersionRepositoryImpl(get())
    }

    single<GetEventRepository> {
        GetEventRepositoryImpl(get())
    }

    single<GetEventCalendarRepository> {
        GetEventCalendarRepositoryImpl(get())
    }

    single<PostEventCalendarRepository> {
        PostEventCalendarRepositoryImpl(get())
    }

    single<DeleteEventCalendarRepository> {
        DeleteEventCalendarRepositoryImpl(get())
    }
}
