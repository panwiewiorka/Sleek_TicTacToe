package com.example.mytictactoe.ui

import androidx.lifecycle.ViewModel
import com.example.mytictactoe.ui.theme.CurrentMove
import com.example.mytictactoe.ui.theme.Draw
import com.example.mytictactoe.ui.theme.StandartCell
import com.example.mytictactoe.ui.theme.Win
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TicViewModel: ViewModel() {

    private val _uiState = MutableStateFlow(TicUiState())
    val uiState: StateFlow<TicUiState> = _uiState.asStateFlow()

    private var cellsLeft: Int = 0
    private var oldI: Int = 0
    private var oldJ: Int = 0

    fun setSettingsFromMemory(yesNo: Boolean){
        _uiState.update { currentState ->
            currentState.copy(memorySettings = yesNo)
        }
    }

    fun checkOrientationChange(yesNo: Boolean){
        if(yesNo != uiState.value.landscapeMode) setSettingsFromMemory(true)
        _uiState.update { currentState ->
            currentState.copy(landscapeMode = yesNo)
        }
    }

    fun showMenuDialog(yesNo: Boolean){
        setSettingsFromMemory(yesNo)
        _uiState.update { currentState ->
            currentState.copy(menuDialog = yesNo)
        }
    }

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
        val gameArray = Array(size) { Array(size) { Field(isClickable = true, fieldText = " ", textColor = StandartCell) } }
        _uiState.update { currentState ->
            currentState.copy(
                lastClickScreen = false,
                gameArray = gameArray,
                currentMove = "X",
            )
        }
        cellsLeft = size * size
    }

    fun makeMove(i: Int, j: Int, currentMove: String){
        val gameArray = uiState.value.gameArray
        if(cellsLeft == (gameArray.size * gameArray.size)){
            oldI = 0
            oldJ = 0
        }
        gameArray[oldI][oldJ].textColor = StandartCell
        gameArray[i][j].textColor = CurrentMove
        gameArray[i][j].fieldText = currentMove
        gameArray[i][j].isClickable = false
        _uiState.update { currentState ->
            currentState.copy(gameArray = gameArray)
        }
        oldI = i
        oldJ = j
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
                uiState.value.gameArray[a][j].textColor = Win
            }
            _uiState.update { currentState ->
                currentState.copy(lastClickScreen = true)
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
                uiState.value.gameArray[i][a].textColor = Win
            }
            _uiState.update { currentState ->
                currentState.copy(lastClickScreen = true)
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
                uiState.value.gameArray[a][a-n+m].textColor = Win
            }
            _uiState.update { currentState ->
                currentState.copy(lastClickScreen = true)
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
                uiState.value.gameArray[a][m-a+n].textColor = Win
            }
            _uiState.update { currentState ->
                currentState.copy(lastClickScreen = true)
            }
        }
    }

    private fun checkDraw(){
        if(cellsLeft == 0){
            for(i in uiState.value.gameArray.indices) {
                for(j in uiState.value.gameArray.indices) {
                    uiState.value.gameArray[i][j].textColor = Draw
                }
            }
            _uiState.update { currentState ->
                currentState.copy(lastClickScreen = true)
            }
        }
    }

}
