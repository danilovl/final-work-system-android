package com.finalworksystem.data.conversation.model.response

import com.google.gson.annotations.SerializedName

data class WorkStatusDto(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String?,
    
    @SerializedName("color")
    val color: String
)

fun WorkStatusDto.toDomain(): com.finalworksystem.domain.conversation.model.WorkStatus {
    return com.finalworksystem.domain.conversation.model.WorkStatus(
        id = id,
        name = name,
        description = description,
        color = color
    )
}
