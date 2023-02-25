package com.smarttoolfactory.colordetector

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import com.smarttoolfactory.extendedcolors.util.ColorUtil
import com.smarttoolfactory.extendedcolors.util.fractionToIntPercent
import kotlin.math.roundToInt

@Immutable
data class ColorData(val color: Color, val name: String) {

    @Stable
    val hexText: String
        get() = ColorUtil.colorToHex(color = color)

    @Stable
    val hslString: String
        get() {
            val arr: FloatArray = ColorUtil.colorToHSL(color)
            return "H: ${arr[0].roundToInt()}° " +
                    "S: ${arr[1].fractionToIntPercent()}% L: ${arr[2].fractionToIntPercent()}%"
        }

    @Stable
    val hsvString: String
        get() {
            val arr: FloatArray = ColorUtil.colorToHSV(color)
            return "H: ${arr[0].roundToInt()}° " +
                    "S: ${arr[1].fractionToIntPercent()}% V: ${arr[2].fractionToIntPercent()}%"
        }

    @Stable
    val rgb: String
        get() {
            val rgb = ColorUtil.colorToRGBArray(color)
            return "R: ${rgb[0]} G: ${rgb[1]} B: ${rgb[2]}"
        }
}