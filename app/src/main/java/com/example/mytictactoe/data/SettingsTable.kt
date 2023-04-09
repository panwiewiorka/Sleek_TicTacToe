package com.example.mytictactoe.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mytictactoe.AppTheme

@Entity(tableName = "settingsTable")
data class SettingsTable(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 1,
    val theme: AppTheme = AppTheme.AUTO,
    val playingVsAI: Boolean = false,
)
