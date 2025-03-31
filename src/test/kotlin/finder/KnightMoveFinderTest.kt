package finder

import adam.backend.portfolio.model.Board
import adam.backend.portfolio.model.Color
import adam.backend.portfolio.model.Move
import adam.backend.portfolio.finder.MoveFinder
import adam.backend.portfolio.model.Knight
import org.junit.jupiter.api.DisplayNameGeneration
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@DisplayNameGeneration(ReplaceUnderscores::class)
class KnightMoveFinderTest {

    @Test
    fun should_return_start_moves() {
        // given
        val b = Board()

        // when
        val moveFinder = MoveFinder()
        val movesb1 = moveFinder.findMoves(Color.WHITE, b, "b1")
        val movesg1 = moveFinder.findMoves(Color.WHITE, b, "g1")
        val movesb8 = moveFinder.findMoves(Color.BLACK, b, "b8")
        val movesg8 = moveFinder.findMoves(Color.BLACK, b, "g8")

        // then
        assertEquals(actual = movesb1, expected = listOf(Move("b1c3"), Move("b1a3")))
        assertEquals(movesg1, listOf(Move("g1h3"), Move("g1f3")))
        assertEquals(movesb8, listOf(Move("b8a6"), Move("b8c6")))
        assertEquals(movesg8, listOf(Move("g8f6"), Move("g8h6")))
    }

    @Test
    fun should_return_takes() {
        // given
        val b = Board()
        b.clear()
        b.board[7][1].piece = Knight(Color.WHITE)
        b.board[5][0].piece = Knight(Color.BLACK)
        b.board[5][2].piece = Knight(Color.BLACK)
        b.board[6][3].piece = Knight(Color.BLACK)

        // when
        val moveFinder = MoveFinder()
        val moves = moveFinder.findMoves(Color.WHITE, b, "b1")

        // then
        assertEquals(moves, listOf(Move("b1c3"), Move("b1a3"), Move("b1d2")))
    }

}