package adam.backend.portfolio

import adam.backend.portfolio.finder.MoveFinder
import adam.backend.portfolio.model.Bishop
import adam.backend.portfolio.model.Board
import adam.backend.portfolio.model.Color
import adam.backend.portfolio.model.Knight
import adam.backend.portfolio.model.Move
import adam.backend.portfolio.model.Pawn
import adam.backend.portfolio.model.Rook
import adam.backend.portfolio.model.Square

class ChessEngine {
    // Cache material weights as constants
    private companion object {
        const val QUEEN_VALUE = 90
        const val KING_VALUE = 10000
        const val ROOK_VALUE = 50
        const val BISHOP_VALUE = 30
        const val KNIGHT_VALUE = 30
        const val PAWN_VALUE = 10

        // Piece-square tables for positional evaluation
        private val PAWN_SQUARE_TABLE = intArrayOf(
            0,  0,  0,  0,  0,  0,  0,  0,
            50, 50, 50, 50, 50, 50, 50, 50,
            10, 10, 20, 30, 30, 20, 10, 10,
            5,  5, 10, 25, 25, 10,  5,  5,
            0,  0,  0, 20, 20,  0,  0,  0,
            5, -5,-10,  0,  0,-10, -5,  5,
            5, 10, 10,-20,-20, 10, 10,  5,
            0,  0,  0,  0,  0,  0,  0,  0
        )

        private val KNIGHT_SQUARE_TABLE = intArrayOf(
            -50,-40,-30,-30,-30,-30,-40,-50,
            -40,-20,  0,  0,  0,  0,-20,-40,
            -30,  0, 10, 15, 15, 10,  0,-30,
            -30,  5, 15, 20, 20, 15,  5,-30,
            -30,  0, 15, 20, 20, 15,  0,-30,
            -30,  5, 10, 15, 15, 10,  5,-30,
            -40,-20,  0,  5,  5,  0,-20,-40,
            -50,-40,-30,-30,-30,-30,-40,-50
        )

        private val BISHOP_SQUARE_TABLE = intArrayOf(
            -20,-10,-10,-10,-10,-10,-10,-20,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -10,  0,  5, 10, 10,  5,  0,-10,
            -10,  5,  5, 10, 10,  5,  5,-10,
            -10,  0, 10, 10, 10, 10,  0,-10,
            -10, 10, 10, 10, 10, 10, 10,-10,
            -10,  5,  0,  0,  0,  0,  5,-10,
            -20,-10,-10,-10,-10,-10,-10,-20
        )

        private val ROOK_SQUARE_TABLE = intArrayOf(
            0,  0,  0,  0,  0,  0,  0,  0,
            5, 10, 10, 10, 10, 10, 10,  5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            0,  0,  0,  5,  5,  0,  0,  0
        )

        private val QUEEN_SQUARE_TABLE = intArrayOf(
            -20,-10,-10, -5, -5,-10,-10,-20,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -10,  0,  5,  5,  5,  5,  0,-10,
            -5,  0,  5,  5,  5,  5,  0, -5,
            0,  0,  5,  5,  5,  5,  0, -5,
            -10,  5,  5,  5,  5,  5,  0,-10,
            -10,  0,  5,  0,  0,  0,  0,-10,
            -20,-10,-10, -5, -5,-10,-10,-20
        )

        private val KING_SQUARE_TABLE = intArrayOf(
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -20,-30,-30,-40,-40,-30,-30,-20,
            -10,-20,-20,-20,-20,-20,-20,-10,
            20, 20,  0,  0,  0,  0, 20, 20,
            20, 30, 10,  0,  0, 10, 30, 20
        )
    }

    private val materialWeightMap: Map<String, Int>
        get() = mapOf(
            "Q" to 90, "K" to 10000, "R" to 50, "B" to 30, "N" to 30, "p" to 10
        )

    fun findBestMove(board: Board, depth: Int, maximizingColor: Color): Move {
        val moveFinder = MoveFinder()
        var bestMove: Move? = null
        var alpha = Int.MIN_VALUE
        val beta = Int.MAX_VALUE
        
        for (move in moveFinder.findAll(board, maximizingColor)) {
            val copy = makeMove(board, move)
            val eval = alphaBeta(depth - 1, copy, maximizingColor.opposite(), maximizingColor, alpha, beta)
            if (eval > alpha) {
                alpha = eval
                bestMove = move
            }
        }
        return bestMove!!
    }

