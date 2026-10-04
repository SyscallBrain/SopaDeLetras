package app.sopadeletras.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DifficultyTest {
    @Test
    fun `easy has fewer words and restricted dirs`() {
        assertEquals(3, diffParams(6, Difficulty.EASY).words)
        assertEquals(BoardConfig.D2, diffParams(6, Difficulty.EASY).dirs)
        assertEquals(BoardConfig.D2, diffParams(8, Difficulty.EASY).dirs)
        assertEquals(BoardConfig.D3, diffParams(10, Difficulty.EASY).dirs)
        assertEquals(BoardConfig.D3, diffParams(12, Difficulty.EASY).dirs)
        assertEquals(0.0, diffParams(8, Difficulty.EASY).decoy, 0.0)
    }

    @Test
    fun `normal matches board table`() {
        assertEquals(4, diffParams(6, Difficulty.NORMAL).words)
        assertEquals(6, diffParams(8, Difficulty.NORMAL).words)
        assertEquals(8, diffParams(10, Difficulty.NORMAL).words)
        assertEquals(10, diffParams(12, Difficulty.NORMAL).words)
        assertEquals(BoardConfig.D2, diffParams(6, Difficulty.NORMAL).dirs)
        assertEquals(BoardConfig.D3, diffParams(8, Difficulty.NORMAL).dirs)
        assertEquals(BoardConfig.D5, diffParams(10, Difficulty.NORMAL).dirs)
        assertEquals(BoardConfig.D8, diffParams(12, Difficulty.NORMAL).dirs)
    }

    @Test
    fun `hard adds words all dirs decoys capped at max`() {
        assertEquals(8, diffParams(8, Difficulty.HARD).words)
        assertEquals(BoardConfig.D8, diffParams(12, Difficulty.HARD).dirs)
        assertEquals(0.8, diffParams(10, Difficulty.HARD).decoy, 0.0)
        for (n in listOf(6, 8, 10, 12)) {
            assertTrue(diffParams(n, Difficulty.HARD).words <= BoardConfig.maxWords(n))
        }
        assertEquals(BoardConfig.maxWords(6), diffParams(6, Difficulty.HARD).words)
    }
}
