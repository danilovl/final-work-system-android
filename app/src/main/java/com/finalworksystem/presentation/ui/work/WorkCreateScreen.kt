package com.finalworksystem.presentation.ui.work

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.finalworksystem.R
import com.finalworksystem.domain.user.model.User
import com.finalworksystem.domain.user.model.UserRole
import com.finalworksystem.domain.user.model.UserType
import com.finalworksystem.domain.work.model.WorkCategory
import com.finalworksystem.infrastructure.user.UserService
import com.finalworksystem.presentation.ui.component.BaseTopAppBar
import com.finalworksystem.presentation.ui.work.component.UserSelectionDialog
import com.finalworksystem.presentation.view_model.work.WorkCreateViewModel
import org.koin.compose.koinInject
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkCreateScreen(
    viewModel: WorkCreateViewModel,
    onNavigateBack: () -> Unit,
    onWorkCreated: (Int) -> Unit
) {
    val userService: UserService = koinInject()
    val currentUser by userService.getUserFlowSafe().collectAsState(initial = null)
    var isSupervisor by remember { mutableStateOf<Boolean?>(null) }

    val createDataState by viewModel.createDataState.collectAsState()
    val submissionState by viewModel.submissionState.collectAsState()

    val title by viewModel.titleState.collectAsState()
    val shortcut by viewModel.shortcutState.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    val selectedStatus by viewModel.selectedStatus.collectAsState()
    val selectedCategories by viewModel.selectedCategories.collectAsState()
    val deadline by viewModel.deadlineState.collectAsState()
    val deadlineProgram by viewModel.deadlineProgramState.collectAsState()

    val selectedAuthor by viewModel.selectedAuthor.collectAsState()
    val selectedOpponent by viewModel.selectedOpponent.collectAsState()
    val selectedConsultant by viewModel.selectedConsultant.collectAsState()

    val userPickersState by viewModel.userPickersState.collectAsState()

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

    Scaffold(
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
                Text(
                    text = stringResource(R.string.access_denied_supervisor),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            } else {
                when (val dataState = createDataState) {
                    is WorkCreateViewModel.CreateDataState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                is WorkCreateViewModel.CreateDataState.Error -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = dataState.message,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadManageCreateData() }) {
                            Text(stringResource(R.string.retry))
                        }
                    }
                }
                is WorkCreateViewModel.CreateDataState.Success -> {
                    val createData = dataState.data

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Title Field
                        OutlinedTextField(
                            value = title,
                            onValueChange = { viewModel.titleState.value = it },
                            label = { Text("${stringResource(R.string.title)} *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // Shortcut Field
                        OutlinedTextField(
                            value = shortcut,
                            onValueChange = { viewModel.shortcutState.value = it },
                            label = { Text(stringResource(R.string.shortcut)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // Type Dropdown
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = selectedType?.name ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("${stringResource(R.string.type)} *") },
                                trailingIcon = {
                                    IconButton(onClick = { isTypeMenuExpanded = true }) {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isTypeMenuExpanded = true }
                            )
                            DropdownMenu(
                                expanded = isTypeMenuExpanded,
                                onDismissRequest = { isTypeMenuExpanded = false },
                                modifier = Modifier.fillMaxWidth(0.9f)
                            ) {
                                createData.types.forEach { type ->
                                    DropdownMenuItem(
                                        text = { Text(type.name) },
                                        onClick = {
                                            viewModel.selectedType.value = type
                                            isTypeMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Status Dropdown
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = selectedStatus?.name ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("${stringResource(R.string.status)} *") },
                                trailingIcon = {
                                    IconButton(onClick = { isStatusMenuExpanded = true }) {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isStatusMenuExpanded = true }
                            )
                            DropdownMenu(
                                expanded = isStatusMenuExpanded,
                                onDismissRequest = { isStatusMenuExpanded = false },
                                modifier = Modifier.fillMaxWidth(0.9f)
                            ) {
                                createData.statuses.forEach { status ->
                                    DropdownMenuItem(
                                        text = { Text(status.name) },
                                        onClick = {
                                            viewModel.selectedStatus.value = status
                                            isStatusMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Category Field (Multi-select via Modal Dialog)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isCategoryMenuExpanded = true }
                        ) {
                            OutlinedTextField(
                                value = if (selectedCategories.isEmpty()) stringResource(R.string.none) else selectedCategories.joinToString(", ") { it.name },
                                onValueChange = {},
                                readOnly = true,
                                enabled = false,
                                label = { Text(stringResource(R.string.categories)) },
                                trailingIcon = {
                                    IconButton(onClick = { isCategoryMenuExpanded = true }) {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
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

                        // Deadline Field with Calendar DatePicker
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDeadlineDatePicker = true }
                        ) {
                            OutlinedTextField(
                                value = deadline,
                                onValueChange = {},
                                readOnly = true,
                                enabled = false,
                                label = { Text("${stringResource(R.string.deadline)} *") },
                                trailingIcon = {
                                    IconButton(onClick = { showDeadlineDatePicker = true }) {
                                        Icon(Icons.Default.DateRange, contentDescription = null)
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Deadline Program Field with Calendar DatePicker
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDeadlineProgramDatePicker = true }
                        ) {
                            OutlinedTextField(
                                value = deadlineProgram,
                                onValueChange = {},
                                readOnly = true,
                                enabled = false,
                                label = { Text(stringResource(R.string.program_deadline)) },
                                trailingIcon = {
                                    IconButton(onClick = { showDeadlineProgramDatePicker = true }) {
                                        Icon(Icons.Default.DateRange, contentDescription = null)
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Text(
                            text = stringResource(R.string.people),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        // Author Selector
                        UserPickerSelectorCard(
                            label = stringResource(R.string.author),
                            selectedUser = selectedAuthor,
                            onClick = {
                                activeUserTypeDialog = UserType.AUTHOR
                                viewModel.loadUsers(UserType.AUTHOR)
                            }
                        )

                        // Opponent Selector
                        UserPickerSelectorCard(
                            label = stringResource(R.string.opponent),
                            selectedUser = selectedOpponent,
                            onClick = {
                                activeUserTypeDialog = UserType.OPPONENT
                                viewModel.loadUsers(UserType.OPPONENT)
                            }
                        )

                        // Consultant Selector
                        UserPickerSelectorCard(
                            label = stringResource(R.string.consultant),
                            selectedUser = selectedConsultant,
                            onClick = {
                                activeUserTypeDialog = UserType.CONSULTANT
                                viewModel.loadUsers(UserType.CONSULTANT)
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Submit Button
                        Button(
                            onClick = { viewModel.submitCreateWork() },
                            enabled = submissionState !is WorkCreateViewModel.SubmissionState.Submitting,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            if (submissionState is WorkCreateViewModel.SubmissionState.Submitting) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(2.dp)
                                )
                            } else {
                                Text(
                                    text = stringResource(R.string.create),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
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
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
        CustomDatePickerDialog(
            onDateSelected = { selectedMillis ->
                if (selectedMillis != null) {
                    val formatted = Instant.ofEpochMilli(selectedMillis)
                        .atZone(ZoneId.of("UTC"))
                        .toLocalDate()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE)
                    viewModel.deadlineState.value = formatted
                }
                showDeadlineDatePicker = false
            },
            onDismiss = { showDeadlineDatePicker = false },
            datePickerState = datePickerState
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
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
        CustomDatePickerDialog(
            onDateSelected = { selectedMillis ->
                if (selectedMillis != null) {
                    val formatted = Instant.ofEpochMilli(selectedMillis)
                        .atZone(ZoneId.of("UTC"))
                        .toLocalDate()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE)
                    viewModel.deadlineProgramState.value = formatted
                }
                showDeadlineProgramDatePicker = false
            },
            onDismiss = { showDeadlineProgramDatePicker = false },
            datePickerState = datePickerState
        )
    }

    // User Picker Dialog
    activeUserTypeDialog?.let { userType ->
        val pickerState = userPickersState[userType] ?: WorkCreateViewModel.UserPickerState()
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
            pickerState = pickerState,
            onSearch = { query -> viewModel.loadUsers(userType, query) },
            onLoadMore = { viewModel.loadMoreUsers(userType) },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomDatePickerDialog(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
    datePickerState: DatePickerState
) {
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onDateSelected(datePickerState.selectedDateMillis) }) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
private fun UserPickerSelectorCard(
    label: String,
    selectedUser: User?,
    onClick: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = selectedUser?.let { user ->
                            buildString {
                                if (!user.degreeBefore.isNullOrEmpty()) append("${user.degreeBefore} ")
                                append("${user.firstname} ${user.lastname}")
                                if (!user.degreeAfter.isNullOrEmpty()) append(" ${user.degreeAfter}")
                            }
                        } ?: stringResource(R.string.not_specified),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (selectedUser != null) FontWeight.Medium else FontWeight.Normal
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null
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
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.75f),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.categories),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.close)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onClearAll() }
                            .padding(vertical = 10.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.none),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    categories.forEach { category ->
                        val isSelected = selectedCategories.any { it.id == category.id }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onCategoryToggle(category) }
                                .padding(vertical = 8.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = { onCategoryToggle(category) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.ok))
                }
            }
        }
    }
}
