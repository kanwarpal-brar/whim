package com.example.myapplication.view

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.myapplication.ui.theme.dark_grey
import com.example.myapplication.ui.theme.header_black
import com.example.myapplication.ui.theme.interDefault
import com.example.myapplication.ui.theme.interSemiBold
import kotlinx.coroutines.launch

@Composable
fun AddFriendsScreen(userViewModel: UserViewModel, navController: NavController) {
    var enteredUsername by remember { mutableStateOf("") }
    var usernameLenError by remember { mutableStateOf(false) }
    var duplicateUsernameError by remember { mutableStateOf(false) }
    var requestResult by remember { mutableStateOf<Result<Unit>?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                SnackbarHost(
                    hostState = snackbarHostState,
                    snackbar = { snackbarData ->
                        val backgroundColor = if (requestResult?.isSuccess == true) {
                            Color(0xFF4CAF50)
                        } else {
                            Color(0xFFF44336)
                        }
                        Snackbar(
                            snackbarData = snackbarData,
                            containerColor = backgroundColor,
                            contentColor = Color.White
                        )
                    }
                )
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar with back button
            Box(
                modifier = Modifier.fillMaxWidth().padding(24.dp)
            ) {
                IconButton(
                    onClick = { navController.navigate(Routes.FRIENDS) },
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.backarrow),
                        contentDescription = "Back",
                        tint = header_black
                    )
                }
            }

            // Title and description
            Text(
                text = "Add by Username",
                fontFamily = interSemiBold,
                fontSize = 20.sp,
                color = header_black,
                modifier = Modifier.padding(start = 48.dp, top = 48.dp, bottom = 24.dp, end = 48.dp)
            )
            Text(
                text = "Enter your friend's username to add them.",
                fontFamily = interDefault,
                fontSize = 12.sp,
                color = header_black,
                modifier = Modifier.padding(bottom = 48.dp)
            )

            // Username input field and error message
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = enteredUsername,
                    onValueChange = {
                        enteredUsername = it
                        usernameLenError = enteredUsername.length < 3 || enteredUsername.length > 20
                        duplicateUsernameError = false
                    },
                    isError = usernameLenError || duplicateUsernameError,
                    label = {
                        Text(
                            text = "Username",
                            fontFamily = interDefault,
                            fontSize = 12.sp,
                            color = dark_grey
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
                        errorBorderColor = MaterialTheme.colorScheme.error,
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                if (usernameLenError || duplicateUsernameError) {
                    val errorText = if (usernameLenError) {
                        "Username must be between 3 and 20 characters."
                    } else {
                        "That username is already taken."
                    }

                    Text(
                        text = errorText,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                RoundedGradientButton(
                    text = "Send Friend Request",
                    onClick = {
                        coroutineScope.launch {
                            val result = userViewModel.requestFriend(enteredUsername)
                            requestResult = result
                            Log.d("Result", result.toString())
                            val message = if (result.isSuccess) {
                                "Friend request sent successfully."
                            } else {
                                "Invalid username, failed to send friend request."
                            }
                            snackbarHostState.showSnackbar(message)
                            Log.d("Snackbar", "Entered coroutine to launch snackbar")
                            requestResult = null
                        }
                    },
                )
            }
        }
    }
}
