package com.smarttoolfactory.extendedcolors.util

import androidx.compose.ui.graphics.Color
import com.smarttoolfactory.extendedcolors.util.ColorUtil.colorIntToRGBArray


object HSVUtil {
    /*
    HSV-HSL Conversions
 */
    /**
     * Convert [HSV](https://en.wikipedia.org/wiki/HSL_and_HSV)
     * components(hue-saturation-value) to HSL
     * (hue-saturation-lightness) components.
     *
     * @param hue in [0..360f]
     * @param saturation in [0..1f]
     * @param value in [0..1f]
     * @return float array that contains hue, saturation and lightness
     */
    fun hsvToHSL(
        hue: Float,
        saturation: Float,
        value: Float
    ): FloatArray {
        val lightness = (2 - saturation) * value / 2
        var saturationHSL = saturation

        if (lightness != 0f) {
            saturationHSL = when {
                lightness == 1f -> {
                    0f
                }

                lightness < 0.5f -> {
                    saturationHSL * value / (lightness * 2)
                }

                else -> {
                    saturationHSL * value / (2 - lightness * 2)
                }
            }
        }

        return floatArrayOf(hue, saturationHSL.coerceIn(0f, 1f), lightness.coerceIn(0f, 1f))
    }

    /**
     * Convert [HSV](https://en.wikipedia.org/wiki/HSL_and_HSV)
     * components(hue-saturation-value) to HSL
     * (hue-saturation-lightness) components and apply them to [hslOut].
     * @param hue in [0..360f]
     * @param saturation in [0..1f]
     * @param value in [0..1f]
     * @param hslOut array contains hue, saturation and lightness properties
     *
     */
    fun hsvToHSL(
        hue: Float,
        saturation: Float,
        value: Float,
        hslOut: FloatArray
    ) {
        val lightness = (2 - saturation) * value / 2
        var saturationHSL = saturation

        if (lightness != 0f) {
            saturationHSL = when {
                lightness == 1f -> {
                    0f
                }

                lightness < 0.5f -> {
                    saturationHSL * value / (lightness * 2)
                }

                else -> {
                    saturationHSL * value / (2 - lightness * 2)
                }
            }
        }

        hslOut[0] = hue
        hslOut[1] = saturationHSL.coerceIn(0f, 1f)
        hslOut[2] = lightness.coerceIn(0f, 1f)
    }

    /**
     * Convert HSV components(hue-saturation-value) to HSL
     * (hue-saturation-lightness) components.
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
    fun hsvToHSL(hsvIn: FloatArray): FloatArray {
        return hsvToHSL(hsvIn[0], hsvIn[1], hsvIn[2])
    }

    /**
     * Convert HSV components(hue-saturation-value) to HSL
     * (hue-saturation-lightness) components.
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
    fun hsvToHSL(hsvIn: FloatArray, hslOut: FloatArray) {
        hsvToHSL(hsvIn[0], hsvIn[1], hsvIn[2], hslOut)
    }


    /*
        HSV-ColorInt Conversions
     */

    /**
     * Convert HSV (hue-saturation-value) components to a RGB color in [Integer] format.
     *
     *  * For instance, red =255, green =0, blue=0 is -65536
     * @param hue in [0..360f]
     * @param saturation in [0..1f]
     * @param value in [0..1f]
     */
    fun hsvToColorInt(
        hue: Float,
        saturation: Float,
        value: Float
    ): Int {
        return hsvToColorInt(floatArrayOf(hue, saturation, value))
    }


    /**
     * Convert HSV (hue-saturation-value) components to a RGB color in [Integer] form.
     *
     *  * For instance, red =255, green =0, blue=0 is -65536
     * ```
     * hsv[0] is Hue [0 .. 360)
     * hsv[1] is Saturation [0...1]
     * hsv[2] is Value [0...1]
     * ```
     * @param hsvIn 3 element array which holds the input HSV components.
     */
    fun hsvToColorInt(hsvIn: FloatArray): Int {
        return android.graphics.Color.HSVToColor(hsvIn)
    }


