package com.example.myapplication.view.CreateEvent

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.navigation.Routes
import com.example.myapplication.ui.components.DateTimePicker
import com.example.myapplication.ui.components.RoundedGradientButton
import com.example.myapplication.ui.theme.PurpleMagentaGradient
import com.example.myapplication.ui.theme.dark_grey
import com.example.myapplication.ui.theme.interDefault
import com.example.myapplication.ui.theme.interSemiBold
import java.time.LocalDateTime
import java.time.Month


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CreateEvent1(event: MutableState<Event>, navController: NavController) {
    val checked = remember { mutableStateOf(false) }
    val isValidForm = remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    fun validateForm(event: Event, checked: MutableState<Boolean>) {
        isValidForm.value = event.title.isNotEmpty() &&
                event.startDate != LocalDateTime.of(1900, Month.JANUARY, 1, 0, 0) &&
                event.endDate != LocalDateTime.of(1900, Month.JANUARY, 1, 0, 0) &&
                event.startDate.isBefore(event.endDate) &&
                event.endDate.isAfter(LocalDateTime.now()) &&
                event.attendeeLimit > 0 &&
                checked.value != false
    }

    LaunchedEffect(checked.value, event.value) {
        validateForm(event.value, checked)
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
                painter = painterResource(id = R.drawable.progress_bar_1),
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Fill in the fields below to create\nyour event!",
                fontSize = 16.sp,
                color = dark_grey,
                fontFamily = interDefault,
                textAlign = TextAlign.Center
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = checked.value,
                    onCheckedChange = { checked.value = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.secondary
                    )
                )
                Text(
                    text = "I agree with terms of use",
                    fontSize = 16.sp,
                    color = dark_grey,
                    fontFamily = interDefault,
                    textAlign = TextAlign.Center
                )
            }
            Divider(thickness = 1.dp)
            Spacer(modifier = Modifier.width(16.dp))
            Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)) {
                Text(
                    text = "Event Details",
                    fontSize = 18.sp,
                    color = dark_grey,
                    fontFamily = interSemiBold,
                    textAlign = TextAlign.Left
                )
            }
            OutlinedTextField(
                value = event.value.title,
                onValueChange = { event.value = event.value.copy(title = it) },
                modifier = Modifier.width(324.dp),
                label = { Text("Title") },
                shape = RoundedCornerShape(10.dp)
            )
            OutlinedTextField(
                value = event.value.address,
                onValueChange = { event.value = event.value.copy(address = it) },
                modifier = Modifier.width(324.dp),
                label = { Text("Location") },
                shape = RoundedCornerShape(10.dp)
            )
            OutlinedTextField(
                value = if (event.value.attendeeLimit > 0) event.value.attendeeLimit.toString() else "",
                onValueChange = { event.value = event.value.copy(attendeeLimit = it.toIntOrNull() ?: 0) },
                modifier = Modifier.width(324.dp),
                label = { Text("Number of Participants") },
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            DateTimePicker(event, "Start Time", true)
            DateTimePicker(event, "End Time", false)

            RoundedGradientButton(text = "Continue", isEnabled =  isValidForm.value, onClick = { navController.navigate(Routes.CREATE_EVENT + "/2") })
        }
    }
}
