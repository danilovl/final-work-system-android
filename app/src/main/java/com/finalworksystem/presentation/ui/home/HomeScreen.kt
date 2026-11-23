package com.finalworksystem.presentation.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.finalworksystem.R
import com.finalworksystem.domain.system_event.model.SystemEventTypeEnum
import com.finalworksystem.presentation.ui.component.DoubleCheckIcon
import com.finalworksystem.presentation.ui.component.DrawerMenuButton
import com.finalworksystem.presentation.ui.component.LoadingProgressIndicator
import com.finalworksystem.presentation.ui.component.isRefreshing
import com.finalworksystem.presentation.ui.event_calendar.component.SystemEventsList
import com.finalworksystem.presentation.view_model.system_event.SystemEventViewModel
import com.finalworksystem.ui.designsystem.AppIconButton
import com.finalworksystem.ui.designsystem.AppScaffold
import com.finalworksystem.ui.designsystem.AppTopBar
import com.finalworksystem.ui.theme.AppIcons

@Composable
fun HomeScreen(systemEventViewModel: SystemEventViewModel) {
    val events = systemEventViewModel.events.collectAsLazyPagingItems()
    val totalCount by systemEventViewModel.totalCount.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        systemEventViewModel.loadSystemEvents(SystemEventTypeEnum.UNREAD)
    }

    AppScaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.system_events),
                navigationIcon = { DrawerMenuButton() },
                actions = {
                    LoadingProgressIndicator(
                        loadedCount = events.itemCount,
                        totalCount = totalCount,
                        isLoading = events.isRefreshing
                    )
                    AppIconButton(onClick = { systemEventViewModel.changeAllViewed() }) {
                        DoubleCheckIcon()
                    }
                    AppIconButton(
                        icon = AppIcons.Refresh,
                        contentDescription = stringResource(R.string.refresh),
                        onClick = { systemEventViewModel.refresh() }
                    )
                }
            )
        }
    ) { paddingValues ->
        SystemEventsList(
            events = events,
            onViewedChange = { eventId, isChecked ->
                if (isChecked) {
                    systemEventViewModel.changeViewed(eventId)
                }
            },
            modifier = Modifier.padding(paddingValues)
        )
    }
}
