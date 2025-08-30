package com.finalworksystem.data.event.model

import com.finalworksystem.domain.user.model.User as DomainUser

data class User(
    val id: Int,
    val username: String,
    val firstname: String,
    val lastname: String,
    val degreeBefore: String?,
    val degreeAfter: String?,
    val email: String,
    val roles: List<String>
)

fun User.toDomainModel(): DomainUser {
    return DomainUser(
        id = id,
        username = username,
        firstname = firstname,
        lastname = lastname,
        fullName = "$firstname $lastname",
        degreeBefore = degreeBefore,
        degreeAfter = degreeAfter,
        email = email,
        token = null,
        roles = roles
    )
}
