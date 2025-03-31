package adam.backend.portfolio.finder

import adam.backend.portfolio.model.Board
import adam.backend.portfolio.model.Color
import adam.backend.portfolio.model.Move
import adam.backend.portfolio.model.Piece
import adam.backend.portfolio.model.Square
import adam.backend.portfolio.finder.MoveFinder.Companion.isTake
import adam.backend.portfolio.finder.MoveFinder.Companion.isValid

class BishopMoveFinder {

    fun find(bishop: Piece, board: Board, color: Color, square: Square): List<Move> {
        val list: MutableList<Move> = mutableListOf()
        val moveFrom = square.value

        var toCheck = square.up().right()
        while (isValid(toCheck, board, color, bishop)) {
            list.add(Move("$moveFrom${toCheck.value}"))
            if (isTake(board, toCheck, color)) {
                break;
            }
            toCheck = toCheck.up().right()
        }

        toCheck = square.up().left()
        while (isValid(toCheck, board, color, bishop)) {
            list.add(Move("$moveFrom${toCheck.value}"))
            if (isTake(board, toCheck, color)) {
                break;
            }
            toCheck = toCheck.up().left()
        }

        toCheck = square.down().left()
        while (isValid(toCheck, board, color, bishop)) {
            list.add(Move("$moveFrom${toCheck.value}"))
            if (isTake(board, toCheck, color)) {
                break;
            }
            toCheck = toCheck.down().left()
        }

        toCheck = square.down().right()
        while (isValid(toCheck, board, color, bishop)) {
            list.add(Move("$moveFrom${toCheck.value}"))
            if (isTake(board, toCheck, color)) {
                break;
            }
            toCheck = toCheck.down().right()
        }

        return list
    }
}
