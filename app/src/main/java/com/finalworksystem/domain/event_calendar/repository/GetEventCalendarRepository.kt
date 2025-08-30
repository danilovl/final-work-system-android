package com.finalworksystem.domain.event_calendar.repository

import com.finalworksystem.domain.event_calendar.model.CalendarEvent
import com.finalworksystem.domain.event_calendar.model.EventCalendarManageCreateData
import com.finalworksystem.domain.work.model.Work
import kotlinx.coroutines.flow.Flow

interface GetEventCalendarRepository {
    fun getEventCalendar(type: String, start: String, end: String): Flow<Result<List<CalendarEvent>>>
    fun getEventCalendarUserWorks(): Flow<Result<List<Work>>>
    fun getEventCalendarManageCreateData(): Flow<Result<EventCalendarManageCreateData>>
}