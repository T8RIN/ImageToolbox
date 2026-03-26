package com.smarttoolfactory.colordetector.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import com.smarttoolfactory.colordetector.util.ColorUtil.fractionToRGBRange
import java.util.Locale
import kotlin.math.roundToInt


object ColorUtil {

    /**
     * Convert Jetpack Compose [Color] to HSV (hue-saturation-value) components.
     * ```
     * Hue is [0 .. 360)
     * Saturation is [0...1]
     * Value is [0...1]
     * ```
     * @param hslIn 3 element array which holds the input HSL components.
     */
    fun colorToHSV(color: Color, hslIn: FloatArray) {
        val rgbArray: IntArray = colorIntToRGBArray(color.toArgb())
        val red = rgbArray[0]
        val green = rgbArray[1]
        val blue = rgbArray[2]
        RGBUtil.rgbToHSV(red, green, blue, hslIn)
    }

    /**
     * Convert Jetpack Compose [Color] to HSV (hue-saturation-value) components.
     * ```
     * Hue is [0 .. 360)
     * Saturation is [0...1]
     * Value is [0...1]
     * ```
     */
    fun colorToHSV(color: Color): FloatArray {
        val rgbArray: IntArray = colorIntToRGBArray(color.toArgb())
        val red = rgbArray[0]
        val green = rgbArray[1]
        val blue = rgbArray[2]
        return RGBUtil.rgbToHSV(red, green, blue)
    }

    /**
     * Convert Jetpack Compose [Color] to HSV (hue-saturation-value) components.
     * ```
     * hsl[0] is Hue [0 .. 360)
     * hsl[1] is Saturation [0...1]
     * hsl[2] is Lightness [0...1]
     * ```
     * @param hslIn 3 element array which holds the input HSL components.
     */
    fun colorToHSL(color: Color, hslIn: FloatArray) {
        val rgbArray: IntArray = colorIntToRGBArray(color.toArgb())
        val red = rgbArray[0]
        val green = rgbArray[1]
        val blue = rgbArray[2]
        RGBUtil.rgbToHSL(red, green, blue, hslIn)
    }

    /**
     * Convert Jetpack Compose [Color] to HSV (hue-saturation-value) components.
     * ```
     * hsl[0] is Hue [0 .. 360)
     * hsl[1] is Saturation [0...1]
     * hsl[2] is Lightness [0...1]
     * ```
     */
    fun colorToHSL(color: Color): FloatArray {
        val rgbArray: IntArray = colorIntToRGBArray(color.toArgb())
        val red = rgbArray[0]
        val green = rgbArray[1]
        val blue = rgbArray[2]
        return RGBUtil.rgbToHSL(red, green, blue)
    }

    /*
        COLOR-RGB Conversions
     */

    /**
     * Convert Jetpack [Color] into 3 element array of red, green, and blue
     *```
     * rgb[0] is Red [0 .. 255]
     * rgb[1] is Green [0...255]
     * rgb[2] is Blue [0...255]
     * ```
     * @param color Jetpack Compose [Color]
     * @return 3 element array which holds the input RGB components.
     */
    fun colorToARGBArray(color: Color): IntArray {
        return colorIntToRGBArray(color.toArgb())
    }

    /**
     * Convert Jetpack [Color] into 3 element array of red, green, and blue
     *```
     * rgb[0] is Red [0 .. 255]
     * rgb[1] is Green [0...255]
     * rgb[2] is Blue [0...255]
     * ```
     * @param color Jetpack Compose [Color]
     * @return 3 element array which holds the input RGB components.
     */
    fun colorToRGBArray(color: Color): IntArray {
        return colorIntToRGBArray(color.toArgb())
    }

    /**
     * Convert Jetpack [Color] into 3 element array of red, green, and blue
     *```
     * rgb[0] is Red [0 .. 255]
     * rgb[1] is Green [0...255]
     * rgb[2] is Blue [0...255]
     * ```
     * @param color Jetpack Compose [Color]
     * @param rgbIn 3 element array which holds the input RGB components.
     */
    fun colorToRGBArray(color: Color, rgbIn: IntArray) {
        val argbArray = colorIntToRGBArray(color.toArgb())
        rgbIn[0] = argbArray[0]
        rgbIn[1] = argbArray[1]
        rgbIn[2] = argbArray[2]
    }


    fun colorToHex(color: Color): String {
        return RGBUtil.rgbToHex(color.red, color.green, color.blue)
    }

