package com.finalworksystem.presentation.ui.event.component

import com.finalworksystem.ui.theme.AppIcons
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finalworksystem.R
import com.finalworksystem.domain.common.util.DateUtils
import com.finalworksystem.domain.work.model.Work
import com.finalworksystem.presentation.ui.component.BaseCard
import com.finalworksystem.ui.designsystem.AppSectionHeader
import com.finalworksystem.ui.designsystem.AppTag
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.theme.AppTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EventWorkInfoCard(work: Work) {
    BaseCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AppSectionHeader(
                title = stringResource(R.string.work_information),
                icon = AppIcons.Work
            )

            AppText(
                text = work.title,
                style = AppTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppTag(
                    text = stringResource(R.string.type_prefix, work.type.name),
                    color = AppTheme.colors.secondary
                )
                AppTag(
                    text = stringResource(R.string.status_prefix, work.status.name),
                    color = AppTheme.colors.tertiary,
                    icon = AppIcons.CheckCircle
                )
                work.deadline?.let { deadline ->
                    AppTag(
                        text = stringResource(R.string.deadline_prefix, DateUtils.formatToReadableDateTime(deadline)),
                        color = AppTheme.colors.warning,
                        icon = AppIcons.CalendarToday
                    )
                }
            }
        }
    }
}
