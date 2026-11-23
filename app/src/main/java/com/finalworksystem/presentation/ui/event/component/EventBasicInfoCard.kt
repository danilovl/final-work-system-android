package com.finalworksystem.presentation.ui.event.component

import com.finalworksystem.ui.theme.AppIcons
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finalworksystem.R
import com.finalworksystem.domain.common.util.DateUtils
import com.finalworksystem.domain.event.model.Event
import com.finalworksystem.presentation.ui.component.BaseCard
import com.finalworksystem.ui.designsystem.AppDivider
import com.finalworksystem.ui.designsystem.AppIconBadge
import com.finalworksystem.ui.designsystem.AppTag
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.theme.AppTheme

@Composable
fun EventBasicInfoCard(event: Event) {
    BaseCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AppText(
                text = stringResource(R.string.event_information),
                style = AppTheme.typography.labelMedium,
                color = AppTheme.colors.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppIconBadge(icon = AppIcons.Today, size = 44)
                Spacer(modifier = Modifier.width(12.dp))
                AppText(
                    text = event.name ?: event.type.name,
                    style = AppTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                AppTag(
                    text = stringResource(R.string.type_prefix, event.type.name),
                    icon = AppIcons.Info
                )
                event.type.description?.let { description ->
                    AppText(
                        text = description,
                        style = AppTheme.typography.bodySmall,
                        color = AppTheme.colors.onSurfaceVariant
                    )
                }
            }

            AppDivider()

            EventInfoLine(icon = AppIcons.DateRange) {
                AppText(
                    text = stringResource(R.string.start_prefix, DateUtils.formatToReadableDateTime(event.start)),
                    style = AppTheme.typography.bodyMedium
                )
                AppText(
                    text = stringResource(R.string.end_prefix, DateUtils.formatToReadableDateTime(event.end)),
                    style = AppTheme.typography.bodyMedium
                )
            }

            EventInfoLine(icon = AppIcons.Person) {
                AppText(
                    text = stringResource(R.string.owner_prefix, event.owner.fullName ?: "${event.owner.firstname} ${event.owner.lastname}"),
                    style = AppTheme.typography.bodyMedium
                )
            }

            event.participant?.let { participant ->
                EventInfoLine(icon = AppIcons.Groups) {
                    AppText(
                        text = stringResource(R.string.participant_prefix, participant.user.fullName ?: "${participant.user.firstname} ${participant.user.lastname}"),
                        style = AppTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun EventInfoLine(
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppIconBadge(icon = icon, size = 36)
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            content()
        }
    }
}
