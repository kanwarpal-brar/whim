package com.example.myapplication.view

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.ui.components.FriendRow
import com.example.myapplication.ui.theme.interSemiBold
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.painterResource
import com.example.myapplication.navigation.Routes
import kotlinx.coroutines.launch
import com.example.myapplication.R

@Composable
fun FriendsScreen(userViewModel: UserViewModel = viewModel(), navController: NavController) {
    val (selectedTabIndex, setSelectedTabIndex) = remember { mutableIntStateOf(0) }
    val friendsListState by userViewModel.friends.observeAsState(emptyList())
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 24.dp)
        ) {
            Text(
                text = "Friends",
                style = TextStyle(
                    fontFamily = interSemiBold,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.W600,
                    lineHeight = 24.sp,
                    color = Color(0xFF1D1D1F),
                    textAlign = TextAlign.Left
                ),
            )
            IconButton(
                onClick = { navController.navigate(Routes.ADD_FRIENDS) },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.plus),
                    contentDescription = "Add Friend",
                    modifier = Modifier.size(24.dp),
                    tint = Color(0xFF000000)
                )
            }
        }

        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.White,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { setSelectedTabIndex(0) }
            ) {
                Text(
                    text = "All",
                    style = TextStyle(
                        fontFamily = interSemiBold,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W600,
                        lineHeight = 16.sp,
                        color = Color(0xFF4F4F4F),
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { setSelectedTabIndex(1) }
            ) {
                Text(
                    text = "Pending",
                    style = TextStyle(
                        fontFamily = interSemiBold,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W600,
                        lineHeight = 16.sp,
                        color = Color(0xFF4F4F4F),
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }

        when (selectedTabIndex) {
            0 -> {
                if (friendsListState?.none { it.friendStatus == "Confirmed" } == true) {
                    Text(
                        text = "Add a friend to see what events your mutuals are attending!",
                        style = TextStyle(
                            fontFamily = interSemiBold,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W400,
                            lineHeight = 24.sp,
                            color = Color(0xFF4F4F4F),
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                } else {
                    friendsListState?.filter { it.friendStatus == "Confirmed" }?.forEach { friend ->
                        FriendRow(
                            friend = friend,
                            onAddClick = {
                                coroutineScope.launch {
                                    userViewModel.acceptFriend(friend.userName)
                                }
                            },
                            onRemoveClick = {
                                coroutineScope.launch {
                                    userViewModel.removeFriend(friend.userName)
                                }
                            }
                        )
                    }
                }
            }
            1 -> {
                if (friendsListState?.none { it.friendStatus == "Pending Request" } == true) {
                    Text(
                        text = "No pending friend requests!",
                        style = TextStyle(
                            fontFamily = interSemiBold,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W400,
                            lineHeight = 24.sp,
                            color = Color(0xFF4F4F4F),
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    )
                } else {
                    friendsListState?.filter { it.friendStatus == "Pending Request" }?.forEach { friend ->
                        FriendRow(
                            friend = friend,
                            onAddClick = {
                                coroutineScope.launch {
                                    userViewModel.acceptFriend(friend.userName)
                                }
                            },
                            onRemoveClick = {
                                coroutineScope.launch {
                                    userViewModel.removeFriend(friend.userName)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
