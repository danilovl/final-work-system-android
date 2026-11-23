package com.finalworksystem.presentation.ui.work.component

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finalworksystem.R
import com.finalworksystem.domain.user.model.User
import com.finalworksystem.domain.user.model.UserType
import com.finalworksystem.domain.work.model.WorkFilter
import com.finalworksystem.domain.work.model.WorkStatus
import com.finalworksystem.domain.work.model.WorkType
import com.finalworksystem.presentation.paging.UserPicker
import com.finalworksystem.ui.designsystem.AppButton
import com.finalworksystem.ui.designsystem.AppButtonVariant
import com.finalworksystem.ui.designsystem.AppCheckbox
import com.finalworksystem.ui.designsystem.AppDatePickerDialog
import com.finalworksystem.ui.designsystem.AppDialog
import com.finalworksystem.ui.designsystem.AppDivider
import com.finalworksystem.ui.designsystem.AppDropdownField
import com.finalworksystem.ui.designsystem.AppDropdownMenuItem
import com.finalworksystem.ui.designsystem.AppIcon
import com.finalworksystem.ui.designsystem.AppIconBadge
import com.finalworksystem.ui.designsystem.AppIconButton
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.designsystem.AppTextField
import com.finalworksystem.ui.designsystem.toUtcMillis
import com.finalworksystem.ui.designsystem.utcMillisToLocalDate
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun WorkFilterDialog(
    isVisible: Boolean,
    initialFilter: WorkFilter,
    workTypes: List<WorkType>,
    workStatuses: List<WorkStatus>,
    userPicker: UserPicker,
    selectedAuthor: User?,
    selectedOpponent: User?,
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

        AppDialog(
            onDismissRequest = onDismiss,
            modifier = Modifier.fillMaxHeight(0.85f)
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
                    AppIconBadge(icon = AppIcons.Search, size = 36)
                    Spacer(modifier = Modifier.width(12.dp))
                    AppText(
                        text = stringResource(R.string.filter_works),
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

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Title
                    AppTextField(
                        value = currentFilter.title ?: "",
                        onValueChange = { currentFilter = currentFilter.copy(title = it.takeIf { it.isNotBlank() }) },
                        label = { AppText(stringResource(R.string.title)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Shortcut
                    AppTextField(
                        value = currentFilter.shortcut ?: "",
                        onValueChange = { currentFilter = currentFilter.copy(shortcut = it.takeIf { it.isNotBlank() }) },
                        label = { AppText(stringResource(R.string.shortcut)) },
                        singleLine = true,
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
                    AppTextField(
                        value = authorName,
                        onValueChange = { },
                        label = { AppText(stringResource(R.string.author)) },
                        readOnly = true,
                        singleLine = true,
                        leadingIcon = { AppIcon(AppIcons.Person, null) },
                        trailingIcon = { AppIcon(AppIcons.KeyboardArrowDown, null) },
                        onClick = { showAuthorPicker = true },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Opponent
                    val opponentName = selectedOpponent?.let { "${it.firstname} ${it.lastname}" } ?: stringResource(R.string.none)
                    AppTextField(
                        value = opponentName,
                        onValueChange = { },
                        label = { AppText(stringResource(R.string.opponent)) },
                        readOnly = true,
                        singleLine = true,
                        leadingIcon = { AppIcon(AppIcons.Person, null) },
                        trailingIcon = { AppIcon(AppIcons.KeyboardArrowDown, null) },
                        onClick = { showOpponentPicker = true },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Deadline
                    AppTextField(
                        value = currentFilter.deadline ?: "",
                        onValueChange = { },
                        label = { AppText(stringResource(R.string.deadline)) },
                        readOnly = true,
                        singleLine = true,
                        trailingIcon = { AppIcon(AppIcons.CalendarToday, null) },
                        onClick = { showDatePicker = true },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AppButton(
                        text = stringResource(R.string.clear_filter),
                        onClick = {
                            onResetFilter()
                            onDismiss()
                        },
                        variant = AppButtonVariant.Outline,
                        modifier = Modifier.weight(1f)
                    )
                    AppButton(
                        text = stringResource(R.string.apply_filter),
                        onClick = {
                            onApplyFilter(currentFilter)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        if (showDatePicker) {
            val initialMillis = remember(currentFilter.deadline) {
                try {
                    currentFilter.deadline?.takeIf { it.isNotBlank() }?.let { LocalDate.parse(it).toUtcMillis() }
                } catch (_: Exception) {
                    null
                }
            }
            AppDatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                onDateSelected = { millis ->
                    millis?.let {
                        val dateString = utcMillisToLocalDate(it).format(DateTimeFormatter.ISO_LOCAL_DATE)
                        currentFilter = currentFilter.copy(deadline = dateString)
                    }
                    showDatePicker = false
                },
                initialSelectedDateMillis = initialMillis,
                title = stringResource(R.string.deadline)
            )
        }

        if (showAuthorPicker) {
            UserSelectionDialog(
                title = stringResource(R.string.select_author),
                isVisible = showAuthorPicker,
                selectedUser = selectedAuthor,
                users = userPicker.users(UserType.AUTHOR),
                initialQuery = userPicker.searchQuery(UserType.AUTHOR),
                onSearch = { userPicker.search(UserType.AUTHOR, it) },
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
                users = userPicker.users(UserType.OPPONENT),
                initialQuery = userPicker.searchQuery(UserType.OPPONENT),
                onSearch = { userPicker.search(UserType.OPPONENT, it) },
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

@Composable
fun TypeDropdown(
    selectedTypeId: Int?,
    types: List<WorkType>,
    onTypeSelected: (Int?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedTypeName = types.find { it.id == selectedTypeId }?.name ?: stringResource(R.string.all_types)

    AppDropdownField(
        value = selectedTypeName,
        expanded = expanded,
        onExpandedChange = { expanded = it },
        label = { AppText(stringResource(R.string.type)) },
        modifier = Modifier.fillMaxWidth()
    ) {
        AppDropdownMenuItem(
            text = stringResource(R.string.all_types),
            selected = selectedTypeId == null,
            onClick = {
                onTypeSelected(null)
                expanded = false
            }
        )
        types.forEach { type ->
            AppDropdownMenuItem(
                text = type.name,
                selected = type.id == selectedTypeId,
                onClick = {
                    onTypeSelected(type.id)
                    expanded = false
                }
            )
        }
    }
}

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

    AppDropdownField(
        value = selectedStatusesNames,
        expanded = expanded,
        onExpandedChange = { expanded = it },
        label = { AppText(stringResource(R.string.status)) },
        modifier = Modifier.fillMaxWidth()
    ) {
        statuses.forEach { status ->
            val isSelected = status.id in selectedStatusIds
            AppDropdownMenuItem(
                text = status.name,
                selected = isSelected,
                leadingIcon = {
                    AppCheckbox(
                        checked = isSelected,
                        onCheckedChange = null
                    )
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
            AppDivider(modifier = Modifier.padding(vertical = 4.dp))
            AppDropdownMenuItem(
                text = stringResource(R.string.clear_filter),
                leadingIcon = { AppIcon(AppIcons.Clear, null) },
                onClick = {
                    onStatusesChanged(emptyList())
                    expanded = false
                }
            )
        }
    }
}
