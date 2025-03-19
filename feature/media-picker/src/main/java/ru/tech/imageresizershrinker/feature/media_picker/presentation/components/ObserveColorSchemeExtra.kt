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

package ru.tech.imageresizershrinker.feature.media_picker.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.t8rin.dynamic.theme.ColorTuple
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.ui.utils.helper.ColorSchemeName
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalComponentActivity

@Composable
internal fun ObserveColorSchemeExtra() {
    val context = LocalComponentActivity.current
    val dynamicTheme = LocalDynamicThemeState.current

    val scope = rememberCoroutineScope()
    SideEffect {
        context.intent.getIntExtra(ColorSchemeName, Color.Transparent.toArgb()).takeIf {
            it != Color.Transparent.toArgb()
        }?.let {
            scope.launch {
                while (dynamicTheme.colorTuple.value.primary != Color(it)) {
                    dynamicTheme.updateColorTuple(ColorTuple(Color(it)))
                    delay(500L)
                }
            }
        }
    }
}
