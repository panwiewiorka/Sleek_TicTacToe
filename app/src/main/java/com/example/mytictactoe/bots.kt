package com.example.mytictactoe

class Bot {
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

    fun checkForWinningMove(
        i: Int,
        j: Int,
        currentMove: CellValues,
        gameArray: Array<Array<Cell>>,
        winRow: Int,
    ){

        // VERTICAL CHECK forward
        var newI = i
        var currentRow = 0

        while((newI + 1 < gameArray.size) && (gameArray[newI + 1][j].cellText == currentMove)){
            currentRow++
            newI++
        }
        // then backward
        newI = i
        while((newI > 0) && (gameArray[newI - 1][j].cellText == currentMove)){
            currentRow++
            newI--
        }
        // if enough X or 0 in a row -> choose those coordinates
        chooseCoordinatesIfCanWin(i, j, currentRow, winRow)

        // HORIZONTAL CHECK
        var newJ = j
        currentRow = 0

        while((newJ + 1 < gameArray.size) && (gameArray[i][newJ + 1].cellText == currentMove)){
            currentRow++
            newJ++
        }
        newJ = j
        while((newJ > 0) && (gameArray[i][newJ - 1].cellText == currentMove)){
            currentRow++
            newJ--
        }

        chooseCoordinatesIfCanWin(i, j, currentRow, winRow)

        // MAIN DIAGONAL CHECK
        newI = i
        newJ = j
        currentRow = 0

        while((newI + 1 < gameArray.size) && (newJ + 1 < gameArray.size) && (gameArray[newI + 1][newJ + 1].cellText == currentMove)){
            currentRow++
            newI++
            newJ++
        }
        newI = i
        newJ = j
        while((newI > 0) && (newJ > 0) && (gameArray[newI - 1][newJ - 1].cellText == currentMove)){
            currentRow++
            newI--
            newJ--
        }

        chooseCoordinatesIfCanWin(i, j, currentRow, winRow)

        // OTHER DIAGONAL CHECK
        newI = i
        newJ = j
        currentRow = 0

        while((newI + 1 < gameArray.size) && (newJ > 0) && (gameArray[newI + 1][newJ - 1].cellText == currentMove)){
            currentRow++
            newI++
            newJ--
        }
        newI = i
        newJ = j
        while((newI > 0) && (newJ + 1 < gameArray.size) && (gameArray[newI - 1][newJ + 1].cellText == currentMove)){
            currentRow++
            newI--
            newJ++
        }

        chooseCoordinatesIfCanWin(i, j, currentRow, winRow)
    }

    fun chooseCoordinatesIfCanWin(i: Int, j: Int, currentRow: Int, winRow: Int){
        if (currentRow >= (winRow - 1)) {
            botI = i
            botJ = j
            botCannotWin = false
            return
        }
    }
}