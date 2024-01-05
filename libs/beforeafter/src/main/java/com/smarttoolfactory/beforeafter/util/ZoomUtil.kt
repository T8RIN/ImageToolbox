package com.smarttoolfactory.beforeafter.util

import androidx.compose.ui.graphics.GraphicsLayerScope
import com.smarttoolfactory.beforeafter.ZoomState

/**
 * Update graphic layer with [zoomState]
 */
internal fun GraphicsLayerScope.update(zoomState: ZoomState) {

    // Set zoom
    val zoom = zoomState.zoom
    this.scaleX = zoom
    this.scaleY = zoom

    // Set pan
    val pan = zoomState.pan
    val translationX = pan.x
    val translationY = pan.y
    this.translationX = translationX
    this.translationY = translationY

    // Set rotation
    this.rotationZ = zoomState.rotation
}

