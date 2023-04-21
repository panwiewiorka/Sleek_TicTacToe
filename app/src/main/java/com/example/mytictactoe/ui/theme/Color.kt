package com.example.mytictactoe.ui.theme

import androidx.compose.material.Colors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val SliderDark = Color(0xFF85C9A9)
val SliderBgDark = Color(0xFF005959)
val StandartCellDark = Color(0xFFFFFFFF)
val CellBackgroundDark = Color(0xFF555555)
val BackgroundDark = Color(0xFF191919)

val StandartCellLight = Color(0xFF000000)
val CellBackgroundLight = Color(0xFFFFFFFF)
val SliderLight = Color(0xFFFF4848)
val SliderBgLight = Color(0xFFFFB7B7)
val BackgroundLight = Color(0xFFCCCCCC)

val Colors.current: Color
    @Composable
    get() = if (isLight) Color(0xFF396F9E) else Color(0xFFFFFF88)

val Colors.win: Color
    @Composable
    get() = if (isLight) Color(0xFF40A668) else Color(0xFF00CD81)

val Colors.lose: Color
    @Composable
    get() = if (isLight) Color(0xFFFF4848) else Color(0xFFED3333)

val Colors.invisible1: Color
    @Composable
    get() = if (isLight) Color(0x00000000) else Color(0x00000000)

val Colors.invisible2: Color
    @Composable
    get() = if (isLight) Color(0x00FFFFFF) else Color(0x00FFFFFF)

val Colors.draw: Color
    @Composable
    get() = if (isLight) Color(0xFFA85562) else Color(0xFF440512)

val Colors.menuBorder: Color
    @Composable
    get() = if (isLight) Color(0x00000000) else Color(0x00222222) // still using? Menu border