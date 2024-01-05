package com.smarttoolfactory.cropper.draw

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlin.math.roundToInt

/**
 * Draw image to be cropped
 */
@Composable
internal fun ImageDrawCanvas(
    modifier: Modifier,
    imageBitmap: ImageBitmap,
    imageWidth: Int,
    imageHeight: Int
) {
    Canvas(modifier = modifier) {

        val canvasWidth = size.width.roundToInt()
        val canvasHeight = size.height.roundToInt()

        drawImage(
            image = imageBitmap,
            srcSize = IntSize(imageBitmap.width, imageBitmap.height),
            dstSize = IntSize(imageWidth, imageHeight),
            dstOffset = IntOffset(
                x = (canvasWidth - imageWidth) / 2,
                y = (canvasHeight - imageHeight) / 2
            )
        )
    }
}
