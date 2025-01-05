package com.example.myapplication.view.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
fun SignupPasswordScreen(navController: NavController, userViewModel: UserViewModel, email: String, username: String) {
    var enteredPassword by remember { mutableStateOf("") }
    var enteredConfirmation by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var unknownError by remember { mutableStateOf(false) }

    val passwordError by remember {
        derivedStateOf {
            (enteredPassword.isNotEmpty() && enteredPassword.length < 6) || (enteredConfirmation.isNotEmpty() && enteredPassword != enteredConfirmation)
        }
    }

    val isFormFilled by remember {
        derivedStateOf {
            enteredPassword.isNotEmpty() && enteredConfirmation.isNotEmpty() && !passwordError
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
                    onClick = { navController.navigate( "${Routes.SIGNUP_USERNAME}/$email") },
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
            text = "Set a password",
            fontFamily = interSemiBold,
            fontSize = 20.sp,
            color = header_black,
            modifier = Modifier.padding(start = 48.dp, top = 48.dp, bottom = 24.dp, end = 48.dp)
        )
        Text(
            text = "Set a strong password for your account.",
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
                    .padding(bottom = 48.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                OutlinedTextField(
                    value = enteredPassword,
                    onValueChange = {
                        enteredPassword = it
                        unknownError = false
                    },
                    isError = passwordError || unknownError,
                    label = {
                        Text(
                            text = "Password",
                            fontFamily = interDefault,
                            fontSize = 12.sp,
                            color = dark_grey
                        )
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon = if (passwordVisible)
                            Icon(
                                painter = painterResource(id = R.drawable.eye_off),
                                contentDescription = "Hide Password",
                                tint = Color.Unspecified
                            )
                            else Icon(
                                painter = painterResource(id = R.drawable.eye),
                                contentDescription = "Show Password",
                                tint = Color.Unspecified
                            )

                        IconButton(onClick = {passwordVisible = !passwordVisible}, content = { icon })
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                )

                OutlinedTextField(
                    value = enteredConfirmation,
                    onValueChange = {
                        enteredConfirmation = it
                        unknownError = false
                    },
                    isError = passwordError || unknownError,
                    label = {
                        Text(
                            text = "Confirm Password",
                            fontFamily = interDefault,
                            fontSize = 12.sp,
                            color = dark_grey
                        )
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon = if (passwordVisible)
                            Icon(
                                painter = painterResource(id = R.drawable.eye_off),
                                contentDescription = "Hide Password",
                                tint = Color.Unspecified
                            )
                        else Icon(
                            painter = painterResource(id = R.drawable.eye),
                            contentDescription = "Show Password",
                            tint = Color.Unspecified
                        )

                        IconButton(onClick = {passwordVisible = !passwordVisible}, content = { icon })
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                )

                if (passwordError || unknownError) {
                    var errorText = ""
                    if (enteredPassword.length < 6) {
                        errorText = "Password must be at least 6 characters."
                    } else if (unknownError) {
                        errorText = "An unknown error occurred, please try again."
                    } else {
                        errorText = "Passwords do not match."
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
                text ="Continue",
                onClick = {
                    coroutineScope.launch {
                        val createRes = userViewModel.createUser(email = email, password = enteredPassword, userName = username, firstName = "", lastName = "")
                        if (createRes.isSuccess) {
                            navController.navigate(Routes.SIGNUP_NAME)
                        }
                    }
                },
                isEnabled = isFormFilled,
                modifier = Modifier.fillMaxWidth()
            )

        }
    }
}