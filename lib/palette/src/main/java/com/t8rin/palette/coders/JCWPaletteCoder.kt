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
import com.t8rin.palette.ColorType
import com.t8rin.palette.Palette
import com.t8rin.palette.PaletteCoder
import com.t8rin.palette.PaletteCoderException
import com.t8rin.palette.PaletteColor
import com.t8rin.palette.utils.ByteOrder
import com.t8rin.palette.utils.BytesReader
import com.t8rin.palette.utils.BytesWriter
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset

/**
 * Xara Palette (JCW) coder
 */
class JCWPaletteCoder : PaletteCoder {
    enum class SupportedColorSpace {
        CMYK, RGB, HSB
    }

    override fun decode(input: InputStream): Palette {
        val parser = BytesReader(input)
        val result = Palette.Builder()

        // Check BOM "JCW"
        val bom = parser.readStringASCII(3)
        if (bom != "JCW") {
            throw PaletteCoderException.InvalidBOM()
        }

        // Version
        val version = parser.readUInt8()
        if (version.toInt() != 1) {
            // Log warning but continue
        }

        // Number of colors
        val numColors = parser.readUInt16(ByteOrder.LITTLE_ENDIAN).toInt()

        // Expected color space for ALL colors
        val colorSpace = parser.readUInt8().toInt()

        // Expected length of color names
        val nameLength = parser.readUInt8().toInt()

        data class SupportedCS(val space: SupportedColorSpace, val type: ColorType)

        val ct: SupportedCS = when (colorSpace) {
            1, 8 -> SupportedCS(SupportedColorSpace.CMYK, ColorType.Normal)
            9 -> SupportedCS(SupportedColorSpace.CMYK, ColorType.Spot)
            2, 10 -> SupportedCS(SupportedColorSpace.RGB, ColorType.Normal)
            11 -> SupportedCS(SupportedColorSpace.RGB, ColorType.Spot)
            3, 12 -> SupportedCS(SupportedColorSpace.HSB, ColorType.Normal)
            13 -> SupportedCS(SupportedColorSpace.HSB, ColorType.Spot)
            else -> throw PaletteCoderException.InvalidFormat()
        }

        for (index in 0 until numColors) {
            val c0 = parser.readUInt16(ByteOrder.LITTLE_ENDIAN).toInt().coerceIn(0, 10000)
            val c1 = parser.readUInt16(ByteOrder.LITTLE_ENDIAN).toInt().coerceIn(0, 10000)
            val c2 = parser.readUInt16(ByteOrder.LITTLE_ENDIAN).toInt().coerceIn(0, 10000)
            val c3 = parser.readUInt16(ByteOrder.LITTLE_ENDIAN).toInt().coerceIn(0, 10000)

            // Read the bytes for the name
            val nameData = parser.readBytes(nameLength)

            // Try to convert to string (ISO Latin1 or ASCII)
            val name = try {
                String(nameData, Charset.forName("ISO-8859-1"))
            } catch (_: Throwable) {
                String(nameData, java.nio.charset.StandardCharsets.US_ASCII)
            }.trimEnd { it.code == 0 }.ifEmpty { "c$index" }

            val color = when (ct.space) {
                SupportedColorSpace.RGB -> {
                    PaletteColor.rgb(
                        r = c0 / 10000.0,
                        g = c1 / 10000.0,
                        b = c2 / 10000.0,
                        name = name,
                        colorType = ct.type
                    )
                }

                SupportedColorSpace.CMYK -> {
                    PaletteColor.cmyk(
                        c = c0 / 10000.0,
                        m = c1 / 10000.0,
                        y = c2 / 10000.0,
                        k = c3 / 10000.0,
                        name = name,
                        colorType = ct.type
                    )
                }

                SupportedColorSpace.HSB -> {
                    PaletteColor.hsb(
                        hf = c0 / 10000.0,
                        sf = c1 / 10000.0,
                        bf = c2 / 10000.0,
                        name = name,
                        colorType = ct.type
                    )
                }
            }

            result.colors.add(color)
        }

        return result.build()
    }

    override fun encode(palette: Palette, output: OutputStream) {
        val writer = BytesWriter(output)

        // Palette colors (all RGB for the first attempt)
        val colors = palette.allColors().map { color ->
            if (color.colorSpace == ColorSpace.RGB) color else color.converted(ColorSpace.RGB)
        }

        // BOM
        writer.writeStringASCII("JCW")
        // version
        writer.writeUInt8(1u)

        // The number of colors (all RGB for the moment)
        writer.writeUInt16(colors.size.toUShort(), ByteOrder.LITTLE_ENDIAN)

        // Colorspace (basic RGB)
        writer.writeUInt8(10u)

        // Name length (14 bytes)
        writer.writeUInt8(14u)

        colors.forEach { color ->
            // Color components
            val rgb = color.toRgb()
            writer.writeUInt16((rgb.rf * 10000).toInt().toUShort(), ByteOrder.LITTLE_ENDIAN)
            writer.writeUInt16((rgb.gf * 10000).toInt().toUShort(), ByteOrder.LITTLE_ENDIAN)
            writer.writeUInt16((rgb.bf * 10000).toInt().toUShort(), ByteOrder.LITTLE_ENDIAN)
            writer.writeUInt16(0u, ByteOrder.LITTLE_ENDIAN)

            // Write the name (ISO-Latin1, trimmed to 14 bytes, padded with zeros)
            val nameBytes = try {
                color.name.toByteArray(Charset.forName("ISO-8859-1"))
            } catch (_: Throwable) {
                color.name.toByteArray(java.nio.charset.StandardCharsets.US_ASCII)
            }

            val paddedName = ByteArray(14) { index ->
                if (index < nameBytes.size) nameBytes[index] else 0
            }
            writer.writeData(paddedName)
        }
    }
}

