package app.sopadeletras.core

import org.junit.Assert.assertEquals
import org.junit.Test

class SelectionTest {
    private val n = 8
    private fun idx(r: Int, c: Int) = r * n + c

    @Test
    fun `line horizontal forward`() {
        assertEquals(
            listOf(idx(3, 3), idx(3, 4), idx(3, 5)),
            lineCells(idx(3, 3), idx(3, 5), n),
        )
    }

    @Test
    fun `line horizontal backward`() {
        assertEquals(
            listOf(idx(3, 5), idx(3, 4), idx(3, 3)),
            lineCells(idx(3, 5), idx(3, 3), n),
        )
    }

    @Test
    fun `line vertical both ways`() {
        assertEquals(
            listOf(idx(1, 2), idx(2, 2), idx(3, 2)),
            lineCells(idx(1, 2), idx(3, 2), n),
        )
        assertEquals(
            listOf(idx(3, 2), idx(2, 2), idx(1, 2)),
            lineCells(idx(3, 2), idx(1, 2), n),
        )
    }

    @Test
    fun `line diagonal both ways`() {
        assertEquals(
            listOf(idx(1, 1), idx(2, 2), idx(3, 3)),
            lineCells(idx(1, 1), idx(3, 3), n),
        )
        assertEquals(
            listOf(idx(3, 3), idx(2, 2), idx(1, 1)),
            lineCells(idx(3, 3), idx(1, 1), n),
        )
    }

    @Test
    fun `line non-aligned returns only start`() {
        assertEquals(
            listOf(idx(3, 3)),
            lineCells(idx(3, 3), idx(4, 5), n),
        )
    }

    @Test
    fun `line start equals end`() {
        assertEquals(listOf(idx(2, 2)), lineCells(idx(2, 2), idx(2, 2), n))
    }

    @Test
    fun `match normal and reversed hit`() {
        val cells = listOf(idx(0, 0), idx(0, 1), idx(0, 2))
        val words = listOf(PlacedWord("SOL", cells))
        assertEquals(words[0], matchesSelection(cells, words, emptySet()))
        assertEquals(words[0], matchesSelection(cells.reversed(), words, emptySet()))
    }

    @Test
    fun `match partial or one extra cell misses`() {
        val cells = listOf(idx(0, 0), idx(0, 1), idx(0, 2))
        val words = listOf(PlacedWord("SOL", cells))
        assertEquals(null, matchesSelection(cells.dropLast(1), words, emptySet()))
        assertEquals(null, matchesSelection(cells + idx(0, 3), words, emptySet()))
    }

    @Test
    fun `match skips already found words`() {
        val cells = listOf(idx(0, 0), idx(0, 1))
        val words = listOf(PlacedWord("NO", cells))
        assertEquals(null, matchesSelection(cells, words, setOf("NO")))
    }

    // snapLine cases mirror design/generator-reference.snap.test.js
    private val a = 3 * 8 + 3 // 27

    @Test
    fun `snap tiny vector returns only start`() {
        assertEquals(listOf(a), snapLine(a, 0.2, 0.1, n))
    }

    @Test
    fun `snap right wobbly`() {
        assertEquals(listOf(27, 28, 29, 30), snapLine(a, 3.0, 0.4, n))
    }

    @Test
    fun `snap left to edge`() {
        assertEquals(listOf(27, 26, 25, 24), snapLine(a, -3.2, -0.5, n))
    }

    @Test
    fun `snap down`() {
        assertEquals(listOf(27, 35, 43, 51), snapLine(a, 0.3, 2.6, n))
    }

    @Test
    fun `snap diagonal down-right`() {
        assertEquals(listOf(27, 36, 45), snapLine(a, 2.0, 2.2, n))
    }

    @Test
    fun `snap diagonal up-left`() {
        assertEquals(listOf(27, 18, 9), snapLine(a, -2.0, -1.8, n))
    }

    @Test
    fun `snap diagonal up-right`() {
        assertEquals(listOf(27, 20, 13), snapLine(a, 2.0, -2.0, n))
    }

    @Test
    fun `snap clamps at edge`() {
        assertEquals(listOf(27, 28, 29, 30, 31), snapLine(a, 9.0, 0.0, n))
        assertEquals(listOf(27, 20, 13, 6), snapLine(a, 6.0, -6.0, n))
    }

    @Test
    fun `snap single step`() {
        assertEquals(listOf(27, 28), snapLine(a, 1.2, 0.4, n))
    }

    @Test
    fun `snap never returns cells outside board`() {
        val corner = 0
        val sel = snapLine(corner, -5.0, -5.0, n)
        assertEquals(listOf(corner), sel)
        sel.forEach { assert(it in 0 until n * n) }
    }
}
