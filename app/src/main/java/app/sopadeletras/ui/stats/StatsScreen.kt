package app.sopadeletras.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.sopadeletras.AppContainer
import app.sopadeletras.core.displayedStreak
import app.sopadeletras.ui.game.formatElapsed
import app.sopadeletras.ui.theme.Bricolage
import app.sopadeletras.ui.theme.Figtree
import app.sopadeletras.ui.theme.LocalSopaColors

@Composable
fun StatsScreen(
    container: AppContainer,
    language: String,
    onBack: () -> Unit,
) {
    val colors = LocalSopaColors.current
    val results by container.gameData.resultsFlow().collectAsState(initial = emptyList())
    val progress by container.gameData.progressFlow(language).collectAsState(initial = emptyMap())
    val daily by container.gameData.dailyFlow().collectAsState(initial = null)
    val today = java.time.LocalDate.now().toEpochDay()
    val streak = if (daily == null) 0 else displayedStreak(daily!!.lastDay, today, daily!!.streak)
    val todaySummary = if (daily?.resultDay == today && daily != null) {
        "%s · %s".format(
            formatElapsed(daily!!.resultSeconds),
            "★".repeat(daily!!.resultStars),
        )
    } else {
        "por jogar"
    }
    val rows = results.map {
        ResultRow(mode = it.mode, n = it.n, seconds = it.seconds, stars = it.stars, score = it.score, words = it.words)
    }
    val best = bestClassicTimes(rows)
    val (done, stars) = campaignTotals(progress)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 18.dp, bottom = 22.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(38.dp).clip(RoundedCornerShape(13.dp)).background(colors.surface).clickable { onBack() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = colors.text)
            }
            Text("Estatísticas", fontFamily = Bricolage, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colors.text, modifier = Modifier.padding(start = 12.dp))
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(number = "${wins(rows)}", label = "Partidas ganhas", modifier = Modifier.weight(1f))
                StatCard(number = "${wordsFound(rows)}", label = "Palavras encontradas", modifier = Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(number = "$streak", label = "Dias seguidos", modifier = Modifier.weight(1f))
                StatCard(number = "${mysteriesSolved(rows)}", label = "Palavras misteriosas", modifier = Modifier.weight(1f))
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            SectionTitle("MELHOR TEMPO · CLÁSSICO")
            for (size in listOf(6, 8, 10, 12)) {
                val seconds = best[size]
                StatLine(
                    label = "$size×$size",
                    value = if (seconds != null) formatElapsed(seconds) else "—",
                    valueAccent = seconds != null,
                )
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            SectionTitle("RECORDES")
            StatLine(label = "Campanha", value = "$done de 300 · ★ $stars", valueAccent = true)
            StatLine(label = "Contra-relógio", value = bestTimeAttack(rows)?.let { "$it pts" } ?: "—", valueAccent = bestTimeAttack(rows) != null)
            StatLine(label = "Desafio de hoje", value = todaySummary, valueAccent = todaySummary != "por jogar")
        }
    }
}

@Composable
private fun StatCard(number: String, label: String, modifier: Modifier = Modifier) {
    val colors = LocalSopaColors.current
    Column(
        modifier = modifier.clip(RoundedCornerShape(18.dp)).background(colors.surface).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(number, fontFamily = Bricolage, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = colors.accent, textAlign = TextAlign.Start)
        Text(label, fontFamily = Figtree, fontSize = 13.sp, color = colors.mute)
    }
}

@Composable
private fun SectionTitle(text: String) {
    val colors = LocalSopaColors.current
    Text(text, fontFamily = Figtree, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = colors.mute)
}

@Composable
private fun StatLine(label: String, value: String, valueAccent: Boolean) {
    val colors = LocalSopaColors.current
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontFamily = Figtree, fontSize = 14.sp, color = colors.text)
        Text(value, fontFamily = Figtree, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = if (valueAccent) colors.accent else colors.mute)
    }
}
