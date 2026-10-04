package app.sopadeletras.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class SopaColors(
    val bg: Color,
    val surface: Color,
    val cell: Color,
    val text: Color,
    val mute: Color,
    val accent: Color,
    val onAccent: Color,
    val found: List<Color>,
)

val TintaAmbar = SopaColors(
    bg = Color(0xFF0F1424),
    surface = Color(0xFF171E35),
    cell = Color(0xFF1B2440),
    text = Color(0xFFE8EAF4),
    mute = Color(0xFF8B93B5),
    accent = Color(0xFFF4B860),
    onAccent = Color(0xFF1A1304),
    found = listOf(
        Color(0xFFF4B860),
        Color(0xFF7CC6FE),
        Color(0xFFC3A6FF),
        Color(0xFF8EE3C2),
        Color(0xFFFF8FA3),
    ),
)

val TokyoNight = SopaColors(
    bg = Color(0xFF1A1B26),
    surface = Color(0xFF24283B),
    cell = Color(0xFF292E42),
    text = Color(0xFFC0CAF5),
    mute = Color(0xFF7982B4),
    accent = Color(0xFF7AA2F7),
    onAccent = Color(0xFF16161E),
    found = listOf(
        Color(0xFF7AA2F7),
        Color(0xFFBB9AF7),
        Color(0xFF9ECE6A),
        Color(0xFFFF9E64),
        Color(0xFFF7768E),
    ),
)
