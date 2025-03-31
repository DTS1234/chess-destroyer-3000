package finder

import adam.backend.portfolio.model.Bishop
import adam.backend.portfolio.model.Board
import adam.backend.portfolio.model.Color
import adam.backend.portfolio.model.King
import adam.backend.portfolio.model.Knight
import adam.backend.portfolio.model.Move
import adam.backend.portfolio.model.Pawn
import adam.backend.portfolio.model.Rook
import adam.backend.portfolio.finder.MoveFinder
import adam.backend.portfolio.printBoard
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayNameGeneration
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@DisplayNameGeneration(ReplaceUnderscores::class)
class KingMoveFinderTest {

    @Test
    fun should_find_king_moves() {
        val b = Board()
        b.clear()
        b.board[4][4].piece = King(Color.BLACK)

        val moveFinder = MoveFinder()
        val moves = moveFinder.findMoves(Color.BLACK, b, "e4")

        println(moves)
        printBoard(b)
        assertEquals(
            listOf(
                Move("e4e5"), Move("e4d4"), Move("e4e3"),
                Move("e4f4"), Move("e4f5"), Move("e4f3"),
                Move("e4d3"), Move("e4d5")
            ).containsAll(moves) && moves.size == 8,
            true
        )
    }

    @Test
    fun should_find_king_short_castle() {
        val b = Board()
        b.move(Move("e2e4")) //w
        b.move(Move("e7e5")) //b
        b.move(Move("f1c4")) //w
        b.move(Move("g8f6")) //b
        b.move(Move("g1f3")) //w
        b.move(Move("h7h6")) //b

        val moveFinder = MoveFinder()
        val moves = moveFinder.findMoves(Color.WHITE, b, "e1")

        assertEquals(
            listOf(
                Move("e1e2"), Move("e1f1"), Move("O-O")
            ),
            moves
        )
    }

    @Test
    fun should_NOT_find_king_short_castle_if_square_in_between_is_under_check() {
        val b = Board()
        b.clear()
        b.put("e1", King(Color.WHITE))
        b.put("h1", Rook(Color.WHITE))
        b.put("h2", Pawn(Color.BLACK))

        printBoard(b)

        val moveFinder = MoveFinder()
        val moves = moveFinder.findMoves(Color.WHITE, b, "e1")

        assertEquals(
            listOf(
                Move("e1e2"), Move("e1d1"), Move("e1f1"), Move("e1f2"), Move("e1d2")
            ),
            moves
        )
    }

    @Test
    fun should_NOT_find_king_short_castle_if_king_moved() {
        val b = Board()
        b.clear()
        b.put("e1", King(Color.WHITE))
        b.put("h1", Rook(Color.WHITE))
        b.move(Move("e1e2"))
        b.move(Move("e2e1"))

        val moveFinder = MoveFinder()
        val moves = moveFinder.findMoves(Color.WHITE, b, "e1")

        assertEquals(
            listOf(
                Move("e1e2"), Move("e1d1"), Move("e1f1"), Move("e1f2"), Move("e1d2")
            ),
            moves
        )
    }

    @Test
    fun should_NOT_find_king_short_castle_if_rook_moved() {
        val b = Board()
        b.clear()
        b.put("e1", King(Color.WHITE))
        b.put("h1", Rook(Color.WHITE))
        b.move(Move("h1h8"))
        b.move(Move("h8h1"))

        val moveFinder = MoveFinder()
        val moves = moveFinder.findMoves(Color.WHITE, b, "e1")

        assertEquals(
            listOf(
                Move("e1e2"), Move("e1d1"), Move("e1f1"), Move("e1f2"), Move("e1d2")
            ),
            moves
        )
    }

    @Test
    fun should_NOT_find_king_short_castle_if_h1_under_attack() {
        val b = Board()
        b.clear()
        b.put("e1", King(Color.WHITE))
        b.put("h1", Rook(Color.WHITE))
        b.put("h2", Rook(Color.BLACK))

        val moveFinder = MoveFinder()
        val moves = moveFinder.findMoves(Color.WHITE, b, "e1")

        assertEquals(
            listOf(
                Move("e1d1"), Move("e1f1")
            ),
            moves
        )
    }

