package com.smarttoolfactory.extendedcolors.model

import androidx.compose.ui.graphics.Color
import com.smarttoolfactory.extendedcolors.util.ColorUtil
import com.smarttoolfactory.extendedcolors.util.fractionToRGBRange

/**
 * Data class that wraps [Color] and contains extend information about this color such
 * as HSL, HSV, RGB, HEX counterparts.
 */
data class ColorItem(var color: Color) {
    val hexARGB
        get() = ColorUtil.colorToHexAlpha(color)

    val hex
        get() = ColorUtil.colorToHex(color)

    val hsvArray
        get() = ColorUtil.colorToHSV(color)

    val hslArray
        get() = ColorUtil.colorToHSL(color)

    val rgb
        get() = ColorUtil.colorToARGBArray(color)

    val alpha: Float
        get() = color.alpha

    val red: Int
        get() = color.red.fractionToRGBRange()

    val green: Int
        get() = color.green.fractionToRGBRange()

    val blue: Int
        get() = color.blue.fractionToRGBRange()

    var label: String = Unspecified

    companion object {
        const val Unspecified = "?????"
    }
}