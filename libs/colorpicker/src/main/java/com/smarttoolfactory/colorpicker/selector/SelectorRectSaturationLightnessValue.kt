package com.smarttoolfactory.colorpicker.selector

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.smarttoolfactory.colorpicker.model.ColorModel
import com.smarttoolfactory.colorpicker.ui.brush.lightnessGradient
import com.smarttoolfactory.colorpicker.ui.brush.saturationHSLGradient
import com.smarttoolfactory.colorpicker.ui.brush.saturationHSVGradient
import com.smarttoolfactory.colorpicker.ui.brush.valueGradient
import com.smarttoolfactory.colorpicker.util.drawBlendingRectGradient
import com.smarttoolfactory.gesture.detectMotionEvents

/**
 * Rectangle Saturation and Lightness selector for
 * [HSL](https://en.wikipedia.org/wiki/HSL_and_HSV) color model
 * @param hue is in [0f..360f] of HSL color
 * @param lightness is in [0f..1f] of HSL color
 * @param selectionRadius radius of selection circle that moves based on touch position
 * @param onChange callback that returns [hue] and [lightness]
 *  when position of touch in this selector has changed.
 */
@Composable
fun SelectorRectSaturationLightnessHSL(
    modifier: Modifier = Modifier,
    hue: Float,
    saturation: Float = 0.5f,
    lightness: Float = 0.5f,
    selectionRadius: Dp = Dp.Unspecified,
    onChange: (Float, Float) -> Unit
) {
    val lightnessGradient = lightnessGradient(hue)
    val hueSaturation = saturationHSLGradient(hue)

    SelectorRect(
        modifier = modifier,
        saturation = saturation,
        property = lightness,
        brushSrc = hueSaturation,
        brushDst = lightnessGradient,
        selectionRadius = selectionRadius,
        onChange = onChange,
        colorModel = ColorModel.HSL
    )
}

/**
 * Rectangle Saturation and Vale selector for
 * [HSV](https://en.wikipedia.org/wiki/HSL_and_HSV) color model
 * @param hue is in [0f..360f] of HSL color
 * @param value is in [0f..1f] of HSL color
 * @param selectionRadius radius of selection circle that moves based on touch position
 * @param onChange callback that returns [hue] and [value]
 *  when position of touch in this selector has changed.
 */
@Composable
fun SelectorRectSaturationValueHSV(
    modifier: Modifier = Modifier,
    hue: Float,
    saturation: Float = 0.5f,
    value: Float = 0.5f,
    selectionRadius: Dp = Dp.Unspecified,
    onChange: (Float, Float) -> Unit
) {

    val valueGradient = valueGradient(hue)
    val hueSaturation = saturationHSVGradient(hue)

    SelectorRect(
        modifier = modifier,
        saturation = saturation,
        property = value,
        brushSrc = hueSaturation,
        brushDst = valueGradient,
        selectionRadius = selectionRadius,
        onChange = onChange,
        colorModel = ColorModel.HSV
    )
}

@Composable
private fun SelectorRect(
    modifier: Modifier = Modifier,
    saturation: Float = 0.5f,
    property: Float = 0.5f,
    brushSrc: Brush,
    brushDst: Brush,
    selectionRadius: Dp = Dp.Unspecified,
    onChange: (Float, Float) -> Unit,
    colorModel: ColorModel
) {

    BoxWithConstraints(modifier) {

        val density = LocalDensity.current.density
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()

        // Center position of color picker
        val center = Offset(width / 2, height / 2)

        /**
         * Circle selector radius for setting [saturation] and [property] by gesture
         */
        val selectorRadius =
            if (selectionRadius != Dp.Unspecified) selectionRadius.value * density
            else width.coerceAtMost(height) * .04f

        /**
         *  Current position is initially set by [saturation] and [property] that is bound
         *  in diamond since (1,1) points to bottom left corner of a rectangle but it's bounded
         *  in diamond by [setSelectorPositionFromColorParams].
         *  When user touches anywhere in diamond current position is updated and
         *  this composable is recomposed
         */
        var currentPosition by remember {
            mutableStateOf(center)
        }

        val posX = saturation * width
        val posY = (1 - property) * height
        currentPosition = Offset(posX, posY)


        val canvasModifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectMotionEvents(
                    onDown = {
                        val position = it.position
                        val saturationChange = (position.x / width).coerceIn(0f, 1f)
                        val valueChange = (1 - (position.y / height)).coerceIn(0f, 1f)
                        onChange(saturationChange, valueChange)
                        it.consume()

                    },
                    onMove = {
                        val position = it.position
                        val saturationChange = (position.x / width).coerceIn(0f, 1f)
                        val valueChange = (1 - (position.y / height)).coerceIn(0f, 1f)
                        onChange(saturationChange, valueChange)
                        it.consume()
                    },
                    delayAfterDownInMillis = 20
                )
            }

        SelectorRectImpl(
            modifier = canvasModifier,
            brushSrc = brushSrc,
            brushDst = brushDst,
            selectorPosition = currentPosition,
            selectorRadius = selectorRadius,
            colorModel = colorModel
        )
    }
}

@Composable
private fun SelectorRectImpl(
    modifier: Modifier,
    brushSrc: Brush,
    brushDst: Brush,
    selectorPosition: Offset,
    selectorRadius: Float,
    colorModel: ColorModel
) {

    Canvas(modifier = modifier) {
        drawBlendingRectGradient(
            dst = brushDst,
            src = brushSrc,
            blendMode = if (colorModel == ColorModel.HSV) BlendMode.Multiply else BlendMode.Overlay
        )
        // Saturation and Value/Lightness or value selector
        drawHueSelectionCircle(
            center = selectorPosition,
            radius = selectorRadius
        )
    }
}
