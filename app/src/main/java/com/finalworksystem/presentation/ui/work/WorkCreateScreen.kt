package com.finalworksystem.presentation.ui.work

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.finalworksystem.R
import com.finalworksystem.domain.user.model.User
import com.finalworksystem.domain.user.model.UserRole
import com.finalworksystem.domain.user.model.UserType
import com.finalworksystem.domain.work.model.WorkCategory
import com.finalworksystem.infrastructure.user.UserService
import com.finalworksystem.presentation.ui.component.BaseTopAppBar
import com.finalworksystem.presentation.ui.work.component.UserSelectionDialog
import com.finalworksystem.presentation.view_model.work.WorkCreateViewModel
import com.finalworksystem.ui.designsystem.AppButton
import com.finalworksystem.ui.designsystem.AppButtonVariant
import com.finalworksystem.ui.designsystem.AppCard
import com.finalworksystem.ui.designsystem.AppCardStyle
import com.finalworksystem.ui.designsystem.AppCheckboxRow
import com.finalworksystem.ui.designsystem.AppDatePickerDialog
import com.finalworksystem.ui.designsystem.AppDialog
import com.finalworksystem.ui.designsystem.AppDivider
import com.finalworksystem.ui.designsystem.AppDropdownField
import com.finalworksystem.ui.designsystem.AppDropdownMenuItem
import com.finalworksystem.ui.designsystem.AppEmptyState
import com.finalworksystem.ui.designsystem.AppIcon
import com.finalworksystem.ui.designsystem.AppIconBadge
import com.finalworksystem.ui.designsystem.AppIconButton
import com.finalworksystem.ui.designsystem.AppLoadingState
import com.finalworksystem.ui.designsystem.AppScaffold
import com.finalworksystem.ui.designsystem.AppSectionHeader
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.designsystem.AppTextField
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme
import org.koin.compose.koinInject
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun WorkCreateScreen(
    viewModel: WorkCreateViewModel,
    onNavigateBack: () -> Unit,
    onWorkCreated: (Int) -> Unit
) {
    val userService: UserService = koinInject()
    val currentUser by userService.getUserFlowSafe().collectAsStateWithLifecycle(initialValue = null)
    var isSupervisor by remember { mutableStateOf<Boolean?>(null) }

    val createDataState by viewModel.createDataState.collectAsStateWithLifecycle()
    val submissionState by viewModel.submissionState.collectAsStateWithLifecycle()

    val title by viewModel.titleState.collectAsStateWithLifecycle()
    val shortcut by viewModel.shortcutState.collectAsStateWithLifecycle()
    val selectedType by viewModel.selectedType.collectAsStateWithLifecycle()
    val selectedStatus by viewModel.selectedStatus.collectAsStateWithLifecycle()
    val selectedCategories by viewModel.selectedCategories.collectAsStateWithLifecycle()
    val deadline by viewModel.deadlineState.collectAsStateWithLifecycle()
    val deadlineProgram by viewModel.deadlineProgramState.collectAsStateWithLifecycle()

    val selectedAuthor by viewModel.selectedAuthor.collectAsStateWithLifecycle()
    val selectedOpponent by viewModel.selectedOpponent.collectAsStateWithLifecycle()
    val selectedConsultant by viewModel.selectedConsultant.collectAsStateWithLifecycle()

    var activeUserTypeDialog by remember { mutableStateOf<UserType?>(null) }

    var isTypeMenuExpanded by remember { mutableStateOf(false) }
    var isStatusMenuExpanded by remember { mutableStateOf(false) }
    var isCategoryMenuExpanded by remember { mutableStateOf(false) }

    var showDeadlineDatePicker by remember { mutableStateOf(false) }
    var showDeadlineProgramDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            val hasSupervisorRole = currentUser?.roles?.contains(UserRole.SUPERVISOR.value) == true
            isSupervisor = hasSupervisorRole
            if (hasSupervisorRole && createDataState is WorkCreateViewModel.CreateDataState.Idle) {
                viewModel.loadManageCreateData()
            }
        }
    }

    LaunchedEffect(submissionState) {
        if (submissionState is WorkCreateViewModel.SubmissionState.Success) {
            val createdWork = (submissionState as WorkCreateViewModel.SubmissionState.Success).work
            viewModel.resetSubmissionState()
            onWorkCreated(createdWork.id)
        }
    }

    AppScaffold(
        topBar = {
            BaseTopAppBar(
                title = stringResource(R.string.create_work),
                onNavigateBack = onNavigateBack,
                onReload = { viewModel.loadManageCreateData() },
                isLoading = createDataState is WorkCreateViewModel.CreateDataState.Loading
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isSupervisor == false) {
                AppEmptyState(
                    title = stringResource(R.string.access_denied_supervisor),
                    icon = AppIcons.Info,
                    iconTint = AppTheme.colors.error
                )
            } else {
                when (val dataState = createDataState) {
                    is WorkCreateViewModel.CreateDataState.Loading -> {
                        AppLoadingState()
                    }
                    is WorkCreateViewModel.CreateDataState.Error -> {
                        AppEmptyState(
                            title = dataState.message,
                            icon = AppIcons.Info,
                            iconTint = AppTheme.colors.error,
                            action = {
                                AppButton(
                                    text = stringResource(R.string.retry),
                                    onClick = { viewModel.loadManageCreateData() },
                                    leadingIcon = AppIcons.Refresh
                                )
                            }
                        )
                    }
                    is WorkCreateViewModel.CreateDataState.Success -> {
                        val createData = dataState.data

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Work information
                            FormSectionCard(
                                title = stringResource(R.string.work_information),
                                icon = AppIcons.Article
                            ) {
                                AppTextField(
                                    value = title,
                                    onValueChange = { viewModel.titleState.value = it },
                                    label = { AppText("${stringResource(R.string.title)} *") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                AppTextField(
                                    value = shortcut,
                                    onValueChange = { viewModel.shortcutState.value = it },
                                    label = { AppText(stringResource(R.string.shortcut)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                AppDropdownField(
                                    value = selectedType?.name ?: "",
                                    expanded = isTypeMenuExpanded,
                                    onExpandedChange = { isTypeMenuExpanded = it },
                                    label = { AppText("${stringResource(R.string.type)} *") },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    createData.types.forEach { type ->
                                        AppDropdownMenuItem(
                                            text = type.name,
                                            selected = selectedType?.id == type.id,
                                            onClick = {
                                                viewModel.selectedType.value = type
                                                isTypeMenuExpanded = false
                                            }
                                        )
                                    }
                                }

                                AppDropdownField(
                                    value = selectedStatus?.name ?: "",
                                    expanded = isStatusMenuExpanded,
                                    onExpandedChange = { isStatusMenuExpanded = it },
                                    label = { AppText("${stringResource(R.string.status)} *") },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    createData.statuses.forEach { status ->
                                        AppDropdownMenuItem(
                                            text = status.name,
                                            selected = selectedStatus?.id == status.id,
                                            onClick = {
                                                viewModel.selectedStatus.value = status
                                                isStatusMenuExpanded = false
                                            }
                                        )
                                    }
                                }

                                // Category Field (Multi-select via Modal Dialog)
                                AppTextField(
                                    value = if (selectedCategories.isEmpty()) stringResource(R.string.none) else selectedCategories.joinToString(", ") { it.name },
                                    onValueChange = {},
                                    readOnly = true,
                                    singleLine = true,
                                    label = { AppText(stringResource(R.string.categories)) },
                                    trailingIcon = { AppIcon(AppIcons.KeyboardArrowDown, contentDescription = null) },
                                    onClick = { isCategoryMenuExpanded = true },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            if (isCategoryMenuExpanded) {
                                CategorySelectionDialog(
                                    categories = createData.categories,
                                    selectedCategories = selectedCategories,
                                    onCategoryToggle = { category ->
                                        val currentList = selectedCategories.toMutableList()
                                        if (currentList.any { it.id == category.id }) {
                                            currentList.removeAll { it.id == category.id }
                                        } else {
                                            currentList.add(category)
                                        }
                                        viewModel.selectedCategories.value = currentList
                                    },
                                    onClearAll = {
                                        viewModel.selectedCategories.value = emptyList()
                                    },
                                    onDismiss = {
                                        isCategoryMenuExpanded = false
                                    }
                                )
                            }

                            // Deadlines
                            FormSectionCard(
                                title = stringResource(R.string.deadline),
                                icon = AppIcons.DateRange
                            ) {
                                AppTextField(
                                    value = deadline,
                                    onValueChange = {},
                                    readOnly = true,
                                    singleLine = true,
                                    label = { AppText("${stringResource(R.string.deadline)} *") },
                                    trailingIcon = { AppIcon(AppIcons.DateRange, contentDescription = null) },
                                    onClick = { showDeadlineDatePicker = true },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                AppTextField(
                                    value = deadlineProgram,
                                    onValueChange = {},
                                    readOnly = true,
                                    singleLine = true,
                                    label = { AppText(stringResource(R.string.program_deadline)) },
                                    trailingIcon = { AppIcon(AppIcons.DateRange, contentDescription = null) },
                                    onClick = { showDeadlineProgramDatePicker = true },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            // People
                            FormSectionCard(
                                title = stringResource(R.string.people),
                                icon = AppIcons.Groups,
                                spacing = 10
                            ) {
                                UserPickerSelectorCard(
                                    label = stringResource(R.string.author),
                                    selectedUser = selectedAuthor,
                                    onClick = { activeUserTypeDialog = UserType.AUTHOR }
                                )
                                UserPickerSelectorCard(
                                    label = stringResource(R.string.opponent),
                                    selectedUser = selectedOpponent,
                                    onClick = { activeUserTypeDialog = UserType.OPPONENT }
                                )
                                UserPickerSelectorCard(
                                    label = stringResource(R.string.consultant),
                                    selectedUser = selectedConsultant,
                                    onClick = { activeUserTypeDialog = UserType.CONSULTANT }
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Submit Button
                            AppButton(
                                text = stringResource(R.string.create),
                                onClick = { viewModel.submitCreateWork() },
                                enabled = submissionState !is WorkCreateViewModel.SubmissionState.Submitting,
                                loading = submissionState is WorkCreateViewModel.SubmissionState.Submitting,
                                leadingIcon = AppIcons.Check,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                    else -> {
                        // Idle
                    }
                }
            }
        }
    }

    // DatePicker Dialogs
    if (showDeadlineDatePicker) {
        val initialMillis = remember(deadline) {
            try {
                if (deadline.isNotBlank()) {
                    LocalDate.parse(deadline).atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
                } else null
            } catch (_: Exception) { null }
        }
        AppDatePickerDialog(
            onDismissRequest = { showDeadlineDatePicker = false },
            onDateSelected = { selectedMillis ->
                if (selectedMillis != null) {
                    viewModel.deadlineState.value = formatUtcMillis(selectedMillis)
                }
                showDeadlineDatePicker = false
            },
            initialSelectedDateMillis = initialMillis,
            title = stringResource(R.string.deadline)
        )
    }

    if (showDeadlineProgramDatePicker) {
        val initialMillis = remember(deadlineProgram) {
            try {
                if (deadlineProgram.isNotBlank()) {
                    LocalDate.parse(deadlineProgram).atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
                } else null
            } catch (_: Exception) { null }
        }
        AppDatePickerDialog(
            onDismissRequest = { showDeadlineProgramDatePicker = false },
            onDateSelected = { selectedMillis ->
                if (selectedMillis != null) {
                    viewModel.deadlineProgramState.value = formatUtcMillis(selectedMillis)
                }
                showDeadlineProgramDatePicker = false
            },
            initialSelectedDateMillis = initialMillis,
            title = stringResource(R.string.program_deadline)
        )
    }

    // User Picker Dialog
    activeUserTypeDialog?.let { userType ->
        val title = when (userType) {
            UserType.AUTHOR -> stringResource(R.string.select_author)
            UserType.OPPONENT -> stringResource(R.string.select_opponent)
            UserType.CONSULTANT -> stringResource(R.string.select_consultant)
            else -> ""
        }
        val currentSelectedUser = when (userType) {
            UserType.AUTHOR -> selectedAuthor
            UserType.OPPONENT -> selectedOpponent
            UserType.CONSULTANT -> selectedConsultant
            else -> null
        }

        UserSelectionDialog(
            title = title,
            isVisible = true,
            selectedUser = currentSelectedUser,
            users = viewModel.userPicker.users(userType),
            initialQuery = viewModel.userPicker.searchQuery(userType),
            onSearch = { query -> viewModel.userPicker.search(userType, query) },
            onUserSelected = { user ->
                when (userType) {
                    UserType.AUTHOR -> viewModel.selectedAuthor.value = user
                    UserType.OPPONENT -> viewModel.selectedOpponent.value = user
                    UserType.CONSULTANT -> viewModel.selectedConsultant.value = user
                    else -> {}
                }
                activeUserTypeDialog = null
            },
            onDismiss = { activeUserTypeDialog = null }
        )
    }
}

private fun formatUtcMillis(millis: Long): String =
    Instant.ofEpochMilli(millis)
        .atZone(ZoneId.of("UTC"))
        .toLocalDate()
        .format(DateTimeFormatter.ISO_LOCAL_DATE)

@Composable
private fun FormSectionCard(
    title: String,
    icon: ImageVector,
    spacing: Int = 16,
    content: @Composable () -> Unit
) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(spacing.dp)
        ) {
            AppSectionHeader(title = title, icon = icon)
            content()
        }
    }
}

@Composable
private fun UserPickerSelectorCard(
    label: String,
    selectedUser: User?,
    onClick: () -> Unit
) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        style = AppCardStyle.Tonal,
        shape = AppTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppIconBadge(
                icon = AppIcons.Person,
                tint = if (selectedUser != null) AppTheme.colors.primary else AppTheme.colors.onSurfaceVariant,
                size = 36
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                AppText(
                    text = label,
                    style = AppTheme.typography.labelMedium,
                    color = AppTheme.colors.onSurfaceVariant
                )
                AppText(
                    text = selectedUser?.let { user ->
                        buildString {
                            if (!user.degreeBefore.isNullOrEmpty()) append("${user.degreeBefore} ")
                            append("${user.firstname} ${user.lastname}")
                            if (!user.degreeAfter.isNullOrEmpty()) append(" ${user.degreeAfter}")
                        }
                    } ?: stringResource(R.string.not_specified),
                    style = AppTheme.typography.bodyMedium,
                    fontWeight = if (selectedUser != null) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (selectedUser != null) AppTheme.colors.onSurface else AppTheme.colors.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            AppIcon(
                imageVector = AppIcons.KeyboardArrowDown,
                contentDescription = null,
                tint = AppTheme.colors.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CategorySelectionDialog(
    categories: List<WorkCategory>,
    selectedCategories: List<WorkCategory>,
    onCategoryToggle: (WorkCategory) -> Unit,
    onClearAll: () -> Unit,
    onDismiss: () -> Unit
) {
    AppDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxHeight(0.75f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppText(
                    text = stringResource(R.string.categories),
                    style = AppTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                AppIconButton(
                    icon = AppIcons.Close,
                    contentDescription = stringResource(R.string.close),
                    onClick = onDismiss
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                AppButton(
                    text = stringResource(R.string.none),
                    onClick = onClearAll,
                    variant = AppButtonVariant.Ghost,
                    leadingIcon = AppIcons.Clear
                )
                AppDivider(modifier = Modifier.padding(vertical = 6.dp))

                categories.forEach { category ->
                    val isSelected = selectedCategories.any { it.id == category.id }
                    AppCheckboxRow(
                        checked = isSelected,
                        onCheckedChange = { onCategoryToggle(category) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AppText(
                            text = category.name,
                            style = AppTheme.typography.bodyLarge,
                            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            AppButton(
                text = stringResource(R.string.ok),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
