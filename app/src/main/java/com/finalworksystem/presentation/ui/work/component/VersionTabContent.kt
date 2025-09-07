package com.finalworksystem.presentation.ui.work.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.finalworksystem.presentation.ui.component.BaseCard
import com.finalworksystem.presentation.view_model.work.WorkDetailViewModel

@Composable
fun VersionTabContent(
    workDetailViewModel: WorkDetailViewModel,
    workId: Int
) {
    val versionsState by workDetailViewModel.versionsState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(workId) {
        workDetailViewModel.loadVersionsForWork(workId, forceRefresh = false)
    }

    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1

            totalItemsNumber > 0 && lastVisibleItemIndex > (totalItemsNumber - 3)
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && versionsState is WorkDetailViewModel.VersionsState.Success) {
            val successState = versionsState as WorkDetailViewModel.VersionsState.Success
            if (successState.hasMoreVersions && !successState.isLoadingMore) {
                workDetailViewModel.loadMoreVersions(workId)
            }
        }
    }

    when (versionsState) {
        is WorkDetailViewModel.VersionsState.Loading -> {
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
        is WorkDetailViewModel.VersionsState.Success -> {
            val successState = versionsState as WorkDetailViewModel.VersionsState.Success
            val versions = successState.versions

            if (versions.isEmpty()) {
                BaseCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "No versions available",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(versions) { version ->
                        VersionCard(version = version)
                    }

                    if (successState.isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
        is WorkDetailViewModel.VersionsState.Error -> {
            val errorMessage = (versionsState as WorkDetailViewModel.VersionsState.Error).message
            BaseCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Error loading versions: $errorMessage",
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