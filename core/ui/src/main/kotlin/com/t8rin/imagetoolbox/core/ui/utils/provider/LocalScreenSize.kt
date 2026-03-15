/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.core.ui.utils.provider

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.t8rin.dynamic.theme.observeAsState

val LocalScreenSize = compositionLocalOf<ScreenSize> { error("ScreenSize not present") }

@ConsistentCopyVisibility
data class ScreenSize internal constructor(
    val width: Dp,
    val height: Dp,
    val widthPx: Int,
    val heightPx: Int
)

@Composable
fun rememberScreenSize(): ScreenSize {
    val windowInfo = LocalWindowInfo.current

    return remember(windowInfo) {
        derivedStateOf {
            windowInfo.run {
                ScreenSize(
                    width = containerDpSize.width,
                    height = containerDpSize.height,
                    widthPx = containerSize.width,
                    heightPx = containerSize.height
                )
            }
        }
    }.value
}

@Composable
fun rememberCurrentLifecycleEvent(): Lifecycle.Event =
    LocalLifecycleOwner.current.lifecycle.observeAsState().value