    private fun alphaBeta(depth: Int, board: Board, color: Color, maximizingColor: Color, alpha: Int, beta: Int): Int {
        if (depth == 0 || board.isCheckmate(Color.WHITE) || board.isCheckmate(Color.BLACK) || board.isDraw()) {
            return evaluate(board, maximizingColor, depth)
        }

        val allMoves = MoveFinder().findAll(board, color)
        var currentAlpha = alpha
        var currentBeta = beta

        if (color == maximizingColor) {
            var maxEval = Int.MIN_VALUE
            for (m in allMoves) {
                val copy = makeMove(board, m)
                val eval = alphaBeta(depth - 1, copy, color.opposite(), maximizingColor, currentAlpha, currentBeta)
                maxEval = maxOf(maxEval, eval)
                currentAlpha = maxOf(currentAlpha, eval)
                if (currentBeta <= currentAlpha) {
                    break // Beta cutoff
                }
            }
            return maxEval
        } else {
            var minEval = Int.MAX_VALUE
            for (m in allMoves) {
                val copy = makeMove(board, m)
                val eval = alphaBeta(depth - 1, copy, color.opposite(), maximizingColor, currentAlpha, currentBeta)
                minEval = minOf(minEval, eval)
                currentBeta = minOf(currentBeta, eval)
                if (currentBeta <= currentAlpha) {
                    break // Alpha cutoff
                }
            }
            return minEval
        }
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
        var whiteScore = 0
        var blackScore = 0

        // Single pass evaluation of all pieces
        for (row in 0..7) {
            for (col in 0..7) {
                val square = board.board[row][col]
                val piece = square.piece ?: continue
                
                val materialValue = when (piece.getSymbol()) {
                    "Q" -> QUEEN_VALUE
                    "K" -> KING_VALUE
                    "R" -> ROOK_VALUE
                    "B" -> BISHOP_VALUE
                    "N" -> KNIGHT_VALUE
                    "p" -> PAWN_VALUE
                    else -> 0
                }

                val positionValue = when (piece.getSymbol()) {
                    "p" -> PAWN_SQUARE_TABLE[if (piece.getColor() == Color.WHITE) row * 8 + col else (7 - row) * 8 + col]
                    "N" -> KNIGHT_SQUARE_TABLE[if (piece.getColor() == Color.WHITE) row * 8 + col else (7 - row) * 8 + col]
                    "B" -> BISHOP_SQUARE_TABLE[if (piece.getColor() == Color.WHITE) row * 8 + col else (7 - row) * 8 + col]
                    "R" -> ROOK_SQUARE_TABLE[if (piece.getColor() == Color.WHITE) row * 8 + col else (7 - row) * 8 + col]
                    "Q" -> QUEEN_SQUARE_TABLE[if (piece.getColor() == Color.WHITE) row * 8 + col else (7 - row) * 8 + col]
                    "K" -> KING_SQUARE_TABLE[if (piece.getColor() == Color.WHITE) row * 8 + col else (7 - row) * 8 + col]
                    else -> 0
                }

                if (piece.getColor() == Color.WHITE) {
                    whiteScore += materialValue + positionValue
                } else {
                    blackScore += materialValue + positionValue
                }
            }
        }

        if (board.isCheckmate(Color.WHITE)) {
            return if (maximizingColor == Color.WHITE) -10_000 + depth else 10_000 - depth
        }
        if (board.isCheckmate(Color.BLACK)) {
            return if (maximizingColor == Color.BLACK) -10_000 + depth else 10_000 - depth
        }

        var eval = whiteScore - blackScore

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
            if (board.findSquare("b8")?.piece != Knight(Color.BLACK) && board.moves.size < 10) {
                return eval + 5
            }
            if (board.findSquare("c8")?.piece != Knight(Color.BLACK) && board.moves.size < 10) {
                return eval + 5
            }
            if (board.findSquare("f8")?.piece != Bishop(Color.BLACK) && board.moves.size < 8) {
                return eval + 5
            }
            if (board.findSquare("g8")?.piece != Knight(Color.BLACK) && board.moves.size < 10) {
                return eval + 5
            }
        }
        return eval
    }

    private fun punishEarlyQueenMoves(eval: Int, board: Board, maximizingColor: Color): Int {
        if (maximizingColor == Color.WHITE) {
            if (board.findSquare("d1")?.isEmpty() == true && board.moves.size < 8) {
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
