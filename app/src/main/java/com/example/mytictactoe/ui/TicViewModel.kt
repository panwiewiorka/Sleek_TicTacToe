package com.example.mytictactoe.ui

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.example.mytictactoe.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.example.mytictactoe.LoadOrSave.*
import com.example.mytictactoe.BotOrGameOverScreen.*

class TicViewModel: ViewModel() {

    private val _uiState = MutableStateFlow(TicUiState())
    val uiState: StateFlow<TicUiState> = _uiState.asStateFlow()

    private var iOneMoveBefore: Int = 0
    private var jOneMoveBefore: Int = 0
    private var iTwoMovesBefore: Int = 0
    private var jTwoMovesBefore: Int = 0
    private var freeCellsLeft: Int = 0
    private var winIsImpossible: Boolean = true

    private val bot = Bot()

    //--------INTERFACE

    fun showMenu(show: Boolean){
        if(show) setMenuSettings(LOAD) else setMenuSettings(SAVE)
        _uiState.update { currentState ->
            currentState.copy(menuIsVisible = show)
        }
    }

    fun updateFieldSize(fieldSize: Dp){
        _uiState.update { a ->
            a.copy(
                fieldSize = fieldSize
            )
        }
    }

    fun setFieldSize(slider: Float){
        // rounding to the nearest int, not necessarily to the lowest
        val size = (slider + 0.5).toInt()

        // recomposing only on discrete value changes
        if(size != uiState.value.gameArray.size){
            resetGame(size)
        }
    }

    private fun setCellFontSize(gameFieldSize: Int) {
        // changing cellFontSize depending on gameFieldSize
        // before unnecessary recomposition possible by AutoResizedText() composable
        _uiState.update { a ->
            a.copy(
                cellFontSize = (62 - (6 * (gameFieldSize - 3))).sp
            )
        }
    }

    //---------SETTINGS

    fun setMenuSettings(loadOrSave: LoadOrSave){
        _uiState.update { currentState ->
            currentState.copy(memorySettings = loadOrSave)
        }
    }

