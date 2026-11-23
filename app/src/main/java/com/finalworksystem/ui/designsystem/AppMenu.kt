package com.finalworksystem.ui.designsystem

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme
import com.finalworksystem.ui.theme.LocalAppContentColor
import com.finalworksystem.ui.theme.LocalAppTextStyle

/**
 * Popup menu anchored to its parent. Place it inside a Box together with the anchor.
 */
@Composable
fun AppDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(0.dp, 6.dp),
    width: Dp? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val state = remember { MutableTransitionState(false) }
    state.targetState = expanded
    if (!state.currentState && !state.targetState) return

    val density = LocalDensity.current
    val positionProvider = remember(offset, density) {
        DropdownPositionProvider(
            with(density) { offset.x.roundToPx() },
            with(density) { offset.y.roundToPx() }
        )
    }

    Popup(
        popupPositionProvider = positionProvider,
        onDismissRequest = onDismissRequest,
        properties = PopupProperties(focusable = true)
    ) {
        AnimatedVisibility(
            visibleState = state,
            enter = fadeIn(tween(120)) + expandVertically(tween(180), expandFrom = Alignment.Top),
            exit = fadeOut(tween(100)) + shrinkVertically(tween(140), shrinkTowards = Alignment.Top)
        ) {
            AppSurface(
                modifier = modifier
                    .padding(8.dp)
                    .then(if (width != null) Modifier.width(width) else Modifier.widthIn(min = 180.dp, max = 320.dp)),
                shape = RoundedCornerShape(16.dp),
                color = AppTheme.colors.surfaceElevated,
                border = BorderStroke(1.dp, AppTheme.colors.outlineVariant),
                shadowElevation = 12.dp
            ) {
                Column(
                    modifier = Modifier
                        .heightIn(max = 360.dp)
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 6.dp),
                    content = content
                )
            }
        }
    }
}

private class DropdownPositionProvider(
    private val offsetX: Int,
    private val offsetY: Int
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        // Popup content has an 8dp outer padding (shadow room); compensate for it via offsets.
        val x = if (layoutDirection == LayoutDirection.Ltr) {
            anchorBounds.left + offsetX
        } else {
            anchorBounds.right - popupContentSize.width - offsetX
        }.coerceIn(0, (windowSize.width - popupContentSize.width).coerceAtLeast(0))

        val below = anchorBounds.bottom + offsetY
        val above = anchorBounds.top - popupContentSize.height - offsetY
        val y = if (below + popupContentSize.height <= windowSize.height || above < 0) below else above
        return IntOffset(x, y.coerceAtLeast(0))
    }
}

@Composable
fun AppDropdownMenuItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selected: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    supportingText: String? = null
) {
    val c = AppTheme.colors
    val fg = when {
        !enabled -> c.onSurface.copy(alpha = 0.38f)
        selected -> c.primary
        else -> c.onSurface
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp)
            .background(if (selected) c.primaryContainer.copy(alpha = 0.6f) else androidx.compose.ui.graphics.Color.Transparent, RoundedCornerShape(10.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompositionLocalProvider(LocalAppContentColor provides fg, LocalAppTextStyle provides AppTheme.typography.bodyLarge) {
            if (leadingIcon != null) {
                Box(Modifier.size(20.dp), contentAlignment = Alignment.Center) { leadingIcon() }
                Spacer(Modifier.width(12.dp))
            }
            Column(Modifier.weight(1f)) {
                AppText(text, maxLines = 2, overflow = TextOverflow.Ellipsis)
                if (supportingText != null) {
                    AppText(supportingText, style = AppTheme.typography.bodySmall, color = c.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            if (trailingIcon != null) {
                Spacer(Modifier.width(12.dp))
                trailingIcon()
            } else if (selected) {
                Spacer(Modifier.width(12.dp))
                AppIcon(AppIcons.Check, null, Modifier.size(18.dp))
            }
        }
    }
}

/**
 * Read-only field which opens a dropdown menu (replacement of the exposed dropdown box).
 * Menu width matches the field.
 */
@Composable
fun AppDropdownField(
    value: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    label: (@Composable () -> Unit)? = null,
    placeholder: (@Composable () -> Unit)? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    supportingText: (@Composable () -> Unit)? = null,
    isError: Boolean = false,
    enabled: Boolean = true,
    menuContent: @Composable ColumnScope.() -> Unit
) {
    var fieldWidth by remember { mutableIntStateOf(0) }
    var fieldHeightAboveBottom by remember { mutableStateOf(0) }
    val density = LocalDensity.current
    val rotation = if (expanded) 180f else 0f

    Box(modifier = modifier.onGloballyPositioned {
        fieldWidth = it.size.width
        fieldHeightAboveBottom = it.size.height
    }) {
        AppTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            singleLine = true,
            label = label,
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            isError = isError,
            supportingText = supportingText,
            trailingIcon = {
                AppIcon(AppIcons.KeyboardArrowDown, null, Modifier.rotate(rotation))
            },
            onClick = { onExpandedChange(!expanded) },
            modifier = Modifier.fillMaxWidth()
        )
        AppDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            offset = DpOffset((-8).dp, (-2).dp),
            width = with(density) { fieldWidth.toDp() },
            content = menuContent
        )
    }
}

/** Typed select field built on [AppDropdownField]. */
@Composable
fun <T> AppSelectField(
    selected: T?,
    options: List<T>,
    optionLabel: (T) -> String,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    label: (@Composable () -> Unit)? = null,
    placeholder: (@Composable () -> Unit)? = null,
    supportingText: (@Composable () -> Unit)? = null,
    isError: Boolean = false,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    AppDropdownField(
        value = selected?.let(optionLabel) ?: "",
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        supportingText = supportingText,
        isError = isError,
        enabled = enabled
    ) {
        options.forEach { option ->
            AppDropdownMenuItem(
                text = optionLabel(option),
                selected = option == selected,
                onClick = {
                    onSelect(option)
                    expanded = false
                }
            )
        }
    }
}
