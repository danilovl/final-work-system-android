package com.finalworksystem.presentation.ui.event_calendar.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.finalworksystem.data.event_calendar.model.request.EventCalendarCreateRequest
import com.finalworksystem.domain.event_calendar.model.EventCalendarManageCreateData
import com.finalworksystem.ui.designsystem.AppButton
import com.finalworksystem.ui.designsystem.AppButtonVariant
import com.finalworksystem.ui.designsystem.AppDatePickerDialog
import com.finalworksystem.ui.designsystem.AppDialog
import com.finalworksystem.ui.designsystem.AppDropdownField
import com.finalworksystem.ui.designsystem.AppDropdownMenuItem
import com.finalworksystem.ui.designsystem.AppIcon
import com.finalworksystem.ui.designsystem.AppIconBadge
import com.finalworksystem.ui.designsystem.AppSelectField
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.designsystem.AppTextField
import com.finalworksystem.ui.designsystem.AppTimePickerDialog
import com.finalworksystem.ui.designsystem.toUtcMillis
import com.finalworksystem.ui.designsystem.utcMillisToLocalDate
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun CreateEventModal(
    createData: EventCalendarManageCreateData,
    selectedDate: LocalDate,
    selectedTypeId: Int?,
    eventName: String,
    selectedAddressId: Int?,
    selectedUserId: Int?,
    selectedWorkId: Int?,
    startDateTime: LocalDateTime?,
    endDateTime: LocalDateTime?,
    isCreatingEvent: Boolean,
    onTypeSelected: (Int) -> Unit,
    onNameChanged: (String) -> Unit,
    onAddressSelected: (Int) -> Unit,
    onUserSelected: (Int?) -> Unit,
    onWorkSelected: (Int?) -> Unit,
    onStartDateTimeChanged: (LocalDateTime) -> Unit,
    onEndDateTimeChanged: (LocalDateTime) -> Unit,
    onCreateEvent: (EventCalendarCreateRequest) -> Unit,
    onDismiss: () -> Unit
) {
    val displayFormatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm") }
    val unknownAddress = stringResource(R.string.unknown_address)

    AppDialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AppIconBadge(icon = AppIcons.Add, size = 40)
                Spacer(modifier = Modifier.width(12.dp))
                AppText(
                    text = stringResource(R.string.create_event),
                    style = AppTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTheme.colors.onSurface
                )
            }

            AppSelectField(
                selected = createData.types.find { it.id == selectedTypeId },
                options = createData.types,
                optionLabel = { it.name },
                onSelect = { onTypeSelected(it.id) },
                label = { AppText(stringResource(R.string.event_type)) },
                modifier = Modifier.fillMaxWidth()
            )

            AppTextField(
                value = eventName,
                onValueChange = onNameChanged,
                label = { AppText(stringResource(R.string.event_name_optional)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            AppSelectField(
                selected = createData.addresses.find { it.id == selectedAddressId },
                options = createData.addresses,
                optionLabel = { it.name ?: unknownAddress },
                onSelect = { onAddressSelected(it.id) },
                label = { AppText(stringResource(R.string.address)) },
                modifier = Modifier.fillMaxWidth()
            )

            var expandedParticipant by remember { mutableStateOf(false) }
            AppDropdownField(
                value = createData.participants.find { it.user.id == selectedUserId }?.let {
                    it.user.fullName ?: "${it.user.firstname} ${it.user.lastname}"
                } ?: if (selectedUserId == null) stringResource(R.string.none) else "",
                expanded = expandedParticipant,
                onExpandedChange = { expandedParticipant = it },
                label = { AppText(stringResource(R.string.participant)) },
                leadingIcon = {
                    AppIcon(
                        imageVector = AppIcons.Person,
                        contentDescription = null,
                        tint = AppTheme.colors.onSurfaceVariant
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                AppDropdownMenuItem(
                    text = stringResource(R.string.none),
                    selected = selectedUserId == null,
                    onClick = {
                        onUserSelected(null)
                        onWorkSelected(null)
                        expandedParticipant = false
                    }
                )
                createData.participants.forEach { participant ->
                    AppDropdownMenuItem(
                        text = participant.user.fullName ?: "${participant.user.firstname} ${participant.user.lastname}",
                        supportingText = participant.work?.title?.takeIf { it.isNotBlank() },
                        selected = participant.user.id == selectedUserId,
                        onClick = {
                            onUserSelected(participant.user.id)
                            onWorkSelected(participant.work?.id)
                            expandedParticipant = false
                        }
                    )
                }
            }

            var showStartDatePicker by remember { mutableStateOf(false) }
            var showStartTimePicker by remember { mutableStateOf(false) }
            var showEndDatePicker by remember { mutableStateOf(false) }
            var showEndTimePicker by remember { mutableStateOf(false) }

            AppTextField(
                value = startDateTime?.format(displayFormatter) ?: "",
                onValueChange = { },
                readOnly = true,
                label = { AppText(stringResource(R.string.start_date_time)) },
                placeholder = { AppText(stringResource(R.string.select_date_time)) },
                leadingIcon = {
                    AppIcon(
                        imageVector = AppIcons.CalendarToday,
                        contentDescription = null,
                        tint = AppTheme.colors.onSurfaceVariant
                    )
                },
                onClick = { showStartDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            )

            AppTextField(
                value = endDateTime?.format(displayFormatter) ?: "",
                onValueChange = { },
                readOnly = true,
                label = { AppText(stringResource(R.string.end_date_time)) },
                placeholder = { AppText(stringResource(R.string.select_date_time)) },
                leadingIcon = {
                    AppIcon(
                        imageVector = AppIcons.DateRange,
                        contentDescription = null,
                        tint = AppTheme.colors.onSurfaceVariant
                    )
                },
                onClick = { showEndDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            )

            if (showStartDatePicker) {
                AppDatePickerDialog(
                    onDismissRequest = { showStartDatePicker = false },
                    onDateSelected = { selectedDateMillis ->
                        selectedDateMillis?.let { millis ->
                            val selectedLocalDate = utcMillisToLocalDate(millis)
                            val currentTime = startDateTime?.toLocalTime() ?: LocalTime.of(9, 0)
                            onStartDateTimeChanged(LocalDateTime.of(selectedLocalDate, currentTime))
                        }
                        showStartDatePicker = false
                        showStartTimePicker = true
                    },
                    initialSelectedDateMillis = (startDateTime?.toLocalDate() ?: selectedDate).toUtcMillis(),
                    title = stringResource(R.string.start_date_time)
                )
            }

            if (showStartTimePicker) {
                AppTimePickerDialog(
                    onDismissRequest = { showStartTimePicker = false },
                    onTimeSelected = { hour, minute ->
                        val currentDate = startDateTime?.toLocalDate() ?: selectedDate
                        onStartDateTimeChanged(LocalDateTime.of(currentDate, LocalTime.of(hour, minute)))
                        showStartTimePicker = false
                    },
                    initialHour = startDateTime?.hour ?: 9,
                    initialMinute = startDateTime?.minute ?: 0,
                    title = stringResource(R.string.start_date_time)
                )
            }

            if (showEndDatePicker) {
                AppDatePickerDialog(
                    onDismissRequest = { showEndDatePicker = false },
                    onDateSelected = { selectedDateMillis ->
                        selectedDateMillis?.let { millis ->
                            val selectedLocalDate = utcMillisToLocalDate(millis)
                            val currentTime = endDateTime?.toLocalTime() ?: LocalTime.of(10, 0)
                            onEndDateTimeChanged(LocalDateTime.of(selectedLocalDate, currentTime))
                        }
                        showEndDatePicker = false
                        showEndTimePicker = true
                    },
                    initialSelectedDateMillis = (endDateTime?.toLocalDate() ?: selectedDate).toUtcMillis(),
                    title = stringResource(R.string.end_date_time)
                )
            }

            if (showEndTimePicker) {
                AppTimePickerDialog(
                    onDismissRequest = { showEndTimePicker = false },
                    onTimeSelected = { hour, minute ->
                        val currentDate = endDateTime?.toLocalDate() ?: selectedDate
                        onEndDateTimeChanged(LocalDateTime.of(currentDate, LocalTime.of(hour, minute)))
                        showEndTimePicker = false
                    },
                    initialHour = endDateTime?.hour ?: 10,
                    initialMinute = endDateTime?.minute ?: 0,
                    title = stringResource(R.string.end_date_time)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppButton(
                    text = stringResource(R.string.cancel),
                    onClick = onDismiss,
                    variant = AppButtonVariant.Outline,
                    modifier = Modifier.weight(1f)
                )
                AppButton(
                    text = stringResource(R.string.create),
                    onClick = {
                        if (selectedTypeId != null && selectedAddressId != null &&
                            startDateTime != null && endDateTime != null) {
                            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                            val request = EventCalendarCreateRequest(
                                typeId = selectedTypeId,
                                name = eventName.ifBlank { null },
                                addressId = selectedAddressId,
                                userId = selectedUserId,
                                workId = selectedWorkId,
                                start = startDateTime.format(formatter),
                                end = endDateTime.format(formatter)
                            )
                            onCreateEvent(request)
                        }
                    },
                    enabled = !isCreatingEvent && selectedTypeId != null && selectedAddressId != null &&
                            startDateTime != null && endDateTime != null,
                    loading = isCreatingEvent,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
