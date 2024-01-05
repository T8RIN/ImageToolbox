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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.smarttoolfactory.colorpicker.ui.brush.transparentToBlackVerticalGradient
import com.smarttoolfactory.colorpicker.ui.brush.transparentToGrayVerticalGradient
import com.smarttoolfactory.colorpicker.ui.brush.transparentToWhiteVerticalGradient
import com.smarttoolfactory.colorpicker.ui.brush.whiteToTransparentToBlackHorizontalGradient
import com.smarttoolfactory.colorpicker.ui.brush.whiteToTransparentToBlackVerticalGradient
import com.smarttoolfactory.colorpicker.ui.gradientColorScaleHSL
import com.smarttoolfactory.colorpicker.ui.gradientColorScaleHSV
import com.smarttoolfactory.gesture.detectMotionEvents

/**
 * Rectangle Hue and Value selector for
 * [HSV](https://en.wikipedia.org/wiki/HSL_and_HSV) color model
 * @param hue is in [0f..360f] of HSV color
 * @param value is in [0f..1f] of HSV color
 * @param selectionRadius radius of selection circle that moves based on touch position
 * @param onChange callback that returns [hue] and [value]
 *  when position of touch in this selector has changed.
 */
@Composable
fun SelectorRectHueValueHSV(
    modifier: Modifier = Modifier,
    hue: Float,
    value: Float,
    selectionRadius: Dp = Dp.Unspecified,
    onChange: (Float, Float) -> Unit
) {

    val selectorType = SelectorType.HVWithHSV

    //  Red, Magenta, Blue, Cyan, Green, Yellow, Red
    val colorScaleHSLGradient = remember {
        Brush.linearGradient(
            colors = gradientColorScaleHSV,
            start = Offset.Zero,
            end = Offset(Float.POSITIVE_INFINITY, 0f)
        )
    }
    val transparentToBlackGradient = remember {
        transparentToBlackVerticalGradient()
    }

    SelectorRect(
        modifier = modifier,
        hue = hue,
        property = value,
        selectionRadius = selectionRadius,
        brushHue = colorScaleHSLGradient,
        brushProperty = transparentToBlackGradient,
        selectorType = selectorType,
        onChange = onChange
    )
}

/**
 * Rectangle Hue and Saturation selector for
 * [HSV](https://en.wikipedia.org/wiki/HSL_and_HSV) color model
 * @param hue is in [0f..360f] of HSV color
 * @param saturation is in [0f..1f] of HSV color
 * @param selectionRadius radius of selection circle that moves based on touch position
 * @param onChange callback that returns [hue] and [saturation]
 *  when position of touch in this selector has changed.
 */
@Composable
fun SelectorRectHueSaturationHSV(
    modifier: Modifier = Modifier,
    hue: Float,
    saturation: Float,
    selectionRadius: Dp = Dp.Unspecified,
    onChange: (Float, Float) -> Unit
) {

    val selectorType = SelectorType.HSWithHSV

    //  Red, Magenta, Blue, Cyan, Green, Yellow, Red
    val colorScaleHSLGradient = remember {
        Brush.linearGradient(
            colors = gradientColorScaleHSV,
            start = Offset.Zero,
            end = Offset(Float.POSITIVE_INFINITY, 0f)
        )
    }
    val transparentToWhiteGradient = remember {
        transparentToWhiteVerticalGradient()
    }

    SelectorRect(
        modifier = modifier,
        hue = hue,
        property = saturation,
        selectionRadius = selectionRadius,
        brushHue = colorScaleHSLGradient,
        brushProperty = transparentToWhiteGradient,
        selectorType = selectorType,
        onChange = { h, p ->
            onChange(h, 1 - p)
        }
    )
}

/**
 * Rectangle Hue and Saturation selector for
 * [HSL](https://en.wikipedia.org/wiki/HSL_and_HSV) color model
 * @param hue is in [0f..360f] of HSL color
 * @param saturation is in [0f..1f] of HSL color
 * @param selectionRadius radius of selection circle that moves based on touch position
 * @param onChange callback that returns [hue] and [saturation]
 *  when position of touch in this selector has changed.
 */
