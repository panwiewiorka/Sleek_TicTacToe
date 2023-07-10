package com.example.mytictactoe.ui

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytictactoe.*
import com.example.mytictactoe.AppTheme.Companion.fromOrdinal
import com.example.mytictactoe.LoadOrSave.*
import com.example.mytictactoe.BotOrGameOverScreen.*
import com.example.mytictactoe.EndOfCheck.*
import com.example.mytictactoe.data.GameFieldTable
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

    private var botWaits: Job = CoroutineScope(EmptyCoroutineContext).launch {  }
    var iOneMoveBefore = 0
    var jOneMoveBefore = 0
    private var iTwoMovesBefore = 0
    private var jTwoMovesBefore = 0
    var freeCellsLeft = 9
    private var winIsImpossible = true
    private var canChangeFirstMove = false
    private var savedMove = CustomCellValues.player1
    private var savedPlayingVsAi = false

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val settingsTable = SettingsTable()
            dao.populateSettings(settingsTable)
            val gameFieldTable = GameFieldTable()
            repeat(9) { i: Int ->
                gameFieldTable.id = i + 1
                dao.populateGameField(gameFieldTable)
            }

            _uiState.update { a ->
                a.copy(
                    theme = dao.loadSettings().theme,
                    memorySettings = dao.loadSettings().memorySettings,
                    menuIsVisible = dao.loadSettings().menuIsVisible,
                    fieldSize = dao.loadSettings().fieldSize.dp,
                    winRow = dao.loadSettings().winRow,
                    savedWinRow = dao.loadSettings().savedWinRow,
                    winNotLose = dao.loadSettings().winNotLose,
                    savedWinNotLose = dao.loadSettings().savedWinNotLose,
                    playingVsAI = dao.loadSettings().playingVsAI,
                    firstMove = dao.loadSettings().firstMove,
                    currentMove = dao.loadSettings().currentMove,
                    cancelMoveButtonEnabled = dao.loadSettings().cancelMoveButtonEnabled,
                    botOrGameOverScreen = dao.loadSettings().botOrGameOverScreen,
                    gameArray = Array(dao.loadSettings().arraySize) { i ->
                        Array(dao.loadSettings().arraySize) { j ->
                            Cell(
                                isClickable = true,
                                cellText = CustomCellValues.EMPTY,
                                cellColor = CellColors.STANDART_COLOR,
                            )
                        }
                    }
                )
            }
            iOneMoveBefore = dao.loadSettings().iOneMoveBefore
            jOneMoveBefore = dao.loadSettings().jOneMoveBefore
            iTwoMovesBefore = dao.loadSettings().iTwoMovesBefore
            jTwoMovesBefore = dao.loadSettings().jTwoMovesBefore
            freeCellsLeft = dao.loadSettings().freeCellsLeft
            winIsImpossible = dao.loadSettings().winIsImpossible
            canChangeFirstMove = dao.loadSettings().canChangeFirstMove
            savedMove = dao.loadSettings().savedMove
            savedPlayingVsAi = dao.loadSettings().savedPlayingVsAi
            CustomCellValues.player1 = dao.loadSettings().player1symbol
            CustomCellValues.player2 = dao.loadSettings().player2symbol

            val gameArray = uiState.value.gameArray
            for(i in gameArray.indices) {
                for(j in gameArray.indices) {
                    gameArray[i][j].cellText = dao.loadGameField((i * gameArray.size) + j + 1).cellText
                    gameArray[i][j].isClickable = dao.loadGameField((i * gameArray.size) + j + 1).isClickable
                    gameArray[i][j].cellColor = dao.loadGameField((i * gameArray.size) + j + 1).cellColor
                }
            }
        }
    }

    fun saveSettingsToDatabase(){
        runBlocking {
            dao.saveSettings(
                SettingsTable(
                    id = 1,
                    theme = uiState.value.theme,
                    memorySettings = LOAD,
                    menuIsVisible = uiState.value.menuIsVisible,
                    fieldSize = uiState.value.fieldSize.value.toInt(),
                    arraySize = uiState.value.gameArray.size,
                    winRow = uiState.value.winRow,
//                    winRow = if(uiState.value.winRow > uiState.value.gameArray.size) uiState.value.gameArray.size else uiState.value.winRow,
                    savedWinRow = uiState.value.savedWinRow,
                    winNotLose = uiState.value.winNotLose,
                    savedWinNotLose = uiState.value.savedWinNotLose,
                    playingVsAI = uiState.value.playingVsAI,
                    firstMove = uiState.value.firstMove,
                    currentMove = uiState.value.currentMove,
                    cancelMoveButtonEnabled = uiState.value.cancelMoveButtonEnabled,
                    botOrGameOverScreen = uiState.value.botOrGameOverScreen,
                    iOneMoveBefore = iOneMoveBefore,
                    jOneMoveBefore = jOneMoveBefore,
                    iTwoMovesBefore = iTwoMovesBefore,
                    jTwoMovesBefore = jTwoMovesBefore,
                    freeCellsLeft = freeCellsLeft,
                    winIsImpossible = winIsImpossible,
                    canChangeFirstMove = canChangeFirstMove,
                    savedMove = savedMove,
                    savedPlayingVsAi = savedPlayingVsAi,
                    player1symbol = CustomCellValues.player1,
                    player2symbol = CustomCellValues.player2,
                )
            )
        }
    }

    fun saveGameFieldToDatabase(){
        runBlocking {
            for (i in uiState.value.gameArray.flatten().mapIndexed{index, value -> index to value}.toMap()){
                dao.saveGameField(
                    GameFieldTable(
                        id = i.key + 1,
                        cellText = i.value.cellText,
                        isClickable = i.value.isClickable,
                        cellColor = i.value.cellColor)
                )
            }
        }
    }

    // TalkBack accessibility
    fun convertIndexToLetter(i: Int): Char{
        val a = 'A'
        return a + uiState.value.gameArray.size - i - 1
    }

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
            canChangeFirstMove = false
            if((uiState.value.botOrGameOverScreen != GAMEOVER) &&
                (freeCellsLeft != (uiState.value.gameArray.size * uiState.value.gameArray.size))){
                canChangeFirstMove = true
            }
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

    fun showWinOrLoseIndication(show: Boolean){
        _uiState.update { a ->
            a.copy(
                winOrLoseShouldBeShown = show
            )
        }
    }

    fun showCustomCellDialog(showDialog: Boolean){
        _uiState.update { a ->
            a.copy(
                customCellDialogIsVisible = showDialog
            )
        }
    }

    fun changeCellSymbol(cellSymbol: Char){
        val gameArray = uiState.value.gameArray

        for (i in gameArray.indices){
            for(j in gameArray[i].indices){
                if(gameArray[i][j].cellText == uiState.value.currentMove){
                    gameArray[i][j].cellText = cellSymbol
                }
            }
        }

        if(uiState.value.currentMove == CustomCellValues.player1){
            CustomCellValues.player1 = cellSymbol
        } else {
            CustomCellValues.player2 = cellSymbol
        }

        if(uiState.value.firstMove == uiState.value.currentMove) {
            _uiState.update { a ->
                a.copy(
                    firstMove = cellSymbol,
                )
            }
        }

        _uiState.update { a ->
            a.copy(
                currentMove = cellSymbol,
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
            canChangeFirstMove = false
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

    fun changeWinOrLose(){
        _uiState.update { a ->
            a.copy(
                winNotLose = !uiState.value.winNotLose
            )
        }
    }

    fun saveWinOrLose(){
        _uiState.update { a ->
            a.copy(
                savedWinNotLose = uiState.value.winNotLose
            )
        }
    }

    fun cancelWinOrLoseChangesDuringTheGame(){
        if((uiState.value.savedWinNotLose != uiState.value.winNotLose) &&
            (freeCellsLeft != (uiState.value.gameArray.size * uiState.value.gameArray.size))){
            shakeMenuButton(true)
            showWinOrLoseIndication(true)
            _uiState.update { a ->
                a.copy(
                    winNotLose = uiState.value.savedWinNotLose
                )
            }
        }
    }

    fun switchPlayingVsAiMode(){
        _uiState.update { a ->
            a.copy(
                playingVsAI = !uiState.value.playingVsAI
            )
        }
    }

    fun savePlayingVsAi(){
        savedPlayingVsAi = uiState.value.playingVsAI
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

    fun saveOrLoadCurrentMove(saveOrLoad: LoadOrSave){
        if(saveOrLoad == SAVE){
            savedMove = uiState.value.currentMove
        } else {
            _uiState.update { a ->
                a.copy(
                    currentMove = savedMove
                )
            }
        }
    }

    fun allowChangingFirstMove(allow: Boolean){
        canChangeFirstMove = allow
    }

    fun changeFirstMoveOnMenuShowing(){
        if(uiState.value.menuIsVisible && (uiState.value.botOrGameOverScreen == GAMEOVER)){
            allowChangingFirstMove(true)
            changeFirstMove()
            allowChangingFirstMove(false)
        }
    }

    //----------GAMEPlAY

    fun resetGame(size: Int){
        val gameArray = Array(size) { Array(size) { Cell(
            isClickable = true,
            cellText = CustomCellValues.EMPTY,
            cellColor = CellColors.STANDART_COLOR,
        ) } }
        setBotOrGameOverScreen(HIDDEN)
        _uiState.update { a ->
            a.copy(
                cancelMoveButtonEnabled = false,
                gameArray = gameArray,
            )
        }
        if(
            uiState.value.winNotLose == uiState.value.savedWinNotLose
            && uiState.value.playingVsAI == savedPlayingVsAi
        ) changeFirstMove()
        allowChangingFirstMove(false)
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
            if(uiState.value.playingVsAI && (uiState.value.currentMove == CustomCellValues.player2) &&
                (uiState.value.botOrGameOverScreen == HIDDEN)) {
                setBotOrGameOverScreen(BOT)
                setMoveCoordinates(uiState.value.winRow, uiState.value.gameArray, ::changeTurn, ::checkField)
                botWaits = CoroutineScope(EmptyCoroutineContext).launch(Dispatchers.Default) {
                    val waitTime = (500L..2000L).random()
                    delay(waitTime)
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
        gameArray[iOneMoveBefore][jOneMoveBefore].cellText = CustomCellValues.EMPTY
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
        val updatedTurn = if(uiState.value.currentMove == CustomCellValues.player1) CustomCellValues.player2 else CustomCellValues.player1
        _uiState.update { a ->
            a.copy(currentMove = updatedTurn)
        }
    }

    private fun changeFirstMove(){
        if(canChangeFirstMove){
            val move = if(uiState.value.firstMove == CustomCellValues.player1) CustomCellValues.player2 else CustomCellValues.player1
            _uiState.update { a ->
                a.copy(
                    firstMove = move,
                    currentMove = move
                )
            }
            allowChangingFirstMove(false)
        }
    }


    private fun directionalCheck(
        endOfCheck: EndOfCheck,
        a: Int,
        b: Int,
    ): Boolean {
        //in case of DRAW - searching for currentMove & EMPTY cells, otherwise - only for currentMove cells (currentMove || currentMove)
        val emptyOrCurrentMoveCell = if(endOfCheck == DRAW) CustomCellValues.EMPTY else uiState.value.currentMove

        return ((uiState.value.gameArray[a][b].cellText == uiState.value.currentMove) ||
                (uiState.value.gameArray[a][b].cellText == (emptyOrCurrentMoveCell)))
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
            (uiState.value.gameArray[newI + 1][j].cellText == CustomCellValues.EMPTY)){
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
            (uiState.value.gameArray[newI - 1][j].cellText == CustomCellValues.EMPTY)){
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
                    allowChangingFirstMove(true)
                }
                DRAW -> { winIsImpossible = false }
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
            (uiState.value.gameArray[i][newJ + 1].cellText == CustomCellValues.EMPTY)){
            winInTwoMoves++
        }

        newJ = j
        while((newJ > 0) && directionalCheck(endOfCheck, i, newJ - 1)){
            currentRow++
            newJ--
        }

        if((endOfCheck == TWO_BEFORE_PLAYER_WIN) && (newJ > 0) &&
            (uiState.value.gameArray[i][newJ - 1].cellText == CustomCellValues.EMPTY)){
            winInTwoMoves++
        }

        if (currentRow >= uiState.value.winRow) {
            when(endOfCheck){
                WIN -> {
                    for(a in newJ until newJ + currentRow) {
                        gameArray[i][a].cellColor = if(uiState.value.winNotLose) CellColors.WIN_COLOR else CellColors.LOSE_COLOR
                    }
                    setBotOrGameOverScreen(GAMEOVER)
                    allowChangingFirstMove(true)
                }
                DRAW -> { winIsImpossible = false }
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
            (uiState.value.gameArray[newI + 1][newJ + 1].cellText == CustomCellValues.EMPTY)){
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
            (uiState.value.gameArray[newI - 1][newJ - 1].cellText == CustomCellValues.EMPTY)){
            winInTwoMoves++
        }

        if (currentRow >= uiState.value.winRow) {
            when(endOfCheck){
                WIN -> {
                    for(a in newI until newI + currentRow) {
                        gameArray[a][a-newI+newJ].cellColor = if(uiState.value.winNotLose) CellColors.WIN_COLOR else CellColors.LOSE_COLOR
                    }
                    setBotOrGameOverScreen(GAMEOVER)
                    allowChangingFirstMove(true)
                }
                DRAW -> { winIsImpossible = false }
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
            (uiState.value.gameArray[newI + 1][newJ - 1].cellText == CustomCellValues.EMPTY)){
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
            (uiState.value.gameArray[newI - 1][newJ + 1].cellText == CustomCellValues.EMPTY)){
            winInTwoMoves++
        }

        if (currentRow >= uiState.value.winRow) {
            when(endOfCheck){
                WIN -> {
                    for(a in newI until newI + currentRow) {
                        gameArray[a][newJ-a+newI].cellColor = if(uiState.value.winNotLose) CellColors.WIN_COLOR else CellColors.LOSE_COLOR
                    }
                    setBotOrGameOverScreen(GAMEOVER)
                    allowChangingFirstMove(true)
                }
                DRAW -> { winIsImpossible = false }
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

        if(freeCellsLeft > 1) {
            for (i in gameArray.indices) {  // for other player
                for (j in gameArray[i].indices) {
                    if ((gameArray[i][j].cellText == CustomCellValues.EMPTY) && winIsImpossible) {
                        checkField(DRAW, i, j)
                    }
                }
            }
        }
        changeTurn()
        for (i in gameArray.indices) {  // for this player
            for (j in gameArray[i].indices) {
                if ((gameArray[i][j].cellText == CustomCellValues.EMPTY) && winIsImpossible) {
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
            allowChangingFirstMove(true)
        }
    }

}
