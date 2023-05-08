package com.smarttoolfactory.colordetector

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isFinite
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.colordetector.util.calculateColorInPixel
import com.smarttoolfactory.gesture.pointerMotionEvents
import com.smarttoolfactory.image.ImageWithConstraints
import com.smarttoolfactory.image.util.update
import com.smarttoolfactory.image.zoom.ZoomState
import com.smarttoolfactory.image.zoom.animatedZoom
import com.smarttoolfactory.image.zoom.rememberAnimatedZoomState


@Composable
fun ImageColorDetector(
    modifier: Modifier = Modifier,
    color: Color,
    canZoom: Boolean = false,
    imageBitmap: ImageBitmap,
    onColorChange: (Color) -> Unit
) {

    var offset by remember {
        mutableStateOf(Offset.Unspecified)
    }

    val zoomState = rememberAnimatedZoomState(limitPan = true)

    ImageWithConstraints(
        modifier = modifier.then(
            if (canZoom) {
                Modifier.animatedZoom(animatedZoomState = zoomState)
            } else {
                Modifier.graphicsLayer {
                    update(zoomState)
                }
            }
        ),
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

        if (!canZoom) {
            Box(
                modifier = Modifier
                    .size(imageWidth, imageHeight)
                    .pointerMotionEvents(
                        onDown = {
                            offset = updateOffset(it)
                        },
                        onMove = {
                            offset = updateOffset(it)
                        },
                        onUp = {
                            offset = updateOffset(it)
                        }
                    )
            )
        }

        if (offset.isSpecified && offset.isFinite) {
            onColorChange(
                calculateColorInPixel(
                    offsetX = offset.x,
                    offsetY = offset.y,
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
            modifier = Modifier.size(imageWidth, imageHeight),
            selectedColor = color,
            zoomState = zoomState,
            offset = offset
        )
    }

}


@Composable
internal fun ColorSelectionDrawing(
    modifier: Modifier,
    selectedColor: Color = Color.Black,
    zoomState: ZoomState? = null,
    offset: Offset,
) {
    val color = animateColorAsState(
        if (selectedColor.luminance() > 0.3f) Color.Black else Color.White
    ).value

    val zoom = zoomState?.zoom ?: 1f

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
