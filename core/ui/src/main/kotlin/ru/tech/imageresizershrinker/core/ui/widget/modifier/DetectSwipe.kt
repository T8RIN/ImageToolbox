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

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.onSwipeLeft(onSwipe: () -> Unit): Modifier {
    var dx = 0F

    return this.pointerInput(Unit) {
        detectHorizontalDragGestures(
            onDragEnd = {
                if (dx < 0) {
                    dx = 0F
                    onSwipe()
                }
            },
            onHorizontalDrag = { _, dragAmount ->
                dx = dragAmount
            }
        )
    }
}

fun Modifier.onSwipeRight(onSwipe: () -> Unit): Modifier {
    var dx = 0F

    return this.pointerInput(Unit) {
        detectHorizontalDragGestures(
            onDragEnd = {
                if (dx > 0) {
                    dx = 0F
                    onSwipe()
                }
            },
            onHorizontalDrag = { _, dragAmount ->
                dx = dragAmount
            }
        )
    }
}

fun Modifier.onSwipeDown(
    enabled: Boolean = true,
    onSwipe: () -> Unit
): Modifier {
    if (!enabled) return this

    var dy = 0F

    return this.pointerInput(Unit) {
        detectVerticalDragGestures(
            onDragEnd = {
                if (dy > 0) {
                    dy = 0F
                    onSwipe()
                }
            },
            onVerticalDrag = { _, dragAmount ->
                dy = dragAmount
            }
        )
    }
}

fun Modifier.detectSwipes(
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit
): Modifier {
    var dx = 0F

    return this.pointerInput(Unit) {
        detectHorizontalDragGestures(
            onDragEnd = {
                if (dx > 0) {
                    onSwipeRight()
                } else {
                    onSwipeLeft()
                }
                dx = 0F
            },
            onHorizontalDrag = { _, dragAmount ->
                dx = dragAmount
            }
        )
    }
}
