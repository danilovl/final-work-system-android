package com.finalworksystem

import com.finalworksystem.data.work.model.response.StatusResponse
import com.finalworksystem.data.work.model.response.TypeResponse
import com.finalworksystem.data.work.model.response.WorkCategoryDto
import com.finalworksystem.infrastructure.api.util.SafeDoubleAdapter
import com.finalworksystem.infrastructure.api.util.SafeIntAdapter
import com.finalworksystem.infrastructure.api.util.SafeLongAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class SafeTypeAdaptersTest {

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Int::class.java, SafeIntAdapter())
        .registerTypeAdapter(Integer::class.java, SafeIntAdapter())
        .registerTypeAdapter(Long::class.java, SafeLongAdapter())
        .registerTypeAdapter(java.lang.Long::class.java, SafeLongAdapter())
        .registerTypeAdapter(Double::class.java, SafeDoubleAdapter())
        .registerTypeAdapter(java.lang.Double::class.java, SafeDoubleAdapter())
        .create()

    @Test
    fun testStatusResponse_withInvalidNumberInputAB() {
        val json = """
            {
                "id": "AB",
                "name": "InProgress",
                "description": "In Progress Status",
                "color": "#FF0000"
            }
        """.trimIndent()

        val status = gson.fromJson(json, StatusResponse::class.java)
        assertNotNull(status)
        assertEquals(0, status.id)
        assertEquals("InProgress", status.name)
        assertEquals("#FF0000", status.color)
        println("[DEBUG_LOG] StatusResponse parsed successfully with 'AB' as id: id=${status.id}")
    }

    @Test
    fun testTypeResponse_withValidAndInvalidInput() {
        val json = """
            {
                "id": "123",
                "name": "Diploma",
                "description": "Diploma Thesis"
            }
        """.trimIndent()

        val type = gson.fromJson(json, TypeResponse::class.java)
        assertNotNull(type)
        assertEquals(123, type.id)
        assertEquals("Diploma", type.name)
        println("[DEBUG_LOG] TypeResponse parsed successfully with string '123' as id: id=${type.id}")
    }

    @Test
    fun testWorkCategoryDto_withSortingAB() {
        val json = """
            {
                "id": 5,
                "name": "IT Category",
                "description": "IT",
                "sorting": "AB"
            }
        """.trimIndent()

        val category = gson.fromJson(json, WorkCategoryDto::class.java)
        assertNotNull(category)
        assertEquals(5, category.id)
        assertEquals(0, category.sorting)
        println("[DEBUG_LOG] WorkCategoryDto parsed successfully with 'AB' as sorting: sorting=${category.sorting}")
    }
}
