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

import com.t8rin.palette.ColorByteFormat
import com.t8rin.palette.ColorSpace
import com.t8rin.palette.Palette
import com.t8rin.palette.PaletteCoder
import com.t8rin.palette.PaletteCoderException
import com.t8rin.palette.PaletteColor
import com.t8rin.palette.utils.hexString
import com.t8rin.palette.utils.readText
import java.io.InputStream
import java.io.OutputStream

/**
 * RGBA text palette coder
 */
class RGBAPaletteCoder : PaletteCoder {
    companion object {
        private val regex = Regex("^#?\\s*([a-f0-9]{3,8})\\s*(.*)\\s*", RegexOption.IGNORE_CASE)
    }

    override fun decode(input: InputStream): Palette {
        val text = input.readText()
        val lines = text.lines()
        val result = Palette.Builder()

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) continue

            val matches = regex.findAll(trimmed).toList()
            if (matches.isEmpty()) {
                throw PaletteCoderException.InvalidRGBAHexString(trimmed)
            }

            matches.forEach { match ->
                val hex = match.groupValues[1]
                val name = match.groupValues[2].trim()

                try {
                    val color = PaletteColor(hex, ColorByteFormat.RGBA, name)
                    result.colors.add(color)
                } catch (_: Throwable) {
                    throw PaletteCoderException.InvalidRGBAHexString(hex)
                }
            }
        }

        val palette = result.build()

        if (palette.allColors().isEmpty()) {
            throw PaletteCoderException.InvalidFormat()
        }

        return palette
    }

    override fun encode(palette: Palette, output: OutputStream) {
        val flattenedColors = palette.allColors().map {
            if (it.colorSpace == ColorSpace.RGB) it else it.converted(ColorSpace.RGB)
        }
        var result = ""

        flattenedColors.forEach { color ->
            if (result.isNotEmpty()) {
                result += "\r\n"
            }
            val rgb = color.toRgb()
            result += rgb.hexString(
                format = ColorByteFormat.RGBA,
                hashmark = true,
                uppercase = false
            )
            if (color.name.isNotEmpty()) {
                result += " ${color.name}"
            }
        }

        output.write(result.toByteArray(java.nio.charset.StandardCharsets.UTF_8))
    }
}

