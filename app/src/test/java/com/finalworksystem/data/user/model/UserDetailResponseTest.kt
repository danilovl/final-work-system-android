package com.finalworksystem.data.user.model

import com.finalworksystem.data.user.model.response.UserDetailResponse
import com.finalworksystem.data.user.model.response.toDataModel
import com.finalworksystem.data.user.model.response.toDomain
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class UserDetailResponseTest {

    @Test
    fun testJsonParsingWithRolesArray() {
        println("[DEBUG_LOG] Testing JSON parsing with roles as array")

        val gson = Gson()
        val jsonResponse = """{"id":155,"username":"xdanv09","firstname":"Vladimir","lastname":"Danilov","fullName":"Danilov Vladimir","degreeBefore":"Bc.","degreeAfter":null,"email":"xdanv09@vse.cz","token":"20b2eb840f4f97e7be64c250340c36029bd7780e","roles":["ROLE_STUDENT","ROLE_ADMIN","ROLE_USER"]}"""

        try {
            val userDetailResponse = gson.fromJson(jsonResponse, UserDetailResponse::class.java)

            assertEquals(155, userDetailResponse.id)
            assertEquals("xdanv09", userDetailResponse.username)
            assertEquals("Vladimir", userDetailResponse.firstname)
            assertEquals("Danilov", userDetailResponse.lastname)
            assertEquals("xdanv09@vse.cz", userDetailResponse.email)
            assertEquals("20b2eb840f4f97e7be64c250340c36029bd7780e", userDetailResponse.token)

            val roles = userDetailResponse.roles ?: emptyList()
            assertEquals(3, roles.size)
            assertTrue(roles.contains("ROLE_STUDENT"))
            assertTrue(roles.contains("ROLE_ADMIN"))
            assertTrue(roles.contains("ROLE_USER"))

            println("[DEBUG_LOG] JSON parsing successful!")
            println("[DEBUG_LOG] Roles: ${userDetailResponse.roles}")

        } catch (e: Exception) {
            println("[DEBUG_LOG] JSON parsing failed: ${e.message}")
            fail("JSON parsing should not fail: ${e.message}")
        }
    }

    @Test
    fun testConversionToDataModel() {
        println("[DEBUG_LOG] Testing conversion to data model")

        val userDetailResponse = UserDetailResponse(
            id = 155,
            username = "xdanv09",
            firstname = "Vladimir",
            lastname = "Danilov",
            fullName = "Danilov Vladimir",
            degreeBefore = "Bc.",
            degreeAfter = null,
            email = "xdanv09@vse.cz",
            token = "20b2eb840f4f97e7be64c250340c36029bd7780e",
            roles = listOf("ROLE_STUDENT", "ROLE_ADMIN", "ROLE_USER")
        )

        try {
            val dataUser = userDetailResponse.toDataModel()

            assertEquals(155, dataUser.id)
            assertEquals("xdanv09", dataUser.username)
            assertEquals(3, dataUser.roles.size)
            assertTrue(dataUser.roles.contains("ROLE_STUDENT"))
            assertTrue(dataUser.roles.contains("ROLE_ADMIN"))
            assertTrue(dataUser.roles.contains("ROLE_USER"))

            println("[DEBUG_LOG] Conversion to data model successful!")

        } catch (e: Exception) {
            println("[DEBUG_LOG] Conversion failed: ${e.message}")
            fail("Conversion should not fail: ${e.message}")
        }
    }

    @Test
    fun testConversionToDomain() {
        println("[DEBUG_LOG] Testing conversion to domain model")

        val userDetailResponse = UserDetailResponse(
            id = 155,
            username = "xdanv09",
            firstname = "Vladimir",
            lastname = "Danilov",
            fullName = "Danilov Vladimir",
            degreeBefore = "Bc.",
            degreeAfter = null,
            email = "xdanv09@vse.cz",
            token = "20b2eb840f4f97e7be64c250340c36029bd7780e",
            roles = listOf("ROLE_STUDENT", "ROLE_ADMIN", "ROLE_USER")
        )

        try {
            val domainUser = userDetailResponse.toDomain()

            assertEquals(155, domainUser.id)
            assertEquals("xdanv09", domainUser.username)
            assertEquals(3, domainUser.roles.size)
            assertTrue(domainUser.roles.contains("ROLE_STUDENT"))
            assertTrue(domainUser.roles.contains("ROLE_ADMIN"))
            assertTrue(domainUser.roles.contains("ROLE_USER"))

            println("[DEBUG_LOG] Conversion to domain model successful!")

        } catch (e: Exception) {
            println("[DEBUG_LOG] Conversion failed: ${e.message}")
            fail("Conversion should not fail: ${e.message}")
        }
    }

    @Test
    fun testJsonParsingWithDifferentRolesArray() {
        println("[DEBUG_LOG] Testing JSON parsing with different roles structure")

        val gson = Gson()
        val jsonResponse = """{"id":156,"username":"supervisor01","firstname":"John","lastname":"Smith","fullName":"Smith John","degreeBefore":"Dr.","degreeAfter":null,"email":"supervisor@vse.cz","token":"30c3fb950e5f08f8ce75d351450d47139ce8890f","roles":["ROLE_SUPERVISOR","ROLE_USER"]}"""

        try {
            val userDetailResponse = gson.fromJson(jsonResponse, UserDetailResponse::class.java)

            assertEquals(156, userDetailResponse.id)
            assertEquals("supervisor01", userDetailResponse.username)
            assertEquals("John", userDetailResponse.firstname)
            assertEquals("Smith", userDetailResponse.lastname)
            assertEquals("supervisor@vse.cz", userDetailResponse.email)
            assertEquals("30c3fb950e5f08f8ce75d351450d47139ce8890f", userDetailResponse.token)

            val roles = userDetailResponse.roles ?: emptyList()
            assertEquals(2, roles.size)
            assertTrue(roles.contains("ROLE_SUPERVISOR"))
            assertTrue(roles.contains("ROLE_USER"))

            println("[DEBUG_LOG] JSON parsing with different roles successful!")
            println("[DEBUG_LOG] Roles: ${userDetailResponse.roles}")

            val dataUser = userDetailResponse.toDataModel()
            assertEquals(2, dataUser.roles.size)
            assertTrue(dataUser.roles.contains("ROLE_SUPERVISOR"))
            assertTrue(dataUser.roles.contains("ROLE_USER"))

            val domainUser = userDetailResponse.toDomain()
            assertEquals(2, domainUser.roles.size)
            assertTrue(domainUser.roles.contains("ROLE_SUPERVISOR"))
            assertTrue(domainUser.roles.contains("ROLE_USER"))

            println("[DEBUG_LOG] All conversions with different roles successful!")

        } catch (e: Exception) {
            println("[DEBUG_LOG] JSON parsing with different roles failed: ${e.message}")
            e.printStackTrace()
            fail("JSON parsing with different roles should not fail: ${e.message}")
        }
    }
}
