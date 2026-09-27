package com.finalworksystem.data.work.model.request

import com.google.gson.annotations.SerializedName

data class WorkCreateRequest(
    @SerializedName("title")
    val title: String,

    @SerializedName("shortcut")
    val shortcut: String? = null,

    @SerializedName("typeId")
    val typeId: Int,

    @SerializedName("statusId")
    val statusId: Int,

    @SerializedName("categoriesIds")
    val categoriesIds: List<Int>? = null,

    @SerializedName("deadline")
    val deadline: String,

    @SerializedName("deadlineProgram")
    val deadlineProgram: String? = null,

    @SerializedName("authorId")
    val authorId: Int? = null,

    @SerializedName("supervisorId")
    val supervisorId: Int? = null,

    @SerializedName("opponentId")
    val opponentId: Int? = null,

    @SerializedName("consultantId")
    val consultantId: Int? = null
)
