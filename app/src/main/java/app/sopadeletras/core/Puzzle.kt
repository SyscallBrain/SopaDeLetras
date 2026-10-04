package app.sopadeletras.core

data class Dir(val dr: Int, val dc: Int)

data class PlacedWord(val word: String, val cells: List<Int>)

data class GenParams(val n: Int, val words: Int, val dirs: List<Dir>, val decoy: Double)

data class Mystery(val word: String, val clue: String, val cells: List<Int>)

data class Puzzle(
    val n: Int,
    val letters: List<Char>,
    val words: List<PlacedWord>,
    val mystery: Mystery?,
    val seed: Long,
)
