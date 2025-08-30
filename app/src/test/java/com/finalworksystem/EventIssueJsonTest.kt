package com.finalworksystem

import com.finalworksystem.data.event.model.response.EventListResponse
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class EventIssueJsonTest {

    @Test
    fun testActualIssueJsonParsing() {
        println("[DEBUG_LOG] Testing actual JSON from issue description")

        val gson = Gson()

        val issueJson = """{"count":10,"totalCount":10,"success":true,"result":[{"id":147,"name":null,"start":"2025-05-30T08:30:00+02:00","end":"2025-05-30T13:00:00+02:00","type":{"id":1,"name":"Konzultace","description":null,"color":"#3b91ad","registrable":true},"owner":{"id":36,"username":"pecinovr","firstname":"Rudolf","lastname":"Pecinovský","email":"rudolf@pecinovsky.cz","degreeBefore":"Ing., CSc.","degreeAfter":null,"roles":["ROLE_SUPERVISOR","ROLE_USER"]},"address":{"id":1,"name":"JM 371","description":null,"street":"Chemická 951, 148 00 Praha-Kunratice, Česko","skype":false,"latitude":50.0205889,"longitude":14.4936111,"owner":{"id":36,"username":"pecinovr","firstname":"Rudolf","lastname":"Pecinovský","email":"rudolf@pecinovsky.cz","degreeBefore":"Ing., CSc.","degreeAfter":null,"roles":["ROLE_SUPERVISOR","ROLE_USER"]}}},{"id":132,"name":null,"start":"2019-10-25T17:00:00+02:00","end":"2019-10-25T19:30:00+02:00","type":{"id":1,"name":"Konzultace","description":null,"color":"#3b91ad","registrable":true},"owner":{"id":36,"username":"pecinovr","firstname":"Rudolf","lastname":"Pecinovský","email":"rudolf@pecinovsky.cz","degreeBefore":"Ing., CSc.","degreeAfter":null,"roles":["ROLE_SUPERVISOR","ROLE_USER"]},"address":{"id":1,"name":"JM 371","description":null,"street":"Chemická 951, 148 00 Praha-Kunratice, Česko","skype":false,"latitude":50.0205889,"longitude":14.4936111,"owner":{"id":36,"username":"pecinovr","firstname":"Rudolf","lastname":"Pecinovský","email":"rudolf@pecinovsky.cz","degreeBefore":"Ing., CSc.","degreeAfter":null,"roles":["ROLE_SUPERVISOR","ROLE_USER"]}}}]}"""

        println("[DEBUG_LOG] JSON length: ${issueJson.length}")

        try {
            val responseDto = gson.fromJson(issueJson, EventListResponse::class.java)

            assertNotNull(responseDto)
            assertEquals(10, responseDto.count)
            assertEquals(10, responseDto.totalCount)
            assertEquals(true, responseDto.success)

            assertNotNull(responseDto.result)
            assertEquals(2, responseDto.result.size)

            val firstEvent = responseDto.result[0]
            assertEquals(147, firstEvent.id)
            assertNull(firstEvent.name)
            assertEquals("2025-05-30T08:30:00+02:00", firstEvent.start)
            assertEquals("2025-05-30T13:00:00+02:00", firstEvent.end)

            assertEquals(1, firstEvent.type.id)
            assertEquals("Konzultace", firstEvent.type.name)
            assertEquals("#3b91ad", firstEvent.type.color)
            assertEquals(true, firstEvent.type.registrable)

            assertEquals(36, firstEvent.owner.id)
            assertEquals("pecinovr", firstEvent.owner.username)
            assertEquals("Rudolf", firstEvent.owner.firstname)
            assertEquals("Pecinovský", firstEvent.owner.lastname)
            assertEquals("rudolf@pecinovsky.cz", firstEvent.owner.email)
            assertEquals("Ing., CSc.", firstEvent.owner.degreeBefore)
            assertNull(firstEvent.owner.degreeAfter)
            assertEquals(2, firstEvent.owner.roles.size)
            assertTrue(firstEvent.owner.roles.contains("ROLE_SUPERVISOR"))
            assertTrue(firstEvent.owner.roles.contains("ROLE_USER"))

            assertNotNull(firstEvent.address)
            assertEquals(1, firstEvent.address!!.id)
            assertEquals("JM 371", firstEvent.address!!.name)
            assertEquals("Chemická 951, 148 00 Praha-Kunratice, Česko", firstEvent.address!!.street)
            assertEquals(false, firstEvent.address!!.skype)
            assertEquals(50.0205889, firstEvent.address!!.latitude!!, 0.0001)
            assertEquals(14.4936111, firstEvent.address!!.longitude!!, 0.0001)

            assertNull(firstEvent.participant)

            val secondEvent = responseDto.result[1]
            assertEquals(132, secondEvent.id)
            assertEquals("2019-10-25T17:00:00+02:00", secondEvent.start)
            assertEquals("2019-10-25T19:30:00+02:00", secondEvent.end)

            println("[DEBUG_LOG] Issue JSON parsing successful!")
            println("[DEBUG_LOG] All events parsed correctly without fullName or token fields")
            println("[DEBUG_LOG] Participant field is correctly optional")

        } catch (e: JsonSyntaxException) {
            println("[DEBUG_LOG] JsonSyntaxException occurred: ${e.message}")
            e.printStackTrace()
            fail("JSON parsing should not fail with issue JSON: ${e.message}")
        } catch (e: Exception) {
            println("[DEBUG_LOG] Unexpected exception: ${e.message}")
            e.printStackTrace()
            fail("Unexpected error during JSON parsing: ${e.message}")
        }
    }
}
