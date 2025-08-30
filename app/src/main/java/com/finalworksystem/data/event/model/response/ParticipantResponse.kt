package com.finalworksystem.data.event.model.response

import com.finalworksystem.data.event.model.Participant
import com.google.gson.annotations.SerializedName

data class ParticipantResponse(
    @SerializedName("id")
    val id: Int?,

    @SerializedName("firstName")
    val firstName: String?,

    @SerializedName("secondName")
    val secondName: String?,

    @SerializedName("email")
    val email: String?,

    @SerializedName("user")
    val user: UserResponse,

    @SerializedName("work")
    val work: WorkResponse?
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
