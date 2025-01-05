package com.example.myapplication.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.myapplication.R

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

@OptIn(ExperimentalTextApi::class)
val interDefault = FontFamily(
    Font(
        R.font.inter,
        variationSettings = FontVariation.Settings()
    )
)
@OptIn(ExperimentalTextApi::class)
val interLight = FontFamily(
    Font(
        R.font.inter,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(300)
        )
    )
)

@OptIn(ExperimentalTextApi::class)
val interMedium = FontFamily(
    Font(
        R.font.inter,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(500)
        )
    )
)

@OptIn(ExperimentalTextApi::class)
val interSemiBold = FontFamily(
    Font(
        R.font.inter,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(600)
        )
    )
)