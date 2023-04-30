//@file:Suppress("UNCHECKED_CAST")

package com.example.mytictactoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.mytictactoe.data.SettingsDatabase
import com.example.mytictactoe.ui.TicApp
import com.example.mytictactoe.ui.TicViewModel
import com.example.mytictactoe.ui.theme.MyTicTacToeTheme

class MainActivity : ComponentActivity() {

    private val db by lazy{
        Room.databaseBuilder(
            applicationContext,
            SettingsDatabase::class.java,
            name = "settings.db"
        ).build()
    }
    private val viewModel by viewModels<TicViewModel>(
        factoryProducer = {
            object: ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TicViewModel(
                        db.dao
                    ) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTicTacToeTheme(ticViewModel = viewModel) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    TicApp(
                        ticViewModel = viewModel
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.makeBotMove()
    }

    override fun onPause() {
        super.onPause()
        viewModel.cancelBotWait()
    }

    override fun onStop() {
        super.onStop()
        viewModel.saveGameFieldToDatabase()
        viewModel.saveSettingsToDatabase()
    }
}