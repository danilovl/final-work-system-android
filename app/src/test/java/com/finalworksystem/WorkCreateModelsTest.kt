package com.finalworksystem

import com.finalworksystem.data.work.model.request.WorkCreateRequest
import com.finalworksystem.infrastructure.api.serialization.ApiJson
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class WorkCreateModelsTest {

    @Test
    fun `WorkCreateRequest is serialized with API field names`() {
        val request = WorkCreateRequest(
            title = "Test Final Work",
            shortcut = "TFW",
            typeId = 1,
            statusId = 2,
            categoriesIds = listOf(1, 2),
            deadline = "2026-10-15",
            deadlineProgram = "2026-10-01",
            authorId = 10,
            supervisorId = 20,
            opponentId = 30,
            consultantId = 40
        )

        val json = ApiJson.parseToJsonElement(ApiJson.encodeToString(request)).jsonObject

        assertEquals("Test Final Work", json["title"]!!.jsonPrimitive.content)
        assertEquals("TFW", json["shortcut"]!!.jsonPrimitive.content)
        assertEquals(1, json["typeId"]!!.jsonPrimitive.int)
        assertEquals(2, json["statusId"]!!.jsonPrimitive.int)
        assertEquals(listOf(1, 2), json["categoriesIds"]!!.jsonArray.map { it.jsonPrimitive.int })
        assertEquals("2026-10-15", json["deadline"]!!.jsonPrimitive.content)
        assertEquals("2026-10-01", json["deadlineProgram"]!!.jsonPrimitive.content)
        assertEquals(10, json["authorId"]!!.jsonPrimitive.int)
        assertEquals(20, json["supervisorId"]!!.jsonPrimitive.int)
        assertEquals(30, json["opponentId"]!!.jsonPrimitive.int)
        assertEquals(40, json["consultantId"]!!.jsonPrimitive.int)
    }

    @Test
    fun `null optional fields are omitted from the request body`() {
        val request = WorkCreateRequest(
            title = "Minimal",
            typeId = 1,
            statusId = 1,
            deadline = "2026-10-15"
        )

        val json = ApiJson.parseToJsonElement(ApiJson.encodeToString(request)).jsonObject

        assertEquals("Minimal", json["title"]!!.jsonPrimitive.content)
        assertFalse(json.containsKey("shortcut"))
        assertFalse(json.containsKey("authorId"))
        assertFalse(json.containsKey("categoriesIds"))
    }
}
