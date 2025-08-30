package com.finalworksystem.domain.event_calendar.repository

import kotlinx.coroutines.flow.Flow

interface DeleteEventCalendarRepository {
    fun deleteEvent(eventId: Int): Flow<Result<Unit>>
}