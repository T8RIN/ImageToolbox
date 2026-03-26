package com.smarttoolfactory.extendedcolors.util

import androidx.compose.ui.graphics.Color
import kotlin.math.roundToInt

/**
 * Creates Material Design2 color swatch from a [Color]
 */
fun createMaterialSwatch(color: Color): Map<Int, Color> {

    val colorSwatch = linkedMapOf<Int, Color>()

    val variants = mutableListOf(.05)
    for (i in 1 until 10) {
        variants.add(0.1 * i)
    }

    val red: Int = color.red.fractionToRGBRange()
    val green: Int = color.green.fractionToRGBRange()
    val blue: Int = color.blue.fractionToRGBRange()

    for (variant in variants) {
        val ds: Double = 0.5 - variant
        val newRed: Int = red + ((if (ds < 0) red else 255 - red) * ds).roundToInt()
        val newGreen: Int = green + ((if (ds < 0) green else 255 - green) * ds).roundToInt()
        val newBlue: Int = blue + ((if (ds < 0) blue else 255 - blue) * ds).roundToInt()

        colorSwatch[(variant * 1000).roundToInt()] = Color(newRed, newGreen, newBlue)
    }

    return colorSwatch
}