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

import com.t8rin.palette.ColorGroup
import com.t8rin.palette.ColorSpace
import com.t8rin.palette.Palette
import com.t8rin.palette.PaletteCoder
import com.t8rin.palette.PaletteCoderException
import com.t8rin.palette.PaletteColor
import com.t8rin.palette.utils.readText
import java.io.InputStream
import java.io.OutputStream
import java.text.DecimalFormat

/**
 * Swift code generator
 */
class SwiftPaletteCoder : PaletteCoder {
    private val formatter = DecimalFormat("0.0000").apply {
        decimalFormatSymbols = java.text.DecimalFormatSymbols(java.util.Locale.US)
    }

    override fun decode(input: InputStream): Palette {
        val text = input.readText()
        val result = Palette.Builder()

        // Parse #colorLiteral(red: x, green: y, blue: z, alpha: w)
        // Also try to capture comments with color names after the colorLiteral: , // name
        // Format: #colorLiteral(...), // name
        // Use non-greedy match to stop at next #colorLiteral or end of line
        val colorLiteralRegex = Regex(
            pattern = """#colorLiteral\s*\(\s*red:\s*([\d.]+)\s*,\s*green:\s*([\d.]+)\s*,\s*blue:\s*([\d.]+)\s*,\s*alpha:\s*([\d.]+)\s*\)\s*,?\s*//\s*([^#\n]*?)(?=\s*#colorLiteral|$)""",
            options = setOf(
                RegexOption.IGNORE_CASE,
                RegexOption.MULTILINE
            )
        )

        var colorIndex = 0
        colorLiteralRegex.findAll(text).forEach { match ->
            try {
                val r = match.groupValues[1].toDoubleOrNull() ?: 0.0
                val g = match.groupValues[2].toDoubleOrNull() ?: 0.0
                val b = match.groupValues[3].toDoubleOrNull() ?: 0.0
                val a = match.groupValues[4].toDoubleOrNull() ?: 1.0
                val colorName = match.groupValues[5].trim().takeIf { it.isNotEmpty() } ?: ""

                val finalName = colorName.ifEmpty {
                    "Color_$colorIndex"
                }

                val color = PaletteColor.rgb(
                    r = r.coerceIn(0.0, 1.0),
                    g = g.coerceIn(0.0, 1.0),
                    b = b.coerceIn(0.0, 1.0),
                    a = a.coerceIn(0.0, 1.0),
                    name = finalName
                )
                result.colors.add(color)
                colorIndex++
            } catch (_: Throwable) {
                // Skip invalid colors
            }
        }

        // Try to extract palette name from comments or struct name
        val paletteCommentMatch = Regex(pattern = """(?m)^\s*//\s*Palette:\s*(.+)\s*$""").find(text)
        if (paletteCommentMatch != null) {
            result.name = paletteCommentMatch.groupValues[1].trim()
        } else {
            val structNameMatch = Regex("""struct\s+(\w+)""").find(text)
            if (structNameMatch != null) {
                result.name = structNameMatch.groupValues[1]
            }
        }

        if (result.colors.isEmpty()) {
            throw PaletteCoderException.InvalidFormat()
        }

        return result.build()
    }

    override fun encode(palette: Palette, output: OutputStream) {
        fun mapColors(group: ColorGroup, offset: Int): String {
            val mapped = group.colors.mapNotNull { color ->
                try {
                    val converted =
                        if (color.colorSpace == ColorSpace.RGB) color else color.converted(
                            ColorSpace.RGB
                        )
                    converted.toRgb()
                } catch (_: Throwable) {
                    null
                }
            }

            if (mapped.isEmpty()) return ""

            var result = "   // Group (${group.name})\n"
            result += "   static let group$offset: [CGColor] = ["

            mapped.forEachIndexed { index, rgb ->
                // Add comment with color name if available (on same line before colorLiteral)
                val originalColor = group.colors.getOrNull(index)
                val colorNameComment =
                    if (originalColor != null && originalColor.name.isNotEmpty()) {
                        " // ${originalColor.name}"
                    } else {
                        ""
                    }

                if (index % 8 == 0) {
                    result += "\n     "
                }

                val rs = formatter.format(rgb.rf)
                val gs = formatter.format(rgb.gf)
                val bs = formatter.format(rgb.bf)
                val aas = formatter.format(rgb.af)
                result += " #colorLiteral(red: $rs, green: $gs, blue: $bs, alpha: $aas),$colorNameComment"
            }

            result += "\n   ]\n\n"
            return result
        }

        var result = ""
        if (palette.name.isNotEmpty()) {
            result += "// Palette: ${palette.name}\n"
        }
        result += "struct ExportedPalettes {\n"

        palette.allGroups.forEachIndexed { index, group ->
            result += mapColors(group, index)
        }

        result += "}\n"

        output.write(result.toByteArray(java.nio.charset.StandardCharsets.UTF_8))
    }
}

