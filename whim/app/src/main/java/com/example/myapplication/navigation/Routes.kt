package com.example.myapplication.navigation

object Routes {
    const val START = "start"
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val VERIFY_EMAIL = "verify_email"
    const val SIGNUP_NAME = "signup_name"
    const val SIGNUP_USERNAME = "signup_username/{email}"
    const val SIGNUP_PASSWORD = "signup_password"
    const val CREATE_EVENT = "create_event"
    const val EVENTS = "events"
    const val JOINED_EVENTS = "joined_events"
    const val HOSTED_EVENTS = "hosted_events"
    const val EVENT_DETAILS = "event_details"
    const val EVENT_CHECKOUT = "event_checkout"
    const val MAP = "map"
    const val FRIENDS = "friends"
    const val ADD_FRIENDS = "add_friends"
    const val PROFILE = "profile"
    const val EDIT_PROFILE = "edit_profile"
    const val PAST_JOINED_EVENTS = "past_joined_events"
    const val PAST_HOSTED_EVENTS = "past_hosted_events"
    const val EVENT_PARTICIPANTS = "event_participants"
}

val USER_AUTH_ROUTES = listOf(
    Routes.START,
    Routes.LOGIN,
    Routes.SIGNUP,
    Routes.VERIFY_EMAIL,
    Routes.SIGNUP_NAME,
    Routes.SIGNUP_USERNAME,
    Routes.SIGNUP_PASSWORD,
    Routes.EDIT_PROFILE,
    Routes.EVENT_CHECKOUT,
    Routes.ADD_FRIENDS
)


object NavBarRouteLabels {
    const val HOST = "Host"
    const val JOINED = "Joined"
    const val EXPLORE = "Explore"
    const val FRIENDS = "Friends"
    const val YOU = "You"
}
