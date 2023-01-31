package com.example.mytictactoe

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
//import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import com.example.mytictactoe.ui.theme.current
import com.example.mytictactoe.ui.theme.draw
import com.example.mytictactoe.ui.theme.win

data class Field(
    var isClickable: Boolean,
    var fieldText: String,
    var textColor: CellColors
)


data class CellValues(
    val empty: String = " ",
    val x: String = "X",
    val o: String = "0"
)
val cells = CellValues()


enum class CellColors {
    STANDART,
    CURRENT,
    WIN,
    DRAW
    ;

    val color: Color
        @Composable
        //@ReadOnlyComposable
        get() = when(this) {
            STANDART -> MaterialTheme.colors.onSecondary
            CURRENT -> MaterialTheme.colors.current
            WIN -> MaterialTheme.colors.win
            DRAW -> MaterialTheme.colors.draw
        }
}