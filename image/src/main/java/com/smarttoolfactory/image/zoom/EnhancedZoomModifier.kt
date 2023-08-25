package com.smarttoolfactory.image.zoom

import androidx.compose.foundation.gestures.detectTapGestures
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
import com.smarttoolfactory.gesture.detectTransformGestures
import com.smarttoolfactory.image.util.getNextZoomLevel
import com.smarttoolfactory.image.util.update
import kotlinx.coroutines.launch

/**
 * Modifier that zooms in or out of Composable set to. This zoom modifier has option
 * to move back to bounds with an animation or option to have fling gesture when user removes
 * from screen while velocity is higher than threshold to have smooth touch effect.
 *
 * @param key is used for [Modifier.pointerInput] to restart closure when any keys assigned
 * change
 * @param clip when set to true clips to parent bounds. Anything outside parent bounds is not
 * drawn
 * empty space on sides or edges of parent.
 * @param enhancedZoomState State of the zoom that contains option to set initial, min, max zoom,
 * enabling rotation, pan or zoom and contains current [EnhancedZoomData]
 * event propagations. Also contains [Rect] of visible area based on pan, zoom and rotation
 * @param zoomOnDoubleTap lambda that returns current [ZoomLevel] and based on current level
 * enables developer to define zoom on double tap gesture
 * @param enabled lambda can be used selectively enable or disable pan and intercepting with
 * scroll, drag or lists or pagers using current zoom, pan or rotation values
 * @param onGestureStart callback to to notify gesture has started and return current
 * [EnhancedZoomData]  of this modifier
 * @param onGesture callback to notify about ongoing gesture and return current
 * [EnhancedZoomData]  of this modifier
 * @param onGestureEnd callback to notify that gesture finished return current
 * [EnhancedZoomData]  of this modifier
 */
fun Modifier.enhancedZoom(
    key: Any? = Unit,
    clip: Boolean = true,
    enhancedZoomState: EnhancedZoomState,
    enabled: (Float, Offset, Float) -> Boolean = DefaultEnabled,
    zoomOnDoubleTap: (ZoomLevel) -> Float = enhancedZoomState.DefaultOnDoubleTap,
    onGestureStart: ((EnhancedZoomData) -> Unit)? = null,
    onGesture: ((EnhancedZoomData) -> Unit)? = null,
    onGestureEnd: ((EnhancedZoomData) -> Unit)? = null,
) = composed(

    factory = {

        val coroutineScope = rememberCoroutineScope()

        // Current Zoom level
        var zoomLevel by remember { mutableStateOf(ZoomLevel.Min) }

        // Whether panning should be limited to bounds of gesture area or not
        val boundPan = enhancedZoomState.limitPan && !enhancedZoomState.rotatable

        // If we bound to touch area or clip is true Modifier.clipToBounds is used
        val clipToBounds = (clip || boundPan)

        val transformModifier = Modifier.pointerInput(key) {
            // Pass size of this Composable this Modifier is attached for constraining operations
            // inside this bounds
            enhancedZoomState.size = this.size
            detectTransformGestures(
                consume = false,
                onGestureStart = {
                    coroutineScope.launch {
                        enhancedZoomState.onGestureStart()
                    }
                    onGestureStart?.invoke(enhancedZoomState.enhancedZoomData)
                },
                onGestureEnd = {
                    coroutineScope.launch {
                        enhancedZoomState.onGestureEnd {
                            onGestureEnd?.invoke(enhancedZoomState.enhancedZoomData)
                        }
                    }
                },
                onGesture = { centroid, pan, zoom, rotate, mainPointer, pointerList ->

                    val currentZoom = enhancedZoomState.zoom
                    val currentPan = enhancedZoomState.pan
                    val currentRotation = enhancedZoomState.rotation
                    val gestureEnabled = enabled(currentZoom, currentPan, currentRotation)

                    coroutineScope.launch {
                        enhancedZoomState.onGesture(
                            centroid = centroid,
                            pan = if (gestureEnabled) pan else Offset.Zero,
                            zoom = zoom,
                            rotation = rotate,
                            mainPointer = mainPointer,
                            changes = pointerList
                        )
                    }

                    onGesture?.invoke(enhancedZoomState.enhancedZoomData)
                    if (gestureEnabled) {
                        mainPointer.consume()
                    }
                }
            )
        }

        val tapModifier = Modifier.pointerInput(key) {
            // Pass size of this Composable this Modifier is attached for constraining operations
            // inside this bounds
            enhancedZoomState.size = this.size
            detectTapGestures(
                onDoubleTap = {
                    coroutineScope.launch {
                        zoomLevel = getNextZoomLevel(zoomLevel)
                        val newZoom = zoomOnDoubleTap(zoomLevel)
                        enhancedZoomState.onDoubleTap(zoom = newZoom) {
                            onGestureEnd?.invoke(enhancedZoomState.enhancedZoomData)
                        }
                    }
                }
            )
        }

        val graphicsModifier = Modifier.graphicsLayer {
            this.update(enhancedZoomState)
        }

        this.then(
            (if (clipToBounds) Modifier.clipToBounds() else Modifier)
                .then(tapModifier)
                .then(transformModifier)
                .then(graphicsModifier)
        )
    },
    inspectorInfo = debugInspectorInfo {
        name = "enhancedZoom"
        // add name and value of each argument
        properties["key"] = key
        properties["enabled"] = enabled
        properties["clip"] = clip
        properties["onDown"] = onGestureStart
        properties["onMove"] = onGesture
        properties["onUp"] = onGestureEnd
    }
)

