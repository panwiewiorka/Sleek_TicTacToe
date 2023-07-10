package com.example.mytictactoe.ui

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytictactoe.AppTheme
import com.example.mytictactoe.CustomCellValues
import com.example.mytictactoe.LoadOrSave
import com.example.mytictactoe.Orientation
import com.example.mytictactoe.R
import com.example.mytictactoe.ui.theme.standart

@Composable
fun MenuWindow(
    ticViewModel: TicViewModel = viewModel(),
    orientation: Orientation,
    theme: AppTheme,
    size: Int,
    winNotLose: Boolean,
    winRow: Int,
    menuIsVisible: Boolean,
    loadMemorySettings: Boolean,
    playingVsAI: Boolean,
    firstMove: Char,
){
    var menuVisibility by remember { mutableStateOf(false) }
    if(menuIsVisible) {menuVisibility = true}
    val lift by animateDpAsState(
        targetValue = if(menuIsVisible) 0.dp else (-10).dp,
        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing),
    )
    val menuAlpha by animateFloatAsState(
        targetValue = if(menuIsVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing),
        finishedListener = {menuVisibility = false}
    )

    if(menuVisibility){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(menuAlpha)
        ){
            val interactionSource = MutableInteractionSource()
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color(0x77000000))
                    .semantics {
                        contentDescription = "Close menu button."
                        onClick(label = "return to the game. ", action = null)
                    }
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        ticViewModel.cancelWinRowChangesDuringTheGame()
                        ticViewModel.cancelWinOrLoseChangesDuringTheGame()
                        ticViewModel.setMenuSettings(LoadOrSave.SAVE)
                        ticViewModel.changeFirstMoveOnMenuShowing()
                        ticViewModel.saveOrLoadCurrentMove(LoadOrSave.LOAD)
                        ticViewModel.showMenu(false)
                        ticViewModel.makeBotMove() // if Bot's turn
                    }
            )

            if(orientation == Orientation.PORTRAIT){
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(0.dp, lift),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    PolicyText(
                        modifier = Modifier
                            .rotate(0f)
                            .alpha(0f),
                        clickable = false
                    )
                    MainMenu(
                        ticViewModel = ticViewModel,
                        theme = theme,
                        size = size,
                        winNotLose = winNotLose,
                        winRow = winRow,
                        loadMemorySettings = loadMemorySettings,
                        playingVsAI = playingVsAI,
                        firstMove = firstMove,
                    )

                    PolicyText(
                        modifier = Modifier
                            .rotate(0f)
                            .alpha(0.2f),
                        clickable = true
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(0.dp, lift),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PolicyText(
                        modifier = Modifier
                            .vertical()
                            .rotate(270f)
                            .alpha(0f),
                        clickable = false
                    )

                    MainMenu(
                        ticViewModel = ticViewModel,
                        theme = theme,
                        size = size,
                        winNotLose = winNotLose,
                        winRow = winRow,
                        loadMemorySettings = loadMemorySettings,
                        playingVsAI = playingVsAI,
                        firstMove = firstMove,
                    )

                    PolicyText(
                        modifier = Modifier
                            .vertical()
                            .rotate(270f)
                            .alpha(0.2f),
                        clickable = true
                    )
                }
            }
        }
    }
}


@Composable
fun PolicyText(
    modifier: Modifier = Modifier,
    clickable: Boolean,
){
    val uriHandler = LocalUriHandler.current
    Text(
        text = "v 1.03  /  Privacy policy",
        fontSize = 14.nonScaledSp,
        color = MaterialTheme.colors.standart,
        modifier = modifier
            .offset(0.dp, (-4).dp)
            .semantics {
                contentDescription = "Privacy policy"
                onClick(label = "open the document in the browser. ", action = null)
            }
            .clickable(enabled = clickable) { uriHandler.openUri("https://docs.google.com/document/d/1K-fJOo8rp4NferJA0X9VbhBavpIp9-PNQLP83_DJSdU/edit?usp=sharing") }
    )
}


