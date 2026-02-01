package com.finalworksystem.presentation.ui.conversation

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.finalworksystem.R
import com.finalworksystem.domain.conversation.model.getDisplayName
import com.finalworksystem.domain.user.model.User
import com.finalworksystem.presentation.ui.component.SearchModal
import com.finalworksystem.presentation.ui.component.SearchResetFloatingActionButton
import com.finalworksystem.presentation.ui.conversation.component.MessageForm
import com.finalworksystem.presentation.ui.conversation.component.MessagesList
import com.finalworksystem.presentation.ui.conversation.component.ParticipantsFullList
import com.finalworksystem.presentation.view_model.conversation.ConversationDetailViewModel
import com.finalworksystem.presentation.view_model.conversation.state.ConversationDetailState
import com.finalworksystem.presentation.view_model.conversation.state.toGeneric

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationDetailScreen(
    conversationId: Int,
    currentUserId: Int,
    currentUser: User?,
    viewModel: ConversationDetailViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val conversationDetailState by viewModel.conversationDetailState.collectAsState()
    val messagesState by viewModel.messagesState.collectAsState()
    val sendMessageState by viewModel.sendMessageState.collectAsState()
    val messageSearchQuery by viewModel.messageSearchQuery.collectAsState()

    var isSearchModalVisible by remember { mutableStateOf(false) }

    LaunchedEffect(conversationId) {
        viewModel.updateMessageSearchQuery("")
        viewModel.loadConversationDetail(conversationId)
        viewModel.loadMessages(conversationId)
    }

    when (val detailState = conversationDetailState) {
        is ConversationDetailState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is ConversationDetailState.Success -> {
            var showParticipantsSheet by remember { mutableStateOf(false) }
            val sheetState = rememberModalBottomSheetState()
            val conversationTitle = detailState.conversation.getDisplayName()

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = conversationTitle,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                viewModel.updateMessageSearchQuery("")
                                onNavigateBack()
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.back)
                                )
                            }
                        },
                        actions = {
                            if (messageSearchQuery.isNotBlank()) {
                                IconButton(onClick = {
                                    viewModel.clearMessageSearch(conversationId)
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = stringResource(R.string.clear_search)
                                    )
                                }
                            }
                            IconButton(onClick = {
                                viewModel.loadConversationDetail(conversationId)
                                viewModel.loadMessages(conversationId, messageSearchQuery.takeIf { it.isNotBlank() })
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = stringResource(R.string.refresh)
                                )
                            }
                        }
                    )
                },
                floatingActionButton = {
                    SearchResetFloatingActionButton(
                        searchQuery = messageSearchQuery,
                        onSearchClick = { isSearchModalVisible = true },
                        onResetClick = { viewModel.clearMessageSearch(conversationId) }
                    )
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                if (change.position.x > size.width * 0.8f && dragAmount.x < -50f) {
                                    showParticipantsSheet = true
                                }

                                if (change.position.y > size.height * 0.8f && dragAmount.y < -50f) {
                                    showParticipantsSheet = true
                                }
                            }
                        }
                ) {
                    MessagesList(
                        messagesState = messagesState.toGeneric(),
                        currentUserId = currentUserId,
                        conversationId = conversationId,
                        onMarkAsRead = { convId, messageId ->
                            viewModel.markMessageAsRead(convId, messageId)
                        },
                        onLoadMore = { viewModel.loadMoreMessages(conversationId) },
                        modifier = Modifier.weight(1f)
                    )

                    MessageForm(
                        onSendMessage = { message ->
                            viewModel.sendMessage(conversationId, message)
                        },
                        sendMessageState = sendMessageState,
                        currentUser = currentUser,
                        conversationWork = detailState.conversation.work,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            if (showParticipantsSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showParticipantsSheet = false },
                    sheetState = sheetState
                ) {
                    ParticipantsFullList(
                        participants = detailState.conversation.participants,
                        onDismiss = { showParticipantsSheet = false }
                    )
                }
            }
        }
                is ConversationDetailState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stringResource(R.string.error_prefix, detailState.message),
                                color = MaterialTheme.colorScheme.error
                            )
                            IconButton(onClick = {
                                viewModel.loadConversationDetail(conversationId)
                                viewModel.loadMessages(conversationId)
                            }) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowUp,
                                    contentDescription = stringResource(R.string.retry),
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                    }
                }
        is ConversationDetailState.Idle -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }

    SearchModal(
        isVisible = isSearchModalVisible,
        searchQuery = messageSearchQuery,
        onSearchQueryChange = { viewModel.updateMessageSearchQuery(it) },
        onSearch = { query ->
            viewModel.performMessageSearch(conversationId, query)
        },
        onDismiss = { isSearchModalVisible = false }
    )
}