@Composable
fun SelectorRectHueSaturationHSL(
    modifier: Modifier = Modifier,
    hue: Float,
    saturation: Float,
    selectionRadius: Dp = Dp.Unspecified,
    onChange: (Float, Float) -> Unit
) {

    val selectorType = SelectorType.HSWithHSL

    //  Red, Magenta, Blue, Cyan, Green, Yellow, Red
    val colorScaleHSLGradient = remember {
        Brush.linearGradient(
            colors = gradientColorScaleHSL,
            start = Offset.Zero,
            end = Offset(Float.POSITIVE_INFINITY, 0f)
        )
    }
    val transparentToGrayGradient = remember {
        transparentToGrayVerticalGradient()
    }

    SelectorRect(
        modifier = modifier,
        hue = hue,
        property = saturation,
        selectionRadius = selectionRadius,
        brushHue = colorScaleHSLGradient,
        brushProperty = transparentToGrayGradient,
        selectorType = selectorType,
        onChange = onChange
    )
}

/**
 * Rectangle Hue and Lightness selector for
 * [HSL](https://en.wikipedia.org/wiki/HSL_and_HSV) color model
 * @param hue is in [0f..360f] of HSL color
 * @param lightness is in [0f..1f] of HSL color
 * @param selectionRadius radius of selection circle that moves based on touch position
 * @param onChange callback that returns [hue] and [lightness]
 *  when position of touch in this selector has changed.
 */
@Composable
fun SelectorRectHueLightnessHSL(
    modifier: Modifier = Modifier,
    hue: Float,
    lightness: Float,
    selectionRadius: Dp = Dp.Unspecified,
    onChange: (Float, Float) -> Unit
) {

    val selectorType = SelectorType.HLWithHSL

    //  Red, Magenta, Blue, Cyan, Green, Yellow, Red
    val colorScaleHSLGradient = remember {
        Brush.linearGradient(
            colors = gradientColorScaleHSL,
            start = Offset.Zero,
            end = Offset(Float.POSITIVE_INFINITY, 0f)
        )
    }
    val transitionGradient = remember {
        whiteToTransparentToBlackVerticalGradient()
    }

    SelectorRect(
        modifier = modifier,
        hue = hue,
        property = lightness,
        selectionRadius = selectionRadius,
        brushHue = colorScaleHSLGradient,
        brushProperty = transitionGradient,
        selectorType = selectorType,
        onChange = onChange
    )
}

@Composable
fun SelectorRectHueLightnessHSLHorizontal(
    modifier: Modifier = Modifier,
    hue: Float,
    lightness: Float,
    selectionRadius: Dp = Dp.Unspecified,
    onChange: (Float, Float) -> Unit
) {

    val selectorType = SelectorType.HLWithHSL
    //  Red, Magenta, Blue, Cyan, Green, Yellow, Red
    val colorScaleHSLGradient = remember {
        Brush.linearGradient(
            colors = gradientColorScaleHSL,
            start = Offset.Zero,
            end = Offset(0f, Float.POSITIVE_INFINITY),
        )
    }
    val transitionGradient = remember {
        whiteToTransparentToBlackHorizontalGradient()
    }

    SelectorRectHorizontal(
        modifier = modifier,
        hue = hue,
        property = lightness,
        selectionRadius = selectionRadius,
        brushHue = colorScaleHSLGradient,
        brushProperty = transitionGradient,
        selectorType = selectorType,
        onChange = onChange
    )
}

/**
 * Base Selector rectangle for Hue and another property such as Saturation or Value in HSV or
 * Saturation or Lightness in HSL color mode.
 *
 * @param hue is in [0f..360f] of HSL color
 * @param property is in [0f..1f] either saturation or value for HSV color model or saturation or
 * lightness for HSL color model
 * @param selectionRadius radius of selection circle that moves based on touch position
 * @param brushHue brush for color scale(hue) in HSV or HSL
 * @param brushProperty transparent layer above  [brushHue] that determines whether this is
 * a value, saturation or lightness selector for specified [selectorType]
 * @param selectorType is one of the 4 selections of Hue-Saturation or Hue-Lightness for HSL
 * or Hue-Saturation or Hue-Value for HSV
 * @param onChange callback that triggered when input position on selector has changed
 */
