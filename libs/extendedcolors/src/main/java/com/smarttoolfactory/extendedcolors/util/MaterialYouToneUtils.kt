package com.smarttoolfactory.extendedcolors.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.smarttoolfactory.extendedcolors.md3.hct.Cam16
import com.smarttoolfactory.extendedcolors.md3.palettes.TonalPalette
import kotlin.math.max

val material3ToneRange = listOf(
    0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 95, 99, 100
)

/**
 * Get
 * [Material Design3 Colors](https://m3.material.io/styles/color/the-color-system/key-colors-tones)
 * from [color] as Map that contains tone numbers as keys and [Color]s as values
 * between 0 and 100 tones in total 13 tones.
 *
 *
 */
fun getColorTonesMap(color: Color): Map<Int, Color> {
    val palette: TonalPalette = TonalPalette.fromInt(color.toArgb())
    val toneMap = linkedMapOf<Int, Color>()

    material3ToneRange.forEach { shade ->
        toneMap[shade] = Color(palette.tone(shade))
    }

    return toneMap
}


/**
 * Get
 * [Material Design3 Colors](https://m3.material.io/styles/color/the-color-system/key-colors-tones)
 * from [color] as list between 0 and 100 tones in total 13 tones.
 *
 */
//fun getColorTonesList(color: Color): List< Color> {
//
//    val palette: TonalPalette = TonalPalette.fromInt(color.toArgb())
//    val toneList = mutableListOf<Color>()
//
//    material3ToneRange.forEach { shade ->
//       toneList.add(Color(palette.tone(shade)))
//    }
//
//    return toneList
//}

fun getColorTonesList(color: Color): List<Color> {

    val camColor = Cam16.fromInt(color.toArgb())
    val palette: TonalPalette =
        TonalPalette.fromHueAndChroma(camColor.hue, max(48.0, camColor.chroma))
    val toneList = mutableListOf<Color>()

    material3ToneRange.forEach { shade ->
        toneList.add(Color(palette.tone(shade)))
    }

    return toneList
}