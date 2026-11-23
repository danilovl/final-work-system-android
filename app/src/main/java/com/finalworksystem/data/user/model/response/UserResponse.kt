@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.user.model.response

import com.finalworksystem.data.user.model.User
import com.finalworksystem.data.work.model.response.WorkResponse
import com.finalworksystem.data.work.model.response.toDomainModel
import com.finalworksystem.domain.user.model.User as DomainUser
import com.finalworksystem.domain.user.model.UserListResult
import com.finalworksystem.domain.user.model.UserWithWorks
import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonTransformingSerializer

@Serializable
data class UserDetailResponse(
    @SerialName("id")
    val id: Int? = null,

    @SerialName("username")
    val username: String? = null,

    @SerialName("firstname")
    val firstname: String? = null,

    @SerialName("lastname")
    val lastname: String? = null,

    @SerialName("fullName")
    val fullName: String? = null,

    @SerialName("degreeBefore")
    val degreeBefore: String? = null,

    @SerialName("degreeAfter")
    val degreeAfter: String? = null,

    @SerialName("email")
    val email: String? = null,

    @SerialName("token")
    val token: String? = null,

    @SerialName("roles")
    val roles: List<String>? = null
)

@Serializable
data class UserListResponse(
    @SerialName("numItemsPerPage")
    val numItemsPerPage: Int? = null,

    @SerialName("totalCount")
    val totalCount: Int? = null,

    @SerialName("currentItemCount")
    val currentItemCount: Int? = null,

    @SerialName("result")
    val result: List<UserWithWorksResponse>? = null
)

@OptIn(ExperimentalSerializationApi::class)
@KeepGeneratedSerializer
@Serializable(with = UserWithWorksResponseSerializer::class)
data class UserWithWorksResponse(
    @SerialName("user")
    val user: UserResponse? = null,

    @SerialName("works")
    val works: List<WorkResponse>? = null
)

/**
 * The user list contains either `{"user": {...}, "works": [...]}`
 * or a bare user object; the latter is normalized to `{"user": {...}}`.
 */
object UserWithWorksResponseSerializer : JsonTransformingSerializer<UserWithWorksResponse>(
    UserWithWorksResponse.generatedSerializer()
) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        if (element !is JsonObject) return JsonObject(emptyMap())
        if (element.containsKey("user")) return element
        return JsonObject(mapOf("user" to element))
    }
}

@Serializable
data class UserResponse(
    @SerialName("id")
    val id: Int? = null,

    @SerialName("username")
    val username: String? = null,

    @SerialName("firstname")
    val firstname: String? = null,

    @SerialName("lastname")
    val lastname: String? = null,

    @SerialName("fullName")
    val fullName: String? = null,

    @SerialName("email")
    val email: String? = null,

    @SerialName("degreeBefore")
    val degreeBefore: String? = null,

    @SerialName("degreeAfter")
    val degreeAfter: String? = null,

    @SerialName("roles")
    val roles: List<String>? = null
)

fun UserDetailResponse?.toDataModel(): User {
    if (this == null) {
        return User(
            id = 0,
            username = "",
            firstname = "",
            lastname = "",
            fullName = null,
            degreeBefore = null,
            degreeAfter = null,
            email = "",
            token = "",
            roles = emptyList()
        )
    }
    val fn = firstname ?: ""
    val ln = lastname ?: ""

    return User(
        id = id ?: 0,
        username = username ?: "",
        firstname = fn,
        lastname = ln,
        fullName = fullName ?: "$fn $ln".trim(),
        degreeBefore = degreeBefore,
        degreeAfter = degreeAfter,
        email = email ?: "",
        token = token ?: "",
        roles = roles ?: emptyList()
    )
}

fun UserDetailResponse?.toDomain(): DomainUser {
    if (this == null) {
        return DomainUser(
            id = 0,
            username = "",
            firstname = "",
            lastname = "",
            fullName = null,
            degreeBefore = null,
            degreeAfter = null,
            email = "",
            token = null,
            roles = emptyList()
        )
    }
    val fn = firstname ?: ""
    val ln = lastname ?: ""

    return DomainUser(
        id = id ?: 0,
        username = username ?: "",
        firstname = fn,
        lastname = ln,
        fullName = fullName ?: "$fn $ln".trim(),
        degreeBefore = degreeBefore,
        degreeAfter = degreeAfter,
        email = email ?: "",
        token = token,
        roles = roles ?: emptyList()
    )
}

fun UserResponse?.toDataModel(): User? {
    if (this == null) {
        return null
    }

    val fn = firstname ?: ""
    val ln = lastname ?: ""
    val computedFullName = fullName ?: if (fn.isNotBlank() || ln.isNotBlank()) "$fn $ln".trim() else null

    return User(
        id = id ?: 0,
        username = username ?: "",
        firstname = fn,
        lastname = ln,
        fullName = computedFullName,
        degreeBefore = degreeBefore,
        degreeAfter = degreeAfter,
        email = email ?: "",
        token = "",
        roles = roles ?: emptyList()
    )
}

fun UserResponse?.toDomainModel(): DomainUser {
    if (this == null) {
        return DomainUser(
            id = 0,
            username = "",
            firstname = "",
            lastname = "",
            fullName = null,
            degreeBefore = null,
            degreeAfter = null,
            email = "",
            token = null,
            roles = emptyList()
        )
    }
    val fn = firstname ?: ""
    val ln = lastname ?: ""
    val computedFullName = fullName ?: if (fn.isNotBlank() || ln.isNotBlank()) "$fn $ln".trim() else null

    return DomainUser(
        id = id ?: 0,
        username = username ?: "",
        firstname = fn,
        lastname = ln,
        fullName = computedFullName,
        degreeBefore = degreeBefore,
        degreeAfter = degreeAfter,
        email = email ?: "",
        token = null,
        roles = roles ?: emptyList()
    )
}

fun UserWithWorksResponse?.toDomainModel(): UserWithWorks {
    val userDomain = this?.user.toDomainModel()
    val worksDomain = this?.works?.mapNotNull { it.toDomainModel() } ?: emptyList()

    return UserWithWorks(
        user = userDomain,
        works = worksDomain
    )
}

fun UserListResponse?.toDomainModel(): UserListResult {
    if (this == null) {
        return UserListResult(
            numItemsPerPage = 0,
            totalCount = 0,
            currentItemCount = 0,
            result = emptyList()
        )
    }

    return UserListResult(
        numItemsPerPage = numItemsPerPage ?: 0,
        totalCount = totalCount ?: 0,
        currentItemCount = currentItemCount ?: 0,
        result = result?.mapNotNull { it.toDomainModel() } ?: emptyList()
    )
}
