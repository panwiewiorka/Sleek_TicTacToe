package com.example.mytictactoe.data

import androidx.room.*

@Dao
interface SettingsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun populateSettings(settingsTable: SettingsTable)

    @Upsert
    suspend fun saveSettings(settingsTable: SettingsTable)

    @Query("SELECT * from settingsTable WHERE id = 1")
    fun loadSettings(): SettingsTable

    //---------

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun populateGameField(gameFieldTable: GameFieldTable)

    @Upsert
    suspend fun saveGameField(gameFieldTable: GameFieldTable)

    @Query("SELECT * from gameFieldTable WHERE id = :id")
    fun loadGameField(id: Int): GameFieldTable

}