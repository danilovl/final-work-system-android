package com.finalworksystem.presentation.ui.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.finalworksystem.R
import com.finalworksystem.domain.user.model.UserType
import com.finalworksystem.presentation.ui.component.BaseTopAppBar
import com.finalworksystem.presentation.ui.component.PagedLazyColumn
import com.finalworksystem.presentation.ui.component.SearchModal
import com.finalworksystem.presentation.ui.component.SearchResetFloatingActionButton
import com.finalworksystem.presentation.ui.component.isRefreshing
import com.finalworksystem.presentation.ui.user.component.UserWithWorksCard
import com.finalworksystem.presentation.view_model.user.UserViewModel
import com.finalworksystem.ui.designsystem.AppScaffold

@Composable
fun UserListScreen(
    userType: String,
    userViewModel: UserViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToWorkDetail: (Int) -> Unit
) {
    val users = userViewModel.users.collectAsLazyPagingItems()
    val totalCount by userViewModel.totalCount.collectAsStateWithLifecycle()
    val searchQuery by userViewModel.searchQuery.collectAsStateWithLifecycle()
    val enumUserType = UserType.fromString(userType)
    var isSearchModalVisible by remember { mutableStateOf(false) }

    LaunchedEffect(userType) {
        enumUserType?.let { type ->
            userViewModel.loadUserList(type)
        }
    }

    val title = when (enumUserType) {
        UserType.AUTHOR -> stringResource(R.string.author)
        UserType.OPPONENT -> stringResource(R.string.opponent)
        UserType.CONSULTANT -> stringResource(R.string.consultant)
        UserType.SUPERVISOR -> stringResource(R.string.supervisor)
        else -> userType.replaceFirstChar { it.uppercase() }
    }

    AppScaffold(
        topBar = {
            BaseTopAppBar(
                title = title,
                onNavigateBack = onNavigateBack,
                onReload = { userViewModel.refresh() },
                loadedCount = users.itemCount,
                totalCount = totalCount,
                isLoading = users.isRefreshing
            )
        },
        floatingActionButton = {
            SearchResetFloatingActionButton(
                searchQuery = searchQuery,
                onSearchClick = { isSearchModalVisible = true },
                onResetClick = { userViewModel.clearSearch() }
            )
        }
    ) { paddingValues ->
        PagedLazyColumn(
            items = users,
            emptyText = stringResource(R.string.no_users_found),
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 160.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) { userWithWorks ->
            UserWithWorksCard(
                userWithWorks = userWithWorks,
                onNavigateToWorkDetail = onNavigateToWorkDetail
            )
        }
    }

    SearchModal(
        isVisible = isSearchModalVisible,
        searchQuery = searchQuery,
        onSearchQueryChange = { userViewModel.updateSearchQuery(it) },
        onSearch = { userViewModel.performSearch(it) },
        onDismiss = { isSearchModalVisible = false },
        placeholder = stringResource(R.string.search_users_placeholder)
    )
}
