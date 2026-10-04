package app.sopadeletras.core

import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.round
import kotlin.math.sign
import kotlin.math.sqrt

fun lineCells(start: Int, end: Int, n: Int): List<Int> {
    if (start == end) return listOf(start)
    val ar = start / n
    val ac = start % n
    val br = end / n
    val bc = end % n
    val dr = br - ar
    val dc = bc - ac
    if (!(dr == 0 || dc == 0 || abs(dr) == abs(dc))) return listOf(start)
    val len = maxOf(abs(dr), abs(dc))
    val stepR = sign(dr.toDouble()).toInt()
    val stepC = sign(dc.toDouble()).toInt()
    return (0..len).map { k -> (ar + stepR * k) * n + ac + stepC * k }
}

private val SNAP_DIRS = listOf(
    Dir(0, 1), Dir(1, 1), Dir(1, 0), Dir(1, -1),
    Dir(0, -1), Dir(-1, -1), Dir(-1, 0), Dir(-1, 1),
)

fun snapLine(start: Int, vx: Double, vy: Double, n: Int): List<Int> {
    if (sqrt(vx * vx + vy * vy) < 0.6) return listOf(start)
    val sector = ((round(atan2(vy, vx) / (Math.PI / 4)).toInt() % 8) + 8) % 8
    val d = SNAP_DIRS[sector]
    val k = round((vx * d.dc + vy * d.dr) / (d.dr * d.dr + d.dc * d.dc).toDouble()).toInt()
    if (k <= 0) return listOf(start)
    val r0 = start / n
    val c0 = start % n
    val out = mutableListOf(start)
    for (s in 1..k) {
        val r = r0 + d.dr * s
        val c = c0 + d.dc * s
        if (r < 0 || r >= n || c < 0 || c >= n) break
        out.add(r * n + c)
    }
    return out
}

fun matchesSelection(
    selection: List<Int>,
    words: List<PlacedWord>,
    foundWords: Set<String>,
): PlacedWord? {
    for (w in words) {
        if (w.word in foundWords) continue
        if (selection == w.cells || selection == w.cells.reversed()) return w
    }
    return null
}
