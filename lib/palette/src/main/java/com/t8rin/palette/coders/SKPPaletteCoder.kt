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
import com.t8rin.palette.PaletteCoderException
import com.t8rin.palette.PaletteColor
import com.t8rin.palette.utils.readText
import java.io.InputStream
import java.io.OutputStream

/**
 * SK1 Color Palette coder
 */
class SKPPaletteCoder : PaletteCoder {
    companion object {
        private val rgbColorRegex =
            Regex("color\\(\\['RGB', \\[(\\d+(?:\\.\\d+)?), (\\d+(?:\\.\\d+)?), (\\d+(?:\\.\\d+)?)], (\\d+(?:\\.\\d+)?),.*?'(.*?)'")
        private val paletteNameRegex = Regex("set_name\\(.*?'(.*?)'\\)")
    }

    override fun decode(input: InputStream): Palette {
        val text = input.readText()
        val lines = text.lines()

        if (lines.isEmpty() || !lines[0].contains("##sK1 palette")) {
            throw PaletteCoderException.InvalidFormat()
        }

        val result = Palette.Builder()

        for (line in lines.drop(1)) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) continue

            // Check for palette name
            paletteNameRegex.find(trimmed)?.let { match ->
                val name = match.groupValues[1]
                if (name.isNotEmpty()) {
                    result.name = name
                }
            }

            // Check for color definition
            rgbColorRegex.find(trimmed)?.let { match ->
                val r = match.groupValues[1].toDoubleOrNull() ?: return@let
                val g = match.groupValues[2].toDoubleOrNull() ?: return@let
                val b = match.groupValues[3].toDoubleOrNull() ?: return@let
                val a = match.groupValues[4].toDoubleOrNull() ?: return@let
                val name = match.groupValues[5]

                val color = PaletteColor.rgb(
                    r = r.coerceIn(0.0, 1.0),
                    g = g.coerceIn(0.0, 1.0),
                    b = b.coerceIn(0.0, 1.0),
                    a = a.coerceIn(0.0, 1.0),
                    name = name
                )
                result.colors.add(color)
            }
        }

        val palette = result.build()

        if (palette.allColors().isEmpty()) {
            throw PaletteCoderException.InvalidFormat()
        }

        return palette
    }

    override fun encode(palette: Palette, output: OutputStream) {
        var result = "##sK1 palette\n"
        result += "Palette.Builder()\n"
        result += "set_name('${palette.name.replace("'", "_").replace("\"", "_")}')\n"
        result += "set_source('ColorPaletteCodable')\n"
        result += "set_columns(4)\n"

        palette.allColors().forEach { color ->
            val rgb = color.toRgb()
            val colorName = color.name.replace("'", "_").replace("\"", "_")
            result += "color(['RGB', [${rgb.rf}, ${rgb.gf}, ${rgb.bf}], ${rgb.af}, '$colorName'])\n"
        }

        result += "palette_end()\n"

        output.write(result.toByteArray(java.nio.charset.StandardCharsets.UTF_8))
    }
}


