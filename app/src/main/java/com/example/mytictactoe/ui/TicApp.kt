package com.example.mytictactoe.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytictactoe.*
import com.example.mytictactoe.AutoResizeLimit.*
import com.example.mytictactoe.LoadOrSave.*
import com.example.mytictactoe.Orientation.*
import com.example.mytictactoe.R
import com.example.mytictactoe.ui.theme.MyTicTacToeTheme
import com.example.mytictactoe.ui.theme.menuBorder
import kotlinx.coroutines.*


@Composable
fun TicApp(
    ticViewModel: TicViewModel = viewModel()
) {
    val ticUiState by ticViewModel.uiState.collectAsState()

    //----------------------------popup MENU SCREEN
    if (ticUiState.menuIsVisible){
        AlertDialog(
            onDismissRequest = {
                ticViewModel.showMenu(false)
                ticViewModel.cancelWinRowChangesDuringTheGame() },
            buttons = {
                MainMenu (
                    size = ticUiState.gameArray.size,
                    winRow = ticUiState.winRow,
                    loadMemorySettings = ticUiState.memorySettings.loadOrSave,
                    playingVsAI = ticUiState.playingVsAI,
                    switchGameMode = {ticViewModel.switchGameMode(ticUiState.playingVsAI)},
                )
            },
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .testTag("Menu Window")
                .widthIn(220.dp, 300.dp)
                .border(
                    width = 3.dp,
                    color = MaterialTheme.colors.menuBorder,
                    shape = RoundedCornerShape(15.dp)
                ),
        )
    }

    //-------------------------------------MAIN SCREEN
    BoxWithConstraints(contentAlignment = Alignment.Center) {
        val orientation = if(maxWidth > maxHeight) LANDSCAPE else PORTRAIT
        ticViewModel.rememberSettingsDuringOrientationChange(orientation)

        if(orientation == PORTRAIT) {

            GameField(
                vertPadding = 50.dp,
                horPadding = 0.dp,
                cellFontSize = ticUiState.cellFontSize,
                gameArray = ticUiState.gameArray,
                gameOverScreenVisible = ticUiState.gameOverScreenVisible,
            )

            //-----------------------VERTICAL LAYOUT: TOP BAR with BUTTONS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceAround
            ) {

                //---------------------------button  <
                CancelButton(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("Cancel Button"),
                    cancelMoveButtonEnabled = ticUiState.cancelMoveButtonEnabled,
                    cancelMove = {ticViewModel.cancelMove()},
                    paddingStart = 10.dp,
                )

                //---------------------------icon  XO
                XOButton(
                    modifier = Modifier.weight(1f),
                    currentMove = ticUiState.currentMove,
                    paddingTop = 8.dp,
                    paddingBottom = 10.dp,
                    makeBotMove = {ticViewModel.makeBotMove()}
                )

                //---------------------------button  []
                MenuButton(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("Menu Button"),
                    menuIsVisible = ticUiState.menuIsVisible,
                    winRow = ticUiState.winRow,
                    saveWinRow = {ticViewModel.saveWinRow()},
                    showMenuDialog = {ticViewModel.showMenu(!ticUiState.menuIsVisible)},
                )
            }
        } else {

            GameField(
                vertPadding = 0.dp,
                horPadding = 50.dp,
                cellFontSize = ticUiState.cellFontSize,
                gameArray = ticUiState.gameArray,
                gameOverScreenVisible = ticUiState.gameOverScreenVisible,
            )

            //_______________________HORIZONTAL LAYOUT: LEFT BAR with BUTTONS
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterStart),
                verticalArrangement = Arrangement.SpaceAround
            ) {

                //---------------------------button  []
                MenuButton(
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 18.dp)
                        .testTag("Menu Button"),
                    menuIsVisible = ticUiState.menuIsVisible,
                    winRow = ticUiState.winRow,
                    saveWinRow = {ticViewModel.saveWinRow()},
                    showMenuDialog = {ticViewModel.showMenu(!ticUiState.menuIsVisible)},
                )

                //---------------------------icon  XO
                XOButton(
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 24.dp),
                    currentMove = ticUiState.currentMove,
                    paddingStart = 15.dp,
                    makeBotMove = {ticViewModel.makeBotMove()}
                )

                //---------------------------button  <
                CancelButton(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("Cancel Button"),
                    cancelMoveButtonEnabled = ticUiState.cancelMoveButtonEnabled,
                    cancelMove = {ticViewModel.cancelMove()},
                    paddingStart = 10.dp,
                    paddingBoxBottom = 34.dp,
                )
            }
        }
    }
}


