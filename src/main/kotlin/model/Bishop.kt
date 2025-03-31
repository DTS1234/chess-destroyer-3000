package adam.backend.portfolio.model

class Bishop(val c: Color) : Piece {
    override fun getSymbol(): String {
        return "B"
    }

    override fun getColor(): Color {
        return c;
    }
}