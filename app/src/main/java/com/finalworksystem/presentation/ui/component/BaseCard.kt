package com.finalworksystem.presentation.ui.component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.finalworksystem.ui.designsystem.AppCard
import com.finalworksystem.ui.designsystem.AppCardStyle

@Composable
fun BaseCard(
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: AppCardStyle = AppCardStyle.Elevated,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    AppCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        style = style,
        color = color,
        content = content
    )
}
