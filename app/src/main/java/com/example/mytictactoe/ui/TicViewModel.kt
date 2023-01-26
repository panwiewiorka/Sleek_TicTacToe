package com.example.mytictactoe.ui

import androidx.lifecycle.ViewModel
import com.example.mytictactoe.Field
import com.example.mytictactoe.cells
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

    private var iOneMoveBefore: Int = 0
    private var jOneMoveBefore: Int = 0
    private var iTwoMovesBefore: Int = 0
    private var jTwoMovesBefore: Int = 0
    private var cellsLeft: Int = 0
    private var possibleWin: Boolean = false

    fun loadSettingsFromUiState(yesNo: Boolean){
        _uiState.update { currentState ->
            currentState.copy(memorySettings = yesNo)
        }
    }

    fun rememberSettingsDuringOrientationChange(yesNo: Boolean){
        if(yesNo != uiState.value.landscapeMode) loadSettingsFromUiState(true)
        _uiState.update { currentState ->
            currentState.copy(landscapeMode = yesNo)
        }
    }

    fun showMenuDialog(yesNo: Boolean){
        loadSettingsFromUiState(yesNo)
        _uiState.update { currentState ->
            currentState.copy(menuDialog = yesNo)
        }
    }

    fun setSize(slider: Float){
        // rounding to the nearest int, not necessarily to the lowest
        val size = (slider + 0.5).toInt()

        // recomposing only on discrete value changes
        if(size != uiState.value.gameArray.size){
            resetGame(size)
        }
    }

    fun setWinRow(slider: Float){
        // rounding to the nearest int, not necessarily to the lowest
        val winRow = (slider + 0.5).toInt()

        // recomposing only on discrete value changes
        if(winRow != uiState.value.winRow){
            _uiState.update { a ->
                a.copy(winRow = winRow)
            }
        }
    }

    fun cancelWinRowChange(loadWinRow: Boolean){
        if(!loadWinRow) {
            _uiState.update { a ->
                a.copy(savedWinRow = uiState.value.winRow)
            }
        } else if(cellsLeft != (uiState.value.gameArray.size * uiState.value.gameArray.size)) {
            _uiState.update { a ->
                a.copy(winRow = uiState.value.savedWinRow)
            }
        }
    }

    fun resetGame(size: Int){
        val gameArray = Array(size) { Array(size) { Field(isClickable = true, fieldText = cells.empty, textColor = StandartCell) } }
        _uiState.update { currentState ->
            currentState.copy(
                lastClickScreen = false,
                cancelMoveButtonEnabled = false,
                gameArray = gameArray,
                currentMove = cells.x,
            )
        }
        cellsLeft = size * size
    }

    fun makeMove(i: Int, j: Int){
        val gameArray = uiState.value.gameArray
        if(cellsLeft == (gameArray.size * gameArray.size)){
            iOneMoveBefore = 0
            jOneMoveBefore = 0
            iTwoMovesBefore = 0
            jTwoMovesBefore = 0
        }
        //gameArray[iTwoMovesBefore][jTwoMovesBefore].textColor = StandartCell
        iTwoMovesBefore = iOneMoveBefore
        jTwoMovesBefore = jOneMoveBefore
        gameArray[iOneMoveBefore][jOneMoveBefore].textColor = StandartCell
        gameArray[i][j].textColor = CurrentMove
        gameArray[i][j].fieldText = uiState.value.currentMove
        gameArray[i][j].isClickable = false
        _uiState.update { currentState ->
            currentState.copy(
                gameArray = gameArray,
                cancelMoveButtonEnabled = true
            )
        }
        iOneMoveBefore = i
        jOneMoveBefore = j
        cellsLeft--
        checkWin(i, j, uiState.value.currentMove)
        if(!uiState.value.lastClickScreen) {
            checkDraw()
            if(!uiState.value.lastClickScreen) {
                changeTurn(uiState.value.currentMove)
            }
        }
    }

    fun cancelMove(){
        val gameArray = uiState.value.gameArray
        if(uiState.value.lastClickScreen){
            for(i in gameArray.indices) {
                for(j in gameArray.indices) {
                    gameArray[i][j].textColor = StandartCell
                }
            }
            _uiState.update { currentState ->
                currentState.copy(lastClickScreen = false)
            }
        } else changeTurn(uiState.value.currentMove)
        gameArray[iOneMoveBefore][jOneMoveBefore].fieldText = cells.empty
        gameArray[iOneMoveBefore][jOneMoveBefore].isClickable = true
        gameArray[iTwoMovesBefore][jTwoMovesBefore].textColor = CurrentMove
        _uiState.update { currentState ->
            currentState.copy(
                gameArray = gameArray,
                cancelMoveButtonEnabled = false
            )
        }
        iOneMoveBefore = iTwoMovesBefore
        jOneMoveBefore = jTwoMovesBefore
        cellsLeft++
    }

    private fun changeTurn(currentMove: String){
        val updatedTurn = if(currentMove == cells.x) cells.o else cells.x
        _uiState.update { a ->
            a.copy(currentMove = updatedTurn)
        }
    }

    private fun checkWin(i: Int, j: Int, currentMove: String){
        /* Algorithm is similar to drawAlgorithm:
        from currently clicked cell we are looking forward and backward, in all directions,
        to find same (X or 0) cells, adding those founded to compare to winRow.

        Then (contrary to drawAlgorithm algorithm)
        we have to make sure that AT LEAST ONE of the four directions can win (HAVE enough X or 0 in a row)
         */

        // VERTICAL CHECK forward
        var n = i
        var currentRow = 1

        while((n + 1 < uiState.value.gameArray.size) && (uiState.value.gameArray[n + 1][j].fieldText == currentMove)){
            currentRow++
            n++
        }
        // then backward
        n = i
        while((n > 0) && (uiState.value.gameArray[n - 1][j].fieldText == currentMove)){
            currentRow++
            n--
        }
        // if enough X or 0 in a row -> WIN
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
        possibleWin = false
        // checking whether any of the free remaining cells can possibly win
        changeTurn(uiState.value.currentMove)  // X or 0
        for (i in uiState.value.gameArray.indices){
            for (j in uiState.value.gameArray[i].indices){
                if((uiState.value.gameArray[i][j].fieldText == cells.empty) && (!possibleWin)){
                    drawAlgorithm(i, j, uiState.value.currentMove)
                }
            }
        }
        changeTurn(uiState.value.currentMove)  // 0 or X
        for (i in uiState.value.gameArray.indices){
            for (j in uiState.value.gameArray[i].indices){
                if((uiState.value.gameArray[i][j].fieldText == cells.empty) && (!possibleWin)){
                    drawAlgorithm(i, j, uiState.value.currentMove)
                }
            }
        }
        // if there are no such cells -> Draw
        if(!possibleWin) {
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

    private fun drawAlgorithm(i: Int, j: Int, currentMove: String){
        /* Algorithm is similar to checkWin:
        FOR EVERY cell we are looking forward and backward, in all directions,
        to find same (X or 0) OR EMPTY cells, adding those founded to compare to winRow.

        Then, if any cell could possibly win - there's no Draw
         */

        // VERTICAL CHECK forward
        var n = i
        var currentRow = 1

        while((n + 1 < uiState.value.gameArray.size) && (uiState.value.gameArray[n + 1][j].fieldText != currentMove)){
            currentRow++
            n++
        }
        // then backward
        n = i
        while((n > 0) && (uiState.value.gameArray[n - 1][j].fieldText != currentMove)){
            currentRow++
            n--
        }
        // if enough ((X or 0) + " ") in a row -> possible Win (no Draw)
        if (currentRow >= uiState.value.winRow) {
            possibleWin = true
            return
        }

        // HORIZONTAL CHECK
        var m = j
        currentRow = 1

        while((m + 1 < uiState.value.gameArray.size) && (uiState.value.gameArray[i][m + 1].fieldText != currentMove)){
            currentRow++
            m++
        }
        m = j
        while((m > 0) && (uiState.value.gameArray[i][m - 1].fieldText != currentMove)){
            currentRow++
            m--
        }
        if (currentRow >= uiState.value.winRow) {
            possibleWin = true
            return
        }

        // MAIN DIAGONAL CHECK
        n = i
        m = j
        currentRow = 1

        while((n + 1 < uiState.value.gameArray.size) && (m + 1 < uiState.value.gameArray.size) && (uiState.value.gameArray[n + 1][m + 1].fieldText != currentMove)){
            currentRow++
            n++
            m++
        }
        n = i
        m = j
        while((n > 0) && (m > 0) && (uiState.value.gameArray[n - 1][m - 1].fieldText != currentMove)){
            currentRow++
            n--
            m--
        }
        if (currentRow >= uiState.value.winRow) {
            possibleWin = true
            return
        }

        // OTHER DIAGONAL CHECK
        n = i
        m = j
        currentRow = 1

        while((n + 1 < uiState.value.gameArray.size) && (m > 0) && (uiState.value.gameArray[n + 1][m - 1].fieldText != currentMove)){
            currentRow++
            n++
            m--
        }
        n = i
        m = j
        while((n > 0) && (m + 1 < uiState.value.gameArray.size) && (uiState.value.gameArray[n - 1][m + 1].fieldText != currentMove)){
            currentRow++
            n--
            m++
        }
        if (currentRow >= uiState.value.winRow) {
            possibleWin = true
            return
        }
    }

}
