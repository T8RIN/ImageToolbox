package com.smarttoolfactory.cropper.state

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * State of the pan, zoom and rotation. Allows to change zoom, pan via [Animatable]
 * objects' [Animatable.animateTo], [Animatable.snapTo].
 * @param initialZoom initial zoom level
 * @param initialRotation initial angle in degrees
 * @param minZoom minimum zoom
 * @param maxZoom maximum zoom
 * @param zoomable when set to true zoom is enabled
 * @param pannable when set to true pan is enabled
 * @param rotatable when set to true rotation is enabled
 * @param limitPan limits pan to bounds of parent Composable. Using this flag prevents creating
 * empty space on sides or edges of parent.
 *
 */
@Stable
open class TransformState(
    internal val imageSize: IntSize,
    val containerSize: IntSize,
    val drawAreaSize: IntSize,
    initialZoom: Float = 1f,
    initialRotation: Float = 0f,
    minZoom: Float = 1f,
    maxZoom: Float = 10f,
    internal var zoomable: Boolean = true,
    internal var pannable: Boolean = true,
    internal var rotatable: Boolean = true,
    internal var limitPan: Boolean = false
) {

    var drawAreaRect: Rect by mutableStateOf(
        Rect(
            offset = Offset(
                x = ((containerSize.width - drawAreaSize.width) / 2).toFloat(),
                y = ((containerSize.height - drawAreaSize.height) / 2).toFloat()
            ),
            size = Size(drawAreaSize.width.toFloat(), drawAreaSize.height.toFloat())
        )
    )

    internal val zoomMin = minZoom.coerceAtLeast(1f)
    internal var zoomMax = maxZoom.coerceAtLeast(1f)
    private val zoomInitial = initialZoom.coerceIn(zoomMin, zoomMax)
    private val rotationInitial = initialRotation % 360

    internal val animatablePanX = Animatable(0f)
    internal val animatablePanY = Animatable(0f)
    internal val animatableZoom = Animatable(zoomInitial)
    internal val animatableRotation = Animatable(rotationInitial)

    private val velocityTracker = VelocityTracker()

    init {
        animatableZoom.updateBounds(zoomMin, zoomMax)
        require(zoomMax >= zoomMin)
    }

    val pan: Offset
        get() = Offset(animatablePanX.value, animatablePanY.value)

    val zoom: Float
        get() = animatableZoom.value

    val rotation: Float
        get() = animatableRotation.value

    val isZooming: Boolean
        get() = animatableZoom.isRunning

    val isPanning: Boolean
        get() = animatablePanX.isRunning || animatablePanY.isRunning

    val isRotating: Boolean
        get() = animatableRotation.isRunning

    val isAnimationRunning: Boolean
        get() = isZooming || isPanning || isRotating

    internal open fun updateBounds(lowerBound: Offset?, upperBound: Offset?) {
        animatablePanX.updateBounds(lowerBound?.x, upperBound?.x)
        animatablePanY.updateBounds(lowerBound?.y, upperBound?.y)
    }

    /**
     * Update centroid, pan, zoom and rotation of this state when transform gestures are
     * invoked with one or multiple pointers
     */
    internal open suspend fun updateTransformState(
        centroid: Offset,
        panChange: Offset,
        zoomChange: Float,
        rotationChange: Float = 1f,
    ) {
        val newZoom = (this.zoom * zoomChange).coerceIn(zoomMin, zoomMax)

        snapZoomTo(newZoom)
        val newRotation = if (rotatable) {
            this.rotation + rotationChange
        } else {
            0f
        }
        snapRotationTo(newRotation)

        if (pannable) {
            val newPan = this.pan + panChange.times(this.zoom)
            snapPanXto(newPan.x)
            snapPanYto(newPan.y)
        }
    }

    /**
     * Reset [pan], [zoom] and [rotation] with animation.
     */
    internal suspend fun resetWithAnimation(
        pan: Offset = Offset.Zero,
        zoom: Float = 1f,
        rotation: Float = 0f,
        animationSpec: AnimationSpec<Float> = tween(400)
    ) = coroutineScope {
        launch { animatePanXto(pan.x, animationSpec) }
        launch { animatePanYto(pan.y, animationSpec) }
        launch { animateZoomTo(zoom, animationSpec) }
        launch { animateRotationTo(rotation, animationSpec) }
    }

    internal suspend fun animatePanXto(
        panX: Float,
        animationSpec: AnimationSpec<Float> = tween(400)
    ) {
        if (pannable && pan.x != panX) {
            animatablePanX.animateTo(panX, animationSpec)
        }
    }

    internal suspend fun animatePanYto(
        panY: Float,
        animationSpec: AnimationSpec<Float> = tween(400)
    ) {
        if (pannable && pan.y != panY) {
            animatablePanY.animateTo(panY, animationSpec)
        }
    }

    internal suspend fun animateZoomTo(
        zoom: Float,
        animationSpec: AnimationSpec<Float> = tween(400)
    ) {
        if (zoomable && this.zoom != zoom) {
            val newZoom = zoom.coerceIn(zoomMin, zoomMax)
            animatableZoom.animateTo(newZoom, animationSpec)
        }
    }

    internal suspend fun animateRotationTo(
        rotation: Float,
        animationSpec: AnimationSpec<Float> = tween(400)
    ) {
        if (rotatable && this.rotation != rotation) {
            animatableRotation.animateTo(rotation, animationSpec)
        }
    }

    internal suspend fun snapPanXto(panX: Float) {
        if (pannable) {
            animatablePanX.snapTo(panX)
        }
    }

    internal suspend fun snapPanYto(panY: Float) {
        if (pannable) {
            animatablePanY.snapTo(panY)
        }
    }

    internal suspend fun snapZoomTo(zoom: Float) {
        if (zoomable) {
            animatableZoom.snapTo(zoom.coerceIn(zoomMin, zoomMax))
        }
    }

    internal suspend fun snapRotationTo(rotation: Float) {
        if (rotatable) {
            animatableRotation.snapTo(rotation)
        }
    }

    /*
    Fling gesture
 */
    internal fun addPosition(timeMillis: Long, position: Offset) {
        velocityTracker.addPosition(
            timeMillis = timeMillis,
            position = position
        )
    }

    /**
     * Create a fling gesture when user removes finger from scree to have continuous movement
     * until [velocityTracker] speed reached to lower bound
     */
    internal suspend fun fling(onFlingStart: () -> Unit) = coroutineScope {
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

    internal fun resetTracking() {
        velocityTracker.resetTracking()
    }
}
