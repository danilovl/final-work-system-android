package com.finalworksystem.presentation.ui.work

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.finalworksystem.domain.work.model.WorkListType
import com.finalworksystem.presentation.ui.component.BaseTopAppBar
import com.finalworksystem.presentation.ui.component.SearchModal
import com.finalworksystem.presentation.ui.component.SearchResetFloatingActionButton
import com.finalworksystem.presentation.ui.work.component.WorksList
import com.finalworksystem.presentation.view_model.work.WorkViewModel

@Composable
fun WorkListScreen(
    workListType: String = "author",
    workViewModel: WorkViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToWorkDetail: (Int) -> Unit
) {
    val worksState by workViewModel.worksState.collectAsState()
    val searchQuery by workViewModel.searchQuery.collectAsState()
    val workListTypeEnum = WorkListType.fromString(workListType)

    var isSearchModalVisible by remember { mutableStateOf(false) }

    LaunchedEffect(workListType) {
        workViewModel.loadWorks(workListTypeEnum)
    }

    DisposableEffect(Unit) {
        workViewModel.markEnteredWorkListSection()
        onDispose {
            workViewModel.markLeftWorkListSection()
        }
    }

    Scaffold(
        topBar = {
            BaseTopAppBar(
                title = "${workListTypeEnum.value.replaceFirstChar { it.uppercase() }} works",
                onNavigateBack = onNavigateBack,
                onReload = { workViewModel.loadWorks(workListTypeEnum, forceRefresh = true) },
                loadedCount = (worksState as? WorkViewModel.WorksState.Success)?.works?.size ?: 0,
                totalCount = (worksState as? WorkViewModel.WorksState.Success)?.totalCount ?: 0,
                isLoading = worksState is WorkViewModel.WorksState.Loading
            )
        },
        floatingActionButton = {
            SearchResetFloatingActionButton(
                searchQuery = searchQuery,
                onSearchClick = { isSearchModalVisible = true },
                onResetClick = { workViewModel.clearSearch() }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (worksState) {
                is WorkViewModel.WorksState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is WorkViewModel.WorksState.Success -> {
                    val works = (worksState as WorkViewModel.WorksState.Success).works
                    if (works.isEmpty()) {
                        Text(
                            text = "No works found",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(8.dp)
                        )
                    } else {
                        WorksList(
                            works = works,
                            hasMoreWorks = (worksState as WorkViewModel.WorksState.Success).hasMoreWorks,
                            isLoadingMore = (worksState as WorkViewModel.WorksState.Success).isLoadingMore,
                            onWorkClick = onNavigateToWorkDetail,
                            onLoadMore = {
                                workViewModel.loadMoreWorks()
                            }
                        )
                    }
                }
                is WorkViewModel.WorksState.Error -> {
                    Text(
                        text = "Error: ${(worksState as WorkViewModel.WorksState.Error).message}",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(8.dp),
                        color = androidx.compose.material3.MaterialTheme.colorScheme.error
                    )
                }
                else -> {
                    // nothing
                }
            }
        }
    }

    SearchModal(
        isVisible = isSearchModalVisible,
        searchQuery = searchQuery,
        onSearchQueryChange = { workViewModel.updateSearchQuery(it) },
        onSearch = { query ->
            workViewModel.performSearch(query)
        },
        onDismiss = { isSearchModalVisible = false }
    )
}
