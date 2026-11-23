package com.finalworksystem

import com.finalworksystem.data.user.model.response.UserListResponse
import com.finalworksystem.data.user.model.response.UserResponse
import com.finalworksystem.data.user.model.response.toDomainModel
import com.finalworksystem.infrastructure.api.serialization.ApiJson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class UserSearchMappingTest {


    @Test
    fun testUserSearchResponseMapping_withNullWorks() {
        val json = """
            {
                "numItemsPerPage": 20,
                "totalCount": 1,
                "currentItemCount": 1,
                "result": [
                    {
                        "user": {
                            "id": 42,
                            "username": "jdoe",
                            "firstname": "John",
                            "lastname": "Doe",
                            "email": "jdoe@example.com",
                            "degreeBefore": null,
                            "degreeAfter": null
                        },
                        "works": null
                    }
                ]
            }
        """.trimIndent()

        val response = ApiJson.decodeFromString<UserListResponse>(json)
        val domainResult = response.toDomainModel()

        assertNotNull(domainResult)
        assertEquals(1, domainResult.totalCount)
        assertEquals(1, domainResult.result.size)
        assertEquals(42, domainResult.result[0].user.id)
        assertEquals("John", domainResult.result[0].user.firstname)
        assertEquals("Doe", domainResult.result[0].user.lastname)
        assertEquals("jdoe@example.com", domainResult.result[0].user.email)
        assertEquals(0, domainResult.result[0].works.size)
    }

    @Test
    fun testUserSearchResponseMapping_withNullUserAndNullFields() {
        val json = """
            {
                "numItemsPerPage": null,
                "totalCount": null,
                "currentItemCount": null,
                "result": [
                    {
                        "user": null,
                        "works": null
                    }
                ]
            }
        """.trimIndent()

        val response = ApiJson.decodeFromString<UserListResponse>(json)
        val domainResult = response.toDomainModel()

        assertNotNull(domainResult)
        assertEquals(0, domainResult.totalCount)
        assertEquals(1, domainResult.result.size)
        assertEquals(0, domainResult.result[0].user.id)
        assertEquals("", domainResult.result[0].user.firstname)
        assertEquals(0, domainResult.result[0].works.size)
    }

    @Test
    fun testUserResponseToDomainModel_withNullReceiver() {
        val nullResponse: UserResponse? = null
        val domainUser = nullResponse.toDomainModel()

        assertNotNull(domainUser)
        assertEquals(0, domainUser.id)
        assertEquals("", domainUser.username)
        assertEquals("", domainUser.firstname)
        assertEquals("", domainUser.lastname)
        assertEquals("", domainUser.email)
    }

    @Test
    fun testUserSearchResponseMapping_withDirectUserObjectsInResult() {
        val json = """
            {
                "numItemsPerPage": 25,
                "totalCount": 1,
                "currentItemCount": 1,
                "result": [
                    {
                        "id": 155,
                        "username": "xdanv09",
                        "firstname": "Vladimir",
                        "lastname": "Danilov",
                        "fullName": "Danilov Vladimir",
                        "email": "xdanv09@vse.cz",
                        "degreeBefore": "Bc.",
                        "degreeAfter": null,
                        "roles": [
                            "ROLE_STUDENT",
                            "ROLE_ADMIN",
                            "ROLE_USER"
                        ]
                    }
                ]
            }
        """.trimIndent()

        val response = ApiJson.decodeFromString<UserListResponse>(json)
        val domainResult = response.toDomainModel()

        assertNotNull(domainResult)
        assertEquals(25, domainResult.numItemsPerPage)
        assertEquals(1, domainResult.totalCount)
        assertEquals(1, domainResult.currentItemCount)
        assertEquals(1, domainResult.result.size)

        val user = domainResult.result[0].user
        assertEquals(155, user.id)
        assertEquals("xdanv09", user.username)
        assertEquals("Vladimir", user.firstname)
        assertEquals("Danilov", user.lastname)
        assertEquals("Danilov Vladimir", user.fullName)
        assertEquals("xdanv09@vse.cz", user.email)
        assertEquals("Bc.", user.degreeBefore)
        assertEquals(listOf("ROLE_STUDENT", "ROLE_ADMIN", "ROLE_USER"), user.roles)
    }
}
