package com.finalworksystem.presentation.ui.task

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finalworksystem.R
import com.finalworksystem.presentation.ui.component.BaseTopAppBar
import com.finalworksystem.presentation.ui.task.component.WorkSelectionDialog
import com.finalworksystem.presentation.view_model.task.TaskCreateViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCreateScreen(
    viewModel: TaskCreateViewModel,
    workId: Int = -1,
    onNavigateBack: () -> Unit,
    onTaskCreated: (Int) -> Unit
) {
    val submissionState by viewModel.submissionState.collectAsState()
    val workPickerState by viewModel.workPickerState.collectAsState()

    val name by viewModel.nameState.collectAsState()
    val description by viewModel.descriptionState.collectAsState()
    val deadline by viewModel.deadlineState.collectAsState()
    val selectedWork by viewModel.selectedWork.collectAsState()

    var isWorkSelectionVisible by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(workId) {
        if (workId != -1) {
            viewModel.setSelectedWorkById(workId)
        }
    }

    LaunchedEffect(submissionState) {
        if (submissionState is TaskCreateViewModel.SubmissionState.Success) {
            val task = (submissionState as TaskCreateViewModel.SubmissionState.Success).task
            onTaskCreated(task.id)
            viewModel.resetSubmissionState()
        }
    }

    Scaffold(
        topBar = {
            BaseTopAppBar(
                title = stringResource(R.string.create_task),
                onNavigateBack = onNavigateBack,
                onReload = { viewModel.loadWorks() }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Task Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { viewModel.nameState.value = it },
                    label = { Text(stringResource(R.string.task_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { viewModel.descriptionState.value = it },
                    label = { Text(stringResource(R.string.description)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                // Work Selection
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isWorkSelectionVisible = true },
                    shape = RoundedCornerShape(4.dp),
                    colors = CardDefaults.outlinedCardColors()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.work_information),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = selectedWork?.title ?: stringResource(R.string.select_work),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (selectedWork != null) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                }

                // Deadline
                OutlinedTextField(
                    value = deadline,
                    onValueChange = { },
                    label = { Text(stringResource(R.string.deadline)) },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(imageVector = Icons.Default.DateRange, contentDescription = null)
                        }
                    }
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { viewModel.submitCreateTask() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = submissionState !is TaskCreateViewModel.SubmissionState.Submitting
                ) {
                    if (submissionState is TaskCreateViewModel.SubmissionState.Submitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(stringResource(R.string.create))
                    }
                }
            }

            if (showDatePicker) {
                val datePickerState = rememberDatePickerState()
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val date = Instant.ofEpochMilli(millis)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                                viewModel.deadlineState.value = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                            }
                            showDatePicker = false
                        }) {
                            Text(stringResource(R.string.ok))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text(stringResource(R.string.cancel))
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }
        }
    }

    WorkSelectionDialog(
        isVisible = isWorkSelectionVisible,
        selectedWork = selectedWork,
        pickerState = workPickerState,
        onSearch = { viewModel.loadWorks(it) },
        onLoadMore = { viewModel.loadMoreWorks() },
        onWorkSelected = {
            viewModel.selectedWork.value = it
            isWorkSelectionVisible = false
        },
        onDismiss = { isWorkSelectionVisible = false }
    )
}
