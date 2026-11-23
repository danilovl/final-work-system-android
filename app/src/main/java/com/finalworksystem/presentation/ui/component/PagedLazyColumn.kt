package com.finalworksystem.presentation.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import com.finalworksystem.R
import com.finalworksystem.ui.designsystem.AppButton
import com.finalworksystem.ui.designsystem.AppButtonSize
import com.finalworksystem.ui.designsystem.AppButtonVariant
import com.finalworksystem.ui.designsystem.AppEmptyState
import com.finalworksystem.ui.designsystem.AppLoadingState
import com.finalworksystem.ui.designsystem.AppSpinner
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme

/**
 * [LazyColumn] for [LazyPagingItems] with the standard states of a paged list:
 * first-page loading, error with retry, empty list, and a footer while the next
 * page is loading or failed. Next pages are requested automatically while scrolling.
 */
@Composable
fun <T : Any> PagedLazyColumn(
    items: LazyPagingItems<T>,
    emptyText: String,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(16.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(12.dp),
    reverseLayout: Boolean = false,
    header: (LazyListScope.() -> Unit)? = null,
    itemContent: @Composable LazyItemScope.(T) -> Unit
) {
    val refreshState = items.loadState.refresh

    when {
        items.itemCount == 0 && refreshState is LoadState.Loading -> {
            AppLoadingState(modifier = modifier.fillMaxSize())
        }

        items.itemCount == 0 && refreshState is LoadState.Error -> {
            AppEmptyState(
                title = stringResource(
                    R.string.error_prefix,
                    refreshState.error.message ?: stringResource(R.string.unknown_error)
                ),
                icon = AppIcons.Info,
                iconTint = AppTheme.colors.error,
                modifier = modifier.fillMaxSize(),
                action = {
                    AppButton(
                        text = stringResource(R.string.retry),
                        onClick = items::retry,
                        variant = AppButtonVariant.Primary,
                        leadingIcon = AppIcons.Refresh
                    )
                }
            )
        }

        items.itemCount == 0 -> {
            AppEmptyState(
                title = emptyText,
                icon = AppIcons.Article,
                modifier = modifier.fillMaxSize()
            )
        }

        else -> {
            LazyColumn(
                state = state,
                modifier = modifier.fillMaxSize(),
                contentPadding = contentPadding,
                verticalArrangement = verticalArrangement,
                reverseLayout = reverseLayout
            ) {
                header?.invoke(this)

                // Keys are not used on purpose: the API may return an item twice when data
                // changes between two page requests, and duplicate keys crash LazyColumn.
                items(
                    count = items.itemCount,
                    contentType = items.itemContentType()
                ) { index ->
                    items[index]?.let { item -> itemContent(item) }
                }

                when (val appendState = items.loadState.append) {
                    is LoadState.Loading -> item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            AppSpinner(modifier = Modifier.size(28.dp))
                        }
                    }

                    is LoadState.Error -> item {
                        PagingAppendError(
                            error = appendState.error,
                            onRetry = items::retry,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    is LoadState.NotLoading -> Unit
                }
            }
        }
    }
}

@Composable
private fun PagingAppendError(
    error: Throwable,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AppText(
                text = stringResource(
                    R.string.error_prefix,
                    error.message ?: stringResource(R.string.unknown_error)
                ),
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.error,
                textAlign = TextAlign.Center
            )
            AppButton(
                text = stringResource(R.string.retry),
                onClick = onRetry,
                variant = AppButtonVariant.Outline,
                size = AppButtonSize.Small,
                leadingIcon = AppIcons.Refresh
            )
        }
    }
}

/** True while the first page of the list is being loaded. */
val LazyPagingItems<*>.isRefreshing: Boolean
    get() = loadState.refresh is LoadState.Loading
