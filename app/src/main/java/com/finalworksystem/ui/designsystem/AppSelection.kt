package com.finalworksystem.ui.designsystem

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.finalworksystem.ui.theme.AppTheme

@Composable
fun AppCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val c = AppTheme.colors
    val progress by animateFloatAsState(if (checked) 1f else 0f, spring(stiffness = 700f), label = "check")
    val bg by animateColorAsState(if (checked) c.primary else c.surface, label = "checkBg")
    val border by animateColorAsState(if (checked) c.primary else c.outline, label = "checkBorder")
    val shape = RoundedCornerShape(7.dp)
    val checkColor = c.onPrimary

    val toggle = if (onCheckedChange != null) {
        Modifier.toggleable(
            value = checked,
            enabled = enabled,
            role = Role.Checkbox,
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onValueChange = onCheckedChange
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .then(toggle)
            .padding(10.dp)
            .size(22.dp)
            .clip(shape)
            .background(if (enabled) bg else bg.copy(alpha = 0.4f), shape)
            .border(1.5.dp, if (enabled) border else border.copy(alpha = 0.4f), shape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.size(14.dp)) {
            if (progress <= 0f) return@Canvas
            val path = Path().apply {
                moveTo(size.width * 0.12f, size.height * 0.52f)
                lineTo(size.width * 0.40f, size.height * 0.78f)
                lineTo(size.width * 0.90f, size.height * 0.22f)
            }
            val measure = PathMeasure().apply { setPath(path, false) }
            val partial = Path()
            measure.getSegment(0f, measure.length * progress, partial, true)
            drawPath(partial, checkColor, style = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
    }
}

@Composable
fun AppRadioButton(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val c = AppTheme.colors
    val dot by animateDpAsState(if (selected) 10.dp else 0.dp, spring(dampingRatio = 0.55f, stiffness = 600f), label = "radioDot")
    val border by animateColorAsState(if (selected) c.primary else c.outline, label = "radioBorder")

    val select = if (onClick != null) {
        Modifier.selectable(
            selected = selected,
            enabled = enabled,
            role = Role.RadioButton,
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .then(select)
            .padding(10.dp)
            .size(22.dp)
            .border(2.dp, if (enabled) border else border.copy(alpha = 0.4f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .size(dot)
                .background(if (enabled) c.primary else c.primary.copy(alpha = 0.4f), CircleShape)
        )
    }
}

@Composable
fun AppSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val c = AppTheme.colors
    val track by animateColorAsState(if (checked) c.primary else c.outline, label = "switchTrack")
    val offset by animateDpAsState(if (checked) 20.dp else 0.dp, spring(dampingRatio = 0.6f, stiffness = 500f), label = "switchThumb")

    val toggle = if (onCheckedChange != null) {
        Modifier.toggleable(
            value = checked,
            enabled = enabled,
            role = Role.Switch,
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onValueChange = onCheckedChange
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .then(toggle)
            .size(width = 48.dp, height = 28.dp)
            .clip(CircleShape)
            .background(if (enabled) track else track.copy(alpha = 0.4f))
            .padding(3.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            Modifier
                .offset { androidx.compose.ui.unit.IntOffset(offset.roundToPx(), 0) }
                .size(22.dp)
                .shadow(2.dp, CircleShape)
                .background(c.surface, CircleShape)
        )
    }
}

/** Row with a checkbox and a label; the whole row is clickable. */
@Composable
fun AppCheckboxRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .clip(AppTheme.shapes.small)
            .toggleable(value = checked, enabled = enabled, role = Role.Checkbox, onValueChange = onCheckedChange)
            .padding(end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppCheckbox(checked = checked, onCheckedChange = null, enabled = enabled)
        Spacer(Modifier.width(2.dp))
        content()
    }
}

/** Row with a radio button and a label; the whole row is clickable. */
@Composable
fun AppRadioRow(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .clip(AppTheme.shapes.small)
            .selectable(selected = selected, enabled = enabled, role = Role.RadioButton, onClick = onClick)
            .padding(end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppRadioButton(selected = selected, onClick = null, enabled = enabled)
        Spacer(Modifier.width(2.dp))
        content()
    }
}
