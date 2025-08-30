package com.finalworksystem.domain.user.model

enum class UserRole(val value: String, val displayName: String) {
    STUDENT("ROLE_STUDENT", "Student"),
    SUPERVISOR("ROLE_SUPERVISOR", "Supervisor"),
    OPPONENT("ROLE_OPPONENT", "Opponent"),
    ADMIN("ROLE_ADMIN", "Admin"),
    USER("ROLE_USER", "User"),
    API("ROLE_API", "API");

    companion object {
        fun fromString(value: String?): UserRole? {
            return if (value.isNullOrBlank()) {
                null
            } else {
                try {
                    values().find { it.value == value }
                } catch (_: Exception) {
                    null
                }
            }
        }

        fun getDisplayName(value: String?): String {
            return if (value.isNullOrBlank()) {
                "Unknown Role"
            } else {
                try {
                    fromString(value)?.displayName ?: value
                } catch (_: Exception) {
                    value
                }
            }
        }
    }
}
