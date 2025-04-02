package adam.backend.portfolio.model

import adam.backend.portfolio.finder.MoveFinder
import adam.backend.portfolio.finder.MoveFinder.Companion.isOnBoard

class Board {
    val board: MutableList<MutableList<Square>>

    val moves: MutableList<Move> = mutableListOf();

    init {
        this.board = initBoard()
    }

    fun clear() {
        board.forEachIndexed { i, v -> v.forEachIndexed { j, s -> s.piece = null } }
    }

    fun setTurn(color: Color) {
        moves.clear()
        if (color == Color.BLACK) {
            moves.add(Move("z8z9"))
        }
    }

    fun put(coordinate: String, piece: Piece?) {
        val idx = 8 - coordinate[1].digitToInt()
        val row = this.board.get(idx)
        val found = row.find { s -> s.value == coordinate }

        found?.piece = piece
    }

    fun shortCastle() {
        val isBlack = moves.size % 2 != 0
        if (isBlack) {
            val kingSquare = findSquare("e8")
            val rookSquare = findSquare("h8")
            val kingTo = findSquare("g8")
            val rookTo = findSquare("f8")

            kingSquare?.piece = null
            rookSquare?.piece = null
            kingTo?.piece = King(Color.BLACK)
            rookTo?.piece = Rook(Color.BLACK)
        } else {
            val kingSquare = findSquare("e1")
            val rookSquare = findSquare("h1")
            val kingTo = findSquare("g1")
            val rookTo = findSquare("f1")

            kingSquare?.piece = null
            rookSquare?.piece = null
            kingTo?.piece = King(Color.WHITE)
            rookTo?.piece = Rook(Color.WHITE)
        }
    }

    fun move(move: Move) {
        val isShortCastle = move.value.startsWith("O-O")
        if (isShortCastle) {
            shortCastle()
            moves.add(Move("O-O"))
            return
        }
        val isLongCastle = move.value.startsWith("O-O-O")
        if (isLongCastle) {
            longCastle()
            moves.add(Move("O-O-O"))
            return
        }

        val from = move.value.substring(0, 2)
        val to = move.value.substring(2, 4);

        val idx = 8 - from[1].digitToInt()
        val row = this.board.get(idx)

        val foundFrom = row.find { s -> s.value == from }

        val color = foundFrom?.piece?.getColor()
        assert(color != null) { println("You have to select a piece.") }

        val temp = foundFrom?.piece
        foundFrom?.piece = null

        val idxTo = 8 - to[1].digitToInt()
        val rowTo = this.board[idxTo]
        val foundTo = rowTo.find { s -> s.value == to }
        foundTo?.piece = temp

        if (foundTo?.value?.get(1)?.digitToInt() == 8 && temp is Pawn && temp.getColor() == Color.WHITE) {
            foundTo.piece = Queen(temp.getColor())
        }

        if (foundTo?.value?.get(1)?.digitToInt() == 1 && temp is Pawn && temp.getColor() == Color.BLACK) {
            foundTo.piece = Queen(temp.getColor())
        }

        handleEnPassant(move, foundTo, color!!)

        moves.add(Move("$from$to"))
    }

    private fun handleEnPassant(move: Move, foundTo: Square?, color: Color) {
        val isEnPassant = move.value.endsWith("E")
        if (!isEnPassant) return

        if (color == Color.WHITE) {
            val pawnToRemoveCoordinate = foundTo?.down()?.value
            assert(pawnToRemoveCoordinate != null) { println("pawn taken with en passant cannot be null") }
            val pawnToRemoveSquare = findSquare(pawnToRemoveCoordinate!!)
            pawnToRemoveSquare?.piece = null
        }

        if (color == Color.BLACK) {
            val pawnToRemoveCoordinate = foundTo?.up()?.value
            assert(pawnToRemoveCoordinate != null) { println("pawn taken with en passant cannot be null") }
            val pawnToRemoveSquare = findSquare(pawnToRemoveCoordinate!!)
            pawnToRemoveSquare?.piece = null
        }
    }

