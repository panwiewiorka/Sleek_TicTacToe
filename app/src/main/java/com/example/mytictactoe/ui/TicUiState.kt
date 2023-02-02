package com.example.mytictactoe.ui

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.mytictactoe.CellColors
import com.example.mytictactoe.CellValues
import com.example.mytictactoe.Field

data class TicUiState(
    val landscapeMode: Boolean = false,
    val memorySettings: Boolean = true,
    val menuDialog: Boolean = true,
    val lastClickScreen: Boolean = false,
    val winRow: Int = 3,
    val savedWinRow: Int = 3,
    val currentMove: CellValues = CellValues.X,
    val cancelMoveButtonEnabled: Boolean = false,
    val cellFontSize: TextUnit = 68.sp,
    val gameArray: Array<Array<Field>> = Array(3) { i -> Array(3) { j -> Field(
        isClickable = true,
        fieldText = CellValues.EMPTY,
        textColor = CellColors.STANDART,
    ) } }
)