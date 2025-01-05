package com.example.myapplication.view

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.authentication.FriendProfile
import com.example.myapplication.events.Event
import com.example.myapplication.events.EventRepository
import com.example.myapplication.navigation.Routes
import com.example.myapplication.ui.components.EventList
import com.example.myapplication.ui.components.EventListItem
import com.example.myapplication.ui.components.EventsSearchBar
import com.example.myapplication.ui.components.RoundedGradientButton
import com.example.myapplication.ui.theme.PurpleMagentaGradient
import com.example.myapplication.ui.theme.dark_grey
import com.example.myapplication.ui.theme.header_black
import com.example.myapplication.ui.theme.interLight
import com.example.myapplication.ui.theme.interSemiBold
import com.example.myapplication.ui.theme.page_background_grey
import com.example.myapplication.util.location
import java.time.format.DateTimeFormatter

const val WATERLOO_LAT = 43.4643
const val WATERLOO_LONG = -80.5204

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EventsScreen(EventViewModel: EventViewModel, userViewModel: UserViewModel, navController: NavController, innerPadding: PaddingValues) {
    var eventListState by remember { mutableStateOf<List<Event>>(emptyList()) }
    var filteredEventListState by remember { mutableStateOf<List<Event>>(emptyList()) }
    var userFriends by remember { mutableStateOf<List<FriendProfile>>(emptyList()) }
    var eventIdsJoinedByFriends by remember { mutableStateOf<List<String>>(emptyList()) }

    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var shouldFetchFriendsJoinedEvents by remember { mutableStateOf(false) }

    val eventObserver = Observer<List<Event>> {
        eventListState = it
        if (searchQuery.isEmpty()) {
            filteredEventListState = it
        } else {
            filteredEventListState = it.filter { event ->
                event.title.contains(searchQuery, ignoreCase = true)
            }
        }
        isLoading = false
    }

    EventViewModel.events.observeForever(eventObserver)
    userViewModel.friends.observeForever {
        if (it != null) {
            userFriends = it
            shouldFetchFriendsJoinedEvents = true
        }
    }

    // Fetch events from repo
    LaunchedEffect(Unit) {
        // TODO: why is this WATERLOO_LAT and WATERLOO_LONG?
        EventViewModel.fetchEvents(location(WATERLOO_LAT, WATERLOO_LONG), 10000.0)
    }

    LaunchedEffect(shouldFetchFriendsJoinedEvents) {
        if (shouldFetchFriendsJoinedEvents) {
            val joinedEventIds = fetchFriendsJoinedEvents(userFriends, EventViewModel)
            eventIdsJoinedByFriends = joinedEventIds
            shouldFetchFriendsJoinedEvents = false
        }
    }



    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = Modifier
            .padding(innerPadding)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                })
            }
            .background(page_background_grey)
    ) {

        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)

            ) {
                Text(
                    text = "Events",
                    fontSize = 24.sp,
                    color = header_black,
                    fontFamily = interSemiBold
                )
                IconButton(
                    onClick = { navController.navigate(Routes.MAP) },
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.map),
                        contentDescription = "Back",
                        tint = header_black
                    )
                }
            }

            EventsSearchBar(
                query = searchQuery,
                onQueryChange = {
                    searchQuery = it
                },
            )
            EventList(
                events = filteredEventListState,
                navController = navController,
                isLoading = isLoading,
                userViewModel = userViewModel,
                eventIdsJoinedByFriends = eventIdsJoinedByFriends,
                includeRatings = true
            )
        }
    }
}

private suspend fun fetchFriendsJoinedEvents(
    friends: List<FriendProfile>,
    EventViewModel: EventViewModel
): List<String> {
    val joinedEventIds = mutableListOf<String>()
    friends.forEach { friend ->
        val result = EventViewModel.getEventsJoined(friend.userID, false)
        println("friend get events joined: $result ${friend.userID}")
        result.onSuccess { events ->
            joinedEventIds.addAll(events.map { it.eventID })
        }
    }
    return joinedEventIds
}