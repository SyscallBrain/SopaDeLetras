package app.sopadeletras.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import app.sopadeletras.core.Difficulty
import app.sopadeletras.core.Language
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.Locale

private val Context.settingsStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

data class StartupSnapshot(
    val language: String,
    val theme: String,
    val assist: Boolean,
    val haptics: Boolean,
    val letterScale: Float,
    val colorblind: Boolean,
)

class SettingsStore(context: Context) {
    private val store = context.applicationContext.settingsStore

    suspend fun snapshot(): StartupSnapshot {
        val prefs = store.data.first()
        return StartupSnapshot(
            language = prefs[Keys.LANGUAGE] ?: defaultLanguageForSystem(),
            theme = prefs[Keys.THEME] ?: "TINTA",
            assist = prefs[Keys.ASSIST] ?: true,
            haptics = prefs[Keys.HAPTICS] ?: true,
            letterScale = prefs[Keys.LETTER_SCALE] ?: 1f,
            colorblind = prefs[Keys.COLORBLIND] ?: false,
        )
    }

    val language: Flow<String> = store.data.map { it[Keys.LANGUAGE] ?: defaultLanguageForSystem() }
    val difficulty: Flow<Difficulty> = store.data.map {
        runCatching { Difficulty.valueOf(it[Keys.DIFFICULTY] ?: "NORMAL") }.getOrDefault(Difficulty.NORMAL)
    }
    val theme: Flow<String> = store.data.map { it[Keys.THEME] ?: "TINTA" }
    val directionAssist: Flow<Boolean> = store.data.map { it[Keys.ASSIST] ?: true }
    val boardSize: Flow<Int> = store.data.map { it[Keys.BOARD_SIZE] ?: 8 }
    val categoryId: Flow<String> = store.data.map { it[Keys.CATEGORY] ?: "" }
    val timeAttackDuration: Flow<Int> = store.data.map { it[Keys.TIME_ATTACK] ?: 180 }
    val haptics: Flow<Boolean> = store.data.map { it[Keys.HAPTICS] ?: true }
    val sound: Flow<Boolean> = store.data.map { it[Keys.SOUND] ?: true }
    val letterScale: Flow<Float> = store.data.map { it[Keys.LETTER_SCALE] ?: 1f }
    val colorblind: Flow<Boolean> = store.data.map { it[Keys.COLORBLIND] ?: false }
    val tutorialSeen: Flow<Set<String>> = store.data.map { it[Keys.TUTORIAL] ?: emptySet() }

    suspend fun setDifficulty(value: Difficulty) {
        store.edit { it[Keys.DIFFICULTY] = value.name }
    }

    suspend fun setLanguage(value: String) {
        store.edit { it[Keys.LANGUAGE] = value }
    }

    suspend fun setTheme(value: String) {
        store.edit { it[Keys.THEME] = value }
    }

    suspend fun setDirectionAssist(value: Boolean) {
        store.edit { it[Keys.ASSIST] = value }
    }

    suspend fun setHaptics(value: Boolean) {
        store.edit { it[Keys.HAPTICS] = value }
    }

    suspend fun setSound(value: Boolean) {
        store.edit { it[Keys.SOUND] = value }
    }

    suspend fun setLetterScale(value: Float) {
        store.edit { it[Keys.LETTER_SCALE] = value }
    }

    suspend fun setColorblind(value: Boolean) {
        store.edit { it[Keys.COLORBLIND] = value }
    }

    suspend fun setBoardSize(value: Int) {
        store.edit { it[Keys.BOARD_SIZE] = value }
    }

    suspend fun setCategoryId(value: String) {
        store.edit { it[Keys.CATEGORY] = value }
    }

    suspend fun setTimeAttackDuration(value: Int) {
        store.edit { it[Keys.TIME_ATTACK] = value }
    }

    suspend fun markTutorialSeen(tip: String) {
        store.edit { it[Keys.TUTORIAL] = (it[Keys.TUTORIAL] ?: emptySet()) + tip }
    }

    private object Keys {
        val LANGUAGE = stringPreferencesKey("word_language")
        val DIFFICULTY = stringPreferencesKey("difficulty")
        val THEME = stringPreferencesKey("theme")
        val ASSIST = booleanPreferencesKey("direction_assist")
        val BOARD_SIZE = intPreferencesKey("board_size")
        val CATEGORY = stringPreferencesKey("category")
        val TIME_ATTACK = intPreferencesKey("time_attack_duration")
        val HAPTICS = booleanPreferencesKey("haptics")
        val SOUND = booleanPreferencesKey("sound")
        val LETTER_SCALE = floatPreferencesKey("letter_scale")
        val COLORBLIND = booleanPreferencesKey("colorblind")
        val TUTORIAL = stringSetPreferencesKey("tutorial_seen")
    }
}

fun defaultLanguageForSystem(): String =
    if (Locale.getDefault().language == "en") Language.EN_CODE else Language.PT_CODE
