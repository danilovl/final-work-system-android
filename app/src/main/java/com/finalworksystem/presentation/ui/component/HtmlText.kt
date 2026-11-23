package com.finalworksystem.presentation.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.theme.AppTheme

/**
 * Converts HTML (bold, italic, underline, lists, links, ...) to an [AnnotatedString]
 * with Compose's own parser. Links are clickable and styled with the theme color.
 */
@Composable
fun rememberHtmlText(html: String?): AnnotatedString {
    val linkColor = AppTheme.colors.primary

    return remember(html, linkColor) {
        if (html.isNullOrBlank()) {
            AnnotatedString("")
        } else {
            AnnotatedString.fromHtml(
                htmlString = html,
                linkStyles = TextLinkStyles(
                    style = SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline)
                )
            )
        }
    }
}

@Composable
fun HtmlText(
    html: String,
    modifier: Modifier = Modifier,
    textColor: Color = AppTheme.colors.onSurface,
    textSize: Float = 14f
) {
    AppText(
        text = rememberHtmlText(html),
        modifier = modifier,
        color = textColor,
        fontSize = textSize.sp,
        style = AppTheme.typography.bodyMedium
    )
}
