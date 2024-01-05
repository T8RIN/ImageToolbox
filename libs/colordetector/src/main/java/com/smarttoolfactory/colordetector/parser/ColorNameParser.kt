package com.smarttoolfactory.colordetector.parser

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.smarttoolfactory.colordetector.model.ColorItem
import com.smarttoolfactory.colordetector.util.ColorUtil
import com.smarttoolfactory.colordetector.util.HexUtil
import kotlin.math.sqrt

@Composable
fun rememberColorParser(): ColorNameParser {
    return remember {
        ColorNameParser()
    }
}

/**
 * Parses color name from [Color]
 */
class ColorNameParser internal constructor() {

    private val rbgData: List<RGBData> by lazy {
        colorNameMap.map { entry: Map.Entry<String, String> ->
            val rgbArray = HexUtil.hexToRGB(entry.key)
            val label = entry.value
            RGBData(
                x = rgbArray[0],
                y = rgbArray[1],
                z = rgbArray[2],
                label = label
            )
        }
    }

    /**
     * Parse name of [Color]
     */
    fun parseColorName(color: Color): String {
        val rgbArray = ColorUtil.colorToRGBArray(color)

        val red: Int = rgbArray[0]
        val green: Int = rgbArray[1]
        val blue: Int = rgbArray[2]

        var distance: Int = Int.MAX_VALUE

        var colorId = -1

        rbgData.forEachIndexed { index, rgbData ->
            val currentDistance = sqrt(
                (
                        (rgbData.x - red) * (rgbData.x - red) +
                                (rgbData.y - green) * (rgbData.y - green) +
                                (rgbData.z - blue) * (rgbData.z - blue)
                        ).toDouble()
            ).toInt()

            if (currentDistance < distance) {
                distance = currentDistance
                colorId = index
            }
        }

        return if (colorId >= 0) {
            rbgData[colorId].label
        } else ColorItem.Unspecified

    }
}