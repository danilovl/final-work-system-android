package com.finalworksystem.domain.work.model

data class WorkCategory(
    val id: Int,
    val name: String,
    val description: String?,
    val sorting: Int
)
