package com.example.myapplication.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.navigation.Routes
import com.example.myapplication.ui.components.RoundedGradientButton
import com.example.myapplication.ui.theme.header_black
import com.example.myapplication.ui.theme.interDefault
import kotlinx.coroutines.launch

@Composable
fun EventCheckoutScreen(navController: NavController, userViewModel: UserViewModel, hostId: String) {
    val coroutineScope = rememberCoroutineScope()
    var rating by remember { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.popper),
                contentDescription = "",
                tint = Color.Unspecified
            )
            Text(
                text = "You've successfully checked out.",
                fontFamily = interDefault,
                fontSize = 16.sp,
                color = header_black,
                modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
            )
            Text(
                text = "Leave a rating for the host?.",
                fontFamily = interDefault,
                fontSize = 16.sp,
                color = header_black,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                for (i in 1..5) {
                    Icon(
                        painter = painterResource(id = if (i <= rating) R.drawable.filled_star else R.drawable.outlined_star),
                        contentDescription = "Rating Input",
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .clickable { rating = i }
                    )
                }
            }
        }

        RoundedGradientButton(
            text = "Submit",
            onClick = {
                    coroutineScope.launch {
                            userViewModel.submitHostRating(userID = hostId, rating = rating.toDouble())
                            navController.navigate(Routes.MAP)
                        }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
    }
}