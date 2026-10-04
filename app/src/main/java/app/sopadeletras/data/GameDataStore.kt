package app.sopadeletras.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import app.sopadeletras.core.updateStreak
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.gameStore by preferencesDataStore(name = "game")

data class SavedGameData(
    val language: String,
    val level: Int,
    val foundWords: List<String>,
    val elapsed: Int,
    val hints: Int,
)

data class DailyData(
    val lastDay: Long,
    val streak: Int,
    val resultDay: Long?,
    val resultSeconds: Int,
    val resultStars: Int,
)

class GameDataStore(context: Context) {
    private val store = context.applicationContext.gameStore

    fun progressFlow(language: String): Flow<Map<Int, Int>> = store.data.map { prefs ->
        (1..300).mapNotNull { level ->
            prefs[intPreferencesKey(progressStarsKey(language, level))]?.let { stars -> level to stars }
        }.toMap()
    }

    suspend fun recordWin(language: String, level: Int, stars: Int, seconds: Int) {
        store.edit { prefs ->
            val starsKey = intPreferencesKey(progressStarsKey(language, level))
            val secondsKey = intPreferencesKey(progressSecondsKey(language, level))
            val previous = prefs[starsKey] ?: 0
            if (stars > previous) {
                prefs[starsKey] = stars
                prefs[secondsKey] = seconds
            } else if (stars == previous) {
                val best = prefs[secondsKey]
                if (best == null || seconds < best) prefs[secondsKey] = seconds
            }
        }
    }

    fun savedGameFlow(): Flow<SavedGameData?> = store.data.map { prefs ->
        if ((prefs[intPreferencesKey("sg_present")] ?: 0) != 1) return@map null
        SavedGameData(
            language = prefs[stringPreferencesKey("sg_language")] ?: return@map null,
            level = prefs[intPreferencesKey("sg_level")] ?: return@map null,
            foundWords = decodeFound(prefs[stringPreferencesKey("sg_words")] ?: ""),
            elapsed = prefs[intPreferencesKey("sg_elapsed")] ?: 0,
            hints = prefs[intPreferencesKey("sg_hints")] ?: 0,
        )
    }

    suspend fun saveGame(data: SavedGameData) {
        store.edit { prefs ->
            prefs[intPreferencesKey("sg_present")] = 1
            prefs[stringPreferencesKey("sg_language")] = data.language
            prefs[intPreferencesKey("sg_level")] = data.level
            prefs[stringPreferencesKey("sg_words")] = encodeFound(data.foundWords)
            prefs[intPreferencesKey("sg_elapsed")] = data.elapsed
            prefs[intPreferencesKey("sg_hints")] = data.hints
        }
    }

    suspend fun clearSavedGame() {
        store.edit { prefs ->
            prefs[intPreferencesKey("sg_present")] = 0
            prefs.remove(stringPreferencesKey("sg_language"))
            prefs.remove(intPreferencesKey("sg_level"))
            prefs.remove(stringPreferencesKey("sg_words"))
            prefs.remove(intPreferencesKey("sg_elapsed"))
            prefs.remove(intPreferencesKey("sg_hints"))
        }
    }

    fun resultsFlow(): Flow<List<StoredResult>> = store.data.map { prefs ->
        (prefs[stringSetPreferencesKey("results")] ?: emptySet()).map(::decodeResult)
    }

    fun dailyFlow(): Flow<DailyData?> = store.data.map { prefs ->
        val lastDay = prefs[longPreferencesKey("dy_last")] ?: return@map null
        DailyData(
            lastDay = lastDay,
            streak = prefs[intPreferencesKey("dy_streak")] ?: 0,
            resultDay = prefs[longPreferencesKey("dy_result_day")],
            resultSeconds = prefs[intPreferencesKey("dy_result_sec")] ?: 0,
            resultStars = prefs[intPreferencesKey("dy_result_stars")] ?: 0,
        )
    }

    suspend fun recordDaily(today: Long, seconds: Int, stars: Int) {
        store.edit { prefs ->
            if (prefs[longPreferencesKey("dy_result_day")] == today) return@edit
            val lastDay = prefs[longPreferencesKey("dy_last")]
            val streak = prefs[intPreferencesKey("dy_streak")] ?: 0
            val next = if (lastDay == null) 1 else updateStreak(lastDay, today, streak)
            prefs[longPreferencesKey("dy_last")] = today
            prefs[intPreferencesKey("dy_streak")] = next
            prefs[longPreferencesKey("dy_result_day")] = today
            prefs[intPreferencesKey("dy_result_sec")] = seconds
            prefs[intPreferencesKey("dy_result_stars")] = stars
        }
    }

    suspend fun addResult(row: StoredResult) {
        val current = resultsFlow().first().map(::encodeResult).toMutableSet()
        current.add(encodeResult(row))
        while (current.size > 1000) {
            val oldest = current.minByOrNull { decodeResult(it).finishedAt } ?: break
            current.remove(oldest)
        }
        store.edit { prefs ->
            prefs[stringSetPreferencesKey("results")] = current
        }
    }
}
