/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package ru.tech.imageresizershrinker.core.ui.widget.other

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalComponentActivity


@Composable
fun DrawLockScreenOrientation() {
    if (!LocalSettingsState.current.lockDrawOrientation) return

    val activity = LocalComponentActivity.current
    DisposableEffect(activity) {
        val originalOrientation = activity.requestedOrientation
        activity.lockOrientation()
        onDispose {
            activity.requestedOrientation = originalOrientation
        }
    }
}

@Composable
fun LockScreenOrientation(
    mode: Int? = null
) {
    val activity = LocalComponentActivity.current
    DisposableEffect(activity) {
        val originalOrientation = activity.requestedOrientation
        activity.lockOrientation(mode)
        onDispose {
            activity.requestedOrientation = originalOrientation
        }
    }
}

private fun Activity.lockOrientation(mode: Int? = null) {
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
    requestedOrientation = mode ?: orientation
}