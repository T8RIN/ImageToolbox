package com.smarttoolfactory.image.transform

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import com.smarttoolfactory.gesture.pointerMotionEvents
import com.smarttoolfactory.image.util.getTouchRegion

internal fun Modifier.morph(
    enabled: Boolean,
    initialSize: DpSize,
    touchRegionRadius: Float,
    minDimension: Float,
    constraints: Constraints,
    handlePlacement: HandlePlacement,
    onDown: () -> Unit,
    onMove: (DpSize) -> Unit,
    onUp: () -> Unit
) = composed(
    factory = {

        val density = LocalDensity.current

        val maxWidth: Dp
        val maxHeight: Dp

        with(density) {
            maxWidth = constraints.maxWidth.toDp()
            maxHeight = constraints.maxHeight.toDp()
        }
        var updatedSize by remember {
            mutableStateOf(initialSize)
        }

        var transform by remember {
            mutableStateOf(
                Transform(rotation = 0f)
            )
        }

        var touchRegion by remember(enabled) { mutableStateOf(TouchRegion.None) }

        Modifier
            .size(updatedSize)
            .graphicsLayer {
                translationX = transform.translationX
                translationY = transform.translationY
                rotationZ = transform.rotation
            }
            .pointerMotionEvents(
                onDown = { change ->

                    if (enabled) {
                        val size = with(density) { updatedSize.toSize() }
                        val position = change.position

                        touchRegion = getTouchRegion(
                            position = position,
                            rect = Rect(offset = Offset.Zero, size = size),
                            threshold = touchRegionRadius * 2,
                            handlePlacement = handlePlacement
                        )
                        onDown()
                    }
                },
                onMove = { change ->

                    if (!enabled) return@pointerMotionEvents

                    val dragAmount = change.positionChange()

                    updateTransform(
                        touchRegion,
                        density,
                        updatedSize,
                        dragAmount,
                        minDimension,
                        transform,
                        onUpdate = { dpSize, transformChange ->
                            updatedSize = DpSize(
                                dpSize.width.coerceAtMost(maxWidth),
                                dpSize.height.coerceAtMost(maxHeight)
                            )
                            onMove(updatedSize)
                            transform = transformChange
                        }
                    )

                    change.consume()
                },
                onUp = {
                    touchRegion = TouchRegion.None
                    onUp()
                },
                key1 = enabled
            )
    },
    inspectorInfo = {
        name = "morph"
        // add name and value of each argument
        properties["enabled"] = enabled
        properties["initialSize"] = initialSize
        properties["touchRegionRadius"] = touchRegionRadius
        properties["minDimension"] = minDimension
        properties["handlePlacement"] = handlePlacement
        properties["onDown"] = onDown
        properties["onMove"] = onMove
        properties["onUp"] = onUp
    }
)

