package com.example.mytictactoe.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TicViewModel: ViewModel() {

            // Game UI state
    private val _uiState = MutableStateFlow(TicUiState())
            // Backing property to avoid state updates from other classes
    val uiState: StateFlow<TicUiState> = _uiState.asStateFlow()
            // The asStateFlow() makes this mutable state flow a read-only state flow

    fun setSize(sliderPosition: Float){
        // округление в правильную сторону (а не обязательно в меньшую)
        val size = if((sliderPosition - sliderPosition.toInt()) > 0.5)
        {sliderPosition.toInt() + 1}
        else sliderPosition.toInt()

        // if - чтобы рекомпозить только при смене целых значений
        if(size != uiState.value.gameArray.size){
            resetGame(size)
        }
    }

    fun resetGame(size: Int){
        val gameArray = Array(size) { Array(size) { Field(isClickable = true, fieldText = " ", textColor = 0xFFFFFFFF) } }
        _uiState.update { currentState ->
            currentState.copy(
                lastClick = false,
                gameArray = gameArray,
                currentMove = "X",
            )
        }
    }

    fun showMenuDialog(yesNo: Boolean){
        _uiState.update { currentState ->
            currentState.copy(menuDialog = yesNo)
        }
    }

    fun makeMove(i: Int, j: Int, currentMove: String){
        val gameArray = uiState.value.gameArray
        gameArray[i][j].fieldText = currentMove
        gameArray[i][j].isClickable = false
        _uiState.update { currentState ->
            currentState.copy(gameArray = gameArray)
        }
        checkWin(i, j, currentMove)
        changeTurn(currentMove)
    }

    private fun changeTurn(turn: String){
        val updatedTurn = if(turn == "X") "0" else "X"
        _uiState.update { currentState ->
            currentState.copy(currentMove = updatedTurn)
        }
    }

    private fun checkWin(i: Int, j: Int, currentMove: String){
        val winRow = 3

        // VERTICAL CHECK
        var n = i
        var currentRow = 1

        while((n + 1 < uiState.value.gameArray.size) && (uiState.value.gameArray[n + 1][j].fieldText == currentMove)){
            currentRow++
            n++
        }
        n = i
        while((n > 0) && (uiState.value.gameArray[n - 1][j].fieldText == currentMove)){
            currentRow++
            n--
        }
        if (currentRow >= winRow) {
            for(a in n until n+currentRow) {
                uiState.value.gameArray[a][j].textColor = 0xFF00DD41
            }
            _uiState.update { currentState ->
                currentState.copy(lastClick = true)
            }
        }

        // HORIZONTAL CHECK
        var m = j
        currentRow = 1

        while((m + 1 < uiState.value.gameArray.size) && (uiState.value.gameArray[i][m + 1].fieldText == currentMove)){
            currentRow++
            m++
        }
        m = j
        while((m > 0) && (uiState.value.gameArray[i][m - 1].fieldText == currentMove)){
            currentRow++
            m--
        }
        if (currentRow >= winRow) {
            for(a in m until m+currentRow) {
                uiState.value.gameArray[i][a].textColor = 0xFF00DD41
            }
            _uiState.update { currentState ->
                currentState.copy(lastClick = true)
            }
        }

        // MAIN DIAGONAL CHECK
        n = i
        m = j
        currentRow = 1

        while((n + 1 < uiState.value.gameArray.size) && (m + 1 < uiState.value.gameArray.size) && (uiState.value.gameArray[n + 1][m + 1].fieldText == currentMove)){
            currentRow++
            n++
            m++
        }
        n = i
        m = j
        while((n > 0) && (m > 0) && (uiState.value.gameArray[n - 1][m - 1].fieldText == currentMove)){
            currentRow++
            n--
            m--
        }
        if (currentRow >= winRow) {
            for(a in n until n+currentRow) {
                uiState.value.gameArray[a][a-n+m].textColor = 0xFF00DD41
            }
            _uiState.update { currentState ->
                currentState.copy(lastClick = true)
            }
        }

        // OTHER DIAGONAL CHECK
        n = i
        m = j
        currentRow = 1

        while((n + 1 < uiState.value.gameArray.size) && (m > 0) && (uiState.value.gameArray[n + 1][m - 1].fieldText == currentMove)){
            currentRow++
            n++
            m--
        }
        n = i
        m = j
        while((n > 0) && (m + 1 < uiState.value.gameArray.size) && (uiState.value.gameArray[n - 1][m + 1].fieldText == currentMove)){
            currentRow++
            n--
            m++
        }
        if (currentRow >= winRow) {
            for(a in n until n+currentRow) {
                uiState.value.gameArray[a][m-a+n].textColor = 0xFF00DD41
            }
            _uiState.update { currentState ->
                currentState.copy(lastClick = true)
            }
        }
    }

}
