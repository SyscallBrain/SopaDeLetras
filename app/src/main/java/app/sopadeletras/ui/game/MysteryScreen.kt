package app.sopadeletras.ui.game

import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import app.sopadeletras.ui.components.AdaptiveGameScaffold
import app.sopadeletras.ui.components.HintButton
import app.sopadeletras.ui.components.LetterGrid
import app.sopadeletras.ui.components.PrimaryButton
import app.sopadeletras.ui.components.SecondaryButton
import app.sopadeletras.ui.components.SopaProgress
import app.sopadeletras.ui.components.TimerChip
import app.sopadeletras.ui.theme.Bricolage
import app.sopadeletras.ui.theme.Figtree
import app.sopadeletras.ui.theme.LocalSopaColors
import app.sopadeletras.feedback.sopaConfirm
import app.sopadeletras.feedback.sopaReject
import app.sopadeletras.feedback.sopaTick
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MysteryScreen(
    viewModel: MysteryViewModel,
    title: String,
    subtitle: String,
    onExit: () -> Unit,
    onNext: () -> Unit,
    onWin: (stars: Int, seconds: Int) -> Unit = { _, _ -> },
    winPrimaryLabel: String = "Próximo",
    winSecondaryLabel: String = "Mapa",
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
        Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f,
        ) != 0f
    }
    val state by viewModel.state.collectAsState()
    val puzzle = state.puzzle
    val mystery = puzzle.mystery
    val progress = if (puzzle.words.isEmpty()) 0f else state.found.size / puzzle.words.size.toFloat()

    var pop by remember { mutableStateOf(setOf<Int>()) }
    val popAnim = remember { Animatable(0f) }
    var showVictory by remember { mutableStateOf(false) }
    var winReported by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.revealed, state.solvedByGuess) {
        if (state.revealed && !state.victory && !winReported) {
            winReported = true
            // Guess before reveal: 500 ms; revealed by finding all: 1.5 s.
            delay(if (state.solvedByGuess) 500 else 1500)
            viewModel.markVictory()
            onWin(viewModel.stars(), state.elapsed)
            sounds?.win(sound)
            view.sopaConfirm(context, haptics)
            delay(120)
            view.sopaConfirm(context, haptics)
            delay(380)
            showVictory = true
        }
    }
    LaunchedEffect(state.victory) {
        if (!state.victory) {
            showVictory = false
            winReported = false
        }
    }
    LaunchedEffect(state.victory) {
        if (!state.victory) {
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

    BackHandler(enabled = !state.victory) {
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

    val clueCard: @Composable () -> Unit = {
        if (mystery != null) {
            MysteryClueCard(
                clue = mystery.clue,
                answerLength = mystery.word.length,
                revealed = state.revealed || state.victory,
                answer = if (state.revealed || state.victory) mystery.word else null,
            )
        }
    }
    val chips: @Composable () -> Unit = {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            for (word in puzzle.words) {
                val foundIndex = state.found[word.word]
                MysteryWordChip(length = word.word.length, word = word.word, colorIndex = foundIndex, colorblind = colorblind)
            }
        }
    }
    AdaptiveGameScaffold(
        header = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(title, fontFamily = Bricolage, fontWeight = FontWeight.Bold, fontSize = 21.sp, color = colors.text)
                    Text(subtitle, fontFamily = Figtree, fontSize = 14.sp, color = colors.mute)
                }
                TimerChip(seconds = state.elapsed)
            }
        },
        progress = { SopaProgress(progress = progress) },
        board = {
            Column(verticalArrangement = Arrangement.spacedBy(15.dp)) {
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
                    highlightCells = if (state.revealed) mystery?.cells?.toSet() ?: emptySet() else emptySet(),
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
            }
        },
        side = {
            Column(verticalArrangement = Arrangement.spacedBy(15.dp)) {
                clueCard()
                chips()
            }
        },
        actions = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    HintButton(
                        hintsLeft = MAX_HINTS - state.hintsUsed,
                        onClick = viewModel::useHint,
                        modifier = Modifier.weight(1f),
                    )
                    SecondaryButton(text = "Pausa", onClick = viewModel::pause, modifier = Modifier.weight(1f))
                }
                PrimaryButton(text = "Adivinhar", onClick = viewModel::openSheet)
            }
        },
        overlays = {
            if (state.paused && !state.victory) {
                PauseOverlay(
                    subtitle = "$title · ${formatElapsed(state.elapsed)}",
                    onContinue = viewModel::resume,
                    onRestart = { viewModel.restart(kotlin.random.Random.nextLong()) },
                    onExit = onExit,
                )
            }
            AnimatedVisibility(visible = showVictory, enter = fadeIn(tween(250))) {
                MysteryWinOverlay(
                    stars = viewModel.stars(),
                    answer = mystery?.word ?: "",
                    seconds = state.elapsed,
                    primaryLabel = winPrimaryLabel,
                    secondaryLabel = winSecondaryLabel,
                    onPrimary = onNext,
                    onSecondary = onExit,
                )
            }
            if (state.sheetOpen && mystery != null) {
                GuessSheet(
                    clue = mystery.clue,
                    answerLength = mystery.word.length,
                    error = state.guessError,
                    shakeKey = state.shakeKey,
                    onDismiss = viewModel::closeSheet,
                    onVerify = viewModel::submitGuess,
                )
            }
        },
    )
}

