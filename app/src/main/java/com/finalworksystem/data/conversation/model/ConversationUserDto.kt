package com.finalworksystem.data.conversation.model

data class ConversationUserDto(
    val id: Int,
    val username: String,
    val firstname: String,
    val lastname: String,
    val email: String,
    val degreeBefore: String?,
    val degreeAfter: String?
)

fun ConversationUserDto.toEntity(): com.finalworksystem.domain.user.model.User {
    return com.finalworksystem.domain.user.model.User(
        id = id,
        username = username,
        firstname = firstname,
        lastname = lastname,
        fullName = null,
        degreeBefore = degreeBefore,
        degreeAfter = degreeAfter,
        email = email,
        token = null,
        roles = emptyList()
    )
}
