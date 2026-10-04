package app.sopadeletras.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.sopadeletras.ui.theme.Figtree
import app.sopadeletras.ui.theme.LocalSopaColors

@Composable
fun WordChip(
    word: String,
    colorIndex: Int?,
    modifier: Modifier = Modifier,
    colorblind: Boolean = false,
) {
    val colors = LocalSopaColors.current
    val bg = if (colorIndex != null) colors.found[colorIndex % colors.found.size] else colors.surface
    val fg = if (colorIndex != null) colors.onAccent else colors.text
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(CircleShape)
            .background(bg)
            .padding(horizontal = 11.dp, vertical = 5.dp)
            .semantics {
                contentDescription = "$word, " + if (colorIndex != null) "encontrada" else "por encontrar"
            },
    ) {
        if (colorblind && colorIndex != null) {
            Text(
                text = colorblindSymbol(colorIndex),
                fontSize = 13.sp,
                color = fg.copy(alpha = 0.55f),
            )
            Spacer(Modifier.width(4.dp))
        }
        Text(
            text = word,
            fontFamily = Figtree,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.5.sp,
            letterSpacing = 1.sp,
            color = fg,
            textDecoration = if (colorIndex != null) TextDecoration.LineThrough else null,
        )
    }
}
