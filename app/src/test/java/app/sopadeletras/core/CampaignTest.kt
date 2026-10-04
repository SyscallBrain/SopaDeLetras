package app.sopadeletras.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CampaignTest {
    @Test
    fun `levelInfo matches design table first and 29th levels`() {
        val expected = listOf(
            Triple(6, 3 to 4, 2),
            Triple(8, 4 to 6, 3),
            Triple(8, 6 to 8, 5),
            Triple(10, 6 to 9, 3),
            Triple(10, 8 to 11, 5),
            Triple(10, 10 to 12, 8),
            Triple(12, 8 to 12, 5),
            Triple(12, 10 to 14, 8),
            Triple(12, 12 to 16, 8),
            Triple(12, 14 to 18, 8),
        )
        for (w in 0..9) {
            val first = levelInfo(w * 30 + 1)
            val last = levelInfo(w * 30 + 29)
            val boss = levelInfo(w * 30 + 30)
            val (n, range, dirs) = expected[w]
            assertEquals("world $w n", n, first.n)
            assertEquals("world $w first", range.first, first.words)
            assertEquals("world $w 29th", range.second, last.words)
            assertEquals("world $w boss", minOf(range.second + 2, BoardConfig.maxWords(n)), boss.words)
            assertEquals("world $w dirs", dirs, first.dirsCount)
            assertEquals(LevelKind.TIMED, levelInfo(w * 30 + 10).kind)
            assertEquals(LevelKind.MYSTERY, levelInfo(w * 30 + 20).kind)
            assertEquals(LevelKind.BOSS, boss.kind)
        }
    }

    @Test
    fun `all 300 levels generate valid boards deterministically in both languages`() {
        for (language in listOf("pt-PT", "en-US")) {
            val fill = if (language == "pt-PT") PT_FILL_REF else EN_FILL_REF
            for (level in 1..300) {
                val info = levelInfo(level)
                val seed = campaignSeed(level, language)
                val bank = PT_BANKS[info.category % PT_BANKS.size]
                val puzzle = if (info.kind == LevelKind.MYSTERY) {
                    PuzzleGenerator.genMystery(minOf(info.n, 10), bank, PT_MYSTERIES, seed, fill)
                } else {
                    PuzzleGenerator.generate(levelParams(info), bank, seed, fill)
                }
                val again = if (info.kind == LevelKind.MYSTERY) {
                    PuzzleGenerator.genMystery(minOf(info.n, 10), bank, PT_MYSTERIES, seed, fill)
                } else {
                    PuzzleGenerator.generate(levelParams(info), bank, seed, fill)
                }
                assertEquals("L$level letters", puzzle.letters, again.letters)
                if (info.kind == LevelKind.MYSTERY) {
                    assertTrue("L$level mystery", puzzle.mystery != null)
                } else {
                    assertEquals("L$level words", info.words, puzzle.words.size)
                    // Strict uniqueness only where the category has a real bank
                    // (worlds 1-5, like the reference levels test). Worlds 6-10
                    // reuse the 5 reference banks until M8 delivers final banks.
                    if (level <= 150) {
                        for (w in puzzle.words) {
                            assertEquals("L$level single $w", 1, PuzzleGenerator.countOccurrences(puzzle.letters, puzzle.n, w.word))
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `campaign seed differs per language`() {
        assertEquals(7919L, campaignSeed(1, "pt-PT"))
        assertEquals(7920L, campaignSeed(1, "en-US"))
    }

    @Test
    fun `unlock opens next level and gates worlds`() {
        assertTrue(isLevelUnlocked(1, emptySet()))
        assertFalse(isLevelUnlocked(2, emptySet()))
        assertTrue(isLevelUnlocked(2, setOf(1)))
        assertFalse(isLevelUnlocked(31, (1..29).toSet()))
        assertTrue(isLevelUnlocked(31, (1..30).toSet()))
        assertFalse(isWorldUnlocked(1, (1..29).toSet()))
        assertTrue(isWorldUnlocked(1, (1..30).toSet()))
    }

    @Test
    fun `best stars keep the maximum`() {
        assertEquals(3, bestStars(2, 3))
        assertEquals(3, bestStars(3, 1))
    }
}
