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

package ru.tech.imageresizershrinker.feature.quick_tiles.utils

import android.app.PendingIntent
import android.content.Intent
import android.service.quicksettings.TileService
import androidx.core.service.quicksettings.PendingIntentActivityWrapper
import androidx.core.service.quicksettings.TileServiceCompat
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.buildIntent
import ru.tech.imageresizershrinker.core.ui.utils.helper.putTileScreenAction
import ru.tech.imageresizershrinker.feature.quick_tiles.screenshot.ScreenshotLauncher

fun TileService.startActivityAndCollapse(
    screenAction: String? = null,
    clazz: Class<*> = ScreenshotLauncher::class.java
) {
    runCatching {
        val intent = buildIntent(clazz) {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putTileScreenAction(screenAction)
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