@Composable
private fun SelectorRect(
    modifier: Modifier = Modifier,
    hue: Float,
    property: Float,
    selectionRadius: Dp = Dp.Unspecified,
    brushHue: Brush,
    brushProperty: Brush,
    selectorType: SelectorType,
    onChange: (Float, Float) -> Unit
) {
    BoxWithConstraints(modifier) {

        val density = LocalDensity.current.density

        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()

        // Center position of color picker
        val center = Offset(width / 2, height / 2)

        var currentPosition by remember {
            mutableStateOf(center)
        }

        val posX = hue / 360 * width
        val posY = (1 - property) * height
        currentPosition = Offset(posX, posY)

        val selectorRadius =
            if (selectionRadius != Dp.Unspecified) selectionRadius.value * density
            else width.coerceAtMost(height) * .04f

        val canvasModifier = Modifier
            .pointerInput(Unit) {
                detectMotionEvents(
                    onDown = {
                        val position = it.position
                        val hueChange = (position.x / width).coerceIn(0f, 1f) * 360f
                        val propertyChange = if (selectorType == SelectorType.HSWithHSV) {
                            (position.y / height).coerceIn(0f, 1f)
                        } else {
                            (1 - (position.y / height)).coerceIn(0f, 1f)
                        }
                        onChange(hueChange, propertyChange)
                        it.consume()

                    },
                    onMove = {
                        val position = it.position
                        val hueChange = (position.x / width).coerceIn(0f, 1f) * 360f
                        val propertyChange = if (selectorType == SelectorType.HSWithHSV) {
                            (position.y / height).coerceIn(0f, 1f)
                        } else {
                            (1 - (position.y / height)).coerceIn(0f, 1f)
                        }
                        onChange(hueChange, propertyChange)
                        it.consume()
                    },
                    delayAfterDownInMillis = 20
                )
            }

        SelectorRectImpl(
            modifier = canvasModifier,
            selectorPosition = currentPosition,
            brushHue = brushHue,
            brushProperty = brushProperty,
            selectorRadius = selectorRadius
        )

    }
}

@Composable
private fun SelectorRectHorizontal(
    modifier: Modifier = Modifier,
    hue: Float,
    property: Float,
    selectionRadius: Dp = Dp.Unspecified,
    brushHue: Brush,
    brushProperty: Brush,
    selectorType: SelectorType,
    onChange: (Float, Float) -> Unit
) {
    BoxWithConstraints(modifier) {

        val density = LocalDensity.current.density

        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()

        // Center position of color picker
        val center = Offset(width / 2, height / 2)

        var currentPosition by remember {
            mutableStateOf(center)
        }

        val posX = (1 - property) * width
        val posY = hue / 360 * height
        currentPosition = Offset(posX, posY)

        val selectorRadius =
            if (selectionRadius != Dp.Unspecified) selectionRadius.value * density
            else width.coerceAtMost(height) * .04f

        val canvasModifier = Modifier
            .pointerInput(Unit) {
                detectMotionEvents(
                    onDown = {
                        val position = it.position
                        val hueChange = (position.y / width).coerceIn(0f, 1f) * 360f
                        val propertyChange = if (selectorType == SelectorType.HSWithHSV) {
                            (position.x / height).coerceIn(0f, 1f)
                        } else {
                            (1 - (position.x / height)).coerceIn(0f, 1f)
                        }
                        onChange(hueChange, propertyChange)
                        it.consume()

                    },
                    onMove = {
                        val position = it.position
                        val hueChange = (position.y / width).coerceIn(0f, 1f) * 360f
                        val propertyChange = if (selectorType == SelectorType.HSWithHSV) {
                            (position.x / height).coerceIn(0f, 1f)
                        } else {
                            (1 - (position.x / height)).coerceIn(0f, 1f)
                        }
                        onChange(hueChange, propertyChange)
                        it.consume()
                    },
                    delayAfterDownInMillis = 20
                )
            }

        SelectorRectImpl(
            modifier = canvasModifier,
            selectorPosition = currentPosition,
            brushHue = brushHue,
            brushProperty = brushProperty,
            selectorRadius = selectorRadius
        )

    }
}


@Composable
private fun SelectorRectImpl(
    modifier: Modifier,
    selectorPosition: Offset,
    brushHue: Brush,
    brushProperty: Brush,
    selectorRadius: Float
) {
    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        // Draw hue and saturation selection gradients
        drawRect(brush = brushHue)
        drawRect(brush = brushProperty)

        drawHueSelectionCircle(
            center = selectorPosition,
            radius = selectorRadius
        )
    }
}

enum class SelectorType {
    HLWithHSL, HSWithHSL, HSWithHSV, HVWithHSV
}
