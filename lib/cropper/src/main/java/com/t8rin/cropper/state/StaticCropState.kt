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

package com.t8rin.cropper.state

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.unit.IntSize
import com.t8rin.cropper.model.AspectRatio
import kotlinx.coroutines.coroutineScope

/**
 *  * State for cropper with dynamic overlay. When this state is selected instead of overlay
 *  image is moved while overlay is stationary.
 *
 * @param imageSize size of the **Bitmap**
 * @param containerSize size of the Composable that draws **Bitmap**
 * @param maxZoom maximum zoom value
 * @param fling when set to true dragging pointer builds up velocity. When last
 * pointer leaves Composable a movement invoked against friction till velocity drops below
 * to threshold
 * @param zoomable when set to true zoom is enabled
 * @param pannable when set to true pan is enabled
 * @param rotatable when set to true rotation is enabled
 * @param limitPan limits pan to bounds of parent Composable. Using this flag prevents creating
 * empty space on sides or edges of parent
 */
class StaticCropState internal constructor(
    imageSize: IntSize,
    containerSize: IntSize,
    drawAreaSize: IntSize,
    aspectRatio: AspectRatio,
    overlayRatio: Float,
    maxZoom: Float = 5f,
    fling: Boolean = false,
    zoomable: Boolean = true,
    pannable: Boolean = true,
    rotatable: Boolean = false,
    limitPan: Boolean = false
) : CropState(
    imageSize = imageSize,
    containerSize = containerSize,
    drawAreaSize = drawAreaSize,
    aspectRatio = aspectRatio,
    overlayRatio = overlayRatio,
    maxZoom = maxZoom,
    fling = fling,
    zoomable = zoomable,
    pannable = pannable,
    rotatable = rotatable,
    limitPan = limitPan
) {

    override suspend fun onDown(change: PointerInputChange) = Unit
    override suspend fun onMove(changes: List<PointerInputChange>) = Unit
    override suspend fun onUp(change: PointerInputChange) = Unit

    private var doubleTapped = false

    /*
        Transform gestures
    */
    override suspend fun onGesture(
        centroid: Offset,
        panChange: Offset,
        zoomChange: Float,
        rotationChange: Float,
        mainPointer: PointerInputChange,
        changes: List<PointerInputChange>
    ) = coroutineScope {
        doubleTapped = false

        updateTransformState(
            centroid = centroid,
            zoomChange = zoomChange,
            panChange = panChange,
            rotationChange = rotationChange
        )

        // Update image draw rectangle based on pan, zoom or rotation change
        drawAreaRect = updateImageDrawRectFromTransformation()

        // Fling Gesture
        if (pannable && fling) {
            if (changes.size == 1) {
                addPosition(mainPointer.uptimeMillis, mainPointer.position)
            }
        }
    }

    override suspend fun onGestureStart() = coroutineScope {}

    override suspend fun onGestureEnd(onBoundsCalculated: () -> Unit) {

        // Gesture end might be called after second tap and we don't want to fling
        // or animate back to valid bounds when doubled tapped
        if (!doubleTapped) {

            if (pannable && fling && zoom > 1) {
                fling {
                    // We get target value on start instead of updating bounds after
                    // gesture has finished
                    drawAreaRect = updateImageDrawRectFromTransformation()
                    onBoundsCalculated()
                }
            } else {
                onBoundsCalculated()
            }

            animateTransformationToOverlayBounds(overlayRect, animate = true)
        }
    }

    // Double Tap
    override suspend fun onDoubleTap(
        offset: Offset,
        zoom: Float,
        onAnimationEnd: () -> Unit
    ) {
        doubleTapped = true

        if (fling) {
            resetTracking()
        }
        resetWithAnimation(pan = pan, zoom = zoom, rotation = rotation)
        drawAreaRect = updateImageDrawRectFromTransformation()
        animateTransformationToOverlayBounds(overlayRect, true)
        onAnimationEnd()
    }
}
