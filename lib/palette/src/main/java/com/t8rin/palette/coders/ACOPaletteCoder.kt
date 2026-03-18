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
import com.t8rin.palette.utils.ByteOrder
import com.t8rin.palette.utils.BytesReader
import com.t8rin.palette.utils.BytesWriter
import java.io.InputStream
import java.io.OutputStream
import kotlin.math.abs

/**
 * Adobe Photoshop Swatch (ACO) palette coder
 */
class ACOPaletteCoder : PaletteCoder {
    private enum class ACOColorspace(val rawValue: UShort) {
        RGB(0u),
        HSB(1u),
        CMYK(2u),
        LAB(7u),
        GRAYSCALE(8u)
    }

    override fun decode(input: InputStream): Palette {
        val reader = BytesReader(input)
        val result = Palette.Builder()

        val v1Colors = mutableListOf<PaletteColor>()
        val v2Colors = mutableListOf<PaletteColor>()

        for (type in 1..2) {
            try {
                val version = reader.readUInt16(ByteOrder.BIG_ENDIAN)
                if (version.toInt() != type) {
                    throw PaletteCoderException.InvalidVersion()
                }
            } catch (_: Throwable) {
                // Version 1 file only
                result.colors = v1Colors
                return result.build()
            }

            val numberOfColors = reader.readUInt16(ByteOrder.BIG_ENDIAN)

            repeat(numberOfColors.toInt()) {
                val colorSpace = reader.readUInt16(ByteOrder.BIG_ENDIAN)
                val c0 = reader.readUInt16(ByteOrder.BIG_ENDIAN)
                val c1 = reader.readUInt16(ByteOrder.BIG_ENDIAN)
                val c2 = reader.readUInt16(ByteOrder.BIG_ENDIAN)
                val c3 = reader.readUInt16(ByteOrder.BIG_ENDIAN)

                val name = if (type == 2) {
                    reader.readAdobePascalStyleString()
                } else {
                    ""
                }

                val acoSpace = ACOColorspace.entries.find { it.rawValue == colorSpace }
                val color = when (acoSpace) {
                    ACOColorspace.RGB -> PaletteColor.rgb(
                        r = c0.toDouble() / 65535.0,
                        g = c1.toDouble() / 65535.0,
                        b = c2.toDouble() / 65535.0,
                        name = name
                    )

                    ACOColorspace.CMYK -> PaletteColor.cmyk(
                        c = (65535 - c0.toInt()).toDouble() / 65535.0,
                        m = (65535 - c1.toInt()).toDouble() / 65535.0,
                        y = (65535 - c2.toInt()).toDouble() / 65535.0,
                        k = (65535 - c3.toInt()).toDouble() / 65535.0,
                        name = name
                    )

                    ACOColorspace.GRAYSCALE -> PaletteColor.gray(
                        white = c0.toDouble() / 10000.0,
                        name = name
                    )

                    ACOColorspace.LAB -> PaletteColor.lab(
                        l = c0.toDouble() / 100.0,
                        a = c1.toDouble() / 100.0,
                        b = c2.toDouble() / 100.0,
                        name = name
                    )

                    ACOColorspace.HSB -> {
                        // Convert HSB to RGB
                        val h = c0.toDouble() / 65535.0
                        val s = c1.toDouble() / 65535.0
                        val b = c2.toDouble() / 65535.0
                        // Simple HSB to RGB conversion
                        val rgb = hsbToRgb(h, s, b)
                        PaletteColor.rgb(rgb.first, rgb.second, rgb.third, name = name)
                    }

                    null -> PaletteColor.rgb(1.0, 0.0, 0.0, 0.5, name = "Unsupported Colorspace")
                }

                if (type == 1) {
                    v1Colors.add(color)
                } else {
                    v2Colors.add(color)
                }
            }
        }

        if (v2Colors.isNotEmpty()) {
            result.colors = v2Colors
        } else {
            result.colors = v1Colors
        }

        return result.build()
    }

    override fun encode(palette: Palette, output: OutputStream) {
        val writer = BytesWriter(output)
        val allColors = palette.allColors()

        // Write both v1 and v2
        for (type in 1..2) {
            writer.writeUInt16(type.toUShort(), ByteOrder.BIG_ENDIAN)
            writer.writeUInt16(allColors.size.toUShort(), ByteOrder.BIG_ENDIAN)

            allColors.forEach { color ->
                val (c0, c1, c2, c3, acoModel) = when (color.colorSpace) {
                    ColorSpace.RGB -> {
                        val rgb = color.toRgb()
                        Quad(
                            (rgb.rf * 65535).toInt().toUShort(),
                            (rgb.gf * 65535).toInt().toUShort(),
                            (rgb.bf * 65535).toInt().toUShort(),
                            0u,
                            ACOColorspace.RGB
                        )
                    }

                    ColorSpace.CMYK -> {
                        Quad(
                            (65535 - (color.colorComponents[0] * 65535).toInt()).toUShort(),
                            (65535 - (color.colorComponents[1] * 65535).toInt()).toUShort(),
                            (65535 - (color.colorComponents[2] * 65535).toInt()).toUShort(),
                            (65535 - (color.colorComponents[3] * 65535).toInt()).toUShort(),
                            ACOColorspace.CMYK
                        )
                    }

                    ColorSpace.Gray -> {
                        Quad(
                            (color.colorComponents[0] * 10000).toInt().toUShort(),
                            0u, 0u, 0u,
                            ACOColorspace.GRAYSCALE
                        )
                    }

                    ColorSpace.LAB -> {
                        // Convert LAB to RGB for ACO
                        val converted = color.converted(ColorSpace.RGB)
                        val rgb = converted.toRgb()
                        Quad(
                            (rgb.rf * 65535).toInt().toUShort(),
                            (rgb.gf * 65535).toInt().toUShort(),
                            (rgb.bf * 65535).toInt().toUShort(),
                            0u,
                            ACOColorspace.RGB
                        )
                    }
                }

                writer.writeUInt16(acoModel.rawValue, ByteOrder.BIG_ENDIAN)
                writer.writeUInt16(c0, ByteOrder.BIG_ENDIAN)
                writer.writeUInt16(c1, ByteOrder.BIG_ENDIAN)
                writer.writeUInt16(c2, ByteOrder.BIG_ENDIAN)
                writer.writeUInt16(c3, ByteOrder.BIG_ENDIAN)

                if (type == 2) {
                    writer.writeAdobePascalStyleString(color.name)
                }
            }
        }
    }

    private data class Quad(
        val a: UShort,
        val b: UShort,
        val c: UShort,
        val d: UShort,
        val model: ACOColorspace
    )

    private fun hsbToRgb(h: Double, s: Double, b: Double): Triple<Double, Double, Double> {
        val c = b * s
        val x = c * (1 - abs((h * 6) % 2 - 1))
        val m = b - c

        val (r, g, bl) = when {
            h < 1.0 / 6 -> Triple(c, x, 0.0)
            h < 2.0 / 6 -> Triple(x, c, 0.0)
            h < 3.0 / 6 -> Triple(0.0, c, x)
            h < 4.0 / 6 -> Triple(0.0, x, c)
            h < 5.0 / 6 -> Triple(x, 0.0, c)
            else -> Triple(c, 0.0, x)
        }

        return Triple(r + m, g + m, bl + m)
    }
}

