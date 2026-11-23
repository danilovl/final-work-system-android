package com.finalworksystem.presentation.view_model.work

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.finalworksystem.application.use_case.conversation.GetConversationWorkUseCase
import com.finalworksystem.application.use_case.conversation.GetWorkConversationMessagesUseCase
import com.finalworksystem.application.use_case.event.GetEventsForWorkUseCase
import com.finalworksystem.application.use_case.task.GetTasksForWorkUseCase
import com.finalworksystem.application.use_case.version.GetVersionsForWorkWithPaginationUseCase
import com.finalworksystem.application.use_case.work.GetWorkDetailUseCase
import com.finalworksystem.data.util.isNotFound
import com.finalworksystem.domain.conversation.model.ConversationMessage
import com.finalworksystem.domain.conversation.model.response.ConversationWorkResponse
import com.finalworksystem.domain.event.model.Event
import com.finalworksystem.domain.task.model.Task
import com.finalworksystem.domain.version.model.Version
import com.finalworksystem.domain.work.model.Work
import com.finalworksystem.infrastructure.cache.CacheCleanupReason
import com.finalworksystem.infrastructure.cache.GlobalCacheManager
import com.finalworksystem.infrastructure.popup.PopupMessageService
import com.finalworksystem.infrastructure.user.UserService
import com.finalworksystem.presentation.paging.Page
import com.finalworksystem.presentation.paging.PagedList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class WorkDetailViewModel(
    private val getWorkDetailUseCase: GetWorkDetailUseCase,
    private val getTasksForWorkUseCase: GetTasksForWorkUseCase,
    private val getEventsForWorkUseCase: GetEventsForWorkUseCase,
    private val getVersionsForWorkWithPaginationUseCase: GetVersionsForWorkWithPaginationUseCase,
    private val getWorkConversationMessagesUseCase: GetWorkConversationMessagesUseCase,
    private val getConversationWorkUseCase: GetConversationWorkUseCase,
    private val popupMessageService: PopupMessageService,
    userService: UserService,
    globalCacheManager: GlobalCacheManager
) : ViewModel() {

    sealed class WorkDetailState {
        data object Idle : WorkDetailState()
        data object Loading : WorkDetailState()
        data class Success(val work: Work) : WorkDetailState()
        data class Error(val message: String) : WorkDetailState()
        data object NotFound : WorkDetailState()
    }

    sealed class EventsState {
        data object Idle : EventsState()
        data object Loading : EventsState()
        data class Success(val events: List<Event>) : EventsState()
        data class Error(val message: String) : EventsState()
    }

    sealed class ConversationWorkState {
        data object Idle : ConversationWorkState()
        data object Loading : ConversationWorkState()
        data class Success(val conversationWork: ConversationWorkResponse?) : ConversationWorkState()
        data class Error(val message: String) : ConversationWorkState()
    }

    private val _workDetailState = MutableStateFlow<WorkDetailState>(WorkDetailState.Idle)
    val workDetailState: StateFlow<WorkDetailState> = _workDetailState.asStateFlow()

    private val _eventsState = MutableStateFlow<EventsState>(EventsState.Idle)
    val eventsState: StateFlow<EventsState> = _eventsState.asStateFlow()

    private val _conversationWorkState = MutableStateFlow<ConversationWorkState>(ConversationWorkState.Idle)
    val conversationWorkState: StateFlow<ConversationWorkState> = _conversationWorkState.asStateFlow()

    // Paged tabs; the query is the work id.
    private val pagedTasks = PagedList<Int, Task>(viewModelScope, TASKS_PAGE_SIZE) { workId, page, pageSize ->
        getTasksForWorkUseCase(workId, page, pageSize).first()
            .map { Page(it.tasks, it.totalCount) }
    }

    private val pagedVersions = PagedList<Int, Version>(viewModelScope, VERSIONS_PAGE_SIZE) { workId, page, pageSize ->
        getVersionsForWorkWithPaginationUseCase(workId, page, pageSize).first()
            .map { Page(it.versions, it.totalCount) }
    }

    private val pagedMessages = PagedList<Int, ConversationMessage>(viewModelScope, MESSAGES_PAGE_SIZE) { workId, page, pageSize ->
        getWorkConversationMessagesUseCase(workId, page, pageSize).first()
            .map { Page(it.result, it.totalCount) }
    }

    val tasks: Flow<PagingData<Task>> = pagedTasks.items
    val tasksTotalCount: StateFlow<Int> = pagedTasks.totalCount

    val versions: Flow<PagingData<Version>> = pagedVersions.items
    val versionsTotalCount: StateFlow<Int> = pagedVersions.totalCount

    val workMessages: Flow<PagingData<ConversationMessage>> = pagedMessages.items

    private var cachedWorkDetails: Map<Int, Work> = mapOf()
    private var cachedEvents: Map<Int, List<Event>> = mapOf()
    private var cachedConversationWork: Map<Int, ConversationWorkResponse?> = mapOf()

    init {
        // Data of one user must never be shown to another one.
        viewModelScope.launch {
            userService.getUserFlowSafe()
                .map { it?.id }
                .distinctUntilChanged()
                .drop(1)
                .collect { clearWorkCache() }
        }

        viewModelScope.launch {
            globalCacheManager.cacheCleanupEvents.collect { cacheCleanupEvent ->
                if (cacheCleanupEvent.reason == CacheCleanupReason.DATA_UPDATED) {
                    refreshShownWork()
                } else {
                    clearWorkCache()
                }
            }
        }
    }

    fun loadWorkDetail(id: Int, type: String? = null, forceRefresh: Boolean = false) {
        val cachedWorkDetail = cachedWorkDetails[id]
        if (!forceRefresh && cachedWorkDetail != null) {
            _workDetailState.value = WorkDetailState.Success(cachedWorkDetail)
            return
        }

        viewModelScope.launch {
            _workDetailState.value = WorkDetailState.Loading

            getWorkDetailUseCase(id, type).collect { result ->
                result.fold(
                    onSuccess = { work ->
                        cachedWorkDetails = cachedWorkDetails + (id to work)
                        _workDetailState.value = WorkDetailState.Success(work)
                    },
                    onFailure = { error ->
                        if (error.isNotFound()) {
                            _workDetailState.value = WorkDetailState.NotFound
                            popupMessageService.showMessageResource(
                                com.finalworksystem.R.string.object_not_found,
                                PopupMessageService.MessageLevel.ERROR
                            )
                        } else {
                            val errorMessage = error.message ?: "Unknown error"
                            _workDetailState.value = WorkDetailState.Error(errorMessage)
                            popupMessageService.showMessage(errorMessage, PopupMessageService.MessageLevel.ERROR)
                        }
                    }
                )
            }
        }
    }

    fun loadTasksForWork(workId: Int, forceRefresh: Boolean = false) {
        pagedTasks.loadOrRefresh(workId, forceRefresh)
    }

    fun loadVersionsForWork(workId: Int, forceRefresh: Boolean = false) {
        pagedVersions.loadOrRefresh(workId, forceRefresh)
    }

    fun loadWorkMessages(workId: Int, forceRefresh: Boolean = false) {
        pagedMessages.loadOrRefresh(workId, forceRefresh)
    }

    fun loadEventsForWork(workId: Int, forceRefresh: Boolean = false) {
        val cachedEventsList = cachedEvents[workId]
        if (!forceRefresh && cachedEventsList != null) {
            _eventsState.value = EventsState.Success(cachedEventsList)
            return
        }

        viewModelScope.launch {
            _eventsState.value = EventsState.Loading

            getEventsForWorkUseCase(workId).collect { result ->
                result.fold(
                    onSuccess = { events ->
                        cachedEvents = cachedEvents + (workId to events)
                        _eventsState.value = EventsState.Success(events)
                    },
                    onFailure = { error ->
                        val errorMessage = error.message ?: "Unknown error"
                        _eventsState.value = EventsState.Error(errorMessage)
                        if (!error.isNotFound()) {
                            popupMessageService.showMessage(errorMessage, PopupMessageService.MessageLevel.ERROR)
                        }
                    }
                )
            }
        }
    }

    fun loadConversationWork(workId: Int, forceRefresh: Boolean = false) {
        if (!forceRefresh && cachedConversationWork.containsKey(workId)) {
            _conversationWorkState.value = ConversationWorkState.Success(cachedConversationWork[workId])
            return
        }

        viewModelScope.launch {
            _conversationWorkState.value = ConversationWorkState.Loading

            getConversationWorkUseCase(workId).first().fold(
                onSuccess = { conversationWork ->
                    cachedConversationWork = cachedConversationWork + (workId to conversationWork)
                    _conversationWorkState.value = ConversationWorkState.Success(conversationWork)
                },
                onFailure = { error ->
                    val errorMessage = error.message ?: "Unknown error"
                    _conversationWorkState.value = ConversationWorkState.Error(errorMessage)
                }
            )
        }
    }

    fun clearWorkCache() {
        cachedWorkDetails = mapOf()
        cachedEvents = mapOf()
        cachedConversationWork = mapOf()
        pagedTasks.reset()
        pagedVersions.reset()
        pagedMessages.reset()
    }

    /** Something was created or changed: reload the tabs of the work that is shown. */
    private fun refreshShownWork() {
        val currentState = _workDetailState.value
        if (currentState is WorkDetailState.Success) {
            val workId = currentState.work.id
            loadTasksForWork(workId, forceRefresh = true)
            loadVersionsForWork(workId, forceRefresh = true)
            loadEventsForWork(workId, forceRefresh = true)
            loadWorkMessages(workId, forceRefresh = true)
        }
    }

    private fun <T : Any> PagedList<Int, T>.loadOrRefresh(workId: Int, forceRefresh: Boolean) {
        if (forceRefresh) reload(workId) else load(workId)
    }

    private companion object {
        const val TASKS_PAGE_SIZE = 15
        const val VERSIONS_PAGE_SIZE = 20
        const val MESSAGES_PAGE_SIZE = 20
    }
}
