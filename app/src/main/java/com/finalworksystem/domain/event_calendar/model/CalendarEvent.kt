package com.finalworksystem.domain.event_calendar.model

import com.finalworksystem.domain.event.model.Participant

data class CalendarEvent(
    val id: Int,
    val title: String,
    val color: String?,
    val start: String,
    val end: String,
    val isFree: Boolean? = null,
    val participant: Participant? = null,
    val hasParticipant: Boolean = false
)
