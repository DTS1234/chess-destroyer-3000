package finder

import adam.backend.portfolio.finder.MoveFinder
import adam.backend.portfolio.model.Board
import adam.backend.portfolio.model.Color
import adam.backend.portfolio.model.King
import adam.backend.portfolio.model.Knight
import adam.backend.portfolio.model.Move
import adam.backend.portfolio.model.Pawn
import adam.backend.portfolio.model.Queen
import adam.backend.portfolio.printBoard
import org.junit.jupiter.api.DisplayNameGeneration
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@DisplayNameGeneration(ReplaceUnderscores::class)
class PawnMoveFinderTest {

    @Test
    fun should_promote_white_pawn_move() {
        // given
        val b = Board()
        b.clear()
        b.board[1][4].piece = Pawn(Color.WHITE)

        // when
        val moveFinder = MoveFinder()
        val moves = moveFinder.findMoves(Color.WHITE, b, "e7")

        // then
        assertEquals(moves, mutableListOf(Move("e7e8")))

        //and
        b.move(Move("e7e8"))

        // then
        assertEquals(Queen(Color.WHITE), b.findSquare("e8")?.piece)
    }

    @Test
    fun should_NOT_promote_white_pawn_move() {
        // given
        val b = Board()
        b.clear()
        b.put("a2", Pawn(Color.BLACK))
        b.put("a1", King(Color.WHITE))
        b.put("c8", King(Color.BLACK))

        // when
        val moveFinder = MoveFinder()
        val moves = moveFinder.findMoves(Color.BLACK, b, "a2")

        // then
        assertEquals(moves, emptyList())
    }

    @Test
    fun should_promote_white_pawn_take() {
        // given
        val b = Board()
        b.clear()
        b.board[1][4].piece = Pawn(Color.WHITE)
        b.board[0][5].piece = Knight(Color.BLACK)

        // when
        val moveFinder = MoveFinder()
        val moves = moveFinder.findMoves(Color.WHITE, b, "e7")

        // then
        assertEquals(moves, mutableListOf(Move("e7e8"), Move("e7f8")))

        // and
        b.move(Move("e7f8"))

        // then
        assertEquals(Queen(Color.WHITE), b.findSquare("f8")?.piece)
    }

    @Test
    fun should_promote_black_pawn_move() {
        // given
        val b = Board()
        b.clear()
        b.board[6][4].piece = Pawn(Color.BLACK)

        // when
        val moveFinder = MoveFinder()
        val moves = moveFinder.findMoves(Color.BLACK, b, "e2")

        // then
        assertEquals(moves, mutableListOf(Move("e2e1")))

        // and
        b.move(Move("e2e1"))

        //then
        assertEquals(Queen(Color.BLACK), b.findSquare("e1")?.piece)
    }

    @Test
    fun should_find_en_passant_black() {
        // given
        val b = Board()
        b.move(Move("e2e4"))
        b.move(Move("b7b5"))
        b.move(Move("e4e5"))
        b.move(Move("b5b4"))

        // when
        b.move(Move("c2c4"))

        // then
        val moveFinder = MoveFinder()
        val moves = moveFinder.findMoves(Color.BLACK, b, "b4")

        // then
        assertEquals(moves, mutableListOf(Move("b4b3"), Move("b4c3E")))
    }

    @Test
    fun should_find_en_passant_for_white() {
        // given
        val b = Board()

        b.move(Move("e2e4"))
        b.move(Move("a7a6"))
        b.move(Move("e4e5"))
        b.move(Move("d7d5"))

        // when
        val moveFinder = MoveFinder()
        val moves = moveFinder.findMoves(Color.WHITE, b, "e5")

        // then
        assertEquals(moves, mutableListOf(Move("e5e6"), Move("e5d6E")))

        // and
        b.move(Move("e5d6E"))
        printBoard(b)
        b.findSquare("d5")?.isEmpty()?.let { assertTrue(it) }
    }

    @Test
    fun should_find_starting_moves_black() {
        // given
        val b = Board()
        b.move(Move("b2b4"))

        // when
        val moveFinder = MoveFinder()
        val moves = moveFinder.findMoves(Color.BLACK, b, "a7")

        // then
        assertEquals(moves, mutableListOf(Move("a7a5"), Move("a7a6")))
    }

    @Test
    fun should_not_allow_forward_move() {
        // given
        val b = Board()
        b.clear()
        b.put("a6", Pawn(Color.BLACK))
        b.put("a5", King(Color.WHITE))
        // when
        val moveFinder = MoveFinder()
        val moves = moveFinder.findMoves(Color.BLACK, b, "a6")

        // then
        assertEquals(moves, emptyList())
    }

}