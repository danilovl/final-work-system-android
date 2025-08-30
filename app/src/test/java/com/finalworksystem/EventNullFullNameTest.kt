package com.finalworksystem

import com.finalworksystem.data.event.model.response.EventResponse
import com.finalworksystem.data.event.model.response.toDataModel
import com.finalworksystem.data.event.model.toDomainModel
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.fail
import org.junit.Test

class EventNullFullNameTest {

    @Test
    fun testEventParsingWithNullFullName() {
        println("[DEBUG_LOG] Testing event parsing with null fullName - fixing 'Error loading eventsL PArameter as non null is null dada,evet,User full name'")

        val gson = Gson()

        val eventJson = """
        {
            "id": 1,
            "type": {
                "id": 1,
                "name": "Meeting",
                "description": "Team meeting",
                "color": "#FF0000",
                "registrable": true
            },
            "name": "Test Event",
            "start": "2024-01-01T10:00:00",
            "end": "2024-01-01T11:00:00",
            "owner": {
                "id": 1,
                "username": "testuser",
                "firstname": "John",
                "lastname": "Doe",
                "degreeBefore": "Bc.",
                "degreeAfter": null,
                "email": "john.doe@example.com",
                "roles": ["ROLE_USER"]
            },
            "address": {
                "id": 1,
                "name": "Conference Room",
                "description": "Main conference room",
                "street": "123 Main St",
                "skype": false,
                "latitude": 50.0,
                "longitude": 14.0,
                "owner": {
                    "id": 1,
                    "username": "testuser",
                    "firstname": "John",
                    "lastname": "Doe",
                    "degreeBefore": "Bc.",
                    "degreeAfter": null,
                    "email": "john.doe@example.com",
                    "roles": ["ROLE_USER"]
                }
            },
            "comment": []
        }
        """.trimIndent()

        try {
            val eventDto = gson.fromJson(eventJson, EventResponse::class.java)
            assertNotNull("EventDto should be parsed successfully", eventDto)

            println("[DEBUG_LOG] EventDto parsed successfully")
            println("[DEBUG_LOG] Owner name: ${eventDto.owner.firstname} ${eventDto.owner.lastname}")

            val eventDataModel = eventDto.toDataModel()
            assertNotNull("Event data model should be created successfully", eventDataModel)

            println("[DEBUG_LOG] Event data model created successfully")
            println("[DEBUG_LOG] Owner name from data model: ${eventDataModel.owner.firstname} ${eventDataModel.owner.lastname}")

            assertEquals("John", eventDataModel.owner.firstname)
            assertEquals("Doe", eventDataModel.owner.lastname)
            assertEquals("john.doe@example.com", eventDataModel.owner.email)

            assertNull("Participant should be null when not present in JSON", eventDataModel.participant)

            val domainEvent = eventDataModel.toDomainModel()
            assertNotNull("Domain event should be created successfully", domainEvent)

            println("[DEBUG_LOG] Domain event created successfully")
            println("[DEBUG_LOG] All fields handled correctly without fullName!")
            println("[DEBUG_LOG] Original error should be fixed by removing fullName dependency")

        } catch (e: Exception) {
            println("[DEBUG_LOG] Exception occurred: ${e.message}")
            e.printStackTrace()
            fail("Event parsing should not fail with null fullName: ${e.message}")
        }
    }
}
