/*
 * Copyright 2022 usuiat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.engawapg.lib.zoomable

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.SuspendingPointerInputModifierNode
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.PointerInputModifierNode
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEach
import kotlinx.coroutines.launch

/**
 * Customized transform gesture detector.
 *
 * A caller of this function can choose if the pointer events will be consumed.
 * And the caller can implement [onGestureStart] and [onGestureEnd] event.
 *
 * @param canConsumeGesture Lambda that asks the caller whether the gesture can be consumed.
 * @param onGesture This lambda is called when [canConsumeGesture] returns true.
 * @param onGestureStart This lambda is called when a gesture starts.
 * @param onGestureEnd This lambda is called when a gesture ends.
 * @param onTap will be called when single tap is detected.
 * @param onDoubleTap will be called when double tap is detected.
 * @param enableOneFingerZoom If true, enable one finger zoom gesture, double tap followed by
 * vertical scrolling.
 */
private suspend fun PointerInputScope.detectTransformGestures(
    cancelIfZoomCanceled: Boolean,
    canConsumeGesture: (pan: Offset, zoom: Float) -> Boolean,
    onGesture: (centroid: Offset, pan: Offset, zoom: Float, timeMillis: Long) -> Unit,
    onGestureStart: () -> Unit = {},
    onGestureEnd: () -> Unit = {},
    onTap: (position: Offset) -> Unit = {},
    onDoubleTap: (position: Offset) -> Unit = {},
    enableOneFingerZoom: Boolean = true,
) = awaitEachGesture {
    val firstDown = awaitFirstDown(requireUnconsumed = false)
    onGestureStart()

    var firstUp: PointerInputChange = firstDown
    var hasMoved = false
    var isMultiTouch = false
    var isLongPressed = false
    var isCanceled = false
    forEachPointerEventUntilReleased(
        onCancel = { isCanceled = true },
    ) { event, isTouchSlopPast ->
        if (isTouchSlopPast) {
            val zoomChange = event.calculateZoom()
            val panChange = event.calculatePan()
            if (zoomChange != 1f || panChange != Offset.Zero) {
                val centroid = event.calculateCentroid(useCurrent = true)
                val timeMillis = event.changes[0].uptimeMillis
                if (canConsumeGesture(panChange, zoomChange)) {
                    onGesture(centroid, panChange, zoomChange, timeMillis)
                    event.consumePositionChanges()
                }
            }
            hasMoved = true
        }
        if (event.changes.size > 1) {
            isMultiTouch = true
        }
        firstUp = event.changes[0]
        val cancelGesture = cancelIfZoomCanceled && isMultiTouch && event.changes.size == 1
        !cancelGesture
    }

    if (firstUp.uptimeMillis - firstDown.uptimeMillis > viewConfiguration.longPressTimeoutMillis) {
        isLongPressed = true
    }

    val isTap = !hasMoved && !isMultiTouch && !isLongPressed && !isCanceled
    // Vertical scrolling following a double tap is treated as a zoom gesture.
    if (isTap) {
        val secondDown = awaitSecondDown(firstUp)
        if (secondDown == null) {
            onTap(firstUp.position)
        } else {
            var isDoubleTap = true
            var isSecondCanceled = false
            var secondUp: PointerInputChange = secondDown
            forEachPointerEventUntilReleased(
                onCancel = { isSecondCanceled = true }
            ) { event, isTouchSlopPast ->
                if (isTouchSlopPast) {
                    if (enableOneFingerZoom) {
                        val panChange = event.calculatePan()
                        val zoomChange = 1f + panChange.y * 0.004f
                        if (zoomChange != 1f) {
                            val centroid = event.calculateCentroid(useCurrent = true)
                            val timeMillis = event.changes[0].uptimeMillis
                            if (canConsumeGesture(Offset.Zero, zoomChange)) {
                                onGesture(centroid, Offset.Zero, zoomChange, timeMillis)
                                event.consumePositionChanges()
                            }
                        }
                    }
                    isDoubleTap = false
                }
                if (event.changes.size > 1) {
                    isDoubleTap = false
                }
                secondUp = event.changes[0]
                true
            }

            if (secondUp.uptimeMillis - secondDown.uptimeMillis > viewConfiguration.longPressTimeoutMillis) {
                isDoubleTap = false
            }

            if (isDoubleTap && !isSecondCanceled) {
                onDoubleTap(secondUp.position)
            }
        }
    }
    onGestureEnd()
}

