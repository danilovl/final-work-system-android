package com.finalworksystem

import com.finalworksystem.data.conversation.model.response.ConversationWorkResponseDto
import com.finalworksystem.data.conversation.model.response.ConversationWorkResponseDtoAdapter
import com.finalworksystem.data.user.model.response.UserWithWorksResponse
import com.finalworksystem.data.user.model.response.UserWithWorksResponseAdapter
import com.finalworksystem.data.work.model.response.WorkListResponse
import com.finalworksystem.data.work.model.response.WorkResponse
import com.finalworksystem.data.work.model.response.WorkResponseAdapter
import com.finalworksystem.data.work.model.response.toDomainModel
import com.finalworksystem.infrastructure.api.util.SafeDoubleAdapter
import com.finalworksystem.infrastructure.api.util.SafeIntAdapter
import com.finalworksystem.infrastructure.api.util.SafeLongAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class WorkResponseDeserializationTest {

    private lateinit var gson: Gson

    @Before
    fun setUp() {
        gson = GsonBuilder()
            .registerTypeAdapter(Int::class.java, SafeIntAdapter())
            .registerTypeAdapter(Integer::class.java, SafeIntAdapter())
            .registerTypeAdapter(Long::class.java, SafeLongAdapter())
            .registerTypeAdapter(java.lang.Long::class.java, SafeLongAdapter())
            .registerTypeAdapter(Double::class.java, SafeDoubleAdapter())
            .registerTypeAdapter(java.lang.Double::class.java, SafeDoubleAdapter())
            .registerTypeAdapter(UserWithWorksResponse::class.java, UserWithWorksResponseAdapter())
            .registerTypeAdapter(WorkResponse::class.java, WorkResponseAdapter())
            .registerTypeAdapter(ConversationWorkResponseDto::class.java, ConversationWorkResponseDtoAdapter())
            .create()
    }

    @Test
    fun `testWorkResponse_whenWrappedInSuccessResult_deserializesIdCorrectly`() {
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

        val response = gson.fromJson(json, WorkResponse::class.java)
        assertNotNull(response)
        assertEquals(131, response.id)
        assertEquals("rest", response.title)

        val domainWork = response.toDomainModel()
        assertEquals(131, domainWork.id)
        assertEquals("rest", domainWork.title)
    }

    @Test
    fun `testWorkResponse_whenDirectJsonObject_deserializesIdCorrectly`() {
        val json = """
            {
                "id": 131,
                "title": "rest",
                "shortcut": "REST-1",
                "deadline": "2026-10-01"
            }
        """.trimIndent()

        val response = gson.fromJson(json, WorkResponse::class.java)
        assertNotNull(response)
        assertEquals(131, response.id)
        assertEquals("rest", response.title)

        val domainWork = response.toDomainModel()
        assertEquals(131, domainWork.id)
        assertEquals("rest", domainWork.title)
    }

    @Test
    fun `testWorkListResponse_deserializesItemsInResultCorrectly`() {
        val json = """
            {
                "count": 1,
                "totalCount": 1,
                "success": true,
                "result": [
                    {
                        "id": 131,
                        "title": "rest",
                        "shortcut": "REST-1"
                    }
                ]
            }
        """.trimIndent()

        val response = gson.fromJson(json, WorkListResponse::class.java)
        assertNotNull(response)
        assertEquals(1, response.result.size)

        val workResponse = response.result[0]
        assertEquals(131, workResponse.id)
        assertEquals("rest", workResponse.title)
    }

    @Test
    fun `testConversationWorkResponseDto_whenWrappedInSuccessResult_deserializesIdCorrectly`() {
        val json = """
            {
                "success": true,
                "result": {
                    "id": 42,
                    "name": "Conversation for work 131",
                    "isRead": true
                }
            }
        """.trimIndent()

        val dto = gson.fromJson(json, ConversationWorkResponseDto::class.java)
        assertNotNull(dto)
        assertEquals(42, dto.id)
        assertEquals("Conversation for work 131", dto.name)
    }
}
