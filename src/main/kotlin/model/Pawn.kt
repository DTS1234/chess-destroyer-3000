package adam.backend.portfolio.model

data class Pawn(val c: Color) : Piece {
    override fun getSymbol(): String {
        return "p"
    }

    override fun getColor(): Color {
        return c;
    }
}