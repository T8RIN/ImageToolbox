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
 * Paint Shop Pro (JASC-PAL) palette coder
 */
class PaintShopProPaletteCoder : PaletteCoder {
    companion object {
        private val colorRegex = Regex("^\\s*(\\d+)\\s+(\\d+)\\s+(\\d+)\\s*$")
    }

    override fun decode(input: InputStream): Palette {
        val text = input.readText()
        val lines = text.lines()

        if (lines.size < 3) {
            throw PaletteCoderException.InvalidFormat()
        }

        // Check BOM
        if (!lines[0].contains("JASC-PAL")) {
            throw PaletteCoderException.InvalidFormat()
        }

        // Check version
        if (lines[1] != "0100") {
            throw PaletteCoderException.InvalidFormat()
        }

        // Get color count
        lines[2].toIntOrNull() ?: throw PaletteCoderException.InvalidFormat()

        val result = Palette.Builder()
        var currentName = ""

        for (line in lines.drop(3)) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) continue

            if (trimmed.startsWith(";")) {
                // Comment - might be a color name
                val commentText = trimmed.substring(1).trim()
                if (commentText.isNotEmpty()) {
                    currentName = commentText
                }
                continue
            }

            colorRegex.find(line)?.let { match ->
                val r = match.groupValues[1].toIntOrNull() ?: return@let
                val g = match.groupValues[2].toIntOrNull() ?: return@let
                val b = match.groupValues[3].toIntOrNull() ?: return@let

                val color = PaletteColor.rgb(
                    r = (r / 255.0).coerceIn(0.0, 1.0),
                    g = (g / 255.0).coerceIn(0.0, 1.0),
                    b = (b / 255.0).coerceIn(0.0, 1.0),
                    name = currentName
                )
                result.colors.add(color)
                currentName = ""
            }
        }

        return result.build()
    }

    override fun encode(palette: Palette, output: OutputStream) {
        val flattenedColors = palette.allColors().map { color ->
            if (color.colorSpace == ColorSpace.RGB) color else color.converted(ColorSpace.RGB)
        }

        var result = "JASC-PAL\n0100\n${flattenedColors.size}\n"

        flattenedColors.forEach { color ->
            if (color.name.isNotEmpty()) {
                result += "; ${color.name}\n"
            }
            val rgb = color.toRgb()
            val r = ((rgb.rf * 255).coerceIn(0.0, 255.0).toInt())
            val g = ((rgb.gf * 255).coerceIn(0.0, 255.0).toInt())
            val b = ((rgb.bf * 255).coerceIn(0.0, 255.0).toInt())
            result += "$r $g $b\n"
        }

        output.write(result.toByteArray(java.nio.charset.StandardCharsets.UTF_8))
    }
}


