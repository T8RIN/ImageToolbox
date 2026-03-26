package com.smarttoolfactory.extendedcolors.util

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.ColorUtils
import com.smarttoolfactory.extendedcolors.util.ColorUtil.colorIntToRGBArray

object HSLUtil {

    /**
     * Convert HSL components(hue-saturation-lightness) to HSV
     * (hue-saturation-value) components.
     * @param hue in [0..360f]
     * @param saturation in [0..1f]
     * @param lightness in [0..1f]
     */
    fun hslToHSV(
        hue: Float,
        saturation: Float,
        lightness: Float
    ): FloatArray {
        val value = lightness + saturation * lightness.coerceAtMost(1 - lightness)
        val saturationHSV = if (value == 0f) 0f else 2 * (1 - lightness / value)
        return floatArrayOf(hue, saturationHSV.coerceIn(0f, 1f), value.coerceIn(0f, 1f))
    }

    /**
     * Convert HSL components(hue-saturation-lightness) to HSV
     * (hue-saturation-value) components.
     *
     * ```
     * hsv[0] is Hue [0 .. 360)
     * hsv[1] is Saturation [0...1]
     * hsv[2] is Value [0...1]
     * ```
     * ```
     * hsl[0] is Hue [0 .. 360)
     * hsl[1] is Saturation [0...1]
     * hsl[2] is Lightness [0...1]
     * ```
     */
    fun hslToHSV(hslIn: FloatArray): FloatArray {
        return hslToHSV(hslIn[0], hslIn[1], hslIn[2])
    }

    /*
        HSL-ColorInt Conversions
     */

    /**
     * Convert HSL (hue-saturation-lightness) components to a RGB color in [Integer] format.
     *
     * For instance, red =255, green =0, blue=0 is -65536
     * ```
     * hsl[0] is Hue [0 .. 360)
     * hsl[1] is Saturation [0...1]
     * hsl[2] is Lightness [0...1]
     * ```
     */
    fun hslToColorInt(hslIn: FloatArray): Int {
        return hslToColorInt(hslIn[0], hslIn[1], hslIn[2])
    }

    /**
     * Convert HSL (hue-saturation-lightness) components to a RGB color in [Integer] format.
     * @param hue in [0..360f]
     * @param saturation in [0..1f]
     * @param lightness in [0..1f]
     */
    fun hslToColorInt(
        hue: Float,
        saturation: Float,
        lightness: Float
    ): Int {
        return ColorUtils.HSLToColor(floatArrayOf(hue, saturation, lightness))
    }


    /*
        HSL-RGB Conversions
     */

    /**
     * Convert HSL (hue-saturation-lightness) components to a RGB red, green blue array.
     * ```
     * hsl[0] is Hue [0 .. 360)
     * hsl[1] is Saturation [0...1]
     * hsl[2] is Lightness [0...1]
     * ```
     * ```
     * rgb[0] is Red [0 .. 255]
     * rgb[1] is Green [0...255]
     * rgb[2] is Blue [0...255]
     * ```
     * @param hslIn 3 element array which holds the input HSL components.
     */
    fun hslToRGB(hslIn: FloatArray): IntArray {
        return colorIntToRGBArray(hslToColorInt(hslIn))
    }

    /**
     * Convert HSL (hue-saturation-lightness) components to a RGB red, green blue array.
     * ```
     * Hue is [0 .. 360)
     * Saturation is [0...1]
     * Lightness is [0...1]
     * ```
     *
     * ```
     * rgb[0] is Red [0 .. 255]
     * rgb[1] is Green [0...255]
     * rgb[2] is Blue [0...255]
     * ```
     */
    fun hslToRGB(
        hue: Float,
        saturation: Float,
        lightness: Float
    ): IntArray {
        return colorIntToRGBArray(
            color = hslToColorInt(hue, saturation, lightness)
        )
    }

    /**
     * Convert HSL (hue-saturation-lightness) components to a RGB red, green blue array.
     * ```
     * rgb[0] is Red [0 .. 255]
     * rgb[1] is Green [0...255]
     * rgb[2] is Blue [0...255]
     * ```
     * @param hue in [0..360f]
     * @param saturation in [0..1f]
     * @param lightness in [0..1f]
     * @param rgbIn 3 element array which holds the input RGB components.
     */
    fun hslToRGB(
        hue: Float,
        saturation: Float,
        lightness: Float,
        rgbIn: IntArray
    ) {
        colorIntToRGBArray(hslToColorInt(hue, saturation, lightness), rgbIn)
    }


    /**
     * Convert HSL (hue-saturation-lightness) to RGB red, green, blue components in [0f..1f] range.
     * ```
     * rgb[0] is Red [0f .. 1f)
     * rgb[1] is Green [0f...1f]
     * rgb[2] is Blue [0f...1f]
     * ```
     * @param hue in [0..360f]
     * @param saturation in [0..1f]
     * @param lightness in [0..1f]
     *
     * @return 3 element array which holds the output RGB components.
     */
    fun hslToRGBFloat(
        hue: Float,
        saturation: Float,
        lightness: Float
    ): FloatArray {
        val color = Color.hsl(hue, saturation, lightness)
        return floatArrayOf(color.red, color.green, color.blue)
    }

    /**
     * Convert HSL (hue-saturation-lightness) to RGB red, green, blue components in [0f..1f] range.
     * ```
     * rgb[0] is Red [0f .. 1f)
     * rgb[1] is Green [0f...1f]
     * rgb[2] is Blue [0f...1f]
     * ```
     * @param hue in [0..360f]
     * @param saturation in [0..1f]
     * @param lightness in [0..1f]
     * @param rgbIn 3 element array which holds the output RGB components.
     */
    fun hslToRGBFloat(
        hue: Float,
        saturation: Float,
        lightness: Float,
        rgbIn: FloatArray
    ) {
        val color = Color.hsl(hue, saturation, lightness)
        rgbIn[0] = color.red
        rgbIn[1] = color.green
        rgbIn[2] = color.blue
    }


}