    /*
        HSV-Color Conversions
     */

    /**
     * Convert HSV (hue-saturation-value) components to Jetpack Compose [Color].
     * @param hue in [0..360f]
     * @param saturation in [0..1f]
     * @param value in [0..1f]
     * @param alpha in [0..1f]
     */
    fun hsvToColor(
        hue: Float,
        saturation: Float,
        value: Float,
        alpha: Float
    ): Color {
        return Color.hsv(hue, saturation, value, alpha)
    }


    /*
        HSV-RGB Conversions
     */

    /**
     * Convert HSV (hue-saturation-value) components to a RGB red, green blue array.
     * ```
     * rgb[0] is Red [0 .. 255]
     * rgb[1] is Green [0...255]
     * rgb[2] is Blue [0...255]
     * ```
     * @param hue in [0..360f]
     * @param saturation in [0..1f]
     * @param value in [0..1f]
     */
    fun hsvToRGB(
        hue: Float,
        saturation: Float,
        value: Float
    ): IntArray {
        return colorIntToRGBArray(
            hsvToColorInt(hue, saturation, value)
        )
    }

    /**
     * Convert HSV (hue-saturation-value) components to a RGB red, green blue array.
     * ```
     * hsv[0] is Hue [0 .. 360)
     * hsv[1] is Saturation [0...1]
     * hsv[2] is Value [0...1]
     * ```
     * ```
     * rgb[0] is Red [0 .. 255]
     * rgb[1] is Green [0...255]
     * rgb[2] is Blue [0...255]
     * ```
     * @param hsvIn 3 element array which holds the input HSV components.
     */
    fun hsvToRGB(hsvIn: FloatArray): IntArray {
        return colorIntToRGBArray(
            hsvToColorInt(hsvIn)
        )
    }

    /**
     * Convert HSV (hue-saturation-value) components to a RGB red, green blue array.
     * ```
     * rgb[0] is Red [0 .. 255]
     * rgb[1] is Green [0...255]
     * rgb[2] is Blue [0...255]
     * ```
     * @param hue in [0..360f]
     * @param saturation in [0..1f]
     * @param value in [0..1f]
     * @param rgbIn 3 element array which holds the input RGB components.
     */
    fun hsvToRGB(
        hue: Float,
        saturation: Float,
        value: Float,
        rgbIn: IntArray
    ) {
        colorIntToRGBArray(
            color = hsvToColorInt(hue, saturation, value),
            rgbIn = rgbIn
        )
    }

    /**
     * Convert HSV (hue-saturation-value) to RGB red, green, blue components in [0f..1f] range.
     * ```
     * rgb[0] is Red [0f .. 1f)
     * rgb[1] is Green [0f...1f]
     * rgb[2] is Blue [0f...1f]
     * ```
     * @param hue in [0..360f]
     * @param saturation in [0..1f]
     * @param value in [0..1f]
     *
     * @return 3 element array which holds the output RGB components.
     */
    fun hsvToRGBFloat(
        hue: Float,
        saturation: Float,
        value: Float
    ): FloatArray {
        val color = Color.hsv(hue, saturation, value)
        return floatArrayOf(color.red, color.green, color.blue)
    }

    /**
     * Convert HSV (hue-saturation-value) to RGB red, green, blue components in [0f..1f] range.
     * ```
     * rgb[0] is Red [0f .. 1f)
     * rgb[1] is Green [0f...1f]
     * rgb[2] is Blue [0f...1f]
     * ```
     * @param hue in [0..360f]
     * @param saturation in [0..1f]
     * @param value in [0..1f]
     * @param rgbIn 3 element array which holds the output RGB components.
     */
    fun hsvToRGBFloat(
        hue: Float,
        saturation: Float,
        value: Float,
        rgbIn: FloatArray
    ) {
        val color = Color.hsv(hue, saturation, value)
        rgbIn[0] = color.red
        rgbIn[1] = color.green
        rgbIn[2] = color.blue
    }

}