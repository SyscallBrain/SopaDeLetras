package app.sopadeletras.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import app.sopadeletras.R

val Bricolage = FontFamily(Font(R.font.bricolage_grotesque, FontWeight.Bold))
val Figtree = FontFamily(Font(R.font.figtree, FontWeight.Normal))

val LocalSopaColors = staticCompositionLocalOf { TintaAmbar }
