package app.sopadeletras.feedback

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import app.sopadeletras.R

class Sounds(context: Context) {
    private val app = context.applicationContext
    private val pool: SoundPool = SoundPool.Builder()
        .setMaxStreams(3)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build(),
        )
        .build()
    private val tickId = pool.load(app, R.raw.tick, 1)
    private val foundId = pool.load(app, R.raw.found, 1)
    private val winId = pool.load(app, R.raw.win, 1)

    private fun allowed(enabled: Boolean): Boolean {
        if (!enabled) return false
        val manager = app.getSystemService(AudioManager::class.java) ?: return true
        return manager.ringerMode == AudioManager.RINGER_MODE_NORMAL
    }

    fun tick(enabled: Boolean) {
        if (!allowed(enabled)) return
        pool.play(tickId, 0.25f, 0.25f, 1, 0, 1f)
    }

    fun found(enabled: Boolean) {
        if (!allowed(enabled)) return
        pool.play(foundId, 0.5f, 0.5f, 1, 0, 1f)
    }

    fun win(enabled: Boolean) {
        if (!allowed(enabled)) return
        pool.play(winId, 0.5f, 0.5f, 1, 0, 1f)
    }

    fun release() {
        pool.release()
    }
}
