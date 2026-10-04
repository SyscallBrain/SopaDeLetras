package app.sopadeletras.core

enum class Difficulty {
    EASY,
    NORMAL,
    HARD,
}

fun diffParams(n: Int, difficulty: Difficulty): GenParams {
    val base = BoardConfig.baseWords(n)
    return when (difficulty) {
        Difficulty.EASY -> GenParams(
            n = n,
            words = maxOf(3, base - 1),
            dirs = if (n >= 10) BoardConfig.D3 else BoardConfig.D2,
            decoy = 0.0,
        )
        Difficulty.HARD -> GenParams(
            n = n,
            words = minOf(BoardConfig.maxWords(n), base + 2),
            dirs = BoardConfig.D8,
            decoy = 0.8,
        )
        Difficulty.NORMAL -> GenParams(
            n = n,
            words = base,
            dirs = BoardConfig.normalDirs(n),
            decoy = 0.2,
        )
    }
}
