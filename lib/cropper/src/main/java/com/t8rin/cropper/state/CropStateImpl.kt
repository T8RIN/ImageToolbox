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

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.unit.IntSize
import com.t8rin.cropper.ImageCropSnapshot
import com.t8rin.cropper.TouchRegion
import com.t8rin.cropper.model.AspectRatio
import com.t8rin.cropper.model.CropData
import com.t8rin.cropper.settings.CropProperties
import kotlinx.coroutines.CompletableDeferred

val CropState.cropData: CropData
    get() = CropData(
        zoom = animatableZoom.targetValue,
        pan = Offset(animatablePanX.targetValue, animatablePanY.targetValue),
        rotation = animatableRotation.targetValue,
        overlayRect = overlayRect,
        cropRect = cropRect
    )

internal val CropState.cropSnapshot: ImageCropSnapshot?
    get() {
        if (
            imageSize.width <= 0 ||
            imageSize.height <= 0 ||
            containerSize.width <= 0 ||
            containerSize.height <= 0 ||
            drawAreaSize.width <= 0 ||
            drawAreaSize.height <= 0
        ) {
            return null
        }

        val imageWidth = imageSize.width.toFloat().coerceAtLeast(1f)
        val imageHeight = imageSize.height.toFloat().coerceAtLeast(1f)
        val overlayRect = targetOverlayRect
        val cropRect = snapshotCropRect()
        if (cropRect.width <= 0f || cropRect.height <= 0f ||
            overlayRect.width <= 0f || overlayRect.height <= 0f
        ) {
            return null
        }

        return ImageCropSnapshot(
            normalizedCropRect = Rect(
                left = (cropRect.left / imageWidth).coerceIn(0f, 1f),
                top = (cropRect.top / imageHeight).coerceIn(0f, 1f),
                right = (cropRect.right / imageWidth).coerceIn(0f, 1f),
                bottom = (cropRect.bottom / imageHeight).coerceIn(0f, 1f)
            ),
            normalizedOverlayCenter = Offset(
                x = overlayRect.center.x / containerSize.width.coerceAtLeast(1),
                y = overlayRect.center.y / containerSize.height.coerceAtLeast(1)
            ),
            overlaySizeFraction = overlaySizeFraction(
                overlayRect = overlayRect,
                containerSize = containerSize
            ),
            containerSize = containerSize,
            rotation = animatableRotation.targetValue
        )
    }

internal suspend fun CropState.restoreCropSnapshot(snapshot: ImageCropSnapshot) {
    val geometry = calculateRestoreGeometry(
        snapshot = snapshot,
        imageSize = imageSize,
        containerSize = containerSize,
        drawAreaSize = drawAreaSize,
        overlayRatio = overlayRatio,
        minZoom = zoomMin,
        maxZoom = zoomMax
    ) ?: return

    snapOverlayRectTo(geometry.overlayRect)
    snapZoomTo(geometry.zoom)
    snapRotationTo(snapshot.rotation)
    snapPanXto(geometry.pan.x)
    snapPanYto(geometry.pan.y)
    drawAreaRect = updateImageDrawRectFromTransformation()
    resetTracking()
}

internal data class RestoredCropGeometry(
    val overlayRect: Rect,
    val zoom: Float,
    val pan: Offset
)

