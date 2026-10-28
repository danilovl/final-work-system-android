package com.finalworksystem.domain.work.model

data class WorkFilter(
    val title: String? = null,
    val shortcut: String? = null,
    val statusIds: List<Int>? = null,
    val typeId: Int? = null,
    val authorId: Int? = null,
    val opponentId: Int? = null,
    val deadline: String? = null
) {
    fun isEmpty(): Boolean {
        return title.isNullOrBlank() &&
                shortcut.isNullOrBlank() &&
                statusIds.isNullOrEmpty() &&
                typeId == null &&
                authorId == null &&
                opponentId == null &&
                deadline.isNullOrBlank()
    }
}
