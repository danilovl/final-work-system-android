package com.finalworksystem.ui.designsystem

import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import com.finalworksystem.ui.theme.LocalAppContentColor
import com.finalworksystem.ui.theme.LocalAppTextStyle

@Composable
fun AppText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: TextStyle = LocalAppTextStyle.current,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    fontStyle: FontStyle? = null,
    textAlign: TextAlign? = null,
    textDecoration: TextDecoration? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = mergeStyle(style, color, fontSize, fontWeight, fontStyle, textAlign, textDecoration, lineHeight),
        onTextLayout = onTextLayout,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines
    )
}

@Composable
fun AppText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: TextStyle = LocalAppTextStyle.current,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    fontStyle: FontStyle? = null,
    textAlign: TextAlign? = null,
    textDecoration: TextDecoration? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    inlineContent: Map<String, InlineTextContent> = emptyMap(),
    onTextLayout: ((TextLayoutResult) -> Unit)? = null
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = mergeStyle(style, color, fontSize, fontWeight, fontStyle, textAlign, textDecoration, lineHeight),
        onTextLayout = onTextLayout,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        inlineContent = inlineContent
    )
}

/** Provides a default text style (and optionally a content color) for a subtree. */
@Composable
fun ProvideAppTextStyle(
    style: TextStyle,
    color: Color = Color.Unspecified,
    content: @Composable () -> Unit
) {
    val merged = LocalAppTextStyle.current.merge(style)
    if (color == Color.Unspecified) {
        CompositionLocalProvider(LocalAppTextStyle provides merged, content = content)
    } else {
        CompositionLocalProvider(
            LocalAppTextStyle provides merged,
            LocalAppContentColor provides color,
            content = content
        )
    }
}

@Composable
private fun mergeStyle(
    style: TextStyle,
    color: Color,
    fontSize: TextUnit,
    fontWeight: FontWeight?,
    fontStyle: FontStyle?,
    textAlign: TextAlign?,
    textDecoration: TextDecoration?,
    lineHeight: TextUnit
): TextStyle {
    val resolvedColor = color.takeOrElse { style.color.takeOrElse { LocalAppContentColor.current } }
    return style.merge(
        TextStyle(
            color = resolvedColor,
            fontSize = fontSize,
            fontWeight = fontWeight,
            fontStyle = fontStyle,
            textAlign = textAlign ?: TextAlign.Unspecified,
            textDecoration = textDecoration,
            lineHeight = lineHeight
        )
    )
}
