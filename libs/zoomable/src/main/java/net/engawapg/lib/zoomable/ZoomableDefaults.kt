package net.engawapg.lib.zoomable

import androidx.compose.ui.geometry.Offset

object ZoomableDefaults {

    val ZoomState.defaultZoomOnDoubleTap: suspend (position: Offset, level: DoubleTapZoomLevel) -> Unit
        get() = { position, level ->
            when (level) {
                DoubleTapZoomLevel.Min -> {
                    changeScale(minScale, position)
                }

                DoubleTapZoomLevel.Mid -> {
                    changeScale((maxScale - minScale) / 2f, position)
                }

                DoubleTapZoomLevel.Max -> {
                    changeScale(maxScale, position)
                }
            }
        }

    val DefaultEnabled: (Float, Offset) -> Boolean get() = { _, _ -> true }
}