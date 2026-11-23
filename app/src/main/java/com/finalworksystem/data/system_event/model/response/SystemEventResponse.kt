@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.system_event.model.response

import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class SystemEvent(
    @SerialName("id")
    val id: Int = 0,

    @SerialName("title")
    val title: String = "",

    @SerialName("owner")
    val owner: String = "",

    @SerialName("createdAt")
    val createdAt: String = "",

    @SerialName("viewed")
    val viewed: Boolean = false
)

@Serializable
data class SystemEventListResponse(
    @SerialName("count")
    val count: Int = 0,

    @SerialName("totalCount")
    val totalCount: Int = 0,

    @SerialName("success")
    val success: Boolean = false,

    @SerialName("result")
    val result: List<SystemEvent> = emptyList()
)

