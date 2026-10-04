package app.sopadeletras.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.sopadeletras.ui.game.formatElapsed
import app.sopadeletras.ui.theme.Figtree
import app.sopadeletras.ui.theme.LocalSopaColors

@Composable
fun TimerChip(
    seconds: Int,
    modifier: Modifier = Modifier,
    urgent: Boolean = false,
) {
    val colors = LocalSopaColors.current
    val blink by rememberInfiniteTransition(label = "timerBlink").animateFloat(
        initialValue = 1f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse),
        label = "blink",
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(CircleShape)
            .background(colors.surface)
            .padding(horizontal = 15.dp, vertical = 8.dp)
            .alpha(if (urgent) blink else 1f)
            .semantics { contentDescription = "Tempo: ${formatElapsed(seconds)}" },
    ) {
        ClockIcon(modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text(
            text = formatElapsed(seconds),
            fontFamily = Figtree,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = if (urgent) colors.found[4] else colors.accent,
            style = TextStyle(fontFeatureSettings = "tnum"),
        )
    }
}

@Composable
private fun ClockIcon(modifier: Modifier = Modifier) {
    val colors = LocalSopaColors.current
    Canvas(modifier = modifier) {
        val radius = size.minDimension / 2f
        drawCircle(color = colors.mute, radius = radius, style = androidx.compose.ui.graphics.drawscope.Stroke(width = radius * 0.16f))
        val center = Offset(size.width / 2f, size.height / 2f)
        drawLine(
            color = colors.mute,
            start = center,
            end = center + Offset(0f, -radius * 0.5f),
            strokeWidth = radius * 0.16f,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = colors.mute,
            start = center,
            end = center + Offset(radius * 0.38f, 0f),
            strokeWidth = radius * 0.16f,
            cap = StrokeCap.Round,
        )
    }
}
