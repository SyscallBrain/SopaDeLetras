package app.sopadeletras.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import app.sopadeletras.core.GenParams
import app.sopadeletras.core.PlacedWord
import app.sopadeletras.core.Puzzle
import app.sopadeletras.core.PuzzleGenerator
import app.sopadeletras.core.matchesSelection
import app.sopadeletras.core.nextColorIndex
import app.sopadeletras.core.starsFor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

const val MAX_HINTS = 3

data class GameUiState(
    val puzzle: Puzzle,
    val selected: List<Int> = emptyList(),
    val found: Map<String, Int> = emptyMap(),
    val hintsUsed: Int = 0,
    val hintCell: Int? = null,
    val elapsed: Int = 0,
    val paused: Boolean = false,
    val victory: Boolean = false,
    val timedOut: Boolean = false,
)

data class SavedPlayState(
    val foundWords: List<String>,
    val elapsed: Int,
    val hints: Int,
)

class GameViewModel(
    private val bankWords: List<String>,
    private val params: GenParams,
    seed: Long,
    private val fill: String,
    private val directionAssist: Boolean = true,
    private val timeLimit: Int? = null,
    initial: SavedPlayState? = null,
) : ViewModel() {
    private val _state = MutableStateFlow(
        GameUiState(
            puzzle = newPuzzle(seed),
            found = rebuildFound(initial?.foundWords.orEmpty()),
            elapsed = initial?.elapsed ?: 0,
            hintsUsed = initial?.hints ?: 0,
        ),
    )
    val state: StateFlow<GameUiState> = _state.asStateFlow()
    val assist: Boolean get() = directionAssist
    val limit: Int? get() = timeLimit

    private var timer: Job? = null

    init {
        timer = viewModelScope.launch {
            while (true) {
                delay(1000)
                _state.update { current ->
                    if (current.paused || current.victory || current.timedOut) return@update current
                    val elapsed = current.elapsed + 1
                    if (timeLimit != null && elapsed >= timeLimit) {
                        current.copy(elapsed = elapsed, selected = emptyList(), timedOut = true)
                    } else {
                        current.copy(elapsed = elapsed)
                    }
                }
            }
        }
    }

    private fun newPuzzle(newSeed: Long): Puzzle =
        PuzzleGenerator.generate(params, bankWords, newSeed, fill)

    private fun rebuildFound(words: List<String>): Map<String, Int> {
        val found = mutableMapOf<String, Int>()
        for (word in words) {
            found[word] = nextColorIndex(found.values.toSet(), found.size)
        }
        return found
    }

    fun onSelectionChange(selection: List<Int>) {
        _state.update { it.copy(selected = selection) }
    }

    fun confirmSelection(selection: List<Int>): PlacedWord? {
        val current = _state.value
        if (current.paused || current.victory || current.timedOut) {
            _state.update { it.copy(selected = emptyList()) }
            return null
        }
        val hit = matchesSelection(selection, current.puzzle.words, current.found.keys)
        _state.update {
            if (hit == null) {
                it.copy(selected = emptyList())
            } else {
                val found = it.found + (hit.word to nextColorIndex(it.found.values.toSet(), it.found.size))
                it.copy(
                    selected = emptyList(),
                    found = found,
                    hintCell = null,
                    victory = found.size == it.puzzle.words.size,
                )
            }
        }
        return hit
    }

    fun useHint() {
        _state.update { current ->
            if (current.paused || current.victory || current.timedOut || current.hintsUsed >= MAX_HINTS) return@update current
            val cell = hintTarget(current.puzzle.words, current.found.keys) ?: return@update current
            current.copy(hintCell = cell, hintsUsed = current.hintsUsed + 1)
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
        if (!current.paused && !current.victory && current.found.isNotEmpty()) pause()
    }

    fun restart(newSeed: Long) {
        _state.value = GameUiState(puzzle = newPuzzle(newSeed))
    }

    fun stars(): Int {
        val current = _state.value
        return starsFor(current.elapsed, current.hintsUsed, current.puzzle.words.size, current.puzzle.n)
    }
}

class GameViewModelFactory(
    private val bankWords: List<String>,
    private val params: GenParams,
    private val seed: Long,
    private val fill: String,
    private val directionAssist: Boolean = true,
    private val timeLimit: Int? = null,
    private val initial: SavedPlayState? = null,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(GameViewModel::class.java))
        return GameViewModel(bankWords, params, seed, fill, directionAssist, timeLimit, initial) as T
    }
}
