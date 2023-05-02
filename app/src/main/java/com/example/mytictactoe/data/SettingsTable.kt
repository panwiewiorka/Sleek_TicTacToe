package com.example.mytictactoe.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mytictactoe.*

@Entity(tableName = "settingsTable")
data class SettingsTable(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 1,
    val theme: AppTheme = AppTheme.AUTO,
    val memorySettings: LoadOrSave = LoadOrSave.LOAD,
    val menuIsVisible: Boolean = true,
    val fieldSize: Int = 0,
    val arraySize: Int = 3,
    val winRow: Int = 3,
    val savedWinRow: Int = 3,
    val winNotLose: Boolean = true,
    val savedWinNotLose: Boolean = true,
    val playingVsAI: Boolean = false,
    val firstMove: Char = CustomCellValues.player1,
    val currentMove: Char = CustomCellValues.player1,
    val cancelMoveButtonEnabled: Boolean = false,
    val botOrGameOverScreen: BotOrGameOverScreen = BotOrGameOverScreen.HIDDEN,
    val iOneMoveBefore: Int = 0,
    val jOneMoveBefore: Int = 0,
    val iTwoMovesBefore: Int = 0,
    val jTwoMovesBefore: Int = 0,
    val freeCellsLeft: Int = 9,
    val winIsImpossible: Boolean = true,
    val canChangeFirstMove: Boolean = false,
    val savedMove: Char = CustomCellValues.player1,
    val savedPlayingVsAi: Boolean = false,
    val player1symbol: Char = CustomCellValues.player1,
    val player2symbol: Char = CustomCellValues.player2,
)