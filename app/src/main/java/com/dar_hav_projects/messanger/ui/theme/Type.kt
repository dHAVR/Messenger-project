package com.dar_hav_projects.messanger.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.dar_hav_projects.messanger.R


val mulishFontFamily = FontFamily(
    Font(R.font.mulish),
    Font(R.font.mulish_bold, FontWeight.Bold),
    Font(R.font.mulish_semibold, FontWeight.SemiBold),
    Font(R.font.mulish_extralight, FontWeight.ExtraLight),

)

val h1 = TextStyle(
    fontFamily = mulishFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 32.sp
)

val h2 = TextStyle(
    fontFamily = mulishFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 24.sp,
)

val sub1 = TextStyle(
    fontFamily = mulishFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 18.sp
)

val sub2 = TextStyle(
    fontFamily = mulishFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp,
)

val body1 = TextStyle(
    fontFamily = mulishFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 14.sp
)

val body2 = TextStyle(
    fontFamily = mulishFontFamily,
    fontSize = 14.sp,
)

val meta1 = TextStyle(
    fontFamily = mulishFontFamily,
    fontSize = 12.sp
)

val meta2 = TextStyle(
    fontFamily = mulishFontFamily,
    fontSize = 10.sp,
)

val meta3 = TextStyle(
    fontFamily = mulishFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 10.sp,
)


