package com.example.mytictactoe.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import android.annotation.SuppressLint
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@SuppressLint("ConflictingOnColor")
private val DarkColorPalette = darkColors(
    primary = SliderDark,
    primaryVariant = SliderBgDark,
    secondaryVariant = SliderBgDark,
    surface = Color.Black,
    secondary = CellBackgroundDark,
    onSecondary = StandartCellDark,
    background = BackgroundDark,
)

private val LightColorPalette = lightColors(
    primary = SliderLight,
    primaryVariant = SliderBgLight,
    surface = Color.White,
    secondary = CellBackgroundLight,
    onSecondary = StandartCellLight,
    background = BackgroundLight
)

@Composable
fun MyTicTacToeTheme(
//    darkTheme: Boolean = true,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit) {

    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content,
    )
}