@Composable
private fun MysteryClueCard(
    clue: String,
    answerLength: Int,
    revealed: Boolean,
    answer: String?,
) {
    val colors = LocalSopaColors.current
    val fontScale = androidx.compose.ui.platform.LocalDensity.current.fontScale
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(colors.surface)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = "Pista · $clue",
            fontFamily = Figtree,
            fontSize = 15.sp,
            color = colors.text,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(answerLength) { index ->
                val letter = if (revealed) answer?.getOrNull(index)?.toString() ?: "" else ""
                Box(
                    modifier = Modifier
                        .width(30.dp)
                        .height(35.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(colors.cell),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = letter,
                        fontFamily = Bricolage,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp / fontScale,
                        color = colors.accent,
                    )
                }
            }
        }
    }
}

@Composable
private fun MysteryWordChip(length: Int, word: String, colorIndex: Int?, colorblind: Boolean = false) {
    val colors = LocalSopaColors.current
    if (colorIndex != null) {
        val bg = colors.found[colorIndex % colors.found.size]
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(bg)
                .padding(horizontal = 11.dp, vertical = 5.dp),
        ) {
            if (colorblind) {
                Text(
                    text = app.sopadeletras.ui.components.colorblindSymbol(colorIndex),
                    fontSize = 13.sp,
                    color = colors.onAccent.copy(alpha = 0.55f),
                )
                Spacer(Modifier.width(4.dp))
            }
            Text(
                text = word,
                fontFamily = Figtree,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.5.sp,
                letterSpacing = 1.sp,
                color = colors.onAccent,
            )
        }
    } else {
        Text(
            text = "•".repeat(length),
            fontFamily = Figtree,
            fontSize = 13.5.sp,
            letterSpacing = 1.sp,
            color = colors.text,
            modifier = Modifier
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(colors.surface)
                .padding(horizontal = 11.dp, vertical = 5.dp)
                .semantics { contentDescription = "Palavra de $length letras, por encontrar" },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GuessSheet(
    clue: String,
    answerLength: Int,
    error: String?,
    shakeKey: Int,
    onDismiss: () -> Unit,
    onVerify: (String) -> Unit,
) {
    val colors = LocalSopaColors.current
    var text by remember { mutableStateOf("") }
    val shake = remember { Animatable(0f) }
    LaunchedEffect(shakeKey) {
        if (shakeKey > 0) {
            shake.snapTo(0f)
            shake.animateTo(6f, tween(50))
            shake.animateTo(-6f, tween(100))
            shake.animateTo(4f, tween(70))
            shake.animateTo(0f, tween(80))
        }
    }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.surface,
        shape = RoundedCornerShape(topStart = 35.dp, topEnd = 35.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 22.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text("Qual é a palavra?", fontFamily = Bricolage, fontWeight = FontWeight.Bold, fontSize = 21.sp, color = colors.text)
            Text("$clue · $answerLength letras", fontFamily = Figtree, fontSize = 14.sp, color = colors.mute)
            TextField(
                value = text,
                onValueChange = { text = it.uppercase().filter { c -> c in 'A'..'Z' } },
                singleLine = true,
                textStyle = TextStyle(
                    fontFamily = Bricolage,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    letterSpacing = 4.sp,
                    color = colors.text,
                ),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = { onVerify(text) }),
                isError = error != null,
                shape = RoundedCornerShape(18.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colors.cell,
                    unfocusedContainerColor = colors.cell,
                    errorContainerColor = colors.cell,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = colors.found[4],
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(x = shake.value.dp),
            )
            if (error != null) {
                Text(error, fontFamily = Figtree, fontSize = 14.sp, color = colors.found[4])
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SecondaryButton(text = "Cancelar", onClick = onDismiss, modifier = Modifier.weight(1f))
                PrimaryButton(text = "Verificar", onClick = { onVerify(text) }, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun MysteryWinOverlay(
    stars: Int,
    answer: String,
    seconds: Int,
    primaryLabel: String,
    secondaryLabel: String,
    onPrimary: () -> Unit,
    onSecondary: () -> Unit,
) {
    val colors = LocalSopaColors.current
    Box(
        modifier = Modifier
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
            Text("Palavra descoberta", fontFamily = Bricolage, fontWeight = FontWeight.Bold, fontSize = 32.sp, color = colors.text)
            StarsRow(stars = stars)
            Text("A palavra era", fontFamily = Figtree, fontSize = 14.sp, color = colors.mute)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                answer.forEach { letter ->
                    Box(
                        modifier = Modifier
                            .width(30.dp)
                            .height(35.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(colors.cell),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(letter.toString(), fontFamily = Bricolage, fontWeight = FontWeight.Bold, fontSize = 18.sp / androidx.compose.ui.platform.LocalDensity.current.fontScale, color = colors.accent)
                    }
                }
            }
            Text(formatElapsed(seconds), fontFamily = Figtree, fontSize = 14.sp, color = colors.mute)
            PrimaryButton(text = primaryLabel, onClick = onPrimary)
            SecondaryButton(text = secondaryLabel, onClick = onSecondary)
        }
    }
}
