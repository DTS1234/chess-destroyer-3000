package adam.backend.portfolio.model

data class Knight(val c: Color) : Piece {
    override fun getSymbol(): String {
       return "N"
    }

    override fun getColor(): Color {
        return c;
    }
}