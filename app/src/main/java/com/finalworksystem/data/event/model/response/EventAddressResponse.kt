@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.event.model.response

import com.finalworksystem.data.event.model.EventAddress
import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class EventAddressResponse(
    @SerialName("id")
    val id: Int = 0,

    @SerialName("name")
    val name: String? = null,

    @SerialName("description")
    val description: String? = null,

    @SerialName("street")
    val street: String? = null,

    @SerialName("skype")
    val skype: Boolean = false,

    @SerialName("latitude")
    val latitude: Double? = null,

    @SerialName("longitude")
    val longitude: Double? = null,

    @SerialName("owner")
    val owner: UserResponse = UserResponse()
)

fun EventAddressResponse.toDataModel(): EventAddress {
    return EventAddress(
        id = id,
        name = name,
        description = description,
        street = street,
        skype = skype,
        latitude = latitude,
        longitude = longitude,
        owner = owner.toDataModel()
    )
}
