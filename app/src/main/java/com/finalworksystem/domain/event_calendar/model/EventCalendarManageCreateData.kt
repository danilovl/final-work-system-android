package com.finalworksystem.domain.event_calendar.model

import com.finalworksystem.domain.event.model.EventAddress
import com.finalworksystem.domain.event.model.EventType
import com.finalworksystem.domain.event.model.Participant

data class EventCalendarManageCreateData(
    val types: List<EventType>,
    val addresses: List<EventAddress>,
    val participants: List<Participant>
)