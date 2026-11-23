package com.finalworksystem.ui.designsystem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.unit.dp
import com.finalworksystem.ui.theme.AppTheme

enum class AppCardStyle {
    /** White card with a hairline border and a soft shadow. */
    Elevated,

    /** Card with a border only. */
    Outlined,

    /** Muted fill, no border. */
    Tonal,

    /** Accent (primary container) fill. */
    Accent
}

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    style: AppCardStyle = AppCardStyle.Elevated,
    color: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified,
    shape: Shape = AppTheme.shapes.large,
    border: BorderStroke? = null,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = AppTheme.colors
    val container = when {
        color.isSpecified -> color
        style == AppCardStyle.Tonal -> colors.surfaceVariant
        style == AppCardStyle.Accent -> colors.primaryContainer
        else -> colors.surface
    }
    val resolvedBorder = border ?: when (style) {
        AppCardStyle.Elevated -> if (colors.isDark) BorderStroke(1.dp, colors.outlineVariant) else BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.7f))
        AppCardStyle.Outlined -> BorderStroke(1.dp, colors.outlineVariant)
        else -> null
    }

    AppSurface(
        modifier = modifier,
        shape = shape,
        color = container,
        contentColor = if (contentColor.isSpecified) contentColor else contentColorFor(container),
        border = resolvedBorder,
        shadowElevation = if (style == AppCardStyle.Elevated && !colors.isDark) 3.dp else 0.dp,
        onClick = onClick,
        enabled = enabled,
        pressScale = onClick != null
    ) {
        Column(modifier = Modifier.padding(contentPadding), content = content)
    }
}
