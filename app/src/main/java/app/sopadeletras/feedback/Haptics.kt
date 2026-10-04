package app.sopadeletras.feedback

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.view.View

fun isHapticsAllowed(context: Context, enabled: Boolean): Boolean {
    if (!enabled) return false
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) return true
    return runCatching {
        Settings.System.getInt(context.contentResolver, Settings.System.HAPTIC_FEEDBACK_ENABLED, 1) == 1
    }.getOrDefault(true)
}

private fun buzz(context: Context, enabled: Boolean, effect: (Vibrator) -> Unit) {
    if (!isHapticsAllowed(context, enabled)) return
    val vibrator = context.getSystemService(Vibrator::class.java) ?: return
    if (!vibrator.hasVibrator()) return
    runCatching { effect(vibrator) }
}

fun View.sopaTick(context: Context, enabled: Boolean) {
    buzz(context, enabled) { vibrator ->
        vibrator.vibrate(VibrationEffect.createOneShot(20, 120))
    }
}

fun View.sopaConfirm(context: Context, enabled: Boolean) {
    buzz(context, enabled) { vibrator ->
        vibrator.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE))
    }
}

fun View.sopaReject(context: Context, enabled: Boolean) {
    buzz(context, enabled) { vibrator ->
        vibrator.vibrate(VibrationEffect.createOneShot(70, VibrationEffect.DEFAULT_AMPLITUDE))
    }
}
