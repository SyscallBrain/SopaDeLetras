package app.sopadeletras.core

import java.text.Normalizer

fun parSeconds(words: Int, n: Int): Int = words * when (n) {
    6 -> 15
    8 -> 20
    10 -> 25
    else -> 30
}

fun starsFor(seconds: Int, hints: Int, words: Int, n: Int): Int {
    val par = parSeconds(words, n)
    val effective = seconds + 10 * hints
    return when {
        effective <= par -> 3
        effective <= 1.5 * par -> 2
        else -> 1
    }
}

fun nextColorIndex(usedColors: Set<Int>, foundCount: Int): Int {
    for (i in 0..4) if (i !in usedColors) return i
    return foundCount % 5
}

fun wordPoints(letters: Int, secondsSincePrevious: Double?): Int {
    var points = 10 * letters
    if (secondsSincePrevious != null && secondsSincePrevious < 6.0) points += 5
    return points
}

const val BOARD_COMPLETE_POINTS = 50
const val BOARD_COMPLETE_BONUS_SECONDS = 10

fun applyHintPenalty(remainingSeconds: Int, penalty: Int = 5): Int =
    maxOf(1, remainingSeconds - penalty)

fun recordBeaten(score: Int, record: Int): Boolean = score > record

fun normalizeWord(value: String): String {
    val decomposed = Normalizer.normalize(value, Normalizer.Form.NFD)
    return decomposed.filter { c ->
        Character.getType(c) != Character.NON_SPACING_MARK.toInt()
    }.uppercase()
}

fun isMysteryGuessCorrect(guess: String, answer: String): Boolean =
    normalizeWord(guess.trim()) == normalizeWord(answer.trim())

fun mysteryStars(solvedByGuess: Boolean, hintsUsed: Int): Int {
    val base = if (solvedByGuess) 3 else 2
    return maxOf(1, base - hintsUsed)
}
