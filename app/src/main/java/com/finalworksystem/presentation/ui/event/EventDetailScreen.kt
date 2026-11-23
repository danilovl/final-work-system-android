package com.finalworksystem.presentation.ui.event

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.finalworksystem.ui.theme.AppIcons
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.finalworksystem.R
import com.finalworksystem.domain.event.model.Event
import com.finalworksystem.infrastructure.user.UserService
import com.finalworksystem.presentation.ui.component.BackNavigationButton
import com.finalworksystem.presentation.ui.event.component.EventActionsCard
import com.finalworksystem.presentation.ui.event.component.EventAddressCard
import com.finalworksystem.presentation.ui.event.component.EventBasicInfoCard
import com.finalworksystem.presentation.ui.event.component.EventCommentsCard
import com.finalworksystem.presentation.ui.event.component.EventWorkInfoCard
import com.finalworksystem.presentation.view_model.event.EventDetailViewModel
import com.finalworksystem.ui.designsystem.AppAlertDialog
import com.finalworksystem.ui.designsystem.AppButton
import com.finalworksystem.ui.designsystem.AppButtonVariant
import com.finalworksystem.ui.designsystem.AppEmptyState
import com.finalworksystem.ui.designsystem.AppIcon
import com.finalworksystem.ui.designsystem.AppIconButton
import com.finalworksystem.ui.designsystem.AppLoadingState
import com.finalworksystem.ui.designsystem.AppScaffold
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.designsystem.AppTopBar
import com.finalworksystem.ui.theme.AppTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun EventDetailScreen(
    eventId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToCalendar: (() -> Unit)? = null,
    eventDetailViewModel: EventDetailViewModel = koinViewModel()
) {
    val eventState by eventDetailViewModel.eventState.collectAsStateWithLifecycle()
    val deleteState by eventDetailViewModel.deleteState.collectAsStateWithLifecycle()
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

    AppScaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.event_detail),
                navigationIcon = { BackNavigationButton(onClick = onNavigateBack) },
                actions = {
                    AppIconButton(
                        icon = AppIcons.Refresh,
                        contentDescription = stringResource(R.string.refresh),
                        onClick = { eventDetailViewModel.refreshEvent(eventId) }
                    )
                }
            )
        }
    ) { paddingValues ->
        when (eventState) {
            is EventDetailViewModel.EventState.Loading -> {
                AppLoadingState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
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
                    AppEmptyState(
                        title = stringResource(R.string.error_prefix, errorState.message),
                        icon = AppIcons.Info,
                        iconTint = AppTheme.colors.error,
                        action = {
                            AppButton(
                                text = stringResource(R.string.retry),
                                onClick = { eventDetailViewModel.refreshEvent(eventId) },
                                variant = AppButtonVariant.Secondary,
                                leadingIcon = AppIcons.Refresh
                            )
                        }
                    )
                }
            }
        }
    }

    if (showDeleteConfirmDialog) {
        AppAlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            icon = {
                AppIcon(
                    imageVector = AppIcons.Delete,
                    contentDescription = null,
                    tint = AppTheme.colors.error
                )
            },
            title = { AppText(stringResource(R.string.delete_event)) },
            text = { AppText(stringResource(R.string.delete_event_confirm)) },
            confirmButton = {
                AppButton(
                    text = stringResource(R.string.delete),
                    onClick = {
                        showDeleteConfirmDialog = false
                        eventDetailViewModel.deleteEvent(eventId)
                    },
                    variant = AppButtonVariant.Danger
                )
            },
            dismissButton = {
                AppButton(
                    text = stringResource(R.string.cancel),
                    onClick = { showDeleteConfirmDialog = false },
                    variant = AppButtonVariant.Ghost
                )
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
        verticalArrangement = Arrangement.spacedBy(12.dp)
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
