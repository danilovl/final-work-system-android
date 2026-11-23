package com.finalworksystem.presentation.ui.work.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finalworksystem.ui.designsystem.AppIconBadge
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.theme.AppTheme

@Composable
fun WorkInfoItemWithIcon(
    icon: ImageVector,
    label: String,
    value: String,
    iconTint: Color = AppTheme.colors.primary
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppIconBadge(
            icon = icon,
            tint = iconTint,
            size = 40
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            AppText(
                text = label,
                style = AppTheme.typography.labelMedium,
                color = AppTheme.colors.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            AppText(
                text = value,
                style = AppTheme.typography.bodyLarge,
                color = AppTheme.colors.onSurface,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
