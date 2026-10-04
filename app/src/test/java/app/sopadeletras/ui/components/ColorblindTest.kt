package app.sopadeletras.ui.components

import org.junit.Assert.assertEquals
import org.junit.Test

class ColorblindTest {
    @Test
    fun `each word color maps to its symbol`() {
        assertEquals("•", colorblindSymbol(0))
        assertEquals("○", colorblindSymbol(1))
        assertEquals("/", colorblindSymbol(2))
        assertEquals("◆", colorblindSymbol(3))
        assertEquals("✕", colorblindSymbol(4))
    }

    @Test
    fun `symbols wrap past the fifth color`() {
        assertEquals("•", colorblindSymbol(5))
        assertEquals("○", colorblindSymbol(6))
    }
}
