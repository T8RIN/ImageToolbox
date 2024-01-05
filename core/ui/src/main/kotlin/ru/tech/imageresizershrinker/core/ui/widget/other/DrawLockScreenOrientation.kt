package ru.tech.imageresizershrinker.core.ui.widget.other

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalSettingsState

@Composable
fun DrawLockScreenOrientation(orientation: Int) {
    if (!LocalSettingsState.current.lockDrawOrientation) return

    val activity = LocalContext.current as Activity
    DisposableEffect(orientation) {
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        onDispose {
            activity.requestedOrientation = originalOrientation
        }
    }
}