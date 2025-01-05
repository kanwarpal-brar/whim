package com.example.myapplication.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
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
import com.example.myapplication.ui.theme.light_grey
import com.example.myapplication.ui.theme.link_text_purple
import kotlinx.coroutines.launch

@Composable
fun EditProfileScreen(navController: NavController, userViewModel: UserViewModel) {
    val initialUsername = userViewModel.userProfile.value?.userName ?: ""
    val initialFirstName = userViewModel.userProfile.value?.firstName ?: ""
    val initialLastName = userViewModel.userProfile.value?.lastName ?: ""

    var enteredUsername by remember { mutableStateOf(userViewModel.userProfile.value?.userName ?: "") }
    var enteredFirstName by remember { mutableStateOf(userViewModel.userProfile.value?.firstName ?: "") }
    var enteredLastName by remember { mutableStateOf(userViewModel.userProfile.value?.lastName ?: "") }
    var duplicateUsernameError by remember { mutableStateOf(false) }

    val isFormFilled by remember {
        derivedStateOf {
            enteredUsername.isNotEmpty() && enteredFirstName.isNotEmpty() && enteredLastName.isNotEmpty() && !duplicateUsernameError
        }
    }

    val coroutineScope = rememberCoroutineScope()


    Column(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = {
                navController.navigate(Routes.PROFILE)
            },
            modifier = Modifier
                .padding(16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.backarrow),
                contentDescription = "Back",
                tint = Color.Unspecified
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.music),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .aspectRatio(1f)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = enteredUsername,
                onValueChange = {
                    enteredUsername = it
                    duplicateUsernameError = false
                },
                isError = enteredUsername.isEmpty(),
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
                ),
                modifier = Modifier
                    .fillMaxWidth()
            )
            if (duplicateUsernameError || enteredUsername.isEmpty()) {
                var errorText = ""

                if (duplicateUsernameError) {
                    errorText = "That username is already taken."
                } else {
                    errorText = "Username cannot be empty."
                }

                Text(
                    text = errorText,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(top = 4.dp)
                )
            }

            OutlinedTextField(
                value = enteredFirstName,
                onValueChange = {
                    enteredFirstName = it
                },
                isError = enteredFirstName.isEmpty(),
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
            if (enteredFirstName.isEmpty()) {
                Text(
                    text = "First name cannot be empty.",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(top = 4.dp)
                )
            }

            OutlinedTextField(
                value = enteredLastName,
                onValueChange = {
                    enteredLastName = it
                },
                isError = enteredLastName.isEmpty(),
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
            if (enteredLastName.isEmpty()) {
                Text(
                    text = "Last name cannot be empty.",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(top = 4.dp)
                )
            }
            
            RoundedGradientButton(text = "Save", onClick = {
                coroutineScope.launch {
                    if (enteredUsername != initialUsername) {
                        val takeUserNameResult = userViewModel.takeUserName(enteredUsername)
                        if (takeUserNameResult.isSuccess) {
                            userViewModel.updateUserName(enteredUsername)
                            Toast.makeText(navController.context, "Username updated.", Toast.LENGTH_LONG).show()
                        } else {
                            duplicateUsernameError = true
                        }
                    }
                    if (enteredFirstName != initialFirstName || enteredLastName != initialLastName) {
                        Toast.makeText(navController.context, "Name updated.", Toast.LENGTH_LONG).show()
                        userViewModel.updateFirstLastName(newFirstName = enteredFirstName, newLastName = enteredLastName)
                    }
                }
            }, isEnabled = isFormFilled)



        }
    }
}