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
 * @param keys are used for [Modifier.pointerInput] to restart closure when any keys assigned
 * change
 * @param clip when set to true clips to parent bounds. Anything outside parent bounds is not
 * drawn
 * @param animatedZoomState State of the zoom that contains option to set initial, min, max zoom,
 * enabling rotation, pan or zoom
 * @param zoomOnDoubleTap lambda that returns current [ZoomLevel] and based on current level
 * enables developer to define zoom on double tap gesture
 * @param enabled lambda can be used selectively enable or disable pan and intercepting with
 * scroll, drag or lists or pagers using current zoom, pan or rotation values
 */
fun Modifier.animatedZoom(
    vararg keys: Any?,
    clip: Boolean = true,
    animatedZoomState: AnimatedZoomState,
    enabled: (Float, Offset, Float) -> Boolean = DefaultEnabled,
    zoomOnDoubleTap: (ZoomLevel) -> Float = animatedZoomState.DefaultOnDoubleTap,
) = composed(

    factory = {

        val coroutineScope = rememberCoroutineScope()

        // Current Zoom level
        var zoomLevel by remember { mutableStateOf(ZoomLevel.Min) }

        // Whether panning should be limited to bounds of gesture area or not
        val boundPan = animatedZoomState.limitPan && !animatedZoomState.rotatable

        // If we bound to touch area or clip is true Modifier.clipToBounds is used
        val clipToBounds = (clip || boundPan)

        val transformModifier = Modifier.pointerInput(*keys) {
            // Pass size of this Composable this Modifier is attached for constraining operations
            // inside this bounds
            animatedZoomState.size = this.size
            detectTransformGestures(
                consume = false,
                onGestureEnd = {
                    coroutineScope.launch {
                        animatedZoomState.onGestureEnd {
                        }
                    }
                },
                onGesture = { centroid, pan, zoom, rotate, mainPointer, pointerList ->

                    val currentZoom = animatedZoomState.zoom
                    val currentPan = animatedZoomState.pan
                    val currentRotation = animatedZoomState.rotation
                    val gestureEnabled = enabled(currentZoom, currentPan, currentRotation)

                    coroutineScope.launch {
                        animatedZoomState.onGesture(
                            centroid = centroid,
                            pan = if (gestureEnabled) pan else Offset.Zero,
                            zoom = zoom,
                            rotation = rotate,
                            mainPointer = mainPointer,
                            changes = pointerList
                        )
                    }

                    if (gestureEnabled) {
                        mainPointer.consume()
                    }
                }
            )
        }

        val tapModifier = Modifier.pointerInput(*keys) {
            // Pass size of this Composable this Modifier is attached for constraining operations
            // inside this bounds
            animatedZoomState.size = this.size
            detectTapGestures(
                onDoubleTap = {
                    coroutineScope.launch {
                        zoomLevel = getNextZoomLevel(zoomLevel)
                        val newZoom = zoomOnDoubleTap(zoomLevel)
                        animatedZoomState.onDoubleTap(zoom = newZoom) {}
                    }
                }
            )
        }

        val graphicsModifier = Modifier.graphicsLayer {
            this.update(animatedZoomState)
        }

        this.then(
            (if (clipToBounds) Modifier.clipToBounds() else Modifier)
                .then(tapModifier)
                .then(transformModifier)
                .then(graphicsModifier)
        )
    },
    inspectorInfo = debugInspectorInfo {
        name = "animatedZoomState"
        properties["keys"] = keys
        properties["clip"] = clip
        properties["animatedZoomState"] = animatedZoomState
        properties["enabled"] = enabled
        properties["zoomOnDoubleTap"] = zoomOnDoubleTap
    }
)

/**
 * Modifier that zooms in or out of Composable set to. This zoom modifier has option
 * to move back to bounds with an animation or option to have fling gesture when user removes
 * from screen while velocity is higher than threshold to have smooth touch effect.
 *
 * @param key is used for [Modifier.pointerInput] to restart closure when any keys assigned
 * change
 * @param clip when set to true clips to parent bounds. Anything outside parent bounds is not
 * drawn
 * @param animatedZoomState State of the zoom that contains option to set initial, min, max zoom,
 * enabling rotation, pan or zoom
 * @param zoomOnDoubleTap lambda that returns current [ZoomLevel] and based on current level
 * enables developer to define zoom on double tap gesture
 * @param enabled lambda can be used selectively enable or disable pan and intercepting with
 * scroll, drag or lists or pagers using current zoom, pan or rotation values
 */
