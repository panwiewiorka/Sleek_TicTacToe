package com.example.mytictactoe.ui.theme

import androidx.compose.material.Colors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

//val Purple200 = Color(0xFFBB86FC)
//val SliderBgDark = Color(0xFF493a5c)
val Purple200 = Color(0xFF85C9A9)
val SliderBgDark = Color(0xFF005959)
val StandartCellDark = Color(0xFFFFFFFF)
val CellBackgroundDark = Color(0xFF555555)


val StandartCellLight = Color(0xFF000000)
val CellBackgroundLight = Color(0xFFFFFFFF)
//val SliderLight = Color(0xFF005959)
//val SliderBgLight = Color(0xFF85C9A9)
val SliderLight = Color(0xFFFF4848)
val SliderBgLight = Color(0xFFFFB7B7)
val BackgroundLight = Color(0xFFCCCCCC)

val Colors.current: Color
    @Composable
    get() = if (isLight) Color(0xFF30676E) else Color(0xFFFFFF88)

val Colors.win: Color
    @Composable
    get() = if (isLight) Color(0xFF208648) else Color(0xFF00CD81)
// 0xFF00DD41

val Colors.draw: Color
    @Composable
    get() = if (isLight) Color(0xFFA85562) else Color(0xFF440512)