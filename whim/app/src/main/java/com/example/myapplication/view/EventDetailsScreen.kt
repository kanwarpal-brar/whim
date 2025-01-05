package com.example.myapplication.view

import GlideImage
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.authentication.AttendanceStatus
import com.example.myapplication.authentication.User
import com.example.myapplication.events.Event
import com.example.myapplication.events.EventRepository
import com.example.myapplication.navigation.Routes
import com.example.myapplication.services.LiveLocationForegroundService
import com.example.myapplication.ui.components.RoundedGradientButton
import com.example.myapplication.ui.theme.dark_grey
import com.example.myapplication.ui.theme.header_black
import com.example.myapplication.ui.theme.interDefault
import com.example.myapplication.ui.theme.interLight
import com.example.myapplication.ui.theme.interSemiBold
import com.example.myapplication.ui.theme.page_background_grey
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun EventDetailsScreen(navController: NavController, eventId: String, EventViewModel: EventViewModel,  userViewModel: UserViewModel) {

    var event by remember { mutableStateOf<Event?>(null)}
    var eventRepository = EventRepository()
    val outputFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy • h:mm a")
    val endFormatterSameDay = DateTimeFormatter.ofPattern("h:mm a")
    val endFormatterDifferentDay = DateTimeFormatter.ofPattern("MMMM d, yyyy • h:mm a")

    val formattedStartDate = event?.startTime?.format(outputFormatter)
    val formattedEndDate = event?.let {
        if (it.startTime.toLocalDate() == it.endTime.toLocalDate()) {
            it.endTime.format(endFormatterSameDay)
        } else {
            it.endTime.format(endFormatterDifferentDay)
        }
    }

    fun startLiveLocationService(context: Context, event: Event) {
        val intent = Intent(context, LiveLocationForegroundService::class.java).apply {
            putExtra(LiveLocationForegroundService.TARGET_EVENT_EXTRA, event)
        }
        context.startForegroundService(intent)
    }

    fun stopLiveLocationService(context: Context) {
        val intent = Intent(context, LiveLocationForegroundService::class.java)
        context.stopService(intent)
    }

    val context = LocalContext.current


    val userId = userViewModel.userLiveData.value?.uid ?: ""
    var hostUser by remember { mutableStateOf<User?>(null)}
    var eventAttendees by remember { mutableStateOf<List<User>>(emptyList()) }
    var participantCount by remember { mutableStateOf(0)}
    val isEventFull by remember {
        derivedStateOf {
            println("limit: ${event?.attendeeLimit}, count: $participantCount")
            event?.attendeeLimit == participantCount
        }
    }
    var attendanceState by remember { mutableStateOf<AttendanceStatus?>(AttendanceStatus.NOT_RESERVED) }

    val isCheckInEligible by remember {
        derivedStateOf {
            val currentTime = LocalDateTime.now()
            val eventStartTime = event?.startTime
            val eventEndTime = event?.endTime


            attendanceState == AttendanceStatus.RESERVED && eventStartTime != null && eventEndTime != null && currentTime.isAfter(eventStartTime) && currentTime.isBefore(eventEndTime)
        }
    }
    var showParticipantsDialog by remember { mutableStateOf(false) }

    var eventImageUri by remember { mutableStateOf<String?>("Loading") }

    LaunchedEffect(Unit) {
        val selectedEvent = EventViewModel.events.value?.filter { it.eventID == eventId }
        if (!selectedEvent.isNullOrEmpty()) {
            event = selectedEvent[0]
        }

        val userAttendance = userViewModel.getAttendanceStatus(eventID = eventId, userID = userId)
        if (userAttendance.isSuccess) {
            attendanceState = userAttendance.getOrNull()
        }

        val initialAttendees = event?.let { userViewModel.getAttendees(it.eventID).getOrNull() }
        if (!initialAttendees.isNullOrEmpty()) {
            eventAttendees = initialAttendees
            participantCount = initialAttendees.size
        }

        hostUser = userViewModel.getUser(event?.host ?: "").getOrNull()
        eventImageUri = withContext(Dispatchers.IO) {
            eventRepository.retrieveEventImage(eventId).toString() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = {
                    navController.navigateUp()
                    navController.navigateUp()
                    navController.navigateUp()
                },
                modifier = Modifier
                    .padding(24.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.backarrow),
                    contentDescription = "Back",
                    tint = Color.Unspecified
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f)
        ) {
            GlideImage(
                imageUri = eventImageUri,
                contentDescription = "Event Image",
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)

            ) {
                Text(
                    text = event?.title ?: "",
                    fontFamily = interSemiBold,
                    fontSize = 24.sp,
                    color = header_black,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.detail_map),
                        contentDescription = "Map",
                        tint = Color.Unspecified,
                    )
                    Text(
                        text = event?.address ?: "",
                        fontFamily = interDefault,
                        fontSize = 12.sp,
                        color = dark_grey
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.detail_calendar),
                        contentDescription = "Calendar",
                        tint = Color.Unspecified,
                    )
                    Text(
                        text = "$formattedStartDate - $formattedEndDate",
                        fontFamily = interDefault,
                        fontSize = 12.sp,
                        color = dark_grey
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.detail_users),
                        contentDescription = "Attendees",
                        tint = Color.Unspecified,
                    )
                    Text(
                        text = "$participantCount Participants",
                        fontFamily = interDefault,
                        fontSize = 12.sp,
                        color = dark_grey
                    )
                }
                if (attendanceState == AttendanceStatus.RESERVED) {
                    Text(
                        text = "You have already joined this event.",
                        fontFamily = interDefault,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(color = page_background_grey)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "About",
                    fontFamily = interSemiBold,
                    fontSize = 16.sp,
                    color = dark_grey
                )
                Text(
                    text = event?.description ?: "",
                    fontFamily = interDefault,
                    fontSize = 12.sp,
                    color = dark_grey,
                )


            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(color = page_background_grey)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)

            ) {
                Text(
                    text = "Organizer",
                    fontFamily = interSemiBold,
                    fontSize = 16.sp,
                    color = dark_grey
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.detail_user),
                        contentDescription = "Host Name",
                        tint = Color.Unspecified,
                    )
                    Text(
                        text = if (hostUser != null) hostUser?.firstName + " " + hostUser?.lastName else "",
                        fontFamily = interDefault,
                        fontSize = 12.sp,
                        color = dark_grey
                    )
                }

                if (hostUser !== null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.outlined_star),
                            contentDescription = "Host Rating",
                            tint = dark_grey,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text =  "${hostUser?.hostRating ?: "-"} (${hostUser?.countHostRating?.toInt() ?: "-"} Ratings)",
                            fontFamily = interDefault,
                            fontSize = 12.sp,
                            color = dark_grey
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.detail_mail),
                        contentDescription = "Host Email",
                        tint = Color.Unspecified,
                    )
                    Text(
                        text = hostUser?.email ?: "",
                        fontFamily = interDefault,
                        fontSize = 12.sp,
                        color = dark_grey
                    )
                }
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .background(color = page_background_grey)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.White)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Bottom

            ) {
                if (event?.host == userId) {
                    RoundedGradientButton(
                        text = "View Participants",
                        onClick = {
                            showParticipantsDialog = true
                        },
                        modifier = Modifier,
                    )
                    if (showParticipantsDialog) {
                        EventParticipantsDialog(
                            onDismissRequest = { showParticipantsDialog = false },
                            usernames = eventAttendees.map { it.firstName + " " + it.lastName },
                            emails = eventAttendees.map { it.email },
                            attendeeCount = event?.attendeeCount ?: 0,
                            attendeeLimit = event?.attendeeLimit ?: 0,
                        )
                    }
                } else if (attendanceState == AttendanceStatus.NOT_RESERVED || isCheckInEligible) {
                    RoundedGradientButton(
                        text = if (attendanceState == AttendanceStatus.NOT_RESERVED && isEventFull) {
                            "Full"
                        } else if (isCheckInEligible) {
                            "Check in"
                        } else {
                            "Reserve"
                        },
                        onClick = {
                            if (isCheckInEligible) {
                                EventViewModel.checkIntoEvent(eventId, userId)
                                attendanceState = AttendanceStatus.CHECKED_IN
                                event?.let { startLiveLocationService(context, it) }
                            } else {
                                println("reserving with eventId: ${event?.eventID}, userId: $userId")
                                EventViewModel.reserveEvent(eventId, userId)
                                attendanceState = AttendanceStatus.RESERVED
                                participantCount += 1
                            }
                        },
                        modifier = Modifier,
                        isEnabled = (attendanceState == AttendanceStatus.NOT_RESERVED && !isEventFull) || isCheckInEligible,
                    )
                } else if (attendanceState == AttendanceStatus.CHECKED_OUT) {
                    Text(
                        text = "You have already checked out of this event.",
                        fontFamily = interDefault,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 36.dp)
                    )
                } else {
                    OutlinedButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(horizontal = 36.dp)
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(32.dp)
                            ),
                        onClick = {
                            if (attendanceState == AttendanceStatus.CHECKED_IN) {
                                event?.let { stopLiveLocationService(context) }
                                EventViewModel.checkOutOfEvent(eventId, userId)
                                navController.navigate(Routes.EVENT_CHECKOUT + "/${event?.host ?: ""}")
                            } else {
                                EventViewModel.leaveEvent(eventId, userId)
                                attendanceState = AttendanceStatus.NOT_RESERVED
                                participantCount -= 1
                            }
                        }
                    ) {
                        Text(
                            text = if (attendanceState == AttendanceStatus.CHECKED_IN) "Check out" else "Leave",
                            color = MaterialTheme.colorScheme.error,
                            fontFamily = interLight,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}
