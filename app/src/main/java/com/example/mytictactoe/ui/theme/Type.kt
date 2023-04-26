package com.example.mytictactoe.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.mytictactoe.R

//val Sans = FontFamily(
//    Font(R.font.sans_regular),
//    Font(R.font.sans_medium, FontWeight.Medium),
//    Font(R.font.sans_bold, FontWeight.Bold),
//)

// Set of Material typography styles to start with
val Typography = Typography(
    body1 = TextStyle(
        fontFamily = FontFamily.Default,
//        fontFamily = Sans,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp
    ),
    h5 = TextStyle(
        fontFamily = FontFamily.Default,
//        fontFamily = Sans,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp
    ),
    button = TextStyle(
        fontFamily = FontFamily.Default,
//        fontFamily = Sans,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp
    ),
    h3 = TextStyle(
        fontFamily = FontFamily.Default,
//        fontFamily = Sans,
        fontWeight = FontWeight.Normal,
        fontSize = 62.sp
    ),
)

   // h3 68.sp