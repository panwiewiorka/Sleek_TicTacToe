package com.example.mytictactoe.ui

import com.example.mytictactoe.CellColors
import com.example.mytictactoe.Field
import com.example.mytictactoe.cells

data class TicUiState(
    val landscapeMode: Boolean = false,
    val memorySettings: Boolean = true,
    val menuDialog: Boolean = true,
    val lastClickScreen: Boolean = false,
    val winRow: Int = 3,
    val savedWinRow: Int = 3,
    val currentMove: String = cells.x,
    val cancelMoveButtonEnabled: Boolean = false,
    val gameArray: Array<Array<Field>> = Array(3) { i -> Array(3) { j -> Field(
        isClickable = true, fieldText = cells.empty, textColor = CellColors.STANDART
    ) } }
)