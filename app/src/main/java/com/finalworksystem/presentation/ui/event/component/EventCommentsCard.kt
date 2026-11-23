package com.finalworksystem.presentation.ui.event.component

import com.finalworksystem.presentation.ui.component.HtmlText
import com.finalworksystem.ui.theme.AppIcons
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finalworksystem.R
import com.finalworksystem.domain.event.model.Comment
import com.finalworksystem.presentation.ui.component.BaseCard
import com.finalworksystem.ui.designsystem.AppSectionHeader
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.theme.AppTheme

@Composable
fun EventCommentsCard(comments: List<Comment>) {
    BaseCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AppSectionHeader(
                title = stringResource(R.string.comments_count, comments.size),
                icon = AppIcons.Comment
            )

            comments.forEach { comment ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AppTheme.colors.surfaceVariant, AppTheme.shapes.small)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    AppText(
                        text = comment.owner.fullName ?: "${comment.owner.firstname} ${comment.owner.lastname}",
                        style = AppTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = AppTheme.colors.primary
                    )
                    HtmlText(
                        html = comment.content,
                        modifier = Modifier.fillMaxWidth(),
                        textColor = AppTheme.colors.onSurface,
                        textSize = 14f
                    )
                }
            }
        }
    }
}
