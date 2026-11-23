package com.finalworksystem.ui.designsystem

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.DragScope
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.finalworksystem.ui.theme.AppTheme
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class AppDrawerValue { Closed, Open }

/**
 * State of [AppModalDrawer]. [fraction] is 0 (closed) .. 1 (open).
 *
 * Drag gestures and open/close animations share one [MutatorMutex], so a new drag
 * cancels a running animation and a fling-settle animation is never interrupted
 * by stale drag deltas.
 */
@Stable
class AppDrawerState(initialValue: AppDrawerValue) : DraggableState {
    private val mutex = MutatorMutex()

    private var fractionState by mutableFloatStateOf(if (initialValue == AppDrawerValue.Open) 1f else 0f)
    private var target by mutableStateOf(initialValue)

    /** Drawer width in px, set by [AppModalDrawer] during layout. */
    internal var widthPx: Float = 1f

    val fraction: Float get() = fractionState

    val currentValue: AppDrawerValue get() = target

    val isOpen: Boolean get() = currentValue == AppDrawerValue.Open

    val isClosed: Boolean get() = !isOpen

    suspend fun open() = animateTo(AppDrawerValue.Open, 0f)

    suspend fun close() = animateTo(AppDrawerValue.Closed, 0f)

    internal suspend fun settle(velocityPx: Float) {
        val value = when {
            velocityPx > 800f -> AppDrawerValue.Open
            velocityPx < -800f -> AppDrawerValue.Closed
            fractionState > 0.5f -> AppDrawerValue.Open
            else -> AppDrawerValue.Closed
        }
        animateTo(value, velocityPx / widthPx)
    }

    private suspend fun animateTo(value: AppDrawerValue, initialVelocity: Float) {
        target = value
        val to = if (value == AppDrawerValue.Open) 1f else 0f
        mutex.mutate {
            animate(
                initialValue = fractionState,
                targetValue = to,
                initialVelocity = initialVelocity,
                animationSpec = spring(dampingRatio = 1f, stiffness = 450f)
            ) { v, _ -> fractionState = v.coerceIn(0f, 1f) }
        }
    }

    private val dragScope = object : DragScope {
        override fun dragBy(pixels: Float) = dispatchRawDelta(pixels)
    }

    override fun dispatchRawDelta(delta: Float) {
        fractionState = (fractionState + delta / widthPx).coerceIn(0f, 1f)
    }

    override suspend fun drag(dragPriority: MutatePriority, block: suspend DragScope.() -> Unit) {
        mutex.mutate(dragPriority) { dragScope.block() }
    }

    companion object {
        val Saver: Saver<AppDrawerState, AppDrawerValue> = Saver(
            save = { it.currentValue },
            restore = { AppDrawerState(it) }
        )
    }
}

@Composable
fun rememberAppDrawerState(initialValue: AppDrawerValue = AppDrawerValue.Closed): AppDrawerState =
    rememberSaveable(saver = AppDrawerState.Saver) { AppDrawerState(initialValue) }

/**
 * Modal side drawer sliding from the start edge over the content with a scrim.
 */
@Composable
fun AppModalDrawer(
    drawerState: AppDrawerState,
    drawerContent: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    gesturesEnabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    BoxWithConstraints(modifier.fillMaxSize()) {
        val drawerWidth = min(maxWidth * 0.84f, 340.dp)
        val drawerWidthPx = with(density) { drawerWidth.toPx() }
        drawerState.widthPx = drawerWidthPx
        val fraction = drawerState.fraction
        val visible = fraction > 0f

        Box(
            Modifier
                .fillMaxSize()
                .draggable(
                    state = drawerState,
                    orientation = Orientation.Horizontal,
                    enabled = gesturesEnabled || drawerState.isOpen,
                    reverseDirection = isRtl,
                    onDragStopped = { velocity ->
                        drawerState.settle(if (isRtl) -velocity else velocity)
                    }
                )
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        val scale = 1f - 0.04f * fraction
                        scaleX = scale
                        scaleY = scale
                    }
            ) {
                content()
            }

            if (visible) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(AppTheme.colors.scrim.copy(alpha = 0.45f * fraction))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { scope.launch { drawerState.close() } }
                )
            }

            // The drawer stays in composition while closed (just moved off-screen), so coroutines
            // launched by its items (e.g. "close, then navigate") are not cancelled when it hides.
            run {
                AppSurface(
                    modifier = Modifier
                        .width(drawerWidth)
                        .fillMaxHeight()
                        .offset { IntOffset((-(1f - drawerState.fraction) * drawerWidthPx).roundToInt(), 0) }
                        .graphicsLayer { alpha = if (drawerState.fraction > 0f) 1f else 0f }
                        .then(if (visible) Modifier else Modifier.clearAndSetSemantics { }),
                    shape = RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp),
                    color = AppTheme.colors.surfaceElevated,
                    shadowElevation = 24.dp
                ) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .navigationBarsPadding()
                            .verticalScroll(rememberScrollState())
                            .padding(vertical = 8.dp),
                        content = drawerContent
                    )
                }
            }
        }
    }
}

/** Navigation entry inside the drawer. */
@Composable
fun AppDrawerItem(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    selected: Boolean = false,
    badge: Int? = null,
    tint: Color = Color.Unspecified
) {
    val c = AppTheme.colors
    val bg by animateColorAsState(if (selected) c.primaryContainer else Color.Transparent, label = "drawerItemBg")
    val fg = when {
        tint != Color.Unspecified -> tint
        selected -> c.onPrimaryContainer
        else -> c.onSurface
    }

    AppSurface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 1.dp),
        shape = RoundedCornerShape(12.dp),
        color = bg,
        contentColor = fg,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Box(
                    Modifier
                        .size(30.dp)
                        .background(
                            if (selected) c.primary.copy(alpha = 0.16f) else c.surfaceVariant,
                            RoundedCornerShape(9.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    AppIcon(icon, null, Modifier.size(17.dp), tint = if (tint != Color.Unspecified) tint else if (selected) c.primary else c.onSurfaceVariant)
                }
                Spacer(Modifier.width(12.dp))
            }
            AppText(
                label,
                style = AppTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            if (badge != null && badge > 0) {
                AppBadge(badge, color = c.primary)
            }
        }
    }
}

/** Small uppercase caption separating drawer groups. */
@Composable
fun AppDrawerSectionTitle(text: String, modifier: Modifier = Modifier) {
    AppText(
        text = text.uppercase(),
        style = AppTheme.typography.labelSmall,
        color = AppTheme.colors.onSurfaceVariant,
        modifier = modifier.padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 4.dp)
    )
}
