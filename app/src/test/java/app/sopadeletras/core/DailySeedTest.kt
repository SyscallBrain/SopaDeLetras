package app.sopadeletras.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DailySeedTest {
    @Test
    fun `same date gives same seed board and category`() {
        val a = PuzzleGenerator.generate(
            GenParams(10, 8, BoardConfig.D5, 0.2),
            PT_ESPACO, dailySeedNumber(2026, 10, 4), PT_FILL_REF,
        )
        val b = PuzzleGenerator.generate(
            GenParams(10, 8, BoardConfig.D5, 0.2),
            PT_ESPACO, dailySeedNumber(2026, 10, 4), PT_FILL_REF,
        )
        assertEquals(a.letters, b.letters)
        assertEquals(dailyCategoryIndex(2026, 10, 4), dailyCategoryIndex(2026, 10, 4))
    }

    @Test
    fun `consecutive dates rotate category`() {
        val seen = (0..6).map { dailyCategoryIndex(2026, 10, 4 + it) }.toSet()
        assertTrue(seen.size > 1)
    }

    @Test
    fun `streak rises day to day resets after a missed day`() {
        val monday = dayNumber(2026, 10, 5)
        val tuesday = dayNumber(2026, 10, 6)
        val thursday = dayNumber(2026, 10, 8)
        assertEquals(6, updateStreak(lastDay = monday, today = tuesday, streak = 5))
        assertEquals(1, updateStreak(lastDay = monday, today = thursday, streak = 5))
        assertEquals(5, updateStreak(lastDay = tuesday, today = tuesday, streak = 5))
    }

    @Test
    fun `displayed streak is zero when stale`() {
        val today = dayNumber(2026, 10, 6)
        assertEquals(0, displayedStreak(lastDay = dayNumber(2026, 10, 4), today = today, streak = 5))
        assertEquals(5, displayedStreak(lastDay = today, today = today, streak = 5))
        assertEquals(5, displayedStreak(lastDay = today - 1, today = today, streak = 5))
    }
}
