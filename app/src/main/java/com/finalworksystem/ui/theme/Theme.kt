// Theme tokens are intentionally exposed as CompositionLocals (that is how a design system theme works).
@file:Suppress("ComposeCompositionLocalUsage")

package com.finalworksystem.ui.theme

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

val LocalAppColors = staticCompositionLocalOf { LightAppColors }
val LocalAppTypography = staticCompositionLocalOf { DefaultAppTypography }
val LocalAppShapes = staticCompositionLocalOf { AppShapes() }

/** Preferred color for content (text, icons) drawn on the current container. */
val LocalAppContentColor = compositionLocalOf { Color.Black }

/** Default text style picked up by [com.finalworksystem.ui.designsystem.AppText]. */
val LocalAppTextStyle = compositionLocalOf { TextStyle.Default }

/** Entry point to the design system tokens: `AppTheme.colors`, `AppTheme.typography`, `AppTheme.shapes`. */
object AppTheme {
    val colors: AppColors
        @Composable @ReadOnlyComposable get() = LocalAppColors.current

    val typography: AppTypography
        @Composable @ReadOnlyComposable get() = LocalAppTypography.current

    val shapes: AppShapes
        @Composable @ReadOnlyComposable get() = LocalAppShapes.current
}

@Composable
fun FinalWorkSystemTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkAppColors else LightAppColors
    val selectionColors = remember(colors) {
        TextSelectionColors(handleColor = colors.primary, backgroundColor = colors.primary.copy(alpha = 0.3f))
    }

    CompositionLocalProvider(
        LocalAppColors provides colors,
        LocalAppTypography provides DefaultAppTypography,
        LocalAppShapes provides AppShapes(),
        LocalAppContentColor provides colors.onBackground,
        LocalAppTextStyle provides DefaultAppTypography.bodyMedium,
        LocalIndication provides AppIndication,
        LocalTextSelectionColors provides selectionColors,
        content = content
    )
}
