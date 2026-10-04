package app.sopadeletras.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
fun PauseOverlay(
    subtitle: String,
    onContinue: () -> Unit,
    onRestart: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalSopaColors.current
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bg.copy(alpha = 0.9f))
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "Em pausa",
                fontFamily = Bricolage,
                fontWeight = FontWeight.Bold,
                fontSize = 31.sp,
                color = colors.text,
            )
            Text(
                text = subtitle,
                fontFamily = Figtree,
                fontSize = 14.sp,
                color = colors.mute,
            )
            PrimaryButton(text = "Continuar", onClick = onContinue)
            SecondaryButton(text = "Reiniciar", onClick = onRestart)
            SecondaryButton(text = "Sair", onClick = onExit)
        }
    }
}