    @Test
    fun should_find_king_long_castle() {
        val b = Board()
        b.move(Move("e2e4")) //w
        b.move(Move("e7e5")) //b
        b.move(Move("d2d4")) //w
        b.move(Move("f7f6")) //b
        b.move(Move("c1e3")) //w
        b.move(Move("h7h6")) //b
        b.move(Move("d1f3")) //w
        b.move(Move("f8e7")) //b
        b.move(Move("b1c3")) //w
        b.move(Move("b8c6")) //b

        printBoard(b)

        val moveFinder = MoveFinder()
        val moves = moveFinder.findMoves(Color.WHITE, b, "e1")

        assertEquals(
            listOf(
                Move("e1e2"), Move("e1d1"), Move("e1d2"), Move("O-O-O")
            ),
            moves
        )
    }

    @Test
    fun should_NOT_find_king_long_castle_if_square_in_between_is_under_check() {
        val b = Board()
        b.clear()
        b.put("e1", King(Color.WHITE))
        b.put("a1", Rook(Color.WHITE))
        b.put("a2", Knight(Color.BLACK))

        printBoard(b)

        val moveFinder = MoveFinder()
        val moves = moveFinder.findMoves(Color.WHITE, b, "e1")

        assertEquals(
            listOf(
                Move("e1e2"), Move("e1d1"), Move("e1f1"), Move("e1f2"), Move("e1d2")
            ),
            moves
        )
    }

    @Test
    fun should_NOT_find_king_long_castle_if_king_moved() {
        val b = Board()
        b.clear()
        b.put("e1", King(Color.WHITE))
        b.put("h1", Rook(Color.WHITE))
        b.move(Move("e1e2"))
        b.move(Move("e2e1"))

        val moveFinder = MoveFinder()
        val moves = moveFinder.findMoves(Color.WHITE, b, "e1")

        assertEquals(
            listOf(
                Move("e1e2"), Move("e1d1"), Move("e1f1"), Move("e1f2"), Move("e1d2")
            ),
            moves
        )
    }

    @Test
    fun should_NOT_find_king_long_castle_if_rook_moved() {
        val b = Board()
        b.clear()
        b.put("e1", King(Color.WHITE))
        b.put("h1", Rook(Color.WHITE))
        b.move(Move("h1h8"))
        b.move(Move("h8h1"))

        val moveFinder = MoveFinder()
        val moves = moveFinder.findMoves(Color.WHITE, b, "e1")

        assertEquals(
            listOf(
                Move("e1e2"), Move("e1d1"), Move("e1f1"), Move("e1f2"), Move("e1d2")
            ),
            moves
        )
    }

    @Test
    fun should_NOT_find_king_long_castle_if_a8_under_attack() {
        val b = Board()
        b.clear()
        b.put("e1", King(Color.WHITE))
        b.put("a1", Rook(Color.WHITE))
        b.put("a2", Rook(Color.BLACK))

        printBoard(b)

        val moveFinder = MoveFinder()
        val moves = moveFinder.findMoves(Color.WHITE, b, "e1")

        assertEquals(
            listOf(
                Move("e1d1"), Move("e1f1")
            ),
            moves
        )
    }

    @Test
    fun should_be_able_to_move_out_of_check() {
        val b = Board()
        b.clear()
        b.put("e4", King(Color.WHITE))
        b.put("e8", Rook(Color.BLACK))
        b.put("f7", Bishop(Color.BLACK))
        b.put("e1", Knight(Color.BLACK))
        b.put("g6", Pawn(Color.BLACK))
        b.put("c4", King(Color.BLACK))

        printBoard(b)

        val moveFinder = MoveFinder()
        val moves = moveFinder.findMoves(Color.WHITE, b, "e4")

        println(moves)

        assertEquals(
            expected = listOf(Move("e4f4")),
            actual = moves
        )
    }
}
