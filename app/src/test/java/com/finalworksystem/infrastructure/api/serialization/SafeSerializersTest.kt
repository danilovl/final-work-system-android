package com.finalworksystem.infrastructure.api.serialization

import com.finalworksystem.data.conversation.model.ConversationDto
import com.finalworksystem.data.user.model.response.UserDetailResponse
import com.finalworksystem.data.work.model.response.StatusResponse
import com.finalworksystem.data.work.model.response.TypeResponse
import com.finalworksystem.data.work.model.response.WorkCategoryDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class SafeSerializersTest {

    @Test
    fun `invalid number falls back to zero`() {
        val status = ApiJson.decodeFromString<StatusResponse>(
            """{"id": "AB", "name": "InProgress", "description": "In Progress Status", "color": "#FF0000"}"""
        )

        assertEquals(0, status.id)
        assertEquals("InProgress", status.name)
        assertEquals("#FF0000", status.color)
    }

    @Test
    fun `number sent as string is parsed`() {
        val type = ApiJson.decodeFromString<TypeResponse>(
            """{"id": "123", "name": "Diploma", "description": "Diploma Thesis"}"""
        )

        assertEquals(123, type.id)
        assertEquals("Diploma", type.name)
    }

    @Test
    fun `invalid sorting value falls back to zero`() {
        val category = ApiJson.decodeFromString<WorkCategoryDto>(
            """{"id": 5, "name": "IT Category", "description": "IT", "sorting": "AB"}"""
        )

        assertEquals(5, category.id)
        assertEquals(0, category.sorting)
    }

    @Test
    fun `decimal and boolean values are coerced to int`() {
        val decimal = ApiJson.decodeFromString<TypeResponse>("""{"id": 1.9, "name": "x"}""")
        val boolean = ApiJson.decodeFromString<TypeResponse>("""{"id": true, "name": "x"}""")

        assertEquals(1, decimal.id)
        assertEquals(1, boolean.id)
    }

    @Test
    fun `null stays null for nullable number`() {
        val user = ApiJson.decodeFromString<UserDetailResponse>("""{"id": null, "username": "u"}""")

        assertNull(user.id)
        assertEquals("u", user.username)
    }

    @Test
    fun `null or missing non-null fields use defaults`() {
        val conversation = ApiJson.decodeFromString<ConversationDto>("""{"id": null, "unknownField": 1}""")

        assertEquals(0, conversation.id)
        assertFalse(conversation.isRead)
        assertNull(conversation.name)
    }
}
