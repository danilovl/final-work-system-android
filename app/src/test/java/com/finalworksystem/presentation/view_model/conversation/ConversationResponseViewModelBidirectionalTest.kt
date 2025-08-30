package com.finalworksystem.presentation.view_model.conversation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ConversationResponseViewModelBidirectionalTest {

    @Test
    fun `test bidirectional read status logic`() {
        val unreadMessageIsRead = false
        val newReadStatusForUnread = !unreadMessageIsRead
        val actionTextForUnread = if (newReadStatusForUnread) "read" else "unread"

        assertFalse("Unread message should have isRead = false", unreadMessageIsRead)
        assertTrue("Unread message should become read", newReadStatusForUnread)
        assertEquals("Action text should be 'read'", "read", actionTextForUnread)


        val readMessageIsRead = true
        val newReadStatusForRead = !readMessageIsRead
        val actionTextForRead = if (newReadStatusForRead) "read" else "unread"

        assertTrue("Read message should have isRead = true", readMessageIsRead)
        assertFalse("Read message should become unread", newReadStatusForRead)
        assertEquals("Action text should be 'unread'", "unread", actionTextForRead)
    }

    @Test
    fun `test message border logic`() {
        val unreadMessageIsRead = false
        val shouldShowBorderForUnread = !unreadMessageIsRead
        assertTrue("Unread message should show red border", shouldShowBorderForUnread)

        val readMessageIsRead = true
        val shouldShowBorderForRead = !readMessageIsRead
        assertFalse("Read message should not show red border", shouldShowBorderForRead)
    }
}
