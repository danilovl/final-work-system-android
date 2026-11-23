package com.finalworksystem.presentation.ui.work

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.finalworksystem.ui.theme.AppIcons
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.finalworksystem.R
import com.finalworksystem.domain.common.util.DateUtils
import com.finalworksystem.domain.work.model.Work
import com.finalworksystem.presentation.ui.component.BackNavigationButton
import com.finalworksystem.presentation.ui.component.BaseCard
import com.finalworksystem.presentation.ui.component.TitleDialog
import com.finalworksystem.presentation.ui.work.component.EventTabContent
import com.finalworksystem.presentation.ui.work.component.TaskTabContent
import com.finalworksystem.presentation.ui.work.component.VersionTabContent
import com.finalworksystem.presentation.ui.work.component.WorkDetailConversationMessages
import com.finalworksystem.presentation.ui.work.component.WorkInfoItemWithAvatar
import com.finalworksystem.presentation.ui.work.component.WorkInfoItemWithIcon
import com.finalworksystem.presentation.view_model.work.WorkDetailViewModel
import com.finalworksystem.ui.designsystem.AppButton
import com.finalworksystem.ui.designsystem.AppButtonVariant
import com.finalworksystem.ui.designsystem.AppCardStyle
import com.finalworksystem.ui.designsystem.AppIcon
import com.finalworksystem.ui.designsystem.AppIconButton
import com.finalworksystem.ui.designsystem.AppLoadingState
import com.finalworksystem.ui.designsystem.AppScaffold
import com.finalworksystem.ui.designsystem.AppSectionHeader
import com.finalworksystem.ui.designsystem.AppSurface
import com.finalworksystem.ui.designsystem.AppTab
import com.finalworksystem.ui.designsystem.AppTabRow
import com.finalworksystem.ui.designsystem.AppTag
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.designsystem.AppTopBar
import com.finalworksystem.ui.theme.AppTheme

