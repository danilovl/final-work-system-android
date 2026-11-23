package com.finalworksystem.presentation.ui.component

import android.app.Application
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * `AnnotatedString.fromHtml` uses the Android HTML parser, so the test runs on Robolectric.
 */
@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class HtmlTextTest {

    @Test
    fun `plain text stays unchanged`() {
        assertEquals("Plain text", AnnotatedString.fromHtml("Plain text").text)
    }

    @Test
    fun `tags are removed from the text`() {
        val result = AnnotatedString.fromHtml("<p>Hello <b>bold</b> and <i>italic</i></p>")

        assertEquals("Hello bold and italic", result.text.trim())
    }

    @Test
    fun `bold and italic become span styles`() {
        val result = AnnotatedString.fromHtml("<b>bold</b> <i>italic</i>")

        assertTrue(result.spanStyles.any { it.item.fontWeight == FontWeight.Bold && result.text.substring(it.start, it.end) == "bold" })
        assertTrue(result.spanStyles.any { it.item.fontStyle == FontStyle.Italic && result.text.substring(it.start, it.end) == "italic" })
    }

    @Test
    fun `links are clickable annotations`() {
        val result = AnnotatedString.fromHtml("""see <a href="https://example.com">site</a>""")

        val link = result.getLinkAnnotations(0, result.length).single()
        assertEquals("https://example.com", (link.item as LinkAnnotation.Url).url)
        assertEquals("site", result.text.substring(link.start, link.end))
    }

    @Test
    fun `entities are decoded`() {
        assertEquals("a & b", AnnotatedString.fromHtml("a &amp; b").text)
    }
}
