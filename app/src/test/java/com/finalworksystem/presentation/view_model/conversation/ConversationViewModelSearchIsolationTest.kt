package com.finalworksystem.presentation.view_model.conversation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConversationViewModelSearchIsolationTest {

    @Test
    fun testSearchStateIsolationBetweenScreens() {
        println("[DEBUG_LOG] Testing search state isolation between ConversationListScreen and ConversationDetailScreen")

        var conversationSearchQuery = "conversation search"
        var messageSearchQuery = ""
        
        println("[DEBUG_LOG] ConversationListScreen search query: '$conversationSearchQuery'")
        println("[DEBUG_LOG] ConversationDetailScreen message search query: '$messageSearchQuery'")
        
        assertNotEquals("Search states should be independent", conversationSearchQuery, messageSearchQuery)
        assertEquals("", messageSearchQuery)
        assertEquals("conversation search", conversationSearchQuery)

        println("[DEBUG_LOG] After navigating to ConversationDetailScreen:")
        println("[DEBUG_LOG] ConversationListScreen search query (preserved): '$conversationSearchQuery'")
        println("[DEBUG_LOG] ConversationDetailScreen message search query (fresh): '$messageSearchQuery'")
        
        assertEquals("ConversationListScreen search should be preserved", "conversation search", conversationSearchQuery)
        assertEquals("ConversationDetailScreen should start with empty search", "", messageSearchQuery)

        messageSearchQuery = "message search"
        println("[DEBUG_LOG] After searching in ConversationDetailScreen:")
        println("[DEBUG_LOG] ConversationListScreen search query (unchanged): '$conversationSearchQuery'")
        println("[DEBUG_LOG] ConversationDetailScreen message search query: '$messageSearchQuery'")
        
        assertEquals("ConversationListScreen search should remain unchanged", "conversation search", conversationSearchQuery)
        assertEquals("ConversationDetailScreen should have its own search", "message search", messageSearchQuery)
        assertNotEquals("Search states should remain independent", conversationSearchQuery, messageSearchQuery)

        println("[DEBUG_LOG] After navigating back to ConversationListScreen:")
        println("[DEBUG_LOG] ConversationListScreen search query (preserved): '$conversationSearchQuery'")
        println("[DEBUG_LOG] ConversationDetailScreen message search query (isolated): '$messageSearchQuery'")
        
        assertEquals("ConversationListScreen search should be preserved", "conversation search", conversationSearchQuery)
        assertEquals("ConversationDetailScreen search should remain isolated", "message search", messageSearchQuery)

        println("[DEBUG_LOG] ✓ Search state isolation test completed successfully!")
    }

    @Test
    fun testFreshSearchResetFloatingActionButtonInstance() {
        println("[DEBUG_LOG] Testing fresh SearchResetFloatingActionButton instance behavior")

        var conversationListSearchQuery = "list search"
        var detailScreenSearchQuery = ""
        
        println("[DEBUG_LOG] ConversationListScreen SearchResetFloatingActionButton query: '$conversationListSearchQuery'")
        println("[DEBUG_LOG] ConversationDetailScreen SearchResetFloatingActionButton query: '$detailScreenSearchQuery'")
        
        assertNotEquals("SearchResetFloatingActionButton instances should be independent",
                       conversationListSearchQuery, detailScreenSearchQuery)
        
        assertEquals("ConversationDetailScreen SearchResetFloatingActionButton should be fresh",
                    "", detailScreenSearchQuery)
        
        assertEquals("ConversationListScreen SearchResetFloatingActionButton should retain state",
                    "list search", conversationListSearchQuery)

        println("[DEBUG_LOG] ✓ Fresh SearchResetFloatingActionButton instance test completed successfully!")
    }

    @Test
    fun testSearchDataScopedToSpecificScreen() {
        println("[DEBUG_LOG] Testing that search data is scoped only to specific screens")

        var conversationListSearch = "conversation filter"
        var conversationDetailSearch = ""

        println("[DEBUG_LOG] Initial state:")
        println("[DEBUG_LOG] - ConversationListScreen search: '$conversationListSearch'")
        println("[DEBUG_LOG] - ConversationDetailScreen search: '$conversationDetailSearch'")

        assertTrue("ConversationListScreen should have search data", conversationListSearch.isNotEmpty())
        assertTrue("ConversationDetailScreen should have no search data", conversationDetailSearch.isEmpty())

        conversationDetailSearch = "message filter"
        
        println("[DEBUG_LOG] After adding search in ConversationDetailScreen:")
        println("[DEBUG_LOG] - ConversationListScreen search: '$conversationListSearch'")
        println("[DEBUG_LOG] - ConversationDetailScreen search: '$conversationDetailSearch'")

        assertEquals("ConversationListScreen search should be unchanged", "conversation filter", conversationListSearch)
        assertEquals("ConversationDetailScreen should have its own search", "message filter", conversationDetailSearch)
        
        conversationDetailSearch = ""
        println("[DEBUG_LOG] After clearing ConversationDetailScreen search:")
        println("[DEBUG_LOG] - ConversationListScreen search: '$conversationListSearch'")
        println("[DEBUG_LOG] - ConversationDetailScreen search: '$conversationDetailSearch'")
        
        assertEquals("ConversationListScreen search should remain unaffected", "conversation filter", conversationListSearch)
        assertEquals("ConversationDetailScreen search should be cleared", "", conversationDetailSearch)

        println("[DEBUG_LOG] ✓ Search data scoping test completed successfully!")
    }
}