fun Modifier.animatedZoom(
    key: Any? = Unit,
    clip: Boolean = true,
    animatedZoomState: AnimatedZoomState,
    enabled: (Float, Offset, Float) -> Boolean = DefaultEnabled,
    zoomOnDoubleTap: (ZoomLevel) -> Float = animatedZoomState.DefaultOnDoubleTap,
) = composed(

    factory = {

        val coroutineScope = rememberCoroutineScope()

        // Current Zoom level
        var zoomLevel by remember { mutableStateOf(ZoomLevel.Min) }

        // Whether panning should be limited to bounds of gesture area or not
        val boundPan = animatedZoomState.limitPan && !animatedZoomState.rotatable

        // If we bound to touch area or clip is true Modifier.clipToBounds is used
        val clipToBounds = (clip || boundPan)

        val transformModifier = Modifier.pointerInput(key) {
            // Pass size of this Composable this Modifier is attached for constraining operations
            // inside this bounds
            animatedZoomState.size = this.size
            detectTransformGestures(
                consume = false,
                onGestureEnd = {
                    coroutineScope.launch {
                        animatedZoomState.onGestureEnd {}
                    }
                },
                onGesture = { centroid, pan, zoom, rotate, mainPointer, pointerList ->

                    val currentZoom = animatedZoomState.zoom
                    val currentPan = animatedZoomState.pan
                    val currentRotation = animatedZoomState.rotation
                    val gestureEnabled = enabled(currentZoom, currentPan, currentRotation)

                    coroutineScope.launch {
                        animatedZoomState.onGesture(
                            centroid = centroid,
                            pan = if (gestureEnabled) pan else Offset.Zero,
                            zoom = zoom,
                            rotation = rotate,
                            mainPointer = mainPointer,
                            changes = pointerList
                        )
                    }

                    if (gestureEnabled) {
                        mainPointer.consume()
                    }
                }
            )
        }

        val tapModifier = Modifier.pointerInput(key) {
            // Pass size of this Composable this Modifier is attached for constraining operations
            // inside this bounds
            animatedZoomState.size = this.size
            detectTapGestures(
                onDoubleTap = {
                    coroutineScope.launch {
                        zoomLevel = getNextZoomLevel(zoomLevel)
                        val newZoom = zoomOnDoubleTap(zoomLevel)
                        animatedZoomState.onDoubleTap(zoom = newZoom) {}
                    }
                }
            )
        }

        val graphicsModifier = Modifier.graphicsLayer {
            this.update(animatedZoomState)
        }

        this.then(
            (if (clipToBounds) Modifier.clipToBounds() else Modifier)
                .then(tapModifier)
                .then(transformModifier)
                .then(graphicsModifier)
        )
    },
    inspectorInfo = debugInspectorInfo {
        name = "animatedZoomState"
        properties["key"] = key
        properties["clip"] = clip
        properties["animatedZoomState"] = animatedZoomState
        properties["enabled"] = enabled
        properties["zoomOnDoubleTap"] = zoomOnDoubleTap
    }
)

