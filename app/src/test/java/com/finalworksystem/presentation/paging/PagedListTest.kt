package com.finalworksystem.presentation.paging

import androidx.paging.testing.asSnapshot
import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class PagedListTest {

    private val requests = mutableListOf<Pair<String, Int>>()

    /** 25 items per query, `"<query>-<n>"`. */
    private fun pagedList(scope: kotlinx.coroutines.CoroutineScope) =
        PagedList<String, String>(scope, pageSize = 10) { query, page, pageSize ->
            requests += query to page
            val items = (1..25).drop((page - 1) * pageSize).take(pageSize).map { "$query-$it" }
            Result.success(Page(items, totalCount = 25))
        }

    @Test
    fun `loads the pages of the query while scrolling`() = runTest {
        val list = pagedList(backgroundScope)
        list.load("works")

        val items = list.items.asSnapshot { scrollTo(index = 24) }

        assertEquals(25, items.size)
        assertEquals("works-1", items.first())
        assertEquals("works-25", items.last())
        assertEquals(listOf("works" to 1, "works" to 2, "works" to 3), requests)
    }

    @Test
    fun `reports the total count of the server`() = runTest {
        val list = pagedList(backgroundScope)

        list.totalCount.test {
            assertEquals(0, awaitItem())

            list.load("works")
            list.items.asSnapshot()

            assertEquals(25, awaitItem())
        }
    }

    @Test
    fun `new query replaces the data`() = runTest {
        val list = pagedList(backgroundScope)
        list.load("authors")
        list.items.asSnapshot()

        list.load("opponents")
        val items = list.items.asSnapshot()

        assertEquals("opponents-1", items.first())
    }

    @Test
    fun `reload fetches the same query again`() = runTest {
        val list = pagedList(backgroundScope)
        list.load("tasks")
        list.items.asSnapshot()

        list.reload("tasks")
        list.items.asSnapshot()

        assertEquals(listOf("tasks" to 1, "tasks" to 1), requests)
    }

    @Test
    fun `nothing is loaded before the first query`() = runTest {
        val list = pagedList(backgroundScope)
        list.reset()

        assertEquals(emptyList<Pair<String, Int>>(), requests)
        assertEquals(null, list.query)
    }
}
