package com.finalworksystem.data.user.model.response

import com.finalworksystem.data.user.model.User
import com.finalworksystem.data.work.model.response.WorkResponse
import com.finalworksystem.data.work.model.response.toDomainModel
import com.finalworksystem.domain.user.model.UserListResult
import com.finalworksystem.domain.user.model.UserWithWorks
import com.google.gson.annotations.SerializedName
import com.finalworksystem.domain.user.model.User as DomainUser

data class UserDetailResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("username")
    val username: String,

    @SerializedName("firstname")
    val firstname: String,

    @SerializedName("lastname")
    val lastname: String,

    @SerializedName("fullName")
    val fullName: String?,

    @SerializedName("degreeBefore")
    val degreeBefore: String?,

    @SerializedName("degreeAfter")
    val degreeAfter: String?,

    @SerializedName("email")
    val email: String,

    @SerializedName("token")
    val token: String,

    @SerializedName("roles")
    val roles: List<String>?
)

data class UserListResponse(
    @SerializedName("numItemsPerPage")
    val numItemsPerPage: Int,

    @SerializedName("totalCount")
    val totalCount: Int,

    @SerializedName("currentItemCount")
    val currentItemCount: Int,

    @SerializedName("result")
    val result: List<UserWithWorksResponse>
)

data class UserWithWorksResponse(
    @SerializedName("user")
    val user: UserResponse,

    @SerializedName("works")
    val works: List<WorkResponse>
)

data class UserResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("username")
    val username: String,

    @SerializedName("firstname")
    val firstname: String,

    @SerializedName("lastname")
    val lastname: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("degreeBefore")
    val degreeBefore: String?,

    @SerializedName("degreeAfter")
    val degreeAfter: String?
)

fun UserDetailResponse.toDataModel(): User {
    return User(
        id = id,
        username = username,
        firstname = firstname,
        lastname = lastname,
        fullName = fullName ?: "$firstname $lastname",
        degreeBefore = degreeBefore,
        degreeAfter = degreeAfter,
        email = email,
        token = token,
        roles = roles ?: emptyList()
    )
}

fun UserDetailResponse.toDomain(): DomainUser {
    return DomainUser(
        id = id,
        username = username,
        firstname = firstname,
        lastname = lastname,
        fullName = fullName ?: "$firstname $lastname",
        degreeBefore = degreeBefore,
        degreeAfter = degreeAfter,
        email = email,
        token = token,
        roles = roles ?: emptyList()
    )
}

fun UserResponse.toDomainModel(): DomainUser {
    return DomainUser(
        id = id,
        username = username,
        firstname = firstname,
        lastname = lastname,
        fullName = null,
        degreeBefore = degreeBefore,
        degreeAfter = degreeAfter,
        email = email,
        token = null,
        roles = emptyList()
    )
}

fun UserWithWorksResponse.toDomainModel(): UserWithWorks {
    return UserWithWorks(
        user = user.toDomainModel(),
        works = works.map { it.toDomainModel() }
    )
}

fun UserListResponse.toDomainModel(): UserListResult {
    return UserListResult(
        numItemsPerPage = numItemsPerPage,
        totalCount = totalCount,
        currentItemCount = currentItemCount,
        result = result.map { it.toDomainModel() }
    )
}
