package com.finalworksystem

import com.finalworksystem.data.work.model.request.WorkCreateRequest
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class WorkCreateModelsTest {

    private val gson = Gson()

    @Test
    fun testWorkCreateRequest_serialization() {
        println("[DEBUG_LOG] Testing WorkCreateRequest serialization")

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

        val json = gson.toJson(request)

        assertNotNull(json)
        val jsonObject = gson.fromJson(json, Map::class.java)

        assertEquals("Test Final Work", jsonObject["title"])
        assertEquals("TFW", jsonObject["shortcut"])
        assertEquals(1.0, jsonObject["typeId"])
        assertEquals(2.0, jsonObject["statusId"])
        assertEquals(listOf(1.0, 2.0), jsonObject["categoriesIds"])
        assertEquals("2026-10-15", jsonObject["deadline"])
        assertEquals("2026-10-01", jsonObject["deadlineProgram"])
        assertEquals(10.0, jsonObject["authorId"])
        assertEquals(20.0, jsonObject["supervisorId"])
        assertEquals(30.0, jsonObject["opponentId"])
        assertEquals(40.0, jsonObject["consultantId"])

        println("[DEBUG_LOG] WorkCreateRequest serialization test passed successfully")
    }
}
