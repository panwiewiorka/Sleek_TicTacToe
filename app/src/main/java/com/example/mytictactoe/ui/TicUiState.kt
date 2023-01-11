package com.example.mytictactoe.ui

import androidx.compose.ui.graphics.Color
import com.example.mytictactoe.ui.theme.StandartCell

data class Field(
    var isClickable: Boolean,
    var fieldText: String,
    var textColor: Color
)

data class TicUiState(
    val menuDialog: Boolean = true,
    val lastClick: Boolean = false,
    val winRow: Int = 3,
    val currentMove: String = "X",
    val gameArray: Array<Array<Field>> = Array(3) { i -> Array(3) { j -> Field(isClickable = true, fieldText = " ", textColor = StandartCell) } }
)