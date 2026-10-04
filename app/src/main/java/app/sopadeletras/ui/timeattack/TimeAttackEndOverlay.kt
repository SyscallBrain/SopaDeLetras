package app.sopadeletras.ui.timeattack

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
fun TimeAttackEndOverlay(
    points: Int,
    isRecord: Boolean,
    record: Int,
    boards: Int,
    boardSize: Int,
    categoryName: String,
    onReplay: () -> Unit,
    onHome: () -> Unit,
    modifier: Modifier = Modifier,
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
            Text("Tempo esgotado", fontFamily = Bricolage, fontWeight = FontWeight.Bold, fontSize = 32.sp, color = colors.text)
            Text("$points", fontFamily = Bricolage, fontWeight = FontWeight.ExtraBold, fontSize = 48.sp, color = colors.accent)
            Text("pontos", fontFamily = Figtree, fontSize = 14.sp, color = colors.mute)
            if (isRecord && points > 0) {
                Text(
                    "Novo recorde",
                    fontFamily = Figtree,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = colors.onAccent,
                    modifier = Modifier
                        .background(colors.accent, androidx.compose.foundation.shape.CircleShape)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                )
            } else {
                Text("Recorde: $record", fontFamily = Figtree, fontSize = 14.sp, color = colors.mute)
            }
            Text(
                "$boards tabuleiros completos · $categoryName $boardSize×$boardSize",
                fontFamily = Figtree, fontSize = 14.sp, color = colors.mute,
            )
            PrimaryButton(text = "Jogar outra vez", onClick = onReplay)
            SecondaryButton(text = "Início", onClick = onHome)
        }
    }
}
