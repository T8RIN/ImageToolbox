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

package com.t8rin.imagetoolbox.feature.markup_layers.presentation.components

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateRotation
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.positionChanged
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.UiMarkupLayer
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.screenLogic.MarkupLayersComponent
import kotlin.math.PI
import kotlin.math.abs

internal fun Modifier.activeLayerGestures(
    component: MarkupLayersComponent,
    activeLayer: UiMarkupLayer?
): Modifier = pointerInput(activeLayer) {
    activeLayer ?: return@pointerInput

    awaitEachGesture {
        activeLayer.state
        var totalZoom = 1f
        var totalRotation = 0f
        var totalPan = Offset.Zero
        var pastTouchSlop = false
        val touchSlop = viewConfiguration.touchSlop

        var down = awaitFirstDown(
            pass = PointerEventPass.Initial,
            requireUnconsumed = false
        )
        var activePointerId = down.id

        do {
            val event = awaitPointerEvent(pass = PointerEventPass.Initial)
            val pressedChanges = event.changes.filter(PointerInputChange::pressed)
            if (pressedChanges.isEmpty()) break

            val activePointer = pressedChanges.firstOrNull { it.id == activePointerId }
                ?: pressedChanges.first().also {
                    activePointerId = it.id
                }

            val isMultiTouch = pressedChanges.size > 1
            val panChange = if (isMultiTouch) {
                event.calculatePan()
            } else {
                activePointer.positionChange()
            }
            val zoomChange = if (isMultiTouch) event.calculateZoom() else 1f
            val rotationChange = if (isMultiTouch) event.calculateRotation() else 0f

            if (!pastTouchSlop) {
                totalPan += panChange
                totalZoom *= zoomChange
                totalRotation += rotationChange

                val centroidSize = if (isMultiTouch) {
                    event.calculateCentroidSize(useCurrent = false)
                } else {
                    0f
                }
                val zoomMotion = abs(1 - totalZoom) * centroidSize
                val rotationMotion = abs(totalRotation * PI.toFloat() * centroidSize / 180f)
                val panMotion = totalPan.getDistance()

                if (zoomMotion > touchSlop ||
                    rotationMotion > touchSlop ||
                    panMotion > touchSlop
                ) {
                    component.beginHistoryTransaction()
                    pastTouchSlop = true
                }
            }

            if (pastTouchSlop) {
                component.updateLayerState(
                    layer = activeLayer,
                    commitToHistory = false
                ) {
                    val contentSize = contentSize
                    if (contentSize.width > 0 && contentSize.height > 0) {
                        val canvasWidth = canvasSize.width.takeIf { it > 0 } ?: size.width
                        val canvasHeight = canvasSize.height.takeIf { it > 0 } ?: size.height

                        applyGlobalChanges(
                            parentMaxWidth = canvasWidth,
                            parentMaxHeight = canvasHeight,
                            contentSize = contentSize,
                            cornerRadiusPercent = activeLayer.cornerRadiusPercent,
                            zoomChange = zoomChange,
                            offsetChange = panChange,
                            rotationChange = rotationChange
                        )
                    }
                }

                event.changes.forEach { change ->
                    if (change.positionChanged()) {
                        change.consume()
                    }
                }
            }

            down = activePointer
            activePointerId = down.id
        } while (true)

        if (pastTouchSlop) {
            component.commitHistoryTransaction()
        }
    }
}