@Composable
fun MainMenu(
    ticViewModel: TicViewModel = viewModel(),
    theme: AppTheme,
    size: Int,
    winNotLose: Boolean,
    winRow: Int,
    loadMemorySettings: Boolean,
    playingVsAI: Boolean,
    firstMove: Char,
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
        ticViewModel.setMenuSettings(LoadOrSave.SAVE) // disabling IF condition to load settings only once, when menu is shown
    }

    Card(
        modifier = Modifier.widthIn(220.dp, 300.dp),
        shape = RoundedCornerShape(30.dp),
        elevation = 0.dp,
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Column(
            modifier = Modifier
                .padding(25.dp)
                .widthIn(160.dp, 240.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //-------------------- BOARD SIZE text & slider
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Board size: ${(sizeSliderPosition + 0.5).toInt()}",
                    style = MaterialTheme.typography.h5,
                    color = MaterialTheme.colors.standart,
                    fontSize = 24.nonScaledSp,
                    modifier = Modifier
                        .clearAndSetSemantics { }
                        .testTag("Board Size"),
                )
            }
            Slider(
                value = sizeSliderPosition,
                onValueChange = {
                    sizeSliderPosition = it
                    ticViewModel.setFieldSize(it)
                },
                valueRange = 3f..windowInfo().sliderUpperLimit,
                steps = windowInfo().sliderUpperLimit.toInt() - 4,
                onValueChangeFinished = {
                    if (winRowSliderPosition > sizeSliderPosition) {
                        winRowSliderPosition = sizeSliderPosition
                        ticViewModel.setWinRow(winRowSliderPosition)
                    }
                    winRowSteps = if (sizeSliderPosition > 3) {
                        sizeSliderPosition.toInt() - 4
                    } else 0
                    winRowUpperLimit = sizeSliderPosition
                },
                modifier = Modifier
                    .width(220.dp)
                    .padding(top = 4.dp, bottom = 20.dp)
                    .semantics {
                        contentDescription =
                            "Board size: ${(sizeSliderPosition + 0.5).toInt()} by ${(sizeSliderPosition + 0.5).toInt()}"
                    },
                colors = SliderDefaults.colors(
                    activeTrackColor = MaterialTheme.colors.primary,
                    activeTickColor = MaterialTheme.colors.primary,
                    inactiveTrackColor = MaterialTheme.colors.primaryVariant,
                    inactiveTickColor = MaterialTheme.colors.primaryVariant,
                )
            )
            //-------------------- WIN ROW text
            Box(
                modifier = Modifier.heightIn(
                    10.dp,
                    40.dp
                )   // TODO delete Box() since we don't use AutoResizeText anymore?
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${(winRowSliderPosition + 0.5).toInt()} in a row ",
                        style = MaterialTheme.typography.h5,
                        fontSize = 24.nonScaledSp,
                        modifier = Modifier
                            .clearAndSetSemantics { }
                            .testTag("Win Row"),
                        color = MaterialTheme.colors.standart,
                    )
                    Button(
                        modifier = Modifier
                            .padding(0.dp)
                            .semantics {
                                contentDescription =
                                    "${(winRowSliderPosition + 0.5).toInt()} in a row ${if (winNotLose) "wins" else "loses"}. "
                                onClick(
                                    label = "make ${(winRowSliderPosition + 0.5).toInt()} in a row ${if (winNotLose) "lose" else "win"}",
                                    action = null
                                )
                            },
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
                            Text(
                                text = if (winNotLose) "wins " else "loses",
                                style = MaterialTheme.typography.h5,
                                fontSize = 24.nonScaledSp,
                                color = MaterialTheme.colors.primary,
                                modifier = Modifier
                                    .clearAndSetSemantics { }
                                    .testTag("Win or lose")
                            )
                        }
                    }
                }
            }
            //-------------------- WIN ROW sliders
            Box(
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp),
            ) {
                // background grey slider
                Slider(
                    enabled = false,
                    value = 0f,
                    onValueChange = {},
                    modifier = Modifier
                        .clearAndSetSemantics { }
                        .width(220.dp),
                    colors = SliderDefaults.colors(
                        disabledInactiveTickColor = Color.Transparent,
                        disabledThumbColor = if (winRowUpperLimit == 3f) {
                            MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled)
                                .compositeOver(MaterialTheme.colors.surface)
                        } else Color.Transparent
                    )
                )
                // real winRow slider
                if (winRowUpperLimit != 3f) {
                    Slider(
                        value = winRowSliderPosition,
                        onValueChange = { winRowSliderPosition = it },
                        valueRange = 3f..winRowUpperLimit,
                        steps = winRowSteps,
                        onValueChangeFinished = {
                            /*
                         When winRowUpperLimit == 4f, steps = 0,
                         so we have to manually implement changes to be discrete.

                         It's not implemented like this:
                            if(winRowUpperLimit == 4f){ winRowSliderPosition = if(winRowSliderPosition > 3.5) 4f else 3f }
                        ..because of accessibility users unable to swipe that slider with TalkBack
                        */
                            if (winRowUpperLimit == 4f) {
                                winRowSliderPosition = if (winRow == 3) {
                                    if (winRowSliderPosition > 3) 4f else 3f
                                } else {
                                    if (winRowSliderPosition < 4) 3f else 4f
                                }
                            }
                            ticViewModel.setWinRow(winRowSliderPosition)
                        },
                        modifier = Modifier
                            .width(((200 / (windowInfo().sliderUpperLimit - 3)) * (winRowSteps + 1) + 20).dp)
                            .semantics {
                                contentDescription =
                                    "${(winRowSliderPosition + 0.5).toInt()} in a row ends the game"
                            }
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

            //---------------------BUTTONS: theme, AI, Start
            Row(
                modifier = Modifier.width(204.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                //-------------------- THEME button
                val systemThemeIsDark = isSystemInDarkTheme()
                val themeIconColor =
                    if (theme != AppTheme.AUTO) MaterialTheme.colors.surface else MaterialTheme.colors.primary
                val themeIconBG =
                    if (theme == AppTheme.AUTO) MaterialTheme.colors.surface else MaterialTheme.colors.primary
                Box {
                    Button(
                        onClick = { ticViewModel.changeTheme(systemThemeIsDark) },
                        modifier = Modifier
                            .offset((-5).dp, 0.dp)
                            .size(44.dp)
                            .padding(0.dp)
                            .clearAndSetSemantics { },
                        shape = CircleShape,
                        elevation = null,
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = themeIconBG,
                        )
                    ) {
                        val currentMode = when (theme) {
                            AppTheme.DARK -> painterResource(R.drawable.dark_mode_48px)
                            AppTheme.LIGHT -> painterResource(R.drawable.baseline_light_mode_24)
                            AppTheme.AUTO -> if (systemThemeIsDark) painterResource(R.drawable.dark_mode_48px) else painterResource(
                                R.drawable.light_mode_48px
                            )
                        }
                        Icon(
                            currentMode,
                            "UI theme",
                            tint = themeIconColor,
                        )
                    }
                    val lift by animateDpAsState(
                        targetValue = if (theme == AppTheme.AUTO) (-30).dp else 0.dp,
                        animationSpec = tween(durationMillis = 800, easing = LinearOutSlowInEasing),
                        //finishedListener = {}
                    )
                    val alpha by animateFloatAsState(
                        targetValue = if (theme == AppTheme.AUTO) 0f else 1f,
                        animationSpec = tween(
                            durationMillis = 700,
                            delayMillis = 350,
                            easing = LinearOutSlowInEasing
                        )
                    )
                    Text(
                        text = if (theme == AppTheme.AUTO) "AUTO" else " ",
                        fontSize = 14.nonScaledSp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset((-5).dp, lift)
                            .alpha(alpha)
                    )
                }

                //-------------------- AI button
                val aiIconColor =
                    if (playingVsAI) MaterialTheme.colors.surface else MaterialTheme.colors.primary
                val aiIconBG =
                    if (!playingVsAI) MaterialTheme.colors.surface else MaterialTheme.colors.primary
                Button(
                    onClick = { ticViewModel.switchPlayingVsAiMode() },
                    modifier = Modifier
                        .offset((-3).dp, 0.dp)
                        .size(44.dp)
                        .semantics {
                            contentDescription =
                                "Playing versus AI ${if (playingVsAI) "enabled" else "disabled"} ."
                            onClick(
                                label = "${if (playingVsAI) "disable" else "enable"} ",
                                action = null
                            )
                        }
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
                        contentDescription = null,
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
                        .widthIn(80.dp, 140.dp)
                        .semantics {
                            contentDescription =
                                "Start the game. ${if (firstMove == CustomCellValues.player1) "X" else "O"} ${if (playingVsAI) " is played by AI and" else ""} moves first. "
                            onClick(label = "begin the game", action = null)
                        },
                    elevation = null,
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(15.dp),
                    onClick = {
                        ticViewModel.resetGame(size)
                        ticViewModel.allowChangingFirstMove(true)
                        ticViewModel.setMenuSettings(LoadOrSave.SAVE)
                        ticViewModel.showMenu(false)
                        ticViewModel.makeBotMove()
                    }) {
                    Text(
                        text = "START",
                        modifier = Modifier
                            .clearAndSetSemantics { },
                        style = MaterialTheme.typography.button,
                        fontSize = 20.nonScaledSp,
                    )
                }
            }
        }
    }
}