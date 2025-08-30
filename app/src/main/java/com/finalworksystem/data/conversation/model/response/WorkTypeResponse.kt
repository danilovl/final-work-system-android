package com.finalworksystem.data.conversation.model.response

import com.google.gson.annotations.SerializedName

data class WorkTypeDto(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String?,
    
    @SerializedName("shortcut")
    val shortcut: String
)

fun WorkTypeDto.toDomain(): com.finalworksystem.domain.conversation.model.WorkType {
    return com.finalworksystem.domain.conversation.model.WorkType(
        id = id,
        name = name,
        description = description,
        shortcut = shortcut
    )
}