/**
 * Invoke action for each PointerEvent until all pointers are released.
 *
 * @param onCancel Callback function that will be called if PointerEvents is consumed by other composable.
 * @param action Callback function that will be called every PointerEvents occur.
 */
private suspend fun AwaitPointerEventScope.forEachPointerEventUntilReleased(
    onCancel: () -> Unit,
    action: (event: PointerEvent, isTouchSlopPast: Boolean) -> Boolean,
) {
    val touchSlop = TouchSlop(viewConfiguration.touchSlop)
    do {
        val mainEvent = awaitPointerEvent(pass = PointerEventPass.Main)
        if (mainEvent.changes.fastAny { it.isConsumed }) {
            break
        }

        val isTouchSlopPast = touchSlop.isPast(mainEvent)
        val canContinue = action(mainEvent, isTouchSlopPast)
        if (!canContinue) {
            break
        }
        if (isTouchSlopPast) {
            continue
        }

        val finalEvent = awaitPointerEvent(pass = PointerEventPass.Final)
        if (finalEvent.changes.fastAny { it.isConsumed }) {
            onCancel()
            break
        }
    } while (mainEvent.changes.fastAny { it.pressed })
}

/**
 * Await second down or timeout from first up
 *
 * @param firstUp The first up event
 * @return If the second down event comes before timeout, returns it. If not, returns null.
 */
private suspend fun AwaitPointerEventScope.awaitSecondDown(
    firstUp: PointerInputChange
): PointerInputChange? = withTimeoutOrNull(viewConfiguration.doubleTapTimeoutMillis) {
    val minUptime = firstUp.uptimeMillis + viewConfiguration.doubleTapMinTimeMillis
    var change: PointerInputChange
    // The second tap doesn't count if it happens before DoubleTapMinTime of the first tap
    do {
        change = awaitFirstDown()
    } while (change.uptimeMillis < minUptime)
    change
}

/**
 * Consume event if the position is changed.
 */
private fun PointerEvent.consumePositionChanges() {
    changes.fastForEach {
        if (it.positionChanged()) {
            it.consume()
        }
    }
}

/**
 * Touch slop detector.
 *
 * This class holds accumulated zoom and pan value to see if touch slop is past.
 *
 * @param threshold Threshold of movement of gesture after touch down. If the movement exceeds this
 * value, it is judged to be a swipe or zoom gesture.
 */
private class TouchSlop(private val threshold: Float) {
    private var pan = Offset.Zero
    private var _isPast = false

    /**
     * Judge the touch slop is past.
     *
     * @param event Event that occurs this time.
     * @return True if the accumulated zoom or pan exceeds the threshold.
     */
    fun isPast(event: PointerEvent): Boolean {
        if (_isPast) {
            return true
        }

        if (event.changes.size > 1) {
            // If there are two or more fingers, we determine the touch slop is past immediately.
            _isPast = true
        } else {
            pan += event.calculatePan()
            _isPast = pan.getDistance() > threshold
        }

        return _isPast
    }
}

/**
 * [ScrollGesturePropagation] defines when [Modifier.zoomable] propagates scroll gestures to the
 * parent composable element.
 */
enum class ScrollGesturePropagation {

    /**
     * Propagates the scroll gesture to the parent composable element when the content is scrolled
     * to the edge and attempts to scroll further.
     */
    ContentEdge,

    /**
     * Propagates the scroll gesture to the parent composable element when the content is not zoomed.
     */
    NotZoomed,
}

