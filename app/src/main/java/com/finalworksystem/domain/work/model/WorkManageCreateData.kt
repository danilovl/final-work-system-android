package com.finalworksystem.domain.work.model

data class WorkManageCreateData(
    val types: List<WorkType>,
    val statuses: List<WorkStatus>,
    val categories: List<WorkCategory>
)
