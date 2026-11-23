package com.finalworksystem.presentation.ui.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.finalworksystem.R
import com.finalworksystem.ui.designsystem.AppIconButton
import com.finalworksystem.ui.designsystem.AppIconButtonVariant
import com.finalworksystem.ui.designsystem.AppTopBar
import com.finalworksystem.ui.theme.AppIcons
import kotlinx.coroutines.launch

@Composable
fun BaseTopAppBar(
    title: String,
    onNavigateBack: () -> Unit,
    onReload: () -> Unit,
    loadedCount: Int = 0,
    totalCount: Int = 0,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier,
    additionalActions: @Composable RowScope.() -> Unit = {}
) {
    AppTopBar(
        title = title,
        modifier = modifier,
        navigationIcon = { BackNavigationButton(onClick = onNavigateBack) },
        actions = {
            LoadingProgressIndicator(
                loadedCount = loadedCount,
                totalCount = totalCount,
                isLoading = isLoading
            )
            additionalActions()
            AppIconButton(
                icon = AppIcons.Refresh,
                contentDescription = stringResource(R.string.refresh),
                onClick = onReload
            )
        }
    )
}

/** Standard "back" button for top bars. */
@Composable
fun BackNavigationButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    AppIconButton(
        icon = AppIcons.ArrowBack,
        contentDescription = stringResource(R.string.back),
        onClick = onClick,
        variant = AppIconButtonVariant.Tonal,
        modifier = modifier
    )
}

/** Opens the global navigation drawer; renders nothing when no drawer is available. */
@Composable
fun DrawerMenuButton(modifier: Modifier = Modifier) {
    val drawerState = LocalDrawerState.current ?: return
    val scope = rememberCoroutineScope()
    AppIconButton(
        icon = AppIcons.List,
        contentDescription = stringResource(R.string.open_menu),
        onClick = { scope.launch { drawerState.open() } },
        variant = AppIconButtonVariant.Tonal,
        modifier = modifier
    )
}

