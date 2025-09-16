/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.feature.quick_tiles.tiles

import android.content.Context
import android.content.Intent
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppActivityClass
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.SHORTCUT_OPEN_ACTION
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.buildIntent
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.putScreenExtra
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.feature.quick_tiles.screenshot.SCREENSHOT_ACTION
import com.t8rin.imagetoolbox.feature.quick_tiles.screenshot.ScreenshotLauncher

internal sealed class TileAction(val clazz: Class<*>) {
    data class ScreenshotAndOpenScreen(val screen: Screen?) : TileAction(ScreenshotClass)
    data class OpenScreen(val screen: Screen?) : TileAction(AppActivityClass)
    data object Screenshot : TileAction(ScreenshotClass)
    data object OpenApp : TileAction(AppActivityClass)

    fun toIntent(context: Context): Intent = context.buildIntent(clazz) {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        when (this@TileAction) {
            OpenApp -> setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

            Screenshot -> action = SCREENSHOT_ACTION

            is ScreenshotAndOpenScreen -> {
                action = SHORTCUT_OPEN_ACTION
                putScreenExtra(screen)
            }

            is OpenScreen -> {
                action = SHORTCUT_OPEN_ACTION
                putScreenExtra(screen)
            }
        }
    }

    companion object {
        private val ScreenshotClass = ScreenshotLauncher::class.java
    }
}