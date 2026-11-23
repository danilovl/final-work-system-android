package com.finalworksystem.ui.designsystem

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.finalworksystem.ui.theme.AppTheme
import com.finalworksystem.ui.theme.LocalAppContentColor
import com.finalworksystem.ui.theme.LocalAppTextStyle

/**
 * Text field with the label placed above a soft rounded input box.
 * Slots mirror the familiar API: label / placeholder / leadingIcon / trailingIcon / supportingText.
 * Set [onClick] together with [readOnly] for picker-like fields (date, dropdown).
 */
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = AppTheme.typography.bodyLarge,
    label: (@Composable () -> Unit)? = null,
    placeholder: (@Composable () -> Unit)? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    supportingText: (@Composable () -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource? = null,
    onClick: (() -> Unit)? = null
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val resolvedStyle = textStyle.merge(TextStyle(color = fieldTextColor(enabled)))

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = resolvedStyle,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        visualTransformation = visualTransformation,
        interactionSource = source,
        cursorBrush = SolidColor(if (isError) AppTheme.colors.error else AppTheme.colors.primary),
        decorationBox = { innerTextField ->
            FieldDecoration(
                isEmpty = value.isEmpty(),
                enabled = enabled,
                isError = isError,
                interactionSource = source,
                label = label,
                placeholder = placeholder,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                supportingText = supportingText,
                singleLine = singleLine,
                onClick = onClick,
                innerTextField = innerTextField
            )
        }
    )
}

@Composable
fun AppTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = AppTheme.typography.bodyLarge,
    label: (@Composable () -> Unit)? = null,
    placeholder: (@Composable () -> Unit)? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    supportingText: (@Composable () -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource? = null
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val resolvedStyle = textStyle.merge(TextStyle(color = fieldTextColor(enabled)))

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = resolvedStyle,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        visualTransformation = visualTransformation,
        interactionSource = source,
        cursorBrush = SolidColor(if (isError) AppTheme.colors.error else AppTheme.colors.primary),
        decorationBox = { innerTextField ->
            FieldDecoration(
                isEmpty = value.text.isEmpty(),
                enabled = enabled,
                isError = isError,
                interactionSource = source,
                label = label,
                placeholder = placeholder,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                supportingText = supportingText,
                singleLine = singleLine,
                onClick = null,
                innerTextField = innerTextField
            )
        }
    )
}

@Composable
private fun fieldTextColor(enabled: Boolean): Color =
    if (enabled) AppTheme.colors.onSurface else AppTheme.colors.onSurface.copy(alpha = 0.45f)

@Composable
internal fun FieldDecoration(
    isEmpty: Boolean,
    enabled: Boolean,
    isError: Boolean,
    interactionSource: MutableInteractionSource,
    label: (@Composable () -> Unit)?,
    placeholder: (@Composable () -> Unit)?,
    leadingIcon: (@Composable () -> Unit)?,
    trailingIcon: (@Composable () -> Unit)?,
    supportingText: (@Composable () -> Unit)?,
    singleLine: Boolean,
    onClick: (() -> Unit)?,
    innerTextField: @Composable () -> Unit
) {
    val c = AppTheme.colors
    val focused by interactionSource.collectIsFocusedAsState()
    val borderColor by animateColorAsState(
        when {
            isError -> c.error
            focused -> c.primary
            else -> c.outlineVariant
        },
        label = "fieldBorder"
    )
    val borderWidth by animateDpAsState(if (focused || isError) 1.5.dp else 1.dp, label = "fieldBorderWidth")
    val container = when {
        !enabled -> c.surfaceVariant.copy(alpha = 0.6f)
        focused -> c.surface
        else -> if (c.isDark) c.surfaceVariant else c.surface
    }
    val shape = RoundedCornerShape(14.dp)

    Column(modifier = Modifier.fillMaxWidth()) {
        if (label != null) {
            CompositionLocalProvider(
                LocalAppTextStyle provides AppTheme.typography.labelMedium,
                LocalAppContentColor provides if (isError) c.error else if (focused) c.primary else c.onSurfaceVariant
            ) {
                Box(Modifier.padding(start = 4.dp, bottom = 6.dp)) { label() }
            }
        }

        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 52.dp)
                    .clip(shape)
                    .background(container, shape)
                    .border(borderWidth, borderColor, shape)
                    .padding(horizontal = 14.dp, vertical = if (singleLine) 0.dp else 14.dp),
                verticalAlignment = if (singleLine) Alignment.CenterVertically else Alignment.Top
            ) {
                CompositionLocalProvider(LocalAppContentColor provides if (isError) c.error else c.onSurfaceVariant) {
                    if (leadingIcon != null) {
                        Box(Modifier.padding(end = 10.dp)) { leadingIcon() }
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = if (singleLine) 14.dp else 0.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (isEmpty && placeholder != null) {
                        CompositionLocalProvider(
                            LocalAppTextStyle provides AppTheme.typography.bodyLarge,
                            LocalAppContentColor provides c.onSurfaceVariant.copy(alpha = 0.8f)
                        ) {
                            placeholder()
                        }
                    }
                    innerTextField()
                }
                CompositionLocalProvider(LocalAppContentColor provides c.onSurfaceVariant) {
                    if (trailingIcon != null) {
                        Spacer(Modifier.width(8.dp))
                        Box { trailingIcon() }
                    }
                }
            }
            if (onClick != null) {
                Box(
                    Modifier
                        .matchParentSize()
                        .clip(shape)
                        .clickable(enabled = enabled, onClick = onClick)
                )
            }
        }

        if (supportingText != null) {
            CompositionLocalProvider(
                LocalAppTextStyle provides AppTheme.typography.bodySmall,
                LocalAppContentColor provides if (isError) c.error else c.onSurfaceVariant
            ) {
                Row(
                    modifier = Modifier.padding(start = 4.dp, top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) { supportingText() }
            }
        }
    }
}
