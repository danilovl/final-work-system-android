@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.version.model.response

import com.finalworksystem.data.user.model.User
import com.finalworksystem.data.version.model.Version
import com.finalworksystem.data.version.model.toVersionType
import com.finalworksystem.data.work.model.response.TypeResponse
import com.finalworksystem.data.work.model.response.UserResponse
import com.finalworksystem.data.work.model.response.WorkResponse
import com.finalworksystem.data.work.model.response.toDomainModel
import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class VersionResponse(
    @SerialName("id")
    val id: Int = 0,

    @SerialName("type")
    val type: TypeResponse = TypeResponse(),
    @SerialName("name")
    val name: String = "",

    @SerialName("description")
    val description: String? = null,

    @SerialName("mediaName")
    val mediaName: String = "",

    @SerialName("mediaSize")
    val mediaSize: Int = 0,

    @SerialName("originalExtension")
    val originalExtension: String? = null,

    @SerialName("owner")
    val owner: UserResponse = UserResponse(),
    @SerialName("work")
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
