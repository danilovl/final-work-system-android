package com.finalworksystem

import com.finalworksystem.data.conversation.model.response.ConversationWorkResponseDto
import com.finalworksystem.data.task.model.response.TaskResponse
import com.finalworksystem.data.user.model.response.UserListResponse
import com.finalworksystem.data.work.model.response.WorkListResponse
import com.finalworksystem.data.work.model.response.WorkResponse
import com.finalworksystem.data.work.model.response.toDomainModel
import com.finalworksystem.infrastructure.api.serialization.ApiJson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Covers the custom unwrapping serializers that replaced the Gson adapters. */
class WorkResponseDeserializationTest {

    @Test
    fun `WorkResponse wrapped in result is unwrapped`() {
        val json = """
            {
                "success": true,
                "result": {
                    "id": 131,
                    "title": "rest",
                    "shortcut": "REST-1",
                    "deadline": "2026-10-01",
                    "deadlineProgram": "2026-10-15"
                }
            }
        """.trimIndent()

        val response = ApiJson.decodeFromString<WorkResponse>(json)
        assertEquals(131, response.id)
        assertEquals("rest", response.title)

        val domainWork = response.toDomainModel()
        assertEquals(131, domainWork.id)
        assertEquals("rest", domainWork.title)
    }

    @Test
    fun `WorkResponse as direct object is parsed`() {
        val json = """{"id": 131, "title": "rest", "shortcut": "REST-1", "deadline": "2026-10-01"}"""

        val response = ApiJson.decodeFromString<WorkResponse>(json)
        assertEquals(131, response.id)
        assertEquals("rest", response.title)
        assertEquals(131, response.toDomainModel().id)
    }

    @Test
    fun `WorkResponse wrapped in work or data is unwrapped`() {
        val wrappedInWork = ApiJson.decodeFromString<WorkResponse>("""{"work": {"id": 7, "title": "w"}}""")
        val wrappedInData = ApiJson.decodeFromString<WorkResponse>("""{"data": {"id": 8, "title": "d"}}""")

        assertEquals(7, wrappedInWork.id)
        assertEquals(8, wrappedInData.id)
    }

    @Test
    fun `WorkListResponse parses items in result`() {
        val json = """
            {
                "count": 1,
                "totalCount": 1,
                "success": true,
                "result": [
                    {"id": 131, "title": "rest", "shortcut": "REST-1"}
                ]
            }
        """.trimIndent()

        val response = ApiJson.decodeFromString<WorkListResponse>(json)
        assertEquals(1, response.result.size)
        assertEquals(131, response.result[0].id)
        assertEquals("rest", response.result[0].title)
    }

    @Test
    fun `TaskResponse wrapped in task is unwrapped`() {
        val json = """{"task": {"id": 5, "name": "Write chapter", "complete": true}}"""

        val response = ApiJson.decodeFromString<TaskResponse>(json)
        assertEquals(5, response.id)
        assertEquals("Write chapter", response.name)
        assertEquals(true, response.complete)
    }

    @Test
    fun `ConversationWorkResponseDto wrapped in result is unwrapped with defaults`() {
        val json = """
            {
                "success": true,
                "result": {"id": 42, "name": "Conversation for work 131", "isRead": true}
            }
        """.trimIndent()

        val dto = ApiJson.decodeFromString<ConversationWorkResponseDto>(json)
        assertEquals(42, dto.id)
        assertEquals("Conversation for work 131", dto.name)
        assertTrue(dto.participants.isEmpty())
        assertEquals(0, dto.type.id)
    }

    @Test
    fun `user list accepts both wrapped and bare user objects`() {
        val json = """
            {
                "numItemsPerPage": 2,
                "totalCount": 2,
                "currentItemCount": 2,
                "result": [
                    {"user": {"id": 1, "username": "student"}, "works": [{"id": 10, "title": "Thesis"}]},
                    {"id": 2, "username": "supervisor"}
                ]
            }
        """.trimIndent()

        val response = ApiJson.decodeFromString<UserListResponse>(json)
        val result = response.result!!

        assertEquals(1, result[0].user?.id)
        assertEquals(10, result[0].works?.single()?.id)
        assertEquals(2, result[1].user?.id)
        assertEquals("supervisor", result[1].user?.username)
        assertNull(result[1].works)
    }
}
