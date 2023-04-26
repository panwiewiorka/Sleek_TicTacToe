package com.example.mytictactoe.data

import androidx.room.*

@Dao
interface SettingsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun populateSettings(settingsTable: SettingsTable)

    @Upsert
    suspend fun saveSettings(settingsTable: SettingsTable)

//    @Query("SELECT * from settingsTable WHERE id = :id")
//    fun loadSettings(id: Int): Flow<SettingsTable>

    @Query("SELECT * from settingsTable WHERE id = 1")
    fun loadSettings(): SettingsTable
    //fun loadSettings(darkTheme: Boolean): Flow<SettingsTable>



//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    suspend fun populateGameField(gameFieldTable: GameFieldTable)
//
//    @Upsert
//    suspend fun saveGameField(gameFieldTable: GameFieldTable)
//
//    @Query("SELECT * from gameFieldTable")
//    fun loadGameField(): GameFieldTable

}