package com.finalworksystem

import com.finalworksystem.data.event.model.response.EventResponse
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class EventParticipantNullWorkTest {

    @Test
    fun testEventParsingWithNullWork() {
        println("[DEBUG_LOG] Testing event parsing with null work in participant")

        val gson = Gson()

        val issueJson = """{"id":96,"name":null,"start":"2017-08-30T09:00:00+02:00","end":"2017-08-30T10:00:00+02:00","type":{"id":1,"name":"Konzultace","description":null,"color":"#3b91ad","registrable":true},"owner":{"id":36,"username":"pecinovr","firstname":"Rudolf","lastname":"Pecinovský","fullName":"Pecinovský Rudolf","email":"rudolf@pecinovsky.cz","degreeBefore":"Ing., CSc.","degreeAfter":null,"roles":["ROLE_SUPERVISOR","ROLE_USER"]},"address":{"id":1,"name":"JM 371","description":null,"street":"Chemická 951, 148 00 Praha-Kunratice, Česko","skype":false,"latitude":50.0205889,"longitude":14.4936111,"owner":{"id":36,"username":"pecinovr","firstname":"Rudolf","lastname":"Pecinovský","fullName":"Pecinovský Rudolf","email":"rudolf@pecinovsky.cz","degreeBefore":"Ing., CSc.","degreeAfter":null,"roles":["ROLE_SUPERVISOR","ROLE_USER"]}},"participant":{"id":194,"firstName":null,"secondName":null,"email":null,"user":{"id":155,"username":"xdanv09","firstname":"Vladimir","lastname":"Danilov","fullName":"Danilov Vladimir","email":"xdanv09@vse.cz","degreeBefore":"Bc.","degreeAfter":null,"roles":["ROLE_STUDENT","ROLE_ADMIN","ROLE_USER"]},"work":{"id":109,"title":"Základy PHP frameworku - srovnání Nette\/ Zend\/ Symfony včetně zhodnocení v~rámci praktické webové aplikace","shortTitle":"BP_xdanv09","status":{"id":1,"name":"Zadaná","description":"Téma je schváleno, ale ještě nebyl předán seznam zdrojů.","color":"#ffc107"},"type":{"id":1,"name":"Bakalářská práce","shortName":"BP"},"category":{"id":1,"name":"Informatika"}}},"comment":[{"id":1,"content":"Dobrý den,\n\nlze možno termín konzultace na 12:00?"}]}"""

        println("[DEBUG_LOG] JSON length: ${issueJson.length}")

        try {
            val eventDto = gson.fromJson(issueJson, EventResponse::class.java)

            assertNotNull(eventDto)
            assertEquals(96, eventDto.id)
            assertNull(eventDto.name)
            assertEquals("2017-08-30T09:00:00+02:00", eventDto.start)
            assertEquals("2017-08-30T10:00:00+02:00", eventDto.end)

            assertNotNull(eventDto.participant)
            assertEquals(194, eventDto.participant!!.id)
            assertNotNull(eventDto.participant!!.work)
            assertEquals(109, eventDto.participant!!.work!!.id)

            assertNotNull(eventDto.comment)
            assertTrue(eventDto.comment!!.isNotEmpty())
            assertEquals(1, eventDto.comment!!.size)

            println("[DEBUG_LOG] Event parsing successful with work present!")

        } catch (e: JsonSyntaxException) {
            println("[DEBUG_LOG] JsonSyntaxException occurred: ${e.message}")
            e.printStackTrace()
            fail("JSON parsing should not fail: ${e.message}")
        } catch (e: Exception) {
            println("[DEBUG_LOG] Unexpected exception: ${e.message}")
            e.printStackTrace()
            fail("Unexpected error during JSON parsing: ${e.message}")
        }
    }

    @Test
    fun testEventParsingWithNullWorkInParticipant() {
        println("[DEBUG_LOG] Testing event parsing with null work in participant")

        val gson = Gson()

        val jsonWithNullWork = """{"id":96,"name":null,"start":"2017-08-30T09:00:00+02:00","end":"2017-08-30T10:00:00+02:00","type":{"id":1,"name":"Konzultace","description":null,"color":"#3b91ad","registrable":true},"owner":{"id":36,"username":"pecinovr","firstname":"Rudolf","lastname":"Pecinovský","fullName":"Pecinovský Rudolf","email":"rudolf@pecinovsky.cz","degreeBefore":"Ing., CSc.","degreeAfter":null,"roles":["ROLE_SUPERVISOR","ROLE_USER"]},"address":{"id":1,"name":"JM 371","description":null,"street":"Chemická 951, 148 00 Praha-Kunratice, Česko","skype":false,"latitude":50.0205889,"longitude":14.4936111,"owner":{"id":36,"username":"pecinovr","firstname":"Rudolf","lastname":"Pecinovský","fullName":"Pecinovský Rudolf","email":"rudolf@pecinovsky.cz","degreeBefore":"Ing., CSc.","degreeAfter":null,"roles":["ROLE_SUPERVISOR","ROLE_USER"]}},"participant":{"id":194,"firstName":null,"secondName":null,"email":null,"user":{"id":155,"username":"xdanv09","firstname":"Vladimir","lastname":"Danilov","fullName":"Danilov Vladimir","email":"xdanv09@vse.cz","degreeBefore":"Bc.","degreeAfter":null,"roles":["ROLE_STUDENT","ROLE_ADMIN","ROLE_USER"]},"work":null},"comment":[]}"""

        println("[DEBUG_LOG] JSON with null work length: ${jsonWithNullWork.length}")

        try {
            val eventDto = gson.fromJson(jsonWithNullWork, EventResponse::class.java)

            assertNotNull(eventDto)
            assertEquals(96, eventDto.id)

            assertNotNull(eventDto.participant)
            assertEquals(194, eventDto.participant!!.id)
            assertNull(eventDto.participant!!.work)

            assertNotNull(eventDto.comment)
            assertTrue(eventDto.comment!!.isEmpty())

            println("[DEBUG_LOG] Event parsing successful with null work!")

        } catch (e: JsonSyntaxException) {
            println("[DEBUG_LOG] JsonSyntaxException occurred: ${e.message}")
            e.printStackTrace()
            fail("JSON parsing should not fail with null work: ${e.message}")
        } catch (e: Exception) {
            println("[DEBUG_LOG] Unexpected exception: ${e.message}")
            e.printStackTrace()
            fail("Unexpected error during JSON parsing: ${e.message}")
        }
    }

    @Test
    fun testEventParsingWithNullParticipant() {
        println("[DEBUG_LOG] Testing event parsing with null participant")

        val gson = Gson()

        val jsonWithNullParticipant = """{"id":96,"name":null,"start":"2017-08-30T09:00:00+02:00","end":"2017-08-30T10:00:00+02:00","type":{"id":1,"name":"Konzultace","description":null,"color":"#3b91ad","registrable":true},"owner":{"id":36,"username":"pecinovr","firstname":"Rudolf","lastname":"Pecinovský","fullName":"Pecinovský Rudolf","email":"rudolf@pecinovsky.cz","degreeBefore":"Ing., CSc.","degreeAfter":null,"roles":["ROLE_SUPERVISOR","ROLE_USER"]},"address":{"id":1,"name":"JM 371","description":null,"street":"Chemická 951, 148 00 Praha-Kunratice, Česko","skype":false,"latitude":50.0205889,"longitude":14.4936111,"owner":{"id":36,"username":"pecinovr","firstname":"Rudolf","lastname":"Pecinovský","fullName":"Pecinovský Rudolf","email":"rudolf@pecinovsky.cz","degreeBefore":"Ing., CSc.","degreeAfter":null,"roles":["ROLE_SUPERVISOR","ROLE_USER"]}},"participant":null,"comment":[]}"""

        println("[DEBUG_LOG] JSON with null participant length: ${jsonWithNullParticipant.length}")

        try {
            val eventDto = gson.fromJson(jsonWithNullParticipant, EventResponse::class.java)

            assertNotNull(eventDto)
            assertEquals(96, eventDto.id)

            assertNull(eventDto.participant)

            assertNotNull(eventDto.comment)
            assertTrue(eventDto.comment!!.isEmpty())

            println("[DEBUG_LOG] Event parsing successful with null participant!")

        } catch (e: JsonSyntaxException) {
            println("[DEBUG_LOG] JsonSyntaxException occurred: ${e.message}")
            e.printStackTrace()
            fail("JSON parsing should not fail with null participant: ${e.message}")
        } catch (e: Exception) {
            println("[DEBUG_LOG] Unexpected exception: ${e.message}")
            e.printStackTrace()
            fail("Unexpected error during JSON parsing: ${e.message}")
        }
    }
}