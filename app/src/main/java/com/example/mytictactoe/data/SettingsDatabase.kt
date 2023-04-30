package com.example.mytictactoe.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [
    SettingsTable::class,
    GameFieldTable::class
                     ], version = 1, exportSchema = false)
abstract class SettingsDatabase: RoomDatabase() {
    abstract val dao: SettingsDao
    //abstract fun dao(): SettingsDao
/*
    companion object {
        @Volatile
        private var Instance: SettingsDatabase? = null

        fun getDatabase(context: Context): SettingsDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, SettingsDatabase::class.java, "settingsTable")
                    // Setting this option in your app's database builder means that Room
                    // permanently deletes all data from the tables in your database when it
                    // attempts to perform a migration with no defined migration path.
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }

 */
}