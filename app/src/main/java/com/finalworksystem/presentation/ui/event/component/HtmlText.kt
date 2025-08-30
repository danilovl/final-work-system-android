package com.finalworksystem.presentation.ui.event.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun HtmlText(
    html: String,
    modifier: Modifier = Modifier,
    textColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    textSize: Float = 14f
) {
    val annotatedString = com.finalworksystem.domain.common.util.HtmlUtils.fromHtml(html)

    Text(
        text = annotatedString,
        modifier = modifier,
        color = textColor,
        fontSize = textSize.sp,
        style = MaterialTheme.typography.bodyMedium
    )
}
