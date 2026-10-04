package app.sopadeletras.ui.stats

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class StatsCalcTest {
    private val results = listOf(
        ResultRow(mode = "CAMPAIGN", n = 8, seconds = 100, stars = 3, score = 0, words = 6),
        ResultRow(mode = "FREE", n = 8, seconds = 80, stars = 3, score = 0, words = 6),
        ResultRow(mode = "FREE", n = 6, seconds = 45, stars = 2, score = 0, words = 4),
        ResultRow(mode = "TIME", n = 8, seconds = 180, stars = 0, score = 320, words = 20),
        ResultRow(mode = "MYSTERY", n = 8, seconds = 90, stars = 3, score = 0, words = 5),
    )

    @Test
    fun `wins count all victories`() {
        assertEquals(5, wins(results))
        assertEquals(0, wins(emptyList()))
    }

    @Test
    fun `words found sum across results`() {
        assertEquals(41, wordsFound(results))
    }

    @Test
    fun `best classic time per size ignores time attack and mystery`() {
        val best = bestClassicTimes(results)
        assertEquals(45, best[6])
        assertEquals(80, best[8])
        assertNull(best[10])
        assertNull(best[12])
    }

    @Test
    fun `best time attack is max score`() {
        assertEquals(320, bestTimeAttack(results))
        assertNull(bestTimeAttack(results.filter { it.mode != "TIME" }))
    }

    @Test
    fun `mysteries solved count`() {
        assertEquals(1, mysteriesSolved(results))
    }

    @Test
    fun `campaign totals sum levels and stars`() {
        assertEquals(2 to 5, campaignTotals(mapOf(1 to 3, 2 to 2)))
        assertEquals(0 to 0, campaignTotals(emptyMap()))
    }
}
