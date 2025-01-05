package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.DisabledButtonBrush
import com.example.myapplication.ui.theme.PurpleMagentaGradient
import com.example.myapplication.ui.theme.grey_text
import com.example.myapplication.ui.theme.interLight

@Composable
fun RoundedGradientButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, isEnabled: Boolean = true) {
    Button(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 36.dp)
            .background(
                brush = if (isEnabled) PurpleMagentaGradient else DisabledButtonBrush,
                shape = RoundedCornerShape(32.dp)
            ),
        enabled = isEnabled,
        onClick = onClick
    ) {
        Text(
            text = text,
            color = if (isEnabled) Color.White else grey_text,
            fontFamily = interLight,
            fontSize = 16.sp
        )
    }
}
