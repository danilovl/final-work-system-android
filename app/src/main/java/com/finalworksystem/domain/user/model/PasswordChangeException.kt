package com.finalworksystem.domain.user.model

/** Failure of a password change, with a reason the UI can explain. */
class PasswordChangeException(
    val reason: Reason,
    /** Human readable explanation sent by the server, if any. */
    val serverMessage: String? = null
) : Exception(serverMessage ?: reason.name) {

    enum class Reason {
        /** The current password is wrong. */
        INVALID_CURRENT_PASSWORD,

        /** The new password does not meet the server rules; [message] says why when the server sent it. */
        INVALID_NEW_PASSWORD,

        /** The server does not provide the endpoint (yet). */
        NOT_SUPPORTED,

        OTHER
    }
}
