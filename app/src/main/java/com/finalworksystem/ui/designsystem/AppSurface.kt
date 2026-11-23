package com.finalworksystem.ui.designsystem

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.finalworksystem.ui.theme.AppColors
import com.finalworksystem.ui.theme.AppIndication
import com.finalworksystem.ui.theme.AppTheme
import com.finalworksystem.ui.theme.LocalAppContentColor
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/** Returns the matching "on" color for a palette container color, or the current content color. */
@Composable
@ReadOnlyComposable
fun contentColorFor(color: Color): Color {
    val resolved = AppTheme.colors.contentColorFor(color)
    return if (resolved == Color.Unspecified) LocalAppContentColor.current else resolved
}

fun AppColors.contentColorFor(color: Color): Color = when (color) {
    primary -> onPrimary
    primaryContainer -> onPrimaryContainer
    secondary -> onSecondary
    secondaryContainer -> onSecondaryContainer
    tertiary -> onTertiary
    tertiaryContainer -> onTertiaryContainer
    background -> onBackground
    surface -> onSurface
    surfaceElevated -> onSurface
    surfaceVariant -> onSurface
    inverseSurface -> inverseOnSurface
    error -> onError
    errorContainer -> onErrorContainer
    success -> onSuccess
    successContainer -> onSuccessContainer
    warning -> onWarning
    warningContainer -> onWarningContainer
    else -> Color.Unspecified
}

/**
 * Basic container: shape, background, border, soft shadow, content color and optional click.
 */
@Composable
fun AppSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    color: Color = AppTheme.colors.surface,
    contentColor: Color = contentColorFor(color),
    border: BorderStroke? = null,
    shadowElevation: Dp = 0.dp,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    pressScale: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val shadowColor = AppTheme.colors.shadow

    CompositionLocalProvider(LocalAppContentColor provides contentColor) {
        Box(
            modifier = modifier
                .then(if (pressScale && onClick != null) Modifier.pressScale(source) else Modifier)
                .then(
                    if (shadowElevation > 0.dp) {
                        Modifier.shadow(
                            elevation = shadowElevation,
                            shape = shape,
                            clip = false,
                            ambientColor = shadowColor.copy(alpha = 0.10f),
                            spotColor = shadowColor.copy(alpha = 0.18f)
                        )
                    } else {
                        Modifier
                    }
                )
                .clip(shape)
                .background(color = color, shape = shape)
                .then(if (border != null) Modifier.border(border, shape) else Modifier)
                .then(
                    if (onClick != null) {
                        Modifier.clickable(
                            interactionSource = source,
                            indication = AppIndication,
                            enabled = enabled,
                            onClick = onClick
                        )
                    } else {
                        Modifier
                    }
                ),
            propagateMinConstraints = true
        ) {
            content()
        }
    }
}

/** Slightly shrinks the element while it is pressed (spring animation). */
fun Modifier.pressScale(interactionSource: InteractionSource, pressedScale: Float = 0.97f): Modifier =
    this.then(PressScaleElement(interactionSource, pressedScale))

private data class PressScaleElement(
    val interactionSource: InteractionSource,
    val pressedScale: Float
) : ModifierNodeElement<PressScaleNode>() {
    override fun create() = PressScaleNode(interactionSource, pressedScale)

    override fun update(node: PressScaleNode) {
        node.update(interactionSource, pressedScale)
    }
}

private class PressScaleNode(
    private var interactionSource: InteractionSource,
    private var pressedScale: Float
) : Modifier.Node(), LayoutModifierNode {

    private val scale = Animatable(1f)
    private var job: Job? = null

    override fun onAttach() {
        startCollecting()
    }

    fun update(source: InteractionSource, scaleValue: Float) {
        pressedScale = scaleValue
        if (source != interactionSource) {
            interactionSource = source
            if (isAttached) startCollecting()
        }
    }

    private fun startCollecting() {
        job?.cancel()
        job = coroutineScope.launch {
            var pressed = 0
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> pressed++
                    is PressInteraction.Release, is PressInteraction.Cancel -> pressed = (pressed - 1).coerceAtLeast(0)
                }
                val target = if (pressed > 0) pressedScale else 1f
                launch { scale.animateTo(target, spring(dampingRatio = 0.6f, stiffness = 800f)) }
            }
        }
    }

    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) {
            placeable.placeWithLayer(0, 0) {
                scaleX = scale.value
                scaleY = scale.value
            }
        }
    }
}