internal fun calculateRestoreGeometry(
    snapshot: ImageCropSnapshot,
    imageSize: IntSize,
    containerSize: IntSize,
    drawAreaSize: IntSize,
    overlayRatio: Float = 0.8f,
    minZoom: Float = 0.5f,
    maxZoom: Float = 20f
): RestoredCropGeometry? {
    if (
        imageSize.width <= 0 ||
        imageSize.height <= 0 ||
        containerSize.width <= 0 ||
        containerSize.height <= 0 ||
        drawAreaSize.width <= 0 ||
        drawAreaSize.height <= 0
    ) {
        return null
    }

    val normalizedCropRect = snapshot.normalizedCropRect.coerceToImageBounds()
    if (normalizedCropRect.width <= 0f || normalizedCropRect.height <= 0f) return null

    val containerWidth = containerSize.width.toFloat()
    val containerHeight = containerSize.height.toFloat()
    val cropWidthAtZoomOne = normalizedCropRect.width * drawAreaSize.width
    val cropHeightAtZoomOne = normalizedCropRect.height * drawAreaSize.height
    if (cropWidthAtZoomOne <= 0f || cropHeightAtZoomOne <= 0f) return null

    // Geometry is stored in image coordinates and restored relative to both viewport axes.
    // A numeric zoom value is tied to the old draw area, so carrying it across a rotation can
    // skew the frame even when undo and redo use the same snapshot.
    val viewportFillFraction = snapshot.overlaySizeFraction.coerceIn(0.1f, 1f)
    val zoom = minOf(
        containerWidth * viewportFillFraction / cropWidthAtZoomOne,
        containerHeight * viewportFillFraction / cropHeightAtZoomOne,
        maxZoom.coerceAtLeast(minZoom)
    ).coerceAtLeast(minZoom.coerceAtLeast(0.01f))
    if (!zoom.isFinite() || zoom <= 0f) return null

    val overlayWidth = cropWidthAtZoomOne * zoom
    val overlayHeight = cropHeightAtZoomOne * zoom
    val sourceContainerSize = snapshot.containerSize
    val keepAbsoluteCenter = sourceContainerSize.width == containerSize.width &&
            sourceContainerSize.height > 0
    val requestedCenter = if (keepAbsoluteCenter) {
        Offset(
            x = snapshot.normalizedOverlayCenter.x.coerceIn(0f, 1f) *
                    sourceContainerSize.width,
            y = snapshot.normalizedOverlayCenter.y.coerceIn(0f, 1f) *
                    sourceContainerSize.height
        )
    } else {
        Offset(
            x = snapshot.normalizedOverlayCenter.x.coerceIn(0f, 1f) * containerWidth,
            y = snapshot.normalizedOverlayCenter.y.coerceIn(0f, 1f) * containerHeight
        )
    }
    val overlayCenter = Offset(
        x = constrainedCenter(requestedCenter.x, overlayWidth, containerWidth),
        y = constrainedCenter(requestedCenter.y, overlayHeight, containerHeight)
    )
    val overlayRect = Rect(
        left = overlayCenter.x - overlayWidth / 2f,
        top = overlayCenter.y - overlayHeight / 2f,
        right = overlayCenter.x + overlayWidth / 2f,
        bottom = overlayCenter.y + overlayHeight / 2f
    )

    val drawWidth = drawAreaSize.width * zoom
    val drawHeight = drawAreaSize.height * zoom
    val drawLeft = overlayRect.left - normalizedCropRect.left * drawWidth
    val drawTop = overlayRect.top - normalizedCropRect.top * drawHeight

    return RestoredCropGeometry(
        overlayRect = overlayRect,
        zoom = zoom,
        pan = Offset(
            x = drawLeft - (containerWidth - drawWidth) / 2f,
            y = drawTop - (containerHeight - drawHeight) / 2f
        )
    )
}

private fun overlaySizeFraction(
    overlayRect: Rect,
    containerSize: IntSize
): Float {
    if (overlayRect.width <= 0f || overlayRect.height <= 0f) return 0.8f

    return maxOf(
        overlayRect.width / containerSize.width.coerceAtLeast(1),
        overlayRect.height / containerSize.height.coerceAtLeast(1)
    ).coerceIn(0.1f, 1f)
}

private fun constrainedCenter(
    requested: Float,
    size: Float,
    containerSize: Float
): Float {
    if (size >= containerSize) return containerSize / 2f
    return requested.coerceIn(size / 2f, containerSize - size / 2f)
}

