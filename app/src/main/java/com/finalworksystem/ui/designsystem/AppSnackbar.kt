package com.finalworksystem.ui.designsystem

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.resume

enum class AppSnackbarDuration(val millis: Long) { Short(4000L), Long(10000L), Indefinite(kotlin.Long.MAX_VALUE) }

enum class AppSnackbarResult { Dismissed, ActionPerformed }

@Stable
class AppSnackbarData internal constructor(
    val message: String,
    val actionLabel: String?,
    val withDismissAction: Boolean,
    val duration: AppSnackbarDuration,
    private val continuation: CancellableContinuation<AppSnackbarResult>
) {
    fun dismiss() {
        if (continuation.isActive) continuation.resume(AppSnackbarResult.Dismissed)
    }

    fun performAction() {
        if (continuation.isActive) continuation.resume(AppSnackbarResult.ActionPerformed)
    }
}

/** Queue-like holder of the currently displayed snackbar. */
@Stable
class AppSnackbarHostState {
    private val mutex = Mutex()

    var currentSnackbarData by mutableStateOf<AppSnackbarData?>(null)
        private set

    suspend fun showSnackbar(
        message: String,
        actionLabel: String? = null,
        withDismissAction: Boolean = false,
        duration: AppSnackbarDuration = AppSnackbarDuration.Short
    ): AppSnackbarResult = mutex.withLock {
        try {
            suspendCancellableCoroutine { continuation ->
                currentSnackbarData = AppSnackbarData(message, actionLabel, withDismissAction, duration, continuation)
            }
        } finally {
            currentSnackbarData = null
        }
    }
}

@Composable
fun AppSnackbarHost(
    hostState: AppSnackbarHostState,
    modifier: Modifier = Modifier,
    snackbar: @Composable (AppSnackbarData) -> Unit = { AppSnackbar(it) }
) {
    val current = hostState.currentSnackbarData
    LaunchedEffect(current) {
        if (current != null && current.duration != AppSnackbarDuration.Indefinite) {
            delay(current.duration.millis)
            current.dismiss()
        }
    }

    AnimatedContent(
        targetState = current,
        modifier = modifier,
        transitionSpec = {
            (slideInVertically(spring(dampingRatio = 0.8f, stiffness = 400f)) { it } + fadeIn(tween(150))) togetherWith
                (slideOutVertically(tween(180)) { it / 2 } + fadeOut(tween(150)))
        },
        label = "snackbar"
    ) { data ->
        if (data != null) {
            Box(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) { snackbar(data) }
        }
    }
}

/**
 * Floating toast-like snackbar. [accentColor] tints the leading indicator (e.g. by message level).
 */
@Composable
fun AppSnackbar(
    data: AppSnackbarData,
    modifier: Modifier = Modifier,
    accentColor: Color = Color.Unspecified,
    icon: ImageVector? = null
) {
    val c = AppTheme.colors
    AppSurface(
        modifier = modifier
            .widthIn(max = 560.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = c.inverseSurface,
        contentColor = c.inverseOnSurface,
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier.padding(start = 14.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (accentColor.isSpecified) {
                Box(
                    Modifier
                        .size(30.dp)
                        .background(accentColor.copy(alpha = 0.22f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (icon != null) {
                        AppIcon(icon, null, Modifier.size(18.dp), tint = accentColor)
                    } else {
                        Box(Modifier.size(10.dp).background(accentColor, CircleShape))
                    }
                }
                Spacer(Modifier.width(12.dp))
            }
            AppText(
                text = data.message,
                style = AppTheme.typography.bodyMedium,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 10.dp)
            )
            if (data.actionLabel != null) {
                AppButton(
                    text = data.actionLabel,
                    onClick = { data.performAction() },
                    variant = AppButtonVariant.Ghost,
                    size = AppButtonSize.Small
                )
            }
            if (data.withDismissAction) {
                AppIconButton(
                    icon = AppIcons.Close,
                    contentDescription = null,
                    onClick = { data.dismiss() },
                    size = 36.dp,
                    iconSize = 18.dp
                )
            }
        }
    }
}
