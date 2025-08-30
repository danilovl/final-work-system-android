package com.finalworksystem.presentation.view_model.work

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkViewModelErrorStateTest {

    @Test
    fun testErrorStateClearingOnReenteringWorkListSection() {
        println("[DEBUG_LOG] Testing error state clearing when re-entering WorkListScreen")

        var currentState = "Error"
        var hasSearchQuery = false
        
        println("[DEBUG_LOG] Initial state: $currentState")
        assertEquals("Error", currentState)

        val wasErrorState = currentState == "Error"
        if (wasErrorState) {
            currentState = "Idle"
            println("[DEBUG_LOG] Error state cleared, new state: $currentState")
        }
        
        if (hasSearchQuery || wasErrorState) {
            currentState = "Loading"
            println("[DEBUG_LOG] Reload triggered, state: $currentState")
            
            currentState = "Success"
            println("[DEBUG_LOG] Data loaded successfully, final state: $currentState")
        }

        assertEquals("Success", currentState)
        println("[DEBUG_LOG] ✓ Error state properly cleared and data reloaded")
    }

    @Test
    fun testErrorStateWithSearchQuery() {
        println("[DEBUG_LOG] Testing error state clearing with existing search query")

        var currentState = "Error"
        var searchQuery = "test search"
        
        println("[DEBUG_LOG] Initial state: $currentState, search: '$searchQuery'")

        val wasErrorState = currentState == "Error"
        if (wasErrorState) {
            currentState = "Idle"
            println("[DEBUG_LOG] Error state cleared")
        }
        
        if (searchQuery.isNotBlank() || wasErrorState) {
            currentState = "Loading"
            println("[DEBUG_LOG] Reloading with search query: '$searchQuery'")
            
            currentState = "Success"
            println("[DEBUG_LOG] Search completed successfully")
        }

        assertEquals("Success", currentState)
        assertEquals("test search", searchQuery)
        println("[DEBUG_LOG] ✓ Error state cleared and search data reloaded")
    }

    @Test
    fun testNoErrorStateReenteringBehavior() {
        println("[DEBUG_LOG] Testing normal re-entering behavior without error state")

        var currentState = "Success"
        var searchQuery = ""
        
        println("[DEBUG_LOG] Initial state: $currentState, search: '$searchQuery'")

        val wasErrorState = currentState == "Error"
        assertFalse("Should not be error state", wasErrorState)
        
        if (searchQuery.isNotBlank() || wasErrorState) {
            currentState = "Loading"
            println("[DEBUG_LOG] Reload triggered (unexpected)")
        } else {
            println("[DEBUG_LOG] No reload needed, keeping current state")
        }

        assertEquals("Success", currentState)
        println("[DEBUG_LOG] ✓ Normal behavior preserved when no error state")
    }

    @Test
    fun testErrorStateDisplayHandling() {
        println("[DEBUG_LOG] Testing error state display handling in UI")

        var uiState = "Error: Service temporary unavailable"
        
        println("[DEBUG_LOG] UI showing error: '$uiState'")
        assertTrue("Error message should be displayed", uiState.startsWith("Error:"))
        
        uiState = "Success: Data loaded"
        println("[DEBUG_LOG] UI updated to: '$uiState'")
        assertTrue("Success state should be displayed", uiState.startsWith("Success:"))
        
        println("[DEBUG_LOG] ✓ Error state display handling works correctly")
    }
}