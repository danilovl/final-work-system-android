package com.finalworksystem

import com.finalworksystem.data.event.model.response.EventListResponse
import com.finalworksystem.data.event.model.response.EventResponse
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.fail
import org.junit.Test

class EventDtoParsingTest {

    @Test
    fun testEventDtoParsingWithAddress() {
        println("[DEBUG_LOG] Testing EventDto parsing with address field")

        val gson = Gson()

        val eventJson = """{"id":147,"name":null,"start":"2025-05-30T08:30:00+02:00","end":"2025-05-30T13:00:00+02:00","type":{"id":1,"name":"Konzultace","description":null,"color":"#3b91ad","registrable":true},"owner":{"id":36,"username":"pecinovr","firstname":"Rudolf","lastname":"Pecinovský","email":"rudolf@pecinovsky.cz","degreeBefore":"Ing., CSc.","degreeAfter":null,"roles":["ROLE_SUPERVISOR","ROLE_USER"]},"address":{"id":1,"name":"JM 371","description":null,"street":"Chemická 951, 148 00 Praha-Kunratice, Česko","skype":false,"latitude":50.0205889,"longitude":14.4936111,"owner":{"id":36,"username":"pecinovr","firstname":"Rudolf","lastname":"Pecinovský","email":"rudolf@pecinovsky.cz","degreeBefore":"Ing., CSc.","degreeAfter":null,"roles":["ROLE_SUPERVISOR","ROLE_USER"]}}}"""

        println("[DEBUG_LOG] JSON length: ${eventJson.length}")

        try {
            val eventDto = gson.fromJson(eventJson, EventResponse::class.java)

            assertNotNull(eventDto)
            assertEquals(147, eventDto.id)
            assertEquals("2025-05-30T08:30:00+02:00", eventDto.start)
            assertEquals("2025-05-30T13:00:00+02:00", eventDto.end)

            assertNotNull(eventDto.type)
            assertEquals(1, eventDto.type.id)
            assertEquals("Konzultace", eventDto.type.name)
            assertEquals("#3b91ad", eventDto.type.color)
            assertEquals(true, eventDto.type.registrable)

            assertNotNull(eventDto.owner)
            assertEquals(36, eventDto.owner.id)
            assertEquals("pecinovr", eventDto.owner.username)
            assertEquals("Rudolf", eventDto.owner.firstname)
            assertEquals("Pecinovský", eventDto.owner.lastname)

            assertNotNull(eventDto.address)
            assertEquals(1, eventDto.address!!.id)
            assertEquals("JM 371", eventDto.address!!.name)
            assertEquals("Chemická 951, 148 00 Praha-Kunratice, Česko", eventDto.address!!.street)
            assertEquals(false, eventDto.address!!.skype)
            assertEquals(50.0205889, eventDto.address!!.latitude!!, 0.0001)
            assertEquals(14.4936111, eventDto.address!!.longitude!!, 0.0001)

            assertNotNull(eventDto.address!!.owner)
            assertEquals(36, eventDto.address!!.owner.id)
            assertEquals("pecinovr", eventDto.address!!.owner.username)

            println("[DEBUG_LOG] EventDto parsing successful!")
            println("[DEBUG_LOG] Event ID: ${eventDto.id}")
            println("[DEBUG_LOG] Address: ${eventDto.address!!.name} - ${eventDto.address!!.street}")

        } catch (e: JsonSyntaxException) {
            println("[DEBUG_LOG] JsonSyntaxException occurred: ${e.message}")
            e.printStackTrace()
            fail("JSON parsing should not fail with valid JSON: ${e.message}")
        } catch (e: Exception) {
            println("[DEBUG_LOG] Unexpected exception: ${e.message}")
            e.printStackTrace()
            fail("Unexpected error during JSON parsing: ${e.message}")
        }
    }

