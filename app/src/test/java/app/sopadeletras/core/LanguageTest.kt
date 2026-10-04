package app.sopadeletras.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LanguageTest {
    @Test
    fun `pt and en fills differ`() {
        assertTrue(Language.PT_FILL != Language.EN_FILL)
        assertEquals(PT_FILL_REF, Language.PT_FILL)
        assertEquals(EN_FILL_REF, Language.EN_FILL)
    }

    @Test
    fun `switching language changes board letters`() {
        val params = GenParams(8, 6, BoardConfig.D3, 0.2)
        val pt = PuzzleGenerator.generate(params, PT_ESPACO, 7919L, Language.PT_FILL)
        val en = PuzzleGenerator.generate(params, PT_ESPACO, 7919L, Language.EN_FILL)
        assertTrue(pt.letters != en.letters)
    }

    @Test
    fun `twelve by twelve generates in under 50ms average`() {
        val params = GenParams(12, 10, BoardConfig.D8, 0.2)
        val t0 = System.nanoTime()
        val runs = 20
        repeat(runs) {
            PuzzleGenerator.generate(params, PT_ESPACO, (it + 1) * 7919L, PT_FILL_REF)
        }
        val avgMs = (System.nanoTime() - t0) / 1_000_000.0 / runs
        assertTrue("avg ${avgMs}ms", avgMs < 50.0)
    }
}
