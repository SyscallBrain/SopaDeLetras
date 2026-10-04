package app.sopadeletras.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

@Composable
fun SopaTheme(
    colors: SopaColors = TintaAmbar,
    content: @Composable () -> Unit,
) {
    val scheme = darkColorScheme(
        background = colors.bg,
        surface = colors.surface,
        onBackground = colors.text,
        onSurface = colors.text,
        primary = colors.accent,
        onPrimary = colors.onAccent,
        secondary = colors.mute,
    )
    CompositionLocalProvider(LocalSopaColors provides colors) {
        MaterialTheme(
            colorScheme = scheme,
            content = content,
        )
    }
}
