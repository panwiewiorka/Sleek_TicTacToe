package com.example.mytictactoe.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


//val windowInfo = rememberWindowInfo()
//if(windowInfo.screenWidthInfo is WindowInfo.WindowType.Compact) {} else {}


@Composable
fun windowInfo(): WindowInfo {
    val configuration = LocalConfiguration.current
    val lowestDimension = if(configuration.screenHeightDp.dp > configuration.screenWidthDp.dp) configuration.screenWidthDp.dp else configuration.screenHeightDp.dp

    return WindowInfo(
        screenWidthInfo = when {
            configuration.screenWidthDp < 600 -> WindowInfo.WindowType.Compact
            configuration.screenWidthDp < 840 -> WindowInfo.WindowType.Medium
            else -> WindowInfo.WindowType.Expanded
        },
        screenHeightInfo = when {
            configuration.screenHeightDp < 480 -> WindowInfo.WindowType.Compact
            configuration.screenHeightDp < 900 -> WindowInfo.WindowType.Medium
            else -> WindowInfo.WindowType.Expanded
        },
        screenWidth = configuration.screenWidthDp.dp,
        screenHeight = configuration.screenHeightDp.dp,
        sliderUpperLimit = if(lowestDimension > 400.dp) { ((lowestDimension.value.toInt() / 100) + 5).toFloat() } else 8f,
    )
}

data class WindowInfo(
    val screenWidthInfo: WindowType,
    val screenHeightInfo: WindowType,
    val screenWidth: Dp,
    val screenHeight: Dp,
    val sliderUpperLimit: Float,
) {
    sealed class WindowType {
        object Compact: WindowType()
        object Medium: WindowType()
        object Expanded: WindowType()
    }
}