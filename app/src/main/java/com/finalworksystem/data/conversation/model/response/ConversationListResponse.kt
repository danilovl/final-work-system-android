@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.conversation.model.response

import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class ConversationListResponse(
    @SerialName("numItemsPerPage")
    val numItemsPerPage: Int = 0,

    @SerialName("totalCount")
    val totalCount: Int = 0,

    @SerialName("currentItemCount")
    val currentItemCount: Int = 0,

    @SerialName("result")
    val result: List<ConversationResponse> = emptyList()
)
