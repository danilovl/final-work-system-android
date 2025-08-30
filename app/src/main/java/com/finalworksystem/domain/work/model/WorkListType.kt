package com.finalworksystem.domain.work.model

enum class WorkListType(val value: String) {
    AUTHOR("author"),
    OPPONENT("opponent"),
    CONSULTANT("consultant"),
    SUPERVISOR("supervisor");

    override fun toString(): String {
        return value
    }

    companion object {
        fun fromString(value: String): WorkListType {
            return values().find { it.value == value } ?: throw IllegalArgumentException("Unknown work list type: $value")
        }
    }
}
