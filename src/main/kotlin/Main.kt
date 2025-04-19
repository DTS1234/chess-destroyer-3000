package adam.backend.portfolio

import adam.backend.portfolio.finder.MoveFinder
import adam.backend.portfolio.model.Board
import adam.backend.portfolio.model.Color
import adam.backend.portfolio.model.Move

fun main() {

    //runAgainstMe(Color.WHITE)
    runAgainstRandom()

}

private fun runAgainstMe(myColor: Color) {

    val chessEngine = ChessEngine()
    val board = Board()

    val engineColor = if (myColor == Color.WHITE) {
        println("You play as white")
        Color.BLACK;
    } else {
        println("You play as black")
        Color.WHITE;
    }

    while (!board.isCheckmate(Color.WHITE) && !board.isCheckmate(Color.BLACK) && !board.isDraw()) {
        if (engineColor == Color.WHITE) {
            engineMove(chessEngine, board, engineColor)
            userMove(board)
        } else {
            userMove(board)
            engineMove(chessEngine, board, engineColor)
        }
    }

    println("CHECKMATE!")
}

private fun engineMove(chessEngine: ChessEngine, board: Board, myColor: Color) {
    val engineMove = chessEngine.findBestMove(board, 8, myColor)
    board.move(Move(engineMove.value))
    println("Engine move: $engineMove")
    printBoard(board)
}

private fun userMove(board: Board) {
    println("Type your move:")
    val input = readln()
    board.move(Move(input))
    printBoard(board)
}

fun runAgainstRandom(): String {
    val board = Board()
    printBoard(board)

    val chesEngine = ChessEngine()
    var turn = Color.WHITE

    while (!board.isCheckmate(Color.BLACK) && !board.isCheckmate(Color.WHITE) && !board.isDraw()) {

        val move: Move =
            if (turn == Color.BLACK) {
                val moveFinder = MoveFinder()
                moveFinder.findAll(board, turn).random()
            } else {
                chesEngine.findBestMove(board, 5, Color.WHITE)
            }

        board.move(move)

        println(board.moves.last().value + "----------------------- " + turn)
        printBoard(board)
        turn = if (turn == Color.WHITE) Color.BLACK else Color.WHITE

        if (board.findKing(Color.WHITE) == null || board.findKing(Color.BLACK) == null) {
            println("BUG FOUND!!!")
            printBoard(board)
            break
        }

    }

    return if (board.isCheckmate(Color.WHITE) || board.isCheckmate(Color.BLACK)) {
        "Checkmate"
    } else {
        "Stalemate"
    }
}

fun printBoard(board: Board) {
    for (i in 0..7) {
        for (j in 0..7) {
            print(" | " + board.board[i][j].toString());
            if (j == 7) {
                print(" |")
            }
        }
        println()
    }
}

fun printCoordinates(board: Board) {
    for (i in 0..7) {
        for (j in 0..7) {
            print(" | " + board.board[i][j].value);
            if (j == 7) {
                print(" |")
            }
        }
        println()
    }
}
