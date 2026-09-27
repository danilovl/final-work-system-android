package com.finalworksystem.data.conversation.model.response

import com.finalworksystem.data.conversation.model.ConversationParticipantDto
import com.finalworksystem.data.conversation.model.ConversationTypeDto
import com.finalworksystem.data.conversation.model.ConversationUserDto
import com.finalworksystem.data.conversation.model.ConversationWorkDetailDto
import com.finalworksystem.data.conversation.model.LastMessageDto
import com.finalworksystem.data.conversation.model.toEntity
import com.finalworksystem.data.conversation.model.toEntityList
import com.finalworksystem.domain.conversation.model.response.ConversationWorkResponse
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.JsonAdapter
import java.lang.reflect.Type

private data class ConversationWorkResponseDelegateDto(
    val id: Int,
    val name: String?,
    val isRead: Boolean,
    val recipient: ConversationUserDto?,
    val work: ConversationWorkDetailDto?,
    val participants: List<ConversationParticipantDto>?,
    val lastMessage: LastMessageDto?,
    val type: ConversationTypeDto?
)

class ConversationWorkResponseDtoAdapter : JsonDeserializer<ConversationWorkResponseDto> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): ConversationWorkResponseDto? {
        if (json == null || !json.isJsonObject) {
            return null
        }
        val jsonObject = json.asJsonObject
        val targetObject = when {
            jsonObject.has("result") && jsonObject.get("result").isJsonObject -> jsonObject.getAsJsonObject("result")
            jsonObject.has("work") && jsonObject.get("work").isJsonObject -> jsonObject.getAsJsonObject("work")
            jsonObject.has("data") && jsonObject.get("data").isJsonObject -> jsonObject.getAsJsonObject("data")
            else -> jsonObject
        }
        val dto = context?.deserialize<ConversationWorkResponseDelegateDto>(
            targetObject,
            ConversationWorkResponseDelegateDto::class.java
        ) ?: return null

        return ConversationWorkResponseDto(
            id = dto.id,
            name = dto.name,
            isRead = dto.isRead,
            recipient = dto.recipient,
            work = dto.work,
            participants = dto.participants ?: emptyList(),
            lastMessage = dto.lastMessage,
            type = dto.type ?: ConversationTypeDto(0, "", "")
        )
    }
}

@JsonAdapter(ConversationWorkResponseDtoAdapter::class)
data class ConversationWorkResponseDto(
    val id: Int,
    val name: String?,
    val isRead: Boolean,
    val recipient: ConversationUserDto?,
    val work: ConversationWorkDetailDto?,
    val participants: List<ConversationParticipantDto>,
    val lastMessage: LastMessageDto?,
    val type: ConversationTypeDto
)

fun ConversationWorkResponseDto.toEntity(): ConversationWorkResponse {
    return ConversationWorkResponse(
        id = id,
        name = name,
        isRead = isRead,
        recipient = recipient?.toEntity(),
        work = work?.toEntity(),
        participants = participants.toEntityList(),
        lastMessage = lastMessage?.toEntity(),
        type = type.toEntity()
    )
}
