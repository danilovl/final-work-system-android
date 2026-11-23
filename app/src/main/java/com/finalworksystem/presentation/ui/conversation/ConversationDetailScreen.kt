package com.finalworksystem.presentation.ui.conversation

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.finalworksystem.R
import com.finalworksystem.domain.conversation.model.getDisplayName
import com.finalworksystem.domain.user.model.User
import com.finalworksystem.presentation.ui.component.BackNavigationButton
import com.finalworksystem.presentation.ui.component.SearchModal
import com.finalworksystem.presentation.ui.component.SearchResetFloatingActionButton
import com.finalworksystem.presentation.ui.conversation.component.MessageForm
import com.finalworksystem.presentation.ui.conversation.component.MessagesList
import com.finalworksystem.presentation.ui.conversation.component.ParticipantsFullList
import com.finalworksystem.presentation.view_model.conversation.ConversationDetailViewModel
import com.finalworksystem.presentation.view_model.conversation.state.ConversationDetailState
import com.finalworksystem.ui.designsystem.AppBottomSheet
import com.finalworksystem.ui.designsystem.AppButton
import com.finalworksystem.ui.designsystem.AppButtonVariant
import com.finalworksystem.ui.designsystem.AppEmptyState
import com.finalworksystem.ui.designsystem.AppIconButton
import com.finalworksystem.ui.designsystem.AppLoadingState
import com.finalworksystem.ui.designsystem.AppScaffold
import com.finalworksystem.ui.designsystem.AppTopBar
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme

@Composable
fun ConversationDetailScreen(
    conversationId: Int,
    currentUserId: Int,
    currentUser: User?,
    viewModel: ConversationDetailViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val conversationDetailState by viewModel.conversationDetailState.collectAsStateWithLifecycle()
    val messages = viewModel.messages.collectAsLazyPagingItems()
    val sendMessageState by viewModel.sendMessageState.collectAsStateWithLifecycle()
    val messageSearchQuery by viewModel.messageSearchQuery.collectAsStateWithLifecycle()

    var isSearchModalVisible by remember { mutableStateOf(false) }

    LaunchedEffect(conversationId) {
        viewModel.updateMessageSearchQuery("")
        viewModel.loadConversationDetail(conversationId)
        viewModel.loadMessages(conversationId)
    }

    LaunchedEffect(conversationDetailState) {
        if (conversationDetailState is ConversationDetailState.NotFound) {
            viewModel.updateMessageSearchQuery("")
            onNavigateBack()
        }
    }

    when (val detailState = conversationDetailState) {
        is ConversationDetailState.Loading -> {
            AppLoadingState()
        }
        is ConversationDetailState.Success -> {
            var showParticipantsSheet by remember { mutableStateOf(false) }
            val conversationTitle = detailState.conversation.getDisplayName()
            val participantsCount = detailState.conversation.participants.size

            AppScaffold(
                topBar = {
                    AppTopBar(
                        title = conversationTitle,
                        subtitle = stringResource(R.string.participants_count, participantsCount),
                        navigationIcon = {
                            BackNavigationButton(onClick = {
                                viewModel.updateMessageSearchQuery("")
                                onNavigateBack()
                            })
                        },
                        actions = {
                            if (messageSearchQuery.isNotBlank()) {
                                AppIconButton(
                                    icon = AppIcons.Clear,
                                    contentDescription = stringResource(R.string.clear_search),
                                    onClick = { viewModel.clearMessageSearch(conversationId) }
                                )
                            }
                            AppIconButton(
                                icon = AppIcons.Groups,
                                contentDescription = stringResource(R.string.participants_count, participantsCount),
                                onClick = { showParticipantsSheet = true }
                            )
                            AppIconButton(
                                icon = AppIcons.Refresh,
                                contentDescription = stringResource(R.string.refresh),
                                onClick = {
                                    viewModel.loadConversationDetail(conversationId)
                                    viewModel.loadMessages(conversationId, messageSearchQuery.takeIf { it.isNotBlank() })
                                }
                            )
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
                        .consumeWindowInsets(paddingValues)
                        .imePadding()
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
                        messages = messages,
                        currentUserId = currentUserId,
                        conversationId = conversationId,
                        onMarkAsRead = { convId, messageId ->
                            viewModel.markMessageAsRead(convId, messageId)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp)
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
                AppBottomSheet(
                    onDismissRequest = { showParticipantsSheet = false }
                ) {
                    ParticipantsFullList(
                        participants = detailState.conversation.participants,
                        onDismiss = { showParticipantsSheet = false }
                    )
                }
            }
        }
        is ConversationDetailState.Error -> {
            AppEmptyState(
                title = stringResource(R.string.error_prefix, detailState.message),
                icon = AppIcons.Info,
                iconTint = AppTheme.colors.error,
                action = {
                    AppButton(
                        text = stringResource(R.string.retry),
                        onClick = {
                            viewModel.loadConversationDetail(conversationId)
                            viewModel.loadMessages(conversationId)
                        },
                        variant = AppButtonVariant.Primary,
                        leadingIcon = AppIcons.Refresh
                    )
                }
            )
        }
        is ConversationDetailState.Idle -> {
            AppLoadingState()
        }
        is ConversationDetailState.NotFound -> {
            // Handled by LaunchedEffect
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
