package app.sopadeletras.ui.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.sopadeletras.AppContainer
import app.sopadeletras.core.BoardConfig
import app.sopadeletras.core.Difficulty
import app.sopadeletras.core.GenParams
import app.sopadeletras.core.diffParams
import app.sopadeletras.data.WordCategoryData
import app.sopadeletras.ui.components.PrimaryButton
import app.sopadeletras.ui.components.SegmentedPicker
import app.sopadeletras.ui.theme.Bricolage
import app.sopadeletras.ui.theme.Figtree
import app.sopadeletras.ui.theme.LocalSopaColors
import kotlinx.coroutines.launch

private val DIFFICULTY_NOTES = mapOf(
    Difficulty.EASY to "Menos palavras, só para a direita e para baixo.",
    Difficulty.NORMAL to "O equilíbrio habitual para o tamanho.",
    Difficulty.HARD to "Mais palavras, todas as direções e letras enganadoras.",
)

enum class SetupMode {
    FREE,
    TIME,
    MYSTERY,
}

private val DURATIONS = listOf(120, 180, 300)

@Composable
fun NewGameScreen(
    container: AppContainer,
    categories: List<WordCategoryData>,
    mode: SetupMode,
    onBack: () -> Unit,
    onStartFree: (size: Int, categoryId: String, difficulty: Difficulty) -> Unit = { _, _, _ -> },
    onStartTime: (size: Int, categoryId: String, duration: Int) -> Unit = { _, _, _ -> },
    onStartMystery: (size: Int, categoryId: String) -> Unit = { _, _ -> },
) {
    val colors = LocalSopaColors.current
    val scope = rememberCoroutineScope()
    val savedSize by container.settings.boardSize.collectAsState(initial = 8)
    val savedCategory by container.settings.categoryId.collectAsState(initial = "")
    val savedDifficulty by container.settings.difficulty.collectAsState(initial = Difficulty.NORMAL)
    val savedDuration by container.settings.timeAttackDuration.collectAsState(initial = 180)

    val sizes = if (mode == SetupMode.MYSTERY) listOf(6, 8, 10) else listOf(6, 8, 10, 12)
    var sizeIndex by remember(savedSize, mode) {
        mutableIntStateOf(sizes.indexOf(savedSize).takeIf { it >= 0 } ?: 1)
    }
    var difficultyIndex by remember(savedDifficulty) { mutableIntStateOf(Difficulty.entries.indexOf(savedDifficulty)) }
    var durationIndex by remember(savedDuration) { mutableIntStateOf(DURATIONS.indexOf(savedDuration).takeIf { it >= 0 } ?: 1) }
    var category by remember(savedCategory, categories) {
        mutableStateOf(savedCategory.takeIf { id -> categories.any { it.id == id } } ?: categories.firstOrNull()?.id.orEmpty())
    }

    val size = sizes[sizeIndex]
    val difficulty = Difficulty.entries[difficultyIndex]
    val duration = DURATIONS[durationIndex]
    val params = diffParams(size, difficulty)
    val title = when (mode) {
        SetupMode.FREE -> "Jogo livre"
        SetupMode.TIME -> "Contra-relógio"
        SetupMode.MYSTERY -> "Palavra misteriosa"
    }

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
            Text(title, fontFamily = Bricolage, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colors.text, modifier = Modifier.padding(start = 12.dp))
        }
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            SectionLabel("TAMANHO")
            SegmentedPicker(options = sizes.map { "$it×$it" }, selected = sizeIndex, onSelect = { sizeIndex = it })
            Text(
                sizeDescription(mode, size, params),
                fontFamily = Figtree, fontSize = 13.sp, color = colors.mute,
            )
        }
        if (mode == SetupMode.FREE) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                SectionLabel("DIFICULDADE")
                SegmentedPicker(options = listOf("Fácil", "Normal", "Difícil"), selected = difficultyIndex, onSelect = { difficultyIndex = it })
                Text(DIFFICULTY_NOTES[difficulty].orEmpty(), fontFamily = Figtree, fontSize = 13.sp, color = colors.mute)
            }
        }
        if (mode == SetupMode.TIME) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                SectionLabel("DURAÇÃO")
                SegmentedPicker(options = listOf("02:00", "03:00", "05:00"), selected = durationIndex, onSelect = { durationIndex = it })
                Text("Cada tabuleiro completo dá +50 pontos e +10 s.", fontFamily = Figtree, fontSize = 13.sp, color = colors.mute)
            }
        }
        if (mode == SetupMode.MYSTERY) {
            Text(
                "As letras que sobram, lidas por ordem, formam a palavra final.",
                fontFamily = Figtree, fontSize = 13.sp, color = colors.mute,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            SectionLabel("CATEGORIA")
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                for (row in categories.chunked(2)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        for (cat in row) {
                            CategoryCard(
                                cat = cat,
                                colorIndex = categories.indexOf(cat),
                                selected = cat.id == category,
                                onClick = { category = cat.id },
                                modifier = Modifier.weight(1f),
                            )
                        }
                        if (row.size == 1) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }
        Spacer(Modifier.weight(1f))
        PrimaryButton(
            text = "Começar",
            onClick = {
                scope.launch {
                    container.settings.setBoardSize(size)
                    container.settings.setCategoryId(category)
                    if (mode == SetupMode.FREE) container.settings.setDifficulty(difficulty)
                    if (mode == SetupMode.TIME) container.settings.setTimeAttackDuration(duration)
                }
                when (mode) {
                    SetupMode.FREE -> onStartFree(size, category, difficulty)
                    SetupMode.TIME -> onStartTime(size, category, duration)
                    SetupMode.MYSTERY -> onStartMystery(size, category)
                }
            },
        )
    }
}

private fun sizeDescription(mode: SetupMode, size: Int, params: GenParams): String {
    if (mode == SetupMode.MYSTERY) {
        val dirs = BoardConfig.normalDirs(size).size
        return "$size×$size · $dirs direções · palavras escondidas"
    }
    return "$size×$size · ${params.words} palavras · ${params.dirs.size} direções"
}

@Composable
private fun CategoryCard(
    cat: WordCategoryData,
    colorIndex: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalSopaColors.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(colors.surface)
            .then(if (selected) Modifier.border(2.dp, colors.accent, RoundedCornerShape(18.dp)) else Modifier)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(Modifier.size(18.dp).clip(RoundedCornerShape(5.dp)).background(colors.found[colorIndex % colors.found.size]))
        Column {
            Text(cat.name, fontFamily = Bricolage, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = colors.text)
            Text("${cat.words.size} palavras", fontFamily = Figtree, fontSize = 12.sp, color = colors.mute)
        }
    }
}

@Composable
private fun SectionLabel(text: String) {    val colors = LocalSopaColors.current
    Text(text, fontFamily = Figtree, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = colors.mute)
}
