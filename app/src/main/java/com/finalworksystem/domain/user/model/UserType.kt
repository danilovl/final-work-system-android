package com.finalworksystem.domain.user.model

enum class UserType(val value: String, val displayName: String) {
    AUTHOR("author", "Author"),
    OPPONENT("opponent", "Opponent"),
    CONSULTANT("consultant", "Consultant"),
    SUPERVISOR("supervisor", "Supervisor");

    companion object {
        fun fromString(value: String): UserType? {
            return values().find { it.value == value }
        }

        fun getDisplayName(value: String): String {
            return fromString(value)?.displayName ?: value
        }
    }
}
