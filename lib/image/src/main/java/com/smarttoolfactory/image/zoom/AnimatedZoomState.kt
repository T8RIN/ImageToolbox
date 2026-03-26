package com.smarttoolfactory.image.zoom

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize

/**
 * Create and [remember] the [AnimatedZoomState] based on the currently appropriate transform
 * configuration to allow changing pan, zoom, and rotation.
 *
 * Allows to change zoom, pan,  translate, or get current state by
 * calling methods on this object. To be hosted and passed to [Modifier.animatedZoom].
 *
 *  [key1] is used to reset remember block to initial calculations. This can be used
 * when image, contentScale or any property changes which requires values to be reset to initial
 * values
 * @param contentSize when the content that will be zoomed is not parent pass child size to
 * bound content correctly inside parent. If parent doesn't have any content this parameter
 * is not required
 * @param initialZoom zoom set initially
 * @param minZoom minimum zoom value
 * @param maxZoom maximum zoom value
 * @param fling when set to true dragging pointer builds up velocity. When last
 * pointer leaves Composable a movement invoked against friction till velocity drops below
 * to threshold
 * @param moveToBounds when set to true if image zoom is lower than initial zoom or
 * panned out of image boundaries moves back to bounds with animation.
 * @param zoomable when set to true zoom is enabled
 * @param pannable when set to true pan is enabled
 * @param rotatable when set to true rotation is enabled
 * @param limitPan limits pan to bounds of parent Composable. Using this flag prevents creating
 * empty space on sides or edges of parent
 */
@Composable
fun rememberAnimatedZoomState(
    contentSize: DpSize = DpSize.Zero,
    initialZoom: Float = 1f,
    minZoom: Float = 1f,
    maxZoom: Float = 5f,
    fling: Boolean = true,
    moveToBounds: Boolean = false,
    zoomable: Boolean = true,
    pannable: Boolean = true,
    rotatable: Boolean = false,
    limitPan: Boolean = true,
    key1: Any? = Unit,
    onChange: (zoom: Float, pan: Offset, rotation: Float) -> Unit = { _, _, _ -> }
): AnimatedZoomState {

    val density = LocalDensity.current

    return remember(key1) {
        val size = if (contentSize == DpSize.Zero) {
            IntSize.Zero
        } else density.run {
            IntSize(
                contentSize.width.roundToPx(),
                contentSize.height.roundToPx()
            )
        }

        AnimatedZoomState(
            contentSize = size,
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
        )
    }
}

/**
 * Create and [remember] the [AnimatedZoomState] based on the currently appropriate transform
 * configuration to allow changing pan, zoom, and rotation.
 *
 * Allows to change zoom, pan,  translate, or get current state by
 * calling methods on this object. To be hosted and passed to [Modifier.animatedZoom].
 *
 *  [key1] or [key2] are used to reset remember block to initial calculations. This can be used
 * when image, contentScale or any property changes which requires values to be reset to initial
 * values
 * @param initialZoom zoom set initially
 * @param minZoom minimum zoom value
 * @param maxZoom maximum zoom value
 * @param fling when set to true dragging pointer builds up velocity. When last
 * pointer leaves Composable a movement invoked against friction till velocity drops below
 * to threshold
 * @param moveToBounds when set to true if image zoom is lower than initial zoom or
 * panned out of image boundaries moves back to bounds with animation.
 * @param zoomable when set to true zoom is enabled
 * @param pannable when set to true pan is enabled
 * @param rotatable when set to true rotation is enabled
 * @param limitPan limits pan to bounds of parent Composable. Using this flag prevents creating
 * empty space on sides or edges of parent
 */
@Composable
fun rememberAnimatedZoomState(
    contentSize: DpSize = DpSize.Zero,
    initialZoom: Float = 1f,
    minZoom: Float = 1f,
    maxZoom: Float = 5f,
    fling: Boolean = true,
    moveToBounds: Boolean = false,
    zoomable: Boolean = true,
    pannable: Boolean = true,
    rotatable: Boolean = false,
    limitPan: Boolean = true,
    key1: Any?,
    key2: Any?,
): AnimatedZoomState {

    val density = LocalDensity.current

    return remember(key1, key2) {

        val size = if (contentSize == DpSize.Zero) {
            IntSize.Zero
        } else density.run {
            IntSize(
                contentSize.width.roundToPx(),
                contentSize.height.roundToPx()
            )
        }

        AnimatedZoomState(
            contentSize = size,
            initialZoom = initialZoom,
            minZoom = minZoom,
            maxZoom = maxZoom,
            fling = fling,
            moveToBounds = moveToBounds,
            zoomable = zoomable,
            pannable = pannable,
            rotatable = rotatable,
            limitPan = limitPan
        )
    }
}

/**
 * Create and [remember] the [AnimatedZoomState] based on the currently appropriate transform
 * configuration to allow changing pan, zoom, and rotation.
 *
 * Allows to change zoom, pan,  translate, or get current state by
 * calling methods on this object. To be hosted and passed to [Modifier.animatedZoom].
 *
 * @param initialZoom zoom set initially
 * @param minZoom minimum zoom value
 * @param maxZoom maximum zoom value
 * @param fling when set to true dragging pointer builds up velocity. When last
 * pointer leaves Composable a movement invoked against friction till velocity drops below
 * to threshold
 * @param moveToBounds when set to true if image zoom is lower than initial zoom or
 * panned out of image boundaries moves back to bounds with animation.
 * @param zoomable when set to true zoom is enabled
 * @param pannable when set to true pan is enabled
 * @param rotatable when set to true rotation is enabled
 * @param limitPan limits pan to bounds of parent Composable. Using this flag prevents creating
 * empty space on sides or edges of parent
 * @param keys are used to reset remember block to initial calculations. This can be used
 * when image, contentScale or any property changes which requires values to be reset to initial
 * values
 */
@Composable
fun rememberAnimatedZoomState(
    contentSize: DpSize = DpSize.Zero,
    initialZoom: Float = 1f,
    minZoom: Float = 1f,
    maxZoom: Float = 5f,
    fling: Boolean = true,
    moveToBounds: Boolean = false,
    zoomable: Boolean = true,
    pannable: Boolean = true,
    rotatable: Boolean = false,
    limitPan: Boolean = true,
    vararg keys: Any?
): AnimatedZoomState {

    val density = LocalDensity.current

    return remember(*keys) {

        val size = if (contentSize == DpSize.Zero) {
            IntSize.Zero
        } else density.run {
            IntSize(
                contentSize.width.roundToPx(),
                contentSize.height.roundToPx()
            )
        }

        AnimatedZoomState(
            contentSize = size,
            initialZoom = initialZoom,
            minZoom = minZoom,
            maxZoom = maxZoom,
            fling = fling,
            moveToBounds = moveToBounds,
            zoomable = zoomable,
            pannable = pannable,
            rotatable = rotatable,
            limitPan = limitPan
        )
    }
}
