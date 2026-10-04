package app.sopadeletras.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.sopadeletras.ui.theme.Figtree
import app.sopadeletras.ui.theme.LocalSopaColors

@Composable
fun SegmentedPicker(
    options: List<String>,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalSopaColors.current
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(colors.surface)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        options.forEachIndexed { index, label ->
            val active = index == selected
            Text(
                text = label,
                fontFamily = Figtree,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = if (active) colors.onAccent else colors.mute,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .clip(CircleShape)
                    .background(if (active) colors.accent else androidx.compose.ui.graphics.Color.Transparent)
                    .clickable { onSelect(index) }
                    .padding(vertical = 10.dp),
            )
        }
    }
}
