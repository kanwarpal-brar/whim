package com.example.myapplication.view

import Calendar
import Compass
import People
import Plus
import Profile
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.myapplication.R
import com.example.myapplication.navigation.NavBarRouteLabels
import com.example.myapplication.navigation.Routes
import com.example.myapplication.ui.theme.DarkGreyBrush
import com.example.myapplication.ui.theme.PurpleMagentaGradient
import com.example.myapplication.ui.theme.dark_grey_button_text
import com.example.myapplication.ui.theme.interSemiBold

@Composable
fun NavBar(navController: NavController) {
    println("route is")
    println(navController.currentDestination?.route)

    Box (modifier = Modifier
        .drawBehind {
            drawLine(
                Color.LightGray,
                Offset(x = size.width, y = 10f),
                Offset(x = 10f, y = 10f),
                strokeWidth = density
            )
        }
        .padding(top = 16.dp, bottom = 16.dp)
    ) {
        BottomAppBar (
            containerColor = Color.Transparent,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                NavIconButton(drawable = R.drawable.star, label = NavBarRouteLabels.HOST, route = "${Routes.HOSTED_EVENTS}/false", navController = navController)
                NavIconButton(drawable = R.drawable.checked_calendar, label = NavBarRouteLabels.JOINED, route = "${Routes.JOINED_EVENTS}/false", navController = navController)
                NavIconButton(drawable = R.drawable.compass, label = NavBarRouteLabels.EXPLORE, route = Routes.MAP, navController = navController)
                NavIconButton(drawable = R.drawable.two_users, label = NavBarRouteLabels.FRIENDS, route = Routes.FRIENDS, navController = navController)
                NavIconButton(drawable = R.drawable.user, label = NavBarRouteLabels.YOU, route = Routes.PROFILE, navController = navController)
            }
        }
    }
}

@Composable
private fun NavIconButton(drawable: Int, label: String, route: String, navController: NavController) {
    var currentRoute by remember {
        mutableStateOf("")
    }
    currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route.toString()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        IconButton(
            onClick = { navController.navigate(route) },
            modifier = Modifier
                 .size(28.dp)
                .padding(bottom = 6.dp)
        ) {
            Icon(
                painter = painterResource(id = drawable),
                contentDescription = "Icon",
                tint = if (currentRoute == route) Color.Unspecified else dark_grey_button_text,
            )
        }
        Text(
            text = label,
            fontFamily = interSemiBold,
            fontSize = 8.sp,
            style = if (currentRoute == route) TextStyle(brush = PurpleMagentaGradient) else TextStyle(brush = DarkGreyBrush)
        )
    }
}