/**
 * Modifier function that make the content zoomable.
 *
 * @param zoomState A [ZoomState] object.
 * @param zoomEnabled specifies if zoom behaviour is enabled or disabled. Even if this is false,
 * [onTap] and [onDoubleTap] will be called.
 * @param enableOneFingerZoom If true, enable one finger zoom gesture, double tap followed by
 * vertical scrolling.
 * @param scrollGesturePropagation specifies when scroll gestures are propagated to the parent
 * composable element.
 * @param onTap will be called when single tap is detected on the element.
 * @param onDoubleTap will be called when double tap is detected on the element. This is a suspend
 * function and called in a coroutine scope. The default is to toggle the scale between 1.0f and
 * 2.5f with animation.
 */
fun Modifier.zoomable(
    zoomState: ZoomState,
    zoomEnabled: Boolean = true,
    enableOneFingerZoom: Boolean = true,
    scrollGesturePropagation: ScrollGesturePropagation = ScrollGesturePropagation.ContentEdge,
    onTap: (position: Offset) -> Unit = {},
    onDoubleTap: suspend (position: Offset) -> Unit = { position ->
        if (zoomEnabled) zoomState.toggleScale(
            5f,
            position
        )
    },
): Modifier = this then ZoomableElement(
    zoomState = zoomState,
    zoomEnabled = zoomEnabled,
    enableOneFingerZoom = enableOneFingerZoom,
    snapBackEnabled = false,
    scrollGesturePropagation = scrollGesturePropagation,
    onTap = onTap,
    onDoubleTap = onDoubleTap,
)

fun Modifier.snapBackZoomable(
    zoomState: ZoomState,
    zoomEnabled: Boolean = true,
    onTap: (position: Offset) -> Unit = {},
    onDoubleTap: suspend (position: Offset) -> Unit = {},
): Modifier = this then ZoomableElement(
    zoomState = zoomState,
    zoomEnabled = zoomEnabled,
    enableOneFingerZoom = false,
    snapBackEnabled = true,
    scrollGesturePropagation = ScrollGesturePropagation.NotZoomed,
    onTap = onTap,
    onDoubleTap = onDoubleTap,
)

private data class ZoomableElement(
    val zoomState: ZoomState,
    val zoomEnabled: Boolean,
    val enableOneFingerZoom: Boolean,
    val snapBackEnabled: Boolean,
    val scrollGesturePropagation: ScrollGesturePropagation,
    val onTap: (position: Offset) -> Unit,
    val onDoubleTap: suspend (position: Offset) -> Unit,
) : ModifierNodeElement<ZoomableNode>() {
    override fun create(): ZoomableNode = ZoomableNode(
        zoomState = zoomState,
        zoomEnabled = zoomEnabled,
        enableOneFingerZoom = enableOneFingerZoom,
        snapBackEnabled = snapBackEnabled,
        scrollGesturePropagation = scrollGesturePropagation,
        onTap = onTap,
        onDoubleTap = onDoubleTap,
    )

    override fun update(node: ZoomableNode) {
        node.update(
            zoomState = zoomState,
            zoomEnabled = zoomEnabled,
            enableOneFingerZoom = enableOneFingerZoom,
            snapBackEnabled = snapBackEnabled,
            scrollGesturePropagation = scrollGesturePropagation,
            onTap = onTap,
            onDoubleTap = onDoubleTap,
        )
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "zoomable"
        properties["zoomState"] = zoomState
        properties["zoomEnabled"] = zoomEnabled
        properties["enableOneFingerZoom"] = enableOneFingerZoom
        properties["snapBackEnabled"] = snapBackEnabled
        properties["scrollGesturePropagation"] = scrollGesturePropagation
        properties["onTap"] = onTap
        properties["onDoubleTap"] = onDoubleTap
    }
}

