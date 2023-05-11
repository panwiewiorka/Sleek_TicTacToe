package com.example.mytictactoe

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.mytictactoe.CustomCellValues.player1
import com.example.mytictactoe.CustomCellValues.player2
import com.example.mytictactoe.ui.TicViewModel
import com.example.mytictactoe.data.SettingsDao
import com.example.mytictactoe.data.SettingsDatabase
import com.example.mytictactoe.data.SettingsTable
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import java.io.IOException

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class TicUnitTests {

    private lateinit var daoo: SettingsDao
    private lateinit var settingsDatabase: SettingsDatabase

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        settingsDatabase = Room.inMemoryDatabaseBuilder(context, SettingsDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        daoo = settingsDatabase.dao
    }

    private var item1 = SettingsTable()

    private val viewModel = TicViewModel(daoo)

    @Test
    fun `ticViewModel check Win`() {
        val gameArray = viewModel.uiState.value.gameArray
        for(i in gameArray.indices){
            gameArray[i][0].cellText = player1
        }
        viewModel.checkField(EndOfCheck.WIN,0, 0)
        assertEquals(CellColors.WIN_COLOR, gameArray[0][0].cellColor)
        assert(viewModel.uiState.value.botOrGameOverScreen.state.visible)
        assert(viewModel.uiState.value.botOrGameOverScreen.state.clickable)
    }

    @Test
    fun `ticViewModel check Draw`() {
        val gameArray = viewModel.uiState.value.gameArray
        val x = player1
        val o = player2
        gameArray[0] = arrayOf(Cell(x), Cell(o), Cell(x))
        gameArray[1] = arrayOf(Cell(x), Cell(o), Cell(x))
        gameArray[2] = arrayOf(Cell(o), Cell(x), Cell(o))
        viewModel.checkDraw()
        assertEquals(CellColors.DRAW_COLOR, gameArray[0][0].cellColor)
        assert(viewModel.uiState.value.botOrGameOverScreen.state.visible)
        assert(viewModel.uiState.value.botOrGameOverScreen.state.clickable)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        settingsDatabase.close()
    }
}