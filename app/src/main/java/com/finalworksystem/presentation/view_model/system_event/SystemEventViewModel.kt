package com.finalworksystem.presentation.view_model.system_event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.filter
import com.finalworksystem.application.use_case.system_event.GetSystemEventsWithPaginationUseCase
import com.finalworksystem.application.use_case.system_event.MarkAllSystemEventsAsViewedUseCase
import com.finalworksystem.application.use_case.system_event.MarkSystemEventAsViewedUseCase
import com.finalworksystem.domain.system_event.model.SystemEvent
import com.finalworksystem.domain.system_event.model.SystemEventTypeEnum
import com.finalworksystem.infrastructure.popup.PopupMessageService
import com.finalworksystem.presentation.paging.Page
import com.finalworksystem.presentation.paging.PagedList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SystemEventViewModel(
    private val getSystemEventsWithPaginationUseCase: GetSystemEventsWithPaginationUseCase,
    private val markSystemEventAsViewedUseCase: MarkSystemEventAsViewedUseCase,
    private val markAllSystemEventsAsViewedUseCase: MarkAllSystemEventsAsViewedUseCase,
    private val popupMessageService: PopupMessageService
) : ViewModel() {

    private val pagedEvents = PagedList<SystemEventTypeEnum, SystemEvent>(viewModelScope, PAGE_SIZE) { type, page, pageSize ->
        getSystemEventsWithPaginationUseCase(type, page, pageSize).first()
            .map { (events, _, totalCount) -> Page(events, totalCount) }
    }

    /** Events marked as viewed since the last load; hidden without reloading the list. */
    private val viewedEventIds = MutableStateFlow<Set<Int>>(emptySet())
    private val allViewed = MutableStateFlow(false)

    val events: Flow<PagingData<SystemEvent>> =
        combine(pagedEvents.items, viewedEventIds, allViewed) { data, viewedIds, all ->
            when {
                all -> data.filter { false }
                viewedIds.isEmpty() -> data
                else -> data.filter { it.id !in viewedIds }
            }
        }

    val totalCount: StateFlow<Int> = pagedEvents.totalCount

    /** Shows the events of the type; an already shown list is reloaded, events change often. */
    fun loadSystemEvents(type: SystemEventTypeEnum = SystemEventTypeEnum.UNREAD) {
        clearLocalChanges()
        pagedEvents.reload(type)
    }

    fun refresh() {
        clearLocalChanges()
        pagedEvents.refresh()
    }

    fun changeViewed(eventId: Int) {
        viewModelScope.launch {
            markSystemEventAsViewedUseCase(eventId).collect { result ->
                result.fold(
                    onSuccess = {
                        viewedEventIds.update { it + eventId }
                        pagedEvents.adjustTotalCount(-1)
                    },
                    onFailure = { error ->
                        val errorMessage = error.message ?: "Unknown error"
                        popupMessageService.showMessage(errorMessage, PopupMessageService.MessageLevel.ERROR)
                    }
                )
            }
        }
    }

    fun changeAllViewed() {
        viewModelScope.launch {
            markAllSystemEventsAsViewedUseCase().collect { result ->
                result.fold(
                    onSuccess = {
                        allViewed.value = true
                        pagedEvents.adjustTotalCount(-pagedEvents.totalCount.value)
                    },
                    onFailure = { error ->
                        val errorMessage = error.message ?: "Unknown error"
                        popupMessageService.showMessage(errorMessage, PopupMessageService.MessageLevel.ERROR)
                    }
                )
            }
        }
    }

    private fun clearLocalChanges() {
        viewedEventIds.value = emptySet()
        allViewed.value = false
    }

    private companion object {
        const val PAGE_SIZE = 20
    }
}
