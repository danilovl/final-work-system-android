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

    LaunchedEffect(messageQueue.size, isShowingMessage.value) {
        if (!isShowingMessage.value || messageQueue.isEmpty()) {
            return@LaunchedEffect
        }

        val nextMessage = popupMessageService.processNextMessage()
        nextMessage?.let { popupMessage ->
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = popupMessage.message,
                    duration = SnackbarDuration.Short,
                    actionLabel = null,
                    withDismissAction = true
                )
                popupMessageService.setNotShowingMessage()
            }
        }
    }
}
