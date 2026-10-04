package app.sopadeletras.ui.theme

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Test

class BoardMetricsTest {
    @Test
    fun `scale is 1 at reference width`() {
        assertEquals(1.0f, scaleFactor(393), 0.0001f)
    }

    @Test
    fun `scale grows linearly and clamps`() {
        assertEquals(412f / 393f, scaleFactor(412), 0.0001f)
        assertEquals(0.85f, scaleFactor(200), 0.0001f)
        assertEquals(1.3f, scaleFactor(800), 0.0001f)
    }

    @Test
    fun `cell radius follows board size`() {
        assertEquals(12.dp, cellRadiusDp(6))
        assertEquals(12.dp, cellRadiusDp(8))
        assertEquals(10.dp, cellRadiusDp(10))
        assertEquals(9.dp, cellRadiusDp(12))
    }

    @Test
    fun `cell spacing follows board size`() {
        assertEquals(4.dp, cellSpacingDp(6))
        assertEquals(4.dp, cellSpacingDp(8))
        assertEquals(3.dp, cellSpacingDp(10))
        assertEquals(3.dp, cellSpacingDp(12))
    }

    @Test
    fun `grid of cells plus spacing exactly fills board minus padding`() {
        for (n in listOf(6, 8, 10, 12)) {
            val board = 1000f
            val padding = 36f
            val spacing = 12f
            val cell = cellSizePx(board, padding, spacing, n)
            assertEquals(board - 2 * padding, n * cell + (n - 1) * spacing, 0.001f)
        }
    }
}
