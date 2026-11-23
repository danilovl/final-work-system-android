@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.conversation.model.response

import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class WorkTypeDto(
    @SerialName("id")
    val id: Int = 0,
    
    @SerialName("name")
    val name: String = "",
    
    @SerialName("description")
    val description: String? = null,
    
    @SerialName("shortcut")
    val shortcut: String = ""
)

fun WorkTypeDto.toDomain(): com.finalworksystem.domain.conversation.model.WorkType {
    return com.finalworksystem.domain.conversation.model.WorkType(
        id = id,
        name = name,
        description = description,
        shortcut = shortcut
    )
}
