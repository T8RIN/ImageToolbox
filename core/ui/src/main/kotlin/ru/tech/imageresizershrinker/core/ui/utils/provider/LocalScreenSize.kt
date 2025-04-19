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

package ru.tech.imageresizershrinker.core.ui.utils.provider

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.t8rin.dynamic.theme.observeAsState
import com.t8rin.modalsheet.FullscreenPopup

val LocalScreenSize = compositionLocalOf<ScreenSize> { error("ScreenSize not present") }

@ConsistentCopyVisibility
data class ScreenSize internal constructor(
    val width: Dp,
    val height: Dp,
    val widthPx: Int,
    val heightPx: Int
)

private fun Density.ScreenSize(
    width: Dp,
    height: Dp,
) = ScreenSize(
    width = width,
    height = height,
    widthPx = width.roundToPx(),
    heightPx = height.roundToPx()
)

@Composable
fun rememberScreenSize(): ScreenSize {
    val windowInfo = LocalWindowInfo.current

    var constraints by remember(windowInfo) {
        mutableStateOf<Constraints?>(null)
    }

    if (constraints == null) {
        FullscreenPopup {
            BoxWithConstraints(
                modifier = Modifier.fillMaxSize()
            ) {
                SideEffect {
                    constraints = this.constraints
                }
            }
        }
    }

    val density = LocalDensity.current

    return remember(constraints, windowInfo, density) {
        derivedStateOf {
            with(density) {
                ScreenSize(
                    width = constraints?.maxWidth?.toDp() ?: windowInfo.containerSize.height.toDp(),
                    height = constraints?.maxHeight?.toDp()
                        ?: windowInfo.containerSize.width.toDp(),
                )
            }
        }
    }.value
}

@Composable
fun rememberCurrentLifecycleEvent(): Lifecycle.Event =
    LocalLifecycleOwner.current.lifecycle.observeAsState().value