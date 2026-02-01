package com.finalworksystem.presentation.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.finalworksystem.R
import com.finalworksystem.domain.system_event.model.SystemEventTypeEnum
import com.finalworksystem.presentation.ui.component.DoubleCheckIcon
import com.finalworksystem.presentation.ui.component.LoadingProgressIndicator
import com.finalworksystem.presentation.ui.event_calendar.component.SystemEventsList
import com.finalworksystem.presentation.view_model.system_event.SystemEventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(systemEventViewModel: SystemEventViewModel) {
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val systemEventsState by systemEventViewModel.systemEventsState.collectAsState()

    LaunchedEffect(Unit) {
        systemEventViewModel.loadSystemEvents(SystemEventTypeEnum.UNREAD)
    }

    LaunchedEffect(systemEventsState) {
        when (systemEventsState) {
            is SystemEventViewModel.SystemEventsState.Error -> {
                showError = true
                errorMessage = (systemEventsState as SystemEventViewModel.SystemEventsState.Error).message
            }
            else -> {
                showError = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.system_events)) },
                actions = {
                    LoadingProgressIndicator(
                        loadedCount = (systemEventsState as? SystemEventViewModel.SystemEventsState.Success)?.events?.size ?: 0,
                        totalCount = (systemEventsState as? SystemEventViewModel.SystemEventsState.Success)?.totalCount ?: 0,
                        isLoading = systemEventsState is SystemEventViewModel.SystemEventsState.Loading
                    )
                    IconButton(onClick = {
                        systemEventViewModel.changeAllViewed()
                    }) {
                        DoubleCheckIcon()
                    }
                    IconButton(onClick = {
                        systemEventViewModel.loadSystemEvents(SystemEventTypeEnum.UNREAD)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.refresh)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (systemEventsState) {
                is SystemEventViewModel.SystemEventsState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is SystemEventViewModel.SystemEventsState.Success -> {
                    val events = (systemEventsState as SystemEventViewModel.SystemEventsState.Success).events
                    if (events.isEmpty()) {
                        Text(
                            text = stringResource(R.string.no_unread_events),
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp)
                        )
                    } else {
                        SystemEventsList(
                            events = events,
                            hasMoreEvents = (systemEventsState as SystemEventViewModel.SystemEventsState.Success).hasMoreEvents,
                            isLoadingMore = (systemEventsState as SystemEventViewModel.SystemEventsState.Success).isLoadingMore,
                            onViewedChange = { eventId, isChecked ->
                                if (isChecked) {
                                    systemEventViewModel.changeViewed(eventId)
                                }
                            },
                            onLoadMore = {
                                systemEventViewModel.loadMoreEvents()
                            }
                        )
                    }
                }
                else -> {
                    // nothing
                }
            }

            if (showError) {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(text = errorMessage)
                }
            }
        }
    }
}
