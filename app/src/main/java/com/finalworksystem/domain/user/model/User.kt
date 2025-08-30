package com.finalworksystem.domain.user.model

import com.finalworksystem.data.user.model.User as DataUser

data class User(
    val id: Int,
    val username: String,
    val firstname: String,
    val lastname: String,
    val fullName: String?,
    val degreeBefore: String?,
    val degreeAfter: String?,
    val email: String,
    val token: String?,
    val roles: List<String>
) {
    fun toData(): DataUser {
        return DataUser(
            id = id,
            username = username,
            firstname = firstname,
            lastname = lastname,
            fullName = fullName,
            degreeBefore = degreeBefore,
            degreeAfter = degreeAfter,
            email = email,
            token = token ?: "",
            roles = roles
        )
    }
}
