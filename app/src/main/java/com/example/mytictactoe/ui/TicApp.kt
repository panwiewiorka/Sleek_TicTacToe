package com.example.mytictactoe.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytictactoe.R
import com.example.mytictactoe.ui.theme.CellBackground
import com.example.mytictactoe.ui.theme.MyTicTacToeTheme


@Composable
fun TicApp( ticViewModel: TicViewModel = viewModel() ) {
    val ticUiState by ticViewModel.uiState.collectAsState()

    //----------------------------POPUP MENU
    if (ticUiState.menuDialog){
        AlertDialog(
            onDismissRequest = {
                ticViewModel.showMenuDialog(false)
                if(ticUiState.menuButtonIsClicked)
                    ticViewModel.cancelWinRowChange(true) },
            buttons = {
                MainMenu (
                    { ticViewModel.setSize(it) },
                    { ticViewModel.setWinRow(it) },
                    { ticViewModel.resetGame(ticUiState.gameArray.size)},
                    { ticViewModel.showMenuDialog(false) },
                    size = ticUiState.gameArray.size,
                    winRow = ticUiState.winRow,
                    memorySettings = ticUiState.memorySettings,
                    { ticViewModel.loadSettingsFromUiState(false) }
                )
            },
            shape = RoundedCornerShape(15.dp),
        )
    }

    //-------------------------------MAIN
    
    BoxWithConstraints(contentAlignment = Alignment.Center) {
        ticViewModel.rememberSettingsDuringOrientationChange(maxWidth > maxHeight)
        Column() {
            //------------------------TOP BAR with ICONS
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .weight(1f),
                horizontalArrangement = Arrangement.SpaceAround) {
                Box(contentAlignment = Alignment.Center){
                    Icon(painterResource(R.drawable.arrow_back_ios_48px),
                        "Cancel move",
                        modifier = Modifier
                            .size(32.dp)
                            .padding(start = 10.dp))
                }
                Box(contentAlignment = Alignment.Center){
                    val currentMove = if(ticUiState.currentMove == "X")
                        painterResource(R.drawable.close_48px)
                    else painterResource(R.drawable.fiber_manual_record_48px)
                    Icon(currentMove,
                        null,
                        modifier = Modifier
                            .size(32.dp)
                    )
                }
                Box(contentAlignment = Alignment.Center,
                    modifier = Modifier.clickable {
                        ticViewModel.cancelWinRowChange(false)
                        ticViewModel.showMenuDialog(!ticUiState.menuDialog)
                    }){
                    Icon(painterResource(R.drawable.crop_square_48px),
                        "Menu",
                        modifier = Modifier.size(30.dp)
                    )
                    Text(text = "${ticUiState.winRow}", fontSize = 18.sp)
                }
            }
            Spacer(modifier = Modifier.weight(1f))

            //-----------------------------GAME FIELD----------------------------
            Box(
                modifier = Modifier
                    .aspectRatio(1f, ticUiState.landscapeMode)
            ) {
                Column {
                    for (i in ticUiState.gameArray.indices) {
                        Row {
                            for (j in ticUiState.gameArray[i].indices) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .padding(1.dp)
                                        .weight(1f)
                                        .background(CellBackground)
                                        .clickable(
                                            enabled = ticUiState.gameArray[i][j].isClickable,
                                            onClick = {
                                                ticViewModel.makeMove(
                                                    i = i,
                                                    j = j,
                                                    currentMove = ticUiState.currentMove
                                                    // TODO: ^ избавиться от этого параметра? (брать в функции makeMove напрямую из uiState)
                                                )
                                            }
                                        )
                                ) {
                                    Text(
                                        text = ticUiState.gameArray[i][j].fieldText,
                                        color = ticUiState.gameArray[i][j].textColor,
                                        fontSize = 36.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.weight(2f))
        }
        //----------------------------------LAST SCREEN (win / draw)
        if (ticUiState.lastClickScreen) {
            Box(modifier = Modifier
                .fillMaxSize()
                .clickable(enabled = true) { ticViewModel.showMenuDialog(true) }) {}
        }
    }
}


@Composable
fun MainMenu(
               setSize: (Float) -> Unit,
               setWinRow: (Float) -> Unit,
               resetGame: (Int) -> Unit,
               showMenuDialog: (Boolean) -> Unit,
               size: Int,
               winRow: Int,
               memorySettings: Boolean,
               setSettingsFromMemory: (Boolean) -> Unit
){
    var sizeSliderPosition by remember { mutableStateOf(3f) }
    var winRowSliderPosition by remember { mutableStateOf(3f) }
    var winRowUpperLimit by remember { mutableStateOf(3f) }
    var winRowSteps by remember { mutableStateOf(0) }
    // ^^ by remember { mutableStateOf() }   made for sliders local operation. Settings are saved in UiState.
    if(memorySettings){
        sizeSliderPosition = size.toFloat()
        winRowSliderPosition = winRow.toFloat()
        winRowUpperLimit = sizeSliderPosition
        if(sizeSliderPosition > 3){
            winRowSteps = sizeSliderPosition.toInt() - 4
            // TODO winRow slider visibility
        } else {
            winRowSteps = 0
            // TODO winRow slider visibility
        }
        //resetGame(size)
        setSettingsFromMemory(false)
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
                setSize(it) },
            valueRange = 3f..8f,
            steps = 4,
            onValueChangeFinished = {
                if(winRowSliderPosition > sizeSliderPosition){
                    winRowSliderPosition = sizeSliderPosition
                    setWinRow(winRowSliderPosition)
                }
                if(sizeSliderPosition > 3){
                    winRowSteps = sizeSliderPosition.toInt() - 4
                    // TODO winRow slider visibility
                } else {
                    winRowSteps = 0
                    // TODO winRow slider visibility
                }
                winRowUpperLimit = sizeSliderPosition
                                    },
            modifier = Modifier
                .width(220.dp)
                .padding(top = 4.dp, bottom = 20.dp)
        )
        Text(text = "Win row: ${(winRowSliderPosition + 0.5).toInt()}", fontSize = 28.sp)
        Box(modifier = Modifier.padding(top = 4.dp, bottom = 20.dp),
        ){
            Slider(
                enabled = false,
                value = winRowSliderPosition,
                onValueChange = {},
                valueRange = 3f..8f,
                steps = 4,
                modifier = Modifier.width(220.dp)
            )
            Slider(
                enabled = winRowUpperLimit != 3f,
                value = winRowSliderPosition,
                onValueChange = {
                    winRowSliderPosition = it
                    setWinRow(it) },
                valueRange = 3f..winRowUpperLimit,
                steps = winRowSteps,
                onValueChangeFinished = {
                    // When winRowUpperLimit == 4f, steps == 0, so we have to manually implement changes to be discrete.
                    if(winRowUpperLimit == 4f){
                        winRowSliderPosition = if(winRowSliderPosition > 3.5) 4f else 3f
                    }
                },
                modifier = Modifier
                    .width((40 * (winRowSteps + 1) + 20).dp)
            )
        }
        Button(onClick = {
            resetGame(size)
            showMenuDialog(false)
            setWinRow(winRowSliderPosition)
        }) {
            Text(text = "START", fontSize = 20.sp)
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
