package com.finalworksystem.data.task.model.request

import com.google.gson.annotations.SerializedName

data class TaskCreateRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("deadline")
    val deadline: String? = null,

    @SerializedName("workId")
    val workId: Int
)
