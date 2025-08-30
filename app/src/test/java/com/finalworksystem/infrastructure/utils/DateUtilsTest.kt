package com.finalworksystem.infrastructure.utils

import com.finalworksystem.domain.common.util.DateUtils
import org.junit.Assert.assertEquals
import org.junit.Test

class DateUtilsTest {

    @Test
    fun testFormatToYmd_withValidAtomDate() {
        val atomDate = "2023-12-25T10:30:00Z"
        val result = DateUtils.formatToYmd(atomDate)
        assertEquals("2023-12-25", result)
    }

    @Test
    fun testFormatToYmd_withValidAtomDateWithoutZ() {
        val atomDate = "2023-12-25T10:30:00"
        val result = DateUtils.formatToYmd(atomDate)
        assertEquals("2023-12-25", result)
    }

    @Test
    fun testFormatToYmd_withNullDate() {
        val result = DateUtils.formatToYmd(null)
        assertEquals("", result)
    }

    @Test
    fun testFormatToYmd_withEmptyDate() {
        val result = DateUtils.formatToYmd("")
        assertEquals("", result)
    }

    @Test
    fun testFormatToYmd_withInvalidDate() {
        val invalidDate = "invalid-date"
        val result = DateUtils.formatToYmd(invalidDate)
        assertEquals("invalid-date", result)
    }

    @Test
    fun testFormatToReadable_withValidAtomDate() {
        val atomDate = "2023-12-25T10:30:00Z"
        val result = DateUtils.formatToReadable(atomDate)
        assertEquals("25.12.2023", result)
    }

    @Test
    fun testFormatDate_withCustomFormat() {
        val atomDate = "2023-12-25T10:30:00Z"
        val result = DateUtils.formatDate(atomDate, "dd/MM/yyyy")
        assertEquals("25/12/2023", result)
    }

    @Test
    fun testFormatToReadableDateTime_withValidAtomDate() {
        val atomDate = "2023-12-25T10:30:00Z"
        val result = DateUtils.formatToReadableDateTime(atomDate)
        assertEquals("25.12.2023 10:30", result)
    }

    @Test
    fun testFormatToReadableDateTime_withValidAtomDateWithoutZ() {
        val atomDate = "2023-12-25T10:30:00"
        val result = DateUtils.formatToReadableDateTime(atomDate)
        assertEquals("25.12.2023 10:30", result)
    }

    @Test
    fun testFormatToReadableDateTime_withNullDate() {
        val result = DateUtils.formatToReadableDateTime(null)
        assertEquals("", result)
    }

    @Test
    fun testFormatToReadableDateTime_withEmptyDate() {
        val result = DateUtils.formatToReadableDateTime("")
        assertEquals("", result)
    }

    @Test
    fun testFormatToReadableDateTime_withInvalidDate() {
        val invalidDate = "invalid-date"
        val result = DateUtils.formatToReadableDateTime(invalidDate)
        assertEquals("invalid-date", result)
    }

    @Test
    fun testFormatToYmd_withSpaceSeparatedFormat() {
        val atomDate = "2016-01-01 00:00:00"
        val result = DateUtils.formatToYmd(atomDate)
        assertEquals("2016-01-01", result)
    }

    @Test
    fun testFormatToYmd_withSpaceSeparatedFormatNonZeroTime() {
        val atomDate = "2023-12-25 14:30:45"
        val result = DateUtils.formatToYmd(atomDate)
        assertEquals("2023-12-25", result)
    }

    @Test
    fun testFormatToReadable_withSpaceSeparatedFormat() {
        val atomDate = "2016-01-01 00:00:00"
        val result = DateUtils.formatToReadable(atomDate)
        assertEquals("01.01.2016", result)
    }

    @Test
    fun testFormatToReadableDateTime_withSpaceSeparatedFormat() {
        val atomDate = "2016-01-01 14:30:00"
        val result = DateUtils.formatToReadableDateTime(atomDate)
        assertEquals("01.01.2016 14:30", result)
    }

    @Test
    fun testFormatToYmd_withISOOffsetDateTime() {
        val atomDate = "2023-12-25T10:30:00+03:00"
        val result = DateUtils.formatToYmd(atomDate)
        assertEquals("2023-12-25", result)
    }

    @Test
    fun testFormatToYmd_withDateOnly() {
        val atomDate = "2023-12-25"
        val result = DateUtils.formatToYmd(atomDate)
        assertEquals("2023-12-25", result)
    }

    @Test
    fun testFormatDate_withSpaceSeparatedFormat() {
        val atomDate = "2016-01-01 00:00:00"
        val result = DateUtils.formatDate(atomDate, "dd/MM/yyyy")
        assertEquals("01/01/2016", result)
    }

}
