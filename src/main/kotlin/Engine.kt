package adam.backend.portfolio

import adam.backend.portfolio.finder.MoveFinder
import adam.backend.portfolio.model.Board
import adam.backend.portfolio.model.Color
import adam.backend.portfolio.model.Move

class ChessEngine {

    private val materialWeightMap: Map<String, Int>
        get() = mapOf(
            "Q" to 9, "K" to 0, "R" to 5, "B" to 3, "N" to 3, "p" to 1
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

        return if (maximizingColor == Color.WHITE) eval else -eval
    }
}