package com.finalworksystem.data.event.model

import com.finalworksystem.domain.event.model.Participant as DomainParticipant

data class Participant(
    val id: Int?,
    val firstName: String?,
    val secondName: String?,
    val email: String?,
    val user: User,
    val work: Work?
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
