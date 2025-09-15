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

package com.t8rin.imagetoolbox.feature.quick_tiles.utils

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import androidx.core.service.quicksettings.PendingIntentActivityWrapper
import androidx.core.service.quicksettings.TileServiceCompat
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppActivityClass
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.SCREEN_ID_EXTRA
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.SHORTCUT_OPEN_ACTION
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.buildIntent
import com.t8rin.imagetoolbox.core.ui.utils.helper.ScreenshotAction
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.feature.quick_tiles.screenshot.ScreenshotLauncher

@RequiresApi(Build.VERSION_CODES.N)
internal fun TileService.startActivityAndCollapse(
    tileAction: TileAction
) {
    runCatching {
        val intent = buildIntent(tileAction.clazz) {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            when (tileAction) {
                TileAction.OpenApp -> Unit

                TileAction.Screenshot -> action = ScreenshotAction

                is TileAction.ScreenshotAndOpenScreen -> {
                    action = SHORTCUT_OPEN_ACTION
                    tileAction.screen?.let {
                        putExtra(SCREEN_ID_EXTRA, it.id)
                    }
                }

                is TileAction.OpenScreen -> {
                    action = SHORTCUT_OPEN_ACTION
                    tileAction.screen?.let {
                        putExtra(SCREEN_ID_EXTRA, it.id)
                    }
                }
            }
        }

        TileServiceCompat.startActivityAndCollapse(
            this,
            PendingIntentActivityWrapper(
                applicationContext,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT,
                false
            )
        )
    }
}

internal sealed class TileAction(
    val clazz: Class<*>
) {
    data class ScreenshotAndOpenScreen(val screen: Screen?) :
        TileAction(ScreenshotLauncher::class.java)

    data class OpenScreen(val screen: Screen?) : TileAction(AppActivityClass)

    data object Screenshot : TileAction(ScreenshotLauncher::class.java)
    data object OpenApp : TileAction(AppActivityClass)
}