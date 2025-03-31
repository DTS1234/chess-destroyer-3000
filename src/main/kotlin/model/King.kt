package adam.backend.portfolio.model

data class King(val c: Color) : Piece {
    override fun getSymbol(): String {
        return "K"
    }

    override fun getColor(): Color {
        return c;
    }
}