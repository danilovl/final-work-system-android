package com.finalworksystem

import com.finalworksystem.infrastructure.api.ApiConstant
import com.finalworksystem.infrastructure.api.ApiEndpoint
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UserProfileImageByIdTest {

    @Test
    fun testApiEndpointReplacedWithIdBasedVersion() {
        println("[DEBUG_LOG] Testing that ApiEndpoints.User.GET_PROFILE_IMAGE now uses ID-based format")

        val endpoint = ApiEndpoint.User.GET_PROFILE_IMAGE
        assertNotNull("[DEBUG_LOG] Endpoint should not be null", endpoint)
        assertEquals("[DEBUG_LOG] Endpoint should now use ID-based format", 
            "/api/key/users/{id}/profile/image", endpoint)

        println("[DEBUG_LOG] Test passed: Original endpoint now uses ID-based format")
    }

    @Test
    fun testApiConstantPointsToIdBasedEndpoint() {
        println("[DEBUG_LOG] Testing that ApiConstants.API_KEY_USER_PROFILE_IMAGE points to ID-based endpoint")

        val constant = ApiConstant.API_KEY_USER_PROFILE_IMAGE
        assertNotNull("[DEBUG_LOG] Constant should not be null", constant)
        assertEquals("[DEBUG_LOG] Constant should reference ID-based endpoint", 
            ApiEndpoint.User.GET_PROFILE_IMAGE, constant)
        assertEquals("[DEBUG_LOG] Constant should have ID-based format", 
            "/api/key/users/{id}/profile/image", constant)

        println("[DEBUG_LOG] Test passed: Constant points to ID-based endpoint")
    }

    @Test
    fun testEndpointFormatIsCorrect() {
        println("[DEBUG_LOG] Testing that the endpoint format matches requirements")

        val endpoint = ApiEndpoint.User.GET_PROFILE_IMAGE

        assertTrue("[DEBUG_LOG] Endpoint should contain {id} placeholder",
            endpoint.contains("{id}"))

        assertTrue("[DEBUG_LOG] Endpoint should end with /profile/image",
            endpoint.endsWith("/profile/image"))

        val expectedPattern = "/api/key/users/{id}/profile/image"
        assertEquals("[DEBUG_LOG] Endpoint should match expected pattern", 
            expectedPattern, endpoint)

        println("[DEBUG_LOG] Test passed: Endpoint format is correct")
    }

    @Test
    fun testReplacementImplementation() {
        println("[DEBUG_LOG] Testing that the replacement implementation is correct")

        assertEquals("[DEBUG_LOG] Endpoint should use ID-based format",
            "/api/key/users/{id}/profile/image", ApiEndpoint.User.GET_PROFILE_IMAGE)

        assertEquals("[DEBUG_LOG] Constant should reference updated endpoint",
            ApiEndpoint.User.GET_PROFILE_IMAGE, ApiConstant.API_KEY_USER_PROFILE_IMAGE)

        println("[DEBUG_LOG] Test passed: Replacement implementation is correct")
    }
}
