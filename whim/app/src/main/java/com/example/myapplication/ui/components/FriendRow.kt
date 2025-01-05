package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.authentication.FriendProfile
import com.example.myapplication.ui.theme.interSemiBold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close


@Composable
fun FriendRow(friend: FriendProfile, onAddClick: () -> Unit, onRemoveClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = Color(0xFF001A52),
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = friend.firstName.first().toString(),
                            style = TextStyle(
                                fontFamily = interSemiBold,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.W600,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
                Text(
                    text = "${friend.firstName} ${friend.lastName}",
                    style = TextStyle(
                        fontFamily = interSemiBold,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W400,
                        lineHeight = 16.sp,
                        color = Color(0xFF333333),
                        textAlign = TextAlign.Left
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }
        if(friend.friendStatus == "Pending Request") {
            IconButton(
                onClick = onAddClick,
                modifier = Modifier
                    .size(24.dp)
//                .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Add Friend",
                    tint = Color(0xFF000000)
                )
            }
            IconButton(
                onClick = onRemoveClick,
                modifier = Modifier
                    .size(24.dp)
//                .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove Friend",
                    tint = Color(0xFF000000)
                )
            }
        }
    }
}

