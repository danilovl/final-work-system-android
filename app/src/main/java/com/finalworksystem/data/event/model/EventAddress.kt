package com.finalworksystem.data.event.model

import com.finalworksystem.domain.event.model.EventAddress as DomainEventAddress

data class EventAddress(
    val id: Int,
    val name: String?,
    val description: String?,
    val street: String?,
    val skype: Boolean,
    val latitude: Double?,
    val longitude: Double?,
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
