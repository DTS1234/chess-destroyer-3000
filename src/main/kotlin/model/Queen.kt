package adam.backend.portfolio.model

data class Queen(val c: Color): Piece {
    override fun getSymbol(): String {
        return "Q"
    }

    override fun getColor(): Color {
        return c
    }
}