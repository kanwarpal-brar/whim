package com.example.myapplication.view.auth

import androidx.compose.foundation.layout.Arrangement
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
fun SignupNameScreen(navController: NavController, userViewModel: UserViewModel) {
    var enteredFirstName by remember { mutableStateOf("") }
    var enteredLastName by remember { mutableStateOf("") }


    val isFormFilled by remember {
        derivedStateOf {
            enteredFirstName.isNotEmpty() && enteredLastName.isNotEmpty()
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
            Icon(
                painter = painterResource(id = R.drawable.landplot),
                contentDescription = "Whim Logo",
                tint = Color.Unspecified,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Text(
            text = "Enter your name",
            fontFamily = interSemiBold,
            fontSize = 20.sp,
            color = header_black,
            modifier = Modifier.padding(start = 48.dp, top = 48.dp, bottom = 24.dp, end = 48.dp)
        )
        Text(
            text = "Provide your first and last name for your profile.",
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
                    value = enteredFirstName,
                    onValueChange = {
                        enteredFirstName = it
                    },
                    label = {
                        Text(
                            text = "First Name",
                            fontFamily = interDefault,
                            fontSize = 12.sp,
                            color = dark_grey
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                )

                OutlinedTextField(
                    value = enteredLastName,
                    onValueChange = {
                        enteredLastName = it
                    },
                    label = {
                        Text(
                            text = "Last Name",
                            fontFamily = interDefault,
                            fontSize = 12.sp,
                            color = dark_grey
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }


            RoundedGradientButton(
                text ="Continue",
                onClick = {
                  coroutineScope.launch {
                      userViewModel.updateFirstLastName(newFirstName = enteredFirstName, newLastName = enteredLastName)
                      userViewModel.sendVerificationEmail()

                      // Sets emailVerified on User LiveData Object
                      userViewModel.isEmailVerified()
                      if (userViewModel.userLiveData.value?.isEmailVerified == true) {
                          navController.navigate(Routes.MAP)
                      } else {
                          navController.navigate(Routes.VERIFY_EMAIL)
                      }
                  }
                },
                isEnabled = isFormFilled,
                modifier = Modifier.fillMaxWidth()
            )

        }
    }
}