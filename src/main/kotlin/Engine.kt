package adam.backend.portfolio

import adam.backend.portfolio.finder.MoveFinder
import adam.backend.portfolio.model.Board
import adam.backend.portfolio.model.Color
import adam.backend.portfolio.model.Move
import adam.backend.portfolio.model.Pawn
import adam.backend.portfolio.model.Rook
import adam.backend.portfolio.model.Square

class ChessEngine {

    private val materialWeightMap: Map<String, Int>
        get() = mapOf(
            "Q" to 90, "K" to 10000, "R" to 50, "B" to 30, "N" to 30, "p" to 10
        )

    fun findBestMove(board: Board, depth: Int, maximizingColor: Color): Move {
        val moveFinder = MoveFinder()
        var maxV = Int.MIN_VALUE
        var bestMove: Move? = null
        for (move in moveFinder.findAll(board, maximizingColor)) {
            val copy = makeMove(board, move)
            val minmaxRoot = minmax(depth - 1, copy, maximizingColor.opposite(), maximizingColor)
            if (minmaxRoot > maxV) {
                maxV = minmaxRoot
                bestMove = move
            }
        }
        return bestMove!!
    }

    private fun minmax(depth: Int, board: Board, color: Color, maximizingColor: Color): Int {
        if (depth == 0 || board.isCheckmate(Color.WHITE) || board.isCheckmate(Color.BLACK) || board.isDraw()) {
            val score = evaluate(board, maximizingColor, depth)
            return score
        }

        val allMoves = MoveFinder().findAll(board, color)
        var bestEval = if (color == maximizingColor) Int.MIN_VALUE else Int.MAX_VALUE

        for (m in allMoves) {
            val copy = makeMove(board, m)
            val eval = minmax(depth - 1, copy, color.opposite(), maximizingColor)

            if (color == maximizingColor) {
                if (eval > bestEval) {
                    bestEval = eval
                }
            } else {
                if (eval < bestEval) {
                    bestEval = eval
                }
            }
        }

        return bestEval
    }


    private fun makeMove(board: Board, move: Move): Board {
        val copy = board.copy()
        if (move.value.startsWith("O")) {
            copy.move(move)
        } else {
            copy.move(move)
        }
        return copy
    }

    fun evaluate(board: Board, maximizingColor: Color, depth: Int): Int {

        // get all white pieces and sum the values
        val whiteScore = board.board
            .asSequence().flatten()
            .mapNotNull { it.piece }
            .filter { it.getColor() == Color.WHITE }
            .map { materialWeightMap[it.getSymbol()]!! }
            .sum()

        // get all black pieces and sum the values
        val blackScore = board.board
            .asSequence().flatten()
            .mapNotNull { it.piece }
            .filter { it.getColor() == Color.BLACK }
            .map { materialWeightMap[it.getSymbol()]!! }
            .sum()

        // calculate the difference if white 10 and black 11 score = -1
        var eval = whiteScore - blackScore

        // if white is checkmated
        if (board.isCheckmate(Color.WHITE)) {
            return if (maximizingColor == Color.WHITE) {
                -10_000 + depth // white lost, return min value
            } else {
                10_000 - depth // white won return max value
            }
        }

        // if black is checkmated
        if (board.isCheckmate(Color.BLACK)) {
            return if (maximizingColor == Color.BLACK) {
                -10_000 + depth // black lost return min value
            } else {
                10_000 - depth // black won return max value
            }
        }

        val whiteKing = board.findKing(Color.WHITE) ?: return eval
        val blackKing = board.findKing(Color.BLACK) ?: return eval

        if (board.isAttacked(whiteKing, Color.BLACK)) {
            eval -= 10
        }
        if (board.isAttacked(blackKing, Color.WHITE)) {
            eval += 10
        }

        eval = addForCastling(eval, board, maximizingColor)
        eval = punishEarlyQueenMoves(eval, board, maximizingColor)
        eval = rewardEarlyLightPiecesDevelopment(eval, board, maximizingColor)
        eval = rewardCenterTakeOvers(eval, board, maximizingColor)

        return if (maximizingColor == Color.WHITE) eval else -eval
    }

