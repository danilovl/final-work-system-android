package com.finalworksystem.presentation.ui.work.component

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.finalworksystem.R
import com.finalworksystem.presentation.ui.component.BaseCard
import com.finalworksystem.presentation.view_model.work.WorkDetailViewModel
import com.finalworksystem.ui.designsystem.AppEmptyState
import com.finalworksystem.ui.designsystem.AppSpinner
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme

@Composable
fun EventTabContent(
    workDetailViewModel: WorkDetailViewModel,
    workId: Int,
    onEventClick: ((com.finalworksystem.domain.event.model.Event) -> Unit)? = null
) {
    val eventsState by workDetailViewModel.eventsState.collectAsStateWithLifecycle()

    LaunchedEffect(workId) {
        workDetailViewModel.loadEventsForWork(workId, forceRefresh = false)
    }

    when (eventsState) {
        is WorkDetailViewModel.EventsState.Loading -> {
            BaseCard(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AppSpinner()
                }
            }
        }
        is WorkDetailViewModel.EventsState.Success -> {
            val events = (eventsState as WorkDetailViewModel.EventsState.Success).events
            if (events.isEmpty()) {
                BaseCard(modifier = Modifier.fillMaxWidth()) {
                    AppEmptyState(
                        title = stringResource(R.string.no_events_available),
                        icon = AppIcons.CalendarToday,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(events) { event ->
                        EventCard(
                            event = event,
                            onClick = onEventClick
                        )
                    }
                }
            }
        }
        is WorkDetailViewModel.EventsState.Error -> {
            val errorMessage = (eventsState as WorkDetailViewModel.EventsState.Error).message
            BaseCard(modifier = Modifier.fillMaxWidth()) {
                AppEmptyState(
                    title = stringResource(R.string.error_loading_events, errorMessage),
                    icon = AppIcons.Info,
                    iconTint = AppTheme.colors.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        else -> {
        }
    }
}
