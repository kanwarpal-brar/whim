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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.events.Event
import com.example.myapplication.navigation.Routes
import com.example.myapplication.ui.components.EventList
import com.example.myapplication.ui.components.EventsSearchBar
import com.example.myapplication.ui.theme.header_black
import com.example.myapplication.ui.theme.interSemiBold
import com.example.myapplication.ui.theme.page_background_grey


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HostEventsScreen(EventViewModel: EventViewModel, userViewModel: UserViewModel,  navController: NavController, innerPadding: PaddingValues, showPastEvents: Boolean) {
    var eventListState by remember { mutableStateOf<List<Event>>(emptyList()) }
    var filteredEventListState by remember { mutableStateOf<List<Event>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val userId = userViewModel.userLiveData.value?.uid ?: ""
        val queryRes = EventViewModel.getEventsHosted(userId = userId, active = !showPastEvents)
        if (queryRes.isSuccess) {
            eventListState = queryRes.getOrNull() ?: emptyList()
            filteredEventListState = eventListState
            isLoading = false
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
                if (!showPastEvents) {
                    Text(
                        text = "Host",
                        fontSize = 24.sp,
                        color = header_black,
                        fontFamily = interSemiBold
                    )
                    IconButton(
                        onClick = { navController.navigate(Routes.CREATE_EVENT) },
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.plus),
                            contentDescription = "Create Event",
                            tint = header_black
                        )
                    }
                } else {
                    IconButton(
                        onClick = {
                            navController.navigate(Routes.PROFILE)
                        },
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.backarrow),
                            contentDescription = "Back",
                            tint = Color.Unspecified
                        )
                    }
                }
            }

            EventsSearchBar(
                query = searchQuery,
                onQueryChange = {
                    searchQuery = it
                    if (it.isEmpty()) {
                        filteredEventListState = eventListState
                    } else {
                        filteredEventListState = filteredEventListState.filter { event ->
                            event.title.contains(it, ignoreCase = true)
                        }
                    }
                },
            )
            EventList(
                events = filteredEventListState,
                navController = navController,
                isLoading = isLoading,
                userViewModel = userViewModel,
                includeParticipants = true,
                archivePage = showPastEvents
            )
        }
    }
}
