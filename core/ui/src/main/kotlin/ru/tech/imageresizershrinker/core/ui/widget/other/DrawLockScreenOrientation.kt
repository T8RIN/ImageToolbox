package ru.tech.imageresizershrinker.core.ui.widget.other

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalSettingsState


@Composable
fun DrawLockScreenOrientation() {
    if (!LocalSettingsState.current.lockDrawOrientation) return

    val activity = LocalContext.current as Activity
    DisposableEffect(activity) {
        val originalOrientation = activity.requestedOrientation
        activity.lockOrientation()
        onDispose {
            activity.requestedOrientation = originalOrientation
        }
    }
}

private fun Activity.lockOrientation() {
    val display = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
        display
    } else {
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay
    }

    val rotation = display?.rotation ?: 0
    val orientation: Int = when (resources.configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            if (rotation == 0 || rotation == 90) {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else {
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
            }
        }

        Configuration.ORIENTATION_PORTRAIT -> {
            if (rotation == 0 || rotation == 270) {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } else {
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
            }
        }

        else -> 0
    }
    requestedOrientation = orientation
}