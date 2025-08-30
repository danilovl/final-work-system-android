package com.finalworksystem.presentation.view_model.work

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.finalworksystem.application.use_case.conversation.GetConversationWorkUseCase
import com.finalworksystem.application.use_case.conversation.GetWorkConversationMessagesUseCase
import com.finalworksystem.application.use_case.event.GetEventsForWorkUseCase
import com.finalworksystem.application.use_case.task.GetTasksForWorkUseCase
import com.finalworksystem.application.use_case.version.GetVersionsForWorkWithPaginationUseCase
import com.finalworksystem.application.use_case.work.GetWorkDetailUseCase
import com.finalworksystem.application.use_case.work.GetWorkListWithPaginationUseCase
import com.finalworksystem.domain.conversation.model.ConversationMessage
import com.finalworksystem.domain.conversation.model.response.ConversationWorkResponse
import com.finalworksystem.domain.event.model.Event
import com.finalworksystem.domain.task.model.Task
import com.finalworksystem.domain.version.model.Version
import com.finalworksystem.domain.work.model.Work
import com.finalworksystem.domain.work.model.WorkListType
import com.finalworksystem.infrastructure.cache.GlobalCacheManager
import com.finalworksystem.infrastructure.popup.PopupMessageService
import com.finalworksystem.infrastructure.user.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class WorkViewModel(
    application: Application,
    private val getWorkListWithPaginationUseCase: GetWorkListWithPaginationUseCase,
    private val getWorkDetailUseCase: GetWorkDetailUseCase,
    private val getTasksForWorkUseCase: GetTasksForWorkUseCase,
    private val getEventsForWorkUseCase: GetEventsForWorkUseCase,
    private val getVersionsForWorkWithPaginationUseCase: GetVersionsForWorkWithPaginationUseCase,
    private val getWorkConversationMessagesUseCase: GetWorkConversationMessagesUseCase,
    private val getConversationWorkUseCase: GetConversationWorkUseCase,
    private val popupMessageService: PopupMessageService,
    private val userService: UserService,
    private val globalCacheManager: GlobalCacheManager
) : AndroidViewModel(application) {

    private val _worksState = MutableStateFlow<WorksState>(WorksState.Idle)
    val worksState: StateFlow<WorksState> = _worksState.asStateFlow()

    private val _workDetailState = MutableStateFlow<WorkDetailState>(WorkDetailState.Idle)
    val workDetailState: StateFlow<WorkDetailState> = _workDetailState.asStateFlow()

    private val _tasksState = MutableStateFlow<TasksState>(TasksState.Idle)
    val tasksState: StateFlow<TasksState> = _tasksState.asStateFlow()

    private val _versionsState = MutableStateFlow<VersionsState>(VersionsState.Idle)
    val versionsState: StateFlow<VersionsState> = _versionsState.asStateFlow()

    private val _eventsState = MutableStateFlow<EventsState>(EventsState.Idle)
    val eventsState: StateFlow<EventsState> = _eventsState.asStateFlow()

    private val _workMessagesState = MutableStateFlow<WorkMessagesState>(WorkMessagesState.Idle)
    val workMessagesState: StateFlow<WorkMessagesState> = _workMessagesState.asStateFlow()

    private val _conversationWorkState = MutableStateFlow<ConversationWorkState>(ConversationWorkState.Idle)
    val conversationWorkState: StateFlow<ConversationWorkState> = _conversationWorkState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var hasLeftWorkListSection = false

    private var cachedWorks: Map<WorkListType, List<Work>> = mapOf()

    private var cachedWorkDetails: Map<Int, Work> = mapOf()

    private var cachedTasks: Map<Int, List<Task>> = mapOf()

    private var cachedVersions: Map<Int, List<Version>> = mapOf()

    private var cachedEvents: Map<Int, List<Event>> = mapOf()

    private var cachedWorkMessages: Map<Int, List<ConversationMessage>> = mapOf()

    private var cachedConversationWork: Map<Int, ConversationWorkResponse?> = mapOf()

    private var currentPage = 1
    private val pageSize = 20
    private var currentType = WorkListType.AUTHOR
    private var currentSearch: String? = null
    private var isLoadingMore = false

    private var currentTasksPageMap: MutableMap<Int, Int> = mutableMapOf()
    private var totalTasksCountMap: MutableMap<Int, Int> = mutableMapOf()
    private val tasksPageSize = 15
    private var isLoadingMoreTasks = false

    private var currentUserId: Int? = null

    init {
        viewModelScope.launch {
            userService.getUserFlowSafe().collect { user ->
                val newUserId = user?.id
                if (currentUserId != null && currentUserId != newUserId) {
                    clearWorkCache()
                }
                currentUserId = newUserId
            }
        }

        viewModelScope.launch {
            globalCacheManager.cacheCleanupEvents.collect { cacheCleanupEvent ->
                clearWorkCache()
                popupMessageService.showMessage(
                    "Cache cleared due to ${cacheCleanupEvent.reason.name.lowercase()}", 
                    PopupMessageService.MessageLevel.INFO
                )
            }
        }
    }

    fun loadWorks(type: WorkListType = WorkListType.AUTHOR, forceRefresh: Boolean = false, search: String? = null) {
        if (!forceRefresh && !hasLeftWorkListSection && cachedWorks.containsKey(type) && search == null) {
            val cachedWorksList = cachedWorks[type]
            if (cachedWorksList != null && cachedWorksList.isNotEmpty()) {
                _worksState.value = WorksState.Success(cachedWorksList, false, false, cachedWorksList.size)
                return
            }
        }

        viewModelScope.launch {
            _worksState.value = WorksState.Loading
            currentPage = 1
            currentType = type
            currentSearch = search

            getWorkListWithPaginationUseCase(type, currentPage, pageSize, search).collect { result ->
                result.fold(
                    onSuccess = { paginatedWorks ->
                        val works = paginatedWorks.works
                        val hasMore = paginatedWorks.currentItemCount + ((currentPage - 1) * pageSize) < paginatedWorks.totalCount

                        cachedWorks = cachedWorks + (type to works)
                        hasLeftWorkListSection = false

                        _worksState.value = WorksState.Success(works, hasMore, false, paginatedWorks.totalCount)
                    },
                    onFailure = { error ->
                        val errorMessage = error.message ?: "Unknown error"
                        _worksState.value = WorksState.Error(errorMessage)
                        popupMessageService.showMessage(errorMessage, PopupMessageService.MessageLevel.ERROR)
                    }
                )
            }
        }
    }

    fun loadMoreWorks() {
        val currentState = _worksState.value
        if (currentState !is WorksState.Success || !currentState.hasMoreWorks || isLoadingMore || currentState.isLoadingMore) {
            return
        }

        isLoadingMore = true
        _worksState.value = currentState.copy(isLoadingMore = true)

        viewModelScope.launch {
            currentPage++

            getWorkListWithPaginationUseCase(currentType, currentPage, pageSize, currentSearch).collect { result ->
                isLoadingMore = false
                result.fold(
                    onSuccess = { paginatedWorks ->
                        val newWorks = paginatedWorks.works
                        val currentWorks = currentState.works
                        val updatedWorks = currentWorks + newWorks
                        val hasMore = updatedWorks.size < paginatedWorks.totalCount

                        _worksState.value = WorksState.Success(updatedWorks, hasMore, false, paginatedWorks.totalCount)
                    },
                    onFailure = { error ->
                        val errorMessage = error.message ?: "Unknown error"
                        _worksState.value = currentState.copy(isLoadingMore = false)
                        popupMessageService.showMessage("Failed to load more works: $errorMessage", PopupMessageService.MessageLevel.ERROR)
                    }
                )
            }
        }
    }

    fun loadWorkDetail(id: Int, type: String? = null, forceRefresh: Boolean = false) {
        if (!forceRefresh && !hasLeftWorkListSection && cachedWorkDetails.containsKey(id)) {
            val cachedWorkDetail = cachedWorkDetails[id]
            if (cachedWorkDetail != null) {
                _workDetailState.value = WorkDetailState.Success(cachedWorkDetail)
                return
            }
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
                        val errorMessage = error.message ?: "Unknown error"
                        _workDetailState.value = WorkDetailState.Error(errorMessage)
                        popupMessageService.showMessage(errorMessage, PopupMessageService.MessageLevel.ERROR)
                    }
                )
            }
        }
    }

    fun markLeftWorkListSection() {
        hasLeftWorkListSection = true
    }

    fun markEnteredWorkListSection() {
        hasLeftWorkListSection = false
        val wasErrorState = _worksState.value is WorksState.Error
        if (wasErrorState) {
            _worksState.value = WorksState.Idle
        }

        if (_searchQuery.value.isNotBlank() || wasErrorState) {
            loadWorks(currentType, forceRefresh = true, search = _searchQuery.value.takeIf { it.isNotBlank() })
        }
    }

    fun clearWorkCache() {
        cachedWorks = mapOf()
        cachedWorkDetails = mapOf()
        cachedTasks = mapOf()
        cachedVersions = mapOf()
        cachedEvents = mapOf()
        cachedWorkMessages = mapOf()
        cachedConversationWork = mapOf()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun performSearch(query: String) {
        loadWorks(currentType, forceRefresh = true, search = query.takeIf { it.isNotBlank() })
    }

    fun clearSearch() {
        _searchQuery.value = ""
        loadWorks(currentType, forceRefresh = true, search = null)
    }

    fun loadTasksForWork(workId: Int, forceRefresh: Boolean = false) {
        if (!forceRefresh && cachedTasks.containsKey(workId)) {
            val cachedTasksList = cachedTasks[workId]
            if (cachedTasksList != null) {
                val totalCount = totalTasksCountMap[workId] ?: 0
                val hasMore = if (totalCount > 0) {
                    cachedTasksList.size < totalCount
                } else {
                    val currentPage = currentTasksPageMap[workId] ?: 1
                    cachedTasksList.size >= tasksPageSize * currentPage
                }
                _tasksState.value = TasksState.Success(cachedTasksList, hasMore, false, totalCount)
                return
            }
        }

        viewModelScope.launch {
            _tasksState.value = TasksState.Loading
            currentTasksPageMap[workId] = 1

            getTasksForWorkUseCase(workId, 1, tasksPageSize).collect { result ->
                result.fold(
                    onSuccess = { paginatedTasks ->
                        val tasks = paginatedTasks.tasks
                        totalTasksCountMap[workId] = paginatedTasks.totalCount
                        val hasMore = tasks.size < paginatedTasks.totalCount

                        cachedTasks = cachedTasks + (workId to tasks)

                        _tasksState.value = TasksState.Success(tasks, hasMore, false, paginatedTasks.totalCount)
                    },
                    onFailure = { error ->
                        val errorMessage = error.message ?: "Unknown error"
                        _tasksState.value = TasksState.Error(errorMessage)
                        popupMessageService.showMessage(errorMessage, PopupMessageService.MessageLevel.ERROR)
                    }
                )
            }
        }
    }

    fun resetTasksState() {
        _tasksState.value = TasksState.Idle
    }

    fun loadMoreTasks(workId: Int) {
        val currentState = _tasksState.value
        if (currentState !is TasksState.Success || !currentState.hasMoreTasks || isLoadingMoreTasks || currentState.isLoadingMore) {
            return
        }

        isLoadingMoreTasks = true
        _tasksState.value = currentState.copy(isLoadingMore = true)

        viewModelScope.launch {
            val currentPage = currentTasksPageMap[workId] ?: 1
            val nextPage = currentPage + 1
            currentTasksPageMap[workId] = nextPage

            getTasksForWorkUseCase(workId, nextPage, tasksPageSize).collect { result ->
                isLoadingMoreTasks = false
                result.fold(
                    onSuccess = { paginatedTasks ->
                        val newTasks = paginatedTasks.tasks
                        val currentTasks = currentState.tasks
                        val updatedTasks = currentTasks + newTasks
                        totalTasksCountMap[workId] = paginatedTasks.totalCount
                        val hasMore = updatedTasks.size < paginatedTasks.totalCount

                        cachedTasks = cachedTasks + (workId to updatedTasks)

                        _tasksState.value = TasksState.Success(updatedTasks, hasMore, false, paginatedTasks.totalCount)
                    },
                    onFailure = { error ->
                        val errorMessage = error.message ?: "Unknown error"
                        currentTasksPageMap[workId] = currentPage
                        _tasksState.value = currentState.copy(isLoadingMore = false)
                        popupMessageService.showMessage("Failed to load more tasks: $errorMessage", PopupMessageService.MessageLevel.ERROR)
                    }
                )
            }
        }
    }

    fun loadEventsForWork(workId: Int, forceRefresh: Boolean = false) {
        if (!forceRefresh && cachedEvents.containsKey(workId)) {
            val cachedEventsList = cachedEvents[workId]
            if (cachedEventsList != null) {
                _eventsState.value = EventsState.Success(cachedEventsList)
                return
            }
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
                        popupMessageService.showMessage(errorMessage, PopupMessageService.MessageLevel.ERROR)
                    }
                )
            }
        }
    }

    fun loadVersionsForWork(workId: Int, forceRefresh: Boolean = false) {
        if (!forceRefresh && cachedVersions.containsKey(workId)) {
            val cachedVersionsList = cachedVersions[workId]
            if (cachedVersionsList != null) {
                val totalCount = totalVersionsCountMap[workId] ?: 0
                val hasMore = if (totalCount > 0) {
                    cachedVersionsList.size < totalCount
                } else {
                    val currentPage = currentVersionsPageMap[workId] ?: 1
                    cachedVersionsList.size >= versionsPageSize * currentPage
                }
                _versionsState.value = VersionsState.Success(cachedVersionsList, hasMore, false, totalCount)
                return
            }
        }

        viewModelScope.launch {
            _versionsState.value = VersionsState.Loading
            currentVersionsPageMap[workId] = 1

            getVersionsForWorkWithPaginationUseCase(workId, 1, versionsPageSize).collect { result ->
                result.fold(
                    onSuccess = { paginatedVersions ->
                        val versions = paginatedVersions.versions
                        totalVersionsCountMap[workId] = paginatedVersions.totalCount
                        val hasMore = versions.size < paginatedVersions.totalCount

                        cachedVersions = cachedVersions + (workId to versions)

                        _versionsState.value = VersionsState.Success(versions, hasMore, false, paginatedVersions.totalCount)
                    },
                    onFailure = { error ->
                        val errorMessage = error.message ?: "Unknown error"
                        _versionsState.value = VersionsState.Error(errorMessage)
                        popupMessageService.showMessage(errorMessage, PopupMessageService.MessageLevel.ERROR)
                    }
                )
            }
        }
    }

    fun loadMoreVersions(workId: Int) {
        val currentState = _versionsState.value
        if (currentState !is VersionsState.Success || !currentState.hasMoreVersions || isLoadingMoreVersions || currentState.isLoadingMore) {
            return
        }

        isLoadingMoreVersions = true
        _versionsState.value = currentState.copy(isLoadingMore = true)

        viewModelScope.launch {
            val currentPage = currentVersionsPageMap[workId] ?: 1
            val nextPage = currentPage + 1
            currentVersionsPageMap[workId] = nextPage

            getVersionsForWorkWithPaginationUseCase(workId, nextPage, versionsPageSize).collect { result ->
                isLoadingMoreVersions = false
                result.fold(
                    onSuccess = { paginatedVersions ->
                        val newVersions = paginatedVersions.versions
                        val currentVersions = currentState.versions
                        val updatedVersions = currentVersions + newVersions
                        totalVersionsCountMap[workId] = paginatedVersions.totalCount
                        val hasMore = updatedVersions.size < paginatedVersions.totalCount

                        cachedVersions = cachedVersions + (workId to updatedVersions)

                        _versionsState.value = VersionsState.Success(updatedVersions, hasMore, false, paginatedVersions.totalCount)
                    },
                    onFailure = { error ->
                        val errorMessage = error.message ?: "Unknown error"
                        currentVersionsPageMap[workId] = currentPage
                        _versionsState.value = currentState.copy(isLoadingMore = false)
                        popupMessageService.showMessage("Failed to load more versions: $errorMessage", PopupMessageService.MessageLevel.ERROR)
                    }
                )
            }
        }
    }

    private val messagesPageSize = 20
    private var isLoadingMoreMessages = false

    private val currentMessagesPageMap = mutableMapOf<Int, Int>()
    private val totalMessagesCountMap = mutableMapOf<Int, Int>()

    private val versionsPageSize = 20
    private var isLoadingMoreVersions = false

    private val currentVersionsPageMap = mutableMapOf<Int, Int>()
    private val totalVersionsCountMap = mutableMapOf<Int, Int>()

    fun loadWorkMessages(workId: Int, forceRefresh: Boolean = false) {
        if (!forceRefresh && cachedWorkMessages.containsKey(workId)) {
            val cachedMessagesList = cachedWorkMessages[workId]
            if (cachedMessagesList != null) {
                val totalCount = totalMessagesCountMap[workId] ?: 0
                val hasMore = if (totalCount > 0) {
                    cachedMessagesList.size < totalCount
                } else {
                    val currentPage = currentMessagesPageMap[workId] ?: 1
                    cachedMessagesList.size >= messagesPageSize * currentPage
                }
                _workMessagesState.value = WorkMessagesState.Success(cachedMessagesList, hasMore, false)
                return
            }
        }

        viewModelScope.launch {
            _workMessagesState.value = WorkMessagesState.Loading
            currentMessagesPageMap[workId] = 1

            getWorkConversationMessagesUseCase(workId, 1, messagesPageSize).collect { result ->
                result.fold(
                    onSuccess = { messagesResponse ->
                        val messages = messagesResponse.result
                        totalMessagesCountMap[workId] = messagesResponse.totalCount
                        val hasMore = messages.size < messagesResponse.totalCount

                        cachedWorkMessages = cachedWorkMessages + (workId to messages)

                        _workMessagesState.value = WorkMessagesState.Success(messages, hasMore, false)
                    },
                    onFailure = { error ->
                        val errorMessage = error.message ?: "Unknown error"
                        _workMessagesState.value = WorkMessagesState.Error(errorMessage)
                        popupMessageService.showMessage(errorMessage, PopupMessageService.MessageLevel.ERROR)
                    }
                )
            }
        }
    }

    fun loadMoreWorkMessages(workId: Int) {
        val currentState = _workMessagesState.value
        if (currentState !is WorkMessagesState.Success || !currentState.hasMoreMessages || isLoadingMoreMessages || currentState.isLoadingMore) {
            return
        }

        isLoadingMoreMessages = true
        _workMessagesState.value = currentState.copy(isLoadingMore = true)

        viewModelScope.launch {
            val currentPage = currentMessagesPageMap[workId] ?: 1
            val nextPage = currentPage + 1
            currentMessagesPageMap[workId] = nextPage

            getWorkConversationMessagesUseCase(workId, nextPage, messagesPageSize).collect { result ->
                isLoadingMoreMessages = false
                result.fold(
                    onSuccess = { messagesResponse ->
                        val newMessages = messagesResponse.result
                        val currentMessages = currentState.messages
                        val updatedMessages = currentMessages + newMessages
                        totalMessagesCountMap[workId] = messagesResponse.totalCount
                        val hasMore = updatedMessages.size < messagesResponse.totalCount

                        cachedWorkMessages = cachedWorkMessages + (workId to updatedMessages)

                        _workMessagesState.value = WorkMessagesState.Success(updatedMessages, hasMore, false)
                    },
                    onFailure = { error ->
                        val errorMessage = error.message ?: "Unknown error"
                        currentMessagesPageMap[workId] = currentPage
                        _workMessagesState.value = currentState.copy(isLoadingMore = false)
                        popupMessageService.showMessage("Failed to load more messages: $errorMessage", PopupMessageService.MessageLevel.ERROR)
                    }
                )
            }
        }
    }

    fun loadConversationWork(workId: Int, forceRefresh: Boolean = false) {
        if (!forceRefresh && cachedConversationWork.containsKey(workId)) {
            val cachedData = cachedConversationWork[workId]
            _conversationWorkState.value = ConversationWorkState.Success(cachedData)
            return
        }

        viewModelScope.launch {
            _conversationWorkState.value = ConversationWorkState.Loading

            val result = getConversationWorkUseCase(workId).first()
            result.fold(
                onSuccess = { conversationWork: ConversationWorkResponse? ->
                    cachedConversationWork = cachedConversationWork + (workId to conversationWork)
                    _conversationWorkState.value = ConversationWorkState.Success(conversationWork)
                },
                onFailure = { error: Throwable ->
                    val errorMessage = error.message ?: "Unknown error"
                    _conversationWorkState.value = ConversationWorkState.Error(errorMessage)
                }
            )
        }
    }

    sealed class WorksState {
        object Idle : WorksState()
        object Loading : WorksState()
        data class Success(
            val works: List<Work>,
            val hasMoreWorks: Boolean = false,
            val isLoadingMore: Boolean = false,
            val totalCount: Int = 0
        ) : WorksState()
        data class Error(val message: String) : WorksState()
    }

    sealed class WorkDetailState {
        object Idle : WorkDetailState()
        object Loading : WorkDetailState()
        data class Success(val work: Work) : WorkDetailState()
        data class Error(val message: String) : WorkDetailState()
    }

    sealed class TasksState {
        object Idle : TasksState()
        object Loading : TasksState()
        data class Success(
            val tasks: List<Task>,
            val hasMoreTasks: Boolean = false,
            val isLoadingMore: Boolean = false,
            val totalCount: Int = 0
        ) : TasksState()
        data class Error(val message: String) : TasksState()
    }

    sealed class VersionsState {
        object Idle : VersionsState()
        object Loading : VersionsState()
        data class Success(
            val versions: List<Version>,
            val hasMoreVersions: Boolean = false,
            val isLoadingMore: Boolean = false,
            val totalCount: Int = 0
        ) : VersionsState()
        data class Error(val message: String) : VersionsState()
    }

    sealed class EventsState {
        object Idle : EventsState()
        object Loading : EventsState()
        data class Success(val events: List<Event>) : EventsState()
        data class Error(val message: String) : EventsState()
    }

    sealed class WorkMessagesState {
        object Idle : WorkMessagesState()
        object Loading : WorkMessagesState()
        data class Success(
            val messages: List<ConversationMessage>,
            val hasMoreMessages: Boolean = false,
            val isLoadingMore: Boolean = false
        ) : WorkMessagesState()
        data class Error(val message: String) : WorkMessagesState()
    }

    sealed class ConversationWorkState {
        object Idle : ConversationWorkState()
        object Loading : ConversationWorkState()
        data class Success(val conversationWork: ConversationWorkResponse?) : ConversationWorkState()
        data class Error(val message: String) : ConversationWorkState()
    }
}
