package app.sopadeletras.ui.game

import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import app.sopadeletras.ui.components.AdaptiveGameScaffold
import app.sopadeletras.ui.components.HintButton
import app.sopadeletras.ui.components.LetterGrid
import app.sopadeletras.ui.components.SecondaryButton
import app.sopadeletras.ui.components.SopaProgress
import app.sopadeletras.ui.components.TimerChip
import app.sopadeletras.ui.components.WordChip
import app.sopadeletras.ui.theme.Figtree
import app.sopadeletras.ui.theme.LocalSopaColors
import app.sopadeletras.ui.theme.Bricolage
import app.sopadeletras.feedback.sopaConfirm
import app.sopadeletras.feedback.sopaReject
import app.sopadeletras.feedback.sopaTick
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GameScreen(
    viewModel: GameViewModel,
    title: String,
    subtitle: String,
    onExit: () -> Unit,
    onNext: () -> Unit,
    winTitle: String = "Nível concluído",
    winPrimaryLabel: String = "Próximo",
    winSecondaryLabel: String? = "Início",
    winExtraLine: String? = null,
    timeoutTitle: String = "Tempo esgotado",
    timeoutRetryLabel: String = "Tentar outra vez",
    timeoutExitLabel: String = "Mapa",
    tutorialTip: String? = null,
    onTutorialAction: () -> Unit = {},
    onWin: (stars: Int, seconds: Int) -> Unit = { _, _ -> },
    haptics: Boolean = true,
    letterScale: Float = 1f,
    colorblind: Boolean = false,
    sounds: app.sopadeletras.feedback.Sounds? = null,
    sound: Boolean = true,
) {
    val colors = LocalSopaColors.current
    val context = androidx.compose.ui.platform.LocalContext.current
    val view = androidx.compose.ui.platform.LocalView.current
    val animationsEnabled = remember {
        Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f,
        ) != 0f
    }
    val state by viewModel.state.collectAsState()
    val puzzle = state.puzzle
    val progress = if (puzzle.words.isEmpty()) 0f else state.found.size / puzzle.words.size.toFloat()
    val limit = viewModel.limit
    val remaining = limit?.let { remainingSeconds(it, state.elapsed) }

    var pop by remember { mutableStateOf(setOf<Int>()) }
    val popAnim = remember { Animatable(0f) }
    var showVictory by remember { mutableStateOf(false) }
    var winReported by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.victory) {
        if (state.victory && !winReported) {
            winReported = true
            onWin(viewModel.stars(), state.elapsed)
            view.sopaConfirm(context, haptics)
            sounds?.win(sound)
            delay(120)
            view.sopaConfirm(context, haptics)
            delay(380)
            showVictory = true
        } else if (!state.victory) {
            showVictory = false
            winReported = false
        }
    }
    var lastSelCount by remember { mutableIntStateOf(0) }
    LaunchedEffect(state.selected.size) {
        if (state.selected.size > lastSelCount) {
            view.sopaTick(context, haptics)
            sounds?.tick(sound)
        }
        lastSelCount = state.selected.size
    }
    LaunchedEffect(pop) {
        if (pop.isNotEmpty() && animationsEnabled) {
            popAnim.snapTo(0f)
            popAnim.animateTo(1f, tween(durationMillis = 350))
            pop = emptySet()
        }
    }

    BackHandler(enabled = !state.victory && !state.timedOut) {
        if (state.paused) viewModel.resume() else viewModel.pause()
    }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) viewModel.onAppBackground()
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }

    AdaptiveGameScaffold(
        header = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontFamily = Bricolage,
                        fontWeight = FontWeight.Bold,
                        fontSize = 21.sp,
                        color = colors.text,
                    )
                    Text(
                        text = subtitle,
                        fontFamily = Figtree,
                        fontSize = 14.sp,
                        color = colors.mute,
                    )
                }
                TimerChip(
                    seconds = remaining ?: state.elapsed,
                    urgent = remaining != null && remaining <= 10,
                )
            }
        },
        progress = {
            SopaProgress(progress = if (limit != null) state.elapsed / limit.toFloat() else progress)
        },
        board = {
            Column(verticalArrangement = Arrangement.spacedBy(15.dp)) {
                if (tutorialTip != null) {
                    Text(
                        text = tutorialTip,
                        fontFamily = Figtree,
                        fontSize = 14.sp,
                        color = colors.text,
                        modifier = Modifier
                            .background(colors.surface, RoundedCornerShape(18.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                    )
                }
                if (!state.paused) {
                    LetterGrid(
                        puzzle = puzzle,
                        selectedCells = state.selected,
                        foundColors = state.found,
                        popCells = pop,
                        popProgress = popAnim.value,
                        directionAssist = viewModel.assist,
                        animationsEnabled = animationsEnabled,
                        hintCell = state.hintCell,
                        letterScale = letterScale,
                        colorblind = colorblind,
                        onSelectionChange = viewModel::onSelectionChange,
                        onSelectionEnd = { sel ->
                            val hit = viewModel.confirmSelection(sel)
                            onTutorialAction()
                            if (hit != null) {
                                view.sopaConfirm(context, haptics)
                                sounds?.found(sound)
                                if (animationsEnabled) {
                                    scope.launch {
                                        pop = hit.cells.toSet()
                                    }
                                }
                            } else if (sel.size >= 2) {
                                view.sopaReject(context, haptics)
                            }
                        },
                    )
                }
            }
        },
        side = {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                for (word in puzzle.words) {
                    WordChip(word = word.word, colorIndex = state.found[word.word], colorblind = colorblind)
                }
            }
        },
        actions = {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HintButton(
                    hintsLeft = MAX_HINTS - state.hintsUsed,
                    onClick = {
                        viewModel.useHint()
                        onTutorialAction()
                    },
                    modifier = Modifier.weight(1f),
                )
                SecondaryButton(
                    text = "Pausa",
                    onClick = viewModel::pause,
                    modifier = Modifier.weight(1f),
                )
            }
        },
        overlays = {
            if (state.paused && !state.victory && !state.timedOut) {
                PauseOverlay(
                    subtitle = "$title · ${formatElapsed(state.elapsed)}",
                    onContinue = viewModel::resume,
                    onRestart = { viewModel.restart(kotlin.random.Random.nextLong()) },
                    onExit = onExit,
                )
            }
            if (state.timedOut && !state.victory) {
                WinOverlay(
                    title = timeoutTitle,
                    stars = 0,
                    summary = "Faltaram ${puzzle.words.size - state.found.size} palavras",
                    primaryLabel = timeoutRetryLabel,
                    onPrimary = { viewModel.restart(puzzle.seed) },
                    secondaryLabel = timeoutExitLabel,
                    onSecondary = onExit,
                )
            }
            AnimatedVisibility(
                visible = showVictory,
                enter = fadeIn(tween(250)),
            ) {
                WinOverlay(
                    title = winTitle,
                    stars = viewModel.stars(),
                    summary = victorySummary(puzzle.words.size, state.elapsed),
                    extraLine = winExtraLine,
                    primaryLabel = winPrimaryLabel,
                    onPrimary = onNext,
                    secondaryLabel = winSecondaryLabel,
                    onSecondary = onExit,
                )
            }
        },
    )
}
