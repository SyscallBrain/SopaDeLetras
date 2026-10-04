package app.sopadeletras.ui.timeattack

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import app.sopadeletras.core.GenParams
import app.sopadeletras.core.PlacedWord
import app.sopadeletras.core.Puzzle
import app.sopadeletras.core.PuzzleGenerator
import app.sopadeletras.core.applyHintPenalty
import app.sopadeletras.core.matchesSelection
import app.sopadeletras.core.nextColorIndex
import app.sopadeletras.core.recordBeaten
import app.sopadeletras.core.wordPoints
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

const val BOARD_BONUS_POINTS = 50
const val BOARD_BONUS_SECONDS = 10
const val NEW_BOARD_DELAY_MS = 650L

data class TimeAttackUiState(
    val puzzle: Puzzle,
    val selected: List<Int> = emptyList(),
    val found: Map<String, Int> = emptyMap(),
    val hintCell: Int? = null,
    val remaining: Int = 180,
    val total: Int = 180,
    val elapsed: Int = 0,
    val points: Int = 0,
    val boards: Int = 0,
    val totalWords: Int = 0,
    val lastFoundElapsed: Int? = null,
    val bonusVisible: Boolean = false,
    val paused: Boolean = false,
    val finished: Boolean = false,
)

class TimeAttackViewModel(
    private val bankWords: List<String>,
    private val params: GenParams,
    seed: Long,
    private val fill: String,
    duration: Int,
    private val directionAssist: Boolean = true,
) : ViewModel() {
    private val _state = MutableStateFlow(
        TimeAttackUiState(
            puzzle = newPuzzle(seed),
            remaining = duration,
            total = duration,
        ),
    )
    val state: StateFlow<TimeAttackUiState> = _state.asStateFlow()
    val assist: Boolean get() = directionAssist

    init {
        viewModelScope.launch {
            while (true) {
                delay(1000)
                _state.update { current ->
                    if (current.paused || current.finished) return@update current
                    val remaining = current.remaining - 1
                    if (remaining <= 0) current.copy(remaining = 0, selected = emptyList(), finished = true)
                    else current.copy(remaining = remaining, elapsed = current.elapsed + 1)
                }
            }
        }
    }

    private fun newPuzzle(newSeed: Long): Puzzle =
        PuzzleGenerator.generate(params, bankWords, newSeed, fill)

    fun onSelectionChange(selection: List<Int>) {
        _state.update { it.copy(selected = selection) }
    }

    fun confirmSelection(selection: List<Int>): PlacedWord? {
        val current = _state.value
        if (current.paused || current.finished) {
            _state.update { it.copy(selected = emptyList()) }
            return null
        }
        val hit = matchesSelection(selection, current.puzzle.words, current.found.keys)
        if (hit == null) {
            _state.update { it.copy(selected = emptyList()) }
            return null
        }
        val gap = current.lastFoundElapsed?.let { current.elapsed - it }?.toDouble()
        val gained = wordPoints(hit.word.length, gap)
        _state.update {
            val found = it.found + (hit.word to nextColorIndex(it.found.values.toSet(), it.found.size))
            var next = it.copy(
                selected = emptyList(),
                found = found,
                hintCell = null,
                points = it.points + gained,
                totalWords = it.totalWords + 1,
                lastFoundElapsed = it.elapsed,
            )
            if (found.size == it.puzzle.words.size) {
                next = next.copy(
                    points = next.points + BOARD_BONUS_POINTS,
                    remaining = next.remaining + BOARD_BONUS_SECONDS,
                    total = next.total + BOARD_BONUS_SECONDS,
                    boards = next.boards + 1,
                    bonusVisible = true,
                )
                viewModelScope.launch {
                    delay(1400)
                    _state.update { inner -> inner.copy(bonusVisible = false) }
                }
                viewModelScope.launch {
                    delay(NEW_BOARD_DELAY_MS)
                    _state.update { inner ->
                        inner.copy(
                            puzzle = newPuzzle(kotlin.random.Random.nextLong()),
                            found = emptyMap(),
                        )
                    }
                }
            }
            next
        }
        return hit
    }

    fun useHint() {
        _state.update { current ->
            if (current.paused || current.finished) return@update current
            val cell = app.sopadeletras.ui.game.hintTarget(current.puzzle.words, current.found.keys)
                ?: return@update current
            current.copy(
                selected = emptyList(),
                remaining = applyHintPenalty(current.remaining, 5),
                hintCell = cell,
            )
        }
    }

    fun pause() {
        _state.update { it.copy(selected = emptyList(), paused = true) }
    }

    fun resume() {
        _state.update { it.copy(paused = false) }
    }

    fun onAppBackground() {
        val current = _state.value
        if (!current.paused && !current.finished) pause()
    }

    fun restart(duration: Int, newSeed: Long) {
        _state.value = TimeAttackUiState(
            puzzle = newPuzzle(newSeed),
            remaining = duration,
            total = duration,
        )
    }

    fun isRecord(record: Int): Boolean = recordBeaten(_state.value.points, record)
}

class TimeAttackViewModelFactory(
    private val bankWords: List<String>,
    private val params: GenParams,
    private val seed: Long,
    private val fill: String,
    private val duration: Int,
    private val directionAssist: Boolean = true,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(TimeAttackViewModel::class.java))
        return TimeAttackViewModel(bankWords, params, seed, fill, duration, directionAssist) as T
    }
}
