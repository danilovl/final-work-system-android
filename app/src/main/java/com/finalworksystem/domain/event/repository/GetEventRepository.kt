package com.finalworksystem.domain.event.repository

import com.finalworksystem.domain.event.model.Event
import kotlinx.coroutines.flow.Flow

interface GetEventRepository {
    fun getEventsForWork(workId: Int): Flow<Result<List<Event>>>
    fun getEvent(eventId: Int): Flow<Result<Event>>
}