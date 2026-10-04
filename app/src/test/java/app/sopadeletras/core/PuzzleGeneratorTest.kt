package app.sopadeletras.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PuzzleGeneratorTest {
    @Test
    fun `same seed and bank gives identical board`() {
        val params = GenParams(8, 6, BoardConfig.D3, 0.2)
        val a = PuzzleGenerator.generate(params, PT_ESPACO, 7919L, PT_FILL_REF)
        val b = PuzzleGenerator.generate(params, PT_ESPACO, 7919L, PT_FILL_REF)
        assertEquals(a.letters, b.letters)
        assertEquals(a.words, b.words)
    }

    @Test
    fun `all sizes place requested words readable in cells with allowed dirs`() {
        val sizes = listOf(6 to 4, 8 to 6, 10 to 8, 12 to 10)
        for ((n, count) in sizes) {
            val allowed = when (n) {
                6 -> BoardConfig.D2.toSet()
                8 -> BoardConfig.D3.toSet()
                10 -> BoardConfig.D5.toSet()
                else -> BoardConfig.D8.toSet()
            }
            for (seed in 1..200) {
                val params = GenParams(n, count, allowed.toList(), 0.2)
                val puzzle = PuzzleGenerator.generate(params, PT_ESPACO, seed * 7919L, PT_FILL_REF)
                assertEquals("n=$n seed=$seed", count, puzzle.words.size)
                assertTrue(puzzle.letters.all { it in 'A'..'Z' })
                for (w in puzzle.words) {
                    val read = w.cells.map { puzzle.letters[it] }.joinToString("")
                    assertEquals("n=$n seed=$seed", w.word, read)
                    assertEquals("n=$n seed=$seed", 1, PuzzleGenerator.countOccurrences(puzzle.letters, n, w.word))
                    val dir = directionOf(w.cells, n)
                    assertTrue("n=$n seed=$seed dir=$dir", dir in allowed)
                }
            }
        }
    }

    @Test
    fun `different seeds usually differ`() {
        val params = GenParams(8, 6, BoardConfig.D3, 0.2)
        val a = PuzzleGenerator.generate(params, PT_ESPACO, 1L, PT_FILL_REF)
        val b = PuzzleGenerator.generate(params, PT_ESPACO, 2L, PT_FILL_REF)
        assertTrue(a.letters != b.letters || a.words != b.words)
    }

    @Test
    fun `rng matches reference mulberry32 sequence`() {
        val r = Mulberry32(7919L)
        assertEquals(0.6900994984898716, r.nextDouble(), 1e-15)
        assertEquals(0.7416439699009061, r.nextDouble(), 1e-15)
        assertEquals(0.37708328501321375, r.nextDouble(), 1e-15)
    }

    @Test
    fun `level 213 pt matches reference board exactly`() {
        // Golden values produced by design/generator-reference.js:
        // levelInfo(213) -> n=12 words=10 D8 decoy=0.6 seed=1686747, COMIDA bank.
        val params = GenParams(12, 10, BoardConfig.D8, 0.6)
        val puzzle = PuzzleGenerator.generate(params, PT_COMIDA, 1686747L, PT_FILL_REF)
        assertEquals(
            listOf("MACA", "ARROZ", "BATATA", "MASSA", "PASTEL", "CARNE", "CEBOLA", "QUEIJO", "UVA", "MANTEIGA"),
            puzzle.words.map { it.word },
        )
        assertEquals(
            "TOMODSAAEAICNNUARROZGEMEEEOAARENMAORNOOESEMCVLJAIBSGSLPUAOICOGAUAEIDTTEEESGTMTASAAUBGUNOASOITLQOCARNEAIAAOAASOEROPTEBBUMOCMACAIRIEGIAGIETNAMGCDA",
            puzzle.letters.joinToString(""),
        )
    }

    private fun directionOf(cells: List<Int>, n: Int): Dir {
        if (cells.size < 2) return Dir(0, 1)
        val r0 = cells[0] / n
        val c0 = cells[0] % n
        val r1 = cells[1] / n
        val c1 = cells[1] % n
        return Dir(r1 - r0, c1 - c0)
    }
}
