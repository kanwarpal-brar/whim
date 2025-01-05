package com.example.myapplication.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun PastHostedEventsScreen(EventViewModel: EventViewModel, userViewModel: UserViewModel, navController: NavController, innerPadding: PaddingValues) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
       HostEventsScreen(
           EventViewModel = EventViewModel,
           userViewModel = userViewModel,
           navController = navController,
           innerPadding = innerPadding,
           showPastEvents = true,
       )
    }
}