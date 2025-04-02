import adam.backend.portfolio.ChessEngine
import adam.backend.portfolio.model.Bishop
import adam.backend.portfolio.model.Board
import adam.backend.portfolio.model.Color
import adam.backend.portfolio.model.King
import adam.backend.portfolio.model.Move
import adam.backend.portfolio.model.Pawn
import adam.backend.portfolio.model.Queen
import adam.backend.portfolio.model.Rook
import adam.backend.portfolio.printBoard
import org.junit.jupiter.api.DisplayNameGeneration
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@DisplayNameGeneration(ReplaceUnderscores::class)
class ChessEngineTest {

    @Test
    fun should_punish_early_queen_moves() {
        val chesEngine = ChessEngine()
        val b = Board()

        b.move(Move("e2e3"))//w
        b.move(Move("e7e5"))//b
        b.move(Move("d1e2"))//w

        val evaluate = chesEngine.evaluate(b, maximizingColor = Color.WHITE, 3)
        assertEquals(expected = -2, actual = evaluate)
    }

    @Test
    fun should_evaluate_castled_king() {
        val chesEngine = ChessEngine()
        val b = Board()

        b.move(Move("e2e4"))//w
        b.move(Move("e7e5"))//b
        b.move(Move("f1e2"))//w
        b.move(Move("f8e7"))//b
        b.move(Move("g1f3"))//w
        b.move(Move("a7a6"))//b
        b.move(Move("O-O"))//w

        printBoard(b)

        assertEquals(actual = chesEngine.evaluate(b, Color.WHITE, 1), expected = 10)
    }

    @Test
    fun should_evaluate_checkmate_black() {
        val chesEngine = ChessEngine()
        val b = Board()
        b.clear()

        b.put("a1", King(Color.WHITE))
        b.put("b8", Rook(Color.BLACK))
        b.put("a7", Rook(Color.BLACK))
        b.put("h8", King(Color.BLACK))

        assertEquals(actual = chesEngine.evaluate(b, Color.BLACK, 1), expected = 9999)
    }

    @Test
    fun should_evaluate_checkmate_white() {
        val chesEngine = ChessEngine()
        val b = Board()
        b.clear()

        b.put("e1", King(Color.BLACK))
        b.put("a2", Rook(Color.WHITE))
        b.put("e2", Queen(Color.WHITE))
        b.put("h8", King(Color.WHITE))

        printBoard(b)

        assertEquals(chesEngine.evaluate(b, Color.WHITE, 1), 9999)
    }

    @Test
    fun should_recommend_checkmate_black() {
        val chesEngine = ChessEngine()
        val b = Board()
        b.clear()
        b.setTurn(Color.BLACK)

        b.put("a1", King(Color.WHITE))
        b.put("b8", Rook(Color.BLACK))
        b.put("c7", Rook(Color.BLACK))
        b.put("h8", King(Color.BLACK))

        printBoard(b)
        val move = chesEngine.findBestMove(b, 1, Color.BLACK)
        b.move(move)

        assertEquals(actual = move, expected = Move("c7a7"))
        assertTrue(b.isCheckmate(Color.WHITE))
    }

    @Test
    fun should_recommend_checkmate_white() {
        val chesEngine = ChessEngine()
        val b = Board()
        b.clear()
        b.setTurn(Color.WHITE)

        b.put("e1", King(Color.BLACK))
        b.put("a2", Rook(Color.WHITE))
        b.put("c4", Queen(Color.WHITE))
        b.put("h8", King(Color.WHITE))

        val move = chesEngine.findBestMove(b, 1, Color.WHITE)
        b.move(move)

        assertTrue(b.isCheckmate(Color.BLACK))
    }

    @Test
    fun should_find_mate_in_2_white() {
        val chesEngine = ChessEngine()
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

        printBoard(b)

        val whiteMove = chesEngine.findBestMove(b, 4, Color.WHITE)
        println(whiteMove)
        b.move(whiteMove)
        val blackMove = chesEngine.findBestMove(b, 4, Color.BLACK)
        println(blackMove)
        printBoard(b)

        //assertTrue(b.isCheckmate(Color.BLACK))
        assertEquals("g8h8", whiteMove.value)
    }
}
