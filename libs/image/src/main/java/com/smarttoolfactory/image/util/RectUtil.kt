package com.smarttoolfactory.image.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize

/**
 * Get rectangle of current transformation of [pan], [zoom] and current bounds of the Composable's
 * selected area as [rectSelection] in a Bitmap with [bitmapWidth] and [bitmapHeight] which
 * is displayed in a Composable with [containerWidth] and [containerHeight].
 *
 * This is getting area to display or crop based on the rectangle returned
 *
 * @param bitmapWidth width of the physical file
 * @param bitmapHeight height of the physical file
 * @param containerWidth width of the Composable that displays image
 * @param containerHeight height of the Composable that displays image
 * @param pan current translation of image via gestures or set values
 * @param rectSelection bounds of the selected area in Composable. This can be used for selecting
 * area via a cropping app
 */
fun getCropRect(
    bitmapWidth: Int,
    bitmapHeight: Int,
    containerWidth: Float,
    containerHeight: Float,
    pan: Offset,
    zoom: Float,
    rectSelection: Rect
): IntRect {
    val widthRatio = bitmapWidth / containerWidth
    val heightRatio = bitmapHeight / containerHeight

    val width = (widthRatio * rectSelection.width / zoom).coerceIn(0f, bitmapWidth.toFloat())
    val height = (heightRatio * rectSelection.height / zoom).coerceIn(0f, bitmapHeight.toFloat())

    val offsetXInBitmap = (widthRatio * (pan.x + rectSelection.left / zoom))
        .coerceIn(0f, bitmapWidth - width).toInt()
    val offsetYInBitmap = (heightRatio * (pan.y + rectSelection.top / zoom))
        .coerceIn(0f, bitmapHeight - height).toInt()

    return IntRect(
        offset = IntOffset(offsetXInBitmap, offsetYInBitmap),
        size = IntSize(width.toInt(), height.toInt())
    )
}