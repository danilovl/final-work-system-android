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

class EventNullAddressTest {

    @Test
    fun testEventDtoParsingWithNullAddress() {
        println("[DEBUG_LOG] Testing EventDto parsing with null address field")

        val gson = Gson()

        val eventJson = """{"id":147,"name":null,"start":"2025-05-30T08:30:00+02:00","end":"2025-05-30T13:00:00+02:00","type":{"id":1,"name":"Konzultace","description":null,"color":"#3b91ad","registrable":true},"owner":{"id":36,"username":"pecinovr","firstname":"Rudolf","lastname":"Pecinovský","email":"rudolf@pecinovsky.cz","degreeBefore":"Ing., CSc.","degreeAfter":null,"fullName":"Rudolf Pecinovský","token":null,"roles":[]},"address":null,"comment":null,"participant":{"id":1,"firstName":"Test","secondName":"User","email":"test@example.com","user":{"id":1,"username":"testuser","firstname":"Test","lastname":"User","fullName":"Test User","degreeBefore":null,"degreeAfter":null,"email":"test@example.com","token":null,"roles":[]},"work":{"id":1,"title":"Test Work","type":{"id":1,"name":"Bachelor","description":null},"status":{"id":1,"name":"Active","description":null},"deadline":"2025-12-31","deadlineProgram":null,"author":{"id":1,"username":"testuser","firstname":"Test","lastname":"User","fullName":"Test User","degreeBefore":null,"degreeAfter":null,"email":"test@example.com","token":null,"roles":[]},"supervisor":{"id":36,"username":"pecinovr","firstname":"Rudolf","lastname":"Pecinovský","email":"rudolf@pecinovsky.cz","degreeBefore":"Ing., CSc.","degreeAfter":null,"fullName":"Rudolf Pecinovský","token":null,"roles":[]},"opponent":null,"consultant":null}}}"""

        println("[DEBUG_LOG] JSON length: ${eventJson.length}")

        try {
            val eventDto = gson.fromJson(eventJson, EventResponse::class.java)

            assertNotNull(eventDto)
            assertEquals(147, eventDto.id)
            assertEquals("2025-05-30T08:30:00+02:00", eventDto.start)
            assertEquals("2025-05-30T13:00:00+02:00", eventDto.end)

            assertNull(eventDto.address)

            println("[DEBUG_LOG] EventDto parsing with null address successful!")
            println("[DEBUG_LOG] Event ID: ${eventDto.id}")
            println("[DEBUG_LOG] Address is null: ${eventDto.address == null}")

            val dataModel = eventDto.toDataModel()
            assertNotNull(dataModel)
            assertEquals(147, dataModel.id)
            assertNull(dataModel.address)

            println("[DEBUG_LOG] Conversion to data model successful!")

            val domainModel = dataModel.toDomainModel()
            assertNotNull(domainModel)
            assertEquals(147, domainModel.id)
            assertNull(domainModel.address)

            println("[DEBUG_LOG] Conversion to domain model successful!")

        } catch (e: Exception) {
            println("[DEBUG_LOG] Exception occurred: ${e.message}")
            e.printStackTrace()
            fail("Should handle null address gracefully: ${e.message}")
        }
    }
}
