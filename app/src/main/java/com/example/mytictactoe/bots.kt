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

    fun chooseCoordinatesIfCanWin(i: Int, j: Int, winNotLose: Boolean, gameArray: Array<Array<Cell>>){
        botI = i
        botJ = j
        if(winNotLose) {
            botCannotWin = false
        } else {
            gameArray[botI][botJ].cellColor = CellColors.INVISIBLE_COLOR1
        }
    }

    fun chooseCoordinatesIfCanLose(i: Int, j: Int, winNotLose: Boolean, gameArray: Array<Array<Cell>>){
        botI = i
        botJ = j
        if(winNotLose) {
            playerCannotWin = false
        } else {
            gameArray[botI][botJ].cellColor = CellColors.INVISIBLE_COLOR2
        }
    }

    private fun chooseRandomFreeCell(gameArray: Array<Array<Cell>>){
        val mapOfCells = gameArray
            .flatten()
            .mapIndexed{index, value -> index to value}
            .toMap()
        var mapOfEmptyCells = mapOfCells
            .filterValues{(it.cellText == CustomCellValues.EMPTY) && (it.cellColor != CellColors.INVISIBLE_COLOR1) && (it.cellColor != CellColors.INVISIBLE_COLOR2)}
        if(mapOfEmptyCells.isEmpty()){
            mapOfEmptyCells = mapOfCells.filterValues{(it.cellText == CustomCellValues.EMPTY) && (it.cellColor != CellColors.INVISIBLE_COLOR1)}
        }
        if(mapOfEmptyCells.isEmpty()){
            mapOfEmptyCells = mapOfCells.filterValues{it.cellText == CustomCellValues.EMPTY}
        }
        numberToCoordinates(mapOfEmptyCells.keys.random(), gameArray.size)
    }

    fun setMoveCoordinates(
        winRow: Int,
        gameArray: Array<Array<Cell>>,
        changeTurn: () -> Unit,
        checkField: (EndOfCheck, Int, Int) -> Unit,
    ){
        // if bot can win - pick those coordinates
        for (i in gameArray.indices){
            for (j in gameArray[i].indices){
                if((gameArray[i][j].cellText == CustomCellValues.EMPTY) && botCannotWin){
                    checkField(EndOfCheck.ONE_BEFORE_BOT_WIN, i, j)
                }
            }
        }
        // if player can win in next (two) moves - pick those coordinates
        if(botCannotWin){
            changeTurn()
            for (i in gameArray.indices){
                for (j in gameArray[i].indices){
                    if((gameArray[i][j].cellText == CustomCellValues.EMPTY) && playerCannotWin){
                        checkField(EndOfCheck.ONE_BEFORE_PLAYER_WIN, i, j)
                    }
                }
            }
            if((winRow < gameArray.size) && playerCannotWin){
                for (i in gameArray.indices){
                    for (j in gameArray[i].indices){
                        if((gameArray[i][j].cellText == CustomCellValues.EMPTY) && playerCannotWin){
                            checkField(EndOfCheck.TWO_BEFORE_PLAYER_WIN, i, j)
                        }
                    }
                }
            }
            changeTurn()
        }
        // ^^^ check for winning or losing_in_next_moves coordinates, if none - pick random: vvv
        if(botCannotWin && playerCannotWin) chooseRandomFreeCell(gameArray)
        playerCannotWin = true
    }

}