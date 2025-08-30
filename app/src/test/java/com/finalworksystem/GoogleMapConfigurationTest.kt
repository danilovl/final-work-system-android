package com.finalworksystem

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GoogleMapConfigurationTest {

    @Test
    fun testGoogleMapsApiKeyConfiguration() {
        val apiKey = BuildConfig.API_GOOGLE_KEY

        assertNotNull("Google Maps API key should not be null", apiKey)
        assertFalse("Google Maps API key should not be empty", apiKey.isEmpty())

        assertNotEquals("Google Maps API key should be replaced with actual key", 
            "YOUR_GOOGLE_MAPS_API_KEY", apiKey)

        println("[DEBUG_LOG] Google Maps API Key configured: ${apiKey.take(10)}...")
    }

    @Test
    fun testGoogleMapsApiKeyFormat() {
        val apiKey = BuildConfig.API_GOOGLE_KEY

        if (apiKey != "YOUR_GOOGLE_MAPS_API_KEY" && apiKey.isNotEmpty()) {
            assertTrue("Google Maps API key should start with 'AIza'", 
                apiKey.startsWith("AIza"))
            assertEquals("Google Maps API key should be 39 characters long", 
                39, apiKey.length)
        }

        println("[DEBUG_LOG] API Key format validation completed")
    }
}
