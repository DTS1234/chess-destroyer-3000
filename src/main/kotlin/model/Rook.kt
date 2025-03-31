package adam.backend.portfolio.model

data class Rook(val c: Color) : Piece {
    override fun getSymbol(): String {
        return "R"
    }

    override fun getColor(): Color {
        return c
    }
}