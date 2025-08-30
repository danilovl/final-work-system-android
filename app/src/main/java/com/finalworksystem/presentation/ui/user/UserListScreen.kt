package com.finalworksystem.presentation.ui.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import com.finalworksystem.domain.user.model.UserType
import com.finalworksystem.presentation.ui.component.BaseTopAppBar
import com.finalworksystem.presentation.ui.user.component.UserWithWorksCard
import com.finalworksystem.presentation.view_model.user.UserViewModel

@Composable
fun UserListScreen(
    userType: String,
    userViewModel: UserViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToWorkDetail: (Int) -> Unit
) {
    val userListState by userViewModel.userListState.collectAsState()
    val enumUserType = UserType.fromString(userType)
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userType) {
        enumUserType?.let { type ->
            userViewModel.loadUserList(type)
        }
    }

    val shouldLoadMore = remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1

            lastVisibleItemIndex > (totalItemsNumber - 5)
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value && enumUserType != null) {
            when (val currentState = userListState) {
                is UserViewModel.UserListState.Success -> {
                    if (currentState.userListResult.currentItemCount < currentState.userListResult.totalCount) {
                        userViewModel.loadMoreUsers(enumUserType)
                    }
                }
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            BaseTopAppBar(
                title = enumUserType?.displayName ?: userType.replaceFirstChar { it.uppercase() },
                onNavigateBack = onNavigateBack,
                onReload = {
                    enumUserType?.let { type ->
                        userViewModel.loadUserList(type)
                    }
                },
                loadedCount = when (val state = userListState) {
                    is UserViewModel.UserListState.Success -> state.userListResult.currentItemCount
                    is UserViewModel.UserListState.LoadingMore -> state.currentUserListResult.currentItemCount
                    else -> 0
                },
                totalCount = when (val state = userListState) {
                    is UserViewModel.UserListState.Success -> state.userListResult.totalCount
                    is UserViewModel.UserListState.LoadingMore -> state.currentUserListResult.totalCount
                    else -> 0
                },
                isLoading = userListState is UserViewModel.UserListState.Loading
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (userListState) {
                is UserViewModel.UserListState.Idle -> {}

                is UserViewModel.UserListState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is UserViewModel.UserListState.LoadingMore -> {
                    val userListResult = (userListState as UserViewModel.UserListState.LoadingMore).currentUserListResult

                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        items(userListResult.result) { userWithWorks ->
                            UserWithWorksCard(
                                userWithWorks = userWithWorks,
                                onNavigateToWorkDetail = onNavigateToWorkDetail
                            )
                        }

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

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }

                is UserViewModel.UserListState.Success -> {
                    val userListResult = (userListState as UserViewModel.UserListState.Success).userListResult

                    if (userListResult.result.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No users found",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            items(userListResult.result) { userWithWorks ->
                                UserWithWorksCard(
                                    userWithWorks = userWithWorks,
                                    onNavigateToWorkDetail = onNavigateToWorkDetail
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }

                is UserViewModel.UserListState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Error loading users",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = (userListState as UserViewModel.UserListState.Error).message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