    private fun longCastle() {
        val isBlack = moves.size % 2 == 0
        if (isBlack) {
            val kingSquare = findSquare("e8")
            val rookSquare = findSquare("a8")
            val kingTo = findSquare("c8")
            val rookTo = findSquare("d8")

            kingSquare?.piece = null
            rookSquare?.piece = null
            kingTo?.piece = King(Color.BLACK)
            rookTo?.piece = Rook(Color.BLACK)
        } else {
            val kingSquare = findSquare("e1")
            val rookSquare = findSquare("a1")
            val kingTo = findSquare("c1")
            val rookTo = findSquare("d1")

            kingSquare?.piece = null
            rookSquare?.piece = null
            kingTo?.piece = King(Color.WHITE)
            rookTo?.piece = Rook(Color.WHITE)
        }
    }

    private fun initBoard(): MutableList<MutableList<Square>> {
        val b: MutableList<MutableList<Square>> = mutableListOf()
        for (i in 8 downTo 1) {
            if (i == 1 || i == 8) {
                if (i % 2 != 0) {
                    b.add(createPieceRow(color = Color.WHITE, i))
                } else {
                    b.add(createPieceRow(color = Color.BLACK, i))
                }
            } else if (i == 2 || i == 7) {
                if (i % 2 == 0) {
                    b.add(createPawnRow(color = Color.WHITE, i))
                } else {
                    b.add(createPawnRow(color = Color.BLACK, i))
                }
            } else {
                b.add(createEmptyRow(i))
            }
        }
        return b
    }

    fun createPawnRow(color: Color, row: Int): MutableList<Square> {
        val list: MutableList<Square> = mutableListOf()
        var char = 'a';
        for (i in 1..8) {
            list.add(Square("$char$row", Pawn(color)))
            char++;
        }
        return list;
    }

    fun createEmptyRow(row: Int): MutableList<Square> {
        val list: MutableList<Square> = mutableListOf()
        var char = 'a';
        for (i in 1..8) {
            list.add(Square("$char$row", null))
            char++;
        }
        return list;
    }

    fun createPieceRow(color: Color, row: Int): MutableList<Square> {
        val list: MutableList<Square> = mutableListOf()
        val pieces: MutableList<Piece> = mutableListOf(
            Rook(color), Knight(color), Bishop(color), Queen(color),
            King(color), Bishop(color), Knight(color), Rook(color)
        )
        var char = 'a';
        for (i in 1..8) {
            list.add(Square("$char$row", pieces[i - 1]))
            char++;
        }
        return list;
    }

    fun findSquare(coordinates: String): Square? {
        if (!coordinates[1].isDigit()) {
            return null
        }
        val idx = 8 - coordinates[1].digitToInt()
        if (idx >= 8 || idx < 0) {
            return null
        }
        val row = this.board[idx]

        val found: Square = row.find { s -> s.value == coordinates } ?: return null
        return found;
    }

    fun findKing(color: Color): Square? {
        return this.board.flatten().find { s ->
            s.piece is King && s.piece?.getColor() == color
        }?.let {
            return it
        }
    }

