package app.sopadeletras.core

import kotlin.math.round

const val GENERATOR_VERSION = 1

data class MysteryEntry(val word: String, val clue: String)

internal class Mulberry32(seed: Long) {
    private var state: Int = seed.toInt()

    fun nextDouble(): Double {
        state = state + 0x6D2B79F5.toInt()
        var t = (state xor (state ushr 15)) * (1 or state)
        t = t + ((t xor (t ushr 7)) * (61 or t)) xor t
        return (t xor (t ushr 14)).toUInt().toDouble() / 4294967296.0
    }
}

internal fun <T> shuffled(list: List<T>, rnd: Mulberry32): List<T> {
    val a = list.toMutableList()
    for (i in a.size - 1 downTo 1) {
        val j = (rnd.nextDouble() * (i + 1)).toInt()
        val t = a[i]
        a[i] = a[j]
        a[j] = t
    }
    return a
}

object PuzzleGenerator {
    fun countOccurrences(letters: List<Char>, n: Int, word: String): Int {
        var count = 0
        for (r in 0 until n) {
            for (c in 0 until n) {
                for (d in BoardConfig.D8) {
                    var ok = true
                    for (k in word.indices) {
                        val rr = r + d.dr * k
                        val cc = c + d.dc * k
                        if (rr < 0 || rr >= n || cc < 0 || cc >= n || letters[rr * n + cc] != word[k]) {
                            ok = false
                            break
                        }
                    }
                    if (ok) count++
                }
            }
        }
        return count
    }

    private fun tryPlace(
        letters: Array<Char?>,
        n: Int,
        word: String,
        dirs: List<Dir>,
        rnd: Mulberry32,
    ): List<Int>? {
        repeat(300) {
            val d = dirs[(rnd.nextDouble() * dirs.size).toInt()]
            val r = (rnd.nextDouble() * n).toInt()
            val c = (rnd.nextDouble() * n).toInt()
            val er = r + d.dr * (word.length - 1)
            val ec = c + d.dc * (word.length - 1)
            if (er < 0 || er >= n || ec < 0 || ec >= n) return@repeat
            val path = ArrayList<Int>(word.length)
            var ok = true
            for (k in word.indices) {
                val idx = (r + d.dr * k) * n + c + d.dc * k
                if (letters[idx] != null && letters[idx] != word[k]) {
                    ok = false
                    break
                }
                path.add(idx)
            }
            if (!ok) return@repeat
            path.forEachIndexed { k, idx -> letters[idx] = word[k] }
            return path
        }
        return null
    }

    private fun genOnce(
        params: GenParams,
        bank: List<String>,
        seed: Long,
        fill: String,
    ): Puzzle {
        val n = params.n
        val rnd = Mulberry32(seed)
        val letters = arrayOfNulls<Char>(n * n)
        val words = mutableListOf<PlacedWord>()
        val candidates = shuffled(bank.filter { it.length in 3..n }, rnd)
        for (candidate in candidates) {
            if (words.size >= params.words) break
            val path = tryPlace(letters, n, candidate, params.dirs, rnd)
            if (path != null) words.add(PlacedWord(candidate, path))
        }
        val decoys = round(params.decoy * words.size * 0.5).toInt()
        repeat(decoys) {
            if (words.isEmpty()) return@repeat
            val src = words[(rnd.nextDouble() * words.size).toInt()].word
            if (src.length >= 5) tryPlace(letters, n, src.dropLast(2), params.dirs, rnd)
        }
        val filled = List(n * n) { i ->
            letters[i] ?: fill[(rnd.nextDouble() * fill.length).toInt()]
        }
        return Puzzle(n, filled, words, mystery = null, seed = seed)
    }

    fun generate(
        params: GenParams,
        bank: List<String>,
        seed: Long,
        fill: String,
    ): Puzzle {
        var best: Puzzle? = null
        for (a in 0 until 8) {
            val attempt = genOnce(params, bank, seed + a * 977L, fill)
            val ok = attempt.words.size >= params.words &&
                attempt.words.all { countOccurrences(attempt.letters, attempt.n, it.word) == 1 }
            if (ok) return attempt
            if (best == null || attempt.words.size > best.words.size) best = attempt
        }
        return best!!
    }

    fun genMystery(
        n: Int,
        words: List<String>,
        mysteryBank: Map<Int, MysteryEntry>,
        seed: Long,
        fill: String,
    ): Puzzle {
        for (a in 0 until 400) {
            genMysteryAttempt(n, words, mysteryBank, seed + a, fill)?.let { return it }
        }
        if (n > 8) return genMystery(8, words, mysteryBank, seed, fill)
        return generate(GenParams(n, BoardConfig.baseWords(n), BoardConfig.normalDirs(n), 0.2), words, seed, fill)
    }

    private fun genMysteryAttempt(
        n: Int,
        words: List<String>,
        mysteryBank: Map<Int, MysteryEntry>,
        seed: Long,
        fill: String,
    ): Puzzle? {
        val rnd = Mulberry32(seed)
        val dirs = BoardConfig.normalDirs(n)
        val letters = arrayOfNulls<Char>(n * n)
        val placed = mutableListOf<PlacedWord>()
        val candidates = shuffled(words.filter { it.length in 3..n }, rnd)
        var done = false
        for (candidate in candidates) {
            if (done) break
            val snapshot = letters.copyOf()
            val path = tryPlace(letters, n, candidate, dirs, rnd) ?: continue
            val empty = letters.count { it == null }
            if (empty < 4) {
                snapshot.copyInto(letters)
                // remove letters written by tryPlace already reverted via snapshot
                continue
            }
            placed.add(PlacedWord(candidate, path))
            if (empty <= 8) done = true
        }
        if (!done) return null
        val free = letters.indices.filter { letters[it] == null }
        val entry = mysteryBank[free.size] ?: return null
        free.forEachIndexed { k, idx -> letters[idx] = entry.word[k] }
        val filled = List(n * n) { i ->
            letters[i] ?: fill[(rnd.nextDouble() * fill.length).toInt()]
        }
        return Puzzle(
            n = n,
            letters = filled,
            words = placed,
            mystery = Mystery(entry.word, entry.clue, free),
            seed = seed,
        )
    }
}
