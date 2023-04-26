package com.example.mytictactoe.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mytictactoe.*

@Entity(tableName = "settingsTable")
data class SettingsTable(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 1,
    val theme: AppTheme = AppTheme.AUTO,
    val arraySize: Int = 3,
    val winRow: Int = 3,
    val winNotLose: Boolean = true,
    val playingVsAI: Boolean = false,
    val freeCellsLeft: Int = 9,
    val player1symbol: Char = 'X',
    val player2symbol: Char = 'O',
)