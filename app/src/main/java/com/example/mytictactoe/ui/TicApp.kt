package com.example.mytictactoe.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import com.example.mytictactoe.ui.theme.*
import kotlinx.coroutines.coroutineScope

val Int.nonScaledSp
    @Composable
    get() = (this / LocalDensity.current.fontScale).sp

@Composable
fun TicApp(
    ticViewModel: TicViewModel = viewModel()
) {
    val ticUiState by ticViewModel.uiState.collectAsState()

    //-------------------------------popup MENU SCREEN
    if (ticUiState.menuIsVisible){
        AlertDialog(
            onDismissRequest = {
                ticViewModel.cancelWinRowChangesDuringTheGame()
                ticViewModel.setMenuSettings(SAVE)
                ticViewModel.changeFirstMoveOnMenuShowing()
                ticViewModel.saveOrLoadCurrentMove(LOAD)
                ticViewModel.showMenu(false)
                ticViewModel.makeBotMove() // if Bot's turn
                               },
            buttons = {
                MainMenu (
                    ticViewModel = ticViewModel,
                    theme = ticUiState.theme,
                    size = ticUiState.gameArray.size,
                    winNotLose = ticUiState.winNotLose,
                    winRow = ticUiState.winRow,
                    loadMemorySettings = ticUiState.memorySettings.loadOrSave,
                    playingVsAI = ticUiState.playingVsAI,
                )
            },
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .testTag("Menu Window")
                .widthIn(220.dp, 300.dp)
                .border(
                    width = 3.dp,
                    color = MaterialTheme.colors.menuBorder,
                    shape = RoundedCornerShape(30.dp)
                ),
        )
    }

    //-------------------------------------MAIN SCREEN

    BoxWithConstraints(
        contentAlignment = Alignment.Center,
    ) {
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
                    .align(Alignment.TopCenter)
                    .height(46.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {

                //---------------------------button  <
                CancelButton(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
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
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    playingVsAI = ticUiState.playingVsAI,
                    menuIsVisible = ticUiState.menuIsVisible,
                    currentMove = ticUiState.currentMove,
                    showCustomCellDialog = {ticViewModel.showCustomCellDialog(true)},
                    cancelBotWait = {ticViewModel.cancelBotWait()},
                    //paddingTop = 8.dp,
                    //paddingBottom = 10.dp,
                    botIconOffsetX = 30.dp,
                )

                //---------------------------button  []
                MenuButton(
                    ticViewModel = ticViewModel,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .testTag("Menu Button"),
                    winRow = ticUiState.winRow,
                    menuButtonShouldBeShaken = ticUiState.menuButtonShouldBeShaken,
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
                    .width(46.dp)
                    .align(Alignment.CenterStart),
                verticalArrangement = Arrangement.SpaceAround
            ) {

                //---------------------------button  []
                MenuButton(
                    ticViewModel = ticViewModel,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .offset(0.dp, (-9).dp)
                        //.padding(bottom = 18.dp)
                        .testTag("Menu Button"),
                    winRow = ticUiState.winRow,
                    menuButtonShouldBeShaken = ticUiState.menuButtonShouldBeShaken,
                )

                //---------------------------icon  XO
                CurrentMoveAndAiIcons(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .offset(0.dp, (-12).dp),
                        //.padding(bottom = 24.dp),
                    playingVsAI = ticUiState.playingVsAI,
                    menuIsVisible = ticUiState.menuIsVisible,
                    currentMove = ticUiState.currentMove,
                    showCustomCellDialog = {ticViewModel.showCustomCellDialog(true)},
                    cancelBotWait = {ticViewModel.cancelBotWait()},
                    //paddingStart = 15.dp,
                    botIconOffsetY = (-34).dp,
                )

                //---------------------------button  <
                CancelButton(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .testTag("Cancel Button"),
                    cancelMoveButtonEnabled = ticUiState.cancelMoveButtonEnabled,
                    cancelMove = {ticViewModel.cancelMove()},
                    paddingStart = 10.dp,
                    paddingBoxBottom = 34.dp,
                    )
            }
        }
    }

    //----------------------------CUSTOM SYMBOL Dialog
    if(ticUiState.customCellDialogIsVisible){
        AlertDialog(
            onDismissRequest = {
                ticViewModel.showCustomCellDialog(false)
                ticViewModel.makeBotMove() // if Bot's turn
                },
            buttons = {
                CustomCellDialog(
                    ticViewModel = ticViewModel,
                    currentMove = ticUiState.currentMove,
                    theme = ticUiState.theme,
                )
            },
            shape = RoundedCornerShape(0.dp),
            backgroundColor = Color.Transparent,
            modifier = Modifier
                .padding(0.dp)
                .size(100.dp)
                .clickable {
                    ticViewModel.showCustomCellDialog(false)
                    ticViewModel.makeBotMove() // if Bot's turn
                }
                .wrapContentSize(align = Alignment.Center, unbounded = true),
            )
    }
}


//****************************************************************************

@Composable
fun MainMenu(
    ticViewModel: TicViewModel = viewModel(),
    theme: AppTheme,
    size: Int,
    winNotLose: Boolean,
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
        modifier = Modifier
            .padding(25.dp)
            .widthIn(160.dp, 240.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //-------------------- BOARD SIZE text & slider
        Box(
            modifier = Modifier.heightIn(10.dp, 40.dp)
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
                activeTickColor = MaterialTheme.colors.primary,
                inactiveTrackColor = MaterialTheme.colors.primaryVariant,
                inactiveTickColor = MaterialTheme.colors.primaryVariant,
            )
        )
        //-------------------- WIN ROW text & sliders
        Box(
            modifier = Modifier.heightIn(10.dp, 40.dp)
        ){
            Row(verticalAlignment = Alignment.CenterVertically){
                AutoResizedText(
                    text = "${(winRowSliderPosition + 0.5).toInt()} in a row ",
                    style = MaterialTheme.typography.h5,
                    autoResizeHeightOrWidth = HEIGHT,
                    modifier = Modifier.testTag("Win Row"),
                )
                Button(
                    modifier = Modifier.padding(0.dp),
                    onClick = { ticViewModel.changeWinOrLose() },
                    enabled = true,
                    shape = RoundedCornerShape(30.dp),
                    border = null,
                    elevation = null,
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0x00000000),
                        disabledBackgroundColor = Color(0x00000000)
                    )
                ) {
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier
                            .padding(0.dp)
                    ) {
                        AutoResizedText(
                            text = if(winNotLose) "wins " else "loses",
                            style = MaterialTheme.typography.h5,
                            color = MaterialTheme.colors.primary,
                            autoResizeHeightOrWidth = HEIGHT,
                            modifier = Modifier
                                .testTag("Win or lose")
                        )
                    }
                }
            }
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
                colors = SliderDefaults.colors(
                    disabledInactiveTickColor = MaterialTheme.colors.onSurface.copy(alpha = 0f)
                )
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
                        activeTickColor = MaterialTheme.colors.primary,
                        inactiveTrackColor = MaterialTheme.colors.primaryVariant,
                        inactiveTickColor = MaterialTheme.colors.primaryVariant,
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
                        .size(44.dp)
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
                onClick = { ticViewModel.switchPlayingVsAiMode() },
                modifier = Modifier
                    .offset((-3).dp, 0.dp)
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
                    modifier = Modifier
                        .size(40.dp)
                        .offset(0.dp, (-1).dp),
                    tint = aiIconColor,
                )
            }
            //-------------------- START button
            Button(
                modifier = Modifier
                    .height(44.dp)
                    .widthIn(80.dp, 140.dp),
                elevation = null,
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(15.dp),
                onClick = {
                    ticViewModel.resetGame(size)
                    ticViewModel.allowChangingFirstMove(true)
                    ticViewModel.setMenuSettings(SAVE)
                    ticViewModel.showMenu(false)
                    ticViewModel.makeBotMove()
                }) {
                AutoResizedText(
                    text = "START",
                    style = MaterialTheme.typography.button,
                    autoResizeHeightOrWidth = HEIGHT
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
            backgroundColor = Color.Transparent,
            disabledBackgroundColor = Color.Transparent
        ),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(bottom = paddingBoxBottom)
                .wrapContentSize(Alignment.Center, unbounded = true),
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
    currentMove: Char,
    showCustomCellDialog: (Boolean) -> Unit,
    cancelBotWait: () -> Unit,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    botIconOffsetX: Dp = 0.dp,
    botIconOffsetY: Dp = 0.dp,
){
    Button(
        modifier = modifier,
        onClick = {
            showCustomCellDialog(true)
            cancelBotWait()
                  },
        shape = RoundedCornerShape(60.dp),
        border = null,
        elevation = null,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent,
            disabledBackgroundColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier.wrapContentSize(Alignment.Center, unbounded = true),
            contentAlignment = Alignment.Center,
        ) {
            val currentMoveIcon = if (currentMove == CustomCellValues.player1)
                painterResource(R.drawable.close_48px)
            else painterResource(R.drawable.fiber_manual_record_48px)
            
            val testStringCurrentMove = if (currentMove == CustomCellValues.player1)
                "currentMove: X" else "currentMove: 0"
            if(currentMove == 'X' || currentMove == 'O'){
                Icon(
                    currentMoveIcon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                        .offset(offsetX, offsetY)
                        .testTag(testStringCurrentMove)
                )
            } else {
                Text(
                    text = currentMove.toString(),
                    fontWeight = FontWeight.Normal,
                    fontSize = 28.nonScaledSp,
                    modifier = Modifier.offset(0.dp, (-1).dp)
                )
            }
            if(playingVsAI){
                val iconAlpha = if(currentMove == CustomCellValues.player2) 1f else 0f
                val infiniteMove = rememberInfiniteTransition()
                val moveIcon by infiniteMove.animateFloat(
                    initialValue = 0f,
                    targetValue = 2f,
                    animationSpec = infiniteRepeatable(
                        tween(durationMillis = 450, easing = EaseInOutSine),
                        repeatMode = RepeatMode.Reverse
                    )
                )
                val moveAmplitude by animateFloatAsState(
                    targetValue = if(menuIsVisible) 0f else 1f,
                    animationSpec = tween(durationMillis = 225, easing = EaseInOutSine),
                )
                Icon(
                    painterResource(R.drawable.smart_toy_48px),
                    contentDescription = null,
                    modifier = Modifier
                        .alpha(iconAlpha)
                        .size(30.dp)
//                        .padding(
//                            top = paddingTop,
//                            bottom = paddingBottom,
//                            start = paddingStart,
//                            end = paddingEnd
//                        )
                        .offset(
                            botIconOffsetX,
                            botIconOffsetY + ((moveIcon * moveAmplitude).dp - 1.dp)
                        )
                )
            }
        }
    }
}


