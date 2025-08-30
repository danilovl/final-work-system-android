package com.finalworksystem

import com.finalworksystem.domain.event.model.EventAddress
import com.finalworksystem.domain.user.model.User
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class EventAddressMapTest {

    @Test
    fun testEventAddressWithCoordinates() {
        val testUser = User(
            id = 1,
            username = "testuser",
            firstname = "Test",
            lastname = "User",
            fullName = "Test User",
            degreeBefore = null,
            degreeAfter = null,
            email = "test@example.com",
            token = "test-token",
            roles = listOf("ROLE_USER")
        )

        val eventAddress = EventAddress(
            id = 1,
            name = "Test Location",
            description = "Test Description",
            street = "123 Test Street",
            skype = false,
            latitude = 40.7128,
            longitude = -74.0060,
            owner = testUser
        )

        assertNotNull("Latitude should not be null", eventAddress.latitude)
        assertNotNull("Longitude should not be null", eventAddress.longitude)
        assertEquals("Latitude should match", 40.7128, eventAddress.latitude!!, 0.0001)
        assertEquals("Longitude should match", -74.0060, eventAddress.longitude!!, 0.0001)

        println("[DEBUG_LOG] EventAddress with coordinates created successfully")
        println("[DEBUG_LOG] Location: ${eventAddress.name} at ${eventAddress.latitude}, ${eventAddress.longitude}")
    }

    @Test
    fun testEventAddressWithoutCoordinates() {
        val testUser = User(
            id = 1,
            username = "testuser2",
            firstname = "Test",
            lastname = "User",
            fullName = "Test User",
            degreeBefore = null,
            degreeAfter = null,
            email = "test2@example.com",
            token = "test-token",
            roles = listOf("ROLE_USER")
        )

        val eventAddress = EventAddress(
            id = 2,
            name = "Test Location Without Coords",
            description = "Test Description",
            street = "456 Test Avenue",
            skype = true,
            latitude = null,
            longitude = null,
            owner = testUser
        )

        assertNull("Latitude should be null", eventAddress.latitude)
        assertNull("Longitude should be null", eventAddress.longitude)

        println("[DEBUG_LOG] EventAddress without coordinates created successfully")
        println("[DEBUG_LOG] Location: ${eventAddress.name} (no coordinates)")
    }
}
