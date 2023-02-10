package com.example.mytictactoe

import com.example.mytictactoe.ui.TicViewModel

class Bot() {

    var botI = 0
    var botJ = 0
    var botCannotWin: Boolean = true

//    fun coordinatesToNumber(i: Int, j: Int, size: Int): Int{
//        return (i * size) + j
//    }

    private fun numberToCoordinates(n: Int, size: Int){
        botI = n / size
        botJ = n % size
    }

    fun chooseCoordinatesIfCanWin(i: Int, j: Int){
        botI = i
        botJ = j
        botCannotWin = false
        return
    }

    fun chooseRandomFreeCell(
        gameArray: Array<Array<Cell>>
    ){
        val mapOfEmptyCells = gameArray
            .flatten()
            .mapIndexed{index, value -> index to value}
            .toMap()
            .filterValues{it.cellText == CellValues.EMPTY}
        numberToCoordinates(mapOfEmptyCells.keys.random(), gameArray.size)
    }

    fun setMoveCoordinates(gameArray: Array<Array<Cell>>, checkField: (EndOfCheck, Int, Int) -> Unit){
        for (i in gameArray.indices){
            for (j in gameArray[i].indices){
                if((gameArray[i][j].cellText == CellValues.EMPTY) && botCannotWin){
                    checkField(EndOfCheck.ONE_BEFORE_WIN, i, j)
                }
            }
        }
        if(botCannotWin) chooseRandomFreeCell(gameArray)
    }

}