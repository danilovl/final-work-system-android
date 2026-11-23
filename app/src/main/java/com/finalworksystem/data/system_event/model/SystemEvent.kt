@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.system_event.model

import com.finalworksystem.data.system_event.model.response.SystemEvent
import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class SystemEventDto(
    val id: Int = 0,
    val title: String = "",
    val owner: String = "",
    val createdAt: String = "",
    val isRead: Boolean = false,
    val viewed: Boolean = false
)

fun SystemEventDto.toApiModel(): SystemEvent {
    return SystemEvent(
        id = id,
        title = title,
        owner = owner,
        createdAt = createdAt,
        viewed = viewed
    )
}

fun SystemEventDto.toEntity(): com.finalworksystem.domain.system_event.model.SystemEvent {
    return com.finalworksystem.domain.system_event.model.SystemEvent(
        id = id,
        title = title,
        owner = owner,
        createdAt = createdAt,
        isRead = isRead,
        viewed = viewed
    )
}

fun List<SystemEventDto>.toEntityList(): List<com.finalworksystem.domain.system_event.model.SystemEvent> {
    return map { it.toEntity() }
}
