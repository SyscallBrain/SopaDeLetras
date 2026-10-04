package app.sopadeletras.core

object Language {
    const val PT_CODE = "pt-PT"
    const val EN_CODE = "en-US"
    const val PT_FILL = "AAAEEEIIOOOSSRRMNDTLCPUBG"
    const val EN_FILL = "EEEEAAARRIIOOTTNNSSLLCUDPMHGBF"

    fun fillFor(code: String): String = if (code == EN_CODE) EN_FILL else PT_FILL
}
