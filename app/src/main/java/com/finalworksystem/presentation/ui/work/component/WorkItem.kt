package com.finalworksystem.presentation.ui.work.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.finalworksystem.R
import com.finalworksystem.domain.common.util.DateUtils
import com.finalworksystem.domain.work.model.Work
import com.finalworksystem.presentation.ui.component.BaseCard
import com.finalworksystem.presentation.ui.user.component.UserAvatar
import com.finalworksystem.ui.designsystem.AppDivider
import com.finalworksystem.ui.designsystem.AppIcon
import com.finalworksystem.ui.designsystem.AppTag
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WorkItem(
    work: Work,
    onClick: () -> Unit
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography

    BaseCard(
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                AppText(
                    text = work.title,
                    style = typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.onSurface,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                AppText(
                    text = stringResource(R.string.id_prefix, work.id.toString()),
                    style = typography.labelMedium,
                    color = colors.onSurfaceVariant,
                    modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                )
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AppTag(
                    text = stringResource(R.string.type_prefix, work.type.name),
                    color = colors.primary,
                    icon = AppIcons.Article
                )
                AppTag(
                    text = stringResource(R.string.status_prefix, work.status.name),
                    color = colors.tertiary,
                    icon = AppIcons.CheckCircle
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                AppIcon(
                    imageVector = AppIcons.CalendarToday,
                    contentDescription = null,
                    tint = colors.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                AppText(
                    text = stringResource(R.string.deadline_prefix, DateUtils.formatToYmd(work.deadline)),
                    style = typography.bodySmall,
                    color = colors.onSurfaceVariant,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }

            AppDivider()

            val author = work.author
            val authorName = author?.fullName ?: author?.let { "${it.firstname} ${it.lastname}" }
                ?: stringResource(R.string.none)

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (author != null) {
                    UserAvatar(
                        firstname = author.firstname,
                        lastname = author.lastname,
                        userId = author.id,
                        modifier = Modifier.size(28.dp)
                    )
                } else {
                    AppIcon(
                        imageVector = AppIcons.Person,
                        contentDescription = null,
                        tint = colors.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column(modifier = Modifier.padding(start = 10.dp)) {
                    AppText(
                        text = stringResource(R.string.author),
                        style = typography.labelSmall,
                        color = colors.onSurfaceVariant
                    )
                    AppText(
                        text = authorName,
                        style = typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = colors.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