    private fun rewardCenterTakeOvers(eval: Int, board: Board, maximizingColor: Color): Int {
        var newEval = eval
        val center = listOf("e4", "e5", "d4", "d5")
        for (square in center) {
            if (board.isAttacked(Square(square, null), maximizingColor)) {
                newEval += 3
            }
        }
        return newEval
    }

    private fun rewardEarlyLightPiecesDevelopment(eval: Int, board: Board, maximizingColor: Color): Int {
        if (maximizingColor == Color.WHITE) {
            if (board.findSquare("b1")?.isEmpty() == true && board.moves.size < 8) {
                return eval + 2
            }
            if (board.findSquare("c1")?.isEmpty() == true && board.moves.size < 8) {
                return eval + 2
            }
            if (board.findSquare("f1")?.isEmpty() == true && board.moves.size < 8) {
                return eval + 2
            }
            if (board.findSquare("g1")?.isEmpty() == true && board.moves.size < 8) {
                return eval + 2
            }
        } else {
            if (board.findSquare("b8")?.isEmpty() == true && board.moves.size < 8) {
                return eval + 2
            }
            if (board.findSquare("c8")?.isEmpty() == true && board.moves.size < 8) {
                return eval + 2
            }
            if (board.findSquare("f8")?.isEmpty() == true && board.moves.size < 8) {
                return eval + 2
            }
            if (board.findSquare("g8")?.isEmpty() == true && board.moves.size < 8) {
                return eval + 2
            }
        }
        return eval
    }

    private fun punishEarlyQueenMoves(eval: Int, board: Board, maximizingColor: Color): Int {
        if (maximizingColor == Color.WHITE) {
            if (board.findSquare("d1")?.isEmpty() == true && board.moves.size < 8) {
                println("queen moved")
                return eval - 3
            }
        } else {
            if (board.findSquare("d8")?.isEmpty() == true && board.moves.size < 8) {
                return eval - 3
            }
        }
        return eval
    }

    private fun addForCastling(eval: Int, board: Board, maximizingColor: Color): Int {
        if (maximizingColor == Color.WHITE) {
            val kingSquare = board.findKing(maximizingColor)
            if (kingSquare?.value == "g1" && board.findSquare("f1")?.piece == Rook(Color.WHITE)) {
                if (board.findSquare("f2")?.piece == Pawn(Color.WHITE) && board.findSquare("g2")?.piece == Pawn(Color.WHITE)) {
                    return eval + 10
                } else {
                    return eval + 5
                }
            }
        } else {
            val kingSquare = board.findKing(maximizingColor)
            if (kingSquare?.value == "g8" && board.findSquare("f8")?.piece == Rook(Color.BLACK)) {
                if (board.findSquare("f7")?.piece == Pawn(Color.BLACK) && board.findSquare("g8")?.piece == Pawn(Color.BLACK)) {
                    return eval + 10
                } else {
                    return eval + 5
                }
            }
        }

        if (maximizingColor == Color.WHITE) {
            val kingSquare = board.findKing(maximizingColor)
            if (kingSquare?.value == "c1" && board.findSquare("d1")?.piece == Rook(Color.WHITE)) {
                if (board.findSquare("c2")?.piece == Pawn(Color.WHITE) && board.findSquare("d2")?.piece == Pawn(Color.WHITE)) {
                    return eval + 5
                } else {
                    return eval + 3
                }
            }
        } else {
            val kingSquare = board.findKing(maximizingColor)
            if (kingSquare?.value == "c8" && board.findSquare("d8")?.piece == Rook(Color.BLACK)) {
                if (board.findSquare("c7")?.piece == Pawn(Color.BLACK) && board.findSquare("d7")?.piece == Pawn(Color.BLACK)) {
                    return eval + 5
                } else {
                    return eval + 3
                }
            }
        }
        return eval
    }
}
