package com.finalworksystem.presentation.ui.event_calendar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.finalworksystem.application.use_case.event_calendar.DeleteEventUseCase
import com.finalworksystem.application.use_case.event_calendar.GetEventCalendarManageCreateDataUseCase
import com.finalworksystem.application.use_case.event_calendar.GetEventCalendarUseCase
import com.finalworksystem.application.use_case.event_calendar.GetEventCalendarUserWorksUseCase
import com.finalworksystem.application.use_case.event_calendar.PostEventCalendarCreateUseCase
import com.finalworksystem.application.use_case.event_calendar.PostEventCalendarReservationUseCase
import com.finalworksystem.domain.event_calendar.model.CalendarEvent
import com.finalworksystem.domain.event_calendar.model.EventCalendarManageCreateData
import com.finalworksystem.domain.work.model.Work
import com.finalworksystem.infrastructure.user.UserService
import com.finalworksystem.presentation.ui.component.BaseTopAppBar
import com.finalworksystem.presentation.ui.event_calendar.component.CalendarGrid
import com.finalworksystem.presentation.ui.event_calendar.component.CalendarHeader
import com.finalworksystem.presentation.ui.event_calendar.component.CreateEventModal
import com.finalworksystem.presentation.ui.event_calendar.component.EventRegistrationModal
import com.finalworksystem.presentation.ui.event_calendar.component.EventsSection
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

