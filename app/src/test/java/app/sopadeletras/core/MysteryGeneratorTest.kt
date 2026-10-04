package app.sopadeletras.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MysteryGeneratorTest {
    @Test
    fun `mystery boards hold 4 to 8 free cells forming the mystery word`() {
        var total = 0
        var ok = 0
        var elapsed = 0L
        for (n in listOf(6, 8, 10)) {
            for ((catIndex, bank) in PT_BANKS.withIndex()) {
                for (s in 1..40) {
                    total++
                    val seed = s * 104729L
                    val t0 = System.nanoTime()
                    val puzzle = PuzzleGenerator.genMystery(n, bank, PT_MYSTERIES, seed, PT_FILL_REF)
                    elapsed += System.nanoTime() - t0
                    if (puzzle.mystery == null) continue
                    ok++
                    val m = puzzle.mystery
                    assertTrue("n=$n cat=$catIndex seed=$s", m.cells.size in 4..8)
                    val covered = puzzle.words.flatMap { it.cells }.toSet()
                    val free = puzzle.letters.indices.filter { it !in covered }
                    assertEquals("n=$n cat=$catIndex seed=$s", free, m.cells)
                    val read = m.cells.map { puzzle.letters[it] }.joinToString("")
                    assertEquals("n=$n cat=$catIndex seed=$s", m.word, read)
                    assertTrue(
                        "n=$n cat=$catIndex seed=$s",
                        puzzle.words.none { it.word == m.word },
                    )
                }
            }
        }
        assertTrue("success rate ${ok}/${total}", ok.toDouble() / total >= 0.99)
        val avgMs = elapsed / 1_000_000.0 / total
        assertTrue("avg ${avgMs}ms", avgMs < 30.0)
    }
}
