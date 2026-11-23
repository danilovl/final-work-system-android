package com.finalworksystem.presentation.ui.user.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.finalworksystem.domain.common.util.DateUtils
import com.finalworksystem.domain.work.model.Work
import com.finalworksystem.presentation.ui.component.BaseCard
import com.finalworksystem.ui.designsystem.AppBottomSheet
import com.finalworksystem.ui.designsystem.AppButton
import com.finalworksystem.ui.designsystem.AppButtonSize
import com.finalworksystem.ui.designsystem.AppButtonVariant
import com.finalworksystem.ui.designsystem.AppCardStyle
import com.finalworksystem.ui.designsystem.AppIconBadge
import com.finalworksystem.ui.designsystem.AppIconButton
import com.finalworksystem.ui.designsystem.AppIconButtonVariant
import com.finalworksystem.ui.designsystem.AppInfoRow
import com.finalworksystem.ui.designsystem.AppTag
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme

@Composable
fun WorksList(works: List<Work>, onNavigateToWorkDetail: (Int) -> Unit) {
    var showWorksSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        AppButton(
            text = stringResource(R.string.works_with_count, works.size),
            onClick = { showWorksSheet = true },
            variant = AppButtonVariant.Secondary,
            size = AppButtonSize.Small,
            leadingIcon = AppIcons.List,
            modifier = Modifier.fillMaxWidth()
        )
    }

    if (showWorksSheet) {
        AppBottomSheet(
            onDismissRequest = { showWorksSheet = false }
        ) {
            WorksFullList(
                works = works,
                onDismiss = { showWorksSheet = false },
                onNavigateToWorkDetail = { workId ->
                    showWorksSheet = false
                    onNavigateToWorkDetail(workId)
                }
            )
        }
    }
}

@Composable
fun WorksFullList(
    works: List<Work>,
    onDismiss: () -> Unit,
    onNavigateToWorkDetail: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppIconBadge(icon = AppIcons.Work, size = 40)

            Spacer(modifier = Modifier.width(12.dp))

            AppText(
                text = stringResource(R.string.works_with_count, works.size),
                style = AppTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )

            AppIconButton(
                icon = AppIcons.Close,
                contentDescription = stringResource(R.string.close),
                onClick = onDismiss,
                variant = AppIconButtonVariant.Tonal
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(works) { work ->
                WorkFullWidthItem(
                    work = work,
                    onClick = { onNavigateToWorkDetail(work.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun WorkFullWidthItem(work: Work, onClick: () -> Unit) {
    val colors = AppTheme.colors

    BaseCard(
        modifier = Modifier.fillMaxWidth(),
        style = AppCardStyle.Tonal,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            AppText(
                text = work.title,
                style = AppTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                AppTag(text = stringResource(R.string.type_prefix, work.type.name), color = colors.secondary)
                AppTag(text = stringResource(R.string.status_prefix, work.status.name), color = colors.primary)
            }

            Spacer(modifier = Modifier.height(8.dp))

            AppInfoRow(
                label = stringResource(R.string.supervisor),
                value = work.supervisor?.let { "${it.firstname} ${it.lastname}" } ?: "N/A",
                icon = AppIcons.Person
            )

            AppText(
                text = stringResource(R.string.deadline_prefix, DateUtils.formatToReadable(work.deadline)),
                style = AppTheme.typography.labelMedium,
                color = colors.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
