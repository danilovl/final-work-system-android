package com.finalworksystem.domain.system_event.model

data class SystemEventType(
    val id: Int,
    val name: String,
    val description: String
)

enum class SystemEventTypeEnum(val value: String) {
    READ("read"),
    UNREAD("unread"),
    ALL("all")
}