    fun isAttacked(square: Square, color: Color): Boolean {

        var toCheck = square.left();
        while (isOnBoard(toCheck.value)) {
            val piece = findSquare(toCheck.value)?.piece
            if (listOf("Q", "R").contains(piece?.getSymbol()) && piece?.getColor() != null && piece.getColor() != color) {
                return true
            } else if (piece != null) {
                break
            }
            toCheck = toCheck.left()
        }
        toCheck = square.right();
        while (isOnBoard(toCheck.value)) {
            val piece = findSquare(toCheck.value)?.piece
            if (listOf("Q", "R").contains(piece?.getSymbol()) && piece?.getColor() != null && piece.getColor() != color) {
                return true;
            } else if (piece != null) {
                break
            }
            toCheck = toCheck.right();
        }
        toCheck = square.up();
        while (isOnBoard(toCheck.value)) {
            val piece = findSquare(toCheck.value)?.piece
            if (listOf("Q", "R").contains(piece?.getSymbol()) && piece?.getColor() != null && piece.getColor() != color) {
                return true;
            } else if (piece != null) {
                break
            }
            toCheck = toCheck.up();
        }
        toCheck = square.down();
        while (isOnBoard(toCheck.value)) {
            val piece = findSquare(toCheck.value)?.piece
            if (listOf("Q", "R").contains(piece?.getSymbol()) && piece?.getColor() != null && piece.getColor() != color) {
                return true;
            } else if (piece != null) {
                break
            }
            toCheck = toCheck.down();
        }
        toCheck = square.up().left();
        while (isOnBoard(toCheck.value)) {
            val piece = findSquare(toCheck.value)?.piece
            if (listOf("Q", "B").contains(piece?.getSymbol()) && piece?.getColor() != null && piece.getColor() != color) {
                return true;
            } else if (piece != null) {
                break
            }
            toCheck = toCheck.up().left();
        }

        toCheck = square.up().right();
        while (isOnBoard(toCheck.value)) {
            val piece = findSquare(toCheck.value)?.piece
            if (listOf("Q", "B").contains(piece?.getSymbol()) && piece?.getColor() != null && piece.getColor() != color) {
                return true;
            } else if (piece != null) {
                break
            }
            toCheck = toCheck.up().right();
        }

        toCheck = square.down().left();
        while (isOnBoard(toCheck.value)) {
            val piece = findSquare(toCheck.value)?.piece
            if (listOf("Q", "B").contains(piece?.getSymbol()) && piece?.getColor() != null && piece.getColor() != color) {
                return true;
            } else if (piece != null) {
                break
            }
            toCheck = toCheck.down().left();
        }
        toCheck = square.down().right();
        while (isOnBoard(toCheck.value)) {
            val piece = findSquare(toCheck.value)?.piece
            if (listOf("Q", "B").contains(piece?.getSymbol()) && piece?.getColor() != null && piece.getColor() != color) {
                return true;
            } else if (piece != null) {
                break
            }
            toCheck = toCheck.down().right();
        }

        // is attacked by pawn ?
        if (color == Color.WHITE) {
            val upRight = findSquare(square.up().right().value)
            if (upRight?.piece == Pawn(Color.BLACK)) {
                return true
            }
            val upLeft = findSquare(square.up().left().value)
            if (upLeft?.piece == Pawn(Color.BLACK)) {
                return true
            }
        } else {
            val downRight = findSquare(square.down().right().value)
            if (downRight?.piece == Pawn(Color.WHITE)) {
                return true
            }
            val downLeft = findSquare(square.down().left().value)
            if (downLeft?.piece == Pawn(Color.WHITE)) {
                return true
            }
        }

        val oppositeColor = if (color == Color.WHITE) {
            Color.BLACK
        } else {
            Color.WHITE
        }

        // is attacked by a king ?

        val kingMoves = listOf(
            square.up(), square.down(), square.left(), square.right(),
            square.up().left(), square.up().right(), square.down().left(), square.down().right()
        )

        val isKingCheck = kingMoves
            .mapNotNull { findSquare(it.value) }
            .filter { isOnBoard(it.value) }
            .any { it.piece == King(oppositeColor) }

        if (isKingCheck) {
            return true
        }

        // is attacked by a knight ?
        val knightMoves = listOf(
            square.up().up().right(), square.up().up().left(), square.down().down().left(), square.down().down().right(),
            square.left().left().up(), square.left().left().down(), square.right().right().down(), square.right().right().up()
        )
        val isKnightCheck = knightMoves
            .mapNotNull { findSquare(it.value) }
            .filter { isOnBoard(it.value) }
            .any { it.piece == Knight(oppositeColor) }

        return isKnightCheck
    }

    fun isKingInCheck(king: Square, defendingColor: Color): Boolean {
        return isAttacked(square = king, color = defendingColor)
    }

    fun copy(): Board {
        val b = Board()
        b.clear()

        for (i in 0..7) {
            for (j in 0..7) {
                val square = this.board[i][j]
                if (square.piece != null) {
                    b.put(square.value, square.piece!!)
                }
            }
        }

        b.moves.addAll(this.moves)

        return b
    }

    fun isCheckmate(colorToLose: Color): Boolean {
        val turn = if (colorToLose == Color.WHITE) Color.BLACK else Color.WHITE
        val king = findKing(colorToLose)
        val moveFinder = MoveFinder()
        if (king != null && isAttacked(king, colorToLose)) {

            // if there is any move that can prevent the checkmate then it's not checkmate
            moveFinder.findAll(this, colorToLose).forEach { move ->
                val b = copy()
                b.move(move)
                val king1 = b.findKing(colorToLose)
                if (king1 != null && !b.isKingInCheck(king1, colorToLose)) {
                    return false
                }
            }

            return moveFinder.findMoves(colorToLose, this, king.value).isEmpty()
        }
        return false
    }

