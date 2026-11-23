package com.finalworksystem.ui.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.finalworksystem.ui.theme.AppTheme
import com.finalworksystem.ui.theme.LocalAppContentColor
import com.finalworksystem.ui.theme.LocalAppTextStyle

/**
 * Top bar: navigation icon, title (optionally with subtitle) and actions.
 * Handles status bar insets itself.
 */
@Composable
fun AppTopBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    containerColor: Color = AppTheme.colors.background,
    windowInsets: WindowInsets = WindowInsets.statusBars,
    showDivider: Boolean = false
) {
    val c = AppTheme.colors
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(containerColor)
            .windowInsetsPadding(windowInsets)
    ) {
        CompositionLocalProvider(LocalAppContentColor provides c.onBackground) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                navigationIcon()
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    CompositionLocalProvider(LocalAppTextStyle provides AppTheme.typography.titleLarge) {
                        title()
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    content = actions
                )
            }
        }
        if (showDivider) AppDivider()
    }
}

/** Convenience overload with a single-line text title and optional subtitle. */
@Composable
fun AppTopBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    containerColor: Color = AppTheme.colors.background
) {
    AppTopBar(
        title = {
            Column {
                AppText(title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (subtitle != null) {
                    AppText(
                        subtitle,
                        style = AppTheme.typography.bodySmall,
                        color = AppTheme.colors.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        modifier = modifier,
        navigationIcon = navigationIcon,
        actions = actions,
        containerColor = containerColor
    )
}

private enum class ScaffoldSlot { Top, Bottom, Fab, Snackbar, Content }

/**
 * Screen skeleton. [content] receives padding for the top bar / bottom bar / system bars.
 */
@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    containerColor: Color = AppTheme.colors.background,
    contentColor: Color = AppTheme.colors.onBackground,
    contentWindowInsets: WindowInsets = WindowInsets.systemBars,
    imeAware: Boolean = true,
    content: @Composable (PaddingValues) -> Unit
) {
    val imeInsets = WindowInsets.ime
    CompositionLocalProvider(LocalAppContentColor provides contentColor) {
        SubcomposeLayout(modifier = modifier.background(containerColor)) { constraints ->
            val width = constraints.maxWidth
            val height = constraints.maxHeight
            val loose = constraints.copy(minWidth = 0, minHeight = 0)

            val topPlaceables = subcompose(ScaffoldSlot.Top, topBar).map { it.measure(loose) }
            val topHeight = topPlaceables.maxOfOrNull { it.height } ?: 0

            val bottomPlaceables = subcompose(ScaffoldSlot.Bottom, bottomBar).map { it.measure(loose) }
            val bottomHeight = bottomPlaceables.maxOfOrNull { it.height } ?: 0

            val insetTop = contentWindowInsets.getTop(this)
            val insetBottom = contentWindowInsets.getBottom(this)
            val insetLeft = contentWindowInsets.getLeft(this, layoutDirection)
            val insetRight = contentWindowInsets.getRight(this, layoutDirection)

            val fabPlaceables = subcompose(ScaffoldSlot.Fab, floatingActionButton).map { it.measure(loose) }
            val fabWidth = fabPlaceables.maxOfOrNull { it.width } ?: 0
            val fabHeight = fabPlaceables.maxOfOrNull { it.height } ?: 0

            val snackPlaceables = subcompose(ScaffoldSlot.Snackbar, snackbarHost).map {
                it.measure(loose.copy(maxWidth = width - insetLeft - insetRight))
            }
            val snackHeight = snackPlaceables.maxOfOrNull { it.height } ?: 0

            // Content is lifted above the keyboard (edge-to-edge without adjustResize).
            val imeBottom = if (imeAware) imeInsets.getBottom(this) else 0
            val bottomOffset = maxOf(if (bottomPlaceables.isEmpty()) insetBottom else bottomHeight, imeBottom)
            val padding = PaddingValues(
                top = (if (topPlaceables.isEmpty()) insetTop else topHeight).toDp(),
                bottom = bottomOffset.toDp(),
                start = insetLeft.toDp(),
                end = insetRight.toDp()
            )

            val contentPlaceables = subcompose(ScaffoldSlot.Content) { content(padding) }
                .map { it.measure(loose.copy(maxHeight = height)) }

            val margin = 16.dp.roundToPx()

            layout(width, height) {
                contentPlaceables.forEach { it.place(0, 0) }
                topPlaceables.forEach { it.place(0, 0) }
                bottomPlaceables.forEach { it.place(0, height - bottomHeight) }
                val fabY = height - bottomOffset - margin - fabHeight
                fabPlaceables.forEach { it.place(width - insetRight - margin - fabWidth, fabY) }
                val snackY = if (fabPlaceables.isEmpty()) height - bottomOffset - snackHeight else fabY - snackHeight
                snackPlaceables.forEach { it.place(insetLeft + (width - insetLeft - insetRight - it.width) / 2, snackY) }
            }
        }
    }
}
