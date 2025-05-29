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

package ru.tech.imageresizershrinker.feature.draw.presentation.components.utils

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.magnifier
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.gesture.pointerMotionEvents
import net.engawapg.lib.zoomable.ZoomState
import net.engawapg.lib.zoomable.ZoomableDefaults.defaultZoomOnDoubleTap
import net.engawapg.lib.zoomable.zoomable
import ru.tech.imageresizershrinker.core.ui.widget.modifier.observePointersCountWithOffset
import ru.tech.imageresizershrinker.core.ui.widget.modifier.smartDelayAfterDownInMillis

fun Modifier.pointerDrawObserver(
    magnifierEnabled: Boolean,
    currentDrawPosition: Offset,
    zoomState: ZoomState,
    globalTouchPointersCount: MutableIntState,
    panEnabled: Boolean
) = this.composed {
    var globalTouchPosition by remember { mutableStateOf(Offset.Unspecified) }

    Modifier
        .fillMaxSize()
        .observePointersCountWithOffset { size, offset ->
            globalTouchPointersCount.intValue = size
            globalTouchPosition = offset
        }
        .then(
            if (magnifierEnabled) {
                Modifier.magnifier(
                    sourceCenter = {
                        if (currentDrawPosition.isSpecified) {
                            globalTouchPosition
                        } else Offset.Unspecified
                    },
                    magnifierCenter = {
                        globalTouchPosition - Offset(0f, 100.dp.toPx())
                    },
                    size = DpSize(height = 100.dp, width = 100.dp),
                    cornerRadius = 50.dp,
                    elevation = 2.dp
                )
            } else Modifier
        )
        .clipToBounds()
        .zoomable(
            zoomState = zoomState,
            zoomEnabled = (globalTouchPointersCount.intValue >= 2 || panEnabled),
            enableOneFingerZoom = panEnabled,
            onDoubleTap = { pos ->
                if (panEnabled) zoomState.defaultZoomOnDoubleTap(pos)
            }
        )
}

fun Modifier.pointerDrawHandler(
    globalTouchPointersCount: MutableIntState,
    onReceiveMotionEvent: (MotionEvent) -> Unit,
    onInvalidate: () -> Unit,
    onUpdateCurrentDrawPosition: (Offset) -> Unit,
    onUpdateDrawDownPosition: (Offset) -> Unit,
    enabled: Boolean
) = if (enabled) {
    this.composed {
        var drawStartedWithOnePointer by remember {
            mutableStateOf(false)
        }

        Modifier.pointerMotionEvents(
            onDown = { pointerInputChange ->
                drawStartedWithOnePointer = globalTouchPointersCount.intValue <= 1

                if (drawStartedWithOnePointer) {
                    onReceiveMotionEvent(MotionEvent.Down)
                    onUpdateCurrentDrawPosition(pointerInputChange.position)
                    onUpdateDrawDownPosition(pointerInputChange.position)
                    pointerInputChange.consume()
                    onInvalidate()
                }
            },
            onMove = { pointerInputChange ->
                if (drawStartedWithOnePointer) {
                    onReceiveMotionEvent(MotionEvent.Move)
                    onUpdateCurrentDrawPosition(pointerInputChange.position)
                    pointerInputChange.consume()
                    onInvalidate()
                }
            },
            onUp = { pointerInputChange ->
                if (drawStartedWithOnePointer) {
                    onReceiveMotionEvent(MotionEvent.Up)
                    pointerInputChange.consume()
                    onInvalidate()
                }
                drawStartedWithOnePointer = false
            },
            delayAfterDownInMillis = smartDelayAfterDownInMillis(globalTouchPointersCount.intValue)
        )
    }
} else {
    this
}