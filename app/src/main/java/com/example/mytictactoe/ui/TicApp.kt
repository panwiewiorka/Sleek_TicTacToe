package com.example.mytictactoe.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytictactoe.Orientation.LANDSCAPE
import com.example.mytictactoe.Orientation.PORTRAIT
import com.example.mytictactoe.ui.theme.MyTicTacToeTheme

val Int.nonScaledSp
    @Composable
    get() = (this / LocalDensity.current.fontScale).sp

@Composable
fun TicApp(
    ticViewModel: TicViewModel = viewModel()
) {
    val ticUiState by ticViewModel.uiState.collectAsState()

    //-------------------------------------MAIN SCREEN
    BoxWithConstraints(
        contentAlignment = Alignment.Center,
    ) {
        val orientation = if(maxWidth > maxHeight) LANDSCAPE else PORTRAIT
        ticViewModel.rememberSettingsDuringOrientationChange(orientation)

        if(orientation == PORTRAIT) {

            GameField(
                ticViewModel = ticViewModel,
                currentMove = ticUiState.currentMove,
                playingVsAI = ticUiState.playingVsAI,
                vertPadding = 50.dp,
                horPadding = 0.dp,
                gameArray = ticUiState.gameArray,
                botOrGameOverScreen = ticUiState.botOrGameOverScreen,
            )

            // accessibility instructions, invisible
            TalkBackMessages(
                ticViewModel = ticViewModel,
                botOrGameOverScreen = ticUiState.botOrGameOverScreen,
                currentMove = ticUiState.currentMove,
                playingVsAI = ticUiState.playingVsAI,
                gameArray = ticUiState.gameArray,
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
                    ticViewModel = ticViewModel,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .testTag("Cancel Button"),
                    cancelMoveButtonEnabled = ticUiState.cancelMoveButtonEnabled,
                    cancelMove = {
                        ticViewModel.cancelBotWait()
                        ticViewModel.cancelMove()
                                 },
                    playingVsAI = ticUiState.playingVsAI,
                    currentMove = ticUiState.currentMove,
                    gameArray = ticUiState.gameArray,
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
                    winNotLose = ticUiState.winNotLose,
                    menuButtonShouldBeShaken = ticUiState.menuButtonShouldBeShaken,
                    winOrLoseShouldBeShown = ticUiState.winOrLoseShouldBeShown,
                    orientation = orientation,
                )
            }
        } else {

            GameField(
                ticViewModel = ticViewModel,
                currentMove = ticUiState.currentMove,
                playingVsAI = ticUiState.playingVsAI,
                vertPadding = 0.dp,
                horPadding = 70.dp,
                gameArray = ticUiState.gameArray,
                botOrGameOverScreen = ticUiState.botOrGameOverScreen,
                )

            // accessibility instructions, invisible
            TalkBackMessages(
                ticViewModel = ticViewModel,
                botOrGameOverScreen = ticUiState.botOrGameOverScreen,
                currentMove = ticUiState.currentMove,
                playingVsAI = ticUiState.playingVsAI,
                gameArray = ticUiState.gameArray,
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
                        .testTag("Menu Button"),
                    winRow = ticUiState.winRow,
                    winNotLose = ticUiState.winNotLose,
                    menuButtonShouldBeShaken = ticUiState.menuButtonShouldBeShaken,
                    winOrLoseShouldBeShown = ticUiState.winOrLoseShouldBeShown,
                    orientation = orientation,
                    )

                //---------------------------icon  XO
                CurrentMoveAndAiIcons(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .offset(0.dp, (-12).dp),
                    playingVsAI = ticUiState.playingVsAI,
                    menuIsVisible = ticUiState.menuIsVisible,
                    currentMove = ticUiState.currentMove,
                    showCustomCellDialog = {ticViewModel.showCustomCellDialog(true)},
                    cancelBotWait = {ticViewModel.cancelBotWait()},
                    botIconOffsetY = (-34).dp,
                )

                //---------------------------button  <
                CancelButton(
                    ticViewModel = ticViewModel,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .testTag("Cancel Button"),
                    cancelMoveButtonEnabled = ticUiState.cancelMoveButtonEnabled,
                    cancelMove = {
                        ticViewModel.cancelBotWait()
                        ticViewModel.cancelMove()
                    },
                    playingVsAI = ticUiState.playingVsAI,
                    currentMove = ticUiState.currentMove,
                    gameArray = ticUiState.gameArray,
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
            backgroundColor = Color.Transparent,
            shape = RectangleShape,
            modifier = Modifier
                .height(100.dp)
                .width(200.dp)
                .wrapContentSize(align = Alignment.Center, unbounded = true),
        )
    }

    //-------------------------------------MENU WINDOW
    MenuWindow(
        ticViewModel = ticViewModel,
        orientation = ticUiState.orientation,
        theme = ticUiState.theme,
        size = ticUiState.gameArray.size,
        winNotLose = ticUiState.winNotLose,
        winRow = ticUiState.winRow,
        menuIsVisible = ticUiState.menuIsVisible,
        loadMemorySettings = ticUiState.memorySettings.loadOrSave,
        playingVsAI = ticUiState.playingVsAI,
        firstMove = ticUiState.firstMove,
    )
}



//========================================
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    MyTicTacToeTheme {
        TicApp()
    }
}
