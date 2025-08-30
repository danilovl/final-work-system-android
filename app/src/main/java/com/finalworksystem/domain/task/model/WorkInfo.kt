package com.finalworksystem.domain.task.model

import com.finalworksystem.data.task.model.WorkInfo as DataWorkInfo

data class WorkInfo(
    val id: Int,
    val title: String,
    val shortcut: String?,
    val deadline: String,
    val deadlineProgram: String?
)

fun WorkInfo.toDataModel(): DataWorkInfo {
    return DataWorkInfo(
        id = id,
        title = title,
        shortcut = shortcut,
        deadline = deadline,
        deadlineProgram = deadlineProgram
    )
}