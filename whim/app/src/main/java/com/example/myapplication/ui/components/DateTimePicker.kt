package com.example.myapplication.ui.components

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.example.myapplication.view.CreateEvent.Event
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

@SuppressLint("DefaultLocale")
@Composable
fun DateTimePicker(event: MutableState<Event>, label: String, toggle: Boolean) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val keyboardController = LocalSoftwareKeyboardController.current

    val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm")
    val dateTimeString = if (toggle) {
        event.value.startDate.format(dateTimeFormatter)
    } else {
        event.value.endDate.format(dateTimeFormatter)
    }

    OutlinedTextField(
        value = dateTimeString,
        onValueChange = { },
        label = { Text(label) },
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .width(324.dp)
            .onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    keyboardController?.hide()
                    val datePickerDialog = DatePickerDialog(
                        context,
                        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                            val timePickerDialog = TimePickerDialog(
                                context,
                                { _: TimePicker, hour: Int, minute: Int ->
                                    val newDateTime = LocalDateTime.of(year, month + 1, dayOfMonth, hour, minute)
                                    if (toggle) {
                                        event.value = event.value.copy(startDate = newDateTime)
                                    } else {
                                        event.value = event.value.copy(endDate = newDateTime)
                                    }
                                },
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                false
                            )
                            timePickerDialog.show()
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    )
                    datePickerDialog.show()
                }
            }
    )
}