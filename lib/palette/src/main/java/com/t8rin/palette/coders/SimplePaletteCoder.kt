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

package com.t8rin.palette.coders

import com.t8rin.palette.Palette
import com.t8rin.palette.PaletteCoder
import com.t8rin.palette.PaletteColor
import com.t8rin.palette.utils.readText
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import kotlin.math.pow

/**
 * Simple Color Palette coder
 * https://sindresorhus.com/simple-color-palette
 */
class SimplePaletteCoder : PaletteCoder {
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    @Serializable
    private data class SimplePaletteColor(
        val name: String? = null,
        val components: List<Double>
    )

    @Serializable
    private data class SimplePalette(
        val name: String? = null,
        val colors: List<SimplePaletteColor>
    )

    companion object {
        private const val MAX_DECIMAL_PLACES = 4

        private fun Double.roundToPlaces(places: Int): Double {
            if (places <= 0) return this
            val multiplier = 10.0.pow(places.toDouble())
            return (this * multiplier).let { kotlin.math.round(it) } / multiplier
        }

        private fun sRGB2Linear(value: Double): Double {
            return if (value <= 0.04045) {
                value / 12.92
            } else {
                ((value + 0.055) / 1.055).pow(2.4)
            }
        }

        private fun linearSRGB2SRGB(value: Double): Double {
            return if (value <= 0.0031308) {
                value * 12.92
            } else {
                1.055 * value.pow(1.0 / 2.4) - 0.055
            }
        }
    }

    override fun decode(input: InputStream): Palette {
        val text = input.readText()
        val s = json.decodeFromString(SimplePalette.serializer(), text)

        val result = Palette.Builder()
        result.name = s.name ?: ""

        val colors = s.colors.mapNotNull { colorData ->
            val components = colorData.components.map { it.roundToPlaces(MAX_DECIMAL_PLACES) }
            if (components.size < 3 || components.size > 4) return@mapNotNull null

            val lr = components[0]
            val lg = components[1]
            val lb = components[2]
            val la = if (components.size == 4) components[3].coerceIn(0.0, 1.0) else 1.0

            // Convert from linear extended sRGB to sRGB
            val r = linearSRGB2SRGB(lr)
            val g = linearSRGB2SRGB(lg)
            val b = linearSRGB2SRGB(lb)

            PaletteColor.rgb(
                r = r,
                g = g,
                b = b,
                a = la,
                name = colorData.name ?: ""
            )
        }

        result.colors = colors.toMutableList()
        return result.build()
    }

    override fun encode(palette: Palette, output: OutputStream) {
        val name: String? = palette.name.ifEmpty { null }

        val colors = palette.allColors().map { color ->
            val rgb = color.toRgb()

            // Convert to linear (extended)
            val components = mutableListOf(
                sRGB2Linear(rgb.rf).roundToPlaces(MAX_DECIMAL_PLACES),
                sRGB2Linear(rgb.gf).roundToPlaces(MAX_DECIMAL_PLACES),
                sRGB2Linear(rgb.bf).roundToPlaces(MAX_DECIMAL_PLACES)
            )

            // Add alpha if not 1.0
            if (rgb.af.roundToPlaces(MAX_DECIMAL_PLACES) != 1.0) {
                components.add(rgb.af.roundToPlaces(MAX_DECIMAL_PLACES))
            }

            SimplePaletteColor(
                name = color.name.ifEmpty { null },
                components = components
            )
        }

        val result = SimplePalette(name = name, colors = colors)
        val encoded = json.encodeToString(SimplePalette.serializer(), result)
        output.write(encoded.toByteArray(java.nio.charset.StandardCharsets.UTF_8))
    }
}


