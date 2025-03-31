package adam.backend.portfolio.finder

import adam.backend.portfolio.model.Board
import adam.backend.portfolio.model.Color
import adam.backend.portfolio.model.Knight
import adam.backend.portfolio.model.Move
import adam.backend.portfolio.model.Square
import adam.backend.portfolio.finder.MoveFinder.Companion.isValid

class KnightMoveFinder {

    fun find(bishop: Knight, board: Board, color: Color, square: Square): List<Move> {
        val list: MutableList<Move> = mutableListOf()
        val moveFrom = square.value

        var toCheck = square.up().up().right()
        if (isValid(toCheck, board, color, bishop)) {
            list.add(Move("$moveFrom${toCheck.value}"))
        }
        toCheck = square.up().up().left()
        if (isValid(toCheck, board, color, bishop)) {
            list.add(Move("$moveFrom${toCheck.value}"))
        }
        toCheck = square.down().down().left()
        if (isValid(toCheck, board, color, bishop)) {
            list.add(Move("$moveFrom${toCheck.value}"))
        }
        toCheck = square.down().down().right()
        if (isValid(toCheck, board, color, bishop)) {
            list.add(Move("$moveFrom${toCheck.value}"))
        }
        toCheck = square.left().left().up()
        if (isValid(toCheck, board, color, bishop)) {
            list.add(Move("$moveFrom${toCheck.value}"))
        }
        toCheck = square.left().left().down()
        if (isValid(toCheck, board, color, bishop)) {
            list.add(Move("$moveFrom${toCheck.value}"))
        }
        toCheck = square.right().right().down()
        if (isValid(toCheck, board, color, bishop)) {
            list.add(Move("$moveFrom${toCheck.value}"))
        }
        toCheck = square.right().right().up()
        if (isValid(toCheck, board, color, bishop)) {
            list.add(Move("$moveFrom${toCheck.value}"))
        }

        return list
    }

}