@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.work.model

import com.finalworksystem.data.user.model.User
import com.finalworksystem.data.user.model.toDomainModel
import com.finalworksystem.domain.work.model.Work as DomainWork
import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class Work(
    val id: Int = 0,
    val title: String = "",
    val shortcut: String? = null,
    val type: WorkType,
    val status: WorkStatus,
    val deadline: String = "",
    val deadlineProgram: String? = null,
    val author: User? = null,
    val supervisor: User? = null,
    val opponent: User? = null,
    val consultant: User? = null
)

fun Work.toDomainModel(): DomainWork {
    return DomainWork(
        id = id,
        title = title,
        shortcut = shortcut,
        type = type.toDomainModel(),
        status = status.toDomainModel(),
        deadline = deadline,
        deadlineProgram = deadlineProgram,
        author = author?.toDomainModel(),
        supervisor = supervisor?.toDomainModel(),
        opponent = opponent?.toDomainModel(),
        consultant = consultant?.toDomainModel()
    )
}
