package app.sopadeletras

import android.content.Context
import app.sopadeletras.data.GameDataStore
import app.sopadeletras.data.SettingsStore
import app.sopadeletras.data.WordRepository
import app.sopadeletras.feedback.Sounds

class AppContainer(context: Context) {
    private val appContext = context.applicationContext
    val settings = SettingsStore(appContext)
    val gameData = GameDataStore(appContext)
    val words = WordRepository(appContext)
    val sounds = Sounds(appContext)
}
