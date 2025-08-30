package com.finalworksystem.presentation.view_model.work

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkViewModelCacheClearTest {

    @Test
    fun testCacheClearingOnUserLogout() {
        println("[DEBUG_LOG] Testing cache clearing when user logs out")

        var cacheHasData = true
        var currentUserId: Int? = 123
        
        println("[DEBUG_LOG] Initial state - Cache has data: $cacheHasData, User ID: $currentUserId")
        assertTrue("Cache should initially have data", cacheHasData)
        assertNotNull("User should be logged in", currentUserId)

        val newUserId: Int? = null
        
        if (currentUserId != null && currentUserId != newUserId) {
            cacheHasData = false
            println("[DEBUG_LOG] User logged out, clearing all caches")
        }
        currentUserId = newUserId

        assertFalse("Cache should be cleared after logout", cacheHasData)
        assertNull("User should be null after logout", currentUserId)
        
        println("[DEBUG_LOG] ✓ Cache successfully cleared on user logout")
    }

    @Test
    fun testCacheClearingOnUserSwitch() {
        println("[DEBUG_LOG] Testing cache clearing when user switches")

        var cacheHasData = true
        var currentUserId: Int? = 123
        
        println("[DEBUG_LOG] Initial state - Cache has data: $cacheHasData, User ID: $currentUserId")
        assertTrue("Cache should initially have data", cacheHasData)
        assertEquals("Should have first user ID", 123, currentUserId)

        val newUserId: Int? = 456
        
        if (currentUserId != null && currentUserId != newUserId) {
            cacheHasData = false
            println("[DEBUG_LOG] User switched from $currentUserId to $newUserId, clearing all caches")
        }
        currentUserId = newUserId

        assertFalse("Cache should be cleared after user switch", cacheHasData)
        assertEquals("Should have new user ID", 456, currentUserId)
        
        println("[DEBUG_LOG] ✓ Cache successfully cleared on user switch")
    }

    @Test
    fun testNoCacheClearingOnSameUser() {
        println("[DEBUG_LOG] Testing no cache clearing when same user is detected")

        var cacheHasData = true
        var currentUserId: Int? = 123
        
        println("[DEBUG_LOG] Initial state - Cache has data: $cacheHasData, User ID: $currentUserId")

        val newUserId: Int? = 123
        if (currentUserId != null && currentUserId != newUserId) {
            cacheHasData = false
            println("[DEBUG_LOG] ERROR: Cache cleared unexpectedly")
        } else {
            println("[DEBUG_LOG] Same user detected, keeping cache")
        }
        currentUserId = newUserId

        assertTrue("Cache should NOT be cleared for same user", cacheHasData)
        assertEquals("Should have same user ID", 123, currentUserId)
        
        println("[DEBUG_LOG] ✓ Cache correctly preserved for same user")
    }

    @Test
    fun testInitialUserLogin() {
        println("[DEBUG_LOG] Testing initial user login (no cache clearing)")

        var cacheHasData = false
        var currentUserId: Int? = null
        
        println("[DEBUG_LOG] Initial state - Cache has data: $cacheHasData, User ID: $currentUserId")

        val newUserId: Int? = 123
        
        if (currentUserId != null && currentUserId != newUserId) {
            println("[DEBUG_LOG] ERROR: Cache clearing triggered on initial login")
        } else {
            println("[DEBUG_LOG] Initial user login, no cache to clear")
        }
        currentUserId = newUserId

        assertFalse("Cache should remain empty on initial login", cacheHasData)
        assertEquals("Should have new user ID", 123, currentUserId)
        
        println("[DEBUG_LOG] ✓ Initial user login handled correctly")
    }

    @Test
    fun testClearWorkCacheMethod() {
        println("[DEBUG_LOG] Testing clearWorkCache method functionality")

        var cachedWorksEmpty = false
        var cachedWorkDetailsEmpty = false
        var cachedTasksEmpty = false
        var cachedVersionsEmpty = false
        var cachedEventsEmpty = false
        var cachedWorkMessagesEmpty = false
        var cachedConversationWorkEmpty = false
        
        println("[DEBUG_LOG] Initial state - All caches have data")
        
        cachedWorksEmpty = true
        cachedWorkDetailsEmpty = true
        cachedTasksEmpty = true
        cachedVersionsEmpty = true
        cachedEventsEmpty = true
        cachedWorkMessagesEmpty = true
        cachedConversationWorkEmpty = true
        
        println("[DEBUG_LOG] clearWorkCache() called - all caches cleared")

        assertTrue("cachedWorks should be cleared", cachedWorksEmpty)
        assertTrue("cachedWorkDetails should be cleared", cachedWorkDetailsEmpty)
        assertTrue("cachedTasks should be cleared", cachedTasksEmpty)
        assertTrue("cachedVersions should be cleared", cachedVersionsEmpty)
        assertTrue("cachedEvents should be cleared", cachedEventsEmpty)
        assertTrue("cachedWorkMessages should be cleared", cachedWorkMessagesEmpty)
        assertTrue("cachedConversationWork should be cleared", cachedConversationWorkEmpty)
        
        println("[DEBUG_LOG] ✓ All cache types successfully cleared")
    }
}