    fun colorToHexAlpha(color: Color): String {
        return RGBUtil.argbToHex(color.alpha, color.red, color.green, color.blue)
    }

    /**
     * Convert a RGB color in [Integer] form to HSV (hue-saturation-value) components.
     *  * For instance, red =255, green =0, blue=0 is -65536
     * ```
     * hsv[0] is Hue [0 .. 360)
     * hsv[1] is Saturation [0...1]
     * hsv[2] is Value [0...1]
     * ```
     */
    fun colorIntToHSV(color: Int): FloatArray {
        val hsvOut = floatArrayOf(0f, 0f, 0f)
        android.graphics.Color.colorToHSV(color, hsvOut)
        return hsvOut
    }

    /**
     * Convert a RGB color in [Integer] form to HSV (hue-saturation-value) components.
     *  * For instance, red =255, green =0, blue=0 is -65536
     *
     * ```
     * hsv[0] is Hue [0 .. 360)
     * hsv[1] is Saturation [0...1]
     * hsv[2] is Value [0...1]
     * ```
     * @param hsvIn 3 element array which holds the input HSV components.
     */
    fun colorIntToHSV(color: Int, hsvIn: FloatArray) {
        android.graphics.Color.colorToHSV(color, hsvIn)
    }


    /**
     * Convert RGB color [Integer] to HSL (hue-saturation-lightness) components.
     * ```
     * hsl[0] is Hue [0 .. 360)
     * hsl[1] is Saturation [0...1]
     * hsl[2] is Lightness [0...1]
     * ```
     */
    fun colorIntToHSL(color: Int): FloatArray {
        val hslOut = floatArrayOf(0f, 0f, 0f)
        ColorUtils.colorToHSL(color, hslOut)
        return hslOut
    }

    /**
     * Convert RGB color [Integer] to HSL (hue-saturation-lightness) components.
     * ```
     * hsl[0] is Hue [0 .. 360)
     * hsl[1] is Saturation [0...1]
     * hsl[2] is Lightness [0...1]
     * ```
     */
    fun colorIntToHSL(color: Int, hslIn: FloatArray) {
        ColorUtils.colorToHSL(color, hslIn)
    }


    /*
        ColorInt-RGB Conversions
     */
    /**
     * Convert Color [Integer] into 3 element array of red, green, and blue
     *```
     * rgb[0] is Red [0 .. 255]
     * rgb[1] is Green [0...255]
     * rgb[2] is Blue [0...255]
     * ```
     * @return 3 element array which holds the input RGB components.
     */
    fun colorIntToRGBArray(color: Int): IntArray {
        val red = android.graphics.Color.red(color)
        val green = android.graphics.Color.green(color)
        val blue = android.graphics.Color.blue(color)
        return intArrayOf(red, green, blue)
    }

    /**
     * Convert Color [Integer] into 3 element array of red, green, and blue
     *```
     * rgb[0] is Red [0 .. 255]
     * rgb[1] is Green [0...255]
     * rgb[2] is Blue [0...255]
     * ```
     * @param rgbIn 3 element array which holds the input RGB components.
     */
    fun colorIntToRGBArray(color: Int, rgbIn: IntArray) {
        val red = android.graphics.Color.red(color)
        val green = android.graphics.Color.green(color)
        val blue = android.graphics.Color.blue(color)

        rgbIn[0] = red
        rgbIn[1] = green
        rgbIn[2] = blue
    }

    /**
     * Convert Color [Integer] into 4 element array of alpha red, green, and blue
     *```
     * rgb[0] is Alpha [0 .. 255]
     * rgb[1] is Red [0 .. 255]
     * rgb[2] is Green [0...255]
     * rgb[3] is Blue [0...255]
     * ```
     * @return 4 element array which holds the input ARGB components.
     */
    fun colorIntToARGBArray(color: Int): IntArray {
        val alpha = android.graphics.Color.alpha(color)
        val red = android.graphics.Color.red(color)
        val green = android.graphics.Color.green(color)
        val blue = android.graphics.Color.blue(color)
        return intArrayOf(alpha, red, green, blue)
    }

    /**
     * Convert Color [Integer] into 4 element array of alpha red, green, and blue
     *```
     * rgb[0] is Alpha [0 .. 255]
     * rgb[1] is Red [0 .. 255]
     * rgb[2] is Green [0...255]
     * rgb[3] is Blue [0...255]
     * ```
     * @param argbIn 4 element array which holds the input ARGB components.
     */
    fun colorIntToARGBArray(color: Int, argbIn: IntArray) {
        val alpha = android.graphics.Color.alpha(color)
        val red = android.graphics.Color.red(color)
        val green = android.graphics.Color.green(color)
        val blue = android.graphics.Color.blue(color)

        argbIn[0] = alpha
        argbIn[1] = red
        argbIn[2] = green
        argbIn[3] = blue
    }

