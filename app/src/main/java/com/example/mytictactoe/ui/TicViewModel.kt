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
import com.example.mytictactoe.EndOfCheck.*

class TicViewModel: ViewModel() {

    private val _uiState = MutableStateFlow(TicUiState())
    val uiState: StateFlow<TicUiState> = _uiState.asStateFlow()

    private val bot = Bot()

    private var iOneMoveBefore: Int = 0
    private var jOneMoveBefore: Int = 0
    private var iTwoMovesBefore: Int = 0
    private var jTwoMovesBefore: Int = 0
    private var freeCellsLeft: Int = 0
    private var winIsImpossible: Boolean = true

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
            cellColor = CellColors.STANDART_COLOR,
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
        val currentMove = uiState.value.currentMove
        //gameArray[iTwoMovesBefore][jTwoMovesBefore].textColor = StandartCell
        iTwoMovesBefore = iOneMoveBefore
        jTwoMovesBefore = jOneMoveBefore
        gameArray[iOneMoveBefore][jOneMoveBefore].cellColor = CellColors.STANDART_COLOR
        gameArray[i][j].cellColor = CellColors.CURRENT_COLOR
        gameArray[i][j].cellText = currentMove
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
        checkField(WIN, i, j)
        if(uiState.value.botOrGameOverScreen != GAMEOVER) {
            checkDraw()
            if(uiState.value.botOrGameOverScreen != GAMEOVER) {
                changeTurn(currentMove)
            }
        }
    }

    fun makeBotMove(){
        with(bot) {
            setMoveCoordinates(uiState.value.gameArray, ::checkField)
            makeMove(botI, botJ)
            disableCancelButton()
            if (uiState.value.botOrGameOverScreen == BOT) {
                setBotOrGameOverScreen(HIDDEN)
            }
        }
    }

    fun cancelMove(){
        val gameArray = uiState.value.gameArray
        if(uiState.value.botOrGameOverScreen == GAMEOVER){
            for(i in gameArray.indices) {
                for(j in gameArray.indices) {
                    gameArray[i][j].cellColor = CellColors.STANDART_COLOR
                }
            }
        } else changeTurn(uiState.value.currentMove)
        gameArray[iOneMoveBefore][jOneMoveBefore].cellText = CellValues.EMPTY
        gameArray[iOneMoveBefore][jOneMoveBefore].isClickable = true
        gameArray[iTwoMovesBefore][jTwoMovesBefore].cellColor = CellColors.CURRENT_COLOR
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

    fun disableCancelButton(){
        _uiState.update { currentState ->
            currentState.copy(
                cancelMoveButtonEnabled = false
            )
        }
    }

    private fun changeTurn(currentMove: CellValues){
        val updatedTurn = if(currentMove == CellValues.X) CellValues.O else CellValues.X
        _uiState.update { a ->
            a.copy(currentMove = updatedTurn)
        }
    }

    private fun makeWin(
        currentRow: Int,
        from: Int,
        to: Int,
        x: Int?,
        y: Int?,
        z: Int?,
        gameArray: Array<Array<Cell>> = uiState.value.gameArray,
    ){
        // changes color of winning cells to WIN_COLOR
        for(a in from until to + currentRow) {
            gameArray[x ?: a][(y ?: a) + (z ?: -a)].cellColor = CellColors.WIN_COLOR
        }
        setBotOrGameOverScreen(GAMEOVER)
    }

    private fun directionalCheck(
        a: Int,
        b: Int,
        emptyCell: CellValues?,
        gameArray: Array<Array<Cell>> = uiState.value.gameArray,
        currentMove: CellValues = uiState.value.currentMove,
    ): Boolean {
        return ((gameArray[a][b].cellText == currentMove) || (gameArray[a][b].cellText == (emptyCell ?: currentMove)))
    }

    internal fun checkField(
        endOfCheck: EndOfCheck,
        i: Int,
        j: Int,
    ){
        /* Algorithm is similar for checking of Win, Draw, possibleWin on nextMove, etc...
        From currently clicked cell we are looking forward and backward, in all directions,
        to find same (X or 0 [and Empty, when searching for Draw]) cells, adding those founded and comparing them to winRow.

        Then, if enough cells found, endOfCheck chooses the outcome (depending on What we are searching for)
         */

        val gameArray = uiState.value.gameArray

        //in case of DRAW searching for currentMove & EMPTY cells, otherwise only for currentMove cells
        val emptyCell = if(endOfCheck == DRAW) CellValues.EMPTY else null

        // VERTICAL CHECK forward
        var newI = i
        var currentRow = 1

        // searching within the boundaries of array for the currentMove cells (and EMPTY cells in case of DRAW)
        while((newI + 1 < gameArray.size) && directionalCheck(newI + 1, j, emptyCell)){
            currentRow++
            newI++
        }
        // then backward
        newI = i
        while((newI > 0) && directionalCheck(newI - 1, j, emptyCell)){
            currentRow++
            newI--
        }

        if (currentRow >= uiState.value.winRow) {
            when(endOfCheck){
                WIN -> makeWin(currentRow, from = newI, to = newI, x = null, y = j, z = 0)
                DRAW -> { winIsImpossible = false; return }
                ONE_BEFORE_WIN -> bot.chooseCoordinatesIfCanWin(i, j)
            }
        }

        // HORIZONTAL CHECK
        var newJ = j
        currentRow = 1

        while((newJ + 1 < gameArray.size) && directionalCheck(i, newJ + 1, emptyCell)){
            currentRow++
            newJ++
        }
        newJ = j
        while((newJ > 0) && directionalCheck(i, newJ - 1, emptyCell)){
            currentRow++
            newJ--
        }

        if (currentRow >= uiState.value.winRow) {
            when(endOfCheck){
                WIN -> makeWin(currentRow, from = newJ, to = newJ, x = i, y = null, z = 0)
                DRAW -> { winIsImpossible = false; return }
                ONE_BEFORE_WIN -> bot.chooseCoordinatesIfCanWin(i, j)
            }
        }

        // MAIN DIAGONAL CHECK
        newI = i
        newJ = j
        currentRow = 1

        while((newI + 1 < gameArray.size) && (newJ + 1 < gameArray.size) && directionalCheck(newI + 1, newJ + 1, emptyCell)){
            currentRow++
            newI++
            newJ++
        }
        newI = i
        newJ = j
        while((newI > 0) && (newJ > 0) && directionalCheck(newI - 1, newJ - 1, emptyCell)){
            currentRow++
            newI--
            newJ--
        }
        if (currentRow >= uiState.value.winRow) {
            when(endOfCheck){
                WIN -> makeWin(currentRow, from = newI, to = newI, x = null, y = null, z = newJ - newI)
                DRAW -> { winIsImpossible = false; return }
                ONE_BEFORE_WIN -> bot.chooseCoordinatesIfCanWin(i, j)
            }
        }

        // OTHER DIAGONAL CHECK
        newI = i
        newJ = j
        currentRow = 1

        while((newI + 1 < gameArray.size) && (newJ > 0) && directionalCheck(newI + 1, newJ - 1, emptyCell)){
            currentRow++
            newI++
            newJ--
        }
        newI = i
        newJ = j
        while((newI > 0) && (newJ + 1 < gameArray.size) && directionalCheck(newI - 1, newJ + 1, emptyCell)){
            currentRow++
            newI--
            newJ++
        }
        if (currentRow >= uiState.value.winRow) {
            when(endOfCheck){
                WIN -> makeWin(currentRow, from = newI, to = newI, x = null, y = newI + newJ, z = null)
                DRAW -> { winIsImpossible = false; return }
                ONE_BEFORE_WIN -> bot.chooseCoordinatesIfCanWin(i, j)
            }
        }
    }

    internal fun checkDraw(){
        winIsImpossible = true
        val gameArray = uiState.value.gameArray
        // checking whether any of the free remaining cells can possibly win

        for (i in gameArray.indices){  // for other player
            for (j in gameArray[i].indices){
                if((gameArray[i][j].cellText == CellValues.EMPTY) && winIsImpossible){
                    checkField(DRAW, i, j)
                }
            }
        }
        changeTurn(uiState.value.currentMove)
        for (i in gameArray.indices){  // for this player
            for (j in gameArray[i].indices){
                if((gameArray[i][j].cellText == CellValues.EMPTY) && winIsImpossible){
                    checkField(DRAW, i, j)
                }
            }
        }
        changeTurn(uiState.value.currentMove)
        // if there are no such cells -> Draw
        if(winIsImpossible) {
            for(i in gameArray.indices) {
                for(j in gameArray.indices) {
                    gameArray[i][j].cellColor = CellColors.DRAW_COLOR
                }
            }
            setBotOrGameOverScreen(GAMEOVER)
        }
    }
}
