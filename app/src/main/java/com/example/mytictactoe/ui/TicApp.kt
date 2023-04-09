package com.example.mytictactoe.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytictactoe.*
import com.example.mytictactoe.AutoResizeHeightOrWidth.*
import com.example.mytictactoe.LoadOrSave.*
import com.example.mytictactoe.Orientation.*
import com.example.mytictactoe.R
import com.example.mytictactoe.ui.theme.MyTicTacToeTheme
import com.example.mytictactoe.ui.theme.menuBorder
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.coroutineScope


@Composable
fun TicApp(
    ticViewModel: TicViewModel = viewModel()
) {
    val ticUiState by ticViewModel.uiState.collectAsState()

    //----------------------------popup MENU SCREEN
    if (ticUiState.menuIsVisible){
        AlertDialog(
            onDismissRequest = {
                ticViewModel.cancelWinRowChangesDuringTheGame()
                ticViewModel.setMenuSettings(SAVE)
                ticViewModel.showMenu(false)
                ticViewModel.makeBotMove() // if Bot's turn
                               },
            buttons = {
                MainMenu (
                    ticViewModel = ticViewModel,
                    theme = ticUiState.theme,
                    size = ticUiState.gameArray.size,
                    winRow = ticUiState.winRow,
                    loadMemorySettings = ticUiState.memorySettings.loadOrSave,
                    playingVsAI = ticUiState.playingVsAI,
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
                ticViewModel = ticViewModel,
                vertPadding = 50.dp,
                horPadding = 0.dp,
                cellFontSize = ticUiState.cellFontSize,
                gameArray = ticUiState.gameArray,
                botOrGameOverScreen = ticUiState.botOrGameOverScreen,
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
                    cancelMove = {
                        ticViewModel.cancelMove()
                        ticViewModel.cancelBotWait()
                                 },
                    paddingStart = 10.dp,
                )

                //---------------------------icon  XO
                CurrentMoveAndAiIcons(
                    modifier = Modifier.weight(1f),
                    playingVsAI = ticUiState.playingVsAI,
                    menuIsVisible = ticUiState.menuIsVisible,
                    currentMove = ticUiState.currentMove,
                    aiMove = ticUiState.aiMove,
                    paddingTop = 8.dp,
                    paddingBottom = 10.dp,
                    botIconOffsetX = 30.dp,
                )

                //---------------------------button  []
                MenuButton(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("Menu Button"),
                    winRow = ticUiState.winRow,
                    menuLoading = {
                        ticViewModel.cancelBotWait()
                        ticViewModel.saveWinRow()
                        ticViewModel.showMenu(true)
                        ticViewModel.setMenuSettings(LOAD)
                                     },
                    menuButtonShouldBeShaken = ticUiState.menuButtonShouldBeShaken,
                    shakeMenuButton = {ticViewModel.shakeMenuButton(false)}
                )
            }
        } else {

            GameField(
                ticViewModel = ticViewModel,
                vertPadding = 0.dp,
                horPadding = 70.dp,
                cellFontSize = ticUiState.cellFontSize,
                gameArray = ticUiState.gameArray,
                botOrGameOverScreen = ticUiState.botOrGameOverScreen,
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
                    winRow = ticUiState.winRow,
                    menuLoading = {
                        ticViewModel.saveWinRow()
                        ticViewModel.showMenu(true)
                        ticViewModel.setMenuSettings(LOAD)
                    },
                    menuButtonShouldBeShaken = ticUiState.menuButtonShouldBeShaken,
                    shakeMenuButton = {ticViewModel.shakeMenuButton(false)}
                )

                //---------------------------icon  XO
                CurrentMoveAndAiIcons(
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 24.dp),
                    playingVsAI = ticUiState.playingVsAI,
                    menuIsVisible = ticUiState.menuIsVisible,
                    currentMove = ticUiState.currentMove,
                    aiMove = ticUiState.aiMove,
                    paddingStart = 15.dp,
                    botIconOffsetY = (-34).dp,
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
    theme: AppTheme,
    size: Int,
    winRow: Int,
    loadMemorySettings: Boolean,
    playingVsAI: Boolean,
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
        winRowSteps = if(sizeSliderPosition > 3) { sizeSliderPosition.toInt() - 4 } else 0
        ticViewModel.setMenuSettings(SAVE) // disabling IF condition to load settings only once, when menu is shown
    }
    Column(
        modifier = Modifier.padding(25.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //-------------------- BOARD SIZE text & slider
        Box(
            modifier = Modifier.heightIn(10.dp, 60.dp)
        ){
            AutoResizedText(
                text = "Board size: ${(sizeSliderPosition + 0.5).toInt()}",
                style = MaterialTheme.typography.h5,
                autoResizeHeightOrWidth = HEIGHT,
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
        //-------------------- WIN ROW text & sliders
        Box(
            modifier = Modifier.heightIn(10.dp, 60.dp)
        ){
            AutoResizedText(
                text = "Win row: ${(winRowSliderPosition + 0.5).toInt()}",
                style = MaterialTheme.typography.h5,
                autoResizeHeightOrWidth = HEIGHT,
                modifier = Modifier.testTag("Win Row"),
            )
        }
        Box(
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp),
        ){
            // background grey slider
            Slider(
                enabled = false,
                value = winRowSliderPosition,
                onValueChange = {},
                valueRange = 3f..8f,
                steps = 4,
                modifier = Modifier.width(220.dp),
            )
            // real winRow slider
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
            //-------------------- THEME button
            val systemThemeIsDark = isSystemInDarkTheme()
            val themeIconColor = if(theme != AppTheme.AUTO) MaterialTheme.colors.surface else MaterialTheme.colors.primary
            val themeIconBG = if(theme == AppTheme.AUTO) MaterialTheme.colors.surface else MaterialTheme.colors.primary
            Box {
                Button(
                    onClick = { ticViewModel.changeTheme(systemThemeIsDark) },
                    modifier = Modifier
                        .offset((-5).dp, 0.dp)
                        .size(40.dp)
                        .padding(0.dp),
                    shape = CircleShape,
                    elevation = null,
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = themeIconBG,
                    )
                ) {
                    val currentMode = when(theme){
                        AppTheme.DARK -> painterResource(R.drawable.dark_mode_48px)
                        AppTheme.LIGHT -> painterResource(R.drawable.baseline_light_mode_24)
                        AppTheme.AUTO -> if(systemThemeIsDark) painterResource(R.drawable.dark_mode_48px) else painterResource(R.drawable.light_mode_48px)
                    }
                    Icon(
                        currentMode,
                        "UI theme",
                        tint = themeIconColor,
                    )
                }
                val lift by animateDpAsState(
                    targetValue = if(theme == AppTheme.AUTO) (-30).dp else 0.dp,
                    animationSpec = tween(durationMillis = 800, easing = LinearOutSlowInEasing),
                    //finishedListener = {}
                )
                val alpha by animateFloatAsState(
                    targetValue = if(theme == AppTheme.AUTO) 0f else 1f,
                    animationSpec = tween(durationMillis = 700, delayMillis = 350, easing = LinearOutSlowInEasing)
                )
                Text(
                    text = if(theme == AppTheme.AUTO) "AUTO" else " ",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset((-5).dp, lift)
                        .alpha(alpha)
                )
            }

            //-------------------- AI button
            val aiIconColor = if(playingVsAI) MaterialTheme.colors.surface else MaterialTheme.colors.primary
            val aiIconBG = if(!playingVsAI) MaterialTheme.colors.surface else MaterialTheme.colors.primary
            Button(
                onClick = {
                    ticViewModel.switchGameMode()
                    //ticViewModel.loadInitTheme()
                },
                modifier = Modifier
                    .offset((-5).dp, 0.dp)
                    .size(44.dp)
                    .padding(0.dp),
                shape = CircleShape,
                elevation = null,
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = aiIconBG,
                )
            ) {
                Icon(
                    painterResource(R.drawable.smart_toy_48px),
                    contentDescription = "Playing vs AI",
                    modifier = Modifier.size(40.dp) .offset(0.dp, (-1).dp),
                    tint = aiIconColor,
                )
            }
            //-------------------- START button
            Button(
                modifier = Modifier.widthIn(60.dp, 140.dp),
                elevation = null,
                onClick = {
                ticViewModel.resetGame(size)
                ticViewModel.changeBotMove()
                ticViewModel.canChangeBotMove = true
                ticViewModel.setMenuSettings(SAVE)
                ticViewModel.showMenu(false)
                ticViewModel.makeBotMove()
            }) {
                AutoResizedText(
                    text = "START",
                    style = MaterialTheme.typography.button,
                    autoResizeHeightOrWidth = WIDTH
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
        onClick = cancelMove,
        enabled = cancelMoveButtonEnabled,
        shape = RoundedCornerShape(60.dp),
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
fun CurrentMoveAndAiIcons(
    modifier: Modifier,
    playingVsAI: Boolean,
    menuIsVisible: Boolean,
    currentMove: CellValues,
    aiMove: CellValues,
    paddingTop: Dp = 0.dp,
    paddingBottom: Dp = 0.dp,
    paddingStart: Dp = 0.dp,
    paddingEnd: Dp = 0.dp,
    botIconOffsetX: Dp = 0.dp,
    botIconOffsetY: Dp = 0.dp,
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
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .padding(
                    top = paddingTop,
                    bottom = paddingBottom,
                    start = paddingStart,
                    end = paddingEnd
                )
                .testTag(testStringCurrentMove)
        )
        if(playingVsAI){
            val iconAlpha = if(currentMove == aiMove) 1f else 0f
            val iconMove = if(menuIsVisible) 0f else 2f
            val infiniteMove = rememberInfiniteTransition()
            val moveIcon = infiniteMove.animateFloat(
                initialValue = 0f,
                targetValue = iconMove,
                animationSpec = infiniteRepeatable(
                    tween(durationMillis = 450, easing = EaseInOutSine),
                    repeatMode = RepeatMode.Reverse
                )
            )
            Icon(
                painterResource(R.drawable.smart_toy_48px),
                contentDescription = null,
                modifier = Modifier
                    .alpha(iconAlpha)
                    .size(46.dp)
                    .padding(
                        top = paddingTop,
                        bottom = paddingBottom,
                        start = paddingStart,
                        end = paddingEnd
                    )
                    .offset(botIconOffsetX, botIconOffsetY + (moveIcon.value.dp - 1.dp))
            )
        }
    }
}


@Composable
fun MenuButton(
    modifier: Modifier,
    winRow: Int,
    menuLoading: () -> Unit,
    menuButtonShouldBeShaken: Boolean,
    shakeMenuButton: (Boolean) -> Unit,
){
    Button(
        modifier = modifier,
        onClick = menuLoading,
        shape = RoundedCornerShape(60.dp),
        border = null,
        elevation = null,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color(0x00000000),
        )
    ) {
        val shake by animateDpAsState(
            targetValue = if (menuButtonShouldBeShaken) 15.dp else 0.dp,
            animationSpec = if (menuButtonShouldBeShaken){
            tween(durationMillis = 50, easing = FastOutLinearInEasing)
            } else {
                spring(dampingRatio = 0.15f, stiffness = 1200f)
                    },
            finishedListener = {shakeMenuButton(false)}
        )

        Box(contentAlignment = Alignment.Center,
            modifier = Modifier.offset(x = shake,y = 0.dp)
        ){
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
                    autoResizeHeightOrWidth = HEIGHT
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
    botOrGameOverScreen: BotOrGameOverScreen,
){
    BoxWithConstraints(
        modifier = Modifier
            .disableSplitMotionEvents() // anti-multitap
            .padding(vertical = vertPadding, horizontal = horPadding),
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
                                .clickable(
                                    enabled = gameArray[i][j].isClickable,
                                    onClick = {
                                        ticViewModel.makeMove(i = i, j = j)
                                        ticViewModel.makeBotMove()
                                    }
                                )
                                .size(fieldSize / gameArray.size)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colors.background,
                                    shape = RoundedCornerShape(0.dp)
                                )
                                .background(MaterialTheme.colors.secondary)
                                .testTag("Cell $i $j")
                        ) {
                            AutoResizedText(
                                text = gameArray[i][j].cellText.cellValue.toString(),
                                style = MaterialTheme.typography.h3,
                                changedCellSize = fieldSize / gameArray.size,
                                fontSize = cellFontSize,
                                color = gameArray[i][j].cellColor.color,
                                autoResizeHeightOrWidth = HEIGHT,
                                modifier = Modifier.testTag("Text $i $j")
                            )
                        }
                    }
                }
            }
        }
        ticViewModel.updateFieldSize(fieldSize)
    }

    //------------------------BOT or GAME OVER screen (win / draw)
    if (botOrGameOverScreen.state.visible) {
        Box(modifier = Modifier
            .fillMaxSize()
            .testTag("Game Over Screen")
            .clickable(enabled = botOrGameOverScreen.state.clickable) {
                ticViewModel.saveWinRow()
                ticViewModel.showMenu(true)
                ticViewModel.setMenuSettings(LOAD)
                ticViewModel.resetCurrentMoveToX()
                ticViewModel.changeBotMove()
                ticViewModel.canChangeBotMove = false
            }) {}
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
    autoResizeHeightOrWidth: AutoResizeHeightOrWidth,
) {
    var resizedTextStyle by remember {
        mutableStateOf(style)
    }

    var shouldDraw by remember {
        mutableStateOf(false)
    }

    //var resizedValue by remember { mutableStateOf(1000.sp) }

    // savedCellSize implemented to overcome bug:
    // font wasn't changing in first cells when expanding field Size
    var savedCellSize by remember {
        mutableStateOf(1.dp)
    }
    if (savedCellSize != changedCellSize) {
        savedCellSize = changedCellSize
        resizedTextStyle = resizedTextStyle.copy(
            fontSize = fontSize
        )
    }

    val defaultFontSize = MaterialTheme.typography.body1.fontSize

    if(autoResizeHeightOrWidth == WIDTH){
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
                    //resizedValue = resizedTextStyle.fontSize
                } else {
//                    resizedTextStyle = resizedTextStyle.copy(
//                        fontSize = resizedValue
//                    )
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
//                    resizedValue = resizedTextStyle.fontSize
                } else {
//                    resizedTextStyle = resizedTextStyle.copy(
//                        fontSize = resizedValue
//                    )
                    shouldDraw = true
                }
            }
        )
    }
}


fun Modifier.disableSplitMotionEvents() =
    pointerInput(Unit) {
        coroutineScope {
            var currentId: Long = -1L
            awaitPointerEventScope {
                while (true) {
                    awaitPointerEvent(PointerEventPass.Initial).changes.forEach { pointerInfo ->
                        when {
                            pointerInfo.pressed && currentId == -1L -> currentId = pointerInfo.id.value
                            pointerInfo.pressed.not() && currentId == pointerInfo.id.value -> currentId = -1
                            pointerInfo.id.value != currentId && currentId != -1L -> pointerInfo.consume()
                            else -> Unit
                        }
                    }
                }
            }
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
