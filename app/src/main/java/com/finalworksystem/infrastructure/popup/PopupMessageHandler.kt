package com.finalworksystem.infrastructure.popup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.finalworksystem.ui.designsystem.AppSnackbarDuration
import com.finalworksystem.ui.designsystem.AppSnackbarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun PopupMessageHandler(
    popupMessageService: PopupMessageService,
    snackbarHostState: AppSnackbarHostState,
    coroutineScope: CoroutineScope
) {
    val messageQueue = remember { popupMessageService.messageQueue }
    val isShowingMessage = remember { popupMessageService.isShowingMessage }
    val resources = androidx.compose.ui.platform.LocalResources.current

    LaunchedEffect(messageQueue.size, isShowingMessage.value) {
        if (isShowingMessage.value || messageQueue.isEmpty()) {
            return@LaunchedEffect
        }

        val nextMessage = popupMessageService.processNextMessage()
        nextMessage?.let { popupMessage ->
            val messageText = popupMessage.message ?: popupMessage.resourceId?.let { resources.getString(it) } ?: ""
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = messageText,
                    duration = AppSnackbarDuration.Short,
                    actionLabel = null,
                    withDismissAction = true
                )
                popupMessageService.setNotShowingMessage()
            }
        }
    }
}
