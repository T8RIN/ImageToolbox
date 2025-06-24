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

package com.t8rin.imagetoolbox.core.ui.widget.modifier

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager

fun Modifier.tappable(
    key1: Any? = Unit,
    onTap: PointerInputScope.(Offset) -> Unit
): Modifier = pointerInput(key1) {
    detectTapGestures { onTap(it) }
}

fun Modifier.clearFocusOnTap(enabled: Boolean = true) = composed {
    val focus = LocalFocusManager.current

    if (enabled) {
        Modifier.pointerInput(focus) {
            detectTapGestures(
                onTap = {
                    focus.clearFocus()
                }
            )
        }
    } else {
        Modifier
    }
}