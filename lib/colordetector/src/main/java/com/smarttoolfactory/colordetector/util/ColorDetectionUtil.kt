package com.smarttoolfactory.colordetector.util

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntRect
import androidx.core.graphics.get

/**
 * Calculate color of a pixel in a [Bitmap] that is drawn to a Composable with
 * [width] and [height]. [startImageX]
 *
 * @param offsetX x coordinate in Composable from top left corner
 * @param offsetY y coordinate in Composable from top left corner
 * @param startImageX x coordinate of top left position of image
 * @param startImageY y coordinate of top left position of image
 * @param rect contains coordinates of original bitmap to be used as. Full bitmap has
 * rect with (0,0) top left and size of [bitmap]
 * @param width of the Composable that draws this [bitmap]
 * @param height of the Composable that draws this [bitmap]
 * @param bitmap of picture/image that to detect color of a specific pixel in
 */
fun calculateColorInPixel(
    offsetX: Float,
    offsetY: Float,
    startImageX: Float = 0f,
    startImageY: Float = 0f,
    rect: IntRect,
    width: Float,
    height: Float,
    bitmap: Bitmap,
): Color {

    val bitmapWidth = bitmap.width
    val bitmapHeight = bitmap.height

    if (bitmapWidth == 0 || bitmapHeight == 0) return Color.Unspecified

    // End positions, this might be less than Image dimensions if bitmap doesn't fit Image
    val endImageX = width - startImageX
    val endImageY = height - startImageY

    val scaledX = scale(
        start1 = startImageX,
        end1 = endImageX,
        pos = offsetX,
        start2 = rect.left.toFloat(),
        end2 = rect.right.toFloat()
    ).toInt().coerceIn(0, bitmapWidth - 1)

    val scaledY = scale(
        start1 = startImageY,
        end1 = endImageY,
        pos = offsetY,
        start2 = rect.top.toFloat(),
        end2 = rect.bottom.toFloat()
    ).toInt().coerceIn(0, bitmapHeight - 1)

    val pixel: Int = bitmap[scaledX, scaledY]

    val red = android.graphics.Color.red(pixel)
    val green = android.graphics.Color.green(pixel)
    val blue = android.graphics.Color.blue(pixel)

    return (Color(red, green, blue))
}


/**
 * [Linear Interpolation](https://en.wikipedia.org/wiki/Linear_interpolation) function that moves
 * amount from it's current position to start and amount
 * @param start of interval
 * @param end of interval
 * @param amount e closed unit interval [0, 1]
 */
private fun lerp(start: Float, end: Float, amount: Float): Float {
    return (1 - amount) * start + amount * end
}

/**
 * Scale x1 from start1..end1 range to start2..end2 range

 */
private fun scale(start1: Float, end1: Float, pos: Float, start2: Float, end2: Float) =
    lerp(start2, end2, calculateFraction(start1, end1, pos))

/**
 * Calculate fraction for value between a range [end] and [start] coerced into 0f-1f range
 */
private fun calculateFraction(start: Float, end: Float, pos: Float) =
    (if (end - start == 0f) 0f else (pos - start) / (end - start)).coerceIn(0f, 1f)

