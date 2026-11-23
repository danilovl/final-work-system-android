package com.finalworksystem.presentation.ui.user.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.finalworksystem.R
import com.finalworksystem.infrastructure.image.ProfileImageCache
import com.finalworksystem.ui.designsystem.AppIcon
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme

@Composable
fun UserAvatar(
    firstname: String,
    lastname: String,
    userId: Int,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val imageUrl = remember(userId) { ProfileImageCache.url(userId) }
    var isImageLoaded by remember(userId) { mutableStateOf(false) }
    val placeholderBrush = Brush.linearGradient(
        listOf(
            colors.primary.copy(alpha = if (colors.isDark) 0.35f else 0.18f),
            colors.primaryGradientEnd.copy(alpha = if (colors.isDark) 0.25f else 0.10f)
        )
    )

    BoxWithConstraints(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(placeholderBrush, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // Placeholder stays visible while loading and when the user has no image.
        if (!isImageLoaded) {
            AppIcon(
                imageVector = AppIcons.Person,
                contentDescription = stringResource(R.string.user_avatar_for, "$firstname $lastname"),
                tint = colors.primary,
                modifier = Modifier.size(maxWidth * 0.55f)
            )
        }

        AsyncImage(
            model = imageUrl,
            contentDescription = stringResource(R.string.profile_image_for, "$firstname $lastname"),
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            onSuccess = { isImageLoaded = true },
            onError = { isImageLoaded = false }
        )
    }
}
