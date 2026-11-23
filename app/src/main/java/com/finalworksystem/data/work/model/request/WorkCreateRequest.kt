@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.work.model.request

import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class WorkCreateRequest(
    @SerialName("title")
    val title: String,

    @SerialName("shortcut")
    val shortcut: String? = null,

    @SerialName("typeId")
    val typeId: Int,

    @SerialName("statusId")
    val statusId: Int,

    @SerialName("categoriesIds")
    val categoriesIds: List<Int>? = null,

    @SerialName("deadline")
    val deadline: String,

    @SerialName("deadlineProgram")
    val deadlineProgram: String? = null,

    @SerialName("authorId")
    val authorId: Int? = null,

    @SerialName("supervisorId")
    val supervisorId: Int? = null,

    @SerialName("opponentId")
    val opponentId: Int? = null,

    @SerialName("consultantId")
    val consultantId: Int? = null
)
