package adam.backend.portfolio.model

enum class Color {
    BLACK, WHITE;

    fun opposite(): Color {
        return if (this == BLACK) WHITE else BLACK
    }
}