package com.example.myapplication.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.myapplication.view.CreateEvent.CreateEventScreen
import com.example.myapplication.view.EditProfileScreen
import com.example.myapplication.view.EventCheckoutScreen
import com.example.myapplication.view.EventDetailsScreen
import com.example.myapplication.view.EventViewModel
import com.example.myapplication.view.EventsScreen
import com.example.myapplication.view.FriendsScreen
import com.example.myapplication.view.AddFriendsScreen
import com.example.myapplication.view.HostEventsScreen
import com.example.myapplication.view.JoinedEventsScreen
import com.example.myapplication.view.MapScreen
import com.example.myapplication.view.PastHostedEventsScreen
import com.example.myapplication.view.PastJoinedEventsScreen
import com.example.myapplication.view.ProfileScreen
import com.example.myapplication.view.UserViewModel
import com.example.myapplication.view.auth.LoginScreen
import com.example.myapplication.view.auth.SignupNameScreen
import com.example.myapplication.view.auth.SignupPasswordScreen
import com.example.myapplication.view.auth.SignupScreen
import com.example.myapplication.view.auth.SignupUsernameScreen
import com.example.myapplication.view.auth.StartScreen
import com.example.myapplication.view.auth.VerifyEmailScreen

@Composable
fun Navigation(navController: NavHostController, userViewModel: UserViewModel, innerPadding: PaddingValues, EventViewModel: EventViewModel) {
    NavHost(navController = navController, startDestination = Routes.START) {
        composable(Routes.START) { StartScreen(navController) }
        composable(Routes.LOGIN) { LoginScreen(navController, userViewModel) }
        composable(Routes.SIGNUP) { SignupScreen(navController, userViewModel) }
        composable(Routes.SIGNUP_USERNAME + "/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            SignupUsernameScreen(navController, userViewModel, email)
        }
        composable(
            route = "${Routes.SIGNUP_PASSWORD}/{email}/{username}",
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("username") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val username = backStackEntry.arguments?.getString("username") ?: ""
            SignupPasswordScreen(navController = navController, userViewModel = userViewModel, email = email, username = username)
        }
        composable(Routes.SIGNUP_NAME) { SignupNameScreen(navController, userViewModel) }
        composable(Routes.VERIFY_EMAIL) { VerifyEmailScreen(navController, userViewModel) }

        composable(Routes.CREATE_EVENT) { CreateEventScreen(navController, EventViewModel, userViewModel) }
        composable(Routes.MAP) { MapScreen(EventViewModel = EventViewModel, navController = navController) }
        composable (Routes.EVENTS) { EventsScreen(EventViewModel = EventViewModel, userViewModel = userViewModel, navController = navController, innerPadding = innerPadding) }
        composable (route = "${Routes.EVENT_DETAILS}/{eventId}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            EventDetailsScreen(
                navController = navController,
                eventId = eventId,
                EventViewModel = EventViewModel,
                userViewModel = userViewModel
            )
        }
        composable(route = "${Routes.EVENT_PARTICIPANTS}/{eventId}/{attendeeCount}/{attendeeLimit}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            val attendeeCount = backStackEntry.arguments?.getString("attendeeCount")?.toInt() ?: 0
            val attendeeLimit = backStackEntry.arguments?.getString("attendeeLimit")?.toInt() ?: 0
//            EventParticipantsScreen(
//                navController = navController,
//                userViewModel = userViewModel,
//                eventId = eventId,
//                attendeeCount = attendeeCount,
//                attendeeLimit = attendeeLimit
//            )
        }

        composable(route = "${Routes.JOINED_EVENTS}/{showPastEvents}") { backStackEntry ->
            val showPastEvents = backStackEntry.arguments?.getBoolean("showPastEvents") ?: false
            JoinedEventsScreen(
                EventViewModel = EventViewModel,
                userViewModel = userViewModel,
                navController = navController,
                innerPadding = innerPadding,
                showPastEvents = showPastEvents
            )
        }
        composable(route = "${Routes.HOSTED_EVENTS}/{showPastEvents}") { backStackEntry ->
            val showPastEvents = backStackEntry.arguments?.getBoolean("showPastEvents") ?: false
            HostEventsScreen(
                EventViewModel = EventViewModel,
                userViewModel = userViewModel,
                navController = navController,
                innerPadding = innerPadding,
                showPastEvents = showPastEvents
            )
        }

        composable(route = "${Routes.EVENT_CHECKOUT}/{hostId}") { backStackEntry ->
           val hostId = backStackEntry.arguments?.getString("hostId") ?: ""
            EventCheckoutScreen(navController, userViewModel, hostId)
        }

        composable(Routes.FRIENDS) { FriendsScreen(userViewModel, navController) }
        composable(Routes.ADD_FRIENDS) { AddFriendsScreen(userViewModel, navController) }
        composable(Routes.PROFILE) { ProfileScreen(navController, userViewModel) }
        composable(Routes.EDIT_PROFILE) { EditProfileScreen(navController, userViewModel) }
        composable(Routes.PAST_JOINED_EVENTS)  { PastJoinedEventsScreen(
            EventViewModel = EventViewModel,
            userViewModel = userViewModel,
            navController = navController,
            innerPadding = innerPadding
        )}
        composable(Routes.PAST_HOSTED_EVENTS) { PastHostedEventsScreen(
            EventViewModel = EventViewModel,
            userViewModel = userViewModel,
            navController = navController,
            innerPadding = innerPadding
        )}
    }
}
