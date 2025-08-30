package com.finalworksystem.data.conversation.model.response

import com.google.gson.annotations.SerializedName

data class ConversationListResponse(
    @SerializedName("numItemsPerPage")
    val numItemsPerPage: Int,

    @SerializedName("totalCount")
    val totalCount: Int,

    @SerializedName("currentItemCount")
    val currentItemCount: Int,

    @SerializedName("result")
    val result: List<ConversationResponse>
)
