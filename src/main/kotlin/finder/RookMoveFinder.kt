package adam.backend.portfolio.finder

import adam.backend.portfolio.model.Board
import adam.backend.portfolio.model.Color
import adam.backend.portfolio.model.Move
import adam.backend.portfolio.model.Piece
import adam.backend.portfolio.model.Square
import adam.backend.portfolio.finder.MoveFinder.Companion.isTake
import adam.backend.portfolio.finder.MoveFinder.Companion.isValid

class RookMoveFinder {

    fun find(rook: Piece, board: Board, color: Color, square: Square): List<Move> {

        val list: MutableList<Move> = mutableListOf()
        val moveFrom = square.value

        var toCheck = square.up()
        while (isValid(toCheck, board, color, rook)) {
            list.add(Move("$moveFrom${toCheck.value}"))
            if (isTake(board, toCheck, color)) {
                break;
            }
            toCheck = toCheck.up()
        }

        toCheck = square.left()
        while (isValid(toCheck, board, color, rook)) {
            list.add(Move("$moveFrom${toCheck.value}"))
            if (isTake(board, toCheck, color)) {
                break;
            }
            toCheck = toCheck.left()
        }

        toCheck = square.down()
        while (isValid(toCheck, board, color, rook)) {
            list.add(Move("$moveFrom${toCheck.value}"))
            if (isTake(board, toCheck, color)) {
                break;
            }
            toCheck = toCheck.down()
        }

        toCheck = square.right()
        while (isValid(toCheck, board, color, rook)) {
            list.add(Move("$moveFrom${toCheck.value}"))
            if (isTake(board, toCheck, color)) {
                break;
            }
            toCheck = toCheck.right()
        }

        return list
    }

}