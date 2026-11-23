package com.finalworksystem.presentation.ui.conversation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.finalworksystem.R
import com.finalworksystem.domain.conversation.model.ConversationWork
import com.finalworksystem.domain.user.model.User
import com.finalworksystem.domain.user.model.UserRole
import com.finalworksystem.presentation.view_model.conversation.state.SendMessageState
import com.finalworksystem.ui.designsystem.AppDivider
import com.finalworksystem.ui.designsystem.AppIcon
import com.finalworksystem.ui.designsystem.AppSpinner
import com.finalworksystem.ui.designsystem.AppSurface
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme

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

    val colors = AppTheme.colors
    val isError = sendMessageState is SendMessageState.Error
    val isLoading = sendMessageState is SendMessageState.Loading
    val canSend = messageText.isNotBlank() && !isLoading

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
    ) {
        AppDivider()

        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            if (sendMessageState is SendMessageState.Error) {
                AppText(
                    text = sendMessageState.message,
                    style = AppTheme.typography.labelMedium,
                    color = colors.error,
                    modifier = Modifier.padding(start = 16.dp, bottom = 6.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                PillTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = stringResource(R.string.type_message_placeholder),
                    isError = isError,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                SendButton(
                    enabled = canSend,
                    loading = isLoading,
                    onClick = {
                        if (messageText.isNotBlank()) {
                            onSendMessage(messageText.trim())
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun PillTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val shape = RoundedCornerShape(24.dp)
    val textStyle = AppTheme.typography.bodyLarge.merge(TextStyle(color = colors.onSurface))

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        textStyle = textStyle,
        cursorBrush = SolidColor(colors.primary),
        minLines = 1,
        maxLines = 4,
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .background(colors.surfaceVariant, shape)
                    .border(
                        BorderStroke(1.dp, if (isError) colors.error else colors.outlineVariant),
                        shape
                    )
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty()) {
                    AppText(
                        text = placeholder,
                        style = AppTheme.typography.bodyLarge,
                        color = colors.onSurfaceVariant,
                        maxLines = 1
                    )
                }
                innerTextField()
            }
        }
    )
}

@Composable
private fun SendButton(
    enabled: Boolean,
    loading: Boolean,
    onClick: () -> Unit
) {
    val colors = AppTheme.colors
    val background = if (enabled) {
        Brush.linearGradient(listOf(colors.primary, colors.primaryGradientEnd))
    } else {
        SolidColor(colors.onSurface.copy(alpha = 0.08f))
    }
    val contentColor = if (enabled) colors.onPrimary else colors.onSurface.copy(alpha = 0.38f)

    AppSurface(
        modifier = Modifier.size(48.dp),
        shape = CircleShape,
        color = colors.surface.copy(alpha = 0f),
        contentColor = contentColor,
        onClick = onClick,
        enabled = enabled,
        pressScale = true
    ) {
        Box(
            modifier = Modifier.background(background, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (loading) {
                AppSpinner(
                    modifier = Modifier.size(20.dp),
                    color = colors.primary,
                    strokeWidth = 2.dp
                )
            } else {
                AppIcon(
                    imageVector = AppIcons.Send,
                    contentDescription = stringResource(R.string.send_message),
                    tint = contentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
