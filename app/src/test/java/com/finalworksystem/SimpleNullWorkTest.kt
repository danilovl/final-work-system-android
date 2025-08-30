package com.finalworksystem

import com.finalworksystem.data.event.model.response.ParticipantResponse
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.fail
import org.junit.Test

class SimpleNullWorkTest {

    @Test
    fun testParticipantParsingWithNullWork() {
        println("[DEBUG_LOG] Testing participant parsing with null work")

        val gson = Gson()

        val participantJson = """{"id":194,"firstName":null,"secondName":null,"email":null,"user":{"id":155,"username":"xdanv09","firstname":"Vladimir","lastname":"Danilov","fullName":"Danilov Vladimir","email":"xdanv09@vse.cz","degreeBefore":"Bc.","degreeAfter":null,"roles":["ROLE_STUDENT","ROLE_ADMIN","ROLE_USER"]},"work":null}"""

        try {
            val participantDto = gson.fromJson(participantJson, ParticipantResponse::class.java)

            assertNotNull(participantDto)
            assertEquals(194, participantDto.id)
            assertNull(participantDto.work)
            assertNotNull(participantDto.user)
            assertEquals(155, participantDto.user.id)

            println("[DEBUG_LOG] Participant parsing successful with null work!")
            println("[DEBUG_LOG] Participant ID: ${participantDto.id}")
            println("[DEBUG_LOG] Work is null: ${participantDto.work == null}")
            println("[DEBUG_LOG] User ID: ${participantDto.user.id}")

        } catch (e: Exception) {
            println("[DEBUG_LOG] Exception occurred: ${e.message}")
            e.printStackTrace()
            fail("Participant parsing should not fail with null work: ${e.message}")
        }
    }

    @Test
    fun testParticipantParsingWithValidWork() {
        println("[DEBUG_LOG] Testing participant parsing with valid work")

        val gson = Gson()
        val participantJson = """{"id":194,"firstName":null,"secondName":null,"email":null,"user":{"id":155,"username":"xdanv09","firstname":"Vladimir","lastname":"Danilov","fullName":"Danilov Vladimir","email":"xdanv09@vse.cz","degreeBefore":"Bc.","degreeAfter":null,"roles":["ROLE_STUDENT","ROLE_ADMIN","ROLE_USER"]},"work":{"id":109,"title":"Test Work","shortcut":null,"deadline":"2018-04-25T00:00:00+02:00","deadlineProgram":null,"status":{"id":1,"name":"Active","description":null,"color":"#42f442"},"type":{"id":1,"name":"Diploma","description":null,"shortcut":"DP"},"author":null,"supervisor":null,"opponent":null,"consultant":null}}"""

        try {
            val participantDto = gson.fromJson(participantJson, ParticipantResponse::class.java)
            assertNotNull(participantDto)
            assertEquals(194, participantDto.id)
            assertNotNull(participantDto.work)
            assertEquals(109, participantDto.work!!.id)
            assertNotNull(participantDto.user)
            assertEquals(155, participantDto.user.id)

            println("[DEBUG_LOG] Participant parsing successful with valid work!")
            println("[DEBUG_LOG] Participant ID: ${participantDto.id}")
            println("[DEBUG_LOG] Work ID: ${participantDto.work!!.id}")
            println("[DEBUG_LOG] User ID: ${participantDto.user.id}")

        } catch (e: Exception) {
            println("[DEBUG_LOG] Exception occurred: ${e.message}")
            e.printStackTrace()
            fail("Participant parsing should not fail with valid work: ${e.message}")
        }
    }
}