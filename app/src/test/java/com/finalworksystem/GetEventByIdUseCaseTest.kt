package com.finalworksystem

import com.finalworksystem.application.use_case.event.GetEventByIdUseCase
import com.finalworksystem.domain.event.model.Event
import com.finalworksystem.domain.event.model.EventType
import com.finalworksystem.domain.event.repository.GetEventRepository
import com.finalworksystem.domain.user.model.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class GetEventByIdUseCaseTest {

    @Test
    fun `invoke should return event when repository returns success`() = runTest {
        val eventId = 96
        val expectedEvent = Event(
            id = eventId,
            name = null,
            start = "2017-08-30T09:00:00+02:00",
            end = "2017-08-30T10:00:00+02:00",
            type = EventType(
                id = 1,
                name = "Konzultace",
                description = null,
                color = "#3b91ad",
                registrable = true
            ),
            owner = User(
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
            ),
            address = null,
            comment = null,
            participant = null
        )

        val mockRepository = mock(GetEventRepository::class.java)
        `when`(mockRepository.getEvent(eventId)).thenReturn(flowOf(Result.success(expectedEvent)))

        val useCase = GetEventByIdUseCase(mockRepository)

        val result = useCase(eventId).first()

        assertTrue("[DEBUG_LOG] Result should be success", result.isSuccess)
        assertEquals("[DEBUG_LOG] Event ID should match", eventId, result.getOrNull()?.id)
        verify(mockRepository).getEvent(eventId)
    }

    @Test
    fun `invoke should return failure when repository returns failure`() = runTest {
        val eventId = 999
        val exception = Exception("Event not found")

        val mockRepository = mock(GetEventRepository::class.java)
        `when`(mockRepository.getEvent(eventId)).thenReturn(flowOf(Result.failure(exception)))

        val useCase = GetEventByIdUseCase(mockRepository)

        val result = useCase(eventId).first()

        assertTrue("[DEBUG_LOG] Result should be failure", result.isFailure)
        assertEquals("[DEBUG_LOG] Exception message should match", "Event not found", result.exceptionOrNull()?.message)
        verify(mockRepository).getEvent(eventId)
    }
}
