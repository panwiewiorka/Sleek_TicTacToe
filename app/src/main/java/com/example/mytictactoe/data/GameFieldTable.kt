package com.example.mytictactoe.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mytictactoe.CellColors
import com.example.mytictactoe.CustomCellValues

@Entity(tableName = "gameFieldTable")
data class GameFieldTable(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 1,
    var cellText: Char = CustomCellValues.EMPTY,
    var isClickable: Boolean = true,
    var cellColor: CellColors = CellColors.STANDART_COLOR,
    )
