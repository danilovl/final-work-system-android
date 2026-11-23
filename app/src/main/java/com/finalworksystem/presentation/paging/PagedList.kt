package com.finalworksystem.presentation.paging

import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update

/**
 * A paged list driven by a query (list type, search text, filter, ...).
 *
 * - [load] shows the list for a query; the same query keeps the already loaded pages.
 * - [reload] shows the list for a query and always fetches it again (data that changes often).
 * - [refresh] reloads the current query from the first page.
 * - [reset] forgets the data (e.g. after logout); nothing is shown until the next [load].
 *
 * The pages are cached in [scope], so they survive configuration changes and navigation.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PagedList<Q : Any, T : Any>(
    scope: CoroutineScope,
    private val pageSize: Int,
    private val loadPage: suspend (query: Q, page: Int, pageSize: Int) -> Result<Page<T>>
) {
    private data class Request<Q>(val query: Q, val generation: Int)

    private val request = MutableStateFlow<Request<Q>?>(null)

    private val _totalCount = MutableStateFlow(0)
    /** Total number of items reported by the server for the current query. */
    val totalCount: StateFlow<Int> = _totalCount.asStateFlow()

    val query: Q? get() = request.value?.query

    val items: Flow<PagingData<T>> = request
        .flatMapLatest { current ->
            if (current == null) {
                flowOf(PagingData.empty(WAITING_FOR_QUERY))
            } else {
                pager(current.query).flow
            }
        }
        .cachedIn(scope)

    fun load(query: Q) {
        if (request.value?.query != query) {
            _totalCount.value = 0
            request.value = Request(query, generation = 0)
        }
    }

    fun reload(query: Q) {
        if (request.value?.query == query) {
            refresh()
        } else {
            load(query)
        }
    }

    fun refresh() {
        request.update { current -> current?.copy(generation = current.generation + 1) }
    }

    fun reset() {
        _totalCount.value = 0
        request.value = null
    }

    /** Adjusts the total after an item was removed locally (without reloading). */
    fun adjustTotalCount(delta: Int) {
        _totalCount.update { (it + delta).coerceAtLeast(0) }
    }

    private fun pager(query: Q): Pager<Int, T> {
        val config = PagingConfig(
            pageSize = pageSize,
            initialLoadSize = pageSize,
            prefetchDistance = PREFETCH_DISTANCE.coerceAtMost(pageSize),
            enablePlaceholders = false
        )

        return Pager(config) {
            PageNumberPagingSource(
                onTotalCount = { _totalCount.value = it },
                loadPage = { page, size -> loadPage(query, page, size) }
            )
        }
    }

    private companion object {
        const val PREFETCH_DISTANCE = 3

        val WAITING_FOR_QUERY = LoadStates(
            refresh = LoadState.Loading,
            prepend = LoadState.NotLoading(endOfPaginationReached = true),
            append = LoadState.NotLoading(endOfPaginationReached = true)
        )
    }
}
