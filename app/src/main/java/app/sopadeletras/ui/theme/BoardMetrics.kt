package app.sopadeletras.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

const val REFERENCE_WIDTH_DP = 393f
const val MAX_BOARD_WIDTH_DP = 560
const val BOARD_PADDING_DP = 12
const val BOARD_RADIUS_DP = 30
const val LETTER_FRACTION = 0.46f

fun scaleFactor(screenWidthDp: Int): Float =
    (screenWidthDp / REFERENCE_WIDTH_DP).coerceIn(0.85f, 1.3f)

fun cellRadiusDp(n: Int): Dp = when (n) {
    6, 8 -> 12.dp
    10 -> 10.dp
    else -> 9.dp
}

fun cellSpacingDp(n: Int): Dp = if (n <= 8) 4.dp else 3.dp

fun cellSizePx(boardWidthPx: Float, paddingPx: Float, spacingPx: Float, n: Int): Float =
    (boardWidthPx - 2 * paddingPx - spacingPx * (n - 1)) / n
