package app.sopadeletras.ui.game

import app.sopadeletras.core.PlacedWord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GameLogicTest {
    @Test
    fun `format elapsed shows mm-ss`() {
        assertEquals("00:00", formatElapsed(0))
        assertEquals("00:07", formatElapsed(7))
        assertEquals("01:05", formatElapsed(65))
        assertEquals("12:00", formatElapsed(720))
    }

    @Test
    fun `victory summary uses singular for one word`() {
        assertEquals("6 palavras em 2m 05s", victorySummary(6, 125))
        assertEquals("1 palavra em 0m 09s", victorySummary(1, 9))
    }

    @Test
    fun `hint targets first cell of first unfound word`() {
        val words = listOf(
            PlacedWord("SOL", listOf(0, 1, 2)),
            PlacedWord("LUA", listOf(10, 11, 12)),
        )
        assertEquals(0, hintTarget(words, emptySet()))
        assertEquals(10, hintTarget(words, setOf("SOL")))
        assertNull(hintTarget(words, setOf("SOL", "LUA")))
    }
}
