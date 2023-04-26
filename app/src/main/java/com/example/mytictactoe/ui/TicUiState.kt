package com.example.mytictactoe.ui

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mytictactoe.*
import com.example.mytictactoe.LoadOrSave.*
import com.example.mytictactoe.Orientation.*
import com.example.mytictactoe.BotOrGameOverScreen.*

data class TicUiState(
    val theme: AppTheme = AppTheme.AUTO,
    val orientation: Orientation = PORTRAIT,
    val memorySettings: LoadOrSave = LOAD,
    val menuIsVisible: Boolean = true,
    val fieldSize: Dp = 0.dp,
    val winRow: Int = 3,
    val savedWinRow: Int = 3,
    val winNotLose: Boolean = true,
    val savedWinNotLose: Boolean = true,
    val playingVsAI: Boolean = false,
    val menuButtonShouldBeShaken: Boolean = false,
    val firstMove: Char = CustomCellValues.player1,
    val currentMove: Char = CustomCellValues.player1,
    val cancelMoveButtonEnabled: Boolean = false,
    val customCellDialogIsVisible: Boolean = false,
    val botOrGameOverScreen: BotOrGameOverScreen = HIDDEN,
    val cellFontSize: TextUnit = 68.sp,
    val gameArray: Array<Array<Cell>> = Array(3) { i ->
        Array(3) { j ->
            Cell(
                isClickable = true,
                cellText = CustomCellValues.EMPTY,
                cellColor = CellColors.STANDART_COLOR,
            )
        }
    }
)