package com.finalworksystem

import com.finalworksystem.presentation.ui.event_calendar.component.formatDate
import com.finalworksystem.presentation.ui.event_calendar.component.formatTime
import com.finalworksystem.presentation.ui.event_calendar.component.isSameDay
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CalendarUtilsTest {

    @Test
    fun testFormatTime() {
        println("[DEBUG_LOG] Testing formatTime function")

        val dateTime1 = "2025-05-30T08:30:00+02:00"
        val dateTime2 = "2025-05-30T13:00:00+02:00"

        val time1 = formatTime(dateTime1)
        val time2 = formatTime(dateTime2)

        assertEquals("08:30", time1)
        assertEquals("13:00", time2)

        println("[DEBUG_LOG] formatTime test passed: $dateTime1 -> $time1")
        println("[DEBUG_LOG] formatTime test passed: $dateTime2 -> $time2")
    }

    @Test
    fun testFormatDate() {
        println("[DEBUG_LOG] Testing formatDate function")

        val dateTime = "2025-05-30T08:30:00+02:00"
        val formattedDate = formatDate(dateTime)

        assertEquals("2025-5-30", formattedDate)

        println("[DEBUG_LOG] formatDate test passed: $dateTime -> $formattedDate")
    }

    @Test
    fun testIsSameDay_SameDay() {
        println("[DEBUG_LOG] Testing isSameDay function - same day")

        val start = "2025-05-30T08:30:00+02:00"
        val end = "2025-05-30T13:00:00+02:00"

        val result = isSameDay(start, end)

        assertTrue("Events on same day should return true", result)

        println("[DEBUG_LOG] isSameDay test passed for same day: $start and $end -> $result")
    }

    @Test
    fun testIsSameDay_DifferentDays() {
        println("[DEBUG_LOG] Testing isSameDay function - different days")

        val start = "2025-05-30T08:30:00+02:00"
        val end = "2025-05-31T13:00:00+02:00"

        val result = isSameDay(start, end)

        assertFalse("Events on different days should return false", result)

        println("[DEBUG_LOG] isSameDay test passed for different days: $start and $end -> $result")
    }

    @Test
    fun testIsSameDay_SameDayWithTimezone() {
        println("[DEBUG_LOG] Testing isSameDay function - same day with timezone")

        val start = "2025-05-30T08:30:00+02:00"
        val end = "2025-05-30T13:00:00+02:00"

        val result = isSameDay(start, end)

        assertTrue("Events on same day with timezone should return true", result)

        println("[DEBUG_LOG] isSameDay test with timezone: $start and $end -> $result")
    }

    @Test
    fun testIsSameDay_EdgeCases() {
        println("[DEBUG_LOG] Testing isSameDay function - edge cases")

        val emptyResult = isSameDay("", "")
        assertFalse("Empty strings should return false", emptyResult)
        println("[DEBUG_LOG] Empty strings result: $emptyResult")

        val nullResult = isSameDay("null", "null")
        assertFalse("Null strings should return false", nullResult)
        println("[DEBUG_LOG] Null strings result: $nullResult")

        val malformedResult = isSameDay("2025-05-30", "2025-05-30")
        assertFalse("Strings without T should return false", malformedResult)
        println("[DEBUG_LOG] Malformed datetime result: $malformedResult")

        val differentTzResult = isSameDay("2025-05-30T08:30:00Z", "2025-05-30T13:00:00Z")
        assertTrue("Same day with Z timezone should return true", differentTzResult)
        println("[DEBUG_LOG] Z timezone result: $differentTzResult")

        val millisecondsResult = isSameDay("2025-05-30T08:30:00.000+02:00", "2025-05-30T13:00:00.000+02:00")
        assertTrue("Same day with milliseconds should return true", millisecondsResult)
        println("[DEBUG_LOG] Milliseconds result: $millisecondsResult")
    }

    @Test
    fun testIsSameDay_RealWorldScenarios() {
        println("[DEBUG_LOG] Testing isSameDay function - real world scenarios")

        val midnightSameDay = isSameDay("2025-05-30T23:59:00+02:00", "2025-05-30T23:59:59+02:00")
        assertTrue("Same calendar day should return true", midnightSameDay)
        println("[DEBUG_LOG] Midnight same day result: $midnightSameDay")

        val midnightDifferentDay = isSameDay("2025-05-30T23:59:00+02:00", "2025-05-31T00:01:00+02:00")
        assertFalse("Different calendar days should return false", midnightDifferentDay)
        println("[DEBUG_LOG] Midnight different day result: $midnightDifferentDay")

        val differentTimezones = isSameDay("2025-05-30T02:00:00+02:00", "2025-05-30T08:00:00+08:00")
        assertTrue("Same calendar date regardless of timezone should return true", differentTimezones)
        println("[DEBUG_LOG] Different timezones result: $differentTimezones")

        val apiFormat = isSameDay("2025-05-30T08:30:00+02:00", "2025-05-30T13:00:00+02:00")
        assertTrue("API format should work correctly", apiFormat)
        println("[DEBUG_LOG] API format result: $apiFormat")

        val timezoneEdgeCase = isSameDay("2025-05-30T22:00:00-10:00", "2025-05-31T08:00:00+02:00")
        assertFalse("Different local calendar dates should return false", timezoneEdgeCase)
        println("[DEBUG_LOG] Timezone edge case result: $timezoneEdgeCase")
    }

    @Test
    fun testIsSameDay_SpaceSeparatedFormat() {
        println("[DEBUG_LOG] Testing isSameDay function - space-separated format")

        val start = "2025-08-16 19:00:00"
        val end = "2025-08-16 20:00:00"

        val result = isSameDay(start, end)

        assertTrue("Events on same day with space format should return true", result)

        println("[DEBUG_LOG] isSameDay test with space format: $start and $end -> $result")

        val differentDayStart = "2025-08-16 19:00:00"
        val differentDayEnd = "2025-08-17 20:00:00"

        val differentDayResult = isSameDay(differentDayStart, differentDayEnd)

        assertFalse("Events on different days with space format should return false", differentDayResult)

        println("[DEBUG_LOG] isSameDay test with space format (different days): $differentDayStart and $differentDayEnd -> $differentDayResult")
    }

    @Test
    fun testFormatTime_SpaceSeparatedFormat() {
        println("[DEBUG_LOG] Testing formatTime function - space-separated format")

        val dateTime = "2025-08-16 19:00:00"
        val formattedTime = formatTime(dateTime)

        assertEquals("19:00", formattedTime)

        println("[DEBUG_LOG] formatTime test with space format: $dateTime -> $formattedTime")
    }

    @Test
    fun testFormatDate_SpaceSeparatedFormat() {
        println("[DEBUG_LOG] Testing formatDate function - space-separated format")

        val dateTime = "2025-08-16 19:00:00"
        val formattedDate = formatDate(dateTime)

        assertEquals("2025-8-16", formattedDate)

        println("[DEBUG_LOG] formatDate test with space format: $dateTime -> $formattedDate")
    }
}
