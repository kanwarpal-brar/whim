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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.navigation.Routes
import com.example.myapplication.ui.components.RoundedGradientButton
import com.example.myapplication.ui.theme.dark_grey
import com.example.myapplication.ui.theme.grey_text
import com.example.myapplication.ui.theme.header_black
import com.example.myapplication.ui.theme.interDefault
import com.example.myapplication.ui.theme.interSemiBold
import com.example.myapplication.ui.theme.link_text_purple
import com.example.myapplication.view.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@Composable
fun ResendButton(navController: NavController, userViewModel: UserViewModel) {
    var resendButtonEnabled by remember { mutableStateOf(true) }
    var remainingTime by remember { mutableIntStateOf(0) }

    val scope = rememberCoroutineScope()

    fun startResendCountdown() {
        resendButtonEnabled = false
        remainingTime = 30

        scope.launch {
            println("Countdown at: $remainingTime")
            while (remainingTime > 0) {
                delay(1000) // Delay for 1 second
                remainingTime -= 1
            }
            resendButtonEnabled = true
        }
    }

    TextButton(
        onClick = {
            // Check if button is enabled before allowing resend
            if (resendButtonEnabled) {
                startResendCountdown()
            }
            userViewModel.sendVerificationEmail()
        },
        enabled = resendButtonEnabled
    ) {
        Text(
            text = if (remainingTime > 0) "Resend in $remainingTime\u200Bs" else "Resend",
            fontFamily = interDefault,
            fontSize = 12.sp,
            color = if (resendButtonEnabled) link_text_purple else grey_text,
            textDecoration = if (resendButtonEnabled) TextDecoration.Underline else TextDecoration.None
        )
    }
}

@Composable
fun VerifyEmailScreen(navController: NavController, userViewModel: UserViewModel) {
    var isVerified by remember { mutableStateOf(userViewModel.userLiveData.value?.isEmailVerified ?: false) }
    val scope = rememberCoroutineScope()

    // Periodically check for verification status
    LaunchedEffect(Unit) {
        while (true) {
            println("Checking verification status: $isVerified")
            delay(1000)
            userViewModel.isEmailVerified()
            isVerified = userViewModel.userLiveData.value?.isEmailVerified ?: false
            if (isVerified) {
                navController.navigate(Routes.MAP)
            }
        }
    }


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
            Icon(
                painter = painterResource(id = R.drawable.landplot),
                contentDescription = "Whim Logo",
                tint = Color.Unspecified,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Text(
            text = "Verify your email",
            fontFamily = interSemiBold,
            fontSize = 20.sp,
            color = header_black,
            modifier = Modifier.padding(start = 48.dp, top = 48.dp, bottom = 24.dp, end = 48.dp)
        )
        Text(
            text = "Use the link sent to ${userViewModel.userLiveData.value?.email} to verify your account.",
            fontFamily = interDefault,
            fontSize = 12.sp,
            color = header_black,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp, start = 24.dp, end = 24.dp)
        )


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(-7.dp)

            ) {
                Text(
                    text = "Didn't get a link?",
                    fontFamily = interDefault,
                    fontSize = 12.sp,
                    color = dark_grey,
                )
                ResendButton(navController, userViewModel)
            }
        }
    }
}