    fun Float.fractionToRGBRange() = (this * 255.0f).toInt()

    /**
     * Converts alpha, red, green or blue values from range of [0f-1f] to [0-255] and returns
     * it as [String].
     */
    fun Float.fractionToRGBString() = this.fractionToRGBRange().toString()

    /**
     * Rounds this [Float] to another with 2 significant numbers
     * 0.1234 is rounded to 0.12
     * 0.127 is rounded to 0.13
     */
    fun Float.roundToTwoDigits() = (this * 100.0f).roundToInt() / 100.0f

    /**
     * Rounds this [Float] to closest int.
     */
    fun Float.round() = this.roundToInt()

    /**
     * Converts **HSV** or **HSL** colors that are in range of [0f-1f] to [0-100] range in [Integer]
     * with [Float.roundToInt]
     */
    fun Float.fractionToPercent() = (this * 100.0f).roundToInt()

    /**
     * Converts **HSV** or **HSL** colors that are in range of [0f-1f] to [0-100] range in [Integer]
     * with [Float.toInt]
     */
    fun Float.fractionToIntPercent() = (this * 100.0f).toInt()
}


object RGBUtil {

    /**
     * Convert RGB red, green blue to HSV (hue-saturation-value) components.
     * ```
     * hsv[0] is Hue [0 .. 360)
     * hsv[1] is Saturation [0...1]
     * hsv[2] is Value [0...1]
     * ```
     * @param red  Red component [0..255] of the color
     * @param green  Green component [0..255] of the color
     * @param blue Blue component [0..255] of the color
     */
    fun rgbToHSV(
        red: Int,
        green: Int,
        blue: Int
    ): FloatArray {
        val hsvOut = floatArrayOf(0f, 0f, 0f)
        android.graphics.Color.RGBToHSV(red, green, blue, hsvOut)
        return hsvOut
    }

    /**
     * Convert RGB red, green blue to HSV (hue-saturation-value) components.
     * ```
     * hsv[0] is Hue [0 .. 360)
     * hsv[1] is Saturation [0...1]
     * hsv[2] is Value [0...1]
     * ```
     * @param red  Red component [0..255] of the color
     * @param green  Green component [0..255] of the color
     * @param blue Blue component [0..255] of the color
     * @param hsvIn 3 element array which holds the output HSV components.
     */
    fun rgbToHSV(
        red: Int,
        green: Int,
        blue: Int,
        hsvIn: FloatArray
    ) {
        android.graphics.Color.RGBToHSV(red, green, blue, hsvIn)
    }


    /**
     * Convert RGB red, green blue to HSL (hue-saturation-lightness) components.
     * ```
     * hsl[0] is Hue [0 .. 360)
     * hsl[1] is Saturation [0...1]
     * hsl[2] is Lightness [0...1]
     * ```
     * @param red  Red component [0..255] of the color
     * @param green  Green component [0..255] of the color
     * @param blue Blue component [0..255] of the color
     */
    fun rgbToHSL(
        red: Int,
        green: Int,
        blue: Int
    ): FloatArray {
        val outHsl = floatArrayOf(0f, 0f, 0f)
        ColorUtils.RGBToHSL(red, green, blue, outHsl)
        return outHsl
    }

    /**
     * Convert RGB red, green blue to HSL (hue-saturation-lightness) components.
     * ```
     * hsl[0] is Hue [0 .. 360)
     * hsl[1] is Saturation [0...1]
     * hsl[2] is Lightness [0...1]
     * ```
     * @param red  Red component [0..255] of the color
     * @param green  Green component [0..255] of the color
     * @param blue Blue component [0..255] of the color
     * @param hslIn 3 element array which holds the input HSL components.
     */
    fun rgbToHSL(
        red: Int,
        green: Int,
        blue: Int,
        hslIn: FloatArray
    ) {
        ColorUtils.RGBToHSL(red, green, blue, hslIn)
    }

