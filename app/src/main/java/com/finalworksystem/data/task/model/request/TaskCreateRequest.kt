@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.task.model.request

import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class TaskCreateRequest(
    @SerialName("name")
    val name: String,

    @SerialName("description")
    val description: String? = null,

    @SerialName("deadline")
    val deadline: String? = null,

    @SerialName("workId")
    val workId: Int
)
