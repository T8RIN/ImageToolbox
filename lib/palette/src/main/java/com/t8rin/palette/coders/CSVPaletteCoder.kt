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
import com.t8rin.palette.Palette
import com.t8rin.palette.PaletteCoder
import com.t8rin.palette.PaletteCoderException
import com.t8rin.palette.PaletteColor
import com.t8rin.palette.utils.CSVParser
import com.t8rin.palette.utils.hexString
import com.t8rin.palette.utils.readText
import java.io.InputStream
import java.io.OutputStream

/**
 * CSV palette coder
 */
class CSVPaletteCoder(
    private val hexFormat: ColorByteFormat = ColorByteFormat.RGB
) : PaletteCoder {
    override fun decode(input: InputStream): Palette {
        val text = input.readText()
        val records = CSVParser.parse(text)

        if (records.isEmpty()) {
            throw PaletteCoderException.InvalidFormat()
        }

        val colors = when (records.size) {
            1 -> {
                // Single line of hex colors
                records[0].mapNotNull { field ->
                    try {
                        PaletteColor(field.trim(), ColorByteFormat.RGBA)
                    } catch (_: Throwable) {
                        null
                    }
                }
            }

            else -> {
                records.mapNotNull { record ->
                    when {
                        record.isEmpty() -> null
                        record.size == 1 -> {
                            try {
                                PaletteColor(record[0].trim(), ColorByteFormat.RGBA)
                            } catch (_: Throwable) {
                                null
                            }
                        }

                        else -> {
                            try {
                                PaletteColor(
                                    record[0].trim(),
                                    ColorByteFormat.RGBA,
                                    record[1].trim()
                                )
                            } catch (_: Throwable) {
                                null
                            }
                        }
                    }
                }
            }
        }

        if (colors.isEmpty()) {
            throw PaletteCoderException.InvalidFormat()
        }

        return Palette(colors = colors.toMutableList())
    }

    override fun encode(palette: Palette, output: OutputStream) {
        val allColors = palette.allColors()
        var results = ""

        allColors.forEach { color ->
            results += color.hexString(hexFormat, hashmark = true, uppercase = false)
            if (color.name.isNotEmpty()) {
                results += ", ${color.name}"
            }
            results += "\n"
        }

        output.write(results.toByteArray(java.nio.charset.StandardCharsets.UTF_8))
    }
}


