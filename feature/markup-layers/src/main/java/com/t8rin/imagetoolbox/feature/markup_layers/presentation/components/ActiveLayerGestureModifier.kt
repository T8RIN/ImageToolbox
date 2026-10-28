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
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.UiMarkupLayer
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.applyGroupGlobalChanges
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.uiCornerRadiusPercent
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.screenLogic.MarkupLayersComponent
import kotlin.math.abs
import kotlin.math.sign

internal fun Modifier.activeLayerGestures(
    component: MarkupLayersComponent,
    activeLayer: UiMarkupLayer?,
    snappingEnabled: Boolean,
    canvasScale: Float,
    onSnap: (LayerSnapResult?) -> Unit
): Modifier = pointerInput(activeLayer, snappingEnabled, canvasScale) {
    if (activeLayer == null || activeLayer.isLocked) return@pointerInput

    awaitEachGesture {
        var snapCorrection = Offset.Zero
        val snapThreshold = 6.dp.toPx() / canvasScale
        var totalZoom = 1f
        var totalRotation = 0f
        var totalPan = Offset.Zero
        var pastTouchSlop = false
        var pastRotationSlop = false
        val touchSlop = viewConfiguration.touchSlop
        val rotationSlop = 10f

        var down = awaitFirstDown(
            pass = PointerEventPass.Initial,
            requireUnconsumed = false
        )
        var activePointerId = down.id

        try {
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
                val rotationChange = if (isMultiTouch) {
                    val change = event.calculateRotation()
                    if (pastRotationSlop) {
                        change
                    } else {
                        totalRotation += change
                        val rotationBeyondSlop =
                            (abs(totalRotation) - rotationSlop).coerceAtLeast(0f)
                        pastRotationSlop = rotationBeyondSlop > 0f
                        rotationBeyondSlop * totalRotation.sign
                    }
                } else {
                    totalRotation = 0f
                    pastRotationSlop = false
                    0f
                }

                if (!pastTouchSlop) {
                    totalPan += panChange
                    totalZoom *= zoomChange

                    val centroidSize = if (isMultiTouch) {
                        event.calculateCentroidSize(useCurrent = false)
                    } else {
                        0f
                    }
                    val zoomMotion = abs(1 - totalZoom) * centroidSize
                    val panMotion = totalPan.getDistance()

                    if (zoomMotion > touchSlop ||
                        pastRotationSlop ||
                        panMotion > touchSlop
                    ) {
                        component.beginHistoryTransaction()
                        pastTouchSlop = true
                    }
                }

                if (pastTouchSlop) {
                    val canvasSize = IntegerSize(size.width, size.height)
                    val offsetChange = if (snappingEnabled && !isMultiTouch) {
                        panChange - snapCorrection
                    } else panChange
                    snapCorrection = Offset.Zero
                    var snapResult: LayerSnapResult? = null

                    component.updateLayerState(
                        layer = activeLayer,
                        commitToHistory = false
                    ) {
                        if (activeLayer.isGroup) {
                            activeLayer.applyGroupGlobalChanges(
                                zoomChange = zoomChange,
                                offsetChange = offsetChange,
                                rotationChange = rotationChange
                            )
                        } else {
                            val contentSize = contentSize
                            if (contentSize.width > 0 && contentSize.height > 0) {
                                val canvasWidth = canvasSize.width.takeIf { it > 0 } ?: size.width
                                val canvasHeight =
                                    canvasSize.height.takeIf { it > 0 } ?: size.height

                                applyGlobalChanges(
                                    parentMaxWidth = canvasWidth,
                                    parentMaxHeight = canvasHeight,
                                    contentSize = contentSize,
                                    cornerRadiusPercent = activeLayer.uiCornerRadiusPercent(),
                                    zoomChange = zoomChange,
                                    offsetChange = offsetChange,
                                    rotationChange = rotationChange
                                )
                            }
                        }

                        if (snappingEnabled && !isMultiTouch) {
                            snapResult = activeLayer.snapToLayers(
                                layers = component.layers,
                                canvasSize = canvasSize,
                                threshold = snapThreshold
                            )
                            snapCorrection = snapResult?.offset ?: Offset.Zero
                        }
                    }
                    onSnap(snapResult)

                    event.changes.forEach { change ->
                        if (change.positionChanged()) {
                            change.consume()
                        }
                    }
                }

                down = activePointer
                activePointerId = down.id
            } while (true)
        } finally {
            onSnap(null)
            if (pastTouchSlop) {
                component.commitHistoryTransaction()
            }
        }
    }
}
