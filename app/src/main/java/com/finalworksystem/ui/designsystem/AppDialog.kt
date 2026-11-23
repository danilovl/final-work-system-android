package com.finalworksystem.ui.designsystem

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.first
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.finalworksystem.ui.theme.AppTheme
import com.finalworksystem.ui.theme.LocalAppContentColor
import com.finalworksystem.ui.theme.LocalAppTextStyle
import kotlin.math.roundToInt

/**
 * Generic centered dialog container with entrance animation.
 */
@Composable
fun AppDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    properties: DialogProperties = DialogProperties(),
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = properties.dismissOnBackPress,
            dismissOnClickOutside = properties.dismissOnClickOutside,
            securePolicy = properties.securePolicy,
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = properties.decorFitsSystemWindows
        )
    ) {
        val visible = remember { MutableTransitionState(false).apply { targetState = true } }
        AnimatedVisibility(
            visibleState = visible,
            enter = fadeIn(tween(160)) + scaleIn(spring(dampingRatio = 0.8f, stiffness = 500f), initialScale = 0.92f),
            exit = fadeOut() + scaleOut(targetScale = 0.95f)
        ) {
            AppSurface(
                modifier = modifier
                    .padding(horizontal = 20.dp, vertical = 24.dp)
                    .widthIn(max = 560.dp)
                    .fillMaxWidth(),
                shape = AppTheme.shapes.extraLarge,
                color = AppTheme.colors.surfaceElevated,
                shadowElevation = 24.dp
            ) {
                Column(content = content)
            }
        }
    }
}

/**
 * Alert dialog with icon, title, text and buttons (slots mirror the familiar alert API).
 */
@Composable
fun AppAlertDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: (@Composable () -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
    title: (@Composable () -> Unit)? = null,
    text: (@Composable () -> Unit)? = null,
    properties: DialogProperties = DialogProperties()
) {
    val c = AppTheme.colors
    AppDialog(onDismissRequest = onDismissRequest, modifier = modifier, properties = properties) {
        Column(modifier = Modifier.padding(24.dp)) {
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(c.primaryContainer, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    CompositionLocalProvider(LocalAppContentColor provides c.onPrimaryContainer) { icon() }
                }
                Spacer(Modifier.height(16.dp))
            }
            if (title != null) {
                CompositionLocalProvider(
                    LocalAppTextStyle provides AppTheme.typography.headlineSmall,
                    LocalAppContentColor provides c.onSurface
                ) { title() }
                Spacer(Modifier.height(10.dp))
            }
            if (text != null) {
                CompositionLocalProvider(
                    LocalAppTextStyle provides AppTheme.typography.bodyMedium,
                    LocalAppContentColor provides c.onSurfaceVariant
                ) {
                    Box(Modifier.weight(1f, fill = false)) { text() }
                }
            }
            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                dismissButton?.invoke()
                confirmButton()
            }
        }
    }
}

/**
 * Modal bottom sheet with drag-to-dismiss, scrim and slide animation.
 * [onDismissRequest] is called after the exit animation finishes.
 */
@Composable
fun AppBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    showDragHandle: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    val visible = remember { MutableTransitionState(false).apply { targetState = true } }
    val currentOnDismiss by rememberUpdatedState(onDismissRequest)
    var dragOffset by remember { mutableFloatStateOf(0f) }
    val density = LocalDensity.current
    val dismissThreshold = with(density) { 120.dp.toPx() }

    val requestClose: () -> Unit = {
        // A second request while the exit animation runs dismisses immediately, so the
        // (invisible) dialog window can never be left on screen swallowing input.
        if (!visible.targetState) currentOnDismiss() else visible.targetState = false
    }

    // Dismiss once the exit animation has fully finished. snapshotFlow observes every
    // state change, unlike effect keys which may fire while the transition is still running.
    LaunchedEffect(visible) {
        snapshotFlow { !visible.targetState && !visible.currentState && visible.isIdle }
            .first { it }
        currentOnDismiss()
    }

    Dialog(
        onDismissRequest = requestClose,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
    ) {
        val view = LocalView.current
        SideEffect { (view.parent as? DialogWindowProvider)?.window?.setDimAmount(0f) }

        BoxWithConstraints(Modifier.fillMaxSize()) {
            val maxSheetHeight = maxHeight * 0.92f
            AnimatedVisibility(
                visibleState = visible,
                enter = fadeIn(tween(220)),
                exit = fadeOut(tween(200))
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(AppTheme.colors.scrim.copy(alpha = 0.45f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = requestClose
                        )
                )
            }
            AnimatedVisibility(
                visibleState = visible,
                modifier = Modifier.align(Alignment.BottomCenter),
                enter = slideInVertically(spring(dampingRatio = 0.85f, stiffness = 380f)) { it },
                exit = slideOutVertically(tween(220)) { it }
            ) {
                AppSurface(
                    modifier = modifier
                        .fillMaxWidth()
                        .heightIn(max = maxSheetHeight)
                        .offset { IntOffset(0, dragOffset.roundToInt()) }
                        .draggable(
                            orientation = Orientation.Vertical,
                            state = rememberDraggableState { delta ->
                                dragOffset = (dragOffset + delta).coerceAtLeast(0f)
                            },
                            onDragStopped = { velocity ->
                                if (dragOffset > dismissThreshold || velocity > 2000f) {
                                    requestClose()
                                } else {
                                    androidx.compose.animation.core.animate(dragOffset, 0f) { value, _ -> dragOffset = value }
                                }
                            }
                        ),
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                    color = AppTheme.colors.surfaceElevated,
                    shadowElevation = 16.dp
                ) {
                    Column(
                        Modifier
                            .navigationBarsPadding()
                            .imePadding()
                    ) {
                        if (showDragHandle) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp, bottom = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    Modifier
                                        .size(width = 40.dp, height = 4.dp)
                                        .background(AppTheme.colors.outline, CircleShape)
                                )
                            }
                        }
                        content()
                    }
                }
            }
        }
    }
}
