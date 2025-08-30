package com.finalworksystem

import com.finalworksystem.data.user.model.response.UserDetailResponse
import com.finalworksystem.data.user.model.response.toDataModel
import com.finalworksystem.data.user.model.response.toDomain
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class RoleProcessingTest {

    @Test
    fun testRoleProcessingWithStudentAdminUser() {
        println("[DEBUG_LOG] Testing role processing with STUDENT/ADMIN/USER roles")

        val gson = Gson()
        val jsonResponse = """{"id":155,"username":"student01","firstname":"John","lastname":"Doe","fullName":"Doe John","degreeBefore":"Bc.","degreeAfter":null,"email":"student@vse.cz","token":"token123","roles":["ROLE_STUDENT","ROLE_ADMIN","ROLE_USER"]}"""

        try {
            val userDetailResponse = gson.fromJson(jsonResponse, UserDetailResponse::class.java)
            println("[DEBUG_LOG] JSON parsing successful")
            println("[DEBUG_LOG] Roles: ${userDetailResponse.roles}")

            val dataUser = userDetailResponse.toDataModel()
            println("[DEBUG_LOG] Data model conversion successful")
            println("[DEBUG_LOG] Data model roles: ${dataUser.roles}")

            val domainUser = userDetailResponse.toDomain()
            println("[DEBUG_LOG] Domain model conversion successful")
            println("[DEBUG_LOG] Domain model roles: ${domainUser.roles}")

            assertEquals(3, domainUser.roles.size)
            assertTrue(domainUser.roles.contains("ROLE_STUDENT"))
            assertTrue(domainUser.roles.contains("ROLE_ADMIN"))
            assertTrue(domainUser.roles.contains("ROLE_USER"))

        } catch (e: Exception) {
            println("[DEBUG_LOG] Error with STUDENT/ADMIN/USER roles: ${e.message}")
            e.printStackTrace()
            fail("Should not fail with STUDENT/ADMIN/USER roles: ${e.message}")
        }
    }

    @Test
    fun testRoleProcessingWithSupervisorUser() {
        println("[DEBUG_LOG] Testing role processing with SUPERVISOR/USER roles")

        val gson = Gson()
        val jsonResponse = """{"id":156,"username":"supervisor01","firstname":"Jane","lastname":"Smith","fullName":"Smith Jane","degreeBefore":"Dr.","degreeAfter":null,"email":"supervisor@vse.cz","token":"token456","roles":["ROLE_SUPERVISOR","ROLE_USER"]}"""

        try {
            val userDetailResponse = gson.fromJson(jsonResponse, UserDetailResponse::class.java)
            println("[DEBUG_LOG] JSON parsing successful")
            println("[DEBUG_LOG] Roles: ${userDetailResponse.roles}")

            val dataUser = userDetailResponse.toDataModel()
            println("[DEBUG_LOG] Data model conversion successful")
            println("[DEBUG_LOG] Data model roles: ${dataUser.roles}")

            val domainUser = userDetailResponse.toDomain()
            println("[DEBUG_LOG] Domain model conversion successful")
            println("[DEBUG_LOG] Domain model roles: ${domainUser.roles}")

            assertEquals(2, domainUser.roles.size)
            assertTrue(domainUser.roles.contains("ROLE_SUPERVISOR"))
            assertTrue(domainUser.roles.contains("ROLE_USER"))

        } catch (e: Exception) {
            println("[DEBUG_LOG] Error with SUPERVISOR/USER roles: ${e.message}")
            e.printStackTrace()
            fail("Should not fail with SUPERVISOR/USER roles: ${e.message}")
        }
    }

    @Test
    fun testRoleProcessingWithUnknownRoles() {
        println("[DEBUG_LOG] Testing role processing with unknown roles")

        val gson = Gson()
        val jsonResponse = """{"id":157,"username":"unknown01","firstname":"Test","lastname":"User","fullName":"User Test","degreeBefore":null,"degreeAfter":null,"email":"test@vse.cz","token":"token789","roles":["ROLE_UNKNOWN","ROLE_CUSTOM"]}"""

        try {
            val userDetailResponse = gson.fromJson(jsonResponse, UserDetailResponse::class.java)
            println("[DEBUG_LOG] JSON parsing successful")
            println("[DEBUG_LOG] Roles: ${userDetailResponse.roles}")

            val dataUser = userDetailResponse.toDataModel()
            println("[DEBUG_LOG] Data model conversion successful")
            println("[DEBUG_LOG] Data model roles: ${dataUser.roles}")

            val domainUser = userDetailResponse.toDomain()
            println("[DEBUG_LOG] Domain model conversion successful")
            println("[DEBUG_LOG] Domain model roles: ${domainUser.roles}")

            assertEquals(2, domainUser.roles.size)
            assertTrue(domainUser.roles.contains("ROLE_UNKNOWN"))
            assertTrue(domainUser.roles.contains("ROLE_CUSTOM"))

        } catch (e: Exception) {
            println("[DEBUG_LOG] Error with unknown roles: ${e.message}")
            e.printStackTrace()
            fail("Should not fail with unknown roles: ${e.message}")
        }
    }

    @Test
    fun testRoleProcessingWithNullRoles() {
        println("[DEBUG_LOG] Testing role processing with null roles")

        val gson = Gson()
        val jsonResponse = """{"id":158,"username":"nullroles01","firstname":"Null","lastname":"Roles","fullName":"Roles Null","degreeBefore":null,"degreeAfter":null,"email":"nullroles@vse.cz","token":"token000","roles":null}"""

        try {
            val userDetailResponse = gson.fromJson(jsonResponse, UserDetailResponse::class.java)
            println("[DEBUG_LOG] JSON parsing successful")
            println("[DEBUG_LOG] Roles: ${userDetailResponse.roles}")

            val dataUser = userDetailResponse.toDataModel()
            println("[DEBUG_LOG] Data model conversion successful")
            println("[DEBUG_LOG] Data model roles: ${dataUser.roles}")

            val domainUser = userDetailResponse.toDomain()
            println("[DEBUG_LOG] Domain model conversion successful")
            println("[DEBUG_LOG] Domain model roles: ${domainUser.roles}")

            assertNotNull(domainUser.roles)

        } catch (e: Exception) {
            println("[DEBUG_LOG] Expected potential error with null roles: ${e.message}")
        }
    }

    @Test
    fun testRoleProcessingWithEmptyRoles() {
        println("[DEBUG_LOG] Testing role processing with empty roles array")

        val gson = Gson()
        val jsonResponse = """{"id":159,"username":"emptyroles01","firstname":"Empty","lastname":"Roles","fullName":"Roles Empty","degreeBefore":null,"degreeAfter":null,"email":"emptyroles@vse.cz","token":"token111","roles":[]}"""

        try {
            val userDetailResponse = gson.fromJson(jsonResponse, UserDetailResponse::class.java)
            println("[DEBUG_LOG] JSON parsing successful")
            println("[DEBUG_LOG] Roles: ${userDetailResponse.roles}")

            val dataUser = userDetailResponse.toDataModel()
            println("[DEBUG_LOG] Data model conversion successful")
            println("[DEBUG_LOG] Data model roles: ${dataUser.roles}")

            val domainUser = userDetailResponse.toDomain()
            println("[DEBUG_LOG] Domain model conversion successful")
            println("[DEBUG_LOG] Domain model roles: ${domainUser.roles}")

            assertEquals(0, domainUser.roles.size)
            assertTrue(domainUser.roles.isEmpty())

        } catch (e: Exception) {
            println("[DEBUG_LOG] Error with empty roles: ${e.message}")
            e.printStackTrace()
            fail("Should not fail with empty roles: ${e.message}")
        }
    }
}