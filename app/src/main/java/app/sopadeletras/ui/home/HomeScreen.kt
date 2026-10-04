package app.sopadeletras.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.sopadeletras.AppContainer
import app.sopadeletras.core.WORLDS
import app.sopadeletras.core.dailyCategoryIndex
import app.sopadeletras.core.displayedStreak
import app.sopadeletras.core.levelInfo
import app.sopadeletras.data.SavedGameData
import app.sopadeletras.ui.components.SopaProgress
import app.sopadeletras.ui.theme.Bricolage
import app.sopadeletras.ui.theme.Figtree
import app.sopadeletras.ui.theme.LocalSopaColors
import java.time.LocalDate

private val PT_MONTHS = listOf("JAN", "FEV", "MAR", "ABR", "MAI", "JUN", "JUL", "AGO", "SET", "OUT", "NOV", "DEZ")

@Composable
fun HomeScreen(
    container: AppContainer,
    language: String,
    onCampaign: () -> Unit,
    onResume: (Int) -> Unit,
    onFreeGame: () -> Unit,
    onTimeGame: () -> Unit,
    onMysteryGame: () -> Unit,
    onDaily: () -> Unit,
    onStats: () -> Unit,
    onSettings: () -> Unit,
) {
    val colors = LocalSopaColors.current
    val progress by container.gameData.progressFlow(language).collectAsState(initial = emptyMap())
    val saved by container.gameData.savedGameFlow().collectAsState(initial = null)
    val daily by container.gameData.dailyFlow().collectAsState(initial = null)

    val done = progress.size
    val starsTotal = progress.values.sum()
    val current = (1..300).firstOrNull { it !in progress } ?: 300
    val info = levelInfo(current)
    val world = WORLDS[info.world]
    val todayDate = LocalDate.now()
    val today = todayDate.toEpochDay()
    val dailyDone = daily?.resultDay == today
    val streakShown = if (daily == null) 0 else displayedStreak(daily!!.lastDay, today, daily!!.streak)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 18.dp, bottom = 22.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Sopa de Letras", fontFamily = Bricolage, fontWeight = FontWeight.ExtraBold, fontSize = 30.sp, color = colors.text)
        Text("Encontra palavras entre as estrelas.", fontFamily = Figtree, fontSize = 13.sp, color = colors.mute)

        CampaignCard(
            done = done,
            starsTotal = starsTotal,
            worldNumber = info.world + 1,
            worldName = world.name,
            line = resumeLine(saved, current),
            buttonLabel = if (saved != null) "Retomar" else "Jogar nível $current",
            onCard = onCampaign,
            onButton = { if (saved != null) onResume(saved!!.level) else onCampaign() },
        )
        DailyCard(
            today = todayDate,
            categoryName = dailyCategoryName(todayDate),
            streak = streakShown,
            done = dailyDone,
            summary = dailySummary(daily, today),
            onPlay = onDaily,
        )
        ModeRow(title = "Jogo livre", desc = "Tamanho, categoria e dificuldade à escolha.", icon = Icons.Filled.PlayArrow, enabled = true, onClick = onFreeGame)
        ModeRow(title = "Contra-relógio", desc = "Completa tabuleiros contra o tempo.", icon = null, glyph = "⏱", enabled = true, onClick = onTimeGame)
        ModeRow(title = "Palavra misteriosa", desc = "Descobre a palavra final escondida.", icon = null, glyph = "?", enabled = true, onClick = onMysteryGame)
        Spacer(Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SecondaryFooterButton(text = "Estatísticas", onClick = onStats, modifier = Modifier.weight(1f))
            SecondaryFooterButton(text = "Definições", onClick = onSettings, modifier = Modifier.weight(1f))
        }
    }
}

private fun resumeLine(saved: SavedGameData?, current: Int): String {
    if (saved == null) {
        val info = levelInfo(current)
        return "Nível $current · ${info.n}×${info.n}"
    }
    val info = levelInfo(saved.level)
    return "Retomar nível ${saved.level} · ${formatSavedTime(saved.elapsed)} · ${saved.foundWords.size} de ${info.words} palavras"
}

