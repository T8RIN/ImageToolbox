package com.smarttoolfactory.colordetector

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.magnifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isFinite
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAny
import com.smarttoolfactory.colordetector.util.calculateColorInPixel
import com.smarttoolfactory.gesture.pointerMotionEvents
import com.smarttoolfactory.image.ImageWithConstraints
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import net.engawapg.lib.zoomable.ZoomableDefaults.defaultZoomOnDoubleTap
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import kotlin.coroutines.cancellation.CancellationException


@Composable
fun ImageColorDetector(
    modifier: Modifier = Modifier,
    color: Color,
    panEnabled: Boolean = false,
    imageBitmap: ImageBitmap,
    isMagnifierEnabled: Boolean,
    boxModifier: Modifier = Modifier,
    onColorChange: (Color) -> Unit
) {
    var pickerOffset by remember {
        mutableStateOf(Offset.Unspecified)
    }

    val zoomState = rememberZoomState(maxScale = 20f)

    val magnifierEnabled by remember(zoomState.scale) {
        derivedStateOf {
            zoomState.scale <= 3f && !panEnabled && isMagnifierEnabled
        }
    }
    var globalTouchPosition by remember { mutableStateOf(Offset.Unspecified) }
    var globalTouchPointersCount by remember { mutableIntStateOf(0) }

    Box(
        modifier = boxModifier
            .observePointersCountWithOffset { size, offset ->
                globalTouchPointersCount = size
                globalTouchPosition = offset
            }
            .then(
                if (magnifierEnabled && globalTouchPointersCount == 1) {
                    Modifier.magnifier(
                        sourceCenter = {
                            if (pickerOffset.isSpecified) {
                                globalTouchPosition
                            } else Offset.Unspecified
                        },
                        magnifierCenter = {
                            globalTouchPosition - Offset(0f, 200f)
                        },
                        size = DpSize(height = 100.dp, width = 100.dp),
                        zoom = 2f / zoomState.scale,
                        cornerRadius = 50.dp,
                        elevation = 2.dp
                    )
                } else Modifier
            )
            .zoomable(
                zoomState = zoomState,
                enabled = { _, _ ->
                    (globalTouchPointersCount >= 2 || panEnabled)
                },
                enableOneFingerZoom = panEnabled,
                onDoubleTap = { pos ->
                    if (panEnabled) zoomState.defaultZoomOnDoubleTap(pos)
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        ImageWithConstraints(
            modifier = modifier,
            imageBitmap = imageBitmap
        ) {

            val density = LocalDensity.current.density

            val size = rememberUpdatedState(
                newValue = Size(
                    width = imageWidth.value * density,
                    height = imageHeight.value * density
                )
            )

            fun updateOffset(pointerInputChange: PointerInputChange): Offset {
                val offsetX = pointerInputChange.position.x
                    .coerceIn(0f, size.value.width)
                val offsetY = pointerInputChange.position.y
                    .coerceIn(0f, size.value.height)
                pointerInputChange.consume()
                return Offset(offsetX, offsetY)
            }

            if (!panEnabled) {
                var touchStartedWithOnePointer by remember {
                    mutableStateOf(false)
                }
                Box(
                    modifier = Modifier
                        .size(imageWidth, imageHeight)
                        .pointerMotionEvents(
                            onDown = {
                                touchStartedWithOnePointer = globalTouchPointersCount <= 1

                                if (touchStartedWithOnePointer) pickerOffset = updateOffset(it)
                            },
                            onMove = {
                                if (touchStartedWithOnePointer) pickerOffset = updateOffset(it)
                            },
                            onUp = {
                                if (touchStartedWithOnePointer) pickerOffset = updateOffset(it)
                            },
                            delayAfterDownInMillis = 20
                        )
                )
            }

            if (pickerOffset.isSpecified && pickerOffset.isFinite) {
                onColorChange(
                    calculateColorInPixel(
                        offsetX = pickerOffset.x,
                        offsetY = pickerOffset.y,
                        startImageX = 0f,
                        startImageY = 0f,
                        rect = rect,
                        width = imageWidth.value * density,
                        height = imageHeight.value * density,
                        bitmap = imageBitmap.asAndroidBitmap()
                    )
                )
            }

            ColorSelectionDrawing(
                modifier = Modifier
                    .size(imageWidth, imageHeight),
                selectedColor = color,
                zoom = zoomState.scale,
                offset = pickerOffset
            )
        }
    }
}

@Composable
internal fun ColorSelectionDrawing(
    modifier: Modifier,
    selectedColor: Color = Color.Black,
    zoom: Float = 1f,
    offset: Offset,
) {
    val color = animateColorAsState(
        if (selectedColor.luminance() > 0.3f) Color.Black else Color.White
    ).value

    Canvas(modifier = modifier.fillMaxSize()) {

        if (offset.isSpecified) {
            val radius: Float = 8.dp.toPx() / zoom

            // Draw touch position circle
            drawCircle(
                color,
                radius = radius * 0.3f,
                center = offset
            )
            drawCircle(
                color,
                radius = radius * 1.6f,
                center = offset,
                style = Stroke(radius * 0.6f)
            )
        }
    }
}


fun Modifier.observePointersCountWithOffset(
    enabled: Boolean = true,
    onChange: (Int, Offset) -> Unit
) = this then if (enabled) Modifier.pointerInput(Unit) {
    onEachGesture {
        val context = currentCoroutineContext()
        awaitPointerEventScope {
            do {
                val event = awaitPointerEvent()
                onChange(
                    event.changes.size,
                    event.changes.firstOrNull()?.position ?: Offset.Unspecified
                )
            } while (event.changes.any { it.pressed } && context.isActive)
            onChange(0, Offset.Unspecified)
        }
    }
} else Modifier

suspend fun PointerInputScope.onEachGesture(block: suspend PointerInputScope.() -> Unit) {
    val currentContext = currentCoroutineContext()
    while (currentContext.isActive) {
        try {
            block()

            // Wait for all pointers to be up. Gestures start when a finger goes down.
            awaitAllPointersUp()
        } catch (e: CancellationException) {
            if (currentContext.isActive) {
                // The current gesture was canceled. Wait for all fingers to be "up" before looping
                // again.
                awaitAllPointersUp()
            } else {
                // forEachGesture was cancelled externally. Rethrow the cancellation exception to
                // propagate it upwards.
                throw e
            }
        }
    }
}

private suspend fun PointerInputScope.awaitAllPointersUp() {
    awaitPointerEventScope { awaitAllPointersUp() }
}

private suspend fun AwaitPointerEventScope.awaitAllPointersUp() {
    if (!allPointersUp()) {
        do {
            val events = awaitPointerEvent(PointerEventPass.Final)
        } while (events.changes.fastAny { it.pressed })
    }
}

private fun AwaitPointerEventScope.allPointersUp(): Boolean =
    !currentEvent.changes.fastAny { it.pressed }