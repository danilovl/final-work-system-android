package com.finalworksystem.presentation.ui.task

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.finalworksystem.R
import com.finalworksystem.presentation.ui.component.BaseTopAppBar
import com.finalworksystem.presentation.ui.task.component.WorkSelectionDialog
import com.finalworksystem.presentation.view_model.task.TaskCreateViewModel
import com.finalworksystem.ui.designsystem.AppButton
import com.finalworksystem.ui.designsystem.AppButtonVariant
import com.finalworksystem.ui.designsystem.AppCard
import com.finalworksystem.ui.designsystem.AppDatePickerDialog
import com.finalworksystem.ui.designsystem.AppIcon
import com.finalworksystem.ui.designsystem.AppScaffold
import com.finalworksystem.ui.designsystem.AppSectionHeader
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.designsystem.AppTextField
import com.finalworksystem.ui.designsystem.toUtcMillis
import com.finalworksystem.ui.designsystem.utcMillisToLocalDate
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TaskCreateScreen(
    viewModel: TaskCreateViewModel,
    workId: Int = -1,
    onNavigateBack: () -> Unit,
    onTaskCreated: (Int) -> Unit
) {
    val submissionState by viewModel.submissionState.collectAsStateWithLifecycle()

    val name by viewModel.nameState.collectAsStateWithLifecycle()
    val description by viewModel.descriptionState.collectAsStateWithLifecycle()
    val deadline by viewModel.deadlineState.collectAsStateWithLifecycle()
    val selectedWork by viewModel.selectedWork.collectAsStateWithLifecycle()

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

    val isSubmitting = submissionState is TaskCreateViewModel.SubmissionState.Submitting

    AppScaffold(
        topBar = {
            BaseTopAppBar(
                title = stringResource(R.string.create_task),
                onNavigateBack = onNavigateBack,
                onReload = { viewModel.refreshWorks() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    AppSectionHeader(
                        title = stringResource(R.string.task_information),
                        icon = AppIcons.Task
                    )

                    AppTextField(
                        value = name,
                        onValueChange = { viewModel.nameState.value = it },
                        label = { AppText(stringResource(R.string.task_name)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    AppTextField(
                        value = description,
                        onValueChange = { viewModel.descriptionState.value = it },
                        label = { AppText(stringResource(R.string.description)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    AppTextField(
                        value = selectedWork?.title ?: "",
                        onValueChange = { },
                        label = { AppText(stringResource(R.string.work_information)) },
                        placeholder = { AppText(stringResource(R.string.select_work)) },
                        leadingIcon = {
                            AppIcon(
                                imageVector = AppIcons.Work,
                                contentDescription = null,
                                tint = AppTheme.colors.onSurfaceVariant
                            )
                        },
                        trailingIcon = {
                            AppIcon(
                                imageVector = AppIcons.ArrowDropDown,
                                contentDescription = null,
                                tint = AppTheme.colors.onSurfaceVariant
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        singleLine = true,
                        onClick = { isWorkSelectionVisible = true }
                    )

                    AppTextField(
                        value = deadline,
                        onValueChange = { },
                        label = { AppText(stringResource(R.string.deadline)) },
                        leadingIcon = {
                            AppIcon(
                                imageVector = AppIcons.DateRange,
                                contentDescription = null,
                                tint = AppTheme.colors.onSurfaceVariant
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        singleLine = true,
                        onClick = { showDatePicker = true }
                    )
                }
            }

            AppButton(
                text = stringResource(R.string.create),
                onClick = { viewModel.submitCreateTask() },
                modifier = Modifier.fillMaxWidth(),
                variant = AppButtonVariant.Primary,
                leadingIcon = AppIcons.Add,
                loading = isSubmitting,
                enabled = !isSubmitting
            )
        }
    }

    if (showDatePicker) {
        AppDatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            onDateSelected = { millis ->
                millis?.let {
                    viewModel.deadlineState.value =
                        utcMillisToLocalDate(it).format(DateTimeFormatter.ISO_LOCAL_DATE)
                }
                showDatePicker = false
            },
            initialSelectedDateMillis = runCatching { LocalDate.parse(deadline).toUtcMillis() }.getOrNull(),
            title = stringResource(R.string.deadline)
        )
    }

    WorkSelectionDialog(
        isVisible = isWorkSelectionVisible,
        selectedWork = selectedWork,
        works = viewModel.works,
        initialQuery = viewModel.workSearchQuery,
        onSearch = { viewModel.searchWorks(it) },
        onWorkSelected = {
            viewModel.selectedWork.value = it
            isWorkSelectionVisible = false
        },
        onDismiss = { isWorkSelectionVisible = false }
    )
}
