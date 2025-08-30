package com.finalworksystem.presentation.ui.event_calendar.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.finalworksystem.data.event_calendar.model.request.EventCalendarCreateRequest
import com.finalworksystem.domain.event_calendar.model.EventCalendarManageCreateData
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
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
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(horizontal = 8.dp, vertical = 16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Create Event",
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp),
                    fontWeight = FontWeight.Bold
                )

                var expandedType by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedType,
                    onExpandedChange = { expandedType = !expandedType }
                ) {
                    OutlinedTextField(
                        value = createData.types.find { it.id == selectedTypeId }?.name ?: "",
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Event Type", fontSize = 12.sp) },
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedType,
                        onDismissRequest = { expandedType = false }
                    ) {
                        createData.types.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.name, fontSize = 14.sp) },
                                onClick = {
                                    onTypeSelected(type.id)
                                    expandedType = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = eventName,
                    onValueChange = onNameChanged,
                    label = { Text("Event Name (Optional)", fontSize = 12.sp) },
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                    modifier = Modifier.fillMaxWidth()
                )

                var expandedAddress by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedAddress,
                    onExpandedChange = { expandedAddress = !expandedAddress }
                ) {
                    OutlinedTextField(
                        value = createData.addresses.find { it.id == selectedAddressId }?.name ?: "",
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Address", fontSize = 12.sp) },
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedAddress) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedAddress,
                        onDismissRequest = { expandedAddress = false }
                    ) {
                        createData.addresses.forEach { address ->
                            DropdownMenuItem(
                                text = { Text(address.name ?: "Unknown Address", fontSize = 14.sp) },
                                onClick = {
                                    onAddressSelected(address.id)
                                    expandedAddress = false
                                }
                            )
                        }
                    }
                }

                var expandedParticipant by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedParticipant,
                    onExpandedChange = { expandedParticipant = !expandedParticipant }
                ) {
                    OutlinedTextField(
                        value = createData.participants.find { it.user.id == selectedUserId }?.let { 
                            "${it.user.firstname} ${it.user.lastname}"
                        } ?: if (selectedUserId == null) "None" else "",
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Participant", fontSize = 12.sp) },
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedParticipant) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedParticipant,
                        onDismissRequest = { expandedParticipant = false }
                    ) {
                        DropdownMenuItem(
                            text = { 
                                Text(
                                    text = "None",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            onClick = {
                                onUserSelected(null)
                                onWorkSelected(null)
                                expandedParticipant = false
                            }
                        )
                        createData.participants.forEach { participant ->
                            DropdownMenuItem(
                                text = { 
                                    Column {
                                        Text(
                                            text = "${participant.user.firstname} ${participant.user.lastname}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        participant.work?.title?.let { title ->
                                            if (title.isNotBlank()) {
                                                Text(
                                                    text = title,
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                },
                                onClick = {
                                    onUserSelected(participant.user.id)
                                    onWorkSelected(participant.work?.id)
                                    expandedParticipant = false
                                }
                            )
                        }
                    }
                }

                var showStartDatePicker by remember { mutableStateOf(false) }
                var showStartTimePicker by remember { mutableStateOf(false) }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showStartDatePicker = true }
                ) {
                    OutlinedTextField(
                        value = startDateTime?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) ?: "",
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Start Date Time", fontSize = 12.sp) },
                        placeholder = { Text("Select date and time", fontSize = 12.sp) },
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false
                    )
                }

                var showEndDatePicker by remember { mutableStateOf(false) }
                var showEndTimePicker by remember { mutableStateOf(false) }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showEndDatePicker = true }
                ) {
                    OutlinedTextField(
                        value = endDateTime?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) ?: "",
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("End Date Time", fontSize = 12.sp) },
                        placeholder = { Text("Select date and time", fontSize = 12.sp) },
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false
                    )
                }

                if (showStartDatePicker) {
                    val datePickerState = rememberDatePickerState(
                        initialSelectedDateMillis = startDateTime?.toLocalDate()?.toEpochDay()
                            ?.times(24 * 60 * 60 * 1000)
                            ?: (selectedDate.toEpochDay() * 24 * 60 * 60 * 1000)
                    )
                    CustomDatePickerDialog(
                        onDateSelected = { selectedDateMillis ->
                            selectedDateMillis?.let { millis ->
                                val selectedLocalDate = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                                val currentTime = startDateTime?.toLocalTime() ?: LocalTime.of(9, 0)
                                onStartDateTimeChanged(LocalDateTime.of(selectedLocalDate, currentTime))
                            }
                            showStartDatePicker = false
                            showStartTimePicker = true
                        },
                        onDismiss = { showStartDatePicker = false },
                        datePickerState = datePickerState
                    )
                }

                if (showStartTimePicker) {
                    val timePickerState = rememberTimePickerState(
                        initialHour = startDateTime?.hour ?: 9,
                        initialMinute = startDateTime?.minute ?: 0
                    )
                    TimePickerDialog(
                        onTimeSelected = { hour, minute ->
                            val currentDate = startDateTime?.toLocalDate() ?: selectedDate
                            onStartDateTimeChanged(LocalDateTime.of(currentDate, LocalTime.of(hour, minute)))
                            showStartTimePicker = false
                        },
                        onDismiss = { showStartTimePicker = false },
                        timePickerState = timePickerState
                    )
                }

                if (showEndDatePicker) {
                    val datePickerState = rememberDatePickerState(
                        initialSelectedDateMillis = endDateTime?.toLocalDate()?.toEpochDay()
                            ?.times(24 * 60 * 60 * 1000)
                            ?: (selectedDate.toEpochDay() * 24 * 60 * 60 * 1000)
                    )
                    CustomDatePickerDialog(
                        onDateSelected = { selectedDateMillis ->
                            selectedDateMillis?.let { millis ->
                                val selectedLocalDate = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                                val currentTime = endDateTime?.toLocalTime() ?: LocalTime.of(10, 0)
                                onEndDateTimeChanged(LocalDateTime.of(selectedLocalDate, currentTime))
                            }
                            showEndDatePicker = false
                            showEndTimePicker = true
                        },
                        onDismiss = { showEndDatePicker = false },
                        datePickerState = datePickerState
                    )
                }

                if (showEndTimePicker) {
                    val timePickerState = rememberTimePickerState(
                        initialHour = endDateTime?.hour ?: 10,
                        initialMinute = endDateTime?.minute ?: 0
                    )
                    TimePickerDialog(
                        onTimeSelected = { hour, minute ->
                            val currentDate = endDateTime?.toLocalDate() ?: selectedDate
                            onEndDateTimeChanged(LocalDateTime.of(currentDate, LocalTime.of(hour, minute)))
                            showEndTimePicker = false
                        },
                        onDismiss = { showEndTimePicker = false },
                        timePickerState = timePickerState
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", fontSize = 14.sp)
                    }
                    Button(
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
                                startDateTime != null && endDateTime != null
                    ) {
                        if (isCreatingEvent) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Create", fontSize = 14.sp)
                        }
                    }
                }
            }
        }
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
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    onTimeSelected: (Int, Int) -> Unit,
    onDismiss: () -> Unit,
    timePickerState: TimePickerState
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { 
                onTimeSelected(timePickerState.hour, timePickerState.minute)
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        text = {
            TimePicker(state = timePickerState)
        }
    )
}
