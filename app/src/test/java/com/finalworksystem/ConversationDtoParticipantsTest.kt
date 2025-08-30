package com.finalworksystem

import com.finalworksystem.data.conversation.model.ConversationDto
import com.finalworksystem.data.conversation.model.ConversationParticipantDto
import com.finalworksystem.data.conversation.model.ConversationUserDto
import com.finalworksystem.data.conversation.model.toEntity
import org.junit.Test
import org.junit.Assert.*

class ConversationDtoParticipantsTest {

    @Test
    fun `toEntity maps participants correctly when participants exist`() {
        println("[DEBUG_LOG] Testing ConversationDto.toEntity() with participants")

        val userDto = ConversationUserDto(
            id = 1,
            username = "johndoe",
            firstname = "John",
            lastname = "Doe",
            email = "john.doe@example.com",
            degreeBefore = null,
            degreeAfter = null
        )

        val participantDto = ConversationParticipantDto(
            id = 1,
            user = userDto
        )

        val conversationDto = ConversationDto(
            id = 1,
            name = "Test Conversation",
            isRead = true,
            recipient = null,
            work = null,
            lastMessage = null,
            participants = listOf(participantDto)
        )

        val domainConversation = conversationDto.toEntity()

        assertNotNull("Participants should not be null", domainConversation.participants)
        assertEquals("Should have 1 participant", 1, domainConversation.participants.size)
        assertEquals("Participant ID should match", 1, domainConversation.participants[0].id)
        assertEquals("User firstname should match", "John", domainConversation.participants[0].user.firstname)
        assertEquals("User lastname should match", "Doe", domainConversation.participants[0].user.lastname)

        println("[DEBUG_LOG] Participants mapping test passed successfully")
    }

    @Test
    fun `toEntity handles null participants correctly`() {
        println("[DEBUG_LOG] Testing ConversationDto.toEntity() with null participants")

        val conversationDto = ConversationDto(
            id = 1,
            name = "Test Conversation",
            isRead = true,
            recipient = null,
            work = null,
            lastMessage = null,
            participants = null
        )

        val domainConversation = conversationDto.toEntity()

        assertNotNull("Participants should not be null", domainConversation.participants)
        assertTrue("Participants should be empty when null", domainConversation.participants.isEmpty())

        println("[DEBUG_LOG] Null participants handling test passed successfully")
    }
}