    /**
     * Convert RGB red, green, blue components in [0f..1f] range to
     * HSV (hue-saturation-value) components.
     *
     * @param red  Red component [0..255] of the color
     * @param green  Green component [0..255] of the color
     * @param blue Blue component [0..255] of the color
     * @return 3 element array which holds the output RGB components.
     */
    fun rgbFloatToHSV(
        red: Float,
        green: Float,
        blue: Float
    ): FloatArray {
        val colorInt = rgbToColorInt(red = red, green = green, blue = blue)
        val outHsl = floatArrayOf(0f, 0f, 0f)
        ColorUtil.colorIntToHSV(colorInt, outHsl)
        return outHsl
    }

    /**
     * Convert RGB red, green, blue components in [0f..1f] range to
     * HSV (hue-saturation-value) components.
     *
     * @param red  Red component [0..255] of the color
     * @param green  Green component [0..255] of the color
     * @param blue Blue component [0..255] of the color
     * @param hsvIn 3 element array which holds the output HSV components.
     */
    fun rgbFloatToHSV(
        red: Float,
        green: Float,
        blue: Float,
        hsvIn: FloatArray
    ) {
        val colorInt = rgbToColorInt(red = red, green = green, blue = blue)
        ColorUtil.colorIntToHSV(colorInt, hsvIn)
    }


    /**
     * Convert RGB red, green blue in [0f..1f] range to HSL (hue-saturation-lightness) components.
     * ```
     * hsl[0] is Hue [0 .. 360)
     * hsl[1] is Saturation [0...1]
     * hsl[2] is Lightness [0...1]
     * ```
     * @param red  Red component [0f..1f] of the color
     * @param green  Green component [0f..1f] of the color
     * @param blue Blue component [0f..1f] of the color
     */
    fun rgbFloatToHSL(
        red: Float,
        green: Float,
        blue: Float
    ): FloatArray {
        val colorInt = rgbToColorInt(red = red, green = green, blue = blue)
        val outHsl = floatArrayOf(0f, 0f, 0f)
        ColorUtil.colorIntToHSL(colorInt, outHsl)
        return outHsl
    }

    /**
     * Convert RGB red, green blue in [0f..1f] range to HSL (hue-saturation-lightness) components.
     * ```
     * hsl[0] is Hue [0 .. 360)
     * hsl[1] is Saturation [0...1]
     * hsl[2] is Lightness [0...1]
     * ```
     * @param red  Red component [0f..1f] of the color
     * @param green  Green component [0f..1f] of the color
     * @param blue Blue component [0f..1f] of the color
     * @param hslIn 3 element array which holds the input HSL components.
     */
    fun rgbFloatToHSL(
        red: Float,
        green: Float,
        blue: Float,
        hslIn: FloatArray
    ) {
        val colorInt = rgbToColorInt(red = red, green = green, blue = blue)
        ColorUtil.colorIntToHSL(colorInt, hslIn)
    }


    /**
     * Return a color-int from alpha, red, green, blue components.
     * These component values should be [0..255], but there is no range check performed,
     * so if they are out of range, the returned color is undefined.
     *
     * @param red  Red component [0..255] of the color
     * @param green  Green component [0..255] of the color
     * @param blue Blue component [0..255] of the color
     */
    fun rgbToColorInt(
        red: Int,
        green: Int,
        blue: Int
    ): Int {
        return android.graphics.Color.rgb(red, green, blue)
    }

    /**
     * Return a color-int from alpha, red, green, blue components.
     * These component values should be [0f..1f], but there is no range check performed,
     * so if they are out of range, the returned color is undefined.
     *
     * @param red  Red component [0f..1f] of the color
     * @param green  Green component [0..1f] of the color
     * @param blue Blue component [0..1f] of the color
     *
     */
    fun rgbToColorInt(
        red: Float,
        green: Float,
        blue: Float
    ): Int {
        val redInt = red.fractionToRGBRange()
        val greenInt = green.fractionToRGBRange()
        val blueInt = blue.fractionToRGBRange()
        return android.graphics.Color.rgb(redInt, greenInt, blueInt)
    }


    /**
     * Convert red, green, blue components [0..255] range in [Integer] to Hex format String
     */
    fun rgbToHex(
        red: Int,
        green: Int,
        blue: Int
    ): String {
        return "#" +
                Integer.toHexString(red).toStringComponent() +
                Integer.toHexString(green).toStringComponent() +
                Integer.toHexString(blue).toStringComponent()
    }

    /**
     * Convert rgb array to Hex format String
     * ```
     * rgb[0] is Red [0 .. 255]
     * rgb[1] is Green [0...255]
     * rgb[2] is Blue [0...255]
     * ```
     */
    fun rgbToHex(rgb: IntArray): String {
        return "#" +
                Integer.toHexString(rgb[0]).toStringComponent() +
                Integer.toHexString(rgb[1]).toStringComponent() +
                Integer.toHexString(rgb[2]).toStringComponent()
    }

