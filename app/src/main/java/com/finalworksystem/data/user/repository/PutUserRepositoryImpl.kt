package com.finalworksystem.data.user.repository

import com.finalworksystem.data.user.model.request.ChangePasswordRequest
import com.finalworksystem.data.util.UnauthorizedException
import com.finalworksystem.domain.common.util.safeFlowResult
import com.finalworksystem.domain.user.model.PasswordChangeException
import com.finalworksystem.domain.user.model.PasswordChangeException.Reason
import com.finalworksystem.domain.user.repository.PutUserRepository
import com.finalworksystem.infrastructure.api.ApiUserService
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

class PutUserRepositoryImpl(private val apiUserService: ApiUserService) : PutUserRepository {

    override fun changePassword(currentPassword: String, newPassword: String): Flow<Result<Unit>> = safeFlowResult {
        val response = apiUserService.changePassword(ChangePasswordRequest(currentPassword, newPassword))
        if (response.isSuccessful) {
            return@safeFlowResult Result.success(Unit)
        }

        val (code, message) = parseError(response.errorBody()?.string())
        val error = when {
            response.code() == 401 -> UnauthorizedException("Invalid credentials")
            response.code() == 404 || response.code() == 405 -> PasswordChangeException(Reason.NOT_SUPPORTED, message)
            code == "invalid_current_password" -> PasswordChangeException(Reason.INVALID_CURRENT_PASSWORD, message)
            code == "invalid_new_password" || response.code() == 422 -> PasswordChangeException(Reason.INVALID_NEW_PASSWORD, message)
            // Other 400 codes (missing_field, invalid_request, ...) carry their own explanation.
            else -> PasswordChangeException(Reason.OTHER, message)
        }
        Result.failure(error)
    }

    /** Reads `{"code": "...", "message": "..."}` from an error body; both are optional. */
    private fun parseError(body: String?): Pair<String?, String?> {
        if (body.isNullOrBlank()) return null to null

        return try {
            val json = Json.parseToJsonElement(body) as? JsonObject ?: return null to null
            val code = (json["code"] as? JsonPrimitive)?.takeIf { it.isString }?.content
            val message = (json["message"] as? JsonPrimitive)?.takeIf { it.isString }?.content
            code to message?.takeIf { it.isNotBlank() }
        } catch (_: Exception) {
            null to null
        }
    }
}
