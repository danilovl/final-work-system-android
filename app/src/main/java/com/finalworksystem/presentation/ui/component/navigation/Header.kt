package com.finalworksystem.presentation.ui.component.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.finalworksystem.domain.user.model.User
import com.finalworksystem.presentation.ui.user.component.UserAvatar
import com.finalworksystem.ui.designsystem.AppIcon
import com.finalworksystem.ui.designsystem.AppSurface
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme

@Composable
fun Header(
    user: User,
    onUserClick: () -> Unit
) {
    val c = AppTheme.colors
    AppSurface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        shape = RoundedCornerShape(22.dp),
        color = Color.Transparent,
        contentColor = c.onPrimary,
        onClick = onUserClick,
        pressScale = true
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(c.primary, c.primaryGradientEnd)))
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            UserAvatar(
                firstname = user.firstname,
                lastname = user.lastname,
                userId = user.id,
                modifier = Modifier.size(44.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                AppText(
                    text = user.fullName ?: "${user.firstname} ${user.lastname}",
                    style = AppTheme.typography.titleMedium,
                    color = c.onPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                AppText(
                    text = user.email,
                    style = AppTheme.typography.bodySmall,
                    color = c.onPrimary.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            AppIcon(AppIcons.ArrowForward, null, Modifier.size(18.dp), tint = c.onPrimary.copy(alpha = 0.8f))
        }
    }
}