/**
 * Modifier that zooms in or out of Composable set to. This zoom modifier has option
 * to move back to bounds with an animation or option to have fling gesture when user removes
 * from screen while velocity is higher than threshold to have smooth touch effect.
 *
 * [key1], [key2] are used for [Modifier.pointerInput] to restart closure when any keys assigned
 * change
 * @param clip when set to true clips to parent bounds. Anything outside parent bounds is not
 * drawn
 * empty space on sides or edges of parent.
 * @param enhancedZoomState State of the zoom that contains option to set initial, min, max zoom,
 * enabling rotation, pan or zoom and contains current [EnhancedZoomData]
 * event propagations. Also contains [Rect] of visible area based on pan, zoom and rotation
 * @param zoomOnDoubleTap lambda that returns current [ZoomLevel] and based on current level
 * enables developer to define zoom on double tap gesture
 * @param enabled lambda can be used selectively enable or disable pan and intercepting with
 * scroll, drag or lists or pagers using current zoom, pan or rotation values
 * @param onGestureStart callback to to notify gesture has started and return current
 * [EnhancedZoomData]  of this modifier
 * @param onGesture callback to notify about ongoing gesture and return current
 * [EnhancedZoomData]  of this modifier
 * @param onGestureEnd callback to notify that gesture finished return current
 * [EnhancedZoomData]  of this modifier
 */
