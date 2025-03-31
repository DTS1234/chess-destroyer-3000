package adam.backend.portfolio.finder

import adam.backend.portfolio.model.Board
import adam.backend.portfolio.model.Color
import adam.backend.portfolio.model.Move
import adam.backend.portfolio.model.Piece
import adam.backend.portfolio.model.Rook
import adam.backend.portfolio.model.Square
import adam.backend.portfolio.finder.MoveFinder.Companion.isValid

class KingMoveFinder {

    fun find(rook: Piece, board: Board, color: Color, square: Square): List<Move> {

        val moveFrom = square.value

        val list: MutableList<Move> = mutableListOf(
            square.up(), square.left(), square.down(), square.right(), square.right().up(),
            square.right().down(), square.left().down(), square.left().up()
        )
            .filter { isValid(it, board, color, rook) }
            .map { Move("$moveFrom${it.value}") }
            .toMutableList()

        if (isShortCastleAllowed(color, board)) {
            list.add(Move("O-O"))
        }

        if (isLongCastleAllowed(color, board)) {
            list.add(Move("O-O-O"))
        }

        return list
    }

    private fun isLongCastleAllowed(color: Color, board: Board): Boolean {
        var isCastlePossible = false;
        isCastlePossible = !kingMoved(color, board)
        isCastlePossible = isCastlePossible && !didRookMove(color, board, CASTLE.LONG)
        isCastlePossible = isCastlePossible && !isKingInCheck(color, board)
        isCastlePossible = isCastlePossible && isLongCastleOccupied(color, board)
        isCastlePossible = isCastlePossible && !willBeInCheckAfterCastle(color, board, CASTLE.LONG)
        isCastlePossible = isCastlePossible && !isAnyLongCastleSquareInCheck(color, board)

        return isCastlePossible
    }

    private fun isShortCastleAllowed(color: Color, board: Board): Boolean {
        var isCastlePossible = false;
        isCastlePossible = !kingMoved(color, board)
        isCastlePossible = isCastlePossible && !didRookMove(color, board, CASTLE.SHORT)
        isCastlePossible = isCastlePossible && !isKingInCheck(color, board)
        isCastlePossible = isCastlePossible && isShortCastleOccupied(color, board)
        isCastlePossible = isCastlePossible && !willBeInCheckAfterCastle(color, board, CASTLE.SHORT)
        isCastlePossible = isCastlePossible && !isAnyShortCastleSquareInCheck(color, board)
        return isCastlePossible
    }

    private fun willBeInCheckAfterCastle(color: Color, board: Board, castle: CASTLE): Boolean {
        val move = if (castle == CASTLE.SHORT) {
            "O-O"
        } else {
            "O-O-O"
        }
        val copyBoard = board.copy()
        copyBoard.move(Move(move))
        copyBoard.move(Move(move))
        val kingSquare = copyBoard.findKing(color)
        if (kingSquare != null && copyBoard.isKingInCheck(kingSquare, color)) {
            return true
        } else {
            return false
        }
    }

    private fun isAnyShortCastleSquareInCheck(color: Color, board: Board): Boolean {
        if (color == Color.WHITE) {
            return board.isAttacked(board.findSquare("f1")!!, color) ||
                    board.isAttacked(board.findSquare("g1")!!, color) ||
                    board.isAttacked(board.findSquare("h1")!!, color)
        } else {
            return board.isAttacked(board.findSquare("f8")!!, color) ||
                    board.isAttacked(board.findSquare("g8")!!, color) ||
                    board.isAttacked(board.findSquare("h8")!!, color)
        }
    }

    private fun isAnyLongCastleSquareInCheck(color: Color, board: Board): Boolean {
        if (color == Color.WHITE) {
            return board.isAttacked(board.findSquare("b1")!!, color) ||
                    board.isAttacked(board.findSquare("c1")!!, color) ||
                    board.isAttacked(board.findSquare("d1")!!, color) ||
                    board.isAttacked(board.findSquare("a1")!!, color)
        } else {
            return board.isAttacked(board.findSquare("b8")!!, color) ||
                    board.isAttacked(board.findSquare("c8")!!, color) ||
                    board.isAttacked(board.findSquare("d8")!!, color) ||
                    board.isAttacked(board.findSquare("a8")!!, color)
        }
    }

    private fun isShortCastleOccupied(color: Color, board: Board): Boolean {
        if (color == Color.WHITE) {
            val isEmpty = board.findSquare("f1")?.isEmpty() == true && board.findSquare("g1")?.isEmpty() == true
            return isEmpty
        } else {
            val isEmpty = board.findSquare("f8")?.isEmpty() == true && board.findSquare("g8")?.isEmpty() == true
            return isEmpty
        }
    }

    private fun isLongCastleOccupied(color: Color, board: Board): Boolean {
        if (color == Color.WHITE) {
            // listOf("b1", "c1", "d1").none { board.findSquare(it)?.isEmpty() == true }
            val isEmpty = board.findSquare("b1")?.isEmpty() == true &&
                    board.findSquare("c1")?.isEmpty() == true &&
                    board.findSquare("d1")?.isEmpty() == true
            return isEmpty
        } else {
            val isEmpty = board.findSquare("b8")?.isEmpty() == true &&
                    board.findSquare("g8")?.isEmpty() == true &&
                    board.findSquare("d8")?.isEmpty() == true
            return isEmpty
        }
    }

    private fun isKingInCheck(color: Color, board: Board): Boolean {
        val kingSquare = board.findKing(color)

        return if (color == Color.WHITE) {
            val isCorrectSquare = kingSquare != null && kingSquare.value == "e1"
            if (isCorrectSquare) {
                board.isKingInCheck(kingSquare!!, color)
            } else {
                false
            }
        } else {
            val isCorrectSquare = kingSquare != null && kingSquare.value == "e8"
            if (isCorrectSquare) {
                board.isKingInCheck(kingSquare!!, color)
            } else {
                false
            }
        }
    }

    private fun kingMoved(color: Color, board: Board): Boolean {
        if (color == Color.WHITE) {
            return !board.moves.none { m -> m.value.startsWith("e1") || m.value.startsWith("O-O") } || board.findKing(color)?.value != "e1"
        }

        if (color == Color.BLACK) {
            return !board.moves.none { m -> m.value.startsWith("e8") || m.value.startsWith("O-O") } || board.findKing(color)?.value != "e8"
        }

        return false
    }

    private fun didRookMove(color: Color, board: Board, castle: CASTLE): Boolean {
        if (color == Color.WHITE) {
            val rookSquare = if (castle == CASTLE.SHORT) {
                "h1"
            } else {
                "a1"
            }
            return !board.moves.none { m -> m.value.startsWith(rookSquare) } || board.findSquare(rookSquare)?.piece != Rook(color)
        }

        if (color == Color.BLACK) {
            val rookSquare = if (castle == CASTLE.SHORT) {
                "h8"
            } else {
                "a8"
            }
            return !board.moves.none { m -> m.value.startsWith(rookSquare) } || board.findSquare(rookSquare)?.piece != Rook(color)
        }

        return false
    }
}

enum class CASTLE {
    SHORT, LONG
}