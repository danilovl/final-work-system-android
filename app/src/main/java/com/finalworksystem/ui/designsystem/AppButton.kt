package com.finalworksystem.ui.designsystem

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.finalworksystem.ui.theme.AppIndication
import com.finalworksystem.ui.theme.AppTheme
import com.finalworksystem.ui.theme.LocalAppContentColor
import com.finalworksystem.ui.theme.LocalAppTextStyle

enum class AppButtonVariant {
    /** Gradient accent button for the main action. */
    Primary,

    /** Soft tinted button for secondary actions. */
    Secondary,

    /** Transparent button with a border. */
    Outline,

    /** Text-only button (former TextButton). */
    Ghost,

    /** Destructive action. */
    Danger,

    /** Soft destructive action. */
    DangerSoft
}

enum class AppButtonSize(val height: Dp, val horizontalPadding: Dp, val iconSize: Dp) {
    Small(36.dp, 14.dp, 16.dp),
    Medium(46.dp, 18.dp, 18.dp),
    Large(54.dp, 22.dp, 20.dp)
}

private data class ButtonPalette(val background: Brush?, val content: Color, val border: BorderStroke?)

@Composable
private fun buttonPalette(variant: AppButtonVariant, enabled: Boolean): ButtonPalette {
    val c = AppTheme.colors
    if (!enabled) {
        return when (variant) {
            AppButtonVariant.Ghost -> ButtonPalette(null, c.onSurface.copy(alpha = 0.38f), null)
            AppButtonVariant.Outline -> ButtonPalette(null, c.onSurface.copy(alpha = 0.38f), BorderStroke(1.dp, c.outlineVariant))
            else -> ButtonPalette(SolidColor(c.onSurface.copy(alpha = 0.08f)), c.onSurface.copy(alpha = 0.38f), null)
        }
    }
    return when (variant) {
        AppButtonVariant.Primary -> ButtonPalette(
            Brush.linearGradient(listOf(c.primary, c.primaryGradientEnd)),
            c.onPrimary,
            null
        )
        AppButtonVariant.Secondary -> ButtonPalette(SolidColor(c.primaryContainer), c.onPrimaryContainer, null)
        AppButtonVariant.Outline -> ButtonPalette(null, c.onSurface, BorderStroke(1.dp, c.outline))
        AppButtonVariant.Ghost -> ButtonPalette(null, c.primary, null)
        AppButtonVariant.Danger -> ButtonPalette(SolidColor(c.error), c.onError, null)
        AppButtonVariant.DangerSoft -> ButtonPalette(SolidColor(c.errorContainer), c.onErrorContainer, null)
    }
}

/**
 * Button with a custom slot content.
 */
@Composable
fun AppButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: AppButtonVariant = AppButtonVariant.Primary,
    size: AppButtonSize = AppButtonSize.Medium,
    loading: Boolean = false,
    shape: Shape = RoundedCornerShape(14.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = size.horizontalPadding),
    content: @Composable RowScope.() -> Unit
) {
    val palette = buttonPalette(variant, enabled)
    val interactionSource = remember { MutableInteractionSource() }
    val isElevated = enabled && variant == AppButtonVariant.Primary
    val shadowColor = AppTheme.colors.primary

    Box(
        modifier = modifier
            .pressScale(interactionSource)
            .then(
                if (isElevated && !AppTheme.colors.isDark) {
                    Modifier.shadow(8.dp, shape, clip = false, ambientColor = shadowColor.copy(alpha = 0.25f), spotColor = shadowColor.copy(alpha = 0.35f))
                } else {
                    Modifier
                }
            )
            .clip(shape)
            .then(if (palette.background != null) Modifier.background(palette.background, shape) else Modifier)
            .then(if (palette.border != null) Modifier.border(palette.border, shape) else Modifier)
            .clickable(
                interactionSource = interactionSource,
                indication = AppIndication,
                enabled = enabled && !loading,
                role = Role.Button,
                onClick = onClick
            )
            .defaultMinSize(minWidth = 64.dp, minHeight = size.height)
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(
            LocalAppContentColor provides palette.content,
            LocalAppTextStyle provides AppTheme.typography.labelLarge
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                AnimatedVisibility(
                    visible = loading,
                    enter = fadeIn() + expandHorizontally(),
                    exit = fadeOut() + shrinkHorizontally()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AppSpinner(modifier = Modifier.size(size.iconSize), color = palette.content, strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp))
                    }
                }
                content()
            }
        }
    }
}

