package com.finalworksystem.data.event.model.response

import com.finalworksystem.data.event.model.EventAddress
import com.google.gson.annotations.SerializedName

data class EventAddressResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("street")
    val street: String?,

    @SerializedName("skype")
    val skype: Boolean,

    @SerializedName("latitude")
    val latitude: Double?,

    @SerializedName("longitude")
    val longitude: Double?,

    @SerializedName("owner")
    val owner: UserResponse
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
