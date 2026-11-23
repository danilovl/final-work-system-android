package com.finalworksystem.presentation.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource.LoadResult
import androidx.paging.testing.TestPager
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PageNumberPagingSourceTest {

    private val config = PagingConfig(pageSize = 2, initialLoadSize = 2, enablePlaceholders = false)

    /** Server with [total] items `1..total`, returning page-number pages. */
    private fun server(total: Int, reportTotal: Boolean = true): suspend (Int, Int) -> Result<Page<Int>> = { page, size ->
        val items = (1..total).drop((page - 1) * size).take(size)
        Result.success(Page(items, if (reportTotal) total else 0))
    }

    @Test
    fun `loads consecutive pages until the total is reached`() = runTest {
        var reportedTotal = -1
        val pager = TestPager(config, PageNumberPagingSource(onTotalCount = { reportedTotal = it }, loadPage = server(total = 5)))

        val first = pager.refresh() as LoadResult.Page
        val second = pager.append() as LoadResult.Page
        val third = pager.append() as LoadResult.Page

        assertEquals(listOf(1, 2), first.data)
        assertEquals(listOf(3, 4), second.data)
        assertEquals(listOf(5), third.data)
        assertNull("Last page has no next key", third.nextKey)
        assertEquals(5, reportedTotal)
    }

    @Test
    fun `without a total a full page means there may be more`() = runTest {
        val pager = TestPager(config, PageNumberPagingSource(loadPage = server(total = 4, reportTotal = false)))

        val first = pager.refresh() as LoadResult.Page
        val second = pager.append() as LoadResult.Page
        val third = pager.append() as LoadResult.Page

        assertEquals(2, first.nextKey)
        assertEquals(3, second.nextKey)
        assertTrue(third.data.isEmpty())
        assertNull(third.nextKey)
    }

    @Test
    fun `api error becomes a load error`() = runTest {
        val error = IllegalStateException("Server error")
        val pager = TestPager(config, PageNumberPagingSource<Int>(loadPage = { _, _ -> Result.failure(error) }))

        val result = pager.refresh()

        assertTrue(result is LoadResult.Error)
        assertEquals(error, (result as LoadResult.Error).throwable)
    }
}
