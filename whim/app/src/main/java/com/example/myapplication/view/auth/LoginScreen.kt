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
import androidx.compose.material3.TextButton
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController, userViewModel: UserViewModel) {
    var enteredEmail by remember { mutableStateOf("") }
    var enteredPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var formError by remember { mutableStateOf(false) }

    val isFormFilled by remember {
        derivedStateOf {
            enteredPassword.isNotEmpty() && enteredEmail.isNotEmpty()
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
                    onClick = { navController.navigate(Routes.START) },
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

            Row(
                modifier = Modifier.align(Alignment.CenterEnd),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { navController.navigate(Routes.SIGNUP) },
                ) {
                    Text(
                        text = "Sign up",
                        fontFamily = interSemiBold,
                        fontSize = 16.sp,
                        color = header_black,
                    )
                }
            }
        }

        Text(
            text = "Enter your login information",
            fontFamily = interSemiBold,
            fontSize = 20.sp,
            color = header_black,
            modifier = Modifier.padding(48.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {


            OutlinedTextField(
                value = enteredEmail,
                onValueChange = {
                    enteredEmail = it
                },
                label = {
                    Text(
                        text = "Email",
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
                    .padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = enteredPassword,
                onValueChange = {
                    enteredPassword = it
                },
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
                    errorBorderColor = MaterialTheme.colorScheme.error,
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (formError) {
                Text(
                    text = "Invalid username or password",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(top = 4.dp)
                )
            }

            RoundedGradientButton(
                text = "Log in",
                onClick = {
                    coroutineScope.launch {
                        val res = userViewModel.signIn(enteredEmail, enteredPassword)
                        delay(100) // Race Condition somewhere, not enough time to fix.
                        if (res.isSuccess) {
                            // Check for email verification
                            userViewModel.isEmailVerified()
                            if (userViewModel.userLiveData.value?.isEmailVerified == true) {
                                navController.navigate(Routes.MAP)
                            } else {
                                navController.navigate(Routes.VERIFY_EMAIL)
                            }

                        } else {
                            formError = true
                        }
                    }
                },
                isEnabled = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp)
            )

        }
    }
}