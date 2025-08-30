package com.finalworksystem.data.version.model.response

import com.finalworksystem.data.user.model.User
import com.finalworksystem.data.version.model.Version
import com.finalworksystem.data.version.model.toVersionType
import com.finalworksystem.data.work.model.response.TypeResponse
import com.finalworksystem.data.work.model.response.UserResponse
import com.finalworksystem.data.work.model.response.WorkResponse
import com.finalworksystem.data.work.model.response.toDomainModel
import com.google.gson.annotations.SerializedName

data class VersionResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("type")
    val type: TypeResponse,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("mediaName")
    val mediaName: String,

    @SerializedName("mediaSize")
    val mediaSize: Int,

    @SerializedName("originalExtension")
    val originalExtension: String?,

    @SerializedName("owner")
    val owner: UserResponse,

    @SerializedName("work")
    val work: WorkResponse
)

fun VersionResponse.toDataModel(): Version {
    return Version(
        id = id,
        type = type.toVersionType(),
        name = name,
        description = description,
        mediaName = mediaName,
        mediaSize = mediaSize,
        originalExtension = originalExtension,
        owner = User(
            id = owner.id,
            username = owner.username,
            firstname = owner.firstname,
            lastname = owner.lastname,
            fullName = owner.fullName,
            degreeBefore = owner.degreeBefore,
            degreeAfter = owner.degreeAfter,
            email = owner.email,
            token = owner.token ?: "",
            roles = when (owner.roles) {
                is Map<*, *> -> (owner.roles as Map<*, *>).values.map { it.toString() }
                is List<*> -> (owner.roles as List<*>).map { it.toString() }
                else -> emptyList()
            }
        ),
        work = work.toDomainModel()
    )
}
