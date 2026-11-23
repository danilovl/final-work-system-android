package com.finalworksystem.ui.designsystem

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.finalworksystem.ui.theme.AppTheme

data class AppTab(
    val title: String,
    val icon: ImageVector? = null,
    val badge: Int? = null
)

/**
 * Segmented pill tabs. Scrollable when [scrollable] is true, otherwise tabs share the width equally.
 */
@Composable
fun AppTabRow(
    tabs: List<AppTab>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    scrollable: Boolean = true
) {
    val c = AppTheme.colors
    val container = Modifier
        .background(c.surfaceVariant, RoundedCornerShape(16.dp))
        .padding(4.dp)

    if (scrollable) {
        Row(
            modifier = modifier
                .horizontalScroll(rememberScrollState())
                .then(container),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            tabs.forEachIndexed { index, tab ->
                TabPill(tab, selected = index == selectedTabIndex, onClick = { onTabSelected(index) })
            }
        }
    } else {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .then(container),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            tabs.forEachIndexed { index, tab ->
                TabPill(
                    tab,
                    selected = index == selectedTabIndex,
                    onClick = { onTabSelected(index) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun TabPill(
    tab: AppTab,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val c = AppTheme.colors
    val bg by animateColorAsState(if (selected) c.surface else Color.Transparent, label = "tabBg")
    val fg by animateColorAsState(if (selected) c.primary else c.onSurfaceVariant, label = "tabFg")

    AppSurface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = bg,
        contentColor = fg,
        shadowElevation = if (selected && !c.isDark) 2.dp else 0.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (tab.icon != null) {
                AppIcon(tab.icon, null, Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
            }
            AppText(tab.title, style = AppTheme.typography.labelLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (tab.badge != null && tab.badge > 0) {
                Spacer(Modifier.width(6.dp))
                Box { AppBadge(tab.badge, color = if (selected) c.primary else c.outline) }
            }
        }
    }
}
