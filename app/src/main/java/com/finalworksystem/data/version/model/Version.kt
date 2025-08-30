package com.finalworksystem.data.version.model

import com.finalworksystem.data.user.model.toDomainModel
import com.finalworksystem.domain.version.model.Version as DomainVersion

data class Version(
    val id: Int,
    val type: VersionType,
    val name: String,
    val description: String?,
    val mediaName: String,
    val mediaSize: Int,
    val originalExtension: String?,
    val owner: com.finalworksystem.data.user.model.User,
    val work: com.finalworksystem.domain.work.model.Work
)

fun Version.toDomainModel(): DomainVersion {
    return DomainVersion(
        id = id,
        type = type.toDomainModel(),
        name = name,
        description = description,
        mediaName = mediaName,
        mediaSize = mediaSize,
        originalExtension = originalExtension,
        owner = owner.toDomainModel(),
        work = work
    )
}