    fun rememberSettingsDuringOrientationChange(orientation: Orientation){
        if(orientation != uiState.value.orientation) {
            setMenuSettings(LOAD)
            _uiState.update { currentState ->
                currentState.copy(orientation = orientation)
            }
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

    fun saveWinRow(){
        _uiState.update { a ->
            a.copy(savedWinRow = uiState.value.winRow)
        }
    }

    fun cancelWinRowChangesDuringTheGame(){
        if(freeCellsLeft != (uiState.value.gameArray.size * uiState.value.gameArray.size)) {
            _uiState.update { a ->
                a.copy(winRow = uiState.value.savedWinRow)
            }
        }
    }

    fun switchGameMode(playingVsAI: Boolean){
        _uiState.update { a ->
            a.copy(
                playingVsAI = !playingVsAI
            )
        }
    }

    fun setBotOrGameOverScreen(state: BotOrGameOverScreen){
        _uiState.update { a ->
            a.copy(
                botOrGameOverScreen = when(state){
                    BOT -> BOT
                    GAMEOVER -> GAMEOVER
                    HIDDEN -> HIDDEN
                }
            )
        }
    }

    //----------GAMEPlAY

    fun resetGame(size: Int){
        setCellFontSize(size)
        val gameArray = Array(size) { Array(size) { Cell(
            isClickable = true,
            cellText = CellValues.EMPTY,
            cellColor = CellColors.STANDART,
        ) } }
        setBotOrGameOverScreen(HIDDEN)
        _uiState.update { currentState ->
            currentState.copy(
                cancelMoveButtonEnabled = false,
                gameArray = gameArray,
                currentMove = CellValues.X,
            )
        }
        freeCellsLeft = size * size
        bot.botCannotWin = true
        // making sure array coordinates fit within gameField size
        // that is possibly smaller than in previous game vvv
        iOneMoveBefore = 0
        jOneMoveBefore = 0
        iTwoMovesBefore = 0
        jTwoMovesBefore = 0
    }

    fun makeMove(i: Int, j: Int){
        val gameArray = uiState.value.gameArray
        //gameArray[iTwoMovesBefore][jTwoMovesBefore].textColor = StandartCell
        iTwoMovesBefore = iOneMoveBefore
        jTwoMovesBefore = jOneMoveBefore
        gameArray[iOneMoveBefore][jOneMoveBefore].cellColor = CellColors.STANDART
        gameArray[i][j].cellColor = CellColors.CURRENT
        gameArray[i][j].cellText = uiState.value.currentMove
        gameArray[i][j].isClickable = false
        _uiState.update { a ->
            a.copy(
                gameArray = gameArray,
                cancelMoveButtonEnabled = true
            )
        }
        iOneMoveBefore = i
        jOneMoveBefore = j
        freeCellsLeft--
        checkWin(i, j, uiState.value.currentMove)
        if(uiState.value.botOrGameOverScreen != GAMEOVER) {
            checkDraw()
            if(uiState.value.botOrGameOverScreen != GAMEOVER) {
                changeTurn(uiState.value.currentMove)
            }
        }
    }

    fun makeBotMove(){
        val gameArray = uiState.value.gameArray
        val currentMove = uiState.value.currentMove

        for (i in gameArray.indices){
            for (j in gameArray[i].indices){
                if((gameArray[i][j].cellText == CellValues.EMPTY) && bot.botCannotWin){
                    bot.checkForWinningMove(i, j, currentMove, gameArray, uiState.value.winRow)
                }
            }
        }
        if(bot.botCannotWin) bot.chooseRandomFreeCell(gameArray)
        makeMove(bot.botI, bot.botJ)
        _uiState.update { currentState ->
            currentState.copy(
                cancelMoveButtonEnabled = false
            )
        }
        if(uiState.value.botOrGameOverScreen == BOT){
            setBotOrGameOverScreen(HIDDEN)
        }
    }

    fun cancelMove(){
        val gameArray = uiState.value.gameArray
        if(uiState.value.botOrGameOverScreen == GAMEOVER){
            for(i in gameArray.indices) {
                for(j in gameArray.indices) {
                    gameArray[i][j].cellColor = CellColors.STANDART
                }
            }
        } else changeTurn(uiState.value.currentMove)
        gameArray[iOneMoveBefore][jOneMoveBefore].cellText = CellValues.EMPTY
        gameArray[iOneMoveBefore][jOneMoveBefore].isClickable = true
        gameArray[iTwoMovesBefore][jTwoMovesBefore].cellColor = CellColors.CURRENT
        _uiState.update { currentState ->
            currentState.copy(
                gameArray = gameArray,
                cancelMoveButtonEnabled = false
            )
        }
        iOneMoveBefore = iTwoMovesBefore
        jOneMoveBefore = jTwoMovesBefore
        freeCellsLeft++
        setBotOrGameOverScreen(HIDDEN)
    }

    private fun changeTurn(currentMove: CellValues){
        val updatedTurn = if(currentMove == CellValues.X) CellValues.O else CellValues.X
        _uiState.update { a ->
            a.copy(currentMove = updatedTurn)
        }
    }

    internal fun checkWin(i: Int, j: Int, currentMove: CellValues){
        /* Algorithm is similar to drawAlgorithm:
        from currently clicked cell we are looking forward and backward, in all directions,
        to find same (X or 0) cells, adding those founded to compare to winRow.

        Then (contrary to drawAlgorithm algorithm)
        we have to make sure that AT LEAST ONE of the four directions can win (HAVE enough X or 0 in a row)
         */

        val gameArray = uiState.value.gameArray

        // VERTICAL CHECK forward
        var newI = i
        var currentRow = 1

        while((newI + 1 < gameArray.size) && (gameArray[newI + 1][j].cellText == currentMove)){
            currentRow++
            newI++
        }
        // then backward
        newI = i
        while((newI > 0) && (gameArray[newI - 1][j].cellText == currentMove)){
            currentRow++
            newI--
        }
        // if enough X or 0 in a row -> WIN
        if (currentRow >= uiState.value.winRow) {
            for(a in newI until newI + currentRow) {
                gameArray[a][j].cellColor = CellColors.WIN
            }
            setBotOrGameOverScreen(GAMEOVER)
        }

        // HORIZONTAL CHECK
        var newJ = j
        currentRow = 1

        while((newJ + 1 < gameArray.size) && (gameArray[i][newJ + 1].cellText == currentMove)){
            currentRow++
            newJ++
        }
        newJ = j
        while((newJ > 0) && (gameArray[i][newJ - 1].cellText == currentMove)){
            currentRow++
            newJ--
        }
        if (currentRow >= uiState.value.winRow) {
            for(a in newJ until newJ + currentRow) {
                gameArray[i][a].cellColor = CellColors.WIN
            }
            setBotOrGameOverScreen(GAMEOVER)
        }

        // MAIN DIAGONAL CHECK
        newI = i
        newJ = j
        currentRow = 1

        while((newI + 1 < gameArray.size) && (newJ + 1 < gameArray.size) && (gameArray[newI + 1][newJ + 1].cellText == currentMove)){
            currentRow++
            newI++
            newJ++
        }
        newI = i
        newJ = j
        while((newI > 0) && (newJ > 0) && (gameArray[newI - 1][newJ - 1].cellText == currentMove)){
            currentRow++
            newI--
            newJ--
        }
        if (currentRow >= uiState.value.winRow) {
            for(a in newI until newI + currentRow) {
                gameArray[a][a-newI+newJ].cellColor = CellColors.WIN
            }
            setBotOrGameOverScreen(GAMEOVER)
        }

        // OTHER DIAGONAL CHECK
        newI = i
        newJ = j
        currentRow = 1

        while((newI + 1 < gameArray.size) && (newJ > 0) && (gameArray[newI + 1][newJ - 1].cellText == currentMove)){
            currentRow++
            newI++
            newJ--
        }
        newI = i
        newJ = j
        while((newI > 0) && (newJ + 1 < gameArray.size) && (gameArray[newI - 1][newJ + 1].cellText == currentMove)){
            currentRow++
            newI--
            newJ++
        }
        if (currentRow >= uiState.value.winRow) {
            for(a in newI until newI + currentRow) {
                gameArray[a][newJ-a+newI].cellColor = CellColors.WIN
            }
            setBotOrGameOverScreen(GAMEOVER)
        }
    }

    internal fun checkDraw(){
        winIsImpossible = true
        val gameArray = uiState.value.gameArray
        // checking whether any of the free remaining cells can possibly win
        changeTurn(uiState.value.currentMove)  // for X or 0
        for (i in gameArray.indices){
            for (j in gameArray[i].indices){
                if((gameArray[i][j].cellText == CellValues.EMPTY) && winIsImpossible){
                    drawAlgorithm(i, j, uiState.value.currentMove)
                }
            }
        }
        changeTurn(uiState.value.currentMove)  // for 0 or X
        for (i in gameArray.indices){
            for (j in gameArray[i].indices){
                if((gameArray[i][j].cellText == CellValues.EMPTY) && winIsImpossible){
                    drawAlgorithm(i, j, uiState.value.currentMove)
                }
            }
        }
        // if there are no such cells -> Draw
        if(winIsImpossible) {
            for(i in gameArray.indices) {
                for(j in gameArray.indices) {
                    gameArray[i][j].cellColor = CellColors.DRAW
                }
            }
            setBotOrGameOverScreen(GAMEOVER)
        }
    }

    private fun drawAlgorithm(i: Int, j: Int, currentMove: CellValues){
        /* Algorithm is similar to checkWin:
        FOR EVERY cell we are looking forward and backward, in all directions,
        to find same (X or 0) OR EMPTY cells, adding those founded to compare to winRow.

        Then, if any cell could possibly win - there's no Draw
         */

        val gameArray = uiState.value.gameArray

        // VERTICAL CHECK forward
        var newI = i
        var currentRow = 1

        while((newI + 1 < gameArray.size) && (gameArray[newI + 1][j].cellText != currentMove)){
            currentRow++
            newI++
        }
        // then backward
        newI = i
        while((newI > 0) && (gameArray[newI - 1][j].cellText != currentMove)){
            currentRow++
            newI--
        }
        // if enough ((X or 0) + " ") in a row -> possible Win (no Draw)
        if (currentRow >= uiState.value.winRow) {
            winIsImpossible = false
            return
        }

        // HORIZONTAL CHECK
        var newJ = j
        currentRow = 1

        while((newJ + 1 < gameArray.size) && (gameArray[i][newJ + 1].cellText != currentMove)){
            currentRow++
            newJ++
        }
        newJ = j
        while((newJ > 0) && (gameArray[i][newJ - 1].cellText != currentMove)){
            currentRow++
            newJ--
        }
        if (currentRow >= uiState.value.winRow) {
            winIsImpossible = false
            return
        }

        // MAIN DIAGONAL CHECK
        newI = i
        newJ = j
        currentRow = 1

        while((newI + 1 < gameArray.size) && (newJ + 1 < gameArray.size) && (gameArray[newI + 1][newJ + 1].cellText != currentMove)){
            currentRow++
            newI++
            newJ++
        }
        newI = i
        newJ = j
        while((newI > 0) && (newJ > 0) && (gameArray[newI - 1][newJ - 1].cellText != currentMove)){
            currentRow++
            newI--
            newJ--
        }
        if (currentRow >= uiState.value.winRow) {
            winIsImpossible = false
            return
        }

        // OTHER DIAGONAL CHECK
        newI = i
        newJ = j
        currentRow = 1

        while((newI + 1 < gameArray.size) && (newJ > 0) && (gameArray[newI + 1][newJ - 1].cellText != currentMove)){
            currentRow++
            newI++
            newJ--
        }
        newI = i
        newJ = j
        while((newI > 0) && (newJ + 1 < gameArray.size) && (gameArray[newI - 1][newJ + 1].cellText != currentMove)){
            currentRow++
            newI--
            newJ++
        }
        if (currentRow >= uiState.value.winRow) {
            winIsImpossible = false
            return
        }
    }

}
