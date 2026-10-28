package com.finalworksystem.presentation.ui.work.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.finalworksystem.R
import com.finalworksystem.domain.user.model.User
import com.finalworksystem.domain.user.model.UserType
import com.finalworksystem.domain.work.model.WorkFilter
import com.finalworksystem.domain.work.model.WorkStatus
import com.finalworksystem.domain.work.model.WorkType
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkFilterDialog(
    isVisible: Boolean,
    initialFilter: WorkFilter,
    workTypes: List<WorkType>,
    workStatuses: List<WorkStatus>,
    userPickersState: Map<UserType, UserPickerState>,
    selectedAuthor: User?,
    selectedOpponent: User?,
    onLoadUsers: (UserType, String) -> Unit,
    onLoadMoreUsers: (UserType) -> Unit,
    onAuthorSelected: (User?) -> Unit,
    onOpponentSelected: (User?) -> Unit,
    onApplyFilter: (WorkFilter) -> Unit,
    onResetFilter: () -> Unit,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        var currentFilter by remember(initialFilter) { mutableStateOf(initialFilter) }
        var showDatePicker by remember { mutableStateOf(false) }
        var showAuthorPicker by remember { mutableStateOf(false) }
        var showOpponentPicker by remember { mutableStateOf(false) }

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
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.85f),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.filter_works),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(R.string.close)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Title
                        OutlinedTextField(
                            value = currentFilter.title ?: "",
                            onValueChange = { currentFilter = currentFilter.copy(title = it.takeIf { it.isNotBlank() }) },
                            label = { Text(stringResource(R.string.title)) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Shortcut
                        OutlinedTextField(
                            value = currentFilter.shortcut ?: "",
                            onValueChange = { currentFilter = currentFilter.copy(shortcut = it.takeIf { it.isNotBlank() }) },
                            label = { Text(stringResource(R.string.shortcut)) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Work Type
                        TypeDropdown(
                            selectedTypeId = currentFilter.typeId,
                            types = workTypes,
                            onTypeSelected = { currentFilter = currentFilter.copy(typeId = it) }
                        )

                        // Work Status
                        StatusMultiSelect(
                            selectedStatusIds = currentFilter.statusIds ?: emptyList(),
                            statuses = workStatuses,
                            onStatusesChanged = { currentFilter = currentFilter.copy(statusIds = it.takeIf { it.isNotEmpty() }) }
                        )

                        // Author
                        val authorName = selectedAuthor?.let { "${it.firstname} ${it.lastname}" } ?: stringResource(R.string.none)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showAuthorPicker = true }
                        ) {
                            OutlinedTextField(
                                value = authorName,
                                onValueChange = { },
                                label = { Text(stringResource(R.string.author)) },
                                readOnly = true,
                                enabled = false,
                                leadingIcon = { Icon(Icons.Default.Person, null) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        // Opponent
                        val opponentName = selectedOpponent?.let { "${it.firstname} ${it.lastname}" } ?: stringResource(R.string.none)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showOpponentPicker = true }
                        ) {
                            OutlinedTextField(
                                value = opponentName,
                                onValueChange = { },
                                label = { Text(stringResource(R.string.opponent)) },
                                readOnly = true,
                                enabled = false,
                                leadingIcon = { Icon(Icons.Default.Person, null) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        // Deadline
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDatePicker = true }
                        ) {
                            OutlinedTextField(
                                value = currentFilter.deadline ?: "",
                                onValueChange = { },
                                label = { Text(stringResource(R.string.deadline)) },
                                readOnly = true,
                                enabled = false,
                                trailingIcon = {
                                    IconButton(onClick = { showDatePicker = true }) {
                                        Icon(Icons.Default.CalendarToday, null)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = {
                            onResetFilter()
                            onDismiss()
                        }) {
                            Text(stringResource(R.string.clear_filter))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                onApplyFilter(currentFilter)
                                onDismiss()
                            }
                        ) {
                            Text(stringResource(R.string.apply_filter))
                        }
                    }
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
                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val dateString = sdf.format(Date(millis))
                            currentFilter = currentFilter.copy(deadline = dateString)
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

        if (showAuthorPicker) {
            UserSelectionDialog(
                title = stringResource(R.string.select_author),
                isVisible = showAuthorPicker,
                selectedUser = selectedAuthor,
                pickerState = userPickersState[UserType.AUTHOR] ?: UserPickerState(),
                onSearch = { onLoadUsers(UserType.AUTHOR, it) },
                onLoadMore = { onLoadMoreUsers(UserType.AUTHOR) },
                onUserSelected = {
                    onAuthorSelected(it)
                    currentFilter = currentFilter.copy(authorId = it?.id)
                    showAuthorPicker = false
                },
                onDismiss = { showAuthorPicker = false }
            )
        }

        if (showOpponentPicker) {
            UserSelectionDialog(
                title = stringResource(R.string.select_opponent),
                isVisible = showOpponentPicker,
                selectedUser = selectedOpponent,
                pickerState = userPickersState[UserType.OPPONENT] ?: UserPickerState(),
                onSearch = { onLoadUsers(UserType.OPPONENT, it) },
                onLoadMore = { onLoadMoreUsers(UserType.OPPONENT) },
                onUserSelected = {
                    onOpponentSelected(it)
                    currentFilter = currentFilter.copy(opponentId = it?.id)
                    showOpponentPicker = false
                },
                onDismiss = { showOpponentPicker = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypeDropdown(
    selectedTypeId: Int?,
    types: List<WorkType>,
    onTypeSelected: (Int?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedTypeName = types.find { it.id == selectedTypeId }?.name ?: stringResource(R.string.all_types)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedTypeName,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.type)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.all_types)) },
                onClick = {
                    onTypeSelected(null)
                    expanded = false
                }
            )
            types.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.name) },
                    onClick = {
                        onTypeSelected(type.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusMultiSelect(
    selectedStatusIds: List<Int>,
    statuses: List<WorkStatus>,
    onStatusesChanged: (List<Int>) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedStatusesNames = if (selectedStatusIds.isEmpty()) {
        stringResource(R.string.all_statuses)
    } else {
        statuses.filter { it.id in selectedStatusIds }.joinToString(", ") { it.name }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedStatusesNames,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.status)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            statuses.forEach { status ->
                val isSelected = status.id in selectedStatusIds
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(status.name)
                        }
                    },
                    onClick = {
                        val newSelectedIds = if (isSelected) {
                            selectedStatusIds - status.id
                        } else {
                            selectedStatusIds + status.id
                        }
                        onStatusesChanged(newSelectedIds)
                    }
                )
            }
            if (selectedStatusIds.isNotEmpty()) {
                Divider()
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.clear_filter)) },
                    onClick = {
                        onStatusesChanged(emptyList())
                        expanded = false
                    }
                )
            }
        }
    }
}
