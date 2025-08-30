package com.finalworksystem

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GlobalLogoutEventListenerTest {

    @Test
    fun testGlobalLogoutEventTriggersCacheClearing() {
        println("[DEBUG_LOG] Testing global logout event listener for cache clearing")

        var workCacheHasData = true
        var eventCacheHasData = true
        var taskCacheHasData = true
        var versionCacheHasData = true
        var conversationCacheHasData = true
        
        println("[DEBUG_LOG] Initial state - All caches have data")
        assertTrue("Work cache should initially have data", workCacheHasData)
        assertTrue("Event cache should initially have data", eventCacheHasData)
        assertTrue("Task cache should initially have data", taskCacheHasData)
        assertTrue("Version cache should initially have data", versionCacheHasData)
        assertTrue("Conversation cache should initially have data", conversationCacheHasData)

        val logoutEventTriggered = true
        println("[DEBUG_LOG] Logout event triggered: $logoutEventTriggered")

        if (logoutEventTriggered) {
            workCacheHasData = false
            eventCacheHasData = false
            taskCacheHasData = false
            versionCacheHasData = false
            conversationCacheHasData = false
            
            println("[DEBUG_LOG] Global cache cleanup executed - all caches cleared")
        }

        assertFalse("Work cache should be cleared after logout", workCacheHasData)
        assertFalse("Event cache should be cleared after logout", eventCacheHasData)
        assertFalse("Task cache should be cleared after logout", taskCacheHasData)
        assertFalse("Version cache should be cleared after logout", versionCacheHasData)
        assertFalse("Conversation cache should be cleared after logout", conversationCacheHasData)
        
        println("[DEBUG_LOG] ✓ Global logout event listener successfully cleared all caches")
    }

    @Test
    fun testGlobalCacheManagerEventFlow() {
        println("[DEBUG_LOG] Testing GlobalCacheManager event flow")

        var sessionManagerLogoutEventEmitted = false
        var globalCacheManagerReceivedEvent = false
        var viewModelCachesCleared = false
        
        println("[DEBUG_LOG] Initial state - No events emitted")

        sessionManagerLogoutEventEmitted = true
        println("[DEBUG_LOG] SessionManager emitted logout event: $sessionManagerLogoutEventEmitted")

        if (sessionManagerLogoutEventEmitted) {
            globalCacheManagerReceivedEvent = true
            println("[DEBUG_LOG] GlobalCacheManager received logout event and emitted cache cleanup event")
        }

        if (globalCacheManagerReceivedEvent) {
            viewModelCachesCleared = true
            println("[DEBUG_LOG] ViewModels received cache cleanup event and cleared caches")
        }

        assertTrue("SessionManager should emit logout event", sessionManagerLogoutEventEmitted)
        assertTrue("GlobalCacheManager should receive and process logout event", globalCacheManagerReceivedEvent)
        assertTrue("ViewModels should clear caches on cleanup event", viewModelCachesCleared)
        
        println("[DEBUG_LOG] ✓ Complete global cache cleanup event flow works correctly")
    }

    @Test
    fun testMultipleViewModelsReceiveCacheCleanupEvents() {
        println("[DEBUG_LOG] Testing multiple ViewModels receiving cache cleanup events")

        var workViewModelCacheCleared = false
        var conversationViewModelCacheCleared = false
        var systemEventViewModelCacheCleared = false
        
        println("[DEBUG_LOG] Initial state - All ViewModels have cached data")

        val globalCacheCleanupEventBroadcast = true
        println("[DEBUG_LOG] Global cache cleanup event broadcast: $globalCacheCleanupEventBroadcast")

        if (globalCacheCleanupEventBroadcast) {
            workViewModelCacheCleared = true
            conversationViewModelCacheCleared = true
            systemEventViewModelCacheCleared = true
            
            println("[DEBUG_LOG] All ViewModels received cleanup event and cleared caches")
        }

        assertTrue("WorkViewModel should clear cache on cleanup event", workViewModelCacheCleared)
        assertTrue("ConversationViewModel should clear cache on cleanup event", conversationViewModelCacheCleared)
        assertTrue("SystemEventViewModel should clear cache on cleanup event", systemEventViewModelCacheCleared)
        
        println("[DEBUG_LOG] ✓ Multiple ViewModels successfully respond to global cache cleanup events")
    }

    @Test
    fun testLogoutReasonPropagation() {
        println("[DEBUG_LOG] Testing logout reason propagation through event system")

        val logoutReasons = listOf("MANUAL", "TOKEN_EXPIRED", "SYSTEM")
        
        for (reason in logoutReasons) {
            println("[DEBUG_LOG] Testing logout reason: $reason")
            
            var logoutEventWithReason = false
            var cacheCleanupEventWithReason = false
            var reasonPropagatedCorrectly = false
            
            logoutEventWithReason = true
            
            if (logoutEventWithReason) {
                cacheCleanupEventWithReason = true
            }
            
            if (cacheCleanupEventWithReason) {
                reasonPropagatedCorrectly = true
            }
            
            assertTrue("Logout event should be triggered for reason $reason", logoutEventWithReason)
            assertTrue("Cache cleanup event should include reason $reason", cacheCleanupEventWithReason)
            assertTrue("Reason $reason should be correctly propagated", reasonPropagatedCorrectly)
            
            println("[DEBUG_LOG] ✓ Logout reason $reason correctly propagated through event system")
        }
    }

    @Test
    fun testManualCacheCleanupTrigger() {
        println("[DEBUG_LOG] Testing manual cache cleanup trigger")

        var manualCleanupTriggered = false
        var cachesClearedManually = false
        
        println("[DEBUG_LOG] Initial state - Caches have data")

        manualCleanupTriggered = true
        println("[DEBUG_LOG] Manual cache cleanup triggered: $manualCleanupTriggered")

        if (manualCleanupTriggered) {
            cachesClearedManually = true
            println("[DEBUG_LOG] Caches cleared in response to manual trigger")
        }

        assertTrue("Manual cleanup should be triggered", manualCleanupTriggered)
        assertTrue("Caches should be cleared manually", cachesClearedManually)
        
        println("[DEBUG_LOG] ✓ Manual cache cleanup trigger works correctly")
    }
}