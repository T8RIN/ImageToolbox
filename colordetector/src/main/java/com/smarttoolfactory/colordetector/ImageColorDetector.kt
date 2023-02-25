package com.smarttoolfactory.colordetector

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isFinite
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.colordetector.util.calculateColorInPixel
import com.smarttoolfactory.gesture.pointerMotionEvents
import com.smarttoolfactory.image.ImageWithConstraints


@Composable
fun ImageColorDetector(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap,
    onColorChange: (Color) -> Unit
) {

    var offset by remember {
        mutableStateOf(Offset.Unspecified)
    }

    ImageWithConstraints(modifier, imageBitmap = imageBitmap) {

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
            offset = offset
        )
    }

}


@Composable
internal fun ColorSelectionDrawing(
    modifier: Modifier,
    offset: Offset,
) {
    val color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface

    androidx.compose.foundation.Canvas(modifier = modifier.fillMaxSize()) {

        if (offset.isSpecified) {
            val radius: Float = 8.dp.toPx()

            // Draw touch position circle
            drawCircle(
                color,
                radius = radius * 0.2f,
                center = offset,
            )
            drawCircle(
                color,
                radius = radius * 1.4f,
                center = offset,
                style = Stroke(radius * 0.4f)
            )

        }
    }
}
