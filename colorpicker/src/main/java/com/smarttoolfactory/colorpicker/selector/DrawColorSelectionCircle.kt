package com.smarttoolfactory.colorpicker.selector

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 * Draw selection circle with white and black colors
 * Stroke draws half in and half out of the current radius.
 * With 200 radius 20 stroke width starts from 190 and ends at 210.
 * @param center center of the selection circle
 * @param radius radius of the selection circle
 */
fun DrawScope.drawHueSelectionCircle(
    center: Offset,
    radius: Float
) {
    drawCircle(
        Color.White,
        radius = radius,
        center = center,
        style = Stroke(width = radius / 4)
    )

    drawCircle(
        Color.Black,
        radius = radius + radius / 8,
        center = center,
        style = Stroke(width = radius / 8)
    )
}