@Composable
fun CalendarScreen(
    calendarType: String? = null,
    onDateSelected: (LocalDate) -> Unit = {},
    onEventClick: ((CalendarEvent) -> Unit)? = null,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val getEventCalendarUseCase = koinInject<GetEventCalendarUseCase>()
    val getEventCalendarManageCreateDataUseCase = koinInject<GetEventCalendarManageCreateDataUseCase>()
    val postEventCalendarCreateUseCase = koinInject<PostEventCalendarCreateUseCase>()
    val deleteEventUseCase = koinInject<DeleteEventUseCase>()
    val getEventCalendarUserWorksUseCase = koinInject<GetEventCalendarUserWorksUseCase>()
    val postEventCalendarReservationUseCase = koinInject<PostEventCalendarReservationUseCase>()
    val userService = koinInject<UserService>()
    val coroutineScope = rememberCoroutineScope()

    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var calendarEvents by remember { mutableStateOf<List<CalendarEvent>>(emptyList()) }
    var isLoadingEvents by remember { mutableStateOf(false) }
    var errorMessageState by remember { mutableStateOf<String?>(null) }

    var showCreateModal by remember { mutableStateOf(false) }
    var createData by remember { mutableStateOf<EventCalendarManageCreateData?>(null) }
    var isLoadingCreateData by remember { mutableStateOf(false) }
    var isCreatingEvent by remember { mutableStateOf(false) }

    var cachedCreateData by remember { mutableStateOf<EventCalendarManageCreateData?>(null) }

    var isSupervisor by remember { mutableStateOf(false) }
    var isStudent by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var eventToDelete by remember { mutableStateOf<CalendarEvent?>(null) }
    var isDeletingEvent by remember { mutableStateOf(false) }

    var showRegistrationModal by remember { mutableStateOf(false) }
    var eventToRegister by remember { mutableStateOf<CalendarEvent?>(null) }
    var userWorks by remember { mutableStateOf<List<Work>>(emptyList()) }
    var isLoadingUserWorks by remember { mutableStateOf(false) }
    var isRegistering by remember { mutableStateOf(false) }

    var selectedTypeId by remember { mutableStateOf<Int?>(null) }
    var eventName by remember { mutableStateOf("") }
    var selectedAddressId by remember { mutableStateOf<Int?>(null) }
    var selectedUserId by remember { mutableStateOf<Int?>(null) }
    var selectedWorkId by remember { mutableStateOf<Int?>(null) }
    var startDateTime by remember { mutableStateOf<LocalDateTime?>(null) }
    var endDateTime by remember { mutableStateOf<LocalDateTime?>(null) }

    val initialPage = Int.MAX_VALUE / 2
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { Int.MAX_VALUE }
    )

    val pagerCurrentMonth = remember(pagerState.currentPage) {
        YearMonth.now().plusMonths((pagerState.currentPage - initialPage).toLong())
    }

    LaunchedEffect(pagerCurrentMonth) {
        currentMonth = pagerCurrentMonth
        selectedDate = if (pagerCurrentMonth == YearMonth.now()) {
            LocalDate.now()
        } else {
            pagerCurrentMonth.atDay(1)
        }
    }

    val loadCalendarEvents = {
        if (calendarType != null) {
            val startDate = currentMonth.atDay(1).toString()
            val endDate = currentMonth.atEndOfMonth().toString()

            isLoadingEvents = true
            errorMessageState = null

            coroutineScope.launch {
                getEventCalendarUseCase(calendarType, startDate, endDate).collectLatest { result ->
                    isLoadingEvents = false
                    result.fold(
                        onSuccess = { eventList ->
                            calendarEvents = eventList
                        },
                        onFailure = { error ->
                            errorMessageState = error.message
                        }
                    )
                }
            }
        }
    }

    val loadCreateData = {
        if (cachedCreateData != null) {
            createData = cachedCreateData
            showCreateModal = true
        } else {
            isLoadingCreateData = true
            coroutineScope.launch {
                getEventCalendarManageCreateDataUseCase().collectLatest { result ->
                    isLoadingCreateData = false
                    result.fold(
                        onSuccess = { data ->
                            cachedCreateData = data
                            createData = data
                            showCreateModal = true
                        },
                        onFailure = { error ->
                            errorMessageState = error.message
                        }
                    )
                }
            }
        }
    }

    val deleteEvent = { event: CalendarEvent ->
        eventToDelete = event
        showDeleteConfirmDialog = true
    }

    val confirmDeleteEvent = {
        eventToDelete?.let { event ->
            isDeletingEvent = true
            coroutineScope.launch {
                deleteEventUseCase(event.id).collectLatest { result ->
                    isDeletingEvent = false
                    result.fold(
                        onSuccess = {
                            showDeleteConfirmDialog = false
                            eventToDelete = null
                            loadCalendarEvents()
                        },
                        onFailure = { error ->
                            errorMessageState = error.message
                            showDeleteConfirmDialog = false
                            eventToDelete = null
                        }
                    )
                }
            }
        }
    }

    val loadUserWorks = {
        isLoadingUserWorks = true
        coroutineScope.launch {
            getEventCalendarUserWorksUseCase().collectLatest { result ->
                isLoadingUserWorks = false
                result.fold(
                    onSuccess = { works ->
                        userWorks = works
                        showRegistrationModal = true
                    },
                    onFailure = { error ->
                        errorMessageState = error.message
                    }
                )
            }
        }
    }

    val handleEventRegistration: (CalendarEvent) -> Unit = { event ->
        if (isStudent) {
            if (event.hasParticipant) {
                onEventClick?.invoke(event)
            } else {
                eventToRegister = event
                loadUserWorks()
            }
        } else {
            onEventClick?.invoke(event)
        }
    }

    val makeReservation: (Int) -> Unit = { workId ->
        eventToRegister?.let { event ->
            isRegistering = true
            coroutineScope.launch {
                postEventCalendarReservationUseCase(event.id, workId).collectLatest { result ->
                    isRegistering = false
                    result.fold(
                        onSuccess = {
                            showRegistrationModal = false
                            eventToRegister = null
                            userWorks = emptyList()
                            loadCalendarEvents()
                        },
                        onFailure = { error ->
                            errorMessageState = error.message
                        }
                    )
                }
            }
        }
    }

    LaunchedEffect(currentMonth, calendarType) {
        loadCalendarEvents()
    }

    LaunchedEffect(selectedDate) {
        onDateSelected(selectedDate)
    }

    LaunchedEffect(Unit) {
        try {
            isSupervisor = userService.isSupervisor()
            isStudent = userService.isStudent()
        } catch (_: Exception) {
            isSupervisor = false
            isStudent = false
        }
    }

    Scaffold(
        topBar = {
            BaseTopAppBar(
                title = "Calendar",
                onNavigateBack = onNavigateBack,
                onReload = { 
                    loadCalendarEvents()
                },
                loadedCount = calendarEvents.size,
                totalCount = calendarEvents.size,
                isLoading = isLoadingEvents,
                additionalActions = {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(initialPage)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Today,
                            contentDescription = "Go to Today"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (calendarType == "manage") {
                FloatingActionButton(
                    onClick = { loadCreateData() },
                    modifier = Modifier.padding(16.dp)
                ) {
                    if (isLoadingCreateData) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Create Event"
                        )
                    }
                }
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            CalendarHeader(
                currentMonth = currentMonth,
                onPreviousMonth = { 
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                    }
                },
                onNextMonth = { 
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                val pageMonth = YearMonth.now().plusMonths((page - initialPage).toLong())

                Column(
                    modifier = Modifier.fillMaxHeight()
                ) {
                    CalendarGrid(
                        currentMonth = pageMonth,
                        selectedDate = selectedDate,
                        onDateSelected = { date ->
                            selectedDate = date
                        },
                        eventsForMonth = if (pageMonth == currentMonth) calendarEvents else emptyList()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = if (calendarType == "manage") 80.dp else 0.dp)
                    ) {
                        EventsSection(
                            selectedDate = selectedDate,
                            events = if (pageMonth == currentMonth) calendarEvents else emptyList(),
                            isLoading = isLoadingEvents && pageMonth == currentMonth,
                            errorMessage = if (pageMonth == currentMonth) errorMessageState else null,
                            onEventClick = if (isSupervisor || isStudent) handleEventRegistration else null,
                            isSupervisor = isSupervisor,
                            isStudent = isStudent,
                            onDeleteEvent = deleteEvent,
                            onReserveEvent = if (isStudent) handleEventRegistration else null
                        )
                    }
                }
            }
        }

        if (showCreateModal && createData != null) {
            CreateEventModal(
                createData = createData!!,
                selectedDate = selectedDate,
                selectedTypeId = selectedTypeId,
                eventName = eventName,
                selectedAddressId = selectedAddressId,
                selectedUserId = selectedUserId,
                selectedWorkId = selectedWorkId,
                startDateTime = startDateTime,
                endDateTime = endDateTime,
                isCreatingEvent = isCreatingEvent,
                onTypeSelected = { selectedTypeId = it },
                onNameChanged = { eventName = it },
                onAddressSelected = { selectedAddressId = it },
                onUserSelected = { selectedUserId = it },
                onWorkSelected = { selectedWorkId = it },
                onStartDateTimeChanged = { startDateTime = it },
                onEndDateTimeChanged = { endDateTime = it },
                onCreateEvent = { request ->
                    isCreatingEvent = true
                    coroutineScope.launch {
                        postEventCalendarCreateUseCase(request).collectLatest { result ->
                            isCreatingEvent = false
                            result.fold(
                                onSuccess = { response ->
                                    showCreateModal = false
                                    selectedTypeId = null
                                    eventName = ""
                                    selectedAddressId = null
                                    selectedUserId = null
                                    selectedWorkId = null
                                    startDateTime = null
                                    endDateTime = null
                                    loadCalendarEvents()
                                },
                                onFailure = { error ->
                                    errorMessageState = error.message
                                }
                            )
                        }
                    }
                },
                onDismiss = { 
                    showCreateModal = false
                    selectedTypeId = null
                    eventName = ""
                    selectedAddressId = null
                    selectedUserId = null
                    selectedWorkId = null
                    startDateTime = null
                    endDateTime = null
                }
            )
        }

        if (showRegistrationModal && eventToRegister != null) {
            EventRegistrationModal(
                event = eventToRegister!!,
                userWorks = userWorks,
                isLoading = isLoadingUserWorks,
                isRegistering = isRegistering,
                onDismiss = {
                    showRegistrationModal = false
                    eventToRegister = null
                    userWorks = emptyList()
                },
                onRegister = makeReservation
            )
        }

        if (showDeleteConfirmDialog && eventToDelete != null) {
            AlertDialog(
                onDismissRequest = { 
                    showDeleteConfirmDialog = false
                    eventToDelete = null
                },
                title = { Text("Delete Event") },
                text = { Text("Are you sure you want to delete the event \"${eventToDelete!!.title}\"?") },
                confirmButton = {
                    TextButton(
                        onClick = { confirmDeleteEvent() },
                        enabled = !isDeletingEvent
                    ) {
                        if (isDeletingEvent) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Delete")
                        }
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { 
                            showDeleteConfirmDialog = false
                            eventToDelete = null
                        },
                        enabled = !isDeletingEvent
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
