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

package com.t8rin.cropper

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.debugInspectorInfo
import com.t8rin.cropper.model.CropData
import com.t8rin.cropper.state.CropState
import com.t8rin.cropper.state.cropData
import com.t8rin.cropper.util.ZoomLevel
import com.t8rin.cropper.util.getNextZoomLevel
import com.t8rin.cropper.util.update
import com.t8rin.gesture.detectMotionEventsAsList
import com.t8rin.gesture.detectTransformGestures
import kotlinx.coroutines.launch

/**
 * Modifier that zooms in or out of Composable set to. This zoom modifier has option
 * to move back to bounds with an animation or option to have fling gesture when user removes
 * from screen while velocity is higher than threshold to have smooth touch effect.
 *
 * @param keys are used for [Modifier.pointerInput] to restart closure when any keys assigned
 * change
 * empty space on sides or edges of parent.
 * @param cropState State of the zoom that contains option to set initial, min, max zoom,
 * enabling rotation, pan or zoom and contains current [CropData]
 * event propagations. Also contains [Rect] of visible area based on pan, zoom and rotation
 * @param zoomOnDoubleTap lambda that returns current [ZoomLevel] and based on current level
 * enables developer to define zoom on double tap gesture
 * @param onGestureStart callback to to notify gesture has started and return current
 * [CropData]  of this modifier
 * @param onGesture callback to notify about ongoing gesture and return current
 * [CropData]  of this modifier
 * @param onGestureEnd callback to notify that gesture finished return current
 * [CropData]  of this modifier
 */
fun Modifier.crop(
    vararg keys: Any?,
    cropState: CropState,
    zoomOnDoubleTap: (ZoomLevel) -> Float = cropState.DefaultOnDoubleTap,
    onDown: ((CropData) -> Unit)? = null,
    onMove: ((CropData) -> Unit)? = null,
    onUp: ((CropData) -> Unit)? = null,
    onGestureStart: ((CropData) -> Unit)? = null,
    onGesture: ((CropData) -> Unit)? = null,
    onGestureEnd: ((CropData) -> Unit)? = null
) = composed(

    factory = {

        LaunchedEffect(key1 = cropState) {
            cropState.init()
        }

        val coroutineScope = rememberCoroutineScope()

        // Current Zoom level
        var zoomLevel by remember { mutableStateOf(ZoomLevel.Min) }

        val transformModifier = Modifier.pointerInput(*keys) {
            detectTransformGestures(
                consume = false,
                onGestureStart = {
                    onGestureStart?.invoke(cropState.cropData)
                },
                onGestureEnd = {
                    coroutineScope.launch {
                        cropState.onGestureEnd {
                            onGestureEnd?.invoke(cropState.cropData)
                        }
                    }
                },
                onGesture = { centroid, pan, zoom, rotate, mainPointer, pointerList ->

                    coroutineScope.launch {
                        cropState.onGesture(
                            centroid = centroid,
                            panChange = pan,
                            zoomChange = zoom,
                            rotationChange = rotate,
                            mainPointer = mainPointer,
                            changes = pointerList
                        )
                    }
                    onGesture?.invoke(cropState.cropData)
                    mainPointer.consume()
                }
            )
        }

        val tapModifier = Modifier.pointerInput(*keys) {
            detectTapGestures(
                onDoubleTap = { offset: Offset ->
                    coroutineScope.launch {
                        zoomLevel = getNextZoomLevel(zoomLevel)
                        val newZoom = zoomOnDoubleTap(zoomLevel)
                        cropState.onDoubleTap(
                            offset = offset,
                            zoom = newZoom
                        ) {
                            onGestureEnd?.invoke(cropState.cropData)
                        }
                    }
                }
            )
        }

        val touchModifier = Modifier.pointerInput(*keys) {
            detectMotionEventsAsList(
                onDown = {
                    coroutineScope.launch {
                        cropState.onDown(it)
                        onDown?.invoke(cropState.cropData)
                    }
                },
                onMove = {
                    coroutineScope.launch {
                        cropState.onMove(it)
                        onMove?.invoke(cropState.cropData)
                    }
                },
                onUp = {
                    coroutineScope.launch {
                        cropState.onUp(it)
                        onUp?.invoke(cropState.cropData)
                    }
                }
            )
        }

        val graphicsModifier = Modifier.graphicsLayer {
            this.update(cropState)
        }

        this.then(
            Modifier
                .clipToBounds()
                .then(tapModifier)
                .then(transformModifier)
                .then(touchModifier)
                .then(graphicsModifier)
        )
    },
    inspectorInfo = debugInspectorInfo {
        name = "crop"
        // add name and value of each argument
        properties["keys"] = keys
        properties["onDown"] = onGestureStart
        properties["onMove"] = onGesture
        properties["onUp"] = onGestureEnd
    }
)

internal val CropState.DefaultOnDoubleTap: (ZoomLevel) -> Float
    get() = { zoomLevel: ZoomLevel ->
        when (zoomLevel) {
            ZoomLevel.Min -> 1f
            ZoomLevel.Mid -> 3f.coerceIn(zoomMin, zoomMax)
            ZoomLevel.Max -> 5f.coerceAtLeast(zoomMax)
        }
    }
