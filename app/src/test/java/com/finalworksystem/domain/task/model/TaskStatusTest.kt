package com.finalworksystem.domain.task.model

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class TaskStatusTest {

    @Test
    fun testTaskStatusEnumValues() {
        println("[DEBUG_LOG] Testing TaskStatus enum values")

        val expectedNames = arrayOf("COMPLETE", "ACTIVE", "NOTIFY")
        val actualNames = TaskStatus.values().map { it.name }.toTypedArray()

        assertEquals("TaskStatus should have exactly 3 values", 3, TaskStatus.values().size)
        assertArrayEquals("TaskStatus should contain expected names", expectedNames, actualNames)

        val expectedValues = arrayOf("complete", "active", "notify")
        val actualValues = TaskStatus.values().map { it.value }.toTypedArray()
        assertArrayEquals("TaskStatus values should be lowercase", expectedValues, actualValues)

        assertEquals("COMPLETE constant should exist", TaskStatus.COMPLETE, TaskStatus.valueOf("COMPLETE"))
        assertEquals("ACTIVE constant should exist", TaskStatus.ACTIVE, TaskStatus.valueOf("ACTIVE"))
        assertEquals("NOTIFY constant should exist", TaskStatus.NOTIFY, TaskStatus.valueOf("NOTIFY"))

        assertEquals("COMPLETE value should be lowercase", "complete", TaskStatus.COMPLETE.value)
        assertEquals("ACTIVE value should be lowercase", "active", TaskStatus.ACTIVE.value)
        assertEquals("NOTIFY value should be lowercase", "notify", TaskStatus.NOTIFY.value)

        println("[DEBUG_LOG] All TaskStatus enum tests passed")
    }

    @Test
    fun testTaskStatusEnumOrdering() {
        println("[DEBUG_LOG] Testing TaskStatus enum ordering")

        val values = TaskStatus.values()
        assertEquals("First value should be COMPLETE", TaskStatus.COMPLETE, values[0])
        assertEquals("Second value should be ACTIVE", TaskStatus.ACTIVE, values[1])
        assertEquals("Third value should be NOTIFY", TaskStatus.NOTIFY, values[2])

        println("[DEBUG_LOG] TaskStatus enum ordering test passed")
    }
}
