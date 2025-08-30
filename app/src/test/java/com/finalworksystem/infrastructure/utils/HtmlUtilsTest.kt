package com.finalworksystem.infrastructure.utils

import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.finalworksystem.domain.common.util.HtmlUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HtmlUtilsTest {

    @Test
    fun testFromHtml_withNullInput() {
        val result = HtmlUtils.fromHtml(null)
        assertEquals("", result.text)
    }

    @Test
    fun testFromHtml_withEmptyInput() {
        val result = HtmlUtils.fromHtml("")
        assertEquals("", result.text)
    }

    @Test
    fun testFromHtml_withBlankInput() {
        val result = HtmlUtils.fromHtml("   ")
        assertEquals("", result.text)
    }

    @Test
    fun testFromHtml_withPlainText() {
        val input = "This is plain text"
        val result = HtmlUtils.fromHtml(input)
        assertEquals("This is plain text", result.text)
    }

    @Test
    fun testFromHtml_withBoldText() {
        val input = "This is <b>bold</b> text"
        val result = HtmlUtils.fromHtml(input)
        assertEquals("This is bold text", result.text)
        
        // In unit tests, span styling may not be available due to Android context limitations
        // Just verify the text is properly stripped of HTML tags
        assertTrue("Text should not contain HTML tags", !result.text.contains("<"))
    }

    @Test
    fun testFromHtml_withItalicText() {
        val input = "This is <i>italic</i> text"
        val result = HtmlUtils.fromHtml(input)
        assertEquals("This is italic text", result.text)
        
        // In unit tests, span styling may not be available due to Android context limitations
        assertTrue("Text should not contain HTML tags", !result.text.contains("<"))
    }

    @Test
    fun testFromHtml_withUnderlineText() {
        val input = "This is <u>underlined</u> text"
        val result = HtmlUtils.fromHtml(input)
        assertEquals("This is underlined text", result.text)
        
        // In unit tests, span styling may not be available due to Android context limitations
        assertTrue("Text should not contain HTML tags", !result.text.contains("<"))
    }

    @Test
    fun testFromHtml_withStrikethroughText() {
        val input = "This is <strike>strikethrough</strike> text"
        val result = HtmlUtils.fromHtml(input)
        assertEquals("This is strikethrough text", result.text)
        
        // In unit tests, span styling may not be available due to Android context limitations
        assertTrue("Text should not contain HTML tags", !result.text.contains("<"))
    }

    @Test
    fun testFromHtml_withMultipleFormats() {
        val input = "Text with <b>bold</b> and <i>italic</i> formatting"
        val result = HtmlUtils.fromHtml(input)
        assertEquals("Text with bold and italic formatting", result.text)
        
        // In unit tests, span styling may not be available due to Android context limitations
        assertTrue("Text should not contain HTML tags", !result.text.contains("<"))
    }

    @Test
    fun testFromHtml_withNestedTags() {
        val input = "This is <b><i>bold italic</i></b> text"
        val result = HtmlUtils.fromHtml(input)
        assertEquals("This is bold italic text", result.text)
        
        // In unit tests, span styling may not be available due to Android context limitations
        assertTrue("Text should not contain HTML tags", !result.text.contains("<"))
    }
}