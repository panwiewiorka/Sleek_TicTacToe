package com.example.mytictactoe

import com.example.mytictactoe.ui.TicViewModel
import com.example.mytictactoe.CellValues.*
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class TicUnitTests {
    private val viewModel = TicViewModel()

    @Test
    fun `ticViewModel check Win`() {
        val gameArray = viewModel.uiState.value.gameArray
        for(i in gameArray.indices){
            gameArray[i][0].cellText = X
        }
        viewModel.checkWin(0, 0, X)
        assertEquals(CellColors.WIN, gameArray[0][0].cellColor)
        assert(viewModel.uiState.value.gameOverScreenVisible)
    }

    @Test
    fun `ticViewModel check Draw`() {
        val gameArray = viewModel.uiState.value.gameArray
        gameArray[0] = arrayOf(Cell(X), Cell(O), Cell(X))
        gameArray[1] = arrayOf(Cell(X), Cell(O), Cell(X))
        gameArray[2] = arrayOf(Cell(O), Cell(X), Cell(O))
        viewModel.checkDraw()
        assertEquals(CellColors.DRAW, gameArray[0][0].cellColor)
        assert(viewModel.uiState.value.gameOverScreenVisible)
    }
}