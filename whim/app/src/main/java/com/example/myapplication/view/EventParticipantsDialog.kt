package com.example.myapplication.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.myapplication.R
import com.example.myapplication.ui.theme.dark_grey_button_text
import com.example.myapplication.ui.theme.header_black
import com.example.myapplication.ui.theme.interDefault
import com.example.myapplication.ui.theme.interSemiBold
import com.example.myapplication.ui.theme.light_grey

@Composable
fun EventParticipantsDialog(
    onDismissRequest: () -> Unit,
    usernames: List<String>,
    emails: List<String>,
    attendeeCount: Int,
    attendeeLimit: Int
) {
//    var usernames by remember { mutableStateOf(emptyList<String>()) }

//    LaunchedEffect(Unit) {
//        val attendees = userViewModel.getAttendees(eventId).getOrNull()
//        usernames = attendees?.map { it.firstName + " " + it.lastName } ?: emptyList()
//    }
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            color = light_grey,
            modifier = Modifier.fillMaxSize()
            ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.backarrow),
                            contentDescription = "Back",
                            tint = Color.Unspecified
                        )
                    }
                    Text(
                        text = "$attendeeCount / $attendeeLimit Participants",
                        fontFamily = interSemiBold,
                        fontSize = 16.sp,
                        color = header_black,
                    )
                }

                Divider(thickness = 1.dp)

                LazyColumn {
                    items(usernames.zip(emails)) { (username, email) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = username,
                                fontFamily = interDefault,
                                fontSize = 16.sp,
                                color = dark_grey_button_text,
                                modifier = Modifier.padding(24.dp)
                            )
                            Text(
                                text = email,
                                fontFamily = interDefault,
                                fontSize = 16.sp,
                                color = dark_grey_button_text,
                                modifier = Modifier.padding(24.dp)
                            )

                        }
                    }
                }
            }
        }
    }
}