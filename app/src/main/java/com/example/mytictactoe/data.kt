package com.example.mytictactoe

import androidx.compose.ui.graphics.Color

data class Field(
    var isClickable: Boolean,
    var fieldText: String,
    var textColor: Color
)

data class Cells(
    val empty: String = " ",
    val x: String = "X",
    val o: String = "0"
)
val cells = Cells()