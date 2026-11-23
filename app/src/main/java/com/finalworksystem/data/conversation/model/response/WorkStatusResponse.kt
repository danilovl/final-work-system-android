@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.conversation.model.response

import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class WorkStatusDto(
    @SerialName("id")
    val id: Int = 0,
    
    @SerialName("name")
    val name: String = "",
    
    @SerialName("description")
    val description: String? = null,
    
    @SerialName("color")
    val color: String = ""
)

fun WorkStatusDto.toDomain(): com.finalworksystem.domain.conversation.model.WorkStatus {
    return com.finalworksystem.domain.conversation.model.WorkStatus(
        id = id,
        name = name,
        description = description,
        color = color
    )
}
