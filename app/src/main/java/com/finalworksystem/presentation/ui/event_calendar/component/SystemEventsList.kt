package com.finalworksystem.presentation.ui.event_calendar.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.finalworksystem.R
import com.finalworksystem.domain.system_event.model.SystemEvent
import com.finalworksystem.presentation.ui.component.PagedLazyColumn
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SystemEventsList(
    events: LazyPagingItems<SystemEvent>,
    onViewedChange: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    PagedLazyColumn(
        items = events,
        emptyText = stringResource(R.string.no_unread_events),
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.Top
    ) { event ->
        var visible by remember(event.id) { mutableStateOf(true) }
        var isProcessing by remember(event.id) { mutableStateOf(false) }

        AnimatedVisibility(
            visible = visible,
            exit = shrinkVertically(
                animationSpec = tween(durationMillis = 500),
                shrinkTowards = Alignment.Top
            ) + fadeOut(
                animationSpec = tween(durationMillis = 500)
            )
        ) {
            Column(
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                SystemEventItem(
                    event = event,
                    onViewedChange = { eventId, isViewed ->
                        if (isViewed && !isProcessing) {
                            isProcessing = true
                            visible = false

                            scope.launch {
                                delay(500)
                                onViewedChange(eventId, isViewed)
                            }
                        }
                    }
                )
            }
        }
    }
}
