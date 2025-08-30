package com.finalworksystem.presentation.ui.work

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finalworksystem.domain.common.util.DateUtils
import com.finalworksystem.domain.work.model.Work
import com.finalworksystem.presentation.ui.component.BaseCard
import com.finalworksystem.presentation.ui.component.TitleDialog
import com.finalworksystem.presentation.ui.work.component.EventTabContent
import com.finalworksystem.presentation.ui.work.component.TaskTabContent
import com.finalworksystem.presentation.ui.work.component.VersionTabContent
import com.finalworksystem.presentation.ui.work.component.WorkDetailConversationMessages
import com.finalworksystem.presentation.ui.work.component.WorkInfoItemWithAvatar
import com.finalworksystem.presentation.ui.work.component.WorkInfoItemWithIcon
import com.finalworksystem.presentation.view_model.work.WorkViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkDetailScreen(
    workId: Int,
    currentUserId: Int,
    workViewModel: WorkViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToTaskDetail: ((Int, Int) -> Unit)? = null,
    onNavigateToConversationDetail: ((Int) -> Unit)? = null,
    onNavigateToEventDetail: ((Int) -> Unit)? = null
) {
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showTitleDialog by remember { mutableStateOf(false) }

    val workDetailState by workViewModel.workDetailState.collectAsState()

    LaunchedEffect(workId) {
        workViewModel.loadWorkDetail(workId, forceRefresh = false)
        workViewModel.loadTasksForWork(workId, forceRefresh = false)
        workViewModel.loadConversationWork(workId, forceRefresh = false)
    }

    LaunchedEffect(workDetailState) {
        when (workDetailState) {
            is WorkViewModel.WorkDetailState.Error -> {
                showError = true
                errorMessage = (workDetailState as WorkViewModel.WorkDetailState.Error).message
            }
            else -> {
                showError = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val currentState = workDetailState
                    val workTitle = when (currentState) {
                        is WorkViewModel.WorkDetailState.Success -> currentState.work.title
                        else -> "Work Details"
                    }
                    Text(
                        text = workTitle,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = if (currentState is WorkViewModel.WorkDetailState.Success) {
                            Modifier.clickable { showTitleDialog = true }
                        } else {
                            Modifier
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        workViewModel.loadWorkDetail(workId, forceRefresh = true)
                        workViewModel.loadTasksForWork(workId, forceRefresh = true)
                        workViewModel.loadVersionsForWork(workId, forceRefresh = true)
                        workViewModel.loadEventsForWork(workId, forceRefresh = true)
                        workViewModel.loadWorkMessages(workId, forceRefresh = true)
                        workViewModel.loadConversationWork(workId, forceRefresh = true)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
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
                is WorkViewModel.WorkDetailState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is WorkViewModel.WorkDetailState.Success -> {
                    val work = (workDetailState as WorkViewModel.WorkDetailState.Success).work
                    WorkDetailContent(work = work, workViewModel = workViewModel, currentUserId = currentUserId, onNavigateToTaskDetail = onNavigateToTaskDetail, onNavigateToConversationDetail = onNavigateToConversationDetail, onNavigateToEventDetail = onNavigateToEventDetail)
                }
                else -> {
                    // nothing
                }
            }

            if (showError) {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(text = errorMessage)
                }
            }
        }
    }

    val currentState = workDetailState
    if (currentState is WorkViewModel.WorkDetailState.Success) {
        TitleDialog(
            title = currentState.work.title,
            isVisible = showTitleDialog,
            onDismiss = { showTitleDialog = false }
        )
    }
}

@Composable
fun WorkDetailContent(work: Work, workViewModel: WorkViewModel, currentUserId: Int, onNavigateToTaskDetail: ((Int, Int) -> Unit)? = null, onNavigateToConversationDetail: ((Int) -> Unit)? = null, onNavigateToEventDetail: ((Int) -> Unit)? = null) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Task", "Version", "Message", "Event")
    val scrollState = rememberScrollState()

    val conversationWorkState by workViewModel.conversationWorkState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BaseCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = work.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            BaseCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Work Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    WorkInfoItemWithIcon(
                        icon = Icons.Default.Info,
                        label = "Type",
                        value = work.type.name,
                        iconTint = MaterialTheme.colorScheme.primary
                    )

                    WorkInfoItemWithIcon(
                        icon = Icons.Default.CheckCircle,
                        label = "Status",
                        value = work.status.name,
                        iconTint = MaterialTheme.colorScheme.primary
                    )

                    WorkInfoItemWithIcon(
                        icon = Icons.Default.DateRange,
                        label = "Deadline",
                        value = DateUtils.formatToYmd(work.deadline),
                        iconTint = MaterialTheme.colorScheme.error
                    )

                    work.deadlineProgram?.let { deadlineProgram ->
                        WorkInfoItemWithIcon(
                            icon = Icons.Default.DateRange,
                            label = "Program Deadline",
                            value = DateUtils.formatToYmd(deadlineProgram),
                            iconTint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            BaseCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "People",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    work.author?.let { author ->
                        WorkInfoItemWithAvatar(
                            firstname = author.firstname,
                            lastname = author.lastname,
                            userId = author.id,
                            label = "Author",
                            value = author.fullName ?: "${author.firstname} ${author.lastname}"
                        )
                    }

                    work.supervisor?.let { supervisor ->
                        WorkInfoItemWithAvatar(
                            firstname = supervisor.firstname,
                            lastname = supervisor.lastname,
                            userId = supervisor.id,
                            label = "Supervisor",
                            value = supervisor.fullName ?: "${supervisor.firstname} ${supervisor.lastname}"
                        )
                    }

                    work.opponent?.let { opponent ->
                        WorkInfoItemWithAvatar(
                            firstname = opponent.firstname,
                            lastname = opponent.lastname,
                            userId = opponent.id,
                            label = "Opponent",
                            value = opponent.fullName ?: "${opponent.firstname} ${opponent.lastname}"
                        )
                    }

                    work.consultant?.let { consultant ->
                        WorkInfoItemWithAvatar(
                            firstname = consultant.firstname,
                            lastname = consultant.lastname,
                            userId = consultant.id,
                            label = "Consultant",
                            value = consultant.fullName ?: "${consultant.firstname} ${consultant.lastname}"
                        )
                    }
                }
            }

            when (val state = conversationWorkState) {
                is WorkViewModel.ConversationWorkState.Success -> {
                    if (state.conversationWork != null) {
                        BaseCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = "Communication",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Button(
                                    onClick = { onNavigateToConversationDetail?.invoke(state.conversationWork!!.id) },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Open conversation")
                                }
                            }
                        }
                    }
                }
                else -> {
                    // nothing
                }
            }
        }

        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            edgePadding = 0.dp
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .padding(16.dp)
        ) {
            when (selectedTabIndex) {
                0 -> TaskTabContent(workViewModel = workViewModel, workId = work.id, onNavigateToTaskDetail = onNavigateToTaskDetail)
                1 -> VersionTabContent(workViewModel = workViewModel, workId = work.id)
                2 -> WorkDetailConversationMessages(
                    workId = work.id,
                    currentUserId = currentUserId,
                    workViewModel = workViewModel,
                    modifier = Modifier.fillMaxSize()
                )
                3 -> EventTabContent(
                    workViewModel = workViewModel,
                    workId = work.id,
                    onEventClick = { event -> onNavigateToEventDetail?.invoke(event.id) }
                )
            }
        }
    }
}
