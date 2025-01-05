package com.example.myapplication.view.CreateEvent

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.ui.components.RoundedGradientButton
import com.example.myapplication.ui.theme.PurpleMagentaGradient
import com.example.myapplication.ui.theme.dark_grey
import com.example.myapplication.ui.theme.interSemiBold
import androidx.activity.result.launch
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CreateEvent2(event: MutableState<Event>, onSubmit: () -> Unit) {
    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val numberOfParticipants = remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri.value = it
            event.value.imageUri = it
        }
    }

    Scaffold(
        modifier = Modifier
            .padding(vertical = 12.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                })
            },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, top = 32.dp, end = 24.dp, bottom = 12.dp)
            ) {
                Text(
                    text = "Create Event",
                    fontSize = 24.sp,
                    color = dark_grey,
                    style = TextStyle(brush = PurpleMagentaGradient),
                    fontFamily = interSemiBold
                )
            }
            Image(
                painter = painterResource(id = R.drawable.progress_bar_2),
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedTextField(
                value = event.value.description,
                onValueChange = { event.value = event.value.copy(description = it) },
                modifier = Modifier
                    .width(324.dp)
                    .height(180.dp),
                label = { Text("Description") },
                shape = RoundedCornerShape(10.dp)
            )
            Box(
                modifier = Modifier
                    .width(324.dp)
                    .height(180.dp)
                    .background(Color.White, shape = RoundedCornerShape(10.dp))
                    .border(1.dp, Color.Black, shape = RoundedCornerShape(10.dp))
                    .clickable { imagePickerLauncher.launch("image/*") } // Launch image picker
            ) {
                Text(
                    text = "Add Image (Optional)",
                    color = Color.Gray,
                    style = TextStyle(fontSize = 12.sp),
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopStart)
                )

                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Upload Image",
                    modifier = Modifier
                        .align(Alignment.Center),
                    tint = Color.Gray
                )

                // Display selected image preview if available
                imageUri.value?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.Center),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            RoundedGradientButton(text = "Create", onClick = { onSubmit() })
        }
    }
}


