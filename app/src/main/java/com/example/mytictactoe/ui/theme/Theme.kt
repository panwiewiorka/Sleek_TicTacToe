package com.example.mytictactoe.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import android.annotation.SuppressLint
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytictactoe.AppTheme.*
import com.example.mytictactoe.ui.TicViewModel

@SuppressLint("ConflictingOnColor")
private val DarkColors = darkColors(
    primary = SliderDark,
    primaryVariant = SliderBgDark,
    secondaryVariant = SliderBgDark,
    surface = Color.Black,
    secondary = CellBackgroundDark,
    onSecondary = StandartCellDark,
    background = BackgroundDark,
)

private val LightColors = lightColors(
    primary = SliderLight,
    primaryVariant = SliderBgLight,
    surface = Color.White,
    secondary = CellBackgroundLight,
    onSecondary = StandartCellLight,
    background = BackgroundLight
)

@Composable
fun MyTicTacToeTheme(
    SystemThemeIsDark: Boolean = isSystemInDarkTheme(),
    ticViewModel: TicViewModel = viewModel(),
    content: @Composable () -> Unit) {

    val ticUiState by ticViewModel.uiState.collectAsState()


    MaterialTheme(
        colors = when(ticUiState.theme){
            DARK -> DarkColors
            LIGHT -> LightColors
            AUTO -> if(SystemThemeIsDark) DarkColors else LightColors
        } ,
        typography = Typography,
        shapes = Shapes,
        content = content,
    )
}
