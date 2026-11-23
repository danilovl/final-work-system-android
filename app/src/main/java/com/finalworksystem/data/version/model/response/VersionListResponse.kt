@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.version.model.response

import com.finalworksystem.data.version.model.Version
import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class VersionListResponse(
    @SerialName("count")
    val count: Int = 0,

    @SerialName("totalCount")
    val totalCount: Int = 0,

    @SerialName("success")
    val success: Boolean = false,

    @SerialName("result")
    val result: List<VersionResponse> = emptyList()
)

fun VersionListResponse.toDataModel(): List<Version> {
    return result.map { it.toDataModel() }
}