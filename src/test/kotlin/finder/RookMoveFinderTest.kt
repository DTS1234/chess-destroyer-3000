package finder

import adam.backend.portfolio.model.Board
import adam.backend.portfolio.model.Color
import adam.backend.portfolio.model.Move
import adam.backend.portfolio.finder.MoveFinder
import adam.backend.portfolio.model.Rook
import org.junit.jupiter.api.DisplayNameGeneration
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


@DisplayNameGeneration(ReplaceUnderscores::class)
class RookMoveFinderTest {

    @Test
    fun should_return_rook_moves_white() {
        val b = Board()
        b.clear()
        b.board[7][1].piece = Rook(Color.WHITE)

        val moveFinder = MoveFinder()
        val moves = moveFinder.findMoves(Color.WHITE, b, "b1")

        assertEquals(
            actual = moves, expected = listOf(
                Move(value = "b1b2"),
                Move(value = "b1b3"),
                Move(value = "b1b4"),
                Move(value = "b1b5"),
                Move(value = "b1b6"),
                Move(value = "b1b7"),
                Move(value = "b1b8"),
                Move(value = "b1a1"),
                Move(value = "b1c1"),
                Move(value = "b1d1"),
                Move(value = "b1e1"),
                Move(value = "b1f1"),
                Move(value = "b1g1"),
                Move(value = "b1h1")
            )
        )
    }

    @Test
    fun should_return_rook_moves_black() {
        val b = Board()
        b.clear()
        b.board[4][4].piece = Rook(Color.BLACK)

        val moveFinder = MoveFinder()
        val moves = moveFinder.findMoves(Color.BLACK, b, "e4")

        assertEquals(
            actual = moves, expected = listOf(
                Move(value = "e4e5"),
                Move(value = "e4e6"),
                Move(value = "e4e7"),
                Move(value = "e4e8"),
                Move(value = "e4d4"),
                Move(value = "e4c4"),
                Move(value = "e4b4"),
                Move(value = "e4a4"),
                Move(value = "e4e3"),
                Move(value = "e4e2"),
                Move(value = "e4e1"),
                Move(value = "e4f4"),
                Move(value = "e4g4"),
                Move(value = "e4h4")
            )
        )
    }

    @Test
    fun should_return_rook_takes() {
        val b = Board()
        b.clear()
        b.board[4][1].piece = Rook(Color.WHITE) // b4
        b.board[4][4].piece = Rook(Color.BLACK) // e4
        b.board[5][1].piece = Rook(Color.BLACK) // b3
        b.board[0][1].piece = Rook(Color.BLACK) // b8

        val moveFinder = MoveFinder()
        val moves = moveFinder.findMoves(Color.WHITE, b, "b4")

        assertEquals(
            actual = moves, expected = listOf(
                Move(value = "b4b5"), Move(value = "b4b6"),
                Move(value = "b4b7"), Move(value = "b4b8"), Move(value = "b4a4"),
                Move(value = "b4b3"), Move(value = "b4c4"), Move(value = "b4d4"), Move(value = "b4e4")
            )
        )
    }
}