package com.finalworksystem.presentation.ui.user.component

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.finalworksystem.application.use_case.user.GetCachedProfileImageUseCase
import org.koin.compose.koinInject

@Composable
fun UserAvatar(
    firstname: String,
    lastname: String,
    userId: Int,
    modifier: Modifier = Modifier,
    getCachedProfileImageUseCase: GetCachedProfileImageUseCase = koinInject()
) {
    val profileImage by getCachedProfileImageUseCase(userId).collectAsState(initial = null)

    Card(
        modifier = modifier.size(40.dp),
        shape = CircleShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (profileImage != null && profileImage!!.imageData.isNotEmpty()) {
                val bitmap = BitmapFactory.decodeByteArray(
                    profileImage!!.imageData, 
                    0, 
                    profileImage!!.imageData.size
                )
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Profile image for $firstname $lastname",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "User Avatar for $firstname $lastname",
                        tint = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User Avatar for $firstname $lastname",
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
