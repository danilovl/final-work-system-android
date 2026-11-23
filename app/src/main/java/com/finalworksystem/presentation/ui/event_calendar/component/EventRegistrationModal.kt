package com.finalworksystem.presentation.ui.event_calendar.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.finalworksystem.domain.event_calendar.model.CalendarEvent
import com.finalworksystem.domain.work.model.Work
import com.finalworksystem.ui.designsystem.AppButton
import com.finalworksystem.ui.designsystem.AppButtonVariant
import com.finalworksystem.ui.designsystem.AppCard
import com.finalworksystem.ui.designsystem.AppCardStyle
import com.finalworksystem.ui.designsystem.AppDialog
import com.finalworksystem.ui.designsystem.AppIconBadge
import com.finalworksystem.ui.designsystem.AppSelectField
import com.finalworksystem.ui.designsystem.AppSpinner
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme

@Composable
fun EventRegistrationModal(
    event: CalendarEvent,
    userWorks: List<Work>,
    isLoading: Boolean,
    isRegistering: Boolean,
    onDismiss: () -> Unit,
    onRegister: (workId: Int) -> Unit
) {
    var selectedWorkId by remember { mutableStateOf<Int?>(null) }

    AppDialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AppIconBadge(icon = AppIcons.BookmarkAdd, size = 40)
                Spacer(modifier = Modifier.width(12.dp))
                AppText(
                    text = stringResource(R.string.reservation),
                    style = AppTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTheme.colors.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            AppCard(
                style = AppCardStyle.Tonal,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(14.dp)
            ) {
                AppText(
                    text = event.title,
                    style = AppTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTheme.colors.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (isSameDay(event.start, event.end)) {
                        AppText(
                            text = stringResource(R.string.date_prefix, formatDate(event.start)),
                            style = AppTheme.typography.bodyMedium,
                            color = AppTheme.colors.onSurfaceVariant
                        )
                        AppText(
                            text = stringResource(R.string.time_range, formatTime(event.start), formatTime(event.end)),
                            style = AppTheme.typography.bodyMedium,
                            color = AppTheme.colors.onSurfaceVariant
                        )
                    } else {
                        AppText(
                            text = stringResource(R.string.start_prefix, formatDateTime(event.start)),
                            style = AppTheme.typography.bodyMedium,
                            color = AppTheme.colors.onSurfaceVariant
                        )
                        AppText(
                            text = stringResource(R.string.end_prefix, formatDateTime(event.end)),
                            style = AppTheme.typography.bodyMedium,
                            color = AppTheme.colors.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AppSpinner()
                    }
                }

                userWorks.isEmpty() -> {
                    AppText(
                        text = stringResource(R.string.no_works_available_registration),
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colors.error
                    )
                }

                else -> {
                    AppSelectField(
                        selected = userWorks.find { it.id == selectedWorkId },
                        options = userWorks,
                        optionLabel = { it.title },
                        onSelect = { selectedWorkId = it.id },
                        label = { AppText(stringResource(R.string.select_work)) },
                        placeholder = { AppText(stringResource(R.string.select_work_placeholder)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppButton(
                    text = stringResource(R.string.cancel),
                    onClick = onDismiss,
                    enabled = !isRegistering,
                    variant = AppButtonVariant.Outline,
                    modifier = Modifier.weight(1f)
                )

                AppButton(
                    text = stringResource(R.string.register),
                    onClick = {
                        selectedWorkId?.let { workId ->
                            onRegister(workId)
                        }
                    },
                    enabled = selectedWorkId != null && !isRegistering && !isLoading,
                    loading = isRegistering,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
