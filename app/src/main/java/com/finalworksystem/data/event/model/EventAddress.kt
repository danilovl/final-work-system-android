@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.event.model

import com.finalworksystem.domain.event.model.EventAddress as DomainEventAddress
import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class EventAddress(
    val id: Int = 0,
    val name: String? = null,
    val description: String? = null,
    val street: String? = null,
    val skype: Boolean = false,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val owner: User
)


fun EventAddress.toDomainModel(): DomainEventAddress {
    return DomainEventAddress(
        id = id,
        name = name,
        description = description,
        street = street,
        skype = skype,
        latitude = latitude,
        longitude = longitude,
        owner = owner.toDomainModel()
    )
}