internal fun updateTransform(
    touchRegion: TouchRegion,
    density: Density,
    updatedSize: DpSize,
    dragAmount: Offset,
    minDimension: Float,
    transform: Transform,
    onUpdate: (DpSize, Transform) -> Unit
) {

    val translationX: Float
    val translationY: Float
    val newSize: DpSize

    when (touchRegion) {

        // Corners
        TouchRegion.TopLeft -> {

            with(density) {

                // Get current dimensions of Composable
                val oldWidth = updatedSize.width.toPx()
                val oldHeight = updatedSize.height.toPx()

                // Change dimensions of composable by drag amount limited to
                // minimum dimension constraint
                val width = (oldWidth - dragAmount.x).coerceAtLeast(minDimension)
                val height = (oldHeight - dragAmount.y).coerceAtLeast(minDimension)

                // Translate Composable as the difference between old and new dimensions
                translationX = transform.translationX + oldWidth - width
                translationY = transform.translationY + oldHeight - height
                newSize = DpSize(width.toDp(), height.toDp())
            }

            onUpdate(
                newSize,
                transform.copy(
                    translationX = translationX,
                    translationY = translationY,
                )
            )
        }

        TouchRegion.BottomLeft -> {

            with(density) {

                // Get current dimensions of Composable
                val oldWidth = updatedSize.width.toPx()
                val oldHeight = updatedSize.height.toPx()

                // Change dimensions of composable by drag amount limited to
                // minimum dimension constraint
                val width = (oldWidth - dragAmount.x).coerceAtLeast(minDimension)
                val height = (oldHeight + dragAmount.y).coerceAtLeast(minDimension)

                translationX = transform.translationX + oldWidth - width
                translationY = transform.translationY

                // Translate Composable as the difference between old and new dimensions
                newSize = DpSize(width.toDp(), height.toDp())
            }

            onUpdate(
                newSize,
                transform.copy(
                    translationX = translationX,
                    translationY = translationY,
                )
            )
        }

        TouchRegion.TopRight -> {

            with(density) {

                // Get current dimensions of Composable
                val oldWidth = updatedSize.width.toPx()
                val oldHeight = updatedSize.height.toPx()

                val width = (oldWidth + dragAmount.x).coerceAtLeast(minDimension)
                val height = (oldHeight - dragAmount.y).coerceAtLeast(minDimension)

                translationX = transform.translationX
                translationY = transform.translationY + oldHeight - height

                newSize = DpSize(width.toDp(), height.toDp())
            }

            onUpdate(
                newSize,
                transform.copy(
                    translationX = translationX,
                    translationY = translationY,
                )
            )
        }

        TouchRegion.BottomRight -> {
            with(density) {

                // Get current dimensions of Composable
                val oldWidth = updatedSize.width.toPx()
                val oldHeight = updatedSize.height.toPx()

                val width = (oldWidth + dragAmount.x).coerceAtLeast(minDimension)
                val height = (oldHeight + dragAmount.y).coerceAtLeast(minDimension)

                translationX = transform.translationX
                translationY = transform.translationY

                newSize = DpSize(width.toDp(), height.toDp())
            }

            onUpdate(
                newSize,
                transform.copy(
                    translationX = translationX,
                    translationY = translationY,
                )
            )
        }

        // Sides
        TouchRegion.CenterLeft -> {

            with(density) {

                // Get current width of Composable
                val oldWidth = updatedSize.width.toPx()
                val width = (oldWidth - dragAmount.x).coerceAtLeast(minDimension)

                translationX = transform.translationX + oldWidth - width
                newSize = updatedSize.copy(width = width.toDp())
            }

            onUpdate(
                newSize,
                transform.copy(translationX = translationX)
            )
        }

        TouchRegion.TopCenter -> {

            with(density) {

                // Get current height of Composable
                val oldHeight = updatedSize.height.toPx()

                val height = (oldHeight - dragAmount.y).coerceAtLeast(minDimension)

                translationY = transform.translationY + oldHeight - height
                newSize = updatedSize.copy(height = height.toDp())
            }

            onUpdate(
                newSize,
                transform.copy(translationY = translationY)
            )
        }

        TouchRegion.CenterRight -> {

            with(density) {

                // Get current width of Composable
                val oldWidth = updatedSize.width.toPx()

                val width = (oldWidth + dragAmount.x).coerceAtLeast(minDimension)

                translationX = transform.translationX
                newSize = updatedSize.copy(width = width.toDp())
            }

            onUpdate(
                newSize,
                transform.copy(translationX = translationX)
            )
        }

        TouchRegion.BottomCenter -> {

            with(density) {

                // Get current height of Composable
                val oldHeight = updatedSize.height.toPx()

                val height = (oldHeight + dragAmount.y).coerceAtLeast(minDimension)

                translationY = transform.translationY
                newSize = updatedSize.copy(height = height.toDp())
            }

            onUpdate(
                newSize,
                transform.copy(translationY = translationY)
            )
        }

        TouchRegion.Inside -> {

            translationX = transform.translationX + dragAmount.x
            translationY = transform.translationY + dragAmount.y

            onUpdate(
                updatedSize,
                transform.copy(
                    translationX = translationX,
                    translationY = translationY,
                )
            )
        }

        else -> Unit
    }
}
