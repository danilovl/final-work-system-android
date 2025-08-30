package com.finalworksystem.infrastructure.utils

import com.finalworksystem.domain.common.util.FileUtils
import org.junit.Assert.assertEquals
import org.junit.Test

class FileUtilsTest {

    @Test
    fun testFormatFileSize_bytes() {
        val result = FileUtils.formatFileSize(512)
        assertEquals("512 B", result)
    }

    @Test
    fun testFormatFileSize_kilobytes() {
        val result = FileUtils.formatFileSize(2048)
        assertEquals("2 KB", result)
    }

    @Test
    fun testFormatFileSize_megabytes() {
        val result = FileUtils.formatFileSize(2097152)
        assertEquals("2 MB", result)
    }

    @Test
    fun testFormatFileSize_exactKilobyte() {
        val result = FileUtils.formatFileSize(1024)
        assertEquals("1 KB", result)
    }

    @Test
    fun testFormatFileSize_exactMegabyte() {
        val result = FileUtils.formatFileSize(1048576)
        assertEquals("1 MB", result)
    }

    @Test
    fun testFormatFileSize_zero() {
        val result = FileUtils.formatFileSize(0)
        assertEquals("0 B", result)
    }
}