    /**
     * Convert red, green, blue components [0f..1f] range in [Float] to Hex format String
     */
    fun rgbToHex(
        red: Float,
        green: Float,
        blue: Float
    ): String {
        return "#" +
                Integer.toHexString(red.fractionToRGBRange()).toStringComponent() +
                Integer.toHexString(green.fractionToRGBRange()).toStringComponent() +
                Integer.toHexString(blue.fractionToRGBRange()).toStringComponent()
    }


    /**
     * Return a color-int from alpha, red, green, blue components.
     * These component values should be [0..255], but there is no range check performed,
     * so if they are out of range, the returned color is undefined.
     *
     * @param alpha  Alpha component [0..255] of the color
     * @param red  Red component [0..255] of the color
     * @param green  Green component [0..255] of the color
     * @param blue Blue component [0..255] of the color
     */
    fun argbToColorInt(
        alpha: Int,
        red: Int,
        green: Int,
        blue: Int
    ): Int {
        return android.graphics.Color.argb(alpha, red, green, blue)
    }

    /**
     * Return a color-int from alpha, red, green, blue components.
     * These component values should be [0f..1f], but there is no range check performed,
     * so if they are out of range, the returned color is undefined.
     *
     * @param alpha  Alpha component [0f..1f] of the color
     * @param red  Red component [0f..1f] of the color
     * @param green  Green component [0..1f] of the color
     * @param blue Blue component [0..1f] of the color
     */
    fun argbToColorInt(
        alpha: Float,
        red: Float,
        green: Float,
        blue: Float
    ): Int {
        val alphaInt = alpha.fractionToRGBRange()
        val redInt = red.fractionToRGBRange()
        val greenInt = green.fractionToRGBRange()
        val blueInt = blue.fractionToRGBRange()
        return android.graphics.Color.argb(alphaInt, redInt, greenInt, blueInt)
    }


    /**
     * Convert alpha, red, green, blue components in [0..255] range argb to Hex format String
     *
     * ```
     * Alpha is [0 .. 255]
     * Red is [0 .. 255]
     * Green is [0...255]
     * Blue is [0...255]
     * ```
     */
    fun argbToHex(
        alpha: Int,
        red: Int,
        green: Int,
        blue: Int
    ): String {
        return "#" +
                Integer.toHexString(alpha).toStringComponent() +
                Integer.toHexString(red).toStringComponent() +
                Integer.toHexString(green).toStringComponent() +
                Integer.toHexString(blue).toStringComponent()
    }

    /**
     * Convert alpha, red, green, blue components in [0f..1f] range in [Float] argb to Hex format String
     *
     * ```
     * Alpha is [0f .. 1f]
     * Red is [0f .. 1f]
     * Green is [0...1f]
     * Blue is [0...1f]
     * ```
     */
    fun argbToHex(
        alpha: Float,
        red: Float,
        green: Float,
        blue: Float
    ): String {
        return "#" +
                Integer.toHexString(alpha.fractionToRGBRange()).toStringComponent() +
                Integer.toHexString(red.fractionToRGBRange()).toStringComponent() +
                Integer.toHexString(green.fractionToRGBRange()).toStringComponent() +
                Integer.toHexString(blue.fractionToRGBRange()).toStringComponent()
    }

    /*
        RGB-HEX Conversions
     */
    /**
     * Get hex representation of a rgb Color in [Integer] format
     */
    fun Int.toRgbString(): String =
        ("#" +
                red.toStringComponent() +
                green.toStringComponent() +
                blue.toStringComponent())
            .uppercase(Locale.getDefault())

    /**
     * Get hex representation of a argb Color in [Integer] format
     */
    fun Int.toArgbString(): String =
        ("#" +
                alpha.toStringComponent() +
                red.toStringComponent() +
                green.toStringComponent() +
                blue.toStringComponent()
                ).uppercase(Locale.getDefault())

    private fun String.toStringComponent() =
        this.let { if (it.length == 1) "0${it}" else it }

    private fun Int.toStringComponent(): String =
        this.toString(16).let { if (it.length == 1) "0${it}" else it }

    inline val Int.alpha: Int
        get() = (this shr 24) and 0xFF

    inline val Int.red: Int
        get() = (this shr 16) and 0xFF

    inline val Int.green: Int
        get() = (this shr 8) and 0xFF

    inline val Int.blue: Int
        get() = this and 0xFF

}