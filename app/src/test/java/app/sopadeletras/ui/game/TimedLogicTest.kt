package app.sopadeletras.ui.game

import org.junit.Assert.assertEquals
import org.junit.Test

class TimedLogicTest {
    @Test
    fun `timed limit is 1-3x par`() {
        // 8x8, 6 words: par = 120 -> limit 156
        assertEquals(156, timedLimit(words = 6, n = 8))
    }

    @Test
    fun `remaining never goes below zero`() {
        assertEquals(10, remainingSeconds(limit = 100, elapsed = 90))
        assertEquals(0, remainingSeconds(limit = 100, elapsed = 100))
        assertEquals(0, remainingSeconds(limit = 100, elapsed = 150))
    }
}
