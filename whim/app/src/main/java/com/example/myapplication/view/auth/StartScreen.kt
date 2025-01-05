package com.example.myapplication.view.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.navigation.Routes
import com.example.myapplication.ui.theme.PurpleMagentaGradient
import com.example.myapplication.ui.theme.dark_grey_button_text
import com.example.myapplication.ui.theme.interSemiBold

@Composable
fun StartScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = PurpleMagentaGradient)
    ) {
        Text(
            text = "Whim",
            fontFamily = interSemiBold,
            fontSize = 24.sp,
            color = Color.White,
            modifier = Modifier.padding(start = 24.dp, top = 24.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.landplot),
                    contentDescription = "Whim Logo",
                    tint = Color.White,
                    modifier = Modifier
                        .size(60.dp)
                )
                Text(
                    text = "Events in your area.",
                    fontFamily = interSemiBold,
                    fontSize = 24.sp,
                    color = Color.White,
                    modifier = Modifier.padding(top = 24.dp, bottom = 4.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Free on Whim.",
                    fontFamily = interSemiBold,
                    fontSize = 24.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { navController.navigate(Routes.SIGNUP) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()

                ) {
                    Text(
                        text = "Sign Up",
                        fontFamily = interSemiBold,
                        fontSize = 16.sp,
                        color = dark_grey_button_text,
                    )
                }
                TextButton(
                    onClick = { navController.navigate(Routes.LOGIN) },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)

                ) {
                    Text(
                        text = "Login",
                        fontFamily = interSemiBold,
                        fontSize = 16.sp,
                        color = Color.White,
                    )
                }
            }
        }
    }
}