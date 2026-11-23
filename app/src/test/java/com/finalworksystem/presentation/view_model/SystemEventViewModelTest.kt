package com.finalworksystem.presentation.view_model

import androidx.paging.testing.asSnapshot
import com.finalworksystem.application.use_case.system_event.GetSystemEventsWithPaginationUseCase
import com.finalworksystem.application.use_case.system_event.MarkAllSystemEventsAsViewedUseCase
import com.finalworksystem.application.use_case.system_event.MarkSystemEventAsViewedUseCase
import com.finalworksystem.domain.system_event.model.SystemEvent
import com.finalworksystem.domain.system_event.model.SystemEventTypeEnum
import com.finalworksystem.presentation.view_model.system_event.SystemEventViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SystemEventViewModelTest {

    private val events = (1..3).map { id -> mockk<SystemEvent> { every { this@mockk.id } returns id } }

    private val getEvents = mockk<GetSystemEventsWithPaginationUseCase> {
        every { this@mockk(SystemEventTypeEnum.UNREAD, 1, any()) } returns flowOf(Result.success(Triple(events, false, 3)))
    }
    private val markViewed = mockk<MarkSystemEventAsViewedUseCase> {
        every { this@mockk(any()) } returns flowOf(Result.success(true))
    }
    private val markAllViewed = mockk<MarkAllSystemEventsAsViewedUseCase> {
        every { this@mockk() } returns flowOf(Result.success(true))
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel() = SystemEventViewModel(getEvents, markViewed, markAllViewed, mockk(relaxed = true))

    @Test
    fun `viewed event disappears without reloading the list`() = runTest {
        val viewModel = viewModel()
        viewModel.loadSystemEvents()
        assertEquals(listOf(1, 2, 3), viewModel.events.asSnapshot().map { it.id })

        viewModel.changeViewed(2)
        advanceUntilIdle()

        assertEquals(listOf(1, 3), viewModel.events.asSnapshot().map { it.id })
        assertEquals(2, viewModel.totalCount.value)
    }

    @Test
    fun `all events viewed empties the list`() = runTest {
        val viewModel = viewModel()
        viewModel.loadSystemEvents()
        viewModel.events.asSnapshot()

        viewModel.changeAllViewed()
        advanceUntilIdle()

        assertEquals(emptyList<SystemEvent>(), viewModel.events.asSnapshot())
        assertEquals(0, viewModel.totalCount.value)
    }
}
