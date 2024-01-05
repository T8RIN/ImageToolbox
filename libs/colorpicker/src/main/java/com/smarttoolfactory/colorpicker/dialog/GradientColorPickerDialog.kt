package com.smarttoolfactory.colorpicker.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.smarttoolfactory.colorpicker.model.BrushColor
import com.smarttoolfactory.colorpicker.model.GradientColorState
import com.smarttoolfactory.colorpicker.model.rememberGradientColorState
import com.smarttoolfactory.colorpicker.picker.gradient.ColorPickerGradientRingDiamondHSL
import com.smarttoolfactory.colorpicker.picker.gradient.ColorPickerGradientRingRectHSL
import com.smarttoolfactory.colorpicker.picker.gradient.ColorPickerGradientRingRectHSV
import com.smarttoolfactory.colorpicker.ui.Blue400

@Composable
fun ColorPickerRingDiamondGradientHSLDialog(
    initialBrushColor: BrushColor,
    gradientColorState: GradientColorState = rememberGradientColorState(),
    ringOuterRadiusFraction: Float = .9f,
    ringInnerRadiusFraction: Float = .6f,
    ringBackgroundColor: Color = Color.Transparent,
    ringBorderStrokeColor: Color = Color.Black,
    ringBorderStrokeWidth: Dp = 4.dp,
    selectionRadius: Dp = 8.dp,
    onDismiss: (BrushColor) -> Unit
) {

    var brushColor: BrushColor by remember { mutableStateOf(initialBrushColor.copy()) }

    Dialog(
        onDismissRequest = {
            onDismiss(brushColor)
        }
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ColorPickerGradientRingDiamondHSL(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xcc212121), shape = RoundedCornerShape(5.dp))
                    .padding(horizontal = 10.dp, vertical = 2.dp),
                initialBrushColor = initialBrushColor,
                gradientColorState = gradientColorState,
                ringOuterRadiusFraction = ringOuterRadiusFraction,
                ringInnerRadiusFraction = ringInnerRadiusFraction,
                ringBackgroundColor = ringBackgroundColor,
                ringBorderStrokeColor = ringBorderStrokeColor,
                ringBorderStrokeWidth = ringBorderStrokeWidth,
                selectionRadius = selectionRadius,
                onBrushColorChange = {
                    brushColor = it
                }
            )

            FloatingActionButton(
                onClick = { onDismiss(brushColor) },
                backgroundColor = Color.Black
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = null,
                    tint = Blue400
                )
            }
        }
    }
}

@Composable
fun ColorPickerRingRectGradientHSLDialog(
    initialBrushColor: BrushColor,
    gradientColorState: GradientColorState = rememberGradientColorState(),
    ringOuterRadiusFraction: Float = .9f,
    ringInnerRadiusFraction: Float = .6f,
    ringBackgroundColor: Color = Color.Transparent,
    ringBorderStrokeColor: Color = Color.Black,
    ringBorderStrokeWidth: Dp = 4.dp,
    selectionRadius: Dp = 8.dp,
    onDismiss: (BrushColor) -> Unit
) {

    var brushColor: BrushColor by remember { mutableStateOf(initialBrushColor.copy()) }

    Dialog(
        onDismissRequest = {
            onDismiss(brushColor)
        }
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ColorPickerGradientRingRectHSL(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xcc212121), shape = RoundedCornerShape(5.dp))
                    .padding(horizontal = 10.dp, vertical = 2.dp),
                initialBrushColor = initialBrushColor,
                gradientColorState = gradientColorState,
                ringOuterRadiusFraction = ringOuterRadiusFraction,
                ringInnerRadiusFraction = ringInnerRadiusFraction,
                ringBackgroundColor = ringBackgroundColor,
                ringBorderStrokeColor = ringBorderStrokeColor,
                ringBorderStrokeWidth = ringBorderStrokeWidth,
                selectionRadius = selectionRadius,
                onBrushColorChange = {
                    brushColor = it
                }
            )

            FloatingActionButton(
                onClick = { onDismiss(brushColor) },
                backgroundColor = Color.Black
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = null,
                    tint = Blue400
                )
            }
        }
    }
}

@Composable
fun ColorPickerRingRectGradientHSVDialog(
    initialBrushColor: BrushColor,
    gradientColorState: GradientColorState = rememberGradientColorState(),
    ringOuterRadiusFraction: Float = .9f,
    ringInnerRadiusFraction: Float = .6f,
    ringBackgroundColor: Color = Color.Transparent,
    ringBorderStrokeColor: Color = Color.Black,
    ringBorderStrokeWidth: Dp = 4.dp,
    selectionRadius: Dp = 8.dp,
    onDismiss: (BrushColor) -> Unit
) {

    var brushColor: BrushColor by remember { mutableStateOf(initialBrushColor.copy()) }

    Dialog(
        onDismissRequest = {
            onDismiss(brushColor)
        }
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ColorPickerGradientRingRectHSV(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xcc212121), shape = RoundedCornerShape(5.dp))
                    .padding(horizontal = 10.dp, vertical = 2.dp),
                initialBrushColor = initialBrushColor,
                gradientColorState = gradientColorState,
                ringOuterRadiusFraction = ringOuterRadiusFraction,
                ringInnerRadiusFraction = ringInnerRadiusFraction,
                ringBackgroundColor = ringBackgroundColor,
                ringBorderStrokeColor = ringBorderStrokeColor,
                ringBorderStrokeWidth = ringBorderStrokeWidth,
                selectionRadius = selectionRadius,
                onBrushColorChange = {
                    brushColor = it
                }
            )

            FloatingActionButton(
                onClick = { onDismiss(brushColor) },
                backgroundColor = Color.Black
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = null,
                    tint = Blue400
                )
            }
        }
    }
}