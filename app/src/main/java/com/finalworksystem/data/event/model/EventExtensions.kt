package com.finalworksystem.data.event.model

import com.finalworksystem.data.event.model.response.EventListResponse
import com.finalworksystem.data.event.model.response.toDataModel
import com.finalworksystem.domain.event.model.PaginatedEvents as DomainPaginatedEvents

fun EventListResponse.toDataModel(): List<Event> {
    return result.map { it.toDataModel() }
}

fun EventListResponse.toDomainModel(): DomainPaginatedEvents {
    val events = result.map { it.toDataModel().toDomainModel() }
    return DomainPaginatedEvents(
        events = events,
        count = count,
        totalCount = totalCount,
        success = success
    )
}
