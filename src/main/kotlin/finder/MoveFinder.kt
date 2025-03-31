package adam.backend.portfolio.finder

import adam.backend.portfolio.model.Bishop
import adam.backend.portfolio.model.Board
import adam.backend.portfolio.model.Color
import adam.backend.portfolio.model.King
import adam.backend.portfolio.model.Knight
import adam.backend.portfolio.model.Move
import adam.backend.portfolio.model.Pawn
import adam.backend.portfolio.model.Piece
import adam.backend.portfolio.model.Queen
import adam.backend.portfolio.model.Rook
import adam.backend.portfolio.model.Square

class MoveFinder {

    fun findAll(board: Board, color: Color): List<Move>  {
        return board.board.flatMap { list -> list.map { findMoves(color, board, it.value) } }.flatten()
    }

    fun findMoves(color: Color, board: Board, coordinates: String): List<Move> {
        val foundSquare = board.findSquare(coordinates)
        if (foundSquare?.piece != null && foundSquare.piece?.getColor() == color) {
            val moves = when (val piece = foundSquare.piece) {
                is Pawn -> PawnMoveFinder().find(piece, board, color, foundSquare)
                is Bishop -> BishopMoveFinder().find(piece, board, color, foundSquare)
                is Knight -> KnightMoveFinder().find(piece, board, color, foundSquare)
                is Rook -> RookMoveFinder().find(piece, board, color, foundSquare)
                is Queen -> RookMoveFinder().find(piece, board, color, foundSquare) + BishopMoveFinder().find(piece, board, color, foundSquare)
                is King -> KingMoveFinder().find(piece, board, color, foundSquare)
                else -> mutableListOf()
            }

            return moves.filter { m ->
                !shouldRemoveTheMove(board, m, color)
            }.toList()
        }
        return mutableListOf()
    }

    private fun shouldRemoveTheMove(board: Board, m: Move, color: Color): Boolean {
        val boardCopy = board.copy() // copy the board
        boardCopy.move(m)

        // find the king
        val kingSquare: Square? = boardCopy.findKing(color)

        return if (kingSquare != null) {
            return boardCopy.isKingInCheck(kingSquare, color)
        } else {
            false
        }
    }

    companion object {

        fun check(potentialTake: String, board: Board, color: Color): Boolean {
            val takeSquare: Square? = board.findSquare(potentialTake)
            if (takeSquare != null && isOnBoard(potentialTake)) {
                if (!takeSquare.isEmpty() && takeSquare.piece?.getColor() != color) {
                    return true
                }
            }
            return false
        }

        fun isBlocked(piece: Piece, board: Board, coordinates: String, color: Color): Boolean {
            val square: Square? = board.findSquare(coordinates)

            if (square != null && square.isEmpty()) {
                return false
            }

            if (square?.piece?.getColor() != null && square.piece?.getColor() != color) {
                return false
            }

            return true
        }

        fun isBlockedPawn(piece: Piece, board: Board, coordinates: String, color: Color): Boolean {
            val square: Square? = board.findSquare(coordinates)

            if (square != null && square.isEmpty()) {
                return false
            }

            if (square != null && !square.isEmpty()) {
                return true
            }

            return true
        }

        fun isOnBoard(coordinates: String): Boolean {
            val col: Char = coordinates[0]
            if (!coordinates[1].isDigit()) {
                return false
            }
            val row: Int = coordinates[1].digitToInt()

            val chars: MutableList<Char> = mutableListOf()
            val a = 'a' - 1
            for (i in 1..8) {
                chars.add(a + i)
            }

            if (row > 8 || row < 1) {
                return false
            }

            if (!chars.contains(col)) {
                return false
            }

            return true
        }

        fun isValid(square: Square, board: Board, color: Color, piece: Piece): Boolean {
            return isOnBoard(square.value) && !isBlocked(piece, board, square.value, color)
        }

        fun isTake(
            board: Board,
            toCheck: Square,
            color: Color
        ): Boolean {
            if (isOnBoard(toCheck.value)) {
                val toColor = board.findSquare(toCheck.value)?.piece?.getColor()
                if (toColor != null && toColor != color) {
                    return true
                }
            }
            return false
        }
    }
}
