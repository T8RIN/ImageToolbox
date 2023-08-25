package com.smarttoolfactory.colorpicker.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas


/**
 * Draw 2 rectangles that blend with [BlendMode.Multiply] to draw saturation picker
 */
fun DrawScope.drawBlendingRectGradient(
    dst: Brush,
    dstTopLeft: Offset = Offset.Zero,
    dstSize: Size = this.size,
    src: Brush,
    srcTopLeft: Offset = Offset.Zero,
    srcSize: Size = this.size,
    blendMode: BlendMode = BlendMode.Multiply
) {
    with(drawContext.canvas.nativeCanvas) {
        val checkPoint = saveLayer(null, null)
        drawRect(dst, dstTopLeft, dstSize)
        drawRect(src, srcTopLeft, srcSize, blendMode = blendMode)
        restoreToCount(checkPoint)
    }
}

/**
 * Draw into layer
 */
fun DrawScope.drawIntoLayer(
    content: DrawScope.() -> Unit
) {
    with(drawContext.canvas.nativeCanvas) {
        val checkPoint = saveLayer(null, null)
        content()
        restoreToCount(checkPoint)
    }
}
