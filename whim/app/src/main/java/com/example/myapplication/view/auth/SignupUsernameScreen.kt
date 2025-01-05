package com.example.myapplication.view.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
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
import com.example.myapplication.ui.theme.dark_grey
import com.example.myapplication.ui.theme.header_black
import com.example.myapplication.ui.theme.interDefault
import com.example.myapplication.ui.theme.interSemiBold
import com.example.myapplication.view.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun SignupUsernameScreen(navController: NavController, userViewModel: UserViewModel, email: String) {
    var enteredUsername by remember { mutableStateOf("") }
    var usernameLenError by remember { mutableStateOf(false) }
    var duplicateUsernameError by remember { mutableStateOf(false) }

    val isFormFilled by remember {
        derivedStateOf {
            enteredUsername.isNotEmpty() && enteredUsername.length >= 3 && enteredUsername.length <= 20 && !duplicateUsernameError
        }
    }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
        ) {
            Row(
                modifier = Modifier.align(Alignment.CenterStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.navigate(Routes.SIGNUP) },
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.backarrow),
                        contentDescription = "Back",
                        tint = header_black
                    )
                }
            }

            Icon(
                painter = painterResource(id = R.drawable.landplot),
                contentDescription = "Whim Logo",
                tint = Color.Unspecified,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Text(
            text = "Create your username",
            fontFamily = interSemiBold,
            fontSize = 20.sp,
            color = header_black,
            modifier = Modifier.padding(start = 48.dp, top = 48.dp, bottom = 24.dp, end = 48.dp)
        )
        Text(
            text = "Pick a name to use on Whim.",
            fontFamily = interDefault,
            fontSize = 12.sp,
            color = header_black,
            modifier = Modifier.padding(bottom = 48.dp)
        )


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .padding(bottom = 48.dp)
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
                    modifier = Modifier
                        .fillMaxWidth()
                )

                if (usernameLenError || duplicateUsernameError) {
                    var errorText = ""

                    if (usernameLenError) {
                        errorText = "Username must be between 3 and 20 characters."
                    } else {
                        errorText = "That username is already taken."
                    }

                    Text(
                        text = errorText,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .padding(top = 4.dp)
                    )
                }
            }

            RoundedGradientButton(
                text = "Continue",
                onClick = {
                    coroutineScope.launch {
                        val takeUserNameResult = userViewModel.takeUserName(enteredUsername)

                        if (takeUserNameResult.isSuccess) {
                            navController.navigate("${Routes.SIGNUP_PASSWORD}/$email/$enteredUsername")
                        } else {
                            duplicateUsernameError = true
                        }
                    }
                },
                isEnabled = isFormFilled,
                modifier = Modifier.fillMaxWidth()
            )

        }
    }
}