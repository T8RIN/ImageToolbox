package net.engawapg.lib.zoomable

import androidx.compose.ui.geometry.Offset

object ZoomableDefaults {

    val ZoomState.defaultZoomOnDoubleTap: suspend (position: Offset) -> Unit
        get() = { position -> toggleScale(targetScale = 5f, position = position) }

    val ZoomState.threeLevelZoomOnDoubleTap: suspend (position: Offset) -> Unit
        get() = { position ->
            val scale = scale
            val minScale = minScale
            val maxScale = maxScale
            val midScale = (maxScale - minScale) / 2f

            val newScale = when (scale) {
                in minScale..<midScale -> (maxScale - minScale) / 2f
                in midScale..<maxScale -> maxScale
                else -> minScale
            }
            changeScale(newScale, position)
        }

    val DefaultEnabled: (Float, Offset) -> Boolean get() = { _, _ -> true }
}