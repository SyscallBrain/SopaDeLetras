package app.sopadeletras.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.sopadeletras.core.PlacedWord
import app.sopadeletras.core.Puzzle
import app.sopadeletras.core.lineCells
import app.sopadeletras.core.snapLine
import app.sopadeletras.ui.theme.BOARD_PADDING_DP
import app.sopadeletras.ui.theme.BOARD_RADIUS_DP
import app.sopadeletras.ui.theme.Bricolage
import app.sopadeletras.ui.theme.LETTER_FRACTION
import app.sopadeletras.ui.theme.LocalSopaColors
import app.sopadeletras.ui.theme.MAX_BOARD_WIDTH_DP
import app.sopadeletras.ui.theme.cellRadiusDp
import app.sopadeletras.ui.theme.cellSizePx
import app.sopadeletras.ui.theme.cellSpacingDp
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun LetterGrid(
    puzzle: Puzzle,
    selectedCells: List<Int>,
    foundColors: Map<String, Int>,
    popCells: Set<Int>,
    popProgress: Float,
    directionAssist: Boolean,
    animationsEnabled: Boolean,
    onSelectionChange: (List<Int>) -> Unit,
    onSelectionEnd: (List<Int>) -> Unit,
    modifier: Modifier = Modifier,
    hintCell: Int? = null,
    highlightCells: Set<Int> = emptySet(),
    letterScale: Float = 1f,
    colorblind: Boolean = false,
) {
    val colors = LocalSopaColors.current
    val density = LocalDensity.current
    val n = puzzle.n
    val spacing = cellSpacingDp(n)
    val radius = cellRadiusDp(n)

    val latestChange = rememberUpdatedState(onSelectionChange)
    val latestEnd = rememberUpdatedState(onSelectionEnd)

    val selectedSet = remember(selectedCells) { selectedCells.toSet() }
    val foundCellColor = remember(puzzle, foundColors) {
        val byWord: Map<String, PlacedWord> = puzzle.words.associateBy { it.word }
        buildMap {
            for ((word, color) in foundColors) {
                byWord[word]?.cells?.forEach { put(it, color) }
            }
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val boardPx = minOf(maxWidth, MAX_BOARD_WIDTH_DP.dp).let {
            with(density) { it.toPx() }
        }
        val spacingPx = with(density) { spacing.toPx() }
        val paddingPx = with(density) { BOARD_PADDING_DP.dp.toPx() }
        val cellPx = cellSizePx(boardPx, paddingPx, spacingPx, n)
        val pitch = cellPx + spacingPx
        val cellDp = with(density) { cellPx.toDp() }
        // Board letters ignore the system font scale (own setting instead).
        val letterSp = with(density) { (cellPx * LETTER_FRACTION * letterScale).toSp() } / density.fontScale

        fun indexAt(x: Float, y: Float): Int {
            val c = (x / pitch).toInt().coerceIn(0, n - 1)
            val r = (y / pitch).toInt().coerceIn(0, n - 1)
            return r * n + c
        }

        fun selectionFor(start: Int, x: Float, y: Float): List<Int> {
            if (!directionAssist) return lineCells(start, indexAt(x, y), n)
            val cx = (start % n) * pitch + cellPx / 2f
            val cy = (start / n) * pitch + cellPx / 2f
            return snapLine(start, ((x - cx) / pitch).toDouble(), ((y - cy) / pitch).toDouble(), n)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(BOARD_RADIUS_DP.dp))
                .background(colors.surface)
                .padding(BOARD_PADDING_DP.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(spacing),
                modifier = Modifier
                    .pointerInput(n, directionAssist, cellPx, pitch) {
                        awaitEachGesture {
                            val down = awaitFirstDown()
                            val start = indexAt(down.position.x, down.position.y)
                            var current = selectionFor(start, down.position.x, down.position.y)
                            latestChange.value(current)
                            val slop = awaitTouchSlopOrCancellation(down.id) { change, _ ->
                                val sel = selectionFor(start, change.position.x, change.position.y)
                                if (sel != current) {
                                    current = sel
                                    latestChange.value(sel)
                                }
                                change.consume()
                            }
                            if (slop != null) {
                                drag(slop.id) { change ->
                                    val sel = selectionFor(start, change.position.x, change.position.y)
                                    if (sel != current) {
                                        current = sel
                                        latestChange.value(sel)
                                    }
                                    change.consume()
                                }
                            }
                            latestEnd.value(current)
                        }
                    }
                    .semantics {
                        contentDescription = "Tabuleiro de $n por $n letras"
                    },
            ) {
                repeat(n) { r ->
                    Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
                        repeat(n) { c ->
                            val index = r * n + c
                            BoardCell(
                                letter = puzzle.letters[index],
                                isSelected = index in selectedSet,
                                foundColor = foundCellColor[index],
                                isPopping = animationsEnabled && index in popCells,
                                showHint = hintCell == index && foundCellColor[index] == null,
                                highlighted = index in highlightCells,
                                colorblind = colorblind,
                                popProgress = popProgress,
                                animationsEnabled = animationsEnabled,
                                cellDp = cellDp,
                                radius = radius,
                                letterSp = letterSp,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BoardCell(
    letter: Char,
    isSelected: Boolean,
    foundColor: Int?,
    isPopping: Boolean,
    showHint: Boolean,
    highlighted: Boolean,
    colorblind: Boolean,
    popProgress: Float,
    animationsEnabled: Boolean,
    cellDp: Dp,
    radius: Dp,
    letterSp: androidx.compose.ui.unit.TextUnit,
) {
    val colors = LocalSopaColors.current
    val bg = when {
        foundColor != null -> colors.found[foundColor % colors.found.size]
        isSelected -> colors.accent
        else -> colors.cell
    }
    val fg = when {
        foundColor != null || isSelected -> colors.onAccent
        highlighted -> colors.accent
        else -> colors.text
    }
    val popScale = if (isPopping) 1f + 0.12f * sin(PI * popProgress).toFloat() else 1f
    val target = when {
        !animationsEnabled -> 1f
        isPopping -> popScale
        isSelected -> 0.92f
        else -> 1f
    }
    val scale by animateFloatAsState(
        targetValue = target,
        animationSpec = tween(durationMillis = 150),
        label = "cellScale",
    )
    val symbolSp = with(androidx.compose.ui.platform.LocalDensity.current) { (cellDp * 0.16f).toSp() }
    Box(
        modifier = Modifier
            .size(cellDp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(RoundedCornerShape(radius))
            .background(bg)
            .then(if (showHint || highlighted) Modifier.border(2.dp, colors.accent, RoundedCornerShape(radius)) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = letter.toString(),
            fontFamily = Bricolage,
            fontWeight = FontWeight.Bold,
            fontSize = letterSp,
            color = fg,
            style = TextStyle(
                platformStyle = PlatformTextStyle(includeFontPadding = false),
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.Both,
                ),
            ),
        )
        if (colorblind && foundColor != null) {
            Text(
                text = colorblindSymbol(foundColor),
                fontSize = symbolSp,
                color = colors.onAccent.copy(alpha = 0.55f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 1.dp, end = 3.dp),
            )
        }
    }
}
