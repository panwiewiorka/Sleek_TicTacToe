package com.example.mytictactoe.ui

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mytictactoe.*
import com.example.mytictactoe.LoadOrSave.*
import com.example.mytictactoe.Orientation.*

data class TicUiState(
    val orientation: Orientation = PORTRAIT,
    val memorySettings: LoadOrSave = LOAD,
    val menuIsVisible: Boolean = true,
    val playingVsAI: Boolean = false,
    val fieldSize: Dp = 0.dp,
    val botOrGameOverScreenVisible: Boolean = false,
    val botOrGameOverScreenClickable: Boolean = false,
    val winRow: Int = 3,
    val savedWinRow: Int = 3,
    val currentMove: CellValues = CellValues.X,
    val cancelMoveButtonEnabled: Boolean = false,
    val cellFontSize: TextUnit = 68.sp,
    val gameArray: Array<Array<Cell>> = Array(3) { i -> Array(3) { j -> Cell(
        isClickable = true,
        cellText = CellValues.EMPTY,
        cellColor = CellColors.STANDART,
    ) } }
)