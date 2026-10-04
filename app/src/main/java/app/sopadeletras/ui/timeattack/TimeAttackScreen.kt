package app.sopadeletras.ui.timeattack

import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import app.sopadeletras.ui.components.AdaptiveGameScaffold
import app.sopadeletras.ui.components.LetterGrid
import app.sopadeletras.ui.components.SecondaryButton
import app.sopadeletras.ui.components.SopaProgress
import app.sopadeletras.ui.components.TimerChip
import app.sopadeletras.ui.components.WordChip
import app.sopadeletras.ui.game.PauseOverlay
import app.sopadeletras.ui.game.formatElapsed
import app.sopadeletras.feedback.sopaConfirm
import app.sopadeletras.feedback.sopaReject
import app.sopadeletras.feedback.sopaTick
import app.sopadeletras.ui.theme.Bricolage
import app.sopadeletras.ui.theme.Figtree
import app.sopadeletras.ui.theme.LocalSopaColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TimeAttackScreen(
    viewModel: TimeAttackViewModel,
    boardSize: Int,
    categoryName: String,
    duration: Int,
    record: Int,
    onExit: () -> Unit,
    onReplay: () -> Unit,
    onFinish: () -> Unit = {},
    haptics: Boolean = true,
    letterScale: Float = 1f,
    colorblind: Boolean = false,
    sounds: app.sopadeletras.feedback.Sounds? = null,
    sound: Boolean = true,
) {
    val colors = LocalSopaColors.current
    val context = LocalContext.current
    val view = LocalView.current
    val animationsEnabled = remember {
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) != 0f
    }
    val state by viewModel.state.collectAsState()
    val puzzle = state.puzzle
    val progress = if (state.total == 0) 0f else state.remaining / state.total.toFloat()

    var pop by remember { mutableStateOf(setOf<Int>()) }
    val popAnim = remember { Animatable(0f) }
    var finishReported by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.finished) {
        if (state.finished && !finishReported) {
            finishReported = true
            view.sopaConfirm(context, haptics)
            sounds?.win(sound)
            onFinish()
        } else if (!state.finished) {
            finishReported = false
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

    BackHandler(enabled = !state.finished) {
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
                    Text("Contra-relógio", fontFamily = Bricolage, fontWeight = FontWeight.Bold, fontSize = 21.sp, color = colors.text)
                    Text(
                        buildAnnotatedString {
                            append("Pontos ")
                            withStyle(SpanStyle(color = colors.accent)) { append("${state.points}") }
                            append(" · Tabuleiro ${state.boards + 1}")
                        },
                        fontFamily = Figtree, fontSize = 14.sp, color = colors.mute,
                    )
                }
                TimerChip(seconds = state.remaining, urgent = state.remaining <= 10)
            }
        },
        progress = { SopaProgress(progress = progress) },
        board = {
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
                        if (hit != null) {
                            view.sopaConfirm(context, haptics)
                            sounds?.found(sound)
                            if (animationsEnabled) {
                                scope.launch { pop = hit.cells.toSet() }
                            }
                        } else if (sel.size >= 2) {
                            view.sopaReject(context, haptics)
                        }
                    },
                )
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
                SecondaryButton(
                    text = "Pista −5 s",
                    onClick = viewModel::useHint,
                    modifier = Modifier.weight(1f),
                )
                SecondaryButton(text = "Pausa", onClick = viewModel::pause, modifier = Modifier.weight(1f))
            }
        },
        overlays = {
        if (state.bonusVisible) {
            Box(Modifier.align(Alignment.TopCenter).padding(top = 90.dp)) {
                Text(
                    "+10 s · +50 pts",
                    fontFamily = Figtree,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = colors.onAccent,
                    modifier = Modifier.clip(CircleShape).background(colors.accent).padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
        }
        if (state.paused && !state.finished) {
            PauseOverlay(
                subtitle = "$categoryName · restam ${formatElapsed(state.remaining)}",
                onContinue = viewModel::resume,
                onRestart = { viewModel.restart(duration, kotlin.random.Random.nextLong()) },
                onExit = onExit,
            )
        }
        if (state.finished) {
            TimeAttackEndOverlay(
                points = state.points,
                isRecord = viewModel.isRecord(record),
                record = maxOf(record, state.points),
                boards = state.boards,
                boardSize = boardSize,
                categoryName = categoryName,
                onReplay = onReplay,
                onHome = onExit,
            )
        }
        },
    )
}
