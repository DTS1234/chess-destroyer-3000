package adam.backend.portfolio.finder

import adam.backend.portfolio.finder.MoveFinder.Companion.check
import adam.backend.portfolio.finder.MoveFinder.Companion.isBlockedPawn
import adam.backend.portfolio.finder.MoveFinder.Companion.isOnBoard
import adam.backend.portfolio.model.Board
import adam.backend.portfolio.model.Color
import adam.backend.portfolio.model.Move
import adam.backend.portfolio.model.Pawn
import adam.backend.portfolio.model.Square

class PawnMoveFinder {

    fun find(pawn: Pawn, board: Board, color: Color, square: Square): List<Move> {
        val list: MutableList<Move> = mutableListOf()
        val moveFrom = square.value

        if (square.value[1] == '2' && color == Color.WHITE) {
            val moveTo = square.up().up().value
            val moveToPlus1 = square.up().value

            if (!isBlockedPawn(pawn, board, moveToPlus1, color) && !isBlockedPawn(pawn, board, moveTo, color)) {
                list.add(Move("$moveFrom$moveTo"))
            }
        }

        if (square.value[1] == '7' && color == Color.BLACK) {
            val moveTo = square.down().down().value
            val moveToMinus1 = square.down().value
            if (!isBlockedPawn(pawn, board, moveToMinus1, color) && !isBlockedPawn(pawn, board, moveTo, color) && isOnBoard(moveTo)) {
                list.add(Move("$moveFrom$moveTo"))
            }
        }

        if (color == Color.BLACK && !isBlockedPawn(pawn, board, square.down().value, color) && isOnBoard(square.down().value)) {
            val moveTo = square.down().value
            list.add(Move("$moveFrom$moveTo"))
        }

        if (color == Color.WHITE && !isBlockedPawn(pawn, board, square.up().value, color) && isOnBoard(square.up().value)) {
            val moveTo = square.up().value
            list.add(Move("$moveFrom$moveTo"))
        }

        // en passant black
        if (board.moves.isNotEmpty() && !board.moves.last().value.startsWith("O") && board.moves.last().value[1].digitToInt() == 2 && board.moves.last().value[3].digitToInt() == 4 && color == Color.BLACK && isAdjacent(
                board,
                moveFrom
            )
        ) {
            val first = board.moves.last().value[2]
            val second = board.moves.last().value[3]
            val lastMovePosition = "$first$second"
            val moveTo = Square(lastMovePosition, null).down().value
            if (!isBlockedPawn(pawn, board, moveTo, color)) {
                list.add(Move("$moveFrom$moveTo" + "E"))
            }
        }

        // em passant white
        if (board.moves.isNotEmpty() && !board.moves.last().value.startsWith("O") && board.moves.last().value[3].digitToInt() == 5 && color == Color.WHITE && isAdjacent(
                board,
                moveFrom
            ) && board.moves.last().value[1].digitToInt() == 7
        ) {
            val first = board.moves.last().value[2]
            val second = board.moves.last().value[3]
            val lastMovePosition = "$first$second"
            val moveTo = Square(lastMovePosition, null).up().value
            if (!isBlockedPawn(pawn, board, moveTo, color)) {
                list.add(Move("$moveFrom$moveTo"+"E"))
            }
        }

        return list + getPawnTakes(board, square, color)
    }

    private fun isAdjacent(board: Board, moveFrom: String) =
        // Check if the last move was in the column adjacent to the current pawn
        (board.moves.last().value[0].code.plus(1).toChar() == moveFrom[0] ||
                (board.moves.last().value[0].code.minus(1).toChar()) == moveFrom[0]) && (board.moves.last().value[3].digitToInt() == moveFrom[1].digitToInt())

    private fun getPawnTakes(board: Board, square: Square, color: Color): List<Move> {
        val list: MutableList<Move> = mutableListOf()

        if (color == Color.WHITE) {
            val potentialTake1 = square.up().left().value
            val potentialTake2 = square.up().right().value

            if (check(potentialTake2, board, color)) {
                val moveFrom = square.value
                list.add(Move("$moveFrom$potentialTake2"))
            }

            if (check(potentialTake1, board, color)) {
                val moveFrom = square.value
                list.add(Move("$moveFrom$potentialTake1"))
            }
        }

        if (color == Color.BLACK) {
            val potentialTake1 = square.down().left().value
            val potentialTake2 = square.down().right().value

            if (check(potentialTake2, board, color)) {
                val moveFrom = square.value
                list.add(Move("$moveFrom$potentialTake2"))
            }

            if (check(potentialTake1, board, color)) {
                val moveFrom = square.value
                list.add(Move("$moveFrom$potentialTake1"))
            }
        }

        return list
    }
}
