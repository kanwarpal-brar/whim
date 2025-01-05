package com.example.myapplication.ui.components

import GlideImage
import com.example.myapplication.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.events.Event
import com.example.myapplication.navigation.Routes
import com.example.myapplication.ui.theme.dark_grey
import com.example.myapplication.ui.theme.header_black
import com.example.myapplication.ui.theme.interDefault
import com.example.myapplication.ui.theme.interLight
import com.example.myapplication.ui.theme.interMedium
import com.example.myapplication.ui.theme.interSemiBold
import com.example.myapplication.ui.theme.light_grey
import com.example.myapplication.view.UserViewModel
import java.time.format.DateTimeFormatter


@Composable
fun EventsSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        value = query,
        onValueChange = {
            onQueryChange(it)
        },
        placeholder = {
            Text(
                text = "Search",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 14.sp,
                fontFamily = interDefault,
            )
        },
        leadingIcon = {
            Image(
                painter = painterResource(id = R.drawable.search),
                contentDescription = null,
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = {
                    onQueryChange("")
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear Search",
                        tint = Color.Gray
                    )
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                keyboardController?.hide()
            }
        ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = light_grey,
            unfocusedContainerColor = light_grey,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .background(Color.White, RoundedCornerShape(16.dp))
    )
}

@Composable
fun EventListItem(
    dateTime: String,
    title: String,
    location: String,
    imageUri: String?,
    friendsJoined: Int = 0,
    rating: Double = 0.0,
    participants: Int = 0,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .padding(bottom = 12.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(12.dp)
            .clickable(onClick = onClick),
    ) {
        Row {
            if (imageUri != null) {
                GlideImage(
                    imageUri = imageUri,
                    contentDescription = "Event Image",
                    modifier = Modifier
                        .weight(1f)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.music),
                    contentDescription = null,
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(shape = RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(2f)
            ) {
                Text(
                    text = dateTime,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    fontFamily = interDefault,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = title,
                    fontSize = 18.sp,
                    color = header_black,
                    fontFamily = interSemiBold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = location,
                    fontSize = 12.sp,
                    color = dark_grey,
                    fontFamily = interDefault,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                if (friendsJoined > 0) {
                    val text = if (friendsJoined == 1) "1 friend is going!" else "$friendsJoined friends are going!"
                    Text(
                        text = text,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        fontFamily = interDefault,
                    )
                }

                if (rating > 0) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = rating.toString(),
                            fontSize = 10.sp,
                            color = dark_grey,
                            fontFamily = interDefault,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.outlined_star),
                            contentDescription = "Rating",
                            tint = dark_grey,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                if (participants > 0) {
                    Text(
                        text = "$participants Participant(s)",
                        fontFamily = interDefault,
                        fontSize = 10.sp,
                        color = dark_grey,
                    )
                }
            }

        }
    }
}

@Composable
fun EventList(
    events: List<Event>,
    navController: NavController,
    isLoading: Boolean = false,
    userViewModel: UserViewModel,
    eventIdsJoinedByFriends: List<String> = emptyList(),
    includeRatings: Boolean = false,
    includeParticipants: Boolean = false,
    archivePage: Boolean = false
) {
    val outputFormatter = DateTimeFormatter.ofPattern("EE, MMM d • h:mm a")
    var selectedEvent by remember { mutableStateOf<Event?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        if (events.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if(isLoading) "Loading ..." else "Nothing happening!",
                        fontSize = 24.sp,
                        color = dark_grey,
                        fontFamily = interLight,
                        fontWeight = FontWeight(300)
                    )
                    if (!isLoading) {
                        Text(
                            text = "Check back later for events!",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp,
                            fontFamily = interLight,
                            fontWeight = FontWeight(300)
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(24.dp)
            ) {
                items(events) { event ->
                    val formattedStartDate = event.startTime.format(outputFormatter)
                    val formattedEndTime = event.endTime.format(DateTimeFormatter.ofPattern("h:mm a"))

                    // Check if the event is joined by a friend
                    val friendsJoined = eventIdsJoinedByFriends.count { it == event.eventID }

                    // Fetch host object and get their rating
                    var rating by remember { mutableStateOf(0.0) }
                    LaunchedEffect(event.host, includeRatings) {
                        if (includeRatings) {
                            val user = userViewModel.getUser(event.host).getOrNull()
                            if (user != null) {
                                rating = user.hostRating
                            }
                        }
                    }

                    EventListItem(
                        dateTime ="$formattedStartDate - $formattedEndTime",
                        title = event.title,
                        location = "",
                        imageUri = event.imageUri.toString(),
                        friendsJoined = friendsJoined,
                        rating = rating,
                        participants = if (includeParticipants) event.attendeeCount else 0,
                    ) {
                        selectedEvent = event
                    }
                }
            }

            if (!archivePage) {
                selectedEvent?.let {
                    navController.navigate(Routes.EVENT_DETAILS + "/${selectedEvent!!.eventID}")
                }
            }
        }
    }
}

