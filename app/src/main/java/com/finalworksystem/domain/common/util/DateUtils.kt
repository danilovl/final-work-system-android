package com.finalworksystem.domain.common.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object DateUtils {

    fun formatDate(atomDate: String?, targetFormat: String = "yyyy-MM-dd"): String {
        if (atomDate.isNullOrBlank()) {
            return ""
        }

        val inputFormatters = listOf(
            DateTimeFormatter.ISO_OFFSET_DATE_TIME,
            DateTimeFormatter.ISO_DATE_TIME,
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
            DateTimeFormatter.ISO_DATE
        )

        val outputFormatter = DateTimeFormatter.ofPattern(targetFormat)

        for (formatter in inputFormatters) {
            try {
                return try {
                    val offsetDateTime = OffsetDateTime.parse(atomDate, formatter)
                    offsetDateTime.format(outputFormatter)
                } catch (_: Exception) {
                    val localDateTime = LocalDateTime.parse(atomDate, formatter)
                    localDateTime.format(outputFormatter)
                }
            } catch (_: DateTimeParseException) {
                // nothing
            }
        }

        try {
            val localDate = LocalDate.parse(atomDate)
            return localDate.format(outputFormatter)
        } catch (_: DateTimeParseException) {
            return atomDate
        }
    }

    fun formatToYmd(atomDate: String?): String {
        return formatDate(atomDate, "yyyy-MM-dd")
    }

    fun formatToReadable(atomDate: String?): String {
        return formatDate(atomDate, "dd.MM.yyyy")
    }

    fun formatToReadableDateTime(atomDate: String?): String {
        return formatDate(atomDate, "dd.MM.yyyy HH:mm")
    }
}