    @Test
    fun testEventListResponseDtoParsingWithAddress() {
        println("[DEBUG_LOG] Testing EventListResponseDto parsing with address field")

        val gson = Gson()

        val responseJson = """{"count":2,"totalCount":10,"success":true,"result":[{"id":147,"name":null,"start":"2025-05-30T08:30:00+02:00","end":"2025-05-30T13:00:00+02:00","type":{"id":1,"name":"Konzultace","description":null,"color":"#3b91ad","registrable":true},"owner":{"id":36,"username":"pecinovr","firstname":"Rudolf","lastname":"Pecinovský","email":"rudolf@pecinovsky.cz","degreeBefore":"Ing., CSc.","degreeAfter":null,"fullName":"Rudolf Pecinovský","token":null,"roles":[]},"address":{"id":1,"name":"JM 371","description":null,"street":"Chemická 951, 148 00 Praha-Kunratice, Česko","skype":false,"latitude":50.0205889,"longitude":14.4936111,"owner":{"id":36,"username":"pecinovr","firstname":"Rudolf","lastname":"Pecinovský","email":"rudolf@pecinovsky.cz","degreeBefore":"Ing., CSc.","degreeAfter":null,"fullName":"Rudolf Pecinovský","token":null,"roles":[]}},"comment":null,"participant":{"id":1,"firstName":"Test","secondName":"User","email":"test@example.com","user":{"id":1,"username":"testuser","firstname":"Test","lastname":"User","fullName":"Test User","degreeBefore":null,"degreeAfter":null,"email":"test@example.com","token":null,"roles":[]},"work":{"id":1,"title":"Test Work","type":{"id":1,"name":"Bachelor","description":null},"status":{"id":1,"name":"Active","description":null},"deadline":"2025-12-31","deadlineProgram":null,"author":{"id":1,"username":"testuser","firstname":"Test","lastname":"User","fullName":"Test User","degreeBefore":null,"degreeAfter":null,"email":"test@example.com","token":null,"roles":[]},"supervisor":{"id":36,"username":"pecinovr","firstname":"Rudolf","lastname":"Pecinovský","email":"rudolf@pecinovsky.cz","degreeBefore":"Ing., CSc.","degreeAfter":null,"fullName":"Rudolf Pecinovský","token":null,"roles":[]},"opponent":null,"consultant":null}}},{"id":132,"name":null,"start":"2019-10-25T17:00:00+02:00","end":"2019-10-25T19:30:00+02:00","type":{"id":1,"name":"Konzultace","description":null,"color":"#3b91ad","registrable":true},"owner":{"id":36,"username":"pecinovr","firstname":"Rudolf","lastname":"Pecinovský","email":"rudolf@pecinovsky.cz","degreeBefore":"Ing., CSc.","degreeAfter":null,"fullName":"Rudolf Pecinovský","token":null,"roles":[]},"address":{"id":1,"name":"JM 371","description":null,"street":"Chemická 951, 148 00 Praha-Kunratice, Česko","skype":false,"latitude":50.0205889,"longitude":14.4936111,"owner":{"id":36,"username":"pecinovr","firstname":"Rudolf","lastname":"Pecinovský","email":"rudolf@pecinovsky.cz","degreeBefore":"Ing., CSc.","degreeAfter":null,"fullName":"Rudolf Pecinovský","token":null,"roles":[]}},"comment":null,"participant":{"id":1,"firstName":"Test","secondName":"User","email":"test@example.com","user":{"id":1,"username":"testuser","firstname":"Test","lastname":"User","fullName":"Test User","degreeBefore":null,"degreeAfter":null,"email":"test@example.com","token":null,"roles":[]},"work":{"id":1,"title":"Test Work","type":{"id":1,"name":"Bachelor","description":null},"status":{"id":1,"name":"Active","description":null},"deadline":"2025-12-31","deadlineProgram":null,"author":{"id":1,"username":"testuser","firstname":"Test","lastname":"User","fullName":"Test User","degreeBefore":null,"degreeAfter":null,"email":"test@example.com","token":null,"roles":[]},"supervisor":{"id":36,"username":"pecinovr","firstname":"Rudolf","lastname":"Pecinovský","email":"rudolf@pecinovsky.cz","degreeBefore":"Ing., CSc.","degreeAfter":null,"fullName":"Rudolf Pecinovský","token":null,"roles":[]},"opponent":null,"consultant":null}}}]}"""

        println("[DEBUG_LOG] JSON length: ${responseJson.length}")

        try {
            val responseDto = gson.fromJson(responseJson, EventListResponse::class.java)

            assertNotNull(responseDto)
            assertEquals(2, responseDto.count)
            assertEquals(10, responseDto.totalCount)
            assertEquals(true, responseDto.success)

            assertNotNull(responseDto.result)
            assertEquals(2, responseDto.result.size)

            val firstEvent = responseDto.result[0]
            assertEquals(147, firstEvent.id)
            assertNotNull(firstEvent.address)
            assertEquals("JM 371", firstEvent.address!!.name)

            val secondEvent = responseDto.result[1]
            assertEquals(132, secondEvent.id)
            assertNotNull(secondEvent.address)
            assertEquals("JM 371", secondEvent.address!!.name)

            println("[DEBUG_LOG] EventListResponseDto parsing successful!")
            println("[DEBUG_LOG] Event count: ${responseDto.count}")
            println("[DEBUG_LOG] First event address: ${firstEvent.address!!.name}")

        } catch (e: JsonSyntaxException) {
            println("[DEBUG_LOG] JsonSyntaxException occurred: ${e.message}")
            e.printStackTrace()
            fail("JSON parsing should not fail with valid JSON: ${e.message}")
        } catch (e: Exception) {
            println("[DEBUG_LOG] Unexpected exception: ${e.message}")
            e.printStackTrace()
            fail("Unexpected error during JSON parsing: ${e.message}")
        }
    }
}
