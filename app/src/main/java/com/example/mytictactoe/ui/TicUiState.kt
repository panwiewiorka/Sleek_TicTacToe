package com.example.mytictactoe.ui

data class Field(
    var isClickable: Boolean,
    var fieldText: String,
    var textColor: Long
)

data class TicUiState(
    val menuDialog: Boolean = true,
    val lastClick: Boolean = false,
    val currentMove: String = "X",
    val gameArray: Array<Array<Field>> = Array(3) { i -> Array(3) { j -> Field(isClickable = true, fieldText = " ", textColor = 0xFFFFFFFF) } }
)