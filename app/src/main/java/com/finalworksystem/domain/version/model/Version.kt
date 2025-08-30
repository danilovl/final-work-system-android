package com.finalworksystem.domain.version.model

import com.finalworksystem.domain.user.model.User
import com.finalworksystem.domain.work.model.Work

data class Version(
    val id: Int,
    val type: VersionType,
    val name: String,
    val description: String?,
    val mediaName: String,
    val mediaSize: Int,
    val originalExtension: String?,
    val owner: User,
    val work: Work
)

data class VersionType(
    val id: Int,
    val name: String,
    val description: String?
)

data class PaginatedVersions(
    val versions: List<Version>,
    val totalCount: Int,
    val currentItemCount: Int,
    val numItemsPerPage: Int
)
