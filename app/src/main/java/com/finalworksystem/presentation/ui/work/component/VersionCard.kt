package com.finalworksystem.presentation.ui.work.component

import com.finalworksystem.ui.theme.AppIcons
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.finalworksystem.R
import com.finalworksystem.domain.common.util.FileUtils
import com.finalworksystem.domain.version.model.Version
import com.finalworksystem.presentation.ui.component.BaseCard
import com.finalworksystem.ui.designsystem.AppIcon
import com.finalworksystem.ui.designsystem.AppIconBadge
import com.finalworksystem.ui.designsystem.AppTag
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.theme.AppTheme

@Composable
fun VersionCard(
    version: Version,
    onClick: ((Version) -> Unit)? = null
) {
    BaseCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = if (onClick != null) { { onClick(version) } } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppIconBadge(icon = AppIcons.Article, size = 44)
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AppText(
                    text = version.name,
                    style = AppTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTheme.colors.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    AppIcon(
                        imageVector = AppIcons.Person,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = AppTheme.colors.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    AppText(
                        text = version.owner.fullName ?: "${version.owner.firstname} ${version.owner.lastname}",
                        style = AppTheme.typography.bodySmall,
                        color = AppTheme.colors.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppTag(
                        text = version.originalExtension?.uppercase() ?: stringResource(R.string.none),
                        color = AppTheme.colors.primary
                    )
                    AppTag(
                        text = FileUtils.formatFileSize(version.mediaSize),
                        color = AppTheme.colors.secondary
                    )
                }
            }
        }
    }
}
