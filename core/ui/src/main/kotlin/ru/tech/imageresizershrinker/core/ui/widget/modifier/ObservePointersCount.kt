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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.util.fastAny
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlin.coroutines.cancellation.CancellationException


fun Modifier.observePointersCountWithOffset(
    enabled: Boolean = true,
    onChange: (Int, Offset) -> Unit
) = this then if (enabled) Modifier.pointerInput(Unit) {
    onEachGesture {
        val context = currentCoroutineContext()
        awaitPointerEventScope {
            do {
                val event = awaitPointerEvent()
                onChange(
                    event.changes.size,
                    event.changes.firstOrNull()?.position ?: Offset.Unspecified
                )
            } while (event.changes.any { it.pressed } && context.isActive)
            onChange(0, Offset.Unspecified)
        }
    }
} else Modifier

fun Modifier.observePointersCount(
    enabled: Boolean = true,
    onChange: (Int) -> Unit
) = this.observePointersCountWithOffset(enabled) { size, _ ->
    onChange(size)
}

suspend fun PointerInputScope.onEachGesture(block: suspend PointerInputScope.() -> Unit) {
    val currentContext = currentCoroutineContext()
    while (currentContext.isActive) {
        try {
            block()

            // Wait for all pointers to be up. Gestures start when a finger goes down.
            awaitAllPointersUp()
        } catch (e: CancellationException) {
            if (currentContext.isActive) {
                // The current gesture was canceled. Wait for all fingers to be "up" before looping
                // again.
                awaitAllPointersUp()
            } else {
                // forEachGesture was cancelled externally. Rethrow the cancellation exception to
                // propagate it upwards.
                throw e
            }
        }
    }
}

private suspend fun PointerInputScope.awaitAllPointersUp() {
    awaitPointerEventScope { awaitAllPointersUp() }
}

private suspend fun AwaitPointerEventScope.awaitAllPointersUp() {
    if (!allPointersUp()) {
        do {
            val events = awaitPointerEvent(PointerEventPass.Final)
        } while (events.changes.fastAny { it.pressed })
    }
}

private fun AwaitPointerEventScope.allPointersUp(): Boolean =
    !currentEvent.changes.fastAny { it.pressed }


@Composable
fun smartDelayAfterDownInMillis(pointersCount: Int): Long {
    var delayAfterDownInMillis by remember {
        mutableLongStateOf(20L)
    }
    var previousCount by remember {
        mutableIntStateOf(pointersCount)
    }
    LaunchedEffect(pointersCount) {
        delayAfterDownInMillis = if (pointersCount <= 1 && previousCount >= 2) 5L else 20L
        previousCount = pointersCount
    }

    return delayAfterDownInMillis
}