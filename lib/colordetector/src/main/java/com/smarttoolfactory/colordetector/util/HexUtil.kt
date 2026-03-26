package com.smarttoolfactory.colordetector.util

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import com.smarttoolfactory.colordetector.util.ColorUtil.colorIntToARGBArray
import com.smarttoolfactory.colordetector.util.ColorUtil.colorIntToRGBArray

object HexUtil {
    /*
    HEX-ColorInt Conversion
 */
    fun hexToColorInt(colorString: String): Int {
        val completeColorString = if (colorString.first() == '#') colorString else "#$colorString"
        return completeColorString.toColorInt()
    }

    /*
        HEX-RGB Conversion
     */
    fun hexToRGB(colorString: String): IntArray {
        val colorInt = hexToColorInt(colorString)
        return colorIntToRGBArray(colorInt)
    }

    fun hexToRGB(colorString: String, rgbIn: IntArray) {
        val colorInt = hexToColorInt(colorString)
        colorIntToRGBArray(colorInt, rgbIn)
    }

    fun hexToARGB(colorString: String): IntArray {
        val colorInt = hexToColorInt(colorString)
        return colorIntToARGBArray(colorInt)
    }

    fun hexToARGB(colorString: String, argbIn: IntArray) {
        val colorInt = hexToColorInt(colorString)
        colorIntToARGBArray(colorInt, argbIn)
    }


    fun hexToColor(colorString: String): Color {
        return Color(hexToColorInt(colorString))
    }

}