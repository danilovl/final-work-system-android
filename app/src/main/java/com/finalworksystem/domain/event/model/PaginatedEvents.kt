package com.finalworksystem.domain.event.model

data class PaginatedEvents(
    val events: List<Event>,
    val count: Int,
    val totalCount: Int,
    val success: Boolean
)
