package app.sopadeletras.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import app.sopadeletras.core.MysteryEntry
import app.sopadeletras.core.PlacedWord
import app.sopadeletras.core.Puzzle
import app.sopadeletras.core.PuzzleGenerator
import app.sopadeletras.core.isMysteryGuessCorrect
import app.sopadeletras.core.matchesSelection
import app.sopadeletras.core.mysteryStars
import app.sopadeletras.core.nextColorIndex
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MysteryUiState(
    val puzzle: Puzzle,
    val selected: List<Int> = emptyList(),
    val found: Map<String, Int> = emptyMap(),
    val hintsUsed: Int = 0,
    val hintCell: Int? = null,
    val elapsed: Int = 0,
    val paused: Boolean = false,
    val victory: Boolean = false,
    val revealed: Boolean = false,
    val solvedByGuess: Boolean = false,
    val sheetOpen: Boolean = false,
    val guessError: String? = null,
    val shakeKey: Int = 0,
)

class MysteryViewModel(
    private val bankWords: List<String>,
    private val mysteries: Map<Int, MysteryEntry>,
    private val boardSize: Int,
    private val seed: Long,
    private val fill: String,
    private val directionAssist: Boolean = true,
    initial: SavedPlayState? = null,
) : ViewModel() {
    private val _state = MutableStateFlow(
        MysteryUiState(
            puzzle = PuzzleGenerator.genMystery(boardSize, bankWords, mysteries, seed, fill),
            found = rebuildFound(initial?.foundWords.orEmpty()),
            elapsed = initial?.elapsed ?: 0,
            hintsUsed = initial?.hints ?: 0,
        ),
    )
    val state: StateFlow<MysteryUiState> = _state.asStateFlow()
    val assist: Boolean get() = directionAssist

    init {
        _state.update { current ->
            if (current.puzzle.mystery == null) current
            else if (current.found.size == current.puzzle.words.size) current.copy(revealed = true)
            else current
        }
        viewModelScope.launch {
            while (true) {
                delay(1000)
                _state.update { current ->
                    if (current.paused || current.victory || current.sheetOpen) current
                    else current.copy(elapsed = current.elapsed + 1)
                }
            }
        }
    }

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
        if (current.paused || current.victory) {
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
                    revealed = found.size == it.puzzle.words.size,
                )
            }
        }
        return hit
    }

    fun useHint() {
        _state.update { current ->
            if (current.paused || current.victory || current.hintsUsed >= MAX_HINTS) return@update current
            val cell = hintTarget(current.puzzle.words, current.found.keys) ?: return@update current
            current.copy(hintCell = cell, hintsUsed = current.hintsUsed + 1)
        }
    }

    fun openSheet() {
        _state.update { it.copy(sheetOpen = true, guessError = null) }
    }

    fun closeSheet() {
        _state.update { it.copy(sheetOpen = false, guessError = null) }
    }

    fun submitGuess(text: String) {
        _state.update { current ->
            val answer = current.puzzle.mystery?.word ?: return@update current
            if (text.length < answer.length) {
                return@update current.copy(guessError = "Faltam letras.", shakeKey = current.shakeKey + 1)
            }
            if (!isMysteryGuessCorrect(text, answer)) {
                return@update current.copy(guessError = "Ainda não é essa.", shakeKey = current.shakeKey + 1)
            }
            current.copy(
                sheetOpen = false,
                guessError = null,
                solvedByGuess = !current.revealed,
                revealed = true,
            )
        }
    }

    fun markVictory() {
        _state.update { it.copy(victory = true) }
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
        _state.value = MysteryUiState(
            puzzle = PuzzleGenerator.genMystery(boardSize, bankWords, mysteries, newSeed, fill),
        )
    }

    fun stars(): Int {
        val current = _state.value
        return mysteryStars(current.solvedByGuess, current.hintsUsed)
    }
}

class MysteryViewModelFactory(
    private val bankWords: List<String>,
    private val mysteries: Map<Int, MysteryEntry>,
    private val boardSize: Int,
    private val seed: Long,
    private val fill: String,
    private val directionAssist: Boolean = true,
    private val initial: SavedPlayState? = null,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(MysteryViewModel::class.java))
        return MysteryViewModel(bankWords, mysteries, boardSize, seed, fill, directionAssist, initial) as T
    }
}
