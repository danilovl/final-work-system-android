package com.finalworksystem.presentation.ui.event_calendar.component

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import com.finalworksystem.ui.theme.Blue40
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun parseColor(colorString: String?): Color {
    if (colorString.isNullOrBlank()) return Blue40

    val cleanColor = colorString.trim()

    return try {
        when {
            cleanColor.startsWith("#") -> {
                val hex = cleanColor.substring(1)
                Color(parseHexColor(hex))
            }
            cleanColor.startsWith("rgb(") && cleanColor.endsWith(")") -> {
                val values = cleanColor.substringAfter("rgb(").substringBefore(")")
                    .split(",")
                    .map { it.trim().toInt().coerceIn(0, 255) }

                if (values.size == 3) {
                    Color(values[0], values[1], values[2])
                } else {
                    Blue40
                }
            }
            else -> {
                Color(parseHexColor(cleanColor))
            }
        }
    } catch (_: Exception) {
        Blue40
    }
}

private fun parseHexColor(hex: String): Int {
    val normalizedHex = when (hex.length) {
        3 -> hex.map { "$it$it" }.joinToString("")
        6, 8 -> hex
        else -> throw IllegalArgumentException("Invalid hex color length")
    }
    return "#$normalizedHex".toColorInt()
}

fun formatTime(dateTimeString: String): String {
    return try {
        val timePart: String

        if (dateTimeString.contains("T")) {
            timePart = dateTimeString.split("T")[1]
        } else if (dateTimeString.contains(" ")) {
            timePart = dateTimeString.split(" ")[1]
        } else {
            return dateTimeString
        }

        timePart.split(":").take(2).joinToString(":")
    } catch (_: Exception) {
        dateTimeString
    }
}

fun formatDate(dateTimeString: String): String {
    return try {
        val datePart = when {
            dateTimeString.contains("T") -> dateTimeString.substringBefore("T")
            dateTimeString.contains(" ") -> dateTimeString.substringBefore(" ")
            else -> dateTimeString
        }

        val parsedDate = LocalDate.parse(datePart)
        "${parsedDate.year}-${parsedDate.monthValue}-${parsedDate.dayOfMonth}"
    } catch (e: DateTimeParseException) {
        dateTimeString
    }
}

fun isSameDay(startDateTime: String, endDateTime: String): Boolean {
    val formatters = listOf(
        DateTimeFormatter.ISO_OFFSET_DATE_TIME,
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    )

    fun parseToDate(dateTimeStr: String): LocalDate? {
        for (formatter in formatters) {
            try {
                return try {
                    OffsetDateTime.parse(dateTimeStr, formatter).toLocalDate()
                } catch (_: Exception) {
                    LocalDateTime.parse(dateTimeStr, formatter).toLocalDate()
                }
            } catch (_: Exception) { }
        }
        return null
    }

    val startDate = parseToDate(startDateTime)
    val endDate = parseToDate(endDateTime)

    return startDate != null && endDate != null && startDate == endDate
}

fun formatDateTime(dateTimeString: String): String {
    return try {
        val formatterInput = DateTimeFormatter.ofPattern("[yyyy-MM-dd'T'HH:mm:ss][yyyy-MM-dd HH:mm:ss]")
        val formatterOutput = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

        val dateTime = LocalDateTime.parse(dateTimeString, formatterInput)
        dateTime.format(formatterOutput)
    } catch (e: DateTimeParseException) {
        dateTimeString
    }
}
