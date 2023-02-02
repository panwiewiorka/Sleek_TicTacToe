package com.example.mytictactoe.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
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
import com.example.mytictactoe.CellValues
import com.example.mytictactoe.Field
import com.example.mytictactoe.R
import com.example.mytictactoe.ui.theme.MyTicTacToeTheme
import com.example.mytictactoe.ui.theme.menuBorder


@Composable
fun TicApp( ticViewModel: TicViewModel = viewModel() ) {
    val ticUiState by ticViewModel.uiState.collectAsState()

    //----------------------------POPUP MENU SCREEN
    if (ticUiState.menuDialog){
        AlertDialog(
            onDismissRequest = {
                ticViewModel.showMenuDialog(false)
                ticViewModel.cancelWinRowChange(true) },
            buttons = {
                MainMenu (
                    size = ticUiState.gameArray.size,
                    winRow = ticUiState.winRow,
                    memorySettings = ticUiState.memorySettings,
                )
            },
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .testTag("Menu Window")
                .widthIn(220.dp, 300.dp)
                .border(
                    3.dp,
                    color = MaterialTheme.colors.menuBorder,
                    shape = RoundedCornerShape(15.dp)
                ),
        )
    }

    //-------------------------------------MAIN SCREEN
    BoxWithConstraints(contentAlignment = Alignment.Center) {
        ticViewModel.rememberSettingsDuringOrientationChange(maxWidth > maxHeight)
        if(!ticUiState.landscapeMode) {

            GameField(
                vertPadding = 50.dp,
                horPadding = 0.dp,
                cellFontSize = ticUiState.cellFontSize,
                gameArray = ticUiState.gameArray,
                lastClickScreen = ticUiState.lastClickScreen
            )

            //-----------------------VERTICAL LAYOUT: TOP BAR with ICONS
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
                )

                //---------------------------button  []
                MenuButton(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("Menu Button"),
                    menuDialog = ticUiState.menuDialog,
                    winRow = ticUiState.winRow,
                    cancelWinRowChange = {ticViewModel.cancelWinRowChange(false)},
                    showMenuDialog = {ticViewModel.showMenuDialog(!ticUiState.menuDialog)},
                )
            }
        } else {

            GameField(
                vertPadding = 0.dp,
                horPadding = 50.dp,
                cellFontSize = ticUiState.cellFontSize,
                gameArray = ticUiState.gameArray,
                lastClickScreen = ticUiState.lastClickScreen
            )

            //_______________________HORIZONTAL LAYOUT: LEFT BAR with ICONS
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
                    menuDialog = ticUiState.menuDialog,
                    winRow = ticUiState.winRow,
                    cancelWinRowChange = {ticViewModel.cancelWinRowChange(false)},
                    showMenuDialog = {ticViewModel.showMenuDialog(!ticUiState.menuDialog)},
                )

                //---------------------------icon  XO
                XOButton(
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 24.dp),
                    currentMove = ticUiState.currentMove,
                    paddingStart = 15.dp,
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


