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

import com.t8rin.palette.ColorSpace
import com.t8rin.palette.Palette
import com.t8rin.palette.PaletteCoder
import com.t8rin.palette.PaletteCoderException
import com.t8rin.palette.PaletteColor
import com.t8rin.palette.utils.readText
import java.io.InputStream
import java.io.OutputStream

/**
 * CLF Lab Colors coder (decode only)
 */
class CLFPaletteCoder : PaletteCoder {
    override fun decode(input: InputStream): Palette {
        val text = input.readText()
        val result = Palette.Builder()

        text.lines().forEach { line ->
            val components = line.split("\t")
            if (components.size == 4) {
                val name = components[0]
                val ls = components[1].replace(",", ".")
                val `as` = components[2].replace(",", ".")
                val bs = components[3].replace(",", ".")

                val l = ls.trim().toDoubleOrNull() ?: return@forEach
                val a = `as`.trim().toDoubleOrNull() ?: return@forEach
                val b = bs.trim().toDoubleOrNull() ?: return@forEach

                try {
                    val color = PaletteColor.lab(
                        l = l,
                        a = a,
                        b = b,
                        name = name
                    )
                    result.colors.add(color)
                } catch (_: Throwable) {
                    // Skip invalid colors
                }
            }
        }

        if (result.colors.isEmpty()) {
            throw PaletteCoderException.InvalidFormat()
        }

        return result.build()
    }

    override fun encode(palette: Palette, output: OutputStream) {
        val allColors = palette.allColors()
        val lines = allColors.map { color ->
            // Convert to LAB if not already
            val lab = if (color.colorSpace == ColorSpace.LAB) {
                color
            } else {
                try {
                    color.converted(ColorSpace.LAB)
                } catch (_: Throwable) {
                    // If conversion fails, try to create a LAB color from RGB
                    val rgb = color.toRgb()
                    // Approximate conversion to LAB (simplified)
                    PaletteColor.lab(
                        l = (rgb.rf * 0.299 + rgb.gf * 0.587 + rgb.bf * 0.114) * 100,
                        a = (rgb.rf - rgb.gf) * 127,
                        b = (rgb.gf - rgb.bf) * 127,
                        name = color.name
                    )
                }
            }

            val name = color.name.ifEmpty { "Color" }
            val l = lab.colorComponents[0]
            val a = lab.colorComponents[1]
            val b = lab.colorComponents[2]

            // Format: name[tab]L[tab]a[tab]b
            // Replace comma with dot for decimal separator
            "$name\t${l.toString().replace(',', '.')}\t${
                a.toString().replace(',', '.')
            }\t${b.toString().replace(',', '.')}"
        }

        val text = lines.joinToString("\n")
        output.write(text.toByteArray(java.nio.charset.StandardCharsets.UTF_8))
    }
}