private fun Rect.coerceToImageBounds(): Rect = Rect(
    left = left.coerceIn(0f, 1f),
    top = top.coerceIn(0f, 1f),
    right = right.coerceIn(0f, 1f),
    bottom = bottom.coerceIn(0f, 1f)
)

/**
 * Base class for crop operations. Any class that extends this class gets access to pan, zoom,
 * rotation values and animations via [TransformState], fling and moving back to bounds animations.
 * @param imageSize size of the **Bitmap**
 * @param containerSize size of the Composable that draws **Bitmap**. This is full size
 * of the Composable. [drawAreaSize] can be smaller than [containerSize] initially based
 * on content scale of Image composable
 * @param drawAreaSize size of the area that **Bitmap** is drawn
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
abstract class CropState internal constructor(
    imageSize: IntSize,
    containerSize: IntSize,
    drawAreaSize: IntSize,
    maxZoom: Float,
    internal var fling: Boolean = true,
    internal var aspectRatio: AspectRatio,
    internal var overlayRatio: Float,
    zoomable: Boolean = true,
    pannable: Boolean = true,
    rotatable: Boolean = false,
    limitPan: Boolean = false
) : TransformState(
    imageSize = imageSize,
    containerSize = containerSize,
    drawAreaSize = drawAreaSize,
    initialZoom = 1f,
    initialRotation = 0f,
    maxZoom = maxZoom,
    zoomable = zoomable,
    pannable = pannable,
    rotatable = rotatable,
    limitPan = limitPan
) {

    private val animatableRectOverlay = Animatable(
        getOverlayFromAspectRatio(
            containerSize.width.toFloat(),
            containerSize.height.toFloat(),
            drawAreaSize.width.toFloat(),
            aspectRatio,
            overlayRatio
        ),
        Rect.VectorConverter
    )

    val overlayRect: Rect
        get() = animatableRectOverlay.value

    internal val targetOverlayRect: Rect
        get() = animatableRectOverlay.targetValue

    /**
     * Returns crop geometry from one coherent set of target values. During a configuration
     * change Compose can expose a new [CropState] while its rendered draw rectangle is still
     * catching up with the target pan and zoom. Saving [drawAreaRect] in that interval creates
     * a snapshot that neither undo nor redo can repair.
     */
    internal fun snapshotCropRect(): Rect = getCropRectangle(
        bitmapWidth = imageSize.width,
        bitmapHeight = imageSize.height,
        drawAreaRect = updateImageDrawRectFromTransformation(),
        overlayRect = animatableRectOverlay.targetValue
    )

    var cropRect: Rect = Rect.Zero
        get() = getCropRectangle(
            imageSize.width,
            imageSize.height,
            drawAreaRect,
            animatableRectOverlay.targetValue
        )
        private set


    private var initialized: Boolean = false
    private var initializationStarted: Boolean = false
    private val initialization = CompletableDeferred<Unit>()

    /**
     * Region of touch inside, corners of or outside of overlay rectangle
     */
    var touchRegion by mutableStateOf(TouchRegion.None)

    internal suspend fun init() {
        if (initialized) return
        if (initializationStarted) {
            initialization.await()
            return
        }

        initializationStarted = true
        try {
            // When initial aspect ratio doesn't match drawable area
            // overlay gets updated so updates draw area as well
            animateTransformationToOverlayBounds(overlayRect, animate = false)
            initialized = true
        } finally {
            initialization.complete(Unit)
        }
    }

    internal suspend fun awaitInitialization() = initialization.await()

    /**
     * Update properties of [CropState] and animate to valid intervals if required
     */
    internal open suspend fun updateProperties(
        cropProperties: CropProperties,
        forceUpdate: Boolean = false,
        animate: Boolean = true
    ) {

        if (!initialized) return

        fling = cropProperties.fling
        pannable = cropProperties.pannable
        zoomable = cropProperties.zoomable
        rotatable = cropProperties.rotatable

        val maxZoom = cropProperties.maxZoom

        // Update overlay rectangle
        val aspectRatio = cropProperties.aspectRatio

        // Ratio of overlay to screen
        val overlayRatio = cropProperties.overlayRatio

        if (
            this.aspectRatio.value != aspectRatio.value ||
            maxZoom != zoomMax ||
            this.overlayRatio != overlayRatio ||
            forceUpdate
        ) {
            this.aspectRatio = aspectRatio
            this.overlayRatio = overlayRatio

            zoomMax = maxZoom
            animatableZoom.updateBounds(zoomMin, zoomMax)

            val currentZoom = if (zoom > zoomMax) zoomMax else zoom

            // Set new zoom
            snapZoomTo(currentZoom)

            // Calculate new region of image is drawn. It can be drawn left of 0 and right
            // of container width depending on transformation
            drawAreaRect = updateImageDrawRectFromTransformation()

            // Update overlay rectangle based on current draw area and new aspect ratio
            val targetOverlayRect = getOverlayFromAspectRatio(
                    containerSize.width.toFloat(),
                    containerSize.height.toFloat(),
                    drawAreaSize.width.toFloat(),
                    aspectRatio,
                    overlayRatio
                )
            if (animate) {
                animateOverlayRectTo(targetOverlayRect)
            } else {
                snapOverlayRectTo(targetOverlayRect)
            }
        }

        // Animate zoom, pan, rotation to move draw area to cover overlay rect
        // inside draw area rect
        animateTransformationToOverlayBounds(overlayRect, animate = animate)
    }

    /**
     * Animate overlay rectangle to target value
     */
    internal suspend fun animateOverlayRectTo(
        rect: Rect,
        animationSpec: AnimationSpec<Rect> = tween(250)
    ) {
        animatableRectOverlay.animateTo(
            targetValue = rect,
            animationSpec = animationSpec
        )
    }

    /**
     * Snap overlay rectangle to target value
     */
    internal suspend fun snapOverlayRectTo(rect: Rect) {
        animatableRectOverlay.snapTo(rect)
    }

    /*
        Touch gestures
     */
    internal abstract suspend fun onDown(change: PointerInputChange)

    internal abstract suspend fun onMove(changes: List<PointerInputChange>)

    internal abstract suspend fun onUp(change: PointerInputChange)

    /*
        Transform gestures
     */
    internal abstract suspend fun onGesture(
        centroid: Offset,
        panChange: Offset,
        zoomChange: Float,
        rotationChange: Float,
        mainPointer: PointerInputChange,
        changes: List<PointerInputChange>
    )

    internal abstract suspend fun onGestureStart()

    internal abstract suspend fun onGestureEnd(onBoundsCalculated: () -> Unit)

    // Double Tap
    internal abstract suspend fun onDoubleTap(
        offset: Offset,
        zoom: Float = 1f,
        onAnimationEnd: () -> Unit
    )

    /**
     * Check if area that image is drawn covers [overlayRect]
     */
    internal fun isOverlayInImageDrawBounds(): Boolean {
        return drawAreaRect.left <= overlayRect.left &&
                drawAreaRect.top <= overlayRect.top &&
                drawAreaRect.right >= overlayRect.right &&
                drawAreaRect.bottom >= overlayRect.bottom
    }

    /**
     * Check if [rect] is inside container bounds
     */
    internal fun isRectInContainerBounds(rect: Rect): Boolean {
        return rect.left >= 0 &&
                rect.right <= containerSize.width &&
                rect.top >= 0 &&
                rect.bottom <= containerSize.height
    }

    /**
     * Update rectangle for area that image is drawn. This rect changes when zoom and
     * pan changes and position of image changes on screen as result of transformation.
     *
     * This function is called
     *
     * * when [onGesture] is called to update rect when zoom or pan changes
     *  and if [fling] is true just after **fling** gesture starts with target
     *  value in  [StaticCropState].
     *
     *  * when [updateProperties] is called in [CropState]
     *
     *  * when [onUp] is called in [DynamicCropState] to match [overlayRect] that could be
     *  changed and animated if it's out of [containerSize] bounds or its grow
     *  bigger than previous size
     */
    internal fun updateImageDrawRectFromTransformation(): Rect {
        val containerWidth = containerSize.width
        val containerHeight = containerSize.height

        val originalDrawWidth = drawAreaSize.width
        val originalDrawHeight = drawAreaSize.height

        val panX = animatablePanX.targetValue
        val panY = animatablePanY.targetValue

        val left = (containerWidth - originalDrawWidth) / 2
        val top = (containerHeight - originalDrawHeight) / 2

        val zoom = animatableZoom.targetValue

        val newWidth = originalDrawWidth * zoom
        val newHeight = originalDrawHeight * zoom

        return Rect(
            offset = Offset(
                left - (newWidth - originalDrawWidth) / 2 + panX,
                top - (newHeight - originalDrawHeight) / 2 + panY,
            ),
            size = Size(newWidth, newHeight)
        )
    }

    /**
     * Resets to bounds with animation and resets tracking for fling animation.
     * Changes pan, zoom and rotation to valid bounds based on [drawAreaRect] and [overlayRect]
     */
    internal suspend fun animateTransformationToOverlayBounds(
        overlayRect: Rect,
        animate: Boolean,
        animationSpec: AnimationSpec<Float> = tween(250)
    ) {
        // Keep current zoom
        // val zoom = zoom.coerceAtLeast(1f)

        // Calculate new pan based on overlay
        val newDrawAreaRect = calculateValidImageDrawRect(overlayRect, drawAreaRect)

        val newZoom =
            calculateNewZoom(oldRect = drawAreaRect, newRect = newDrawAreaRect, zoom = zoom)

        val leftChange = newDrawAreaRect.left - drawAreaRect.left
        val topChange = newDrawAreaRect.top - drawAreaRect.top

        val widthChange = newDrawAreaRect.width - drawAreaRect.width
        val heightChange = newDrawAreaRect.height - drawAreaRect.height

        val panXChange = leftChange + widthChange / 2
        val panYChange = topChange + heightChange / 2

        val newPanX = pan.x + panXChange
        val newPanY = pan.y + panYChange

        // Update draw area based on new pan and zoom values
        drawAreaRect = newDrawAreaRect

        if (animate) {
            resetWithAnimation(
                pan = Offset(newPanX, newPanY),
                zoom = newZoom,
                animationSpec = animationSpec
            )
        } else {
            snapPanXto(newPanX)
            snapPanYto(newPanY)
            snapZoomTo(newZoom)
        }

        resetTracking()
    }

    /**
     * If new overlay is bigger, when crop type is dynamic, we need to increase zoom at least
     * size of bigger dimension for image draw area([drawAreaRect]) to cover overlay([overlayRect])
     */
    private fun calculateNewZoom(oldRect: Rect, newRect: Rect, zoom: Float): Float {

        if (oldRect.size == Size.Zero || newRect.size == Size.Zero) return zoom

        val widthChange = (newRect.width / oldRect.width)
            .coerceAtLeast(1f)
        val heightChange = (newRect.height / oldRect.height)
            .coerceAtLeast(1f)

        return widthChange.coerceAtLeast(heightChange) * zoom
    }

    /**
     * Calculate valid position for image draw rectangle when pointer is up. Overlay rectangle
     * should fit inside draw image rectangle to have valid bounds when calculation is completed.
     *
     * @param rectOverlay rectangle of overlay that is used for cropping
     * @param rectDrawArea rectangle of image that is being drawn
     */
    private fun calculateValidImageDrawRect(rectOverlay: Rect, rectDrawArea: Rect): Rect {

        var width = rectDrawArea.width
        var height = rectDrawArea.height

        if (width < rectOverlay.width) {
            width = rectOverlay.width
        }

        if (height < rectOverlay.height) {
            height = rectOverlay.height
        }

        var rectImageArea = Rect(offset = rectDrawArea.topLeft, size = Size(width, height))

        if (rectImageArea.left > rectOverlay.left) {
            rectImageArea = rectImageArea.translate(rectOverlay.left - rectImageArea.left, 0f)
        }

        if (rectImageArea.right < rectOverlay.right) {
            rectImageArea = rectImageArea.translate(rectOverlay.right - rectImageArea.right, 0f)
        }

        if (rectImageArea.top > rectOverlay.top) {
            rectImageArea = rectImageArea.translate(0f, rectOverlay.top - rectImageArea.top)
        }

        if (rectImageArea.bottom < rectOverlay.bottom) {
            rectImageArea = rectImageArea.translate(0f, rectOverlay.bottom - rectImageArea.bottom)
        }

        return rectImageArea
    }

    /**
     * Create [Rect] to draw overlay based on selected aspect ratio
     */
    internal fun getOverlayFromAspectRatio(
        containerWidth: Float,
        containerHeight: Float,
        drawAreaWidth: Float,
        aspectRatio: AspectRatio,
        coefficient: Float
    ): Rect {

        if (aspectRatio == AspectRatio.Original) {
            val imageAspectRatio = imageSize.width.toFloat() / imageSize.height.toFloat()

            // Maximum width and height overlay rectangle can be measured with
            val overlayWidthMax = drawAreaWidth.coerceAtMost(containerWidth * coefficient)
            val overlayHeightMax =
                (overlayWidthMax / imageAspectRatio).coerceAtMost(containerHeight * coefficient)

            val offsetX = (containerWidth - overlayWidthMax) / 2f
            val offsetY = (containerHeight - overlayHeightMax) / 2f

            return Rect(
                offset = Offset(offsetX, offsetY),
                size = Size(overlayWidthMax, overlayHeightMax)
            )
        }

        val overlayWidthMax = containerWidth * coefficient
        val overlayHeightMax = containerHeight * coefficient

        val aspectRatioValue = aspectRatio.value

        var width = overlayWidthMax
        var height = overlayWidthMax / aspectRatioValue

        if (height > overlayHeightMax) {
            height = overlayHeightMax
            width = height * aspectRatioValue
        }

        val offsetX = (containerWidth - width) / 2f
        val offsetY = (containerHeight - height) / 2f

        return Rect(offset = Offset(offsetX, offsetY), size = Size(width, height))
    }

    /**
     * Get crop rectangle
     */
    private fun getCropRectangle(
        bitmapWidth: Int,
        bitmapHeight: Int,
        drawAreaRect: Rect,
        overlayRect: Rect
    ): Rect {

        if (drawAreaRect == Rect.Zero || overlayRect == Rect.Zero) return Rect(
            offset = Offset.Zero,
            Size(bitmapWidth.toFloat(), bitmapHeight.toFloat())
        )

        // Calculate latest image draw area based on overlay position
        // This is valid rectangle that contains crop area inside overlay
        val newRect = calculateValidImageDrawRect(overlayRect, drawAreaRect)

        val overlayWidth = overlayRect.width
        val overlayHeight = overlayRect.height

        val drawAreaWidth = newRect.width
        val drawAreaHeight = newRect.height

        val widthRatio = overlayWidth / drawAreaWidth
        val heightRatio = overlayHeight / drawAreaHeight

        val diffLeft = overlayRect.left - newRect.left
        val diffTop = overlayRect.top - newRect.top

        val croppedBitmapLeft = (diffLeft * (bitmapWidth / drawAreaWidth))
        val croppedBitmapTop = (diffTop * (bitmapHeight / drawAreaHeight))

        val croppedBitmapWidth = bitmapWidth * widthRatio
        val croppedBitmapHeight = bitmapHeight * heightRatio

        return Rect(
            offset = Offset(croppedBitmapLeft, croppedBitmapTop),
            size = Size(croppedBitmapWidth, croppedBitmapHeight)
        )
    }
}
