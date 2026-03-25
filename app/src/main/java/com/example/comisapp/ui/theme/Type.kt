package com.example.comisapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.comisapp.R

// Set of Material typography styles to start with
val InterFontFamily = FontFamily(
        Font(R.font.inter_thin, FontWeight.Thin),
Font(R.font.inter_thinitalic, FontWeight.Thin, FontStyle.Italic),
Font(R.font.inter_extralight, FontWeight.ExtraLight),
Font(R.font.inter_extralightitalic, FontWeight.ExtraLight, FontStyle.Italic),
Font(R.font.inter_light, FontWeight.Light),
Font(R.font.inter_lightitalic, FontWeight.Light, FontStyle.Italic),
Font(R.font.inter_regular, FontWeight.Normal),
Font(R.font.inter_italic, FontWeight.Normal, FontStyle.Italic),
Font(R.font.inter_medium, FontWeight.Medium),
Font(R.font.inter_mediumitalic, FontWeight.Medium, FontStyle.Italic),
Font(R.font.inter_semibold, FontWeight.SemiBold),
Font(R.font.inter_semibolditalic, FontWeight.SemiBold, FontStyle.Italic),
Font(R.font.inter_bold, FontWeight.Bold),
Font(R.font.inter_bolditalic, FontWeight.Bold, FontStyle.Italic),
Font(R.font.inter_extrabold, FontWeight.ExtraBold),
Font(R.font.inter_extrabolditalic, FontWeight.ExtraBold, FontStyle.Italic),
Font(R.font.inter_black, FontWeight.Black),
Font(R.font.inter_blackitalic, FontWeight.Black, FontStyle.Italic),
)

val defaultTypography = Typography()

val AppTypography = Typography(
    displayLarge = defaultTypography.displayLarge.copy(fontFamily = InterFontFamily),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = InterFontFamily),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = InterFontFamily),

    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = InterFontFamily),
    headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = InterFontFamily),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = InterFontFamily),

    titleLarge = defaultTypography.titleLarge.copy(fontFamily = InterFontFamily),
    titleMedium = defaultTypography.titleMedium.copy(fontFamily = InterFontFamily),
    titleSmall = defaultTypography.titleSmall.copy(fontFamily = InterFontFamily),

    bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = InterFontFamily),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = InterFontFamily),
    bodySmall = defaultTypography.bodySmall.copy(fontFamily = InterFontFamily),

    labelLarge = defaultTypography.labelLarge.copy(fontFamily = InterFontFamily),
    labelMedium = defaultTypography.labelMedium.copy(fontFamily = InterFontFamily),
    labelSmall = defaultTypography.labelSmall.copy(fontFamily = InterFontFamily)
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