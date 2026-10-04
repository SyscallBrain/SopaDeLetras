package app.sopadeletras.data

import org.junit.Assert.assertEquals
import org.junit.Test

class ProgressTest {
    @Test
    fun `progress keys carry language and level`() {
        assertEquals("cp_pt-PT_12_stars", progressStarsKey("pt-PT", 12))
        assertEquals("cp_en-US_300_seconds", progressSecondsKey("en-US", 300))
    }

    @Test
    fun `campaign stars parse only own language`() {
        val entries = mapOf(
            "cp_pt-PT_1_stars" to 3,
            "cp_pt-PT_2_stars" to 2,
            "cp_en-US_1_stars" to 1,
            "other" to 5,
        )
        assertEquals(mapOf(1 to 3, 2 to 2), campaignStars(entries, "pt-PT"))
        assertEquals(mapOf(1 to 1), campaignStars(entries, "en-US"))
    }

    @Test
    fun `found words round-trip as comma string`() {
        assertEquals("SOL,LUA", encodeFound(listOf("SOL", "LUA")))
        assertEquals(listOf("SOL", "LUA"), decodeFound("SOL,LUA"))
        assertEquals(emptyList<String>(), decodeFound(""))
    }

    @Test
    fun `result rows round-trip through strings`() {
        val row = StoredResult(
            mode = "CAMPAIGN", language = "pt-PT", difficulty = "", level = 12,
            n = 8, category = "oceano", seconds = 95, hints = 1, stars = 3,
            score = 0, words = 6, finishedAt = 1728000000L,
        )
        assertEquals(row, decodeResult(encodeResult(row)))
    }
}
