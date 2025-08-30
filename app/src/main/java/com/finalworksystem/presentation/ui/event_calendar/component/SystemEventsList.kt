package com.finalworksystem.presentation.ui.event_calendar.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.finalworksystem.domain.system_event.model.SystemEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SystemEventsList(
    events: List<SystemEvent>,
    hasMoreEvents: Boolean = false,
    isLoadingMore: Boolean = false,
    onViewedChange: (Int, Boolean) -> Unit,
    onLoadMore: () -> Unit = {}
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null && 
            lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - 3 &&
            hasMoreEvents && 
            !isLoadingMore
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            onLoadMore()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 8.dp)
        ) {
            items(events, key = { it.id }) { event ->
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
                        modifier = Modifier.padding(bottom = 8.dp)
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

            if (isLoadingMore) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}
