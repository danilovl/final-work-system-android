package com.finalworksystem

import com.finalworksystem.application.use_case.event.GetEventByIdUseCase
import com.finalworksystem.application.use_case.event_calendar.DeleteEventUseCase
import com.finalworksystem.domain.event.model.Event
import com.finalworksystem.domain.event.model.EventType
import com.finalworksystem.domain.user.model.User
import com.finalworksystem.presentation.view_model.event.EventDetailViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class EventDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadEvent should emit success state when use case returns success`() = runTest(testDispatcher) {
        println("[DEBUG_LOG] Testing EventDetailViewModel loadEvent functionality")

        val mockUseCase = mock<GetEventByIdUseCase>()
        val mockDeleteUseCase = mock<DeleteEventUseCase>()

        val mockEventType = EventType(
            id = 1,
            name = "Test Event Type",
            description = "Test Description",
            color = "#FF0000",
            registrable = true
        )

        val mockUser = User(
            id = 1,
            username = "testuser",
            firstname = "Test",
            lastname = "User",
            fullName = "Test User",
            email = "test@example.com",
            token = "test_token",
            degreeBefore = null,
            degreeAfter = null,
            roles = listOf("ROLE_USER")
        )

        val mockEvent = Event(
            id = 123,
            name = "Test Event",
            start = "2024-01-01T10:00:00+01:00",
            end = "2024-01-01T12:00:00+01:00",
            type = mockEventType,
            owner = mockUser,
            address = null,
            participant = null,
            comment = null
        )

        whenever(mockUseCase.invoke(123)).thenReturn(
            flowOf(Result.success(mockEvent))
        )

        val viewModel = EventDetailViewModel(mockUseCase, mockDeleteUseCase)

        assertTrue("Initial state should be Loading", 
            viewModel.eventState.value is EventDetailViewModel.EventState.Loading)

        viewModel.loadEvent(123)

        advanceUntilIdle()

        val finalState = viewModel.eventState.value
        println("[DEBUG_LOG] Final state: $finalState")

        assertTrue("Final state should be Success", 
            finalState is EventDetailViewModel.EventState.Success)

        if (finalState is EventDetailViewModel.EventState.Success) {
            assertEquals("Event ID should match", 123, finalState.event.id)
            assertEquals("Event name should match", "Test Event", finalState.event.name)
            println("[DEBUG_LOG] Event loaded successfully: ${finalState.event}")
        }
    }

    @Test
    fun `loadEvent should emit error state when use case returns failure`() = runTest(testDispatcher) {
        println("[DEBUG_LOG] Testing EventDetailViewModel error handling")

        val mockUseCase = mock<GetEventByIdUseCase>()
        val mockDeleteUseCase = mock<DeleteEventUseCase>()

        val errorMessage = "Network error"

        whenever(mockUseCase.invoke(123)).thenReturn(
            flowOf(Result.failure(Exception(errorMessage)))
        )

        val viewModel = EventDetailViewModel(mockUseCase, mockDeleteUseCase)

        viewModel.loadEvent(123)

        advanceUntilIdle()

        val finalState = viewModel.eventState.value
        println("[DEBUG_LOG] Final state: $finalState")

        assertTrue("Final state should be Error", 
            finalState is EventDetailViewModel.EventState.Error)

        if (finalState is EventDetailViewModel.EventState.Error) {
            assertEquals("Error message should match", errorMessage, finalState.message)
            println("[DEBUG_LOG] Error handled correctly: ${finalState.message}")
        }
    }
}
