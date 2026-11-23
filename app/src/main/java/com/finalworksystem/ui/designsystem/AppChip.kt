package com.finalworksystem.ui.designsystem

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.finalworksystem.ui.theme.AppTheme

/** Selectable / clickable pill. */
@Composable
fun AppChip(
    label: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    selected: Boolean = false,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null
) {
    val c = AppTheme.colors
    val bg by animateColorAsState(if (selected) c.primary else c.surface, label = "chipBg")
    val fg by animateColorAsState(if (selected) c.onPrimary else c.onSurface, label = "chipFg")

    AppSurface(
        modifier = modifier,
        shape = CircleShape,
        color = bg,
        contentColor = if (enabled) fg else fg.copy(alpha = 0.4f),
        border = if (selected) null else BorderStroke(1.dp, c.outlineVariant),
        onClick = onClick,
        enabled = enabled,
        pressScale = true
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (leadingIcon != null) AppIcon(leadingIcon, null, Modifier.size(16.dp))
            AppText(label, style = AppTheme.typography.labelLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (trailingIcon != null) AppIcon(trailingIcon, null, Modifier.size(16.dp))
        }
    }
}

/**
 * Small soft-colored label for statuses / types / counters.
 * [color] is the accent; background is derived from it.
 */
@Composable
fun AppTag(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = AppTheme.colors.primary,
    icon: ImageVector? = null,
    solid: Boolean = false
) {
    val bg = if (solid) color else color.copy(alpha = if (AppTheme.colors.isDark) 0.22f else 0.12f)
    val fg = if (solid) AppTheme.colors.contentColorFor(color).let { if (it == Color.Unspecified) Color.White else it } else color

    Row(
        modifier = modifier
            .background(bg, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (icon != null) AppIcon(icon, null, Modifier.size(12.dp), tint = fg)
        AppText(text, style = AppTheme.typography.labelMedium, color = fg, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

/** Small round counter badge. */
@Composable
fun AppBadge(
    count: Int,
    modifier: Modifier = Modifier,
    color: Color = AppTheme.colors.error
) {
    Box(
        modifier = modifier
            .background(color, CircleShape)
            .padding(horizontal = 6.dp, vertical = 1.dp),
        contentAlignment = Alignment.Center
    ) {
        AppText(
            text = if (count > 99) "99+" else count.toString(),
            style = AppTheme.typography.labelSmall,
            color = AppTheme.colors.contentColorFor(color).let { if (it == Color.Unspecified) Color.White else it }
        )
    }
}
