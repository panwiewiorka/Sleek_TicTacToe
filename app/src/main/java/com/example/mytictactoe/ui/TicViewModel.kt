package com.example.mytictactoe.ui

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytictactoe.*
import com.example.mytictactoe.AppTheme.Companion.fromOrdinal
import com.example.mytictactoe.LoadOrSave.*
import com.example.mytictactoe.BotOrGameOverScreen.*
import com.example.mytictactoe.EndOfCheck.*
import com.example.mytictactoe.data.SettingsDao
import com.example.mytictactoe.data.SettingsTable
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.EmptyCoroutineContext

class TicViewModel(
    private val dao: SettingsDao
    ): ViewModel() {

    private val _uiState = MutableStateFlow(TicUiState())
    val uiState: StateFlow<TicUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val settingsTable = SettingsTable(id = 1, theme = AppTheme.AUTO, playingVsAI = false)
            dao.populateSettings(settingsTable)
            _uiState.update { a ->
                a.copy(
                    theme = dao.loadSettings().theme,
                    playingVsAI = dao.loadSettings().playingVsAI
                )
            }
        }
    }

    private var botWaits: Job = CoroutineScope(EmptyCoroutineContext).launch {  }
    private var iOneMoveBefore = 0
    private var jOneMoveBefore = 0
    private var iTwoMovesBefore = 0
    private var jTwoMovesBefore = 0
    private var freeCellsLeft = 9 // TODO check if ALL THOSE VARs should be saved into DB
    private var winIsImpossible = true
    var canChangeBotMove = false

    //--------INTERFACE

    fun changeTheme(systemThemeIsDark: Boolean){
        val themeSign = if(systemThemeIsDark) -1 else 1
        val themeOrdinal = when(uiState.value.theme){
            AppTheme.LIGHT -> 0
            AppTheme.AUTO -> 1
            AppTheme.DARK -> 2
        }
        var ordinal = themeOrdinal + themeSign
        if(ordinal > 2) {ordinal = 0}
        if(ordinal < 0) {ordinal = 2}
        val settingsTable = SettingsTable(id = 1, theme = fromOrdinal(ordinal), playingVsAI = uiState.value.playingVsAI)
        viewModelScope.launch {dao.saveSettings(settingsTable)}
        _uiState.update { a ->
            a.copy(
                theme = fromOrdinal(ordinal)
            )
        }
    }

    fun showMenu(show: Boolean){
        _uiState.update { a ->
            a.copy(
                menuIsVisible = show,
            )
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
            if(uiState.value.playingVsAI && (uiState.value.botOrGameOverScreen != GAMEOVER) &&
                (freeCellsLeft != (uiState.value.gameArray.size * uiState.value.gameArray.size))){
                canChangeBotMove = true
                changeBotMove()
            }
            canChangeBotMove = false
            resetGame(size)
        }
    }

    fun shakeMenuButton(shake: Boolean){
        _uiState.update { a ->
            a.copy(
                menuButtonShouldBeShaken = shake
            )
        }
    }

    private fun setCellFontSize(gameFieldSize: Int) {
        // changing cellFontSize depending on gameFieldSize
        // before unnecessary recomposition possible by AutoResizedText() composable
        // TODO: remember resized cell size and use it for all cells?
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

    fun changeWinOrLose(){
        _uiState.update { a ->
            a.copy(
                winNotLose = !uiState.value.winNotLose
            )
        }
    }

    fun setWinRow(slider: Float){
        // rounding to the nearest int, not necessarily to the lowest
        val winRow = (slider + 0.5).toInt()

        // recomposing only on discrete value changes
        if(winRow != uiState.value.winRow){
            canChangeBotMove = false
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
        if(
            (uiState.value.savedWinRow != uiState.value.winRow) &&
            (freeCellsLeft != (uiState.value.gameArray.size * uiState.value.gameArray.size))){
            shakeMenuButton(true)
            _uiState.update { a ->
                a.copy(
                    winRow = uiState.value.savedWinRow
                )
            }
        }
    }

    fun switchPlayingVsAiMode(){
        val settingsTable = SettingsTable(id = 1, theme = uiState.value.theme, playingVsAI = !uiState.value.playingVsAI)
        viewModelScope.launch {dao.saveSettings(settingsTable)}
        _uiState.update { a ->
            a.copy(
                playingVsAI = !uiState.value.playingVsAI
            )
        }
    }

    fun resetCurrentMoveToX(){
        _uiState.update { a ->
            a.copy(
                currentMove = CellValues.X
            )
        }
    }

    private fun setBotOrGameOverScreen(state: BotOrGameOverScreen){
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
        _uiState.update { a ->
            a.copy(
                cancelMoveButtonEnabled = false,
                gameArray = gameArray,
                currentMove = CellValues.X,
            )
        }
        freeCellsLeft = size * size
        Bot.botCannotWin = true
        // making sure array coordinates fit within gameField array
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
        gameArray[iOneMoveBefore][jOneMoveBefore].cellColor = CellColors.STANDART_COLOR
        gameArray[i][j].cellColor = CellColors.CURRENT_COLOR
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
        checkField(WIN, i, j)
        if(uiState.value.botOrGameOverScreen != GAMEOVER) {
            checkDraw()
            if(uiState.value.botOrGameOverScreen != GAMEOVER) {
                changeTurn()
            }
        }
    }

    fun makeBotMove(){
        with(Bot) {
            if(uiState.value.playingVsAI && (uiState.value.currentMove == uiState.value.aiMove) &&
                ((uiState.value.botOrGameOverScreen != GAMEOVER))) {
                setBotOrGameOverScreen(BOT)
                botWaits = CoroutineScope(EmptyCoroutineContext).launch(Dispatchers.Default) {
                    val waitTime = (500L..2000L).random()
                    delay(waitTime)
                    setMoveCoordinates(uiState.value.winRow, uiState.value.gameArray, ::changeTurn, ::checkField)
                    makeMove(botI, botJ)
                    _uiState.update { currentState ->
                        currentState.copy(
                            cancelMoveButtonEnabled = false
                        )
                    }
                    if (uiState.value.botOrGameOverScreen == BOT) {
                        setBotOrGameOverScreen(HIDDEN)
                    }
                }
            }
        }
    }

    fun changeBotMove(){
        if(uiState.value.playingVsAI && canChangeBotMove){
            _uiState.update { a ->
                a.copy(
                    aiMove = if(uiState.value.aiMove == CellValues.O) CellValues.X else CellValues.O
                )
            }
        }
    }

    fun cancelBotWait(){
        if(uiState.value.playingVsAI && botWaits.isActive) {
            botWaits.cancel()
            setBotOrGameOverScreen(HIDDEN)
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
        } else changeTurn()
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

    private fun changeTurn(){
        val updatedTurn = if(uiState.value.currentMove == CellValues.X) CellValues.O else CellValues.X
        _uiState.update { a ->
            a.copy(currentMove = updatedTurn)
        }
    }


    private fun directionalCheck(
        endOfCheck: EndOfCheck,
        a: Int,
        b: Int,
    ): Boolean {
        //in case of DRAW - searching for currentMove & EMPTY cells, otherwise - only for currentMove cells (currentMove || currentMove)
        val emptyOrCurrentMoveCell = if(endOfCheck == DRAW) CellValues.EMPTY else uiState.value.currentMove
        //val vOrCurrentMoveCell = if(endOfCheck == DRAW) CellValues.V else uiState.value.currentMove

        return ((uiState.value.gameArray[a][b].cellText == uiState.value.currentMove) ||
                (uiState.value.gameArray[a][b].cellText == (emptyOrCurrentMoveCell))
                //|| (uiState.value.gameArray[a][b].cellText == (vOrCurrentMoveCell))
                )
    }

    private fun checkVertically(
        endOfCheck: EndOfCheck,
        i: Int,
        j: Int,
        winNotLose: Boolean,
        ){
        val gameArray = uiState.value.gameArray
        // check forward
        var newI = i
        var currentRow = 1
        var winInTwoMoves = 0

        // searching FORWARD within the boundaries of array for the currentMove cells (and EMPTY cells in case of DRAW)
        while((newI + 1 < gameArray.size) && directionalCheck(endOfCheck, newI + 1, j)){
            currentRow++
            newI++
        }

        // Bot special check for player's possible win in two moves
        if((endOfCheck == TWO_BEFORE_PLAYER_WIN) && (newI + 1 < gameArray.size) &&
            (uiState.value.gameArray[newI + 1][j].cellText == CellValues.EMPTY)){
            winInTwoMoves++
        }

        // then BACKWARD
        newI = i
        while((newI > 0) && directionalCheck(endOfCheck, newI - 1, j)){
            currentRow++
            newI--
        }

        // Bot special check for player's possible win in two moves
        if((endOfCheck == TWO_BEFORE_PLAYER_WIN) && (newI > 0) &&
            (uiState.value.gameArray[newI - 1][j].cellText == CellValues.EMPTY)){
            winInTwoMoves++
        }

        // decision
        if (currentRow >= uiState.value.winRow) {
            when(endOfCheck){
                WIN -> {
                    for(a in newI until newI + currentRow) {
                        gameArray[a][j].cellColor = if(uiState.value.winNotLose) CellColors.WIN_COLOR else CellColors.LOSE_COLOR
                    }
                    setBotOrGameOverScreen(GAMEOVER)
                }
                DRAW -> { winIsImpossible = false}
                ONE_BEFORE_BOT_WIN -> Bot.chooseCoordinatesIfCanWin(i, j, winNotLose, gameArray)
                ONE_BEFORE_PLAYER_WIN -> Bot.chooseCoordinatesIfCanLose(i, j, winNotLose, gameArray)
                TWO_BEFORE_PLAYER_WIN -> return // case never reached
            }
        }
        if((endOfCheck == TWO_BEFORE_PLAYER_WIN) && (currentRow == uiState.value.winRow - 1) && (winInTwoMoves == 2)){
            Bot.chooseCoordinatesIfCanLose(i, j, winNotLose, gameArray)
        }
    }

    private fun checkHorizontally(
        endOfCheck: EndOfCheck,
        i: Int,
        j: Int,
        winNotLose: Boolean,
    ){
        val gameArray = uiState.value.gameArray

        var newJ = j
        var currentRow = 1
        var winInTwoMoves = 0

        while((newJ + 1 < gameArray.size) && directionalCheck(endOfCheck, i, newJ + 1)){
            currentRow++
            newJ++
        }

        if((endOfCheck == TWO_BEFORE_PLAYER_WIN) && (newJ + 1 < gameArray.size) &&
            (uiState.value.gameArray[i][newJ + 1].cellText == CellValues.EMPTY)){
            winInTwoMoves++
        }

        newJ = j
        while((newJ > 0) && directionalCheck(endOfCheck, i, newJ - 1)){
            currentRow++
            newJ--
        }

        if((endOfCheck == TWO_BEFORE_PLAYER_WIN) && (newJ > 0) &&
            (uiState.value.gameArray[i][newJ - 1].cellText == CellValues.EMPTY)){
            winInTwoMoves++
        }

        if (currentRow >= uiState.value.winRow) {
            when(endOfCheck){
                WIN -> {
                    for(a in newJ until newJ + currentRow) {
                        gameArray[i][a].cellColor = if(uiState.value.winNotLose) CellColors.WIN_COLOR else CellColors.LOSE_COLOR
                    }
                    setBotOrGameOverScreen(GAMEOVER)
                }
                DRAW -> { winIsImpossible = false}
                ONE_BEFORE_BOT_WIN -> Bot.chooseCoordinatesIfCanWin(i, j, winNotLose, gameArray)
                ONE_BEFORE_PLAYER_WIN -> Bot.chooseCoordinatesIfCanLose(i, j, winNotLose, gameArray)
                TWO_BEFORE_PLAYER_WIN -> return // case never reached
            }
        }
        if((endOfCheck == TWO_BEFORE_PLAYER_WIN) && (currentRow == uiState.value.winRow - 1) && (winInTwoMoves == 2)){
            Bot.chooseCoordinatesIfCanLose(i, j, winNotLose, gameArray)
        }
    }

    private fun checkMainDiagonal(
        endOfCheck: EndOfCheck,
        i: Int,
        j: Int,
        winNotLose: Boolean,
        ){
        val gameArray = uiState.value.gameArray

        var newI = i
        var newJ = j
        var currentRow = 1
        var winInTwoMoves = 0

        while((newI + 1 < gameArray.size) && (newJ + 1 < gameArray.size) && directionalCheck(endOfCheck, newI + 1, newJ + 1)){
            currentRow++
            newI++
            newJ++
        }

        if((endOfCheck == TWO_BEFORE_PLAYER_WIN) && (newI + 1 < gameArray.size) && (newJ + 1 < gameArray.size) &&
            (uiState.value.gameArray[newI + 1][newJ + 1].cellText == CellValues.EMPTY)){
            winInTwoMoves++
        }

        newI = i
        newJ = j
        while((newI > 0) && (newJ > 0) && directionalCheck(endOfCheck, newI - 1, newJ - 1)){
            currentRow++
            newI--
            newJ--
        }

        if((endOfCheck == TWO_BEFORE_PLAYER_WIN) && (newI > 0) && (newJ > 0) &&
            (uiState.value.gameArray[newI - 1][newJ - 1].cellText == CellValues.EMPTY)){
            winInTwoMoves++
        }

        if (currentRow >= uiState.value.winRow) {
            when(endOfCheck){
                WIN -> {
                    for(a in newI until newI + currentRow) {
                        gameArray[a][a-newI+newJ].cellColor = if(uiState.value.winNotLose) CellColors.WIN_COLOR else CellColors.LOSE_COLOR
                    }
                    setBotOrGameOverScreen(GAMEOVER)
                }
                DRAW -> { winIsImpossible = false}
                ONE_BEFORE_BOT_WIN -> Bot.chooseCoordinatesIfCanWin(i, j, winNotLose, gameArray)
                ONE_BEFORE_PLAYER_WIN -> Bot.chooseCoordinatesIfCanLose(i, j, winNotLose, gameArray)
                TWO_BEFORE_PLAYER_WIN -> return // case never reached
            }
        }
        if((endOfCheck == TWO_BEFORE_PLAYER_WIN) && (currentRow == uiState.value.winRow - 1) && (winInTwoMoves == 2)){
            Bot.chooseCoordinatesIfCanLose(i, j, winNotLose, gameArray)
        }
    }

    private fun checkOtherDiagonal(
        endOfCheck: EndOfCheck,
        i: Int,
        j: Int,
        winNotLose: Boolean,
        ){
        val gameArray = uiState.value.gameArray

        var newI = i
        var newJ = j
        var currentRow = 1
        var winInTwoMoves = 0

        while((newI + 1 < gameArray.size) && (newJ > 0) && directionalCheck(endOfCheck, newI + 1, newJ - 1)){
            currentRow++
            newI++
            newJ--
        }

        if((endOfCheck == TWO_BEFORE_PLAYER_WIN) && (newI + 1 < gameArray.size) && (newJ > 0) &&
            (uiState.value.gameArray[newI + 1][newJ - 1].cellText == CellValues.EMPTY)){
            winInTwoMoves++
        }

        newI = i
        newJ = j
        while((newI > 0) && (newJ + 1 < gameArray.size) && directionalCheck(endOfCheck, newI - 1, newJ + 1)){
            currentRow++
            newI--
            newJ++
        }

        if((endOfCheck == TWO_BEFORE_PLAYER_WIN) && (newI > 0) && (newJ + 1 < gameArray.size) &&
            (uiState.value.gameArray[newI - 1][newJ + 1].cellText == CellValues.EMPTY)){
            winInTwoMoves++
        }

        if (currentRow >= uiState.value.winRow) {
            when(endOfCheck){
                WIN -> {
                    for(a in newI until newI + currentRow) {
                        gameArray[a][newJ-a+newI].cellColor = if(uiState.value.winNotLose) CellColors.WIN_COLOR else CellColors.LOSE_COLOR
                    }
                    setBotOrGameOverScreen(GAMEOVER)
                }
                DRAW -> { winIsImpossible = false}
                ONE_BEFORE_BOT_WIN -> Bot.chooseCoordinatesIfCanWin(i, j, winNotLose, gameArray)
                ONE_BEFORE_PLAYER_WIN -> Bot.chooseCoordinatesIfCanLose(i, j, winNotLose, gameArray)
                TWO_BEFORE_PLAYER_WIN -> return // case never reached
            }
        }
        if((endOfCheck == TWO_BEFORE_PLAYER_WIN) && (currentRow == uiState.value.winRow - 1) && (winInTwoMoves == 2)){
            Bot.chooseCoordinatesIfCanLose(i, j, winNotLose, gameArray)
        }
    }

    internal fun checkField(
        endOfCheck: EndOfCheck,
        i: Int,
        j: Int,
        winNotLose: Boolean = uiState.value.winNotLose,
    ){
        /* This is a multi-purpose algorithm for checking of Win, Draw, possible Win in one move, two moves...
        From currently clicked cell we are looking forward and backward, in all directions,
        to find same (X or 0 [and Empty, when searching for Draw]) cells, adding those founded and comparing them to winRow.

        Then, if enough cells found, endOfCheck chooses the outcome (depending on What we are searching for)
         */

        checkVertically(endOfCheck, i, j, winNotLose)
        checkHorizontally(endOfCheck, i, j, winNotLose)
        checkMainDiagonal(endOfCheck, i, j, winNotLose)
        checkOtherDiagonal(endOfCheck, i, j, winNotLose)
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
        changeTurn()
        for (i in gameArray.indices){  // for this player
            for (j in gameArray[i].indices){
                if((gameArray[i][j].cellText == CellValues.EMPTY) && winIsImpossible){
                    checkField(DRAW, i, j)
                }
            }
        }
        changeTurn()
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


