package app.sopadeletras.ui.components

private val COLORBLIND_SYMBOLS = listOf("•", "○", "/", "◆", "✕")

fun colorblindSymbol(colorIndex: Int): String =
    COLORBLIND_SYMBOLS[colorIndex % COLORBLIND_SYMBOLS.size]
