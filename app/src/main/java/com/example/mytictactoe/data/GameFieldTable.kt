package com.example.mytictactoe.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mytictactoe.CellColors
import com.example.mytictactoe.CustomCellValues

@Entity(tableName = "gameFieldTable")
data class GameFieldTable(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 1,
    val cellText: Char = CustomCellValues.EMPTY,
    val isClickable: Boolean = true,
    val cellColor: CellColors = CellColors.STANDART_COLOR,
)
