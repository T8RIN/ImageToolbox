package com.smarttoolfactory.colorpicker.model

import androidx.annotation.FloatRange
import androidx.compose.ui.graphics.Color
import com.smarttoolfactory.extendedcolors.util.RGBUtil

/**
 * Color in HSV color model
 */
class ColorHSV(
    @FloatRange(from = 0.0, to = 360.0) val hue: Float,
    @FloatRange(from = 0.0, to = 1.0) val saturation: Float,
    @FloatRange(from = 0.0, to = 1.0) val value: Float,
    @FloatRange(from = 0.0, to = 1.0) val alpha: Float,
) : CompositeColor {

    override val color: Color
        get() = Color.hsv(hue, saturation, value, alpha)

    override val argbHexString: String
        get() = RGBUtil.argbToHex(color.alpha, color.red, color.green, color.blue)

    override val rgbHexString: String
        get() = RGBUtil.rgbToHex(color.red, color.green, color.blue)

    companion object {
        val Unspecified = ColorHSV(0f, 0f, 0f, 0f)
    }

    override fun toString(): String {
        return "ColorHSV(hash: ${hashCode()}," +
                " hue: $hue, saturation: $saturation, value: $value, alpha: $alpha)"
    }
}

/**
 * Color in HSL color model
 */
class ColorHSL(
    @FloatRange(from = 0.0, to = 360.0) val hue: Float,
    @FloatRange(from = 0.0, to = 1.0) val saturation: Float,
    @FloatRange(from = 0.0, to = 1.0) val lightness: Float,
    @FloatRange(from = 0.0, to = 1.0) val alpha: Float,
) : CompositeColor {

    override val color: Color
        get() = Color.hsl(hue, saturation, lightness, alpha)

    override val argbHexString: String
        get() = RGBUtil.argbToHex(color.alpha, color.red, color.green, color.blue)

    override val rgbHexString: String
        get() = RGBUtil.rgbToHex(color.red, color.green, color.blue)

    companion object {
        val Unspecified: ColorHSL = ColorHSL(0f, 0f, 0f, 0f)
    }

    override fun toString(): String {
        return "ColorHSL(hash: ${hashCode()}," +
                " hue: $hue, saturation: $saturation, lightness: $lightness, alpha: $alpha)"
    }
}

/**
 * Color in RGB color model
 */
class ColorRGB(
    @FloatRange(from = 0.0, to = 1.0) val red: Float,
    @FloatRange(from = 0.0, to = 1.0) val green: Float,
    @FloatRange(from = 0.0, to = 1.0) val blue: Float,
    @FloatRange(from = 0.0, to = 1.0) val alpha: Float,
) : CompositeColor {

    override val color: Color
        get() = Color(red, green, blue, alpha)

    override val argbHexString: String
        get() = RGBUtil.argbToHex(color.alpha, color.red, color.green, color.blue)

    override val rgbHexString: String
        get() = RGBUtil.rgbToHex(color.red, color.green, color.blue)

    companion object {
        val Unspecified = ColorRGB(0f, 0f, 0f, 0f)
    }

    override fun toString(): String {
        return "ColorRGB(hash: ${hashCode()}," +
                " hue: $red, saturation: $green, lightness: $blue, alpha: $alpha)"
    }
}

/**
 * Interface that can be polymorph into HSV, HSL or RGB color model
 */
interface CompositeColor {
    val color: Color
    val argbHexString: String
    val rgbHexString: String
}