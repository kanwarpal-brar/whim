package com.example.myapplication.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.navigation.Routes
import com.example.myapplication.ui.theme.dark_grey
import com.example.myapplication.ui.theme.header_black
import com.example.myapplication.ui.theme.interSemiBold
import com.example.myapplication.ui.theme.light_grey
import com.example.myapplication.ui.theme.link_text_purple
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(navController: NavController, userViewModel: UserViewModel) {

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.music),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .aspectRatio(1f)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Text(
                text = (userViewModel.userProfile.value?.firstName + " " + userViewModel.userProfile.value?.lastName),
                color = header_black,
                fontFamily = interSemiBold,
                fontSize = 24.sp,
                modifier = Modifier.padding(top = 36.dp)
            )
            Text(
                text = userViewModel.userProfile.value?.userName ?: "",
                color = MaterialTheme.colorScheme.secondary,
                fontFamily = interSemiBold,
                fontSize = 16.sp,
            )
        }
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate(Routes.EDIT_PROFILE)
                    },
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.pencil),
                    contentDescription = "Edit",
                    tint = Color.Unspecified
                )
                Text(
                    text = "Edit Account",
                    fontFamily = interSemiBold,
                    fontSize = 16.sp,
                    color = header_black,
                )
            }
            Divider(
                thickness = 1.dp,
                modifier = Modifier.background(color = light_grey)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(Routes.PAST_JOINED_EVENTS) },
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.calendar_clock),
                    contentDescription = "Clock Calendar",
                    tint = Color.Unspecified
                )
                Text(
                    text = "Past Events",
                    fontFamily = interSemiBold,
                    fontSize = 16.sp,
                    color = header_black,
                )
            }
            Divider(
                thickness = 1.dp,
                modifier = Modifier.background(color = light_grey)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(Routes.PAST_HOSTED_EVENTS) },
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.single_star),
                    contentDescription = "Star",
                    tint = Color.Unspecified
                )
                Text(
                    text = "Hosted Events",
                    fontFamily = interSemiBold,
                    fontSize = 16.sp,
                    color = header_black,
                )
            }
            Divider(
                thickness = 1.dp,
                modifier = Modifier.background(color = light_grey)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        userViewModel.signOut()
                        navController.navigate(Routes.START)
                    },
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,

            ) {
                Icon(
                    painter = painterResource(id = R.drawable.logout),
                    contentDescription = "Log Out",
                    tint = Color.Unspecified
                )
                Text(
                    text = "Log Out",
                    fontFamily = interSemiBold,
                    fontSize = 16.sp,
                    color = header_black,
                )
            }

        }

    }

}