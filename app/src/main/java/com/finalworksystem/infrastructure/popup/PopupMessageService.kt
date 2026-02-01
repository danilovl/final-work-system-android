package com.finalworksystem.infrastructure.popup

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color

class PopupMessageService {
    enum class MessageLevel {
        ERROR,
        WARNING,
        INFO,
        SUCCESS
    }

    private val _messageQueue = mutableStateListOf<PopupMessage>()
    val messageQueue: SnapshotStateList<PopupMessage> = _messageQueue

    private val _isShowingMessage = mutableStateOf(false)
    val isShowingMessage: MutableState<Boolean> = _isShowingMessage

    private val _currentMessage = mutableStateOf<PopupMessage?>(null)
    val currentMessage: MutableState<PopupMessage?> = _currentMessage

    fun getColorForLevel(level: MessageLevel): Color {
        return when (level) {
            MessageLevel.ERROR -> Color(0xFFE57373)
            MessageLevel.WARNING -> Color(0xFFFFD54F)
            MessageLevel.INFO -> Color(0xFF90A4AE)
            MessageLevel.SUCCESS -> Color(0xFF81C784)
        }
    }

    fun showMessage(message: String, level: MessageLevel) {
        _messageQueue.add(PopupMessage(message = message, level = level))
    }

    fun showMessageResource(resourceId: Int, level: MessageLevel) {
        _messageQueue.add(PopupMessage(resourceId = resourceId, level = level))
    }

    fun processNextMessage(): PopupMessage? {
        if (_messageQueue.isEmpty()) {
            _isShowingMessage.value = false
            _currentMessage.value = null

            return null
        }

        _isShowingMessage.value = true
        val nextMessage = _messageQueue.removeFirstOrNull()
        _currentMessage.value = nextMessage

        return nextMessage
    }

    fun setNotShowingMessage() {
        _isShowingMessage.value = false
    }

    data class PopupMessage(
        val message: String? = null,
        val resourceId: Int? = null,
        val level: MessageLevel
    )
}
