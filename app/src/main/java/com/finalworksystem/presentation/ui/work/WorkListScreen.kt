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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.finalworksystem.R
import com.finalworksystem.domain.work.model.WorkListType
import com.finalworksystem.presentation.ui.component.BaseTopAppBar
import com.finalworksystem.presentation.ui.component.SearchModal
import com.finalworksystem.presentation.ui.component.SearchResetFloatingActionButton
import com.finalworksystem.presentation.ui.work.component.WorksList
import com.finalworksystem.presentation.view_model.work.WorkListViewModel

@Composable
fun WorkListScreen(
    workListType: String = "author",
    workListViewModel: WorkListViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToWorkDetail: (Int) -> Unit
) {
    val worksState by workListViewModel.worksState.collectAsState()
    val searchQuery by workListViewModel.searchQuery.collectAsState()
    val workListTypeEnum = WorkListType.fromString(workListType)

    var isSearchModalVisible by remember { mutableStateOf(false) }

    LaunchedEffect(workListType) {
        workListViewModel.loadWorks(workListTypeEnum)
    }

    DisposableEffect(Unit) {
        workListViewModel.markEnteredWorkListSection()
        onDispose {
            workListViewModel.markLeftWorkListSection()
        }
    }

    val title = when (workListTypeEnum) {
        WorkListType.AUTHOR -> stringResource(R.string.author_works)
        WorkListType.OPPONENT -> stringResource(R.string.opponent_works)
        WorkListType.CONSULTANT -> stringResource(R.string.consultant_works)
        WorkListType.SUPERVISOR -> stringResource(R.string.supervisor_works_title)
    }

    Scaffold(
        topBar = {
            BaseTopAppBar(
                title = title,
                onNavigateBack = onNavigateBack,
                onReload = { workListViewModel.loadWorks(workListTypeEnum, forceRefresh = true) },
                loadedCount = (worksState as? WorkListViewModel.WorksState.Success)?.works?.size ?: 0,
                totalCount = (worksState as? WorkListViewModel.WorksState.Success)?.totalCount ?: 0,
                isLoading = worksState is WorkListViewModel.WorksState.Loading
            )
        },
        floatingActionButton = {
            SearchResetFloatingActionButton(
                searchQuery = searchQuery,
                onSearchClick = { isSearchModalVisible = true },
                onResetClick = { workListViewModel.clearSearch() }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (worksState) {
                is WorkListViewModel.WorksState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is WorkListViewModel.WorksState.Success -> {
                    val works = (worksState as WorkListViewModel.WorksState.Success).works
                    if (works.isEmpty()) {
                        Text(
                            text = stringResource(R.string.no_works_found),
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(8.dp)
                        )
                    } else {
                        WorksList(
                            works = works,
                            hasMoreWorks = (worksState as WorkListViewModel.WorksState.Success).hasMoreWorks,
                            isLoadingMore = (worksState as WorkListViewModel.WorksState.Success).isLoadingMore,
                            onWorkClick = onNavigateToWorkDetail,
                            onLoadMore = {
                                workListViewModel.loadMoreWorks()
                            }
                        )
                    }
                }
                is WorkListViewModel.WorksState.Error -> {
                    Text(
                        text = stringResource(R.string.error_prefix, (worksState as WorkListViewModel.WorksState.Error).message),
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
        onSearchQueryChange = { workListViewModel.updateSearchQuery(it) },
        onSearch = { query ->
            workListViewModel.performSearch(query)
        },
        onDismiss = { isSearchModalVisible = false }
    )
}
