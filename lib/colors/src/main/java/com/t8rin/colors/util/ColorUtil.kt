/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

@file:Suppress("unused")

package com.t8rin.colors.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils


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
}