package com.finalworksystem.data.user.model.response

import com.finalworksystem.data.user.model.User
import com.finalworksystem.data.work.model.response.WorkResponse
import com.finalworksystem.data.work.model.response.toDomainModel
import com.finalworksystem.domain.user.model.UserListResult
import com.finalworksystem.domain.user.model.UserWithWorks
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import com.finalworksystem.domain.user.model.User as DomainUser

data class UserDetailResponse(
    @SerializedName("id")
    val id: Int?,

    @SerializedName("username")
    val username: String?,

    @SerializedName("firstname")
    val firstname: String?,

    @SerializedName("lastname")
    val lastname: String?,

    @SerializedName("fullName")
    val fullName: String?,

    @SerializedName("degreeBefore")
    val degreeBefore: String?,

    @SerializedName("degreeAfter")
    val degreeAfter: String?,

    @SerializedName("email")
    val email: String?,

    @SerializedName("token")
    val token: String?,

    @SerializedName("roles")
    val roles: List<String>?
)

data class UserListResponse(
    @SerializedName("numItemsPerPage")
    val numItemsPerPage: Int?,

    @SerializedName("totalCount")
    val totalCount: Int?,

    @SerializedName("currentItemCount")
    val currentItemCount: Int?,

    @SerializedName("result")
    val result: List<UserWithWorksResponse>?
)

class UserWithWorksResponseAdapter : JsonDeserializer<UserWithWorksResponse> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): UserWithWorksResponse {
        if (json == null || !json.isJsonObject) {
            return UserWithWorksResponse(user = null, works = emptyList())
        }
        val jsonObject = json.asJsonObject
        return if (jsonObject.has("user")) {
            val userElement = jsonObject.get("user")
            val user = if (userElement != null && !userElement.isJsonNull) {
                context?.deserialize<UserResponse>(userElement, UserResponse::class.java)
            } else {
                null
            }
            val worksElement = jsonObject.get("works")
            val works = if (worksElement != null && !worksElement.isJsonNull) {
                context?.deserialize<List<WorkResponse>>(
                    worksElement,
                    object : TypeToken<List<WorkResponse>>() {}.type
                )
            } else {
                emptyList()
            }
            UserWithWorksResponse(user = user, works = works ?: emptyList())
        } else {
            val user = context?.deserialize<UserResponse>(jsonObject, UserResponse::class.java)
            UserWithWorksResponse(user = user, works = emptyList())
        }
    }
}

@JsonAdapter(UserWithWorksResponseAdapter::class)
data class UserWithWorksResponse(
    @SerializedName("user")
    val user: UserResponse?,

    @SerializedName("works")
    val works: List<WorkResponse>?
)

data class UserResponse(
    @SerializedName("id")
    val id: Int?,

    @SerializedName("username")
    val username: String?,

    @SerializedName("firstname")
    val firstname: String?,

    @SerializedName("lastname")
    val lastname: String?,

    @SerializedName("fullName")
    val fullName: String?,

    @SerializedName("email")
    val email: String?,

    @SerializedName("degreeBefore")
    val degreeBefore: String?,

    @SerializedName("degreeAfter")
    val degreeAfter: String?,

    @SerializedName("roles")
    val roles: List<String>?
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
