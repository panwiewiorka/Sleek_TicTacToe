package com.example.mytictactoe


object Bot {

    var botI = 0
    var botJ = 0
    var botCannotWin = true
    var playerCannotWin = true

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
    }

    fun chooseCoordinatesIfCanLose(i: Int, j: Int){
        botI = i
        botJ = j
        playerCannotWin = false
    }

    private fun chooseRandomFreeCell(gameArray: Array<Array<Cell>>){
        val mapOfEmptyCells = gameArray
            .flatten()
            .mapIndexed{index, value -> index to value}
            .toMap()
            .filterValues{it.cellText == CellValues.EMPTY}
        numberToCoordinates(mapOfEmptyCells.keys.random(), gameArray.size)
    }

    fun setMoveCoordinates(
        winRow: Int,
        gameArray: Array<Array<Cell>>,
        changeTurn: () -> Unit,
        checkField: (EndOfCheck, Int, Int) -> Unit,
    ){
        // check for bot winning coordinates
        for (i in gameArray.indices){
            for (j in gameArray[i].indices){
                if((gameArray[i][j].cellText == CellValues.EMPTY) && botCannotWin){
                    checkField(EndOfCheck.ONE_BEFORE_BOT_WIN, i, j)
                }
            }
        }
        // check if player can win in next move
        if(botCannotWin){
            changeTurn()
            for (i in gameArray.indices){
                for (j in gameArray[i].indices){
                    if((gameArray[i][j].cellText == CellValues.EMPTY) && playerCannotWin){
                        checkField(EndOfCheck.ONE_BEFORE_PLAYER_WIN, i, j)
                    }
                }
            }
            if((winRow < gameArray.size) && playerCannotWin){
                for (i in gameArray.indices){
                    for (j in gameArray[i].indices){
                        if((gameArray[i][j].cellText == CellValues.EMPTY) && playerCannotWin){
                            checkField(EndOfCheck.TWO_BEFORE_PLAYER_WIN, i, j)
                        }
                    }
                }
            }
            changeTurn()
        }
        // ^^^ check for winning or losing_in_next_move coordinates, if none - pick random: vvv
        if(botCannotWin && playerCannotWin) chooseRandomFreeCell(gameArray)
        playerCannotWin = true
    }

}