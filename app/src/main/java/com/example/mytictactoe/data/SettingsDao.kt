package com.example.mytictactoe.data

import androidx.room.*
import com.example.mytictactoe.AppTheme
import kotlinx.coroutines.flow.Flow

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


}