private fun formatSavedTime(seconds: Int): String =
    "%02d:%02d".format(seconds / 60, seconds % 60)

@Composable
private fun CampaignCard(
    done: Int,
    starsTotal: Int,
    worldNumber: Int,
    worldName: String,
    line: String,
    buttonLabel: String,
    onCard: () -> Unit,
    onButton: () -> Unit,
) {
    val colors = LocalSopaColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(colors.surface)
            .clickable { onCard() }
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("CAMPANHA · $done DE 300 NÍVEIS", fontFamily = Figtree, fontSize = 12.sp, color = colors.mute)
        Text("Mundo $worldNumber · $worldName", fontFamily = Bricolage, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = colors.text)
        Text(line, fontFamily = Figtree, fontSize = 13.sp, color = colors.mute)
        SopaProgress(progress = done / 300f)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(colors.accent)
                .clickable { onButton() }
                .padding(horizontal = 16.dp, vertical = 10.dp),
        ) {
            Text(buttonLabel, fontFamily = Figtree, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = colors.onAccent)
        }
    }
}

@Composable
private fun DailyCard(
    today: LocalDate,
    categoryName: String,
    streak: Int,
    done: Boolean,
    summary: String?,
    onPlay: () -> Unit,
) {
    val colors = LocalSopaColors.current
    val label = "${today.dayOfMonth} ${PT_MONTHS[today.monthValue - 1]}"
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(colors.surface)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("DESAFIO DIÁRIO · $label", fontFamily = Figtree, fontSize = 12.sp, color = colors.mute)
        Text("$categoryName · 10×10", fontFamily = Bricolage, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = colors.text)
        if (streak > 0) {
            Text("🔥 $streak dias seguidos", fontFamily = Figtree, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = colors.accent)
        }
        if (done && summary != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(colors.cell)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
            ) {
                Text("Concluído · $summary", fontFamily = Figtree, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = colors.mute)
            }
        } else {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(colors.accent)
                    .clickable { onPlay() }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
            ) {
                Text("Jogar", fontFamily = Figtree, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = colors.onAccent)
            }
        }
    }
}

private fun dailyCategoryName(today: LocalDate): String {
    val names = listOf("Espaço", "Oceano", "Comida", "Animais", "Países")
    return names[dailyCategoryIndex(today.year, today.monthValue, today.dayOfMonth)]
}

private fun dailySummary(daily: app.sopadeletras.data.DailyData?, today: Long): String? {
    if (daily?.resultDay != today) return null
    return "%02d:%02d · %s".format(
        daily.resultSeconds / 60,
        daily.resultSeconds % 60,
        "★".repeat(daily.resultStars),
    )
}

@Composable
private fun ModeRow(
    title: String,
    desc: String,
    icon: ImageVector?,
    glyph: String = "",
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val colors = LocalSopaColors.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colors.surface)
            .alpha(if (enabled) 1f else 0.6f)
            .then(if (enabled) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier.size(38.dp).clip(RoundedCornerShape(13.dp)).background(colors.cell),
            contentAlignment = Alignment.Center,
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = colors.accent)
            } else {
                Text(glyph, fontFamily = Bricolage, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colors.accent)
            }
        }
        Column(Modifier.weight(1f)) {
            Text(title, fontFamily = Bricolage, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = colors.text)
            Text(desc, fontFamily = Figtree, fontSize = 12.sp, color = colors.mute)
        }
        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = colors.mute)
    }
}

@Composable
private fun SecondaryFooterButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = LocalSopaColors.current
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(colors.surface)
            .alpha(if (enabled) 1f else 0.5f)
            .then(if (enabled) Modifier.clickable { onClick() } else Modifier),
    ) {
        Text(text, fontFamily = Figtree, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = colors.text)
    }
}
