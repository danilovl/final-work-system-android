package com.finalworksystem.presentation.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState

/** One page returned by the API: the items and the total number of items of the whole list. */
data class Page<T>(
    val items: List<T>,
    val totalCount: Int
)

/**
 * [PagingSource] for the page-number pagination of the API (`page` starts at 1).
 *
 * The next page is requested while fewer than `totalCount` items have been loaded.
 * When the server does not send a total, a full page means there may be more.
 */
class PageNumberPagingSource<T : Any>(
    private val onTotalCount: (Int) -> Unit = {},
    private val loadPage: suspend (page: Int, pageSize: Int) -> Result<Page<T>>
) : PagingSource<Int, T>() {

    // Page numbers shift when data changes on the server, so a refresh always starts from the top.
    override fun getRefreshKey(state: PagingState<Int, T>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val page = params.key ?: FIRST_PAGE
        val pageSize = params.loadSize

        return loadPage(page, pageSize).fold(
            onSuccess = { result ->
                if (page == FIRST_PAGE) {
                    onTotalCount(result.totalCount)
                }

                LoadResult.Page(
                    data = result.items,
                    prevKey = null,
                    nextKey = if (hasNextPage(page, pageSize, result)) page + 1 else null
                )
            },
            onFailure = { LoadResult.Error(it) }
        )
    }

    private fun hasNextPage(page: Int, pageSize: Int, result: Page<T>): Boolean {
        if (result.items.isEmpty()) {
            return false
        }

        if (result.totalCount > 0) {
            return (page - 1) * pageSize + result.items.size < result.totalCount
        }

        return result.items.size >= pageSize
    }

    companion object {
        const val FIRST_PAGE = 1
    }
}
