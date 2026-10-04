package app.sopadeletras.core

object BoardConfig {
    val D2 = listOf(Dir(0, 1), Dir(1, 0))
    val D3 = listOf(Dir(0, 1), Dir(1, 0), Dir(1, 1))
    val D5 = listOf(Dir(0, 1), Dir(1, 0), Dir(1, 1), Dir(0, -1), Dir(-1, 0))
    val D8 = listOf(
        Dir(0, 1), Dir(1, 0), Dir(1, 1), Dir(0, -1),
        Dir(-1, 0), Dir(-1, -1), Dir(1, -1), Dir(-1, 1),
    )

    fun baseWords(n: Int): Int = when (n) {
        6 -> 4
        8 -> 6
        10 -> 8
        else -> 10
    }

    fun maxWords(n: Int): Int = when (n) {
        6 -> 5
        8 -> 9
        10 -> 12
        else -> 18
    }

    fun normalDirs(n: Int): List<Dir> = when (n) {
        6 -> D2
        8 -> D3
        10 -> D5
        else -> D8
    }
}
