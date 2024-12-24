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

package ru.tech.imageresizershrinker.core.ui.utils.helper

import androidx.compose.foundation.Indication
import androidx.compose.material.LocalContentColor
import androidx.compose.material.RippleDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState

@Composable
fun rememberRipple(
    bounded: Boolean = true,
    radius: Dp = Dp.Unspecified
): Indication {
    val contentColor = LocalContentColor.current
    val lightTheme = !LocalSettingsState.current.isNightMode
    return remember(bounded, radius) {
        ripple(
            color = {
                RippleDefaults.rippleColor(
                    contentColor = contentColor,
                    lightTheme = lightTheme
                )
            },
            bounded = bounded,
            radius = radius
        )
    }
}