package com.finalworksystem.domain.event.model

import com.finalworksystem.domain.user.model.User
import com.finalworksystem.domain.work.model.Work

data class Participant(
    val id: Int?,
    val firstName: String?,
    val secondName: String?,
    val email: String?,
    val user: User,
    val work: Work?
)