@Composable
fun WorkDetailScreen(
    workId: Int,
    currentUserId: Int,
    workDetailViewModel: WorkDetailViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToTaskDetail: ((Int, Int) -> Unit)? = null,
    onNavigateToTaskCreate: ((Int) -> Unit)? = null,
    onNavigateToConversationDetail: ((Int) -> Unit)? = null,
    onNavigateToEventDetail: ((Int) -> Unit)? = null
) {
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showTitleDialog by remember { mutableStateOf(false) }

    val workDetailState by workDetailViewModel.workDetailState.collectAsStateWithLifecycle()

    LaunchedEffect(workId) {
        workDetailViewModel.loadWorkDetail(workId, forceRefresh = false)
        workDetailViewModel.loadTasksForWork(workId, forceRefresh = false)
        workDetailViewModel.loadConversationWork(workId, forceRefresh = false)
    }

    LaunchedEffect(workDetailState) {
        when (workDetailState) {
            is WorkDetailViewModel.WorkDetailState.NotFound -> {
                onNavigateBack()
            }
            is WorkDetailViewModel.WorkDetailState.Error -> {
                showError = true
                errorMessage = (workDetailState as WorkDetailViewModel.WorkDetailState.Error).message
            }
            else -> {
                showError = false
            }
        }
    }

    AppScaffold(
        topBar = {
            AppTopBar(
                title = {
                    val currentState = workDetailState
                    val workTitle = when (currentState) {
                        is WorkDetailViewModel.WorkDetailState.Success -> currentState.work.title
                        else -> stringResource(R.string.work_details)
                    }
                    AppText(
                        text = workTitle,
                        style = AppTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = if (currentState is WorkDetailViewModel.WorkDetailState.Success) {
                            Modifier.clickable { showTitleDialog = true }
                        } else {
                            Modifier
                        }
                    )
                },
                navigationIcon = { BackNavigationButton(onClick = onNavigateBack) },
                actions = {
                    AppIconButton(
                        icon = AppIcons.Refresh,
                        contentDescription = stringResource(R.string.refresh),
                        onClick = {
                            workDetailViewModel.loadWorkDetail(workId, forceRefresh = true)
                            workDetailViewModel.loadTasksForWork(workId, forceRefresh = true)
                            workDetailViewModel.loadVersionsForWork(workId, forceRefresh = true)
                            workDetailViewModel.loadEventsForWork(workId, forceRefresh = true)
                            workDetailViewModel.loadWorkMessages(workId, forceRefresh = true)
                            workDetailViewModel.loadConversationWork(workId, forceRefresh = true)
                        }
                    )
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (workDetailState) {
                is WorkDetailViewModel.WorkDetailState.Loading -> {
                    AppLoadingState(modifier = Modifier.fillMaxSize())
                }
                is WorkDetailViewModel.WorkDetailState.Success -> {
                    val work = (workDetailState as WorkDetailViewModel.WorkDetailState.Success).work
                    WorkDetailContent(
                        work = work,
                        workDetailViewModel = workDetailViewModel,
                        currentUserId = currentUserId,
                        onNavigateToTaskDetail = onNavigateToTaskDetail,
                        onNavigateToTaskCreate = onNavigateToTaskCreate,
                        onNavigateToConversationDetail = onNavigateToConversationDetail,
                        onNavigateToEventDetail = onNavigateToEventDetail
                    )
                }
                else -> {
                    // nothing
                }
            }

            if (showError) {
                ErrorBanner(
                    message = errorMessage,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }

    val currentState = workDetailState
    if (currentState is WorkDetailViewModel.WorkDetailState.Success) {
        TitleDialog(
            title = currentState.work.title,
            isVisible = showTitleDialog,
            onDismiss = { showTitleDialog = false }
        )
    }
}

@Composable
private fun ErrorBanner(message: String, modifier: Modifier = Modifier) {
    AppSurface(
        modifier = modifier.fillMaxWidth(),
        shape = AppTheme.shapes.medium,
        color = AppTheme.colors.inverseSurface,
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppIcon(
                imageVector = AppIcons.Info,
                contentDescription = null,
                tint = AppTheme.colors.error,
                modifier = Modifier.size(20.dp)
            )
            AppText(
                text = message,
                style = AppTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WorkDetailContent(
    work: Work,
    workDetailViewModel: WorkDetailViewModel,
    currentUserId: Int,
    onNavigateToTaskDetail: ((Int, Int) -> Unit)? = null,
    onNavigateToTaskCreate: ((Int) -> Unit)? = null,
    onNavigateToConversationDetail: ((Int) -> Unit)? = null,
    onNavigateToEventDetail: ((Int) -> Unit)? = null
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf(
        AppTab(stringResource(R.string.task_tab), AppIcons.Task),
        AppTab(stringResource(R.string.version_tab), AppIcons.Article),
        AppTab(stringResource(R.string.message_tab), AppIcons.Comment),
        AppTab(stringResource(R.string.event_tab), AppIcons.CalendarToday)
    )
    val scrollState = rememberScrollState()

    val conversationWorkState by workDetailViewModel.conversationWorkState.collectAsStateWithLifecycle()
    val colors = AppTheme.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header card: title + tags
            BaseCard(style = AppCardStyle.Accent) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    work.shortcut?.takeIf { it.isNotBlank() }?.let { shortcut ->
                        AppText(
                            text = shortcut,
                            style = AppTheme.typography.labelMedium,
                            color = colors.onPrimaryContainer.copy(alpha = 0.75f)
                        )
                    }
                    AppText(
                        text = work.title,
                        style = AppTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.onPrimaryContainer
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AppTag(text = work.type.name, color = colors.primary, icon = AppIcons.Info)
                        AppTag(text = work.status.name, color = colors.tertiary, icon = AppIcons.CheckCircle)
                        AppTag(
                            text = DateUtils.formatToYmd(work.deadline),
                            color = colors.warning,
                            icon = AppIcons.DateRange
                        )
                    }
                }
            }

            BaseCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AppSectionHeader(
                        title = stringResource(R.string.work_information),
                        icon = AppIcons.Work
                    )

                    WorkInfoItemWithIcon(
                        icon = AppIcons.Info,
                        label = stringResource(R.string.type),
                        value = work.type.name,
                        iconTint = colors.primary
                    )

                    WorkInfoItemWithIcon(
                        icon = AppIcons.CheckCircle,
                        label = stringResource(R.string.status),
                        value = work.status.name,
                        iconTint = colors.tertiary
                    )

                    WorkInfoItemWithIcon(
                        icon = AppIcons.DateRange,
                        label = stringResource(R.string.deadline),
                        value = DateUtils.formatToYmd(work.deadline),
                        iconTint = colors.error
                    )

                    work.deadlineProgram?.let { deadlineProgram ->
                        WorkInfoItemWithIcon(
                            icon = AppIcons.DateRange,
                            label = stringResource(R.string.program_deadline),
                            value = DateUtils.formatToYmd(deadlineProgram),
                            iconTint = colors.warning
                        )
                    }
                }
            }

            BaseCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AppSectionHeader(
                        title = stringResource(R.string.people),
                        icon = AppIcons.Groups
                    )

                    work.author?.let { author ->
                        WorkInfoItemWithAvatar(
                            firstname = author.firstname,
                            lastname = author.lastname,
                            userId = author.id,
                            label = stringResource(R.string.author),
                            value = author.fullName ?: "${author.firstname} ${author.lastname}"
                        )
                    }

                    work.supervisor?.let { supervisor ->
                        WorkInfoItemWithAvatar(
                            firstname = supervisor.firstname,
                            lastname = supervisor.lastname,
                            userId = supervisor.id,
                            label = stringResource(R.string.supervisor),
                            value = supervisor.fullName ?: "${supervisor.firstname} ${supervisor.lastname}"
                        )
                    }

                    work.opponent?.let { opponent ->
                        WorkInfoItemWithAvatar(
                            firstname = opponent.firstname,
                            lastname = opponent.lastname,
                            userId = opponent.id,
                            label = stringResource(R.string.opponent),
                            value = opponent.fullName ?: "${opponent.firstname} ${opponent.lastname}"
                        )
                    }

                    work.consultant?.let { consultant ->
                        WorkInfoItemWithAvatar(
                            firstname = consultant.firstname,
                            lastname = consultant.lastname,
                            userId = consultant.id,
                            label = stringResource(R.string.consultant),
                            value = consultant.fullName ?: "${consultant.firstname} ${consultant.lastname}"
                        )
                    }
                }
            }

            when (val state = conversationWorkState) {
                is WorkDetailViewModel.ConversationWorkState.Success -> {
                    if (state.conversationWork != null) {
                        BaseCard {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                AppSectionHeader(
                                    title = stringResource(R.string.communication),
                                    icon = AppIcons.Comment
                                )

                                AppButton(
                                    text = stringResource(R.string.open_conversation),
                                    onClick = { onNavigateToConversationDetail?.invoke(state.conversationWork.id) },
                                    modifier = Modifier.fillMaxWidth(),
                                    variant = AppButtonVariant.Primary,
                                    leadingIcon = AppIcons.Send
                                )
                            }
                        }
                    }
                }
                else -> {
                    // nothing
                }
            }
        }

        AppTabRow(
            tabs = tabs,
            selectedTabIndex = selectedTabIndex,
            onTabSelected = { selectedTabIndex = it },
            scrollable = true,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .padding(16.dp)
        ) {
            when (selectedTabIndex) {
                0 -> TaskTabContent(
                    workDetailViewModel = workDetailViewModel,
                    workId = work.id,
                    onNavigateToTaskDetail = onNavigateToTaskDetail,
                    onNavigateToTaskCreate = onNavigateToTaskCreate
                )
                1 -> VersionTabContent(workDetailViewModel = workDetailViewModel, workId = work.id)
                2 -> WorkDetailConversationMessages(
                    workId = work.id,
                    currentUserId = currentUserId,
                    workDetailViewModel = workDetailViewModel,
                    modifier = Modifier.fillMaxSize()
                )
                3 -> EventTabContent(
                    workDetailViewModel = workDetailViewModel,
                    workId = work.id,
                    onEventClick = { event -> onNavigateToEventDetail?.invoke(event.id) }
                )
            }
        }
    }
}