@Composable
fun MainMenu(
    ticViewModel: TicViewModel = viewModel(),
    size: Int,
    winRow: Int,
    memorySettings: Boolean,
){
    var sizeSliderPosition by remember { mutableStateOf(3f) }
    var winRowSliderPosition by remember { mutableStateOf(3f) }
    var winRowUpperLimit by remember { mutableStateOf(3f) }
    var winRowSteps by remember { mutableStateOf(0) }
    // ^^ by remember { mutableStateOf() }   made for sliders local operation.
    // Settings are saved in UiState.
    if(memorySettings){
        sizeSliderPosition = size.toFloat()
        winRowSliderPosition = winRow.toFloat()
        winRowUpperLimit = sizeSliderPosition
        winRowSteps = if(sizeSliderPosition > 3){ sizeSliderPosition.toInt() - 4 } else 0
        ticViewModel.loadSettingsFromUiState(false)
    }
    Column(
        modifier = Modifier.padding(25.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //Text(text = "Board size: ${(sizeSliderPosition + 0.5).toInt()}", fontSize = 28.sp)
        Box(
            modifier = Modifier.heightIn(10.dp, 60.dp)
        ){
            AutoResizedText(
                text = "Board size: ${(sizeSliderPosition + 0.5).toInt()}",
                style = MaterialTheme.typography.h5,
                widthNotHeight = false,
                modifier = Modifier.testTag("Board Size"),
            )
        }
        Slider(
            value = sizeSliderPosition,
            onValueChange = {
                sizeSliderPosition = it
                ticViewModel.setSize(it) },
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
        //Text(text = "Win row: ${(winRowSliderPosition + 0.5).toInt()}", fontSize = 28.sp)
        Box(
            modifier = Modifier.heightIn(10.dp, 60.dp)
        ){
            AutoResizedText(
                text = "Win row: ${(winRowSliderPosition + 0.5).toInt()}",
                style = MaterialTheme.typography.h5,
                widthNotHeight = false,
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
            Button(
                modifier = Modifier.widthIn(60.dp, 140.dp),
                onClick = {
                ticViewModel.resetGame(size)
                ticViewModel.showMenuDialog(false)
                ticViewModel.setWinRow(winRowSliderPosition)
            }) {
                //Text(text = "START", fontSize = 20.sp)
                AutoResizedText(
                    text = "START",
                    style = MaterialTheme.typography.button,
                    widthNotHeight = true
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
            Icon(
                painterResource(R.drawable.arrow_back_ios_48px), // background grey "disabled" icon
                null,
                modifier = Modifier
                    .size(32.dp)
                    .alpha(0.33f)
                    .padding(start = paddingStart)
            )
            if (cancelMoveButtonEnabled) {
                Icon(
                    painterResource(R.drawable.arrow_back_ios_48px), // clickable icon
                    "Cancel move",
                    modifier = Modifier
                        .size(32.dp)
                        .padding(start = paddingStart)
                        .testTag("Cancel Icon")
                )
            }
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
){
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        val currentMoveIcon = if (currentMove == CellValues.X)
            painterResource(R.drawable.close_48px)
        else painterResource(R.drawable.fiber_manual_record_48px)
        val testCurrentMoveString = if (currentMove == CellValues.X)
            "currentMove: X" else "currentMove: 0"
        Icon(
            currentMoveIcon,
            null,
            modifier = Modifier
                .size(50.dp)
                .padding(top = paddingTop, bottom = paddingBottom, start = paddingStart, end = paddingEnd)
                .testTag(testCurrentMoveString)
        )
    }
}


@Composable
fun MenuButton(
    modifier: Modifier,
    menuDialog: Boolean,
    winRow: Int,
    cancelWinRowChange: (Boolean) -> Unit,
    showMenuDialog: (Boolean) -> Unit,
){
    Button(
        modifier = modifier,
        onClick = {
            cancelWinRowChange(false)
            showMenuDialog(!menuDialog)
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
                    widthNotHeight = false
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
    gameArray: Array<Array<Field>>,
    lastClickScreen: Boolean
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
                                    onClick = { ticViewModel.makeMove(i = i, j = j) }
                                )
                                .testTag("Cell $i $j")
                        ) {
                            AutoResizedText(
                                text = gameArray[i][j].fieldText.cellValue,
                                style = MaterialTheme.typography.h3,
                                observedChanges = fieldSize / gameArray.size,
                                fontSize = cellFontSize,
                                color = gameArray[i][j].textColor.color,
                                widthNotHeight = false,
                                modifier = Modifier.testTag("Text $i $j")
                            )
                        }
                    }
                }
            }
        }
    }
    //------------------------LAST SCREEN (win / draw)
    if (lastClickScreen) {
        Box(modifier = Modifier
            .fillMaxSize()
            .testTag("Last Screen")
            .clickable(enabled = true) { ticViewModel.showMenuDialog(true) }) {}
    }
}


@Composable
fun AutoResizedText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    observedChanges: Dp = 1.dp,
    fontSize: TextUnit = style.fontSize,
    color: Color = style.color,
    widthNotHeight: Boolean,
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
    var savedChanges by remember {
        mutableStateOf(1.dp)
    }
    if (savedChanges != observedChanges) {
        fontSizeChanged = true
        savedChanges = observedChanges
    }

    if(fontSizeChanged){
        resizedTextStyle = resizedTextStyle.copy(
            fontSize = fontSize
        )
        fontSizeChanged = false
    }

    val defaultFontSize = MaterialTheme.typography.body1.fontSize

    if(widthNotHeight){
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
