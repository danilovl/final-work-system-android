@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.event.model

import com.finalworksystem.domain.event.model.Participant as DomainParticipant
import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class Participant(
    val id: Int? = null,
    val firstName: String? = null,
    val secondName: String? = null,
    val email: String? = null,
    val user: User,
    val work: Work? = null
)


fun Participant.toDomainModel(): DomainParticipant {
    return DomainParticipant(
        id = id,
        firstName = firstName,
        secondName = secondName,
        email = email,
        user = user.toDomainModel(),
        work = work?.toDomainModel()
    )
}
