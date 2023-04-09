package com.example.mytictactoe.data

sealed interface SettingsEvent {
    data class SaveTheme(val darkTheme: Boolean): SettingsEvent
    object LoadTheme: SettingsEvent
}