package com.finalworksystem.presentation.ui.conversation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.finalworksystem.R
import com.finalworksystem.domain.conversation.model.ConversationWork
import com.finalworksystem.domain.user.model.User
import com.finalworksystem.domain.user.model.UserRole
import com.finalworksystem.presentation.view_model.conversation.state.SendMessageState

@Composable
fun MessageForm(
    onSendMessage: (String) -> Unit,
    sendMessageState: SendMessageState,
    currentUser: User?,
    conversationWork: ConversationWork?,
    modifier: Modifier = Modifier
) {
    val shouldShowForm = currentUser?.let { user ->
        user.roles.contains(UserRole.SUPERVISOR.value) ||
        (conversationWork != null)
    } ?: false

    if (!shouldShowForm) {
        return
    }

    var messageText by remember { mutableStateOf("") }

    LaunchedEffect(sendMessageState) {
        if (sendMessageState is SendMessageState.Success) {
            messageText = ""
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = messageText,
            onValueChange = { messageText = it },
            placeholder = { Text(stringResource(R.string.type_message_placeholder)) },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 4,
            minLines = 1,
            isError = sendMessageState is SendMessageState.Error,
            supportingText = {
                if (sendMessageState is SendMessageState.Error) {
                    Text(
                        text = sendMessageState.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (messageText.isNotBlank()) {
                    onSendMessage(messageText.trim())
                }
            },
            enabled = messageText.isNotBlank() && sendMessageState !is SendMessageState.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (sendMessageState is SendMessageState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.width(16.dp).height(16.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.sending))
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = stringResource(R.string.send_message)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.send_message))
            }
        }
    }
}
