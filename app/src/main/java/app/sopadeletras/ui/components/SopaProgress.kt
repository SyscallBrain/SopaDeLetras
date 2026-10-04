package app.sopadeletras.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.sopadeletras.ui.theme.LocalSopaColors

@Composable
fun SopaProgress(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val colors = LocalSopaColors.current
    val animated by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 400),
        label = "progress",
    )
    LinearProgressIndicator(
        progress = { animated },
        modifier = modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
        color = colors.accent,
        trackColor = colors.surface,
    )
}

@Composable
fun HintButton(
    hintsLeft: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val label = if (hintsLeft > 0) "Pista · $hintsLeft" else "Pista"
    SecondaryButton(
        text = label,
        onClick = onClick,
        enabled = hintsLeft > 0,
        modifier = modifier.alpha(if (hintsLeft > 0) 1f else 0.4f),
    )
}