/**
 * Text button with optional icons.
 */
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: AppButtonVariant = AppButtonVariant.Primary,
    size: AppButtonSize = AppButtonSize.Medium,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    loading: Boolean = false
) {
    AppButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        variant = variant,
        size = size,
        loading = loading
    ) {
        if (leadingIcon != null && !loading) {
            AppIcon(imageVector = leadingIcon, contentDescription = null, modifier = Modifier.size(size.iconSize))
            Spacer(Modifier.width(8.dp))
        }
        AppText(text = text, maxLines = 1, overflow = TextOverflow.Ellipsis)
        if (trailingIcon != null) {
            Spacer(Modifier.width(8.dp))
            AppIcon(imageVector = trailingIcon, contentDescription = null, modifier = Modifier.size(size.iconSize))
        }
    }
}

enum class AppIconButtonVariant { Plain, Tonal, Filled, Outlined }

@Composable
fun AppIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: AppIconButtonVariant = AppIconButtonVariant.Plain,
    size: Dp = 42.dp,
    contentColor: Color = Color.Unspecified,
    containerColor: Color = Color.Unspecified,
    content: @Composable () -> Unit
) {
    val c = AppTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val (bg, fg) = when (variant) {
        AppIconButtonVariant.Plain -> Color.Transparent to LocalAppContentColor.current
        AppIconButtonVariant.Tonal -> c.surfaceVariant to c.onSurface
        AppIconButtonVariant.Filled -> c.primary to c.onPrimary
        AppIconButtonVariant.Outlined -> Color.Transparent to c.onSurface
    }
    val resolvedBg = if (containerColor.isSpecified) containerColor else bg
    val resolvedFg = (if (contentColor.isSpecified) contentColor else fg).let { if (enabled) it else it.copy(alpha = 0.38f) }
    val shape = RoundedCornerShape(size * 0.36f)

    Box(
        modifier = modifier
            .pressScale(interactionSource, 0.9f)
            .size(size)
            .clip(shape)
            .background(resolvedBg, shape)
            .then(if (variant == AppIconButtonVariant.Outlined) Modifier.border(1.dp, c.outlineVariant, shape) else Modifier)
            .clickable(
                interactionSource = interactionSource,
                indication = AppIndication,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(LocalAppContentColor provides resolvedFg, content = content)
    }
}

@Composable
fun AppIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: AppIconButtonVariant = AppIconButtonVariant.Plain,
    size: Dp = 42.dp,
    iconSize: Dp = 22.dp,
    tint: Color = Color.Unspecified,
    containerColor: Color = Color.Unspecified
) {
    AppIconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        variant = variant,
        size = size,
        contentColor = tint,
        containerColor = containerColor
    ) {
        AppIcon(imageVector = icon, contentDescription = contentDescription, modifier = Modifier.size(iconSize))
    }
}

/**
 * Floating action button. Shows [text] next to the icon when provided (extended FAB).
 */
@Composable
fun AppFab(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    text: String? = null,
    containerColor: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified,
    small: Boolean = false
) {
    val c = AppTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(if (small) 14.dp else 18.dp)
    val background: Brush = if (containerColor.isSpecified) SolidColor(containerColor) else Brush.linearGradient(listOf(c.primary, c.primaryGradientEnd))
    val fg = when {
        contentColor.isSpecified -> contentColor
        containerColor.isSpecified -> c.contentColorFor(containerColor).let { if (it.isSpecified) it else c.onSurface }
        else -> c.onPrimary
    }
    val shadowTint = if (containerColor.isSpecified) c.shadow else c.primary
    val dimension = if (small) 44.dp else 58.dp

    Box(
        modifier = modifier
            .pressScale(interactionSource, 0.92f)
            .shadow(if (small) 6.dp else 12.dp, shape, clip = false, ambientColor = shadowTint.copy(alpha = 0.3f), spotColor = shadowTint.copy(alpha = 0.4f))
            .clip(shape)
            .background(background, shape)
            .clickable(
                interactionSource = interactionSource,
                indication = AppIndication,
                role = Role.Button,
                onClick = onClick
            )
            .defaultMinSize(minWidth = dimension, minHeight = dimension)
            .padding(horizontal = if (text != null) 20.dp else 0.dp),
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(
            LocalAppContentColor provides fg,
            LocalAppTextStyle provides AppTheme.typography.labelLarge
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AppIcon(imageVector = icon, contentDescription = contentDescription, modifier = Modifier.size(if (small) 20.dp else 24.dp))
                if (text != null) {
                    Spacer(Modifier.width(10.dp))
                    AppText(text = text, maxLines = 1)
                }
            }
        }
    }
}
