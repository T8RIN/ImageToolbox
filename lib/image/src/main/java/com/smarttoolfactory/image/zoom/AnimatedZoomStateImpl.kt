package com.smarttoolfactory.image.zoom

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize

open class AnimatedZoomState(
    private val contentSize: IntSize = IntSize.Zero,
    initialZoom: Float = 1f,
    minZoom: Float = .5f,
    maxZoom: Float = 5f,
    fling: Boolean = false,
    moveToBounds: Boolean = true,
    zoomable: Boolean = true,
    pannable: Boolean = true,
    rotatable: Boolean = false,
    limitPan: Boolean = false,
    onChange: (zoom: Float, pan: Offset, rotation: Float) -> Unit = { _, _, _ -> }
) : BaseEnhancedZoomState(
    initialZoom = initialZoom,
    minZoom = minZoom,
    maxZoom = maxZoom,
    fling = fling,
    moveToBounds = moveToBounds,
    zoomable = zoomable,
    pannable = pannable,
    rotatable = rotatable,
    limitPan = limitPan,
    onChange = onChange
) {

    override fun getBounds(size: IntSize): Offset {

        val contentWidth: Int
        val contentHeight: Int
        if (contentSize == IntSize.Zero) {
            contentWidth = size.width
            contentHeight = size.height
        } else {
            contentWidth = contentSize.width
            contentHeight = contentSize.height
        }

        val maxX = ((contentWidth * zoom - size.width) / 2f).coerceAtLeast(0f)
        val maxY = ((contentHeight * zoom - size.height) / 2f).coerceAtLeast(0f)
        return Offset(maxX, maxY)
    }
}