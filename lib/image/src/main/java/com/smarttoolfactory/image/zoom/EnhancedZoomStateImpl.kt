package com.smarttoolfactory.image.zoom

import androidx.compose.animation.core.exponentialDecay
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import com.smarttoolfactory.image.util.coerceIn
import com.smarttoolfactory.image.util.getCropRect
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 *  * State of the enhanced zoom that uses animations and fling
 *  to animate to bounds or have movement after pointers are up.
 *  Allows to change zoom, pan,  translate, or get current state by
 * calling methods on this object. To be hosted and passed to [Modifier.enhancedZoom].
 * Also contains [EnhancedZoomData] about current transformation area of Composable and
 * visible are of image being zoomed, rotated, or panned. If any animation
 * is going on current [isAnimationRunning] is true and [EnhancedZoomData] returns rectangle
 * that belongs to end of animation.
 *
 * @param imageSize size of the image that is zoomed or transformed. Size of the image
 * is required to get [Rect] of visible area after current transformation.
 * @param initialZoom zoom set initially
 * @param minZoom minimum zoom value
 * @param maxZoom maximum zoom value
 * @param fling when set to true dragging pointer builds up velocity. When last
 * pointer leaves Composable a movement invoked against friction till velocity drops down
 * to threshold
 * @param moveToBounds when set to true if image zoom is lower than initial zoom or
 * panned out of image boundaries moves back to bounds with animation.
 * ##Note
 * Currently rotating back to borders is not available
 * @param zoomable when set to true zoom is enabled
 * @param pannable when set to true pan is enabled
 * @param rotatable when set to true rotation is enabled
 * @param limitPan limits pan to bounds of parent Composable. Using this flag prevents creating
 * empty space on sides or edges of parent
 */
open class EnhancedZoomState(
    val imageSize: IntSize,
    initialZoom: Float = 1f,
    minZoom: Float = .5f,
    maxZoom: Float = 5f,
    fling: Boolean = false,
    moveToBounds: Boolean = true,
    zoomable: Boolean = true,
    pannable: Boolean = true,
    rotatable: Boolean = false,
    limitPan: Boolean = false
) : BaseEnhancedZoomState(
    initialZoom = initialZoom,
    minZoom = minZoom,
    maxZoom = maxZoom,
    fling = fling,
    moveToBounds = moveToBounds,
    zoomable = zoomable,
    pannable = pannable,
    rotatable = rotatable,
    limitPan = limitPan
) {

    private val rectDraw: Rect
        get() = Rect(
            offset = Offset.Zero,
            size = Size(size.width.toFloat(), size.height.toFloat())
        )

    val enhancedZoomData: EnhancedZoomData
        get() = EnhancedZoomData(
            zoom = animatableZoom.targetValue,
            pan = Offset(animatablePanX.targetValue, animatablePanY.targetValue),
            rotation = animatableRotation.targetValue,
            imageRegion = rectDraw,
            visibleRegion = calculateRectBounds()
        )

    private fun calculateRectBounds(): IntRect {
        val width = size.width
        val height = size.height

        val bounds = getBounds()
        val zoom = animatableZoom.targetValue
        val panX = animatablePanX.targetValue.coerceIn(-bounds.x, bounds.x)
        val panY = animatablePanY.targetValue.coerceIn(-bounds.y, bounds.y)

        // Offset for interpolating offset from (imageWidth/2,-imageWidth/2) interval
        // to (0, imageWidth) interval when
        // transform origin is TransformOrigin(0.5f,0.5f)
        val horizontalCenterOffset = width * (zoom - 1) / 2f
        val verticalCenterOffset = height * (zoom - 1) / 2f

        val offsetX = (horizontalCenterOffset - panX)
            .coerceAtLeast(0f) / zoom
        val offsetY = (verticalCenterOffset - panY)
            .coerceAtLeast(0f) / zoom

        val offset = Offset(offsetX, offsetY)

        return getCropRect(
            bitmapWidth = imageSize.width,
            bitmapHeight = imageSize.height,
            containerWidth = width.toFloat(),
            containerHeight = height.toFloat(),
            pan = offset,
            zoom = zoom,
            rectSelection = rectDraw
        )
    }
}