//****************************************************************************

@Composable
fun MainMenu(
    ticViewModel: TicViewModel = viewModel(),
    size: Int,
    winRow: Int,
    loadMemorySettings: Boolean,
    playingVsAI: Boolean,
    switchGameMode: (Boolean) -> Unit,
){
    var sizeSliderPosition by remember { mutableStateOf(3f) }
    var winRowSliderPosition by remember { mutableStateOf(3f) }
    var winRowUpperLimit by remember { mutableStateOf(3f) }
    var winRowSteps by remember { mutableStateOf(0) }
    // ^^ by remember { mutableStateOf() }   made for sliders local operation.
    // Settings are saved in UiState.
    if(loadMemorySettings){
        sizeSliderPosition = size.toFloat()
        winRowSliderPosition = winRow.toFloat()
        winRowUpperLimit = sizeSliderPosition
        winRowSteps = if(sizeSliderPosition > 3){ sizeSliderPosition.toInt() - 4 } else 0
        ticViewModel.setMenuSettings(SAVE)
    }
    Column(
        modifier = Modifier.padding(25.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.heightIn(10.dp, 60.dp)
        ){
            AutoResizedText(
                text = "Board size: ${(sizeSliderPosition + 0.5).toInt()}",
                style = MaterialTheme.typography.h5,
                autoResizeLimit = HEIGHT,
                modifier = Modifier.testTag("Board Size"),
            )
        }
        Slider(
            value = sizeSliderPosition,
            onValueChange = {
                sizeSliderPosition = it
                ticViewModel.setFieldSize(it) },
            valueRange = 3f..8f,
            steps = 4,
            onValueChangeFinished = {
                if(winRowSliderPosition > sizeSliderPosition){
                    winRowSliderPosition = sizeSliderPosition
                    ticViewModel.setWinRow(winRowSliderPosition)
                }
                winRowSteps = if(sizeSliderPosition > 3){ sizeSliderPosition.toInt() - 4 } else 0
                winRowUpperLimit = sizeSliderPosition
                                    },
            modifier = Modifier
                .width(220.dp)
                .padding(top = 4.dp, bottom = 20.dp)
                .semantics {
                    contentDescription = "Board size: ${(sizeSliderPosition + 0.5).toInt()}"
                },
            colors = SliderDefaults.colors(
                activeTrackColor = MaterialTheme.colors.primary,
                inactiveTrackColor = MaterialTheme.colors.primaryVariant
            )
        )
        Box(
            modifier = Modifier.heightIn(10.dp, 60.dp)
        ){
            AutoResizedText(
                text = "Win row: ${(winRowSliderPosition + 0.5).toInt()}",
                style = MaterialTheme.typography.h5,
                autoResizeLimit = HEIGHT,
                modifier = Modifier.testTag("Win Row"),
            )
        }
        Box(
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp),
        ){
            Slider( // background grey slider
                enabled = false,
                value = winRowSliderPosition,
                onValueChange = {},
                valueRange = 3f..8f,
                steps = 4,
                modifier = Modifier.width(220.dp),
            )
            if(winRowUpperLimit != 3f){
                Slider(
                    value = winRowSliderPosition,
                    onValueChange = {
                        winRowSliderPosition = it
                        ticViewModel.setWinRow(it) },
                    valueRange = 3f..winRowUpperLimit,
                    steps = winRowSteps,
                    onValueChangeFinished = {
                        // When winRowUpperLimit == 4f, steps = 0,
                        // so we have to manually implement changes to be discrete.
                        if(winRowUpperLimit == 4f){
                            winRowSliderPosition = if(winRowSliderPosition > 3.5) 4f else 3f
                        }
                    },
                    modifier = Modifier
                        .width((40 * (winRowSteps + 1) + 20).dp)
                        .testTag("winRow Slider"),
                    colors = SliderDefaults.colors(
                        activeTrackColor = MaterialTheme.colors.primary,
                        inactiveTrackColor = MaterialTheme.colors.primaryVariant
                    )
                )
            }
        }
        Row(
            modifier = Modifier.width(204.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            val currentMode = if (isSystemInDarkTheme())
                painterResource(R.drawable.light_mode_48px)
            else painterResource(R.drawable.dark_mode_48px)
            Icon(
                currentMode,
                null,
                modifier = Modifier
                    .size(40.dp)
                    .offset((-5).dp, 0.dp)
                    .clickable(true) {},
                tint = MaterialTheme.colors.primary
            )

            val iconColor = if(playingVsAI) MaterialTheme.colors.surface else MaterialTheme.colors.primary
            val iconBG = if(!playingVsAI) MaterialTheme.colors.surface else MaterialTheme.colors.primary
            Button(
                onClick = { switchGameMode(playingVsAI) },
                modifier = Modifier
                    .offset((-5).dp, 0.dp)
                    .size(44.dp)
                    .padding(0.dp),
                shape = CircleShape,
                elevation = null,
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = iconBG,
                )
            ) {
                Icon(
                    painterResource(R.drawable.outline_smart_toy_black_48),
                    contentDescription = "Playing vs AI",
                    modifier = Modifier.size(40.dp),
                    tint = iconColor,
                )
            }

            Button(
                modifier = Modifier.widthIn(60.dp, 140.dp),
                elevation = null,
                onClick = {
                ticViewModel.resetGame(size)
                ticViewModel.showMenu(false)
                ticViewModel.setWinRow(winRowSliderPosition)
            }) {
                AutoResizedText(
                    text = "START",
                    style = MaterialTheme.typography.button,
                    autoResizeLimit = WIDTH
                )
            }
        }
    }
}