    fun isDraw(): Boolean {
        val isNotEmptySquareAndNotAKing: (Square) -> Boolean = { it.piece != null && it.piece?.getSymbol() != "K" }
        val flatBoard = board.flatten()
        val onlyKings = flatBoard.none(isNotEmptySquareAndNotAKing)
        val whitePieces = flatBoard.map { it.piece }.filter { it?.getColor() == Color.WHITE }
        val blackPieces = flatBoard.map { it.piece }.filter { it?.getColor() == Color.BLACK }

        if (blackPieces[0] is King && blackPieces.size == 1) {
            if (whitePieces.size < 3) {
                whitePieces.filter { it?.getSymbol() != "K" }.forEach {
                    if (it?.getSymbol() == "B" || it?.getSymbol() == "N") {
                        return true
                    }
                }
            }
        } else if (whitePieces[0] is King && whitePieces.size == 1) {
            if (blackPieces.size < 3) {
                blackPieces.filter { it?.getSymbol() != "K" }.forEach {
                    if (it?.getSymbol() == "B" || it?.getSymbol() == "N") {
                        return true
                    }
                }
            }
        }

        if (onlyKings) {
            return true
        } else {
            return isStalemate()
        }
    }

    private fun isStalemate(): Boolean {
        val turn = if (moves.size % 2 == 0) Color.WHITE else Color.BLACK
        val moveFinder = MoveFinder()
        val possibleMovesForPosition = moveFinder.findAll(this, turn)
        return !isCheckmate(turn) && possibleMovesForPosition.isEmpty()
    }

    fun fromFEN(fen: String) {
        clear()
        val parts = fen.split(" ")
        val rows = parts[0].split("/")
        for (i in rows.indices) {
            var col = 0
            for (char in rows[i]) {
                when (char) {
                    'r' -> put("${'a' + col}${8 - i}", Rook(Color.BLACK))
                    'n' -> put("${'a' + col}${8 - i}", Knight(Color.BLACK))
                    'b' -> put("${'a' + col}${8 - i}", Bishop(Color.BLACK))
                    'q' -> put("${'a' + col}${8 - i}", Queen(Color.BLACK))
                    'k' -> put("${'a' + col}${8 - i}", King(Color.BLACK))
                    'p' -> put("${'a' + col}${8 - i}", Pawn(Color.BLACK))
                    'R' -> put("${'a' + col}${8 - i}", Rook(Color.WHITE))
                    'N' -> put("${'a' + col}${8 - i}", Knight(Color.WHITE))
                    'B' -> put("${'a' + col}${8 - i}", Bishop(Color.WHITE))
                    'Q' -> put("${'a' + col}${8 - i}", Queen(Color.WHITE))
                    'K' -> put("${'a' + col}${8 - i}", King(Color.WHITE))
                    'P' -> put("${'a' + col}${8 - i}", Pawn(Color.WHITE))
                    else -> col += char.digitToInt() - 1
                }
                col++
            }
        }
        setTurn(if (parts[1] == "b") Color.BLACK else Color.WHITE)
    }

}

data class Square(val value: String, var piece: Piece?) {
    override fun toString(): String {
        val greenColor = "\u001b[32m"
        val redColor = "\u001b[31m"
        val reset = "\u001b[0m" // to reset color to the default

        if (piece != null && piece!!.getColor() == Color.BLACK) {
            return redColor + piece!!.getSymbol() + reset
        } else if (piece != null && piece!!.getColor() == Color.WHITE) {
            return greenColor + piece!!.getSymbol() + reset
        } else {
            return " "
        }
    }

    fun isEmpty(): Boolean {
        return piece == null
    }

    fun up(): Square {
        var row: Char = value[1]
        val col = value[0]
        row++
        return Square("$col$row", null)
    }

    fun down(): Square {
        var row: Char = value[1]
        val col = value[0];
        row--
        return Square("$col$row", null)
    }

    fun right(): Square {
        val row: Char = value[1]
        var col = value[0];
        col++
        return Square("$col$row", null)
    }

    fun left(): Square {
        val row: Char = value[1]
        var col = value[0];
        col--
        return Square("$col$row", null)
    }

}

data class Move(val value: String) {

}