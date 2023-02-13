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
    var cellColor: CellColors = CellColors.STANDART_COLOR,
)

enum class CellValues(val cellValue: Char){
    EMPTY(' '),
    X('X'),
    O('0')
}

enum class CellColors {
    STANDART_COLOR,
    CURRENT_COLOR,
    WIN_COLOR,
    DRAW_COLOR;

    val color: Color
        @Composable
        //@ReadOnlyComposable
        get() = when(this) {
            STANDART_COLOR -> MaterialTheme.colors.onSecondary
            CURRENT_COLOR -> MaterialTheme.colors.current
            WIN_COLOR -> MaterialTheme.colors.win
            DRAW_COLOR -> MaterialTheme.colors.draw
        }
}

enum class LoadOrSave(val loadOrSave: Boolean) {
    LOAD(true),
    SAVE(false)
}

enum class Orientation { PORTRAIT, LANDSCAPE }

enum class AutoResizeLimit { WIDTH, HEIGHT }

data class BotOrGameOverScreenState (
    var visible: Boolean = false,
    val clickable: Boolean = false
        )
enum class BotOrGameOverScreen(val state: BotOrGameOverScreenState) {
    BOT(BotOrGameOverScreenState(visible = true, clickable = false)),
    GAMEOVER(BotOrGameOverScreenState(visible = true, clickable = true)),
    HIDDEN(BotOrGameOverScreenState(visible = false, clickable = false));
}

enum class EndOfCheck { WIN, DRAW, ONE_BEFORE_BOT_WIN, ONE_BEFORE_PLAYER_WIN, TWO_BEFORE_PLAYER_WIN }
