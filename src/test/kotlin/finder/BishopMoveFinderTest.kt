package finder

import adam.backend.portfolio.model.Bishop
import adam.backend.portfolio.model.Board
import adam.backend.portfolio.model.Color
import adam.backend.portfolio.model.Knight
import adam.backend.portfolio.model.Move
import adam.backend.portfolio.finder.MoveFinder
import adam.backend.portfolio.printBoard
import adam.backend.portfolio.printCoordinates
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BishopMoveFinderTest {

    @Test
    fun should_find_bishop_moves_for_white() {
        // given
        val b = Board()
        b.clear()
        b.board[4][4].piece = Bishop(Color.WHITE)

        // when
        val moveFinder = MoveFinder()

        // then
        val moves = moveFinder.findMoves(Color.WHITE, b, "e4")
        assertEquals(
            listOf(
                Move(value = "e4f5"), Move(value = "e4g6"), Move(value = "e4h7"), Move(value = "e4d5"), Move(value = "e4c6"),
                Move(value = "e4b7"), Move(value = "e4a8"), Move(value = "e4d3"), Move(value = "e4c2"), Move(value = "e4b1"), Move(value = "e4f3"),
                Move(value = "e4g2"), Move(value = "e4h1")
            ), moves
        )
    }

    @Test
    fun should_find_bishop_moves_for_black() {
        // given
        val b = Board()
        b.clear()
        b.board[2][5].piece = Bishop(Color.BLACK)
        printBoard(b)
        // when
        val moveFinder = MoveFinder()

        // then
        val moves = moveFinder.findMoves(Color.BLACK, b, "f6")
        println(moves)
        assertEquals(
            listOf(
                Move(value = "f6g7"), Move(value = "f6h8"), Move(value = "f6e7"), Move(value = "f6d8"), Move(value = "f6e5"),
                Move(value = "f6d4"), Move(value = "f6c3"), Move(value = "f6b2"), Move(value = "f6a1"), Move(value = "f6g5"),
                Move(value = "f6h4")
            ),
            moves
        )
    }

    @Test
    fun should_find_bishop_takes_for_black() {
        // given
        val b = Board()
        b.clear()
        b.board[2][5].piece = Knight(Color.WHITE)
        b.board[2][7].piece = Knight(Color.WHITE)
        b.board[4][7].piece = Knight(Color.WHITE)
        b.board[4][5].piece = Knight(Color.WHITE)
        b.board[3][6].piece = Bishop(Color.BLACK)
        printBoard(b)
        printCoordinates(b)
        // when
        val moveFinder = MoveFinder()

        // then
        val moves = moveFinder.findMoves(Color.BLACK, b, "g5")
        println(moves)
        assertEquals(
            listOf(
                Move(value = "g5h6"), Move(value = "g5f6"), Move(value = "g5f4"), Move(value = "g5h4")
            ),
            moves
        )
    }

    @Test
    fun should_find_bishop_takes_for_white() {
        // given
        val b = Board()
        b.clear()
        b.board[2][5].piece = Knight(Color.BLACK)
        b.board[2][7].piece = Knight(Color.BLACK)
        b.board[4][7].piece = Knight(Color.BLACK)
        b.board[4][5].piece = Knight(Color.BLACK)
        b.board[3][6].piece = Bishop(Color.WHITE)
        printBoard(b)
        printCoordinates(b)
        // when
        val moveFinder = MoveFinder()

        // then
        val moves = moveFinder.findMoves(Color.WHITE, b, "g5")
        println(moves)
        assertEquals(
            listOf(
                Move(value = "g5h6"), Move(value = "g5f6"), Move(value = "g5f4"), Move(value = "g5h4")
            ),
            moves
        )
    }

    @Test
    fun should_return_empty_list() {
        // given
        val b = Board()

        // when
        val moveFinder = MoveFinder()

        // then
        val movesF1 = moveFinder.findMoves(Color.WHITE, b, "f1")
        val movesC1 = moveFinder.findMoves(Color.WHITE, b, "c1")
        val movesC8 = moveFinder.findMoves(Color.BLACK, b, "c8")
        val movesF8 = moveFinder.findMoves(Color.BLACK, b, "f8")

        assertTrue(movesF1.isEmpty())
        assertTrue(movesC1.isEmpty())
        assertTrue(movesC8.isEmpty())
        assertTrue(movesF8.isEmpty())
    }
}
