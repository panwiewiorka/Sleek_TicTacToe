package com.example.mytictactoe

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.mytictactoe.ui.theme.current
import com.example.mytictactoe.ui.theme.draw
import com.example.mytictactoe.ui.theme.win


data class Cell(
    var cellText: CellValues = CellValues.EMPTY,
    var isClickable: Boolean = true,
    var cellColor: CellColors = CellColors.STANDART,
)


enum class CellValues(val cellValue: Char){
    EMPTY(' '),
    X('X'),
    O('0')
}


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

enum class LoadOrSave(val loadOrSave: Boolean) {
    LOAD(true),
    SAVE(false)
}

enum class Orientation { PORTRAIT, LANDSCAPE }

enum class AutoResizeLimit { WIDTH, HEIGHT }