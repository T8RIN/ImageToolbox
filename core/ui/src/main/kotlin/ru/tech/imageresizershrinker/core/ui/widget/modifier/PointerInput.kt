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

package ru.tech.imageresizershrinker.core.ui.widget.modifier

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputScope
import kotlinx.coroutines.coroutineScope

suspend fun PointerInputScope.interceptTap(
    pass: PointerEventPass = PointerEventPass.Initial,
    onTap: ((Offset) -> Unit)? = null,
) = coroutineScope {
    if (onTap == null) return@coroutineScope

    awaitEachGesture {
        val down = awaitFirstDown(pass = pass)
        val downTime = System.currentTimeMillis()
        val tapTimeout = viewConfiguration.longPressTimeoutMillis
        val tapPosition = down.position

        do {
            val event = awaitPointerEvent(pass)
            val currentTime = System.currentTimeMillis()

            if (event.changes.size != 1) break // More than one event: not a tap
            if (currentTime - downTime >= tapTimeout) break // Too slow: not a tap

            val change = event.changes[0]

            if ((change.position - tapPosition).getDistance() > viewConfiguration.touchSlop) break

            if (change.id == down.id && !change.pressed) {
                change.consume()
                onTap(change.position)
            }
        } while (event.changes.any { it.id == down.id && it.pressed })
    }
}