@Composable
fun CustomCellDialog(
    ticViewModel: TicViewModel,
    currentMove: Char,
    theme: AppTheme,
){
    val str by remember {
        mutableStateOf("")
    }
    val symbol by remember {
        mutableStateOf(TextFieldValue(str))
    }
    val focusRequester = remember { FocusRequester() }

    var shakeOn by remember { mutableStateOf(false) }
    val shakeDp by animateDpAsState(
        targetValue = if (shakeOn) 15.dp else 0.dp,
        animationSpec = if (shakeOn){
            tween(durationMillis = 50, easing = FastOutLinearInEasing)
        } else {
            spring(dampingRatio = 0.15f, stiffness = 1200f)
        },
        finishedListener = {shakeOn = false}
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .offset(shakeDp, 0.dp)
            //.wrapContentSize(align = Alignment.Center, unbounded = true),
        ){
        OutlinedTextField(
            onValueChange = {
                val itChar = it.text.toCharArray().first()
                val otherPlayer = if(currentMove != CustomCellValues.player1) CustomCellValues.player1 else CustomCellValues.player2
                if((CustomCellValues.forbiddenValues.contains(itChar)) || (itChar == otherPlayer) || (itChar.isSurrogate())){
                    shakeOn = true
                } else {
                    ticViewModel.showCustomCellDialog(false)
                    ticViewModel.changeCellSymbol(itChar)
                    ticViewModel.cancelBotWait() // fixing occasional double-move
                    ticViewModel.makeBotMove() // if Bot's turn
                    //symbol = it
                    //str = ""
                }
            },
            value = symbol,
            singleLine = true,
            label = null,
            modifier = Modifier
                .width(0.dp)
                //.alpha(0f)
                .focusRequester(focusRequester),
        )
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        val circleBG = if((theme == AppTheme.DARK) || ((theme == AppTheme.AUTO) && (isSystemInDarkTheme()))) Color.White else Color.Black
        Canvas(
            modifier = Modifier.size(100.dp),
            onDraw = { drawCircle(color = circleBG) }
        )
        Text(text = currentMove.toString(), fontSize = 62.nonScaledSp, color = MaterialTheme.colors.surface)
        Button(
            onClick = {
                ticViewModel.showCustomCellDialog(false)
                ticViewModel.makeBotMove() // if Bot's turn
                      },
            shape = RoundedCornerShape(0.dp),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier
                .size(160.dp)
                .alpha(0f)
                //.wrapContentSize(align = Alignment.Center, unbounded = true),
        ) {
//            Canvas(
//                modifier = Modifier
//                    .size(300.dp),
//                    //.wrapContentSize(align = Alignment.Center, unbounded = true),
//                onDraw = { drawCircle(color = Color.Blue) }
//            )
        }
    }
}


