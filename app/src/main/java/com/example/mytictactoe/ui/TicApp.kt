package com.example.mytictactoe.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytictactoe.ui.theme.MyTicTacToeTheme


@Composable
fun TicApp( ticViewModel: TicViewModel = viewModel() ) {
    val ticUiState by ticViewModel.uiState.collectAsState()

    //----------------------------POPUP
    if (ticUiState.menuDialog){
        AlertDialog(
            onDismissRequest = {  },
            buttons = {
                SizeSlider (
                    { ticViewModel.setSize(it) },
                    { ticViewModel.resetGame(ticUiState.gameArray.size)},
                    { ticViewModel.showMenuDialog(false) },
                    size = ticUiState.gameArray.size
                )
            },
            shape = RoundedCornerShape(15.dp),
        )
    }

    //----------------------------MAIN

    Box(modifier = Modifier
        .aspectRatio(1f)
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
                                .background(Color(0xFF666666))
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
                                color = Color(ticUiState.gameArray[i][j].textColor),
                                fontSize = 36.sp
                            )
                        }
                    }
                }
            }
        }
    }
    if (ticUiState.lastClick) {
        Box(modifier = Modifier
            .fillMaxSize()
            .clickable(enabled = true) { ticViewModel.showMenuDialog(true) }) {}
    }
}


@Composable
fun SizeSlider(
               setSize: (Float) -> Unit,
               resetGame: (Int) -> Unit,
               showMenuDialog: (Boolean) -> Unit,
               size: Int
){
    var sliderPosition by remember { mutableStateOf(3f) }
    sliderPosition = size.toFloat()
    Column(
        modifier = Modifier.padding(25.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Grid size: $size", fontSize = 28.sp)
        Slider(
            value = sliderPosition,
            onValueChange = {
                sliderPosition = it
                setSize(it) },
            valueRange = 3f..8f,
            steps = 4,
            modifier = Modifier
                .width(220.dp)
                .padding(15.dp)
        )
        Button(onClick = {resetGame(size); showMenuDialog(false)}) {
            Text(text = "Start")
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
