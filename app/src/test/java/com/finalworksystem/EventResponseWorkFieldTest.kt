package com.finalworksystem

import com.finalworksystem.data.event.model.response.EventResponse
import com.finalworksystem.data.event.model.response.toDataModel
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class EventResponseWorkFieldTest {

    @Test
    fun testEventParsingWithWorkField() {
        println("[DEBUG_LOG] Testing event parsing with work field in participant")

        val gson = Gson()

        val issueJson = """{"id":96,"name":null,"start":"2017-08-30T09:00:00+02:00","end":"2017-08-30T10:00:00+02:00","type":{"id":1,"name":"Konzultace","description":null,"color":"#3b91ad","registrable":true},"owner":{"id":36,"username":"pecinovr","firstname":"Rudolf","lastname":"Pecinovský","fullName":"Pecinovský Rudolf","email":"rudolf@pecinovsky.cz","degreeBefore":"Ing., CSc.","degreeAfter":null,"roles":["ROLE_SUPERVISOR","ROLE_USER"]},"address":{"id":1,"name":"JM 371","description":null,"street":"Chemická 951, 148 00 Praha-Kunratice, Česko","skype":false,"latitude":50.0205889,"longitude":14.4936111,"owner":{"id":36,"username":"pecinovr","firstname":"Rudolf","lastname":"Pecinovský","fullName":"Pecinovský Rudolf","email":"rudolf@pecinovsky.cz","degreeBefore":"Ing., CSc.","degreeAfter":null,"roles":["ROLE_SUPERVISOR","ROLE_USER"]}},"participant":{"id":194,"firstName":null,"secondName":null,"email":null,"user":{"id":155,"username":"xdanv09","firstname":"Vladimir","lastname":"Danilov","fullName":"Danilov Vladimir","email":"xdanv09@vse.cz","degreeBefore":"Bc.","degreeAfter":null,"roles":["ROLE_STUDENT","ROLE_ADMIN","ROLE_USER"]},"work":{"id":109,"title":"Systém pro vedení bakalářských a diplomových prací","shortcut":null,"deadline":"2018-04-25T00:00:00+02:00","deadlineProgram":null,"status":{"id":1,"name":"Aktivní","description":null,"color":"#42f442"},"type":{"id":1,"name":"Diplomová práce","description":null,"shortcut":"DP"},"author":null,"supervisor":null,"opponent":null,"consultant":null}},"comment":[{"id":4,"content":"<ul>\n<li>U termínů odevzdání nahradit zopakovaný tex textem <strong>Zbývá:</strong> <br />&nbsp;</li>\n<li>Umožnit studentům přepnout u objednávané místnosti přednastavenou na skype.<br />&nbsp","owner":{"id":36,"username":"pecinovr","firstname":"Rudolf","lastname":"Pecinovský","fullName":"Pecinovský Rudolf","email":"rudolf@pecinovsky.cz","degreeBefore":"Ing., CSc.","degreeAfter":null,"roles":["ROLE_SUPERVISOR","ROLE_USER"]},"createdAt":"2017-08-30T11:00:54+02:00","updatedAt":null}]}"""

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

            val work = eventDto.participant!!.work!!
            assertEquals(109, work.id)
            assertEquals("Systém pro vedení bakalářských a diplomových prací", work.title)
            assertNull(work.shortcut)
            assertEquals("2018-04-25T00:00:00+02:00", work.deadline)
            assertNull(work.deadlineProgram)

            assertEquals(1, work.status.id)
            assertEquals("Aktivní", work.status.name)
            assertNull(work.status.description)
            assertEquals("#42f442", work.status.color)

            assertEquals(1, work.type.id)
            assertEquals("Diplomová práce", work.type.name)
            assertNull(work.type.description)
            assertEquals("DP", work.type.shortcut)

            assertNull(work.author)
            assertNull(work.supervisor)
            assertNull(work.opponent)
            assertNull(work.consultant)

            assertNotNull(eventDto.comment)
            assertTrue(eventDto.comment!!.isNotEmpty())
            assertEquals(1, eventDto.comment!!.size)

            println("[DEBUG_LOG] Event parsing successful with work field properly handled!")

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
    fun testEventDataModelConversion() {
        println("[DEBUG_LOG] Testing event data model conversion with work field")

        val gson = Gson()

        val issueJson = """{"id":96,"name":null,"start":"2017-08-30T09:00:00+02:00","end":"2017-08-30T10:00:00+02:00","type":{"id":1,"name":"Konzultace","description":null,"color":"#3b91ad","registrable":true},"owner":{"id":36,"username":"pecinovr","firstname":"Rudolf","lastname":"Pecinovský","fullName":"Pecinovský Rudolf","email":"rudolf@pecinovsky.cz","degreeBefore":"Ing., CSc.","degreeAfter":null,"roles":["ROLE_SUPERVISOR","ROLE_USER"]},"address":{"id":1,"name":"JM 371","description":null,"street":"Chemická 951, 148 00 Praha-Kunratice, Česko","skype":false,"latitude":50.0205889,"longitude":14.4936111,"owner":{"id":36,"username":"pecinovr","firstname":"Rudolf","lastname":"Pecinovský","fullName":"Pecinovský Rudolf","email":"rudolf@pecinovsky.cz","degreeBefore":"Ing., CSc.","degreeAfter":null,"roles":["ROLE_SUPERVISOR","ROLE_USER"]}},"participant":{"id":194,"firstName":null,"secondName":null,"email":null,"user":{"id":155,"username":"xdanv09","firstname":"Vladimir","lastname":"Danilov","fullName":"Danilov Vladimir","email":"xdanv09@vse.cz","degreeBefore":"Bc.","degreeAfter":null,"roles":["ROLE_STUDENT","ROLE_ADMIN","ROLE_USER"]},"work":{"id":109,"title":"Systém pro vedení bakalářských a diplomových prací","shortcut":null,"deadline":"2018-04-25T00:00:00+02:00","deadlineProgram":null,"status":{"id":1,"name":"Aktivní","description":null,"color":"#42f442"},"type":{"id":1,"name":"Diplomová práce","description":null,"shortcut":"DP"},"author":null,"supervisor":null,"opponent":null,"consultant":null}},"comment":[{"id":4,"content":"<ul>\n<li>U termínů odevzdání nahradit zopakovaný tex textem <strong>Zbývá:</strong> <br />&nbsp;</li>\n<li>Umožnit studentům přepnout u objednávané místnosti přednastavenou na skype.<br />&nbsp","owner":{"id":36,"username":"pecinovr","firstname":"Rudolf","lastname":"Pecinovský","fullName":"Pecinovský Rudolf","email":"rudolf@pecinovsky.cz","degreeBefore":"Ing., CSc.","degreeAfter":null,"roles":["ROLE_SUPERVISOR","ROLE_USER"]},"createdAt":"2017-08-30T11:00:54+02:00","updatedAt":null}]}"""

        try {
            val eventDto = gson.fromJson(issueJson, EventResponse::class.java)

            val eventDataModel = eventDto.toDataModel()

            assertNotNull(eventDataModel)
            assertEquals(96, eventDataModel.id)

            assertNotNull(eventDataModel.participant)
            assertNotNull(eventDataModel.participant!!.work)

            val workDataModel = eventDataModel.participant!!.work!!
            assertEquals(109, workDataModel.id)
            assertEquals("Systém pro vedení bakalářských a diplomových prací", workDataModel.title)
            assertNull(workDataModel.shortcut)

            assertNull(workDataModel.author)
            assertNull(workDataModel.supervisor)
            assertNull(workDataModel.opponent)
            assertNull(workDataModel.consultant)

            println("[DEBUG_LOG] Event data model conversion successful!")

        } catch (e: Exception) {
            println("[DEBUG_LOG] Exception during data model conversion: ${e.message}")
            e.printStackTrace()
            fail("Data model conversion should not fail: ${e.message}")
        }
    }
}
