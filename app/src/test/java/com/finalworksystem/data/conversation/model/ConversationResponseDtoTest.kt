package com.finalworksystem.data.conversation.model

import com.finalworksystem.data.conversation.model.response.WorkStatusDto
import com.finalworksystem.data.conversation.model.response.WorkTypeDto
import org.junit.Assert.assertEquals
import org.junit.Test

class ConversationResponseDtoTest {

    @Test
    fun `getDisplayName returns work title when work is not null`() {
        val work = ConversationWorkDto(
            id = 1,
            title = "Test Work Title",
            shortcut = null,
            deadline = "2024-01-01",
            deadlineProgram = null,
            status = WorkStatusDto(
                id = 1,
                name = "Active",
                description = "Work is active",
                color = "#00FF00"
            ),
            type = WorkTypeDto(
                id = 1,
                name = "Bachelor",
                description = "Bachelor thesis",
                shortcut = "BC"
            ),
            author = null,
            supervisor = null,
            opponent = null,
            consultant = null
        )

        val conversation = ConversationDto(
            id = 1,
            name = "Test Conversation",
            isRead = true,
            recipient = null,
            work = work,
            lastMessage = null
        )

        val displayName = conversation.getDisplayName()

        assertEquals("Test Work Title", displayName)
    }

    @Test
    fun `getDisplayName returns conversation name when work is null`() {
        val conversation = ConversationDto(
            id = 1,
            name = "Test Conversation",
            isRead = true,
            recipient = null,
            work = null,
            lastMessage = null
        )

        val displayName = conversation.getDisplayName()

        assertEquals("Test Conversation", displayName)
    }

    @Test
    fun `getDisplayName returns default name when both work and name are null`() {
        val conversation = ConversationDto(
            id = 1,
            name = null,
            isRead = true,
            recipient = null,
            work = null,
            lastMessage = null
        )

        val displayName = conversation.getDisplayName()

        assertEquals("Unnamed Conversation", displayName)
    }
}