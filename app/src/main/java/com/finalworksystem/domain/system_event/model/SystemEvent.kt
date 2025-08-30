package com.finalworksystem.domain.system_event.model

import com.finalworksystem.data.system_event.model.response.SystemEventListResponse
import com.finalworksystem.data.system_event.model.response.SystemEvent as DataSystemEvent

data class SystemEvent(
    val id: Int,
    val title: String,
    val owner: String,
    val createdAt: String,
    val isRead: Boolean = false,
    val viewed: Boolean = false
)

fun DataSystemEvent.toDomain(): SystemEvent {
    return SystemEvent(
        id = id,
        title = title,
        owner = owner,
        createdAt = createdAt,
        isRead = false,
        viewed = viewed
    )
}

fun SystemEventListResponse.toDomainList(): List<SystemEvent> {
    return result.map { it.toDomain() }
}
