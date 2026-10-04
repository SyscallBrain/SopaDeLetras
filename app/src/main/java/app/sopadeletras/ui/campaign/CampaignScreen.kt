package app.sopadeletras.ui.campaign

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.sopadeletras.AppContainer
import app.sopadeletras.core.LEVELS_PER_WORLD
import app.sopadeletras.core.WORLDS
import app.sopadeletras.core.isLevelUnlocked
import app.sopadeletras.core.isWorldUnlocked
import app.sopadeletras.core.levelInfo
import app.sopadeletras.data.CAMPAIGN_BANK_IDS
import app.sopadeletras.ui.components.SopaProgress
import app.sopadeletras.ui.theme.Bricolage
import app.sopadeletras.ui.theme.Figtree
import app.sopadeletras.ui.theme.LocalSopaColors
import kotlinx.coroutines.launch

@Composable
fun CampaignScreen(
    container: AppContainer,
    language: String,
    onBack: () -> Unit,
    onPlayLevel: (Int) -> Unit,
) {
    val colors = LocalSopaColors.current
    val progress by container.gameData.progressFlow(language).collectAsState(initial = emptyMap())
    val completed = progress.keys
    val current = (1..300).firstOrNull { it !in completed } ?: 300
    var openWorld by remember(current) { mutableIntStateOf((current - 1) / LEVELS_PER_WORLD) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun lockedMessage(world: Int): String? {
        if (!isWorldUnlocked(world, completed)) return "Completa o mundo anterior"
        return null
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg)
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp).padding(top = 18.dp, bottom = 22.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(38.dp).clip(RoundedCornerShape(13.dp)).background(colors.surface).clickable { onBack() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = colors.text)
                }
                Text("Campanha", fontFamily = Bricolage, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colors.text, modifier = Modifier.padding(start = 12.dp))
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items((0..9).toList()) { world ->
                    val locked = lockedMessage(world) != null
                    val open = world == openWorld
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(if (open) colors.accent else colors.surface)
                            .clickable {
                                val message = lockedMessage(world)
                                if (message != null) scope.launch { snackbar.showSnackbar(message) }
                                else openWorld = world
                            },
                    ) {
                        if (locked) {
                            Icon(Icons.Filled.Lock, contentDescription = null, tint = colors.mute, modifier = Modifier.size(18.dp))
                        } else {
                            Text("${world + 1}", fontFamily = Bricolage, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = if (open) colors.onAccent else colors.text)
                        }
                    }
                }
            }
            WorldCard(world = openWorld, progress = progress)
            LazyVerticalGrid(
                columns = GridCells.Fixed(6),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f),
            ) {
                items((1..30).toList()) { num ->
                    val level = openWorld * LEVELS_PER_WORLD + num
                    LevelNode(
                        level = level,
                        world = openWorld,
                        stars = progress[level] ?: 0,
                        isCurrent = level == current,
                        locked = !isLevelUnlocked(level, completed),
                        onClick = {
                            if (!isLevelUnlocked(level, completed)) {
                                scope.launch { snackbar.showSnackbar(lockedMessage(openWorld) ?: "Completa o nível anterior") }
                            } else {
                                onPlayLevel(level)
                            }
                        },
                    )
                }
            }
            Text(
                "Nível 10 cronometrado · 20 palavra misteriosa · 30 chefe",
                fontFamily = Figtree,
                fontSize = 13.sp,
                color = colors.mute,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        SnackbarHost(hostState = snackbar, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 22.dp))
    }
}

@Composable
private fun WorldCard(world: Int, progress: Map<Int, Int>) {
    val colors = LocalSopaColors.current
    val def = WORLDS[world]
    val range = (world * LEVELS_PER_WORLD + 1)..(world * LEVELS_PER_WORLD + 30)
    val done = range.count { it in progress }
    val stars = range.sumOf { progress[it] ?: 0 }
    val bankName = CAMPAIGN_BANK_IDS.getOrNull(def.category)?.uppercase() ?: "EM BREVE"
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(colors.surface).padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text("MUNDO ${world + 1} · $bankName", fontFamily = Figtree, fontSize = 12.sp, color = colors.mute)
        Text(def.name, fontFamily = Bricolage, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colors.text)
        Text("${def.n}×${def.n} · ${def.minWords} a ${def.maxWords} palavras · ${def.dirsCount} direções", fontFamily = Figtree, fontSize = 13.sp, color = colors.mute)
        SopaProgress(progress = done / 30f)
        Text("$done de 30 · ★ $stars de 90", fontFamily = Figtree, fontSize = 13.sp, color = colors.mute)
    }
}

@Composable
private fun LevelNode(
    level: Int,
    world: Int,
    stars: Int,
    isCurrent: Boolean,
    locked: Boolean,
    onClick: () -> Unit,
) {
    val colors = LocalSopaColors.current
    val num = (level - 1) % LEVELS_PER_WORLD + 1
    val bg = when {
        locked -> colors.cell
        stars > 0 -> colors.found[world % colors.found.size]
        isCurrent -> colors.accent
        else -> colors.cell
    }
    val fg = if (locked) colors.mute else colors.onAccent
    val special = when (num) {
        10 -> "⏱"
        20 -> "?"
        30 -> "♛"
        else -> null
    }
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .alpha(if (locked) 0.7f else 1f)
            .then(if (isCurrent && !locked && stars == 0) Modifier.border(3.dp, colors.accent.copy(alpha = 0.35f), RoundedCornerShape(16.dp)) else Modifier)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${(level - 1) % LEVELS_PER_WORLD + 1}", fontFamily = Bricolage, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = if (locked) colors.mute else if (stars > 0 || isCurrent) fg else colors.text)
            if (stars > 0) {
                Text("★".repeat(stars), fontSize = 9.sp, color = fg)
            }
        }
        if (special != null && !locked) {
            Text(special, fontSize = 11.sp, color = fg, modifier = Modifier.align(Alignment.TopEnd).padding(top = 1.dp, end = 3.dp))
        }
    }
}
