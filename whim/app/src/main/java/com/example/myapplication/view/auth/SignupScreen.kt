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
import java.util.regex.Pattern

@Composable
fun SignupScreen(navController: NavController, userViewModel: UserViewModel) {
    var enteredEmail by remember { mutableStateOf("") }
    var emailPatternError by remember { mutableStateOf(false) }
    var duplicateEmailError by remember { mutableStateOf(false) }

    val emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z.-]+\\.(ca|com)$"

    val isFormFilled by remember {
        derivedStateOf {
            enteredEmail.isNotEmpty() && Pattern.matches(emailPattern, enteredEmail) && !duplicateEmailError
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
                    onClick = { navController.navigate(Routes.LOGIN) },
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
                    onClick = { navController.navigate(Routes.LOGIN) },
                ) {
                    Text(
                        text = "Log in",
                        fontFamily = interSemiBold,
                        fontSize = 16.sp,
                        color = header_black,
                    )
                }
            }
        }

        Text(
            text = "Welcome to Whim",
            fontFamily = interSemiBold,
            fontSize = 20.sp,
            color = header_black,
            modifier = Modifier.padding(start = 48.dp, top = 48.dp, bottom = 24.dp, end = 48.dp)
        )
        Text(
            text = "Create an account to get started.",
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
                    value = enteredEmail,
                    onValueChange = {
                        enteredEmail = it
                        emailPatternError = !Pattern.matches(emailPattern, it)
                        duplicateEmailError = false
                    },
                    isError = emailPatternError || duplicateEmailError,
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
                )

                if (emailPatternError || duplicateEmailError) {
                    var errorText = ""
                    if (emailPatternError) {
                        errorText = "Please provide a valid email."
                    } else {
                        errorText = "An account with this email already exists."
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

            Text(
                text = "By continuing you agree to our user agreement.",
                fontFamily = interDefault,
                fontSize = 12.sp,
                color = dark_grey,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            RoundedGradientButton(
                text ="Continue",
                onClick = {
                    coroutineScope.launch {
                        val isEmailAvailable = userViewModel.isEmailAvailable(enteredEmail)
                        if (isEmailAvailable.isSuccess) {
                            navController.navigate("${Routes.SIGNUP_USERNAME}/$enteredEmail")
                        } else {
                            duplicateEmailError = true
                        }
                    }
                },
                isEnabled = isFormFilled,
                modifier = Modifier.fillMaxWidth()
            )

        }
    }
}