@Composable
fun CancelButton(
    modifier: Modifier,
    cancelMoveButtonEnabled: Boolean,
    cancelMove: () -> Unit,
    //paddingTop: Dp = 0.dp,
    //paddingBottom: Dp = 0.dp,
    paddingStart: Dp = 0.dp,
    //paddingEnd: Dp = 0.dp,
    paddingBoxBottom: Dp = 0.dp,
){
    Button(
        modifier = modifier,
        onClick = { cancelMove() },
        enabled = cancelMoveButtonEnabled,
        shape = RoundedCornerShape(15.dp),
        border = null,
        elevation = null,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color(0x00000000),
            disabledBackgroundColor = Color(0x00000000)
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(bottom = paddingBoxBottom)
        ) {
            val alpha = if (cancelMoveButtonEnabled) 1f else 0.33f
            Icon(
                painterResource(R.drawable.arrow_back_ios_48px),
                "Cancel move",
                modifier = Modifier
                    .size(32.dp)
                    .alpha(alpha)
                    .padding(start = paddingStart)
                    .testTag("Cancel Icon")
            )
        }
    }
}


@Composable
fun XOButton(
    modifier: Modifier,
    currentMove: CellValues,
    paddingTop: Dp = 0.dp,
    paddingBottom: Dp = 0.dp,
    paddingStart: Dp = 0.dp,
    paddingEnd: Dp = 0.dp,
    makeBotMove: () -> Unit,
){
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        val currentMoveIcon = if (currentMove == CellValues.X)
            painterResource(R.drawable.close_48px)
        else painterResource(R.drawable.fiber_manual_record_48px)
        val testStringCurrentMove = if (currentMove == CellValues.X)
            "currentMove: X" else "currentMove: 0"
        Icon(
            currentMoveIcon,
            null,
            modifier = Modifier
                .size(50.dp)
                .padding(
                    top = paddingTop,
                    bottom = paddingBottom,
                    start = paddingStart,
                    end = paddingEnd
                )
                .testTag(testStringCurrentMove)
                .clickable { makeBotMove() }
        )
    }
}


@Composable
fun MenuButton(
    modifier: Modifier,
    menuIsVisible: Boolean,
    winRow: Int,
    saveWinRow: () -> Unit,
    showMenuDialog: (Boolean) -> Unit,
){
    Button(
        modifier = modifier,
        onClick = {
            saveWinRow()
            showMenuDialog(!menuIsVisible)
        },
        shape = RoundedCornerShape(15.dp),
        border = null,
        elevation = null,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color(0x00000000),
        )
    ) {
        Box(contentAlignment = Alignment.Center){
            Icon(
                painterResource(R.drawable.crop_square_48px),
                "Menu",
                modifier = Modifier.size(30.dp)
            )
            Box(modifier = Modifier.heightIn(1.dp, 32.dp)){
                AutoResizedText(
                    text = "$winRow",
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.testTag("winRow square"),
                    autoResizeLimit = HEIGHT
                )
            }
        }
    }
}


