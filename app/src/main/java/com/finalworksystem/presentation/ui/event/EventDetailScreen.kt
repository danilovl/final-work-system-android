package com.finalworksystem.presentation.ui.event

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import com.finalworksystem.domain.event.model.Event
import com.finalworksystem.infrastructure.user.UserService
import com.finalworksystem.presentation.ui.event.component.EventActionsCard
import com.finalworksystem.presentation.ui.event.component.EventAddressCard
import com.finalworksystem.presentation.ui.event.component.EventBasicInfoCard
import com.finalworksystem.presentation.ui.event.component.EventCommentsCard
import com.finalworksystem.presentation.ui.event.component.EventWorkInfoCard
import com.finalworksystem.presentation.view_model.event.EventDetailViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    eventId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToCalendar: (() -> Unit)? = null,
    eventDetailViewModel: EventDetailViewModel = koinViewModel()
) {
    val eventState by eventDetailViewModel.eventState.collectAsState()
    val deleteState by eventDetailViewModel.deleteState.collectAsState()
    val userService = koinInject<UserService>()

    var isSupervisor by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(eventId) {
        eventDetailViewModel.loadEvent(eventId)
    }

    LaunchedEffect(Unit) {
        try {
            isSupervisor = userService.isSupervisor()
        } catch (_: Exception) {
            isSupervisor = false
        }
    }

    LaunchedEffect(deleteState) {
        when (deleteState) {
            is EventDetailViewModel.DeleteState.Success -> {
                onNavigateToCalendar?.invoke() ?: onNavigateBack()
                eventDetailViewModel.resetDeleteState()
            }
            is EventDetailViewModel.DeleteState.Error -> {
                eventDetailViewModel.resetDeleteState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Event Detail") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { eventDetailViewModel.refreshEvent(eventId) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when (eventState) {
            is EventDetailViewModel.EventState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is EventDetailViewModel.EventState.Success -> {
                val successState = eventState as EventDetailViewModel.EventState.Success
                EventDetailContent(
                    event = successState.event,
                    isSupervisor = isSupervisor,
                    isDeletingEvent = deleteState is EventDetailViewModel.DeleteState.Loading,
                    onDeleteEventClick = { showDeleteConfirmDialog = true },
                    modifier = Modifier.padding(paddingValues)
                )
            }

            is EventDetailViewModel.EventState.Error -> {
                val errorState = eventState as EventDetailViewModel.EventState.Error
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Error: ${errorState.message}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        IconButton(
                            onClick = { eventDetailViewModel.refreshEvent(eventId) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Retry"
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Delete Event") },
            text = { Text("Are you sure you want to delete this event? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmDialog = false
                        eventDetailViewModel.deleteEvent(eventId)
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun EventDetailContent(
    event: Event,
    isSupervisor: Boolean,
    isDeletingEvent: Boolean,
    onDeleteEventClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        EventBasicInfoCard(event = event)

        event.participant?.work?.let { work ->
            EventWorkInfoCard(work = work)
        }

        event.comment?.takeIf { it.isNotEmpty() }?.let { comments ->
            EventCommentsCard(comments = comments)
        }

        event.address?.let { address ->
            EventAddressCard(address = address)
        }

        EventActionsCard(
            isSupervisor = isSupervisor,
            isDeletingEvent = isDeletingEvent,
            onDeleteEventClick = onDeleteEventClick
        )
    }
}
