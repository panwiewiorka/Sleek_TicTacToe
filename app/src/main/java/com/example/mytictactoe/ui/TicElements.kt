package com.example.mytictactoe.ui

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isUnspecified
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytictactoe.AppTheme
import com.example.mytictactoe.AutoResizeHeightOrWidth
import com.example.mytictactoe.Bot
import com.example.mytictactoe.BotOrGameOverScreen
import com.example.mytictactoe.Cell
import com.example.mytictactoe.CellColors
import com.example.mytictactoe.CustomCellValues
import com.example.mytictactoe.LoadOrSave
import com.example.mytictactoe.Orientation
import com.example.mytictactoe.R
import com.example.mytictactoe.ui.theme.standart
import kotlinx.coroutines.coroutineScope


@Composable
fun CancelButton(
    ticViewModel: TicViewModel,
    modifier: Modifier,
    cancelMoveButtonEnabled: Boolean,
    cancelMove: () -> Unit,
    playingVsAI: Boolean,
    currentMove: Char,
    gameArray: Array<Array<Cell>>,
    //paddingTop: Dp = 0.dp,
    //paddingBottom: Dp = 0.dp,
    paddingStart: Dp = 0.dp,
    //paddingEnd: Dp = 0.dp,
    paddingBoxBottom: Dp = 0.dp,
){
    val player = if(currentMove == CustomCellValues.player1) "O" else "X"
    val coordinates = " ${ticViewModel.convertIndexToLetter(ticViewModel.iOneMoveBefore)} ${ticViewModel.jOneMoveBefore + 1}"
    val msg = if(cancelMoveButtonEnabled) "Cancel last move: $player to $coordinates" else {
        if(ticViewModel.freeCellsLeft == (gameArray.size * gameArray.size)) "Cancel last move. " else {
            if(playingVsAI) "Unable to cancel move of AI. " else "Move cancelled. "
        }
    }
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
                contentDescription = msg,
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
        modifier = modifier.clearAndSetSemantics {},
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
            modifier = Modifier
                /*
                .clearAndSetSemantics {
                contentDescription =
                    "Current move is ${if (currentMove == player1) "X" else "O"}. " +
                            "$winRow in a row ${if (winNotLose) "wins" else "loses"}. " +
                            "Pressing this button opens menu for changing X and O symbols to some others. Affects only visual appearance."
            }
                 */
                .wrapContentSize(Alignment.Center, unbounded = true),
            contentAlignment = Alignment.Center,
        ) {
            val testStringCurrentMove = if (currentMove == CustomCellValues.player1)
                "currentMove: X" else "currentMove: 0"
            if(currentMove == 'X' || currentMove == 'O'){
                Icon(
                    painter = if(currentMove == 'X') painterResource(R.drawable.close_48px) else painterResource(
                        R.drawable.fiber_manual_record_48px),
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
        targetValue = if (shakeOn) (-15).dp else 0.dp,
        animationSpec = if (shakeOn){
            tween(durationMillis = 50, easing = FastOutLinearInEasing)
        } else {
            spring(dampingRatio = 0.15f, stiffness = 1200f)
        },
        finishedListener = {shakeOn = false}
    )
    val circleBG = if((theme == AppTheme.DARK) || ((theme == AppTheme.AUTO) && (isSystemInDarkTheme()))) Color.White else Color.Black

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier.width(200.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(0.dp)
                .offset(shakeDp, 0.dp)
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
                shape = RectangleShape,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .size(130.dp)
                    .alpha(0f)
            ) {}
        }

        val defaultSymbol = when {
            currentMove == CustomCellValues.player1 && CustomCellValues.player1 != 'X' -> 'X'
            currentMove == CustomCellValues.player2 && CustomCellValues.player2 != 'O' -> 'O'
            else -> null
        }
        if(defaultSymbol != null){
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(0.dp)
                    .offset(80.dp, 14.dp)
                    .clickable {
                        ticViewModel.showCustomCellDialog(false)
                        ticViewModel.changeCellSymbol(defaultSymbol)
                        ticViewModel.cancelBotWait() // fixing occasional double-move
                        ticViewModel.makeBotMove() // if Bot's turn
                    }
            ) {
                Canvas(
                    modifier = Modifier.size(36.dp),
                    onDraw = { drawCircle(color = circleBG) }
                )
                Text(text = defaultSymbol.toString(), fontSize = 20.nonScaledSp, color = MaterialTheme.colors.surface, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}


@Composable
fun MenuButton(
    ticViewModel: TicViewModel,
    modifier: Modifier,
    winRow: Int,
    winNotLose: Boolean,
    menuButtonShouldBeShaken: Boolean,
    winOrLoseShouldBeShown: Boolean,
    orientation: Orientation,
){
    Button(
        modifier = modifier,
        onClick = {
            ticViewModel.cancelBotWait()
            ticViewModel.saveWinRow()
            ticViewModel.saveWinOrLose()
            ticViewModel.savePlayingVsAi()
            ticViewModel.showMenu(true)
            ticViewModel.setMenuSettings(LoadOrSave.LOAD)
            ticViewModel.saveOrLoadCurrentMove(LoadOrSave.SAVE)
            ticViewModel.changeFirstMoveOnMenuShowing()
            ticViewModel.shakeMenuButton(false)
            ticViewModel.showWinOrLoseIndication(false)
        },
        shape = RoundedCornerShape(60.dp),
        border = null,
        elevation = null,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent,
        )
    ) {
        val shake by animateDpAsState(
            targetValue = if (menuButtonShouldBeShaken) -(15).dp else 0.dp,
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
                .offset(x = shake, y = 0.dp)
                .wrapContentSize(Alignment.Center, unbounded = true),
        ){
            Icon(
                painterResource(R.drawable.crop_square_48px),
                "Show game menu",
                modifier = Modifier.size(30.dp)
            )
            Box(modifier = Modifier.heightIn(1.dp, 32.dp)){
                AutoResizedText(
                    text = "$winRow",
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier
                        .testTag("winRow square")
                        .clearAndSetSemantics { }
                        .offset(0.dp, (-0.5).dp),
                    color = MaterialTheme.colors.standart,
                    autoResizeHeightOrWidth = AutoResizeHeightOrWidth.HEIGHT
                )
            }

            val alpha by animateFloatAsState(
                targetValue = if(winOrLoseShouldBeShown) 1f else 0f,
                animationSpec = if(winOrLoseShouldBeShown)
                    tween(durationMillis = 0, delayMillis = 0, easing = LinearOutSlowInEasing)
                else tween(durationMillis = 700, delayMillis = 700, easing = LinearOutSlowInEasing),
                finishedListener = {ticViewModel.showWinOrLoseIndication(false)}
            )
            if(orientation == Orientation.PORTRAIT){
                Text(
                    text = if (winNotLose) "wins" else "loses",
                    fontSize = 16.nonScaledSp,
                    modifier = Modifier
                        .offset(34.dp, 0.dp)
                        .alpha(alpha)
                        .width(40.dp)
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
    currentMove: Char,
    playingVsAI: Boolean,
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
                                    onClickLabel = "place ${
                                        if (currentMove == CustomCellValues.player1) "X" else "O"
                                    } to ${ticViewModel.convertIndexToLetter(i)} ${j + 1}",
                                    onClick = {
                                        ticViewModel.makeMove(i = i, j = j)
                                        ticViewModel.makeBotMove()
                                    }
                                )
                                .size(fieldSize / gameArray.size)
                                .border(
                                    width = 0.5.dp,
                                    color = MaterialTheme.colors.background,
                                    shape = RoundedCornerShape(0.dp)
                                )
                                .background(MaterialTheme.colors.secondary)
                                .clearAndSetSemantics {
                                    contentDescription = // "B2 is empty, A7 is X, ..."
                                        "${ticViewModel.convertIndexToLetter(i)} ${j + 1} is ${
                                            when (gameArray[i][j].cellText) {
                                                CustomCellValues.player1 -> "X"
                                                CustomCellValues.player2 -> "O"
                                                else -> "empty"
                                            }
                                        }"
                                }
                                .testTag("Cell $i $j")
                        ) {
                            val text = gameArray[i][j].cellText.toString()
                            Text(
                                text = text,
                                style = MaterialTheme.typography.h3,
                                fontSize = ((fieldSize / (gameArray.size + 1)).value * 0.7).toInt().nonScaledSp,
                                fontWeight = if(CustomCellValues.lookAlikeValues.contains(text.first())) FontWeight.Bold else FontWeight.Normal,
                                color = gameArray[i][j].cellColor.color,
                                modifier = Modifier
                                    .testTag("Text $i $j")
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
            .semantics {
                if (botOrGameOverScreen == BotOrGameOverScreen.GAMEOVER) {
                    contentDescription = if (playingVsAI && (currentMove == CustomCellValues.player2)) {
                        when (gameArray[ticViewModel.iOneMoveBefore][ticViewModel.jOneMoveBefore].cellColor) {
                            CellColors.WIN_COLOR -> "Game over, player O won. "
                            CellColors.LOSE_COLOR -> "Game over, player O lost. "
                            else -> "Game over. Draw. "
                        } + "Show menu? "
                    } else "Show menu? "
                }
            }
            .clickable(enabled = botOrGameOverScreen.state.clickable) {
                ticViewModel.saveWinRow()
                ticViewModel.saveWinOrLose()
                ticViewModel.savePlayingVsAi()
                ticViewModel.showMenu(true)
                ticViewModel.setMenuSettings(LoadOrSave.LOAD)
                ticViewModel.saveOrLoadCurrentMove(LoadOrSave.SAVE)
                ticViewModel.changeFirstMoveOnMenuShowing()
            }
        ) {}
    }
}


@Composable
fun TalkBackMessages(
    ticViewModel: TicViewModel,
    botOrGameOverScreen: BotOrGameOverScreen,
    currentMove: Char,
    playingVsAI: Boolean,
    gameArray: Array<Array<Cell>>,
){
    val player = if(currentMove == CustomCellValues.player1) "X" else "O"
    Text(
        text = if(botOrGameOverScreen == BotOrGameOverScreen.GAMEOVER) {
            when(gameArray[ticViewModel.iOneMoveBefore][ticViewModel.jOneMoveBefore].cellColor){
                CellColors.WIN_COLOR -> "Game over, player $player won. "
                CellColors.LOSE_COLOR -> "Game over, player $player lost. "
                else -> "Game over. Draw. "
            }
        } else {
            if(playingVsAI){"AI moved to ${ticViewModel.convertIndexToLetter(Bot.botI)} ${Bot.botJ + 1}. "} else ""
        },
        color = Color.Transparent,
        fontSize = 1.sp
    )
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

    if(autoResizeHeightOrWidth == AutoResizeHeightOrWidth.WIDTH){
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
                        fontSize = resizedTextStyle.fontSize * 0.9
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
            fontWeight = if(CustomCellValues.lookAlikeValues.contains(text.first())) FontWeight.Bold else FontWeight.Normal,
            onTextLayout = { result ->
                if (result.didOverflowHeight) {
                    if (style.fontSize.isUnspecified) {
                        resizedTextStyle = resizedTextStyle.copy(
                            fontSize = defaultFontSize
                        )
                    }
                    resizedTextStyle = resizedTextStyle.copy(
                        fontSize = resizedTextStyle.fontSize * 0.9
                    )
                } else {
                    shouldDraw = true
                }
            }
        )
    }
}


fun Modifier.vertical() =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.height, placeable.width) {
            placeable.place(
                x = -(placeable.width / 2 - placeable.height / 2),
                y = -(placeable.height / 2 - placeable.width / 2)
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