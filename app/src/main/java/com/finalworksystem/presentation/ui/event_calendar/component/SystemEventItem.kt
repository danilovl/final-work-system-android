package com.finalworksystem.presentation.ui.event_calendar.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finalworksystem.R
import com.finalworksystem.domain.system_event.model.SystemEvent
import com.finalworksystem.presentation.ui.component.BaseCard
import com.finalworksystem.ui.designsystem.AppDivider
import com.finalworksystem.ui.designsystem.AppIcon
import com.finalworksystem.ui.designsystem.AppIconBadge
import com.finalworksystem.ui.designsystem.AppIconButton
import com.finalworksystem.ui.designsystem.AppIconButtonVariant
import com.finalworksystem.ui.designsystem.AppTag
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme

@Composable
fun SystemEventItem(
    event: SystemEvent,
    onViewedChange: (Int, Boolean) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val titleText = event.title
    val isTitleLong = titleText.length > 80

    BaseCard {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                AppIconBadge(icon = AppIcons.Notifications, size = 36)

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(enabled = isTitleLong) { isExpanded = !isExpanded },
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    AppText(
                        text = if (isTitleLong && !isExpanded) titleText.take(80) + "..." else titleText,
                        style = AppTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = AppTheme.colors.onSurface
                    )

                    if (isTitleLong) {
                        AppText(
                            text = if (isExpanded) stringResource(R.string.tap_to_collapse) else stringResource(R.string.tap_to_expand),
                            style = AppTheme.typography.labelMedium,
                            color = AppTheme.colors.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                AppIcon(
                    imageVector = AppIcons.Person,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = AppTheme.colors.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(6.dp))
                AppText(
                    text = stringResource(R.string.by_prefix, event.owner),
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.onSurface
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                AppIcon(
                    imageVector = AppIcons.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = AppTheme.colors.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(6.dp))
                AppText(
                    text = event.createdAt,
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.onSurfaceVariant
                )
            }

            AppDivider(modifier = Modifier.padding(vertical = 12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppTag(
                    text = stringResource(R.string.id_prefix, event.id.toString()),
                    color = AppTheme.colors.onSurfaceVariant
                )

                AppIconButton(
                    icon = AppIcons.Check,
                    contentDescription = stringResource(R.string.mark_as_viewed),
                    onClick = {
                        onViewedChange(event.id, true)
                    },
                    variant = AppIconButtonVariant.Tonal,
                    tint = AppTheme.colors.primary,
                    size = 36.dp,
                    iconSize = 20.dp
                )
            }
        }
    }
}
