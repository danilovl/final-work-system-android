package com.finalworksystem

import com.finalworksystem.data.user.model.response.UserDetailResponse
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class JsonParsingErrorTest {

    @Test
    fun testJsonParsingErrorAtColumn231() {
        println("[DEBUG_LOG] Testing JSON parsing error at column 231 - roles field")

        val gson = Gson()
        
        val jsonResponse = """{"id":155,"username":"testuser","firstname":"Test","lastname":"User","fullName":"Test User","degreeBefore":"Bc.","degreeAfter":null,"email":"test@example.com","token":"1234567890abcdef","roles":["ROLE_STUDENT","ROLE_ADMIN","ROLE_USER"]}"""
        
        println("[DEBUG_LOG] JSON length: ${jsonResponse.length}")
        println("[DEBUG_LOG] Position of 'roles': ${jsonResponse.indexOf("\"roles\"")}")
        
        try {
            val userDetailResponse = gson.fromJson(jsonResponse, UserDetailResponse::class.java)
            
            assertNotNull(userDetailResponse)
            val roles = userDetailResponse.roles ?:  emptyList()
            assertEquals(3, roles.size)
            assertTrue(roles.contains("ROLE_STUDENT"))
            assertTrue(roles.contains("ROLE_ADMIN"))
            assertTrue(roles.contains("ROLE_USER"))
            
            println("[DEBUG_LOG] JSON parsing successful!")
            println("[DEBUG_LOG] Roles: $roles")
            
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
    fun testJsonParsingWithLongerContent() {
        println("[DEBUG_LOG] Testing JSON parsing with longer content to reach column 231")

        val gson = Gson()
        
        val jsonResponse = """{"id":155,"username":"verylongusernamethatmakesthejsonlonger","firstname":"VeryLongFirstName","lastname":"VeryLongLastName","fullName":"VeryLongFirstName VeryLongLastName","degreeBefore":"Bc.","degreeAfter":"Ph.D.","email":"verylongusername@verylongdomainname.com","token":"verylongtokenthatmakesthejsonlongerandlonger1234567890","roles":["ROLE_STUDENT","ROLE_ADMIN","ROLE_USER"]}"""
        
        println("[DEBUG_LOG] JSON length: ${jsonResponse.length}")
        println("[DEBUG_LOG] Position of 'roles': ${jsonResponse.indexOf("\"roles\"")}")
        
        try {
            val userDetailResponse = gson.fromJson(jsonResponse, UserDetailResponse::class.java)
            
            assertNotNull(userDetailResponse)
            val roles = userDetailResponse.roles ?: emptyList()
            assertEquals(3, roles.size)
            assertTrue(roles.contains("ROLE_STUDENT"))
            assertTrue(roles.contains("ROLE_ADMIN"))
            assertTrue(roles.contains("ROLE_USER"))
            
            println("[DEBUG_LOG] JSON parsing successful!")
            println("[DEBUG_LOG] Roles: $roles")
            
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
    fun testJsonParsingWithNullRoles() {
        println("[DEBUG_LOG] Testing JSON parsing with null roles")

        val gson = Gson()
        
        val jsonResponse = """{"id":155,"username":"testuser","firstname":"Test","lastname":"User","fullName":"Test User","degreeBefore":"Bc.","degreeAfter":null,"email":"test@example.com","token":"1234567890abcdef","roles":null}"""
        
        println("[DEBUG_LOG] JSON length: ${jsonResponse.length}")
        println("[DEBUG_LOG] Position of 'roles': ${jsonResponse.indexOf("\"roles\"")}")
        
        try {
            val userDetailResponse = gson.fromJson(jsonResponse, UserDetailResponse::class.java)
            
            assertNotNull(userDetailResponse)
            val roles = userDetailResponse.roles ?: emptyList()
            assertEquals(0, roles.size)
            assertTrue(roles.isEmpty())
            
            println("[DEBUG_LOG] JSON parsing with null roles successful!")
            println("[DEBUG_LOG] Roles: $roles")
            
        } catch (e: JsonSyntaxException) {
            println("[DEBUG_LOG] JsonSyntaxException occurred: ${e.message}")
            e.printStackTrace()
            fail("JSON parsing should not fail with null roles: ${e.message}")
        } catch (e: Exception) {
            println("[DEBUG_LOG] Unexpected exception: ${e.message}")
            e.printStackTrace()
            fail("Unexpected error during JSON parsing: ${e.message}")
        }
    }

    @Test
    fun testJsonParsingWithMalformedRoles() {
        println("[DEBUG_LOG] Testing JSON parsing with potentially malformed roles")

        val gson = Gson()
        
        val testCases = listOf(
            """{"id":155,"username":"user1","firstname":"Test","lastname":"User","fullName":"Test User","degreeBefore":"Bc.","degreeAfter":null,"email":"test@example.com","token":"token123","roles":["ROLE_STUDENT","ROLE_USER"]}""",
            """{"id":156,"username":"user2","firstname":"Test","lastname":"User","fullName":"Test User","degreeBefore":"Bc.","degreeAfter":null,"email":"test@example.com","token":"token123","roles":["ROLE_SUPERVISOR","ROLE_USER"]}""",
            """{"id":157,"username":"user3","firstname":"Test","lastname":"User","fullName":"Test User","degreeBefore":"Bc.","degreeAfter":null,"email":"test@example.com","token":"token123","roles":[]}""",
            """{"id":158,"username":"user4","firstname":"Test","lastname":"User","fullName":"Test User","degreeBefore":"Bc.","degreeAfter":null,"email":"test@example.com","token":"token123","roles":["ROLE_ADMIN"]}""",
            """{"id":159,"username":"user5","firstname":"Test","lastname":"User","fullName":"Test User","degreeBefore":"Bc.","degreeAfter":null,"email":"test@example.com","token":"token123","roles":null}"""
        )
        
        testCases.forEachIndexed { index, jsonResponse ->
            println("[DEBUG_LOG] Testing case ${index + 1}")
            println("[DEBUG_LOG] JSON length: ${jsonResponse.length}")
            println("[DEBUG_LOG] Position of 'roles': ${jsonResponse.indexOf("\"roles\"")}")
            
            try {
                val userDetailResponse = gson.fromJson(jsonResponse, UserDetailResponse::class.java)
                
                assertNotNull(userDetailResponse)
                val roles = userDetailResponse.roles ?: emptyList()
                
                println("[DEBUG_LOG] Case ${index + 1} parsing successful!")
                println("[DEBUG_LOG] Roles: $roles")
                
            } catch (e: JsonSyntaxException) {
                println("[DEBUG_LOG] Case ${index + 1} JsonSyntaxException: ${e.message}")
                e.printStackTrace()
                fail("JSON parsing should not fail for case ${index + 1}: ${e.message}")
            } catch (e: Exception) {
                println("[DEBUG_LOG] Case ${index + 1} unexpected exception: ${e.message}")
                e.printStackTrace()
                fail("Unexpected error for case ${index + 1}: ${e.message}")
            }
        }
    }
}