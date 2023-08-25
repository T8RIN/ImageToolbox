package com.smarttoolfactory.colorpicker.model

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor


/**
 * Data class that contains [Brush] and [Color] and can return either based on user selection.
 */
data class BrushColor(
    var color: Color = Color.Unspecified,
    var brush: Brush? = null
) {
    /**
     * [Brush] that is not **null** [brush] property or [SolidColor] that is not nullable and
     * contains [color] property as [SolidColor.value]
     */
    val activeBrush: Brush
        get() = brush ?: solidColor

    /**
     * [SolidColor] is a [Brush] that
     * wraps [color] property that is used for [activeBrush] if [brush] property is **null**
     */
    val solidColor = SolidColor(color)
}
