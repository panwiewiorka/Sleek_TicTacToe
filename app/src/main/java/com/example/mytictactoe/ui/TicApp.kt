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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytictactoe.Field
import com.example.mytictactoe.R
import com.example.mytictactoe.cells
import com.example.mytictactoe.ui.theme.MyTicTacToeTheme


@Composable
fun TicApp( ticViewModel: TicViewModel = viewModel() ) {
    val ticUiState by ticViewModel.uiState.collectAsState()

    //----------------------------POPUP MENU DIALOG
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
        )
    }

    //-------------------------------------MAIN SCREEN
    BoxWithConstraints(contentAlignment = Alignment.Center) {
        ticViewModel.rememberSettingsDuringOrientationChange(maxWidth > maxHeight)
        if(!ticUiState.landscapeMode) {

            GameField(
                vertPadding = 50.dp,
                horPadding = 0.dp,
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
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = { ticViewModel.cancelMove() },
                    enabled = ticUiState.cancelMoveButtonEnabled,
                    shape = RoundedCornerShape(15.dp),
                    border = null,
                    elevation = null,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0x00000000),
                        disabledBackgroundColor = Color(0x00000000)
                    )
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painterResource(R.drawable.arrow_back_ios_48px), // background grey "disabled" icon
                            null,
                            modifier = Modifier
                                .size(32.dp)
                                .alpha(0.33f)
                                .padding(start = 10.dp)
                        )
                        if (ticUiState.cancelMoveButtonEnabled) {
                            Icon(
                                painterResource(R.drawable.arrow_back_ios_48px), // clickable icon
                                "Cancel move",
                                modifier = Modifier
                                    .size(32.dp)
                                    .padding(start = 10.dp)
                            )
                        }
                    }
                }

                //---------------------------icon  XO
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                ) {
                    val currentMove = if (ticUiState.currentMove == cells.x)
                        painterResource(R.drawable.close_48px)
                    else painterResource(R.drawable.fiber_manual_record_48px)
                    Icon(
                        currentMove,
                        null,
                        modifier = Modifier
                            .size(50.dp)
                            .padding(top = 8.dp, bottom = 10.dp)
                    )
                }

                //---------------------------button  []
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        ticViewModel.cancelWinRowChange(false)
                        ticViewModel.showMenuDialog(!ticUiState.menuDialog)
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
                        Text(text = "${ticUiState.winRow}", fontSize = 18.sp)
                    }
                }
            }
        } else {

            GameField(
                vertPadding = 0.dp,
                horPadding = 50.dp,
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
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 18.dp),
                    onClick = {
                        ticViewModel.cancelWinRowChange(false)
                        ticViewModel.showMenuDialog(!ticUiState.menuDialog)
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
                        Text(text = "${ticUiState.winRow}", fontSize = 18.sp)
                    }
                }

                //---------------------------icon  XO
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 24.dp)
                    ) {
                    val currentMove = if (ticUiState.currentMove == cells.x)
                        painterResource(R.drawable.close_48px)
                    else painterResource(R.drawable.fiber_manual_record_48px)
                    Icon(
                        currentMove,
                        null,
                        modifier = Modifier
                            .size(47.dp)
                            .padding(start = 15.dp)
                    )
                }

                //---------------------------button  <
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = { ticViewModel.cancelMove() },
                    enabled = ticUiState.cancelMoveButtonEnabled,
                    shape = RoundedCornerShape(15.dp),
                    border = null,
                    elevation = null,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0x00000000),
                        disabledBackgroundColor = Color(0x00000000)
                    )
                ) {
                    Box(contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(bottom = 34.dp)) {
                        Icon(
                            painterResource(R.drawable.arrow_back_ios_48px), // background grey "disabled" icon
                            null,
                            modifier = Modifier
                                .size(32.dp)
                                .alpha(0.33f)
                                .padding(start = 10.dp)
                        )
                        if (ticUiState.cancelMoveButtonEnabled) {
                            Icon(
                                painterResource(R.drawable.arrow_back_ios_48px), // clickable icon
                                "Cancel move",
                                modifier = Modifier
                                    .size(32.dp)
                                    .padding(start = 10.dp)
                            )
                        }
                    }
                }
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
        Text(text = "Board size: ${(sizeSliderPosition + 0.5).toInt()}", fontSize = 28.sp)
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
                .padding(top = 4.dp, bottom = 20.dp),
            colors = SliderDefaults.colors(
                activeTrackColor = MaterialTheme.colors.primary,
                inactiveTrackColor = MaterialTheme.colors.primaryVariant
            )
        )
        Text(text = "Win row: ${(winRowSliderPosition + 0.5).toInt()}", fontSize = 28.sp)
        Box(modifier = Modifier.padding(top = 4.dp, bottom = 20.dp),
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
                    modifier = Modifier.width((40 * (winRowSteps + 1) + 20).dp),
                    colors = SliderDefaults.colors(
                        activeTrackColor = MaterialTheme.colors.primary,
                        inactiveTrackColor = MaterialTheme.colors.primaryVariant
                    )
                )
            }
        }
        Row(
            modifier = Modifier.width(204.dp),
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
                    .clickable(true) {}
                //.padding(top = 8.dp, bottom = 10.dp),
            )
            Button(onClick = {
                ticViewModel.resetGame(size)
                ticViewModel.showMenuDialog(false)
                ticViewModel.setWinRow(winRowSliderPosition)
            }) {
                Text(text = "START", fontSize = 20.sp)
            }
        }
    }
}


@Composable
fun GameField(
    vertPadding: Dp,
    horPadding: Dp,
    ticViewModel: TicViewModel = viewModel(),
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
                        ) {
                            Text(
                                text = gameArray[i][j].fieldText,
                                color = gameArray[i][j].textColor.color,
                                fontSize = 36.sp
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
            .clickable(enabled = true) { ticViewModel.showMenuDialog(true) }) {}
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
