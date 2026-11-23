package com.finalworksystem.presentation.ui.user.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.finalworksystem.domain.user.model.UserWithWorks
import com.finalworksystem.presentation.ui.component.BaseCard
import com.finalworksystem.ui.designsystem.AppIcon
import com.finalworksystem.ui.designsystem.AppTag
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme

@Composable
fun UserWithWorksCard(
    userWithWorks: UserWithWorks,
    onNavigateToWorkDetail: (Int) -> Unit
) {
    val colors = AppTheme.colors
    val user = userWithWorks.user

    BaseCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            UserAvatar(
                firstname = user.firstname,
                lastname = user.lastname,
                userId = user.id,
                modifier = Modifier.size(52.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                AppText(
                    text = user.fullName ?: "${user.firstname} ${user.lastname}",
                    style = AppTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (!user.degreeBefore.isNullOrBlank() || !user.degreeAfter.isNullOrBlank()) {
                    val degree = listOfNotNull(
                        user.degreeBefore,
                        user.degreeAfter
                    ).joinToString(" ")

                    if (degree.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        AppTag(text = degree, color = colors.secondary)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (user.email.isNotBlank()) {
                    ContactLine(icon = { AppIcon(AppIcons.Email, null, Modifier.size(16.dp), tint = colors.onSurfaceVariant) }, text = user.email)
                    Spacer(modifier = Modifier.height(4.dp))
                }

                ContactLine(icon = { AppIcon(AppIcons.Person, null, Modifier.size(16.dp), tint = colors.onSurfaceVariant) }, text = user.username)

                if (userWithWorks.works.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    WorksList(
                        works = userWithWorks.works,
                        onNavigateToWorkDetail = onNavigateToWorkDetail
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactLine(icon: @Composable () -> Unit, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        icon()
        Spacer(modifier = Modifier.width(6.dp))
        AppText(
            text = text,
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