fun Modifier.enhancedZoom(
    key1: Any?,
    key2: Any?,
    clip: Boolean = true,
    enhancedZoomState: EnhancedZoomState,
    enabled: (Float, Offset, Float) -> Boolean = DefaultEnabled,
    zoomOnDoubleTap: (ZoomLevel) -> Float = enhancedZoomState.DefaultOnDoubleTap,
    onGestureStart: ((EnhancedZoomData) -> Unit)? = null,
    onGesture: ((EnhancedZoomData) -> Unit)? = null,
    onGestureEnd: ((EnhancedZoomData) -> Unit)? = null,
) = composed(

    factory = {
        val coroutineScope = rememberCoroutineScope()
        // Current Zoom level
        var zoomLevel by remember { mutableStateOf(ZoomLevel.Min) }

        // Whether panning should be limited to bounds of gesture area or not
        val boundPan = enhancedZoomState.limitPan && !enhancedZoomState.rotatable

        // If we bound to touch area or clip is true Modifier.clipToBounds is used
        val clipToBounds = (clip || boundPan)

        val transformModifier = Modifier.pointerInput(key1, key2) {
            // Pass size of this Composable this Modifier is attached for constraining operations
            // inside this bounds
            enhancedZoomState.size = this.size
            detectTransformGestures(
                consume = false,
                onGestureStart = {
                    onGestureStart?.invoke(enhancedZoomState.enhancedZoomData)
                },
                onGestureEnd = {
                    coroutineScope.launch {
                        enhancedZoomState.onGestureEnd {
                            onGestureEnd?.invoke(enhancedZoomState.enhancedZoomData)
                        }
                    }
                },
                onGesture = { centroid, pan, zoom, rotate, mainPointer, pointerList ->

                    val enhancedZoomData = enhancedZoomState.enhancedZoomData
                    val currentZoom = enhancedZoomData.zoom
                    val currentPan = enhancedZoomData.pan
                    val currentRotation = enhancedZoomData.rotation
                    val gestureEnabled = enabled(currentZoom, currentPan, currentRotation)

                    coroutineScope.launch {
                        enhancedZoomState.onGesture(
                            centroid = centroid,
                            pan = if (gestureEnabled) pan else Offset.Zero,
                            zoom = zoom,
                            rotation = rotate,
                            mainPointer = mainPointer,
                            changes = pointerList
                        )
                    }

                    onGesture?.invoke(enhancedZoomState.enhancedZoomData)

                    if (gestureEnabled) {
                        mainPointer.consume()
                    }
                }
            )
        }

        val tapModifier = Modifier.pointerInput(key1, key2) {
            // Pass size of this Composable this Modifier is attached for constraining operations
            // inside this bounds
            enhancedZoomState.size = this.size
            detectTapGestures(
                onDoubleTap = {
                    coroutineScope.launch {
                        zoomLevel = getNextZoomLevel(zoomLevel)
                        val newZoom = zoomOnDoubleTap(zoomLevel)
                        enhancedZoomState.onDoubleTap(zoom = newZoom) {
                            onGestureEnd?.invoke(enhancedZoomState.enhancedZoomData)
                        }
                    }
                }
            )
        }

        val graphicsModifier = Modifier.graphicsLayer {
            this.update(enhancedZoomState)
        }

        this.then(
            (if (clipToBounds) Modifier.clipToBounds() else Modifier)
                .then(tapModifier)
                .then(transformModifier)
                .then(graphicsModifier)
        )
    },
    inspectorInfo = {
        name = "enhancedZoom"
        // add name and value of each argument
        properties["key1"] = key1
        properties["key2"] = key2
        properties["clip"] = clip
        properties["enabled"] = enabled
        properties["zoomOnDoubleTap"] = zoomOnDoubleTap
        properties["onDown"] = onGestureStart
        properties["onMove"] = onGesture
        properties["onUp"] = onGestureEnd
    }
)

/**
 * Modifier that zooms in or out of Composable set to. This zoom modifier has option
 * to move back to bounds with an animation or option to have fling gesture when user removes
 * from screen while velocity is higher than threshold to have smooth touch effect.
 *
 * @param keys are used for [Modifier.pointerInput] to restart closure when any keys assigned
 * change
 * @param clip when set to true clips to parent bounds. Anything outside parent bounds is not
 * drawn
 * empty space on sides or edges of parent.
 * @param enhancedZoomState State of the zoom that contains option to set initial, min, max zoom,
 * enabling rotation, pan or zoom and contains current [EnhancedZoomData]
 * event propagations. Also contains [Rect] of visible area based on pan, zoom and rotation
 * @param zoomOnDoubleTap lambda that returns current [ZoomLevel] and based on current level
 * enables developer to define zoom on double tap gesture
 * @param enabled lambda can be used selectively enable or disable pan and intercepting with
 * scroll, drag or lists or pagers using current zoom, pan or rotation values
 * @param onGestureStart callback to to notify gesture has started and return current
 * [EnhancedZoomData]  of this modifier
 * @param onGesture callback to notify about ongoing gesture and return current
 * [EnhancedZoomData]  of this modifier
 * @param onGestureEnd callback to notify that gesture finished return current
 * [EnhancedZoomData]  of this modifier
 */
