package com.finalworksystem.presentation.ui.work

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.finalworksystem.ui.theme.AppIcons
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import com.finalworksystem.ui.designsystem.AppFab
import com.finalworksystem.ui.designsystem.AppScaffold
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
import com.finalworksystem.domain.user.model.UserRole
import com.finalworksystem.domain.work.model.WorkListType
import com.finalworksystem.infrastructure.user.UserService
import com.finalworksystem.presentation.ui.component.BaseTopAppBar
import com.finalworksystem.presentation.ui.component.SearchResetFloatingActionButton
import com.finalworksystem.presentation.ui.component.isRefreshing
import androidx.paging.compose.collectAsLazyPagingItems
import com.finalworksystem.presentation.ui.work.component.WorkFilterDialog
import com.finalworksystem.presentation.ui.work.component.WorksList
import com.finalworksystem.presentation.view_model.work.WorkListViewModel
import org.koin.compose.koinInject

@Composable
fun WorkListScreen(
    workListType: String = "author",
    workListViewModel: WorkListViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToWorkDetail: (Int) -> Unit,
    onNavigateToWorkCreate: (() -> Unit)? = null
) {
    val userService: UserService = koinInject()
    val currentUser by userService.getUserFlowSafe().collectAsStateWithLifecycle(initialValue = null)
    val isSupervisor = currentUser?.roles?.contains(UserRole.SUPERVISOR.value) == true

    val works = workListViewModel.works.collectAsLazyPagingItems()
    val totalCount by workListViewModel.totalCount.collectAsStateWithLifecycle()
    val filter by workListViewModel.filter.collectAsStateWithLifecycle()
    val workTypes by workListViewModel.workTypes.collectAsStateWithLifecycle()
    val workStatuses by workListViewModel.workStatuses.collectAsStateWithLifecycle()
    val selectedAuthor by workListViewModel.selectedAuthor.collectAsStateWithLifecycle()
    val selectedOpponent by workListViewModel.selectedOpponent.collectAsStateWithLifecycle()
    val workListTypeEnum = WorkListType.fromString(workListType)

    var isSearchModalVisible by remember { mutableStateOf(false) }

    LaunchedEffect(workListType) {
        workListViewModel.loadWorks(workListTypeEnum)
    }

    val title = when (workListTypeEnum) {
        WorkListType.AUTHOR -> stringResource(R.string.author_works)
        WorkListType.OPPONENT -> stringResource(R.string.opponent_works)
        WorkListType.CONSULTANT -> stringResource(R.string.consultant_works)
        WorkListType.SUPERVISOR -> stringResource(R.string.supervisor_works_title)
    }

    AppScaffold(
        topBar = {
            BaseTopAppBar(
                title = title,
                onNavigateBack = onNavigateBack,
                onReload = { workListViewModel.refresh() },
                loadedCount = works.itemCount,
                totalCount = totalCount,
                isLoading = works.isRefreshing
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (onNavigateToWorkCreate != null && isSupervisor) {
                    AppFab(
                        onClick = onNavigateToWorkCreate,
                        icon = AppIcons.Add,
                        contentDescription = stringResource(R.string.create_work)
                    )
                }
                SearchResetFloatingActionButton(
                    searchQuery = if (filter.isEmpty()) "" else "filtered",
                    onSearchClick = { isSearchModalVisible = true },
                    onResetClick = { workListViewModel.clearFilter() }
                )
            }
        }
    ) { paddingValues ->
        WorksList(
            works = works,
            onWorkClick = onNavigateToWorkDetail,
            modifier = Modifier.padding(paddingValues)
        )
    }

    WorkFilterDialog(
        isVisible = isSearchModalVisible,
        initialFilter = filter,
        workTypes = workTypes,
        workStatuses = workStatuses,
        userPicker = workListViewModel.userPicker,
        selectedAuthor = selectedAuthor,
        selectedOpponent = selectedOpponent,
        onAuthorSelected = { workListViewModel.selectedAuthor.value = it },
        onOpponentSelected = { workListViewModel.selectedOpponent.value = it },
        onApplyFilter = { workListViewModel.updateFilter(it) },
        onResetFilter = { workListViewModel.clearFilter() },
        onDismiss = { isSearchModalVisible = false }
    )
}
