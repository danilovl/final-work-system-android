@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.event.model.response

import com.finalworksystem.data.event.model.Participant
import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class ParticipantResponse(
    @SerialName("id")
    val id: Int? = null,

    @SerialName("firstName")
    val firstName: String? = null,

    @SerialName("secondName")
    val secondName: String? = null,

    @SerialName("email")
    val email: String? = null,

    @SerialName("user")
    val user: UserResponse = UserResponse(),
    @SerialName("work")
    val work: WorkResponse? = null
)

fun ParticipantResponse.toDataModel(): Participant {
    return Participant(
        id = id,
        firstName = firstName,
        secondName = secondName,
        email = email,
        user = user.toDataModel(),
        work = work?.toDataModel()
    )
}
