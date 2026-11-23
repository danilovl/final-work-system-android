package com.finalworksystem.ui.designsystem

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.finalworksystem.ui.theme.AppTheme

/** Indeterminate circular loader. Default size is 32dp. */
@Composable
fun AppSpinner(
    modifier: Modifier = Modifier,
    color: Color = AppTheme.colors.primary,
    strokeWidth: Dp = 3.dp,
    trackColor: Color = color.copy(alpha = 0.14f)
) {
    val transition = rememberInfiniteTransition(label = "spinner")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 900, easing = LinearEasing)),
        label = "rotation"
    )
    val sweep by transition.animateFloat(
        initialValue = 40f,
        targetValue = 40f,
        animationSpec = infiniteRepeatable(
            keyframes {
                durationMillis = 1400
                40f at 0
                260f at 700
                40f at 1400
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep"
    )

    Canvas(modifier = modifier.size(32.dp)) {
        val stroke = strokeWidth.toPx()
        val inset = stroke / 2
        val arcSize = androidx.compose.ui.geometry.Size(size.width - stroke, size.height - stroke)
        val topLeft = androidx.compose.ui.geometry.Offset(inset, inset)
        drawArc(trackColor, 0f, 360f, false, topLeft, arcSize, style = Stroke(stroke))
        drawArc(color, rotation - 90f, sweep, false, topLeft, arcSize, style = Stroke(stroke, cap = StrokeCap.Round))
    }
}

/**
 * Linear progress bar. When [progress] is null the bar is indeterminate.
 */
@Composable
fun AppLinearProgress(
    modifier: Modifier = Modifier,
    progress: Float? = null,
    color: Color = AppTheme.colors.primary,
    trackColor: Color = color.copy(alpha = 0.14f),
    height: Dp = 4.dp
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(CircleShape)
            .background(trackColor)
    ) {
        if (progress != null) {
            val animated by animateFloatAsState(progress.coerceIn(0f, 1f), label = "progress")
            Box(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animated)
                    .clip(CircleShape)
                    .background(color)
            )
        } else {
            val transition = rememberInfiniteTransition(label = "linear")
            val position by transition.animateFloat(
                initialValue = -0.4f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing)),
                label = "position"
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .drawWithContent {
                        val w = size.width * 0.4f
                        drawRoundRect(
                            color = color,
                            topLeft = androidx.compose.ui.geometry.Offset(position * size.width, 0f),
                            size = androidx.compose.ui.geometry.Size(w, size.height),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.height / 2)
                        )
                    }
            )
        }
    }
}

/** Centered loader that fills available space, with an optional message. */
@Composable
fun AppLoadingState(
    modifier: Modifier = Modifier,
    message: String? = null
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppSpinner(modifier = Modifier.size(40.dp))
            if (message != null) {
                AppText(
                    text = message,
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
        }
    }
}