@Composable
fun MenuButton(
    ticViewModel: TicViewModel,
    modifier: Modifier,
    winRow: Int,
    menuButtonShouldBeShaken: Boolean,
){
    Button(
        modifier = modifier,
        onClick = {
            ticViewModel.cancelBotWait()
            ticViewModel.saveWinRow()
            ticViewModel.showMenu(true)
            ticViewModel.setMenuSettings(LOAD)
            ticViewModel.saveOrLoadCurrentMove(SAVE)
            ticViewModel.changeFirstMoveOnMenuShowing()
            ticViewModel.shakeMenuButton(false)
        },
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
            finishedListener = {ticViewModel.shakeMenuButton(false)}
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .offset(x = shake,y = 0.dp)
                .wrapContentSize(Alignment.Center, unbounded = true),
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
                    modifier = Modifier
                        .testTag("winRow square")
                        .offset(0.dp, (-0.5).dp),
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
                                text = gameArray[i][j].cellText.toString(),
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
                ticViewModel.saveOrLoadCurrentMove(SAVE)
                ticViewModel.changeFirstMoveOnMenuShowing()
            }
        ) {}
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


/*
// sequencer PressInteraction
@Composable
fun VoiceButton(note: Int, kmmk: KmmkComponentContext){
    val interactionSource = remember { MutableInteractionSource() }
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> { kmmk.noteOn(note) }
                is PressInteraction.Release -> { kmmk.noteOff(note) }
                is PressInteraction.Cancel -> { kmmk.noteOff(note) }
            }
        }
    }
    Button(interactionSource = interactionSource,
        onClick = {},
        modifier = Modifier
            .size(80.dp)
            .border(width = 1.dp, color = Color(0xFF000000))
            .background(Color.Gray, shape = RoundedCornerShape(0.dp))
    ) {

    }
}
 */


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
