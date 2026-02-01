package com.finalworksystem.infrastructure.popup

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun PopupMessageHandler(
    popupMessageService: PopupMessageService,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope
) {
    val messageQueue = remember { popupMessageService.messageQueue }
    val isShowingMessage = remember { popupMessageService.isShowingMessage }
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(messageQueue.size, isShowingMessage.value) {
        if (isShowingMessage.value || messageQueue.isEmpty()) {
            return@LaunchedEffect
        }

        val nextMessage = popupMessageService.processNextMessage()
        nextMessage?.let { popupMessage ->
            val messageText = popupMessage.message ?: popupMessage.resourceId?.let { context.getString(it) } ?: ""
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = messageText,
                    duration = SnackbarDuration.Short,
                    actionLabel = null,
                    withDismissAction = true
                )
                popupMessageService.setNotShowingMessage()
            }
        }
    }
}
