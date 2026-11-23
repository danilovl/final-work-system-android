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
import com.finalworksystem.data.task.repository.PostTaskRepositoryImpl
import com.finalworksystem.data.task.repository.PutTaskRepositoryImpl
import com.finalworksystem.data.user.repository.PutUserRepositoryImpl
import com.finalworksystem.domain.user.repository.PutUserRepository
import com.finalworksystem.data.version.repository.GetVersionRepositoryImpl
import com.finalworksystem.data.work.repository.GetWorkRepositoryImpl
import com.finalworksystem.data.work.repository.PostWorkRepositoryImpl
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
import com.finalworksystem.domain.task.repository.PostTaskRepository
import com.finalworksystem.domain.task.repository.PutTaskRepository
import com.finalworksystem.domain.user.repository.GetUserRepository
import com.finalworksystem.domain.user.repository.SessionUserRepository
import com.finalworksystem.domain.version.repository.GetVersionRepository
import com.finalworksystem.domain.work.repository.GetWorkRepository
import com.finalworksystem.domain.work.repository.PostWorkRepository
import com.finalworksystem.infrastructure.api.ApiClient
import com.finalworksystem.infrastructure.network.NetworkConnectivityService
import com.finalworksystem.infrastructure.popup.PopupMessageService
import com.finalworksystem.infrastructure.session.AppLifecycleManager
import com.finalworksystem.infrastructure.session.SessionManager
import com.finalworksystem.infrastructure.user.UserService
import androidx.datastore.core.DataStore
import com.finalworksystem.infrastructure.image.ProfileImageCache
import com.finalworksystem.infrastructure.session.Session
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import com.finalworksystem.data.auth.repository.AuthRepository as DataAuthRepository
import com.finalworksystem.data.user.repository.GetUserRepository as DataGetUserRepository
import com.finalworksystem.data.user.repository.SessionUserRepository as DataSessionUserRepository

val dataModule = module {
    single<DataStore<Session>> { SessionManager.createDataStore(androidContext()) }
    singleOf(::SessionManager)
    single { CoroutineScope(SupervisorJob() + Dispatchers.Main) }
    singleOf(::AppLifecycleManager)
    singleOf(::ApiClient)
    single { get<ApiClient>().apiAuthService }
    single { get<ApiClient>().apiUserService }
    single { get<ApiClient>().apiWorkService }
    single { get<ApiClient>().apiTaskService }
    single { get<ApiClient>().apiSystemEventService }
    single { get<ApiClient>().apiConversationService }
    single { get<ApiClient>().apiVersionService }
    single { get<ApiClient>().apiEventService }
    single { ProfileImageCache.createImageLoader(androidContext()) { get<ApiClient>().imageHttpClient } }
    singleOf(::ProfileImageCache)
    singleOf(::PopupMessageService)
    singleOf(::UserService)
    singleOf(::NetworkConnectivityService)

    singleOf(::DataAuthRepository) { bind<AuthRepository>() }
    singleOf(::GetSystemEventRepositoryImpl) { bind<GetSystemEventRepository>() }
    singleOf(::PutSystemEventRepositoryImpl) { bind<PutSystemEventRepository>() }
    singleOf(::DataGetUserRepository) { bind<GetUserRepository>() }
    singleOf(::DataSessionUserRepository) { bind<SessionUserRepository>() }
    singleOf(::PutUserRepositoryImpl) { bind<PutUserRepository>() }
    singleOf(::GetWorkRepositoryImpl) { bind<GetWorkRepository>() }
    singleOf(::PostWorkRepositoryImpl) { bind<PostWorkRepository>() }
    singleOf(::PostTaskRepositoryImpl) { bind<PostTaskRepository>() }
    singleOf(::GetTaskRepositoryImpl) { bind<GetTaskRepository>() }
    singleOf(::PutTaskRepositoryImpl) { bind<PutTaskRepository>() }
    singleOf(::DeleteTaskRepositoryImpl) { bind<DeleteTaskRepository>() }
    singleOf(::GetConversationRepositoryImpl) { bind<GetConversationRepository>() }
    singleOf(::PostConversationRepositoryImpl) { bind<PostConversationRepository>() }
    singleOf(::PutConversationRepositoryImpl) { bind<PutConversationRepository>() }
    singleOf(::GetVersionRepositoryImpl) { bind<GetVersionRepository>() }
    singleOf(::GetEventRepositoryImpl) { bind<GetEventRepository>() }
    singleOf(::GetEventCalendarRepositoryImpl) { bind<GetEventCalendarRepository>() }
    singleOf(::PostEventCalendarRepositoryImpl) { bind<PostEventCalendarRepository>() }
    singleOf(::DeleteEventCalendarRepositoryImpl) { bind<DeleteEventCalendarRepository>() }
}
