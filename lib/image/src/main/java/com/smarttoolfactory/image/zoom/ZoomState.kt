package com.smarttoolfactory.image.zoom

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * * Create and [remember] the [ZoomState] based on the currently appropriate transform
 * configuration to allow changing pan, zoom, and rotation.
 *
 *  [key1] is used to reset remember block to initial calculations. This can be used
 * when image, contentScale or any property changes which requires values to be reset to initial
 * values
 *
 * @param initialZoom zoom set initially
 * @param initialRotation rotation set initially
 * @param minZoom minimum zoom value
 * @param maxZoom maximum zoom value
 * @param limitPan limits pan to bounds of parent Composable. Using this flag prevents creating
 * empty space on sides or edges of parent
 * @param zoomable when set to true zoom is enabled
 * @param pannable when set to true pan is enabled
 * @param rotatable when set to true rotation is enabled
 */
@Composable
fun rememberZoomState(
    initialZoom: Float = 1f,
    initialRotation: Float = 0f,
    minZoom: Float = 1f,
    maxZoom: Float = 5f,
    zoomable: Boolean = true,
    pannable: Boolean = true,
    rotatable: Boolean = false,
    limitPan: Boolean = false,
    key1: Any? = Unit
): ZoomState {
    return remember(key1) {
        ZoomState(
            initialZoom = initialZoom,
            initialRotation = initialRotation,
            minZoom = minZoom,
            maxZoom = maxZoom,
            zoomable = zoomable,
            pannable = pannable,
            rotatable = rotatable,
            limitPan = limitPan
        )
    }
}

/**
 * * Create and [remember] the [ZoomState] based on the currently appropriate transform
 * configuration to allow changing pan, zoom, and rotation.
 *
 *  [key1] or [key2] are used to reset remember block to initial calculations. This can be used
 * when image, contentScale or any property changes which requires values to be reset to initial
 * values
 *
 * @param initialZoom zoom set initially
 * @param initialRotation rotation set initially
 * @param minZoom minimum zoom value
 * @param maxZoom maximum zoom value
 * @param limitPan limits pan to bounds of parent Composable. Using this flag prevents creating
 * empty space on sides or edges of parent
 * @param zoomable when set to true zoom is enabled
 * @param pannable when set to true pan is enabled
 * @param rotatable when set to true rotation is enabled
 */
@Composable
fun rememberZoomState(
    initialZoom: Float = 1f,
    initialRotation: Float = 0f,
    minZoom: Float = 1f,
    maxZoom: Float = 5f,
    zoomable: Boolean = true,
    pannable: Boolean = true,
    rotatable: Boolean = false,
    limitPan: Boolean = false,
    key1: Any?,
    key2: Any?,
): ZoomState {
    return remember(key1, key2) {
        ZoomState(
            initialZoom = initialZoom,
            initialRotation = initialRotation,
            minZoom = minZoom,
            maxZoom = maxZoom,
            zoomable = zoomable,
            pannable = pannable,
            rotatable = rotatable,
            limitPan = limitPan
        )
    }
}

/**
 * * Create and [remember] the [ZoomState] based on the currently appropriate transform
 * configuration to allow changing pan, zoom, and rotation.
 *
 * @param initialZoom zoom set initially
 * @param initialRotation rotation set initially
 * @param minZoom minimum zoom value
 * @param maxZoom maximum zoom value
 * @param limitPan limits pan to bounds of parent Composable. Using this flag prevents creating
 * empty space on sides or edges of parent
 * @param zoomable when set to true zoom is enabled
 * @param pannable when set to true pan is enabled
 * @param rotatable when set to true rotation is enabled
 * @param keys are used to reset remember block to initial calculations. This can be used
 * when image, contentScale or any property changes which requires values to be reset to initial
 * values
 */
@Composable
fun rememberZoomState(
    initialZoom: Float = 1f,
    initialRotation: Float = 0f,
    minZoom: Float = 1f,
    maxZoom: Float = 5f,
    zoomable: Boolean = true,
    pannable: Boolean = true,
    rotatable: Boolean = false,
    limitPan: Boolean = false,
    vararg keys: Any?
): ZoomState {
    return remember(*keys) {
        ZoomState(
            initialZoom = initialZoom,
            initialRotation = initialRotation,
            minZoom = minZoom,
            maxZoom = maxZoom,
            zoomable = zoomable,
            pannable = pannable,
            rotatable = rotatable,
            limitPan = limitPan
        )
    }
}