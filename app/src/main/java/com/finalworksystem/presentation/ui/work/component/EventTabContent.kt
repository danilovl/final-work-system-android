package com.finalworksystem.presentation.ui.work.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.finalworksystem.presentation.ui.component.BaseCard
import com.finalworksystem.presentation.view_model.work.WorkDetailViewModel

@Composable
fun EventTabContent(
    workDetailViewModel: WorkDetailViewModel,
    workId: Int,
    onEventClick: ((com.finalworksystem.domain.event.model.Event) -> Unit)? = null
) {
    val eventsState by workDetailViewModel.eventsState.collectAsState()

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
                    CircularProgressIndicator()
                }
            }
        }
        is WorkDetailViewModel.EventsState.Success -> {
            val events = (eventsState as WorkDetailViewModel.EventsState.Success).events
            if (events.isEmpty()) {
                BaseCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "No events available",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
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
                Text(
                    text = "Error loading events: $errorMessage",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(20.dp)
                )
            }
        }
        else -> {
        }
    }
}