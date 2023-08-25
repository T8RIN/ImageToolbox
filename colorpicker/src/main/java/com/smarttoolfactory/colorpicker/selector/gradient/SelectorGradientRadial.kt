package com.smarttoolfactory.colorpicker.selector.gradient

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import com.smarttoolfactory.colorpicker.model.GradientColorState
import com.smarttoolfactory.colorpicker.ui.Pink400
import com.smarttoolfactory.colorpicker.widget.ExpandableColumnWithTitle


@Composable
internal fun RadialGradientSelection(
    gradientColorState: GradientColorState,
    onRadialDimensionsChange: (Offset, Float) -> Unit
) {

    var centerX by remember { mutableStateOf(gradientColorState.centerFriction.x) }
    var centerY by remember { mutableStateOf(gradientColorState.centerFriction.y) }
    var radiusFriction by remember { mutableStateOf(gradientColorState.radiusFriction) }

    onRadialDimensionsChange(Offset(centerX, centerY), radiusFriction)

    ExpandableColumnWithTitle(
        title = "Gradient Center/Radius",
        color = Pink400,
        initialExpandState = false
    ) {
        Column {
            SliderWithPercent(
                modifier = Modifier.fillMaxWidth(),
                title = "CenterX",
                value = centerX
            ) {
                centerX = it
            }

            SliderWithPercent(
                modifier = Modifier.fillMaxWidth(),
                title = "CenterY",
                value = centerY
            ) {
                centerY = it
            }

            SliderWithPercent(
                modifier = Modifier.fillMaxWidth(),
                title = "Radius",
                value = radiusFriction
            ) {
                radiusFriction = it
            }
        }
    }
}