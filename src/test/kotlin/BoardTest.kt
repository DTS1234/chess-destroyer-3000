import adam.backend.portfolio.ChessEngine
import adam.backend.portfolio.finder.MoveFinder
import adam.backend.portfolio.model.Bishop
import adam.backend.portfolio.model.Board
import adam.backend.portfolio.model.Color
import adam.backend.portfolio.model.King
import adam.backend.portfolio.model.Knight
import adam.backend.portfolio.model.Move
import adam.backend.portfolio.model.Pawn
import adam.backend.portfolio.model.Queen
import adam.backend.portfolio.model.Rook
import adam.backend.portfolio.model.Square
import adam.backend.portfolio.printBoard
import org.junit.jupiter.api.DisplayNameGeneration
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@DisplayNameGeneration(ReplaceUnderscores::class)
class BoardTest {

    @Test
    fun should_create_from_FEN() {
        val fen = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq - 0 1"
        val board = Board()
        board.fromFEN(fen)
        printBoard(board)
    }

    @Test
    fun should_be_check() {
        val b = Board()
        b.clear()

        b.put("h6", King(Color.BLACK))
        b.put("f7", Pawn(Color.BLACK))
        b.put("c2", Rook(Color.BLACK))
        b.put("g8", Rook(Color.WHITE))
        b.put("f6", Bishop(Color.WHITE))
        b.put("e5", Pawn(Color.WHITE))
        b.put("g4", Pawn(Color.WHITE))
        b.put("f4", Pawn(Color.WHITE))
        b.put("a1", King(Color.WHITE))

        b.move(Move("g8h8"));

        printBoard(b)

        assertTrue(b.isKingInCheck(b.findKing(Color.BLACK)!!, defendingColor = Color.BLACK))

        val moveFinder = MoveFinder();
        val moves = moveFinder.findAll(b, Color.BLACK)

        b.move(Move("h6g6"))
        printBoard(b)

        val chessEngine = ChessEngine()
        val callForWhite = chessEngine.findBestMove(b, 4, Color.WHITE)
        println(callForWhite)
    }

    @Test
    fun should_be_checkmate_3() {
        val b = Board()
        b.clear()

        b.put("a7", Pawn(Color.BLACK))
        b.put("b8", King(Color.BLACK))
        b.put("b7", Queen(Color.WHITE))
        b.put("c6", Bishop(Color.WHITE))
        b.put("b4", Bishop(Color.BLACK))
        b.put("g1", King(Color.WHITE))

        assertTrue(b.isCheckmate(Color.BLACK))
    }

    @Test
    fun should_not_be_checkmate_black() {
        val b = Board()

        b.put("g6", Queen(Color.WHITE))
        b.put("f7", null)

        printBoard(b)

        assertFalse(b.isCheckmate(Color.BLACK))
    }


    @Test
    fun should_not_be_checkmate() {
        val b = Board()
        b.clear()

        b.put("f6", King(Color.WHITE))
        b.put("e5", Queen(Color.BLACK))
        b.put("f4", Pawn(Color.WHITE))
        b.put("c7", Queen(Color.BLACK))
        b.put("h7", King(Color.BLACK))

        printBoard(b)

        assertFalse(b.isCheckmate(Color.WHITE))
        assertFalse(b.isCheckmate(Color.BLACK))
    }

    @Test
    fun should_find_checkmate() {
        val b = Board()
        b.move(Move("e2e4"))
        b.move(Move("e7e5"))
        b.move(Move("f1c4"))
        b.move(Move("c8c6"))
        b.move(Move("d1f3"))
        b.move(Move("a7a6"))
        b.move(Move("f3f7"))

        assertTrue(b.isCheckmate(Color.BLACK))
    }

    @Test
    fun should_find_checkmate_2() {
        val b = Board()
        b.move(Move("e2e4"))
        b.move(Move("f7f6"))
        b.move(Move("a2a3"))
        b.move(Move("g7g5"))
        b.move(Move("d1h5"))

        assertTrue(b.isCheckmate(Color.BLACK))
    }

    @TestFactory
    fun should_find_stalemate() = listOf(
        DynamicTest.dynamicTest("stalemate 1") {
            val b = stalemate_1()
            printBoard(b)
            assertTrue(b.isDraw())
        },
        DynamicTest.dynamicTest("stalemate 2") {
            val b = stalemate_2()
            printBoard(b)
            assertTrue(b.isDraw())
        },
        DynamicTest.dynamicTest("stalemate 3") {
            val b = stalemate_3()
            printBoard(b)
            assertTrue(b.isDraw())
        },
        DynamicTest.dynamicTest("insufficient material 1") {
            val b = insufficient_material_1()
            printBoard(b)
            assertTrue(b.isDraw())
        },
        DynamicTest.dynamicTest("insufficient material 2") {
            val b = insufficient_material_2()
            printBoard(b)
            assertTrue(b.isDraw())
        },
        DynamicTest.dynamicTest("insufficient material 3") {
            val b = insufficient_material_3()
            printBoard(b)
            assertTrue(b.isDraw())
        }
    )

    private fun stalemate_1(): Board {
        val b = Board()
        b.clear()
        b.setTurn(Color.BLACK)

        b.put("f6", King(Color.WHITE))
        b.put("f7", Pawn(Color.WHITE))
        b.put("f8", King(Color.BLACK))
        return b
    }

    private fun stalemate_2(): Board {
        val b = Board()
        b.clear()
        b.setTurn(Color.BLACK)

        b.put("a8", King(Color.BLACK))
        b.put("h8", Rook(Color.WHITE))
        b.put("b8", Bishop(Color.BLACK))
        b.put("b6", King(Color.WHITE))
        return b
    }

    private fun stalemate_3(): Board {
        val b = Board()
        b.clear()
        b.setTurn(Color.BLACK)

        b.put("a2", Pawn(Color.BLACK))
        b.put("a1", King(Color.BLACK))
        b.put("b3", Queen(Color.WHITE))
        b.put("g5", King(Color.WHITE))
        return b
    }

    private fun insufficient_material_1(): Board {
        val b = Board()
        b.clear()

        b.put("a1", King(Color.WHITE))
        b.put("a8", King(Color.BLACK))
        return b
    }

    private fun insufficient_material_2(): Board {
        val b = Board()
        b.clear()
        b.setTurn(Color.BLACK)

        b.put("a1", King(Color.BLACK))
        b.put("g7", Bishop(Color.WHITE))
        b.put("g5", King(Color.WHITE))
        return b
    }

    private fun insufficient_material_3(): Board {
        val b = Board()
        b.clear()
        b.setTurn(Color.BLACK)

        b.put("a1", King(Color.BLACK))
        b.put("h8", Knight(Color.WHITE))
        b.put("g5", King(Color.WHITE))
        return b
    }


    @Test
    fun should_not_be_marked_as_attacked() {
        val b = Board()
        assertFalse(b.isAttacked(Square("e1", King(Color.WHITE)), Color.WHITE))
        assertFalse(b.isAttacked(Square("e1", King(Color.WHITE)), Color.WHITE))
        b.move(Move("c2c3"))
        assertFalse(b.isAttacked(Square("e8", King(Color.BLACK)), Color.BLACK))
    }
}