private class ZoomableNode(
    var zoomState: ZoomState,
    var zoomEnabled: Boolean,
    var enableOneFingerZoom: Boolean,
    var snapBackEnabled: Boolean,
    var scrollGesturePropagation: ScrollGesturePropagation,
    var onTap: (position: Offset) -> Unit,
    var onDoubleTap: suspend (position: Offset) -> Unit,
) : PointerInputModifierNode, LayoutModifierNode, DelegatingNode() {
    var measuredSize = Size.Zero

    fun update(
        zoomState: ZoomState,
        zoomEnabled: Boolean,
        enableOneFingerZoom: Boolean,
        snapBackEnabled: Boolean,
        scrollGesturePropagation: ScrollGesturePropagation,
        onTap: (position: Offset) -> Unit,
        onDoubleTap: suspend (position: Offset) -> Unit,
    ) {
        if (this.zoomState != zoomState) {
            zoomState.setLayoutSize(measuredSize)
            this.zoomState = zoomState
        }
        this.zoomEnabled = zoomEnabled
        this.enableOneFingerZoom = enableOneFingerZoom
        this.scrollGesturePropagation = scrollGesturePropagation
        this.snapBackEnabled = snapBackEnabled
        this.onTap = onTap
        this.onDoubleTap = onDoubleTap
    }

    val pointerInputNode = delegate(
        SuspendingPointerInputModifierNode {
            detectTransformGestures(
                cancelIfZoomCanceled = snapBackEnabled,
                onGestureStart = {
                    resetConsumeGesture()
                    zoomState.startGesture()
                },
                canConsumeGesture = { pan, zoom ->
                    zoomEnabled && canConsumeGesture(pan, zoom)
                },
                onGesture = { centroid, pan, zoom, timeMillis ->
                    if (zoomEnabled) {
                        coroutineScope.launch {
                            zoomState.applyGesture(
                                pan = pan,
                                zoom = zoom,
                                position = centroid,
                                timeMillis = timeMillis,
                            )
                        }
                    }
                },
                onGestureEnd = {
                    coroutineScope.launch {
                        if (snapBackEnabled || zoomState.scale < 1f) {
                            zoomState.changeScale(1f, Offset.Zero)
                        } else {
                            zoomState.startFling()
                        }
                    }
                },
                onTap = onTap,
                onDoubleTap = { position ->
                    coroutineScope.launch {
                        onDoubleTap(position)
                    }
                },
                enableOneFingerZoom = enableOneFingerZoom,
            )
        }
    )

    private var consumeGesture: Boolean? = null

    private fun resetConsumeGesture() {
        consumeGesture = null
    }

    private fun canConsumeGesture(pan: Offset, zoom: Float): Boolean {
        val currentValue = consumeGesture
        if (currentValue != null) {
            return currentValue
        }

        val newValue = when {
            zoom != 1f -> true
            zoomState.scale == 1f -> false
            scrollGesturePropagation == ScrollGesturePropagation.NotZoomed -> true
            else -> zoomState.willChangeOffset(pan)
        }
        consumeGesture = newValue
        return newValue
    }

    override fun onPointerEvent(
        pointerEvent: PointerEvent,
        pass: PointerEventPass,
        bounds: IntSize
    ) {
        pointerInputNode.onPointerEvent(pointerEvent, pass, bounds)
    }

    override fun onCancelPointerInput() {
        pointerInputNode.onCancelPointerInput()
    }

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        val placeable = measurable.measure(constraints)
        measuredSize = IntSize(placeable.measuredWidth, placeable.measuredHeight).toSize()
        zoomState.setLayoutSize(measuredSize)
        return layout(placeable.width, placeable.height) {
            placeable.placeWithLayer(x = 0, y = 0) {
                scaleX = zoomState.scale
                scaleY = zoomState.scale
                translationX = zoomState.offsetX
                translationY = zoomState.offsetY
            }
        }
    }
}

/**
 * Toggle the scale between [targetScale] and 1.0f.
 *
 * @param targetScale Scale to be set if this function is called when the scale is 1.0f.
 * @param position Zoom around this point.
 * @param animationSpec The animation configuration.
 */
suspend fun ZoomState.toggleScale(
    targetScale: Float,
    position: Offset,
    animationSpec: AnimationSpec<Float> = spring(),
) {
    val newScale = if (scale == minScale) targetScale else minScale
    changeScale(newScale, position, animationSpec)
}