package finder

import adam.backend.portfolio.model.Bishop
import adam.backend.portfolio.model.Board
import adam.backend.portfolio.model.Color
import adam.backend.portfolio.model.King
import adam.backend.portfolio.model.Knight
import adam.backend.portfolio.model.Move
import adam.backend.portfolio.model.Pawn
import adam.backend.portfolio.model.Queen
import adam.backend.portfolio.model.Rook
import adam.backend.portfolio.finder.MoveFinder
import adam.backend.portfolio.printBoard
import org.junit.jupiter.api.DisplayNameGeneration
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@DisplayNameGeneration(ReplaceUnderscores::class)
class MoveFinderTest {

    @Test
    fun should_filter_illegal_moves_producing_checks_rook_up() {
        // given
        val b = Board()
        b.clear()
        b.put("e4", King(Color.WHITE))
        b.put("e5", Pawn(Color.WHITE))
        b.put("e8", Rook(Color.BLACK))
        b.put("f6", Rook(Color.BLACK))

        // when
        val moveFinder = MoveFinder()
        val moves = moveFinder.findMoves(Color.WHITE, b, "e5")

        // then
        assertEquals(expected = listOf(Move("e5e6")), actual = moves)
    }


    @Test
    fun should_filter_illegal_moves_producing_checks_rook_down() {
        // given
        val b = Board()
        b.clear()
        b.put("e4", King(Color.WHITE))
        b.put("e3", Knight(Color.WHITE))
        b.put("f5", Rook(Color.BLACK))
        b.put("e1", Rook(Color.BLACK))

        // when
        val moveFinder = MoveFinder()
        val moves = moveFinder.findMoves(Color.WHITE, b, "e3")

        // then
        assertEquals(expected = emptyList(), actual = moves)
    }

    @Test
    fun should_filter_illegal_moves_producing_checks_rook_left() {
        // given
        val b = Board()
        b.clear()
        b.put("e4", King(Color.WHITE))
        b.put("d4", Bishop(Color.WHITE))
        b.put("a4", Rook(Color.BLACK))

        // when
        val moveFinder = MoveFinder()
        val moves = moveFinder.findMoves(Color.WHITE, b, "d4")

        // then
        assertEquals(expected = emptyList(), actual = moves)
    }

    @Test
    fun should_filter_illegal_moves_producing_checks_rook_right() {
        // given
        val b = Board()
        b.clear()
        b.put("e4", King(Color.WHITE))
        b.put("f4", Queen(Color.WHITE))
        b.put("h4", Rook(Color.BLACK))

        // when
        val moveFinder = MoveFinder()
        val moves = moveFinder.findMoves(Color.WHITE, b, "f4")

        // then
        assertEquals(expected = listOf(Move("f4g4"), Move("f4h4")), actual = moves)
    }

    @Test
    fun should_filter_illegal_moves_producing_checks_bishop_up_right() {
        // given
        val b = Board()
        b.clear()
        b.put("e3", King(Color.WHITE))
        b.put("f4", Pawn(Color.WHITE))
        b.put("e5", Pawn(Color.BLACK))
        b.put("h6", Bishop(Color.BLACK))
        printBoard(b)

        // when
        val moveFinder = MoveFinder()
        val moves = moveFinder.findMoves(Color.WHITE, b, "f4")

        // then
        assertEquals(expected = emptyList(), actual = moves)
    }

    @Test
    fun should_filter_illegal_moves_producing_checks_queen_up_left() {
        // given
        val b = Board()
        b.clear()
        b.put("e3", King(Color.WHITE))
        b.put("d4", Bishop(Color.WHITE))
        b.put("a7", Queen(Color.BLACK))

        // when
        val moveFinder = MoveFinder()
        val moves = moveFinder.findMoves(Color.WHITE, b, "d4")

        // then
        assertEquals(expected = listOf(Move("d4c5"), Move("d4b6"), Move("d4a7")), actual = moves)
    }

    @Test
    fun should_filter_illegal_moves_producing_checks_bishop_down_left() {
        // given
        val b = Board()
        b.clear()
        b.put("b1", King(Color.WHITE))
        b.put("a1", Bishop(Color.BLACK))
        printBoard(b)

        // when
        val moveFinder = MoveFinder()
        val moves = moveFinder.findMoves(Color.WHITE, b, "b1")

        // then
        assertEquals(expected = listOf(
            Move("b1a1"), Move("b1c1"),
            Move("b1c2"), Move("b1a2")
        ), actual = moves)
    }

    @Test
    fun should_filter_illegal_moves_producing_checks_bishop_down_right() {
        // given
        val b = Board()
        b.clear()
        b.put("e4", King(Color.WHITE))
        b.put("f3", Rook(Color.WHITE))
        b.put("g2", Bishop(Color.BLACK))

        // when
        val moveFinder = MoveFinder()
        val moves = moveFinder.findMoves(Color.WHITE, b, "f3")

        // then
        assertEquals(expected = emptyList(), actual = moves)
    }

    @Test
    fun should_filter_illegal_moves_only() {
        // given
        val b = Board()
        b.clear()
        b.put("e4", King(Color.WHITE))
        b.put("f3", Rook(Color.WHITE))
        b.put("g2", Bishop(Color.WHITE))

        // when
        val moveFinder = MoveFinder()
        val moves = moveFinder.findMoves(Color.WHITE, b, "f3")

        // then
        assertEquals(moves.isEmpty(), false)
    }
}