fun Modifier.enhancedZoom(
    vararg keys: Any?,
    clip: Boolean = true,
    enhancedZoomState: EnhancedZoomState,
    enabled: (Float, Offset, Float) -> Boolean = DefaultEnabled,
    zoomOnDoubleTap: (ZoomLevel) -> Float = enhancedZoomState.DefaultOnDoubleTap,
    onGestureStart: ((EnhancedZoomData) -> Unit)? = null,
    onGesture: ((EnhancedZoomData) -> Unit)? = null,
    onGestureEnd: ((EnhancedZoomData) -> Unit)? = null,
) = composed(

    factory = {

        val coroutineScope = rememberCoroutineScope()

        // Current Zoom level
        var zoomLevel by remember { mutableStateOf(ZoomLevel.Min) }

        // Whether panning should be limited to bounds of gesture area or not
        val boundPan = enhancedZoomState.limitPan && !enhancedZoomState.rotatable

        // If we bound to touch area or clip is true Modifier.clipToBounds is used
        val clipToBounds = (clip || boundPan)

        val transformModifier = Modifier.pointerInput(*keys) {
            // Pass size of this Composable this Modifier is attached for constraining operations
            // inside this bounds
            enhancedZoomState.size = this.size
            detectTransformGestures(
                consume = false,
                onGestureStart = {
                    onGestureStart?.invoke(enhancedZoomState.enhancedZoomData)
                },
                onGestureEnd = {
                    coroutineScope.launch {
                        enhancedZoomState.onGestureEnd {
                            onGestureEnd?.invoke(enhancedZoomState.enhancedZoomData)
                        }
                    }
                },
                onGesture = { centroid, pan, zoom, rotate, mainPointer, pointerList ->

                    val enhancedZoomData = enhancedZoomState.enhancedZoomData
                    val currentZoom = enhancedZoomData.zoom
                    val currentPan = enhancedZoomData.pan
                    val currentRotation = enhancedZoomData.rotation
                    val gestureEnabled = enabled(currentZoom, currentPan, currentRotation)

                    coroutineScope.launch {
                        enhancedZoomState.onGesture(
                            centroid = centroid,
                            pan = if (gestureEnabled) pan else Offset.Zero,
                            zoom = zoom,
                            rotation = rotate,
                            mainPointer = mainPointer,
                            changes = pointerList
                        )
                    }

                    onGesture?.invoke(enhancedZoomState.enhancedZoomData)

                    if (gestureEnabled) {
                        mainPointer.consume()
                    }
                }
            )
        }

        val tapModifier = Modifier.pointerInput(*keys) {
            // Pass size of this Composable this Modifier is attached for constraining operations
            // inside this bounds
            enhancedZoomState.size = this.size
            detectTapGestures(
                onDoubleTap = {
                    coroutineScope.launch {
                        zoomLevel = getNextZoomLevel(zoomLevel)
                        val newZoom = zoomOnDoubleTap(zoomLevel)
                        enhancedZoomState.onDoubleTap(zoom = newZoom) {
                            onGestureEnd?.invoke(enhancedZoomState.enhancedZoomData)
                        }
                    }
                }
            )
        }

        val graphicsModifier = Modifier.graphicsLayer {
            this.update(enhancedZoomState)
        }

        this.then(
            (if (clipToBounds) Modifier.clipToBounds() else Modifier)
                .then(tapModifier)
                .then(transformModifier)
                .then(graphicsModifier)
        )
    },
    inspectorInfo = debugInspectorInfo {
        name = "enhancedZoom"
        // add name and value of each argument
        properties["keys"] = keys
        properties["clip"] = clip
        properties["onDown"] = onGestureStart
        properties["onMove"] = onGesture
        properties["onUp"] = onGestureEnd
    }
)

internal val DefaultEnabled = { zoom: Float, pan: Offset, rotation: Float ->
    true
}

internal val DefaultOnDoubleTap: (ZoomLevel) -> Float
    get() = { zoomLevel: ZoomLevel ->
        when (zoomLevel) {
            ZoomLevel.Min -> 1f
            ZoomLevel.Mid -> 2f
            ZoomLevel.Max -> 3f
        }
    }

internal val BaseEnhancedZoomState.DefaultOnDoubleTap: (ZoomLevel) -> Float
    get() = { zoomLevel: ZoomLevel ->
        when (zoomLevel) {
            ZoomLevel.Min -> 1f.coerceAtMost(zoomMin)
            ZoomLevel.Mid -> 3f.coerceIn(zoomMin, zoomMax)
            ZoomLevel.Max -> 5f.coerceAtLeast(zoomMax)
        }
    }
