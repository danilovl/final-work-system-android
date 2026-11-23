package com.finalworksystem.ui.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.finalworksystem.ui.theme.AppTheme

/** Centered placeholder for empty / error states. */
@Composable
fun AppEmptyState(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    message: String? = null,
    iconTint: Color = AppTheme.colors.primary,
    action: (@Composable () -> Unit)? = null
) {
    Box(modifier = modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (icon != null) {
                Box(
                    Modifier
                        .size(72.dp)
                        .background(iconTint.copy(alpha = 0.12f), RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    AppIcon(icon, null, Modifier.size(34.dp), tint = iconTint)
                }
                Spacer(Modifier.height(20.dp))
            }
            AppText(title, style = AppTheme.typography.titleLarge, textAlign = TextAlign.Center)
            if (message != null) {
                Spacer(Modifier.height(8.dp))
                AppText(
                    message,
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
            if (action != null) {
                Spacer(Modifier.height(24.dp))
                action()
            }
        }
    }
}

/** Section heading with an optional trailing slot. */
@Composable
fun AppSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            AppIconBadge(icon = icon, size = 32)
            Spacer(Modifier.width(10.dp))
        }
        AppText(
            title,
            style = AppTheme.typography.titleMedium,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        trailing?.invoke()
    }
}

/** Icon inside a soft tinted rounded square. */
@Composable
fun AppIconBadge(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    tint: Color = AppTheme.colors.primary,
    size: Int = 40
) {
    Box(
        modifier
            .size(size.dp)
            .background(tint.copy(alpha = if (AppTheme.colors.isDark) 0.2f else 0.12f), RoundedCornerShape((size * 0.32f).dp)),
        contentAlignment = Alignment.Center
    ) {
        AppIcon(icon, null, Modifier.size((size * 0.5f).dp), tint = tint)
    }
}

/** Label / value pair with an optional leading icon (for detail screens). */
@Composable
fun AppInfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    valueColor: Color = Color.Unspecified
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        if (icon != null) {
            AppIconBadge(icon = icon, size = 36, tint = AppTheme.colors.primary)
            Spacer(Modifier.width(12.dp))
        }
        Column(Modifier.weight(1f)) {
            AppText(label, style = AppTheme.typography.labelMedium, color = AppTheme.colors.onSurfaceVariant)
            Spacer(Modifier.height(2.dp))
            AppText(value, style = AppTheme.typography.bodyLarge, color = valueColor)
        }
    }
}
