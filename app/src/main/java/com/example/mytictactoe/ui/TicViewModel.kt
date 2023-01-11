package com.example.mytictactoe.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TicViewModel: ViewModel() {

    private val _uiState = MutableStateFlow(TicUiState())
    val uiState: StateFlow<TicUiState> = _uiState.asStateFlow()

    private var cellsLeft: Int = 0

    fun setSize(slider: Float){
        // округление в правильную сторону (а не обязательно в меньшую)
        val size = if((slider - slider.toInt()) > 0.5)
        {slider.toInt() + 1}
        else slider.toInt()

        // if - чтобы рекомпозить только при смене целых значений
        if(size != uiState.value.gameArray.size){
            resetGame(size)
        }
    }

    fun setWinRow(slider: Float){
        // округление в правильную сторону (а не обязательно в меньшую)
        val winRow = if((slider - slider.toInt()) > 0.5)
        {slider.toInt() + 1}
        else slider.toInt()

        // if - чтобы рекомпозить только при смене целых значений
        if(winRow != uiState.value.winRow){
            _uiState.update { currentState ->
                currentState.copy(
                    winRow = slider.toInt()
                )
            }
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
        cellsLeft = size * size
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
        cellsLeft--
        checkWin(i, j, currentMove)
        checkDraw()
        changeTurn(currentMove)
    }

    private fun changeTurn(turn: String){
        val updatedTurn = if(turn == "X") "0" else "X"
        _uiState.update { currentState ->
            currentState.copy(currentMove = updatedTurn)
        }
    }

    private fun checkWin(i: Int, j: Int, currentMove: String){
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
        if (currentRow >= uiState.value.winRow) {
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
        if (currentRow >= uiState.value.winRow) {
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
        if (currentRow >= uiState.value.winRow) {
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
        if (currentRow >= uiState.value.winRow) {
            for(a in n until n+currentRow) {
                uiState.value.gameArray[a][m-a+n].textColor = 0xFF00DD41
            }
            _uiState.update { currentState ->
                currentState.copy(lastClick = true)
            }
        }
    }

    private fun checkDraw(){
        if(cellsLeft == 0){
            for(i in uiState.value.gameArray.indices) {
                for(j in uiState.value.gameArray.indices) {
                    uiState.value.gameArray[i][j].textColor = 0xFF440512
                }
            }
            _uiState.update { currentState ->
                currentState.copy(lastClick = true)
            }
        }
    }

}