@Composable
fun GameField(
    vertPadding: Dp,
    horPadding: Dp,
    ticViewModel: TicViewModel = viewModel(),
    cellFontSize: TextUnit,

    gameArray: Array<Array<Cell>>,
    gameOverScreenVisible: Boolean,
){
    BoxWithConstraints(
        modifier = Modifier.padding(vertical = vertPadding, horizontal = horPadding),
        contentAlignment = Alignment.Center,
    ) {
        val fieldSize = if(maxWidth < maxHeight) maxWidth else maxHeight
        Column {
            for (i in gameArray.indices) {
                Row {
                    for (j in gameArray[i].indices) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(fieldSize / gameArray.size)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colors.background,
                                    shape = RoundedCornerShape(0.dp)
                                )
                                .background(MaterialTheme.colors.secondary)
                                .clickable(
                                    enabled = gameArray[i][j].isClickable,
                                    onClick = {
                                        ticViewModel.makeMove(i = i, j = j)
                                        GlobalScope.launch(Dispatchers.Main) {
                                            delay(1500L)
                                            ticViewModel.makeBotMove()
                                        }
                                    }
                                )
                                .testTag("Cell $i $j")
                        ) {
                            AutoResizedText(
                                text = gameArray[i][j].cellText.cellValue.toString(),
                                style = MaterialTheme.typography.h3,
                                changedCellSize = fieldSize / gameArray.size,
                                fontSize = cellFontSize,
                                color = gameArray[i][j].cellColor.color,
                                autoResizeLimit = HEIGHT,
                                modifier = Modifier.testTag("Text $i $j")
                            )
                        }
                    }
                }
            }
        }
        ticViewModel.updateFieldSize(fieldSize)
    }
    //------------------------GAME OVER SCREEN (win / draw)
    if (gameOverScreenVisible) {
        Box(modifier = Modifier
            .fillMaxSize()
            .testTag("Game Over Screen")
            .clickable(enabled = true) { ticViewModel.showMenu(true) }) {}
    }
}


@Composable
fun AutoResizedText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    changedCellSize: Dp = 1.dp,
    fontSize: TextUnit = style.fontSize,
    color: Color = style.color,
    autoResizeLimit: AutoResizeLimit,
) {
    var resizedTextStyle by remember {
        mutableStateOf(style)
    }
    var shouldDraw by remember {
        mutableStateOf(false)
    }
    var fontSizeChanged by remember {
        mutableStateOf(true)
    }
    // saved + observed implemented to overcome bug:
    // font wasn't changing in first cells when expanding field Size
    var savedCellSize by remember {
        mutableStateOf(1.dp)
    }
    if (savedCellSize != changedCellSize) {
        fontSizeChanged = true
        savedCellSize = changedCellSize
    }

    if(fontSizeChanged){
        resizedTextStyle = resizedTextStyle.copy(
            fontSize = fontSize
        )
        fontSizeChanged = false
    }

    val defaultFontSize = MaterialTheme.typography.body1.fontSize

    if(autoResizeLimit == WIDTH){
        Text(
            text = text,
            color = color,
            modifier = modifier.drawWithContent {
                if (shouldDraw) {
                    drawContent()
                }
            },
            softWrap = false,
            style = resizedTextStyle,
            onTextLayout = { result ->
                if (result.didOverflowWidth) {
                    if (style.fontSize.isUnspecified) {
                        resizedTextStyle = resizedTextStyle.copy(
                            fontSize = defaultFontSize
                        )
                    }
                    resizedTextStyle = resizedTextStyle.copy(
                        fontSize = resizedTextStyle.fontSize * 0.95
                    )
                } else {
                    shouldDraw = true
                }
            }
        )
    } else {
        Text(
            text = text,
            color = color,
            modifier = modifier
                .drawWithContent {
                if (shouldDraw) {
                    drawContent()
                }
            },
            softWrap = false,
            style = resizedTextStyle,
            onTextLayout = { result ->
                if (result.didOverflowHeight) {
                    if (style.fontSize.isUnspecified) {
                        resizedTextStyle = resizedTextStyle.copy(
                            fontSize = defaultFontSize
                        )
                    }
                    resizedTextStyle = resizedTextStyle.copy(
                        fontSize = resizedTextStyle.fontSize * 0.95
                    )
                } else {
                    shouldDraw = true
                }
            }
        )
    }


}


//========================================
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    MyTicTacToeTheme {
        TicApp()
    }
}