fun Modifier.animatedZoom(
    key: Any? = Unit,
    clip: Boolean = true,
    animatedZoomState: AnimatedZoomState,
    enabled: (Float, Offset, Float) -> Boolean = DefaultEnabled,
    zoomOnDoubleTap: (ZoomLevel) -> Float = animatedZoomState.DefaultOnDoubleTap,
    applyZoom: Boolean = true
) = composed(

    factory = {

        val coroutineScope = rememberCoroutineScope()

        // Current Zoom level
        var zoomLevel by remember { mutableStateOf(ZoomLevel.Min) }

        // Whether panning should be limited to bounds of gesture area or not
        val boundPan = animatedZoomState.limitPan && !animatedZoomState.rotatable

        // If we bound to touch area or clip is true Modifier.clipToBounds is used
        val clipToBounds = (clip || boundPan)

        val transformModifier = Modifier.pointerInput(key) {
            // Pass size of this Composable this Modifier is attached for constraining operations
            // inside this bounds
            animatedZoomState.size = this.size
            detectTransformGestures(
                consume = false,
                onGestureEnd = {
                    coroutineScope.launch {
                        animatedZoomState.onGestureEnd {}
                    }
                },
                onGesture = { centroid, pan, zoom, rotate, mainPointer, pointerList ->

                    val currentZoom = animatedZoomState.zoom
                    val currentPan = animatedZoomState.pan
                    val currentRotation = animatedZoomState.rotation
                    val gestureEnabled = enabled(currentZoom, currentPan, currentRotation)

                    coroutineScope.launch {
                        animatedZoomState.onGesture(
                            centroid = centroid,
                            pan = if (gestureEnabled) pan else Offset.Zero,
                            zoom = zoom,
                            rotation = rotate,
                            mainPointer = mainPointer,
                            changes = pointerList
                        )
                    }

                    if (gestureEnabled) {
                        mainPointer.consume()
                    }
                }
            )
        }

        val tapModifier = Modifier.pointerInput(key) {
            // Pass size of this Composable this Modifier is attached for constraining operations
            // inside this bounds
            animatedZoomState.size = this.size
            detectTapGestures(
                onDoubleTap = {
                    coroutineScope.launch {
                        zoomLevel = getNextZoomLevel(zoomLevel)
                        val newZoom = zoomOnDoubleTap(zoomLevel)
                        animatedZoomState.onDoubleTap(zoom = newZoom) {}
                    }
                }
            )
        }

        val graphicsModifier = Modifier.graphicsLayer {
            this.update(animatedZoomState, applyZoom)
        }

        this.then(
            (if (clipToBounds) Modifier.clipToBounds() else Modifier)
                .then(tapModifier)
                .then(transformModifier)
                .then(graphicsModifier)
        )
    },
    inspectorInfo = debugInspectorInfo {
        name = "animatedZoomState"
        properties["key"] = key
        properties["clip"] = clip
        properties["animatedZoomState"] = animatedZoomState
        properties["enabled"] = enabled
        properties["zoomOnDoubleTap"] = zoomOnDoubleTap
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
 * @param animatedZoomState State of the zoom that contains option to set initial, min, max zoom,
 * enabling rotation, pan or zoom
 * @param zoomOnDoubleTap lambda that returns current [ZoomLevel] and based on current level
 * enables developer to define zoom on double tap gesture
 * @param enabled lambda can be used selectively enable or disable pan and intercepting with
 * scroll, drag or lists or pagers using current zoom, pan or rotation values
 */
fun Modifier.animatedZoom(
    key1: Any? = Unit,
    key2: Any? = Unit,
    clip: Boolean = true,
    animatedZoomState: AnimatedZoomState,
    enabled: (Float, Offset, Float) -> Boolean = DefaultEnabled,
    zoomOnDoubleTap: (ZoomLevel) -> Float = animatedZoomState.DefaultOnDoubleTap,
) = composed(

    factory = {

        val coroutineScope = rememberCoroutineScope()

        // Current Zoom level
        var zoomLevel by remember { mutableStateOf(ZoomLevel.Min) }

        // Whether panning should be limited to bounds of gesture area or not
        val boundPan = animatedZoomState.limitPan && !animatedZoomState.rotatable

        // If we bound to touch area or clip is true Modifier.clipToBounds is used
        val clipToBounds = (clip || boundPan)

        val transformModifier = Modifier.pointerInput(key1, key2) {
            // Pass size of this Composable this Modifier is attached for constraining operations
            // inside this bounds
            animatedZoomState.size = this.size
            detectTransformGestures(
                consume = false,
                onGestureEnd = {
                    coroutineScope.launch {
                        animatedZoomState.onGestureEnd {
                        }
                    }
                },
                onGesture = { centroid, pan, zoom, rotate, mainPointer, pointerList ->

                    val currentZoom = animatedZoomState.zoom
                    val currentPan = animatedZoomState.pan
                    val currentRotation = animatedZoomState.rotation
                    val gestureEnabled = enabled(currentZoom, currentPan, currentRotation)

                    coroutineScope.launch {
                        animatedZoomState.onGesture(
                            centroid = centroid,
                            pan = if (gestureEnabled) pan else Offset.Zero,
                            zoom = zoom,
                            rotation = rotate,
                            mainPointer = mainPointer,
                            changes = pointerList
                        )

                        if (gestureEnabled) {
                            mainPointer.consume()
                        }
                    }
                }
            )
        }

        val tapModifier = Modifier.pointerInput(key1, key2) {
            // Pass size of this Composable this Modifier is attached for constraining operations
            // inside this bounds
            animatedZoomState.size = this.size
            detectTapGestures(
                onDoubleTap = {
                    coroutineScope.launch {
                        zoomLevel = getNextZoomLevel(zoomLevel)
                        val newZoom = zoomOnDoubleTap(zoomLevel)
                        animatedZoomState.onDoubleTap(zoom = newZoom) {}
                    }
                }
            )
        }

        val graphicsModifier = Modifier.graphicsLayer {
            this.update(animatedZoomState)
        }

        this.then(
            (if (clipToBounds) Modifier.clipToBounds() else Modifier)
                .then(tapModifier)
                .then(transformModifier)
                .then(graphicsModifier)
        )
    },
    inspectorInfo = debugInspectorInfo {
        name = "animatedZoomState"
        properties["key1"] = key1
        properties["key2"] = key2
        properties["clip"] = clip
        properties["animatedZoomState"] = animatedZoomState
        properties["enabled"] = enabled
        properties["zoomOnDoubleTap"] = zoomOnDoubleTap
    }
)