open class BaseEnhancedZoomState(
    initialZoom: Float = 1f,
    minZoom: Float = .5f,
    maxZoom: Float = 5f,
    val fling: Boolean = true,
    val moveToBounds: Boolean = true,
    zoomable: Boolean = true,
    pannable: Boolean = true,
    rotatable: Boolean = false,
    limitPan: Boolean = false,
    private val onChange: (zoom: Float, pan: Offset, rotation: Float) -> Unit = { _, _, _ -> }
) : ZoomState(
    initialZoom = initialZoom,
    initialRotation = 0f,
    minZoom = minZoom,
    maxZoom = maxZoom,
    zoomable = zoomable,
    pannable = pannable,
    rotatable = rotatable,
    limitPan = limitPan
) {
    private val velocityTracker = VelocityTracker()

    private var doubleTapped = false

    open suspend fun onGesture(
        centroid: Offset,
        pan: Offset,
        zoom: Float,
        rotation: Float,
        mainPointer: PointerInputChange,
        changes: List<PointerInputChange>
    ) = coroutineScope {

        doubleTapped = false

        onChange(zoom, pan, rotation)

        updateZoomState(
            centroid = centroid,
            zoomChange = zoom,
            panChange = pan,
            rotationChange = rotation
        )

        // Fling Gesture
        if (fling) {
            if (changes.size == 1) {
                addPosition(mainPointer.uptimeMillis, mainPointer.position)
            }
        }
    }

    open suspend fun onGestureStart() = coroutineScope {}

    open suspend fun onGestureEnd(onBoundsCalculated: () -> Unit) {

        // Gesture end might be called after second tap and we don't want to fling
        // or animate back to valid bounds when doubled tapped
        if (!doubleTapped) {

            if (fling && zoom > 1) {
                fling {
                    // We get target value on start instead of updating bounds after
                    // gesture has finished
                    onBoundsCalculated()
                }
            } else {
                onBoundsCalculated()
            }

            if (moveToBounds) {
                resetToValidBounds()
            }
        }
    }

    // Double Tap
    suspend fun onDoubleTap(
        pan: Offset = Offset.Zero,
        zoom: Float = 1f,
        rotation: Float = 0f,
        onAnimationEnd: () -> Unit
    ) {
        doubleTapped = true

        if (fling) {
            resetTracking()
        }
        resetWithAnimation(pan = pan, zoom = zoom, rotation = rotation)
        onAnimationEnd()
    }

    // TODO Add resetting back to bounds for rotated state as well
    /**
     * Resets to bounds with animation and resets tracking for fling animation
     */
    private suspend fun resetToValidBounds() {
        val zoom = zoom.coerceAtLeast(1f)
        val bounds = getBounds()
        val pan = pan.coerceIn(-bounds.x..bounds.x, -bounds.y..bounds.y)
        resetWithAnimation(pan = pan, zoom = zoom)
        resetTracking()
    }

    /*
        Fling gesture
     */
    private fun addPosition(timeMillis: Long, position: Offset) {
        velocityTracker.addPosition(
            timeMillis = timeMillis,
            position = position
        )
    }

    /**
     * Create a fling gesture when user removes finger from scree to have continuous movement
     * until [velocityTracker] speed reached to lower bound
     */
    private suspend fun fling(onFlingStart: () -> Unit) = coroutineScope {
        val velocityTracker = velocityTracker.calculateVelocity()
        val velocity = Offset(velocityTracker.x, velocityTracker.y)
        var flingStarted = false

        launch {
            animatablePanX.animateDecay(
                velocity.x,
                exponentialDecay(absVelocityThreshold = 20f),
                block = {
                    // This callback returns target value of fling gesture initially
                    if (!flingStarted) {
                        onFlingStart()
                        flingStarted = true
                    }
                }
            )
        }

        launch {
            animatablePanY.animateDecay(
                velocity.y,
                exponentialDecay(absVelocityThreshold = 20f),
                block = {
                    // This callback returns target value of fling gesture initially
                    if (!flingStarted) {
                        onFlingStart()
                        flingStarted = true
                    }
                }
            )
        }
    }

    private fun resetTracking() {
        velocityTracker.resetTracking()
    }
}
