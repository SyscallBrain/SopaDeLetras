package app.sopadeletras.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.sopadeletras.ui.components.PrimaryButton
import app.sopadeletras.ui.components.SecondaryButton
import app.sopadeletras.ui.theme.Bricolage
import app.sopadeletras.ui.theme.Figtree
import app.sopadeletras.ui.theme.LocalSopaColors

@Composable
fun StarsRow(
    stars: Int,
    modifier: Modifier = Modifier,
) {
    val colors = LocalSopaColors.current
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = modifier) {
        repeat(3) { index ->
            Text(
                text = "★",
                fontSize = 30.sp,
                color = if (index < stars) colors.accent else colors.cell,
            )
        }
    }
}

@Composable
fun WinOverlay(
    title: String,
    stars: Int,
    summary: String,
    primaryLabel: String,
    onPrimary: () -> Unit,
    secondaryLabel: String?,
    onSecondary: () -> Unit,
    modifier: Modifier = Modifier,
    extraLine: String? = null,
) {
    val colors = LocalSopaColors.current
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bg.copy(alpha = 0.88f))
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = title,
                fontFamily = Bricolage,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = colors.text,
            )
            StarsRow(stars = stars)
            Text(
                text = summary,
                fontFamily = Figtree,
                fontSize = 14.sp,
                color = colors.mute,
            )
            if (extraLine != null) {
                Text(
                    text = extraLine,
                    fontFamily = Figtree,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = colors.accent,
                )
            }
            PrimaryButton(text = primaryLabel, onClick = onPrimary)
            if (secondaryLabel != null) {
                SecondaryButton(text = secondaryLabel, onClick = onSecondary)
            }
        }
    }
}
