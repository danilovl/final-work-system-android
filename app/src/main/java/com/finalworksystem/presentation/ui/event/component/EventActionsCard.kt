package com.finalworksystem.presentation.ui.event.component

import com.finalworksystem.ui.theme.AppIcons
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.finalworksystem.R
import com.finalworksystem.presentation.ui.component.BaseCard
import com.finalworksystem.ui.designsystem.AppButton
import com.finalworksystem.ui.designsystem.AppButtonVariant
import com.finalworksystem.ui.designsystem.AppSectionHeader

@Composable
fun EventActionsCard(
    isSupervisor: Boolean,
    isDeletingEvent: Boolean,
    onDeleteEventClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isSupervisor) {
        BaseCard(
            modifier = modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AppSectionHeader(
                    title = stringResource(R.string.actions),
                    icon = AppIcons.Edit
                )

                AppButton(
                    text = if (isDeletingEvent) {
                        stringResource(R.string.deleting)
                    } else {
                        stringResource(R.string.delete_event)
                    },
                    onClick = onDeleteEventClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isDeletingEvent,
                    variant = AppButtonVariant.DangerSoft,
                    leadingIcon = if (isDeletingEvent) null else AppIcons.Delete,
                    loading = isDeletingEvent
                )
            }
        }
    }
}
