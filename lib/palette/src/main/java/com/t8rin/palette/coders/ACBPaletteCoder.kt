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

/**
 * Adobe Color Book (ACB) palette coder (decode only)
 */
class ACBPaletteCoder : PaletteCoder {
    override fun decode(input: InputStream): Palette {
        val parser = BytesReader(input)
        val result = Palette.Builder()

        // Check BOM "8BCB"
        val bom = parser.readStringASCII(4)
        if (bom != "8BCB") {
            throw PaletteCoderException.InvalidBOM()
        }

        // Version
        val version = parser.readUInt16(ByteOrder.BIG_ENDIAN)
        if (version.toInt() != 1) {
            // Log warning but continue
        }

        // Identifier
        parser.readUInt16(ByteOrder.BIG_ENDIAN)

        // Title
        var title = parser.readAdobePascalStyleString()
        if (title.startsWith("$$$")) {
            title = title.split("=").lastOrNull() ?: title
        }
        result.name = title

        // Prefix, suffix, description (skip)
        parser.readAdobePascalStyleString()
        parser.readAdobePascalStyleString()
        parser.readAdobePascalStyleString()

        // Color count
        val colorCount = parser.readUInt16(ByteOrder.BIG_ENDIAN).toInt()

        // Page size, page selector offset (skip)
        parser.readUInt16(ByteOrder.BIG_ENDIAN)
        parser.readUInt16(ByteOrder.BIG_ENDIAN)

        // Color space
        val colorSpace = parser.readUInt16(ByteOrder.BIG_ENDIAN).toInt()

        val colorspace: ColorSpace
        val componentCount: Int
        when (colorSpace) {
            0 -> { // RGB
                colorspace = ColorSpace.RGB
                componentCount = 3
            }

            2 -> { // CMYK
                colorspace = ColorSpace.CMYK
                componentCount = 4
            }

            7 -> { // LAB
                colorspace = ColorSpace.LAB
                componentCount = 3
            }

            8 -> { // Grayscale
                colorspace = ColorSpace.Gray
                componentCount = 1
            }

            else -> throw PaletteCoderException.UnsupportedColorSpace()
        }

        // Read colors
        for (i in 0 until colorCount) {
            val colorName = parser.readAdobePascalStyleString()
            val colorCode = parser.readStringASCII(6)

            // Color channels
            val channels = parser.readBytes(componentCount)

            if (colorName.trim().isEmpty() && colorCode.trim().isEmpty()) {
                continue
            }

            val mapped = channels.map { it.toUByte().toDouble() }
            val components: List<Double>

            when (colorspace) {
                ColorSpace.CMYK -> {
                    components = listOf(
                        ((255.0 - mapped[0]) / 255.0).coerceIn(0.0, 1.0),
                        ((255.0 - mapped[1]) / 255.0).coerceIn(0.0, 1.0),
                        ((255.0 - mapped[2]) / 255.0).coerceIn(0.0, 1.0),
                        ((255.0 - mapped[3]) / 255.0).coerceIn(0.0, 1.0)
                    )
                }

                ColorSpace.RGB -> {
                    components = listOf(
                        (mapped[0] / 255.0).coerceIn(0.0, 1.0),
                        (mapped[1] / 255.0).coerceIn(0.0, 1.0),
                        (mapped[2] / 255.0).coerceIn(0.0, 1.0)
                    )
                }

                ColorSpace.LAB -> {
                    components = listOf(
                        mapped[0] / 2.55,   // 0...100
                        mapped[1] - 128.0,  // -128...128
                        mapped[2] - 128.0   // -128...128
                    )
                }

                ColorSpace.Gray -> {
                    components = listOf((mapped[0] / 255.0).coerceIn(0.0, 1.0))
                }
            }

            try {
                val color = PaletteColor(
                    name = colorName,
                    colorSpace = colorspace,
                    colorComponents = components,
                    alpha = 1.0
                )
                result.colors.add(color)
            } catch (_: Throwable) {
                // Skip invalid colors
            }
        }

        // Spot identifier (may or may not be present)
        try {
            parser.readStringASCII(8)
        } catch (_: Throwable) {
            // Ignore if not present
        }

        return result.build()
    }


    @Suppress("VariableNeverRead", "AssignedValueIsNeverRead")
    override fun encode(palette: Palette, output: OutputStream) {
        val writer = BytesWriter(output)
        val allColors = palette.allColors()
        val colorCount = allColors.size

        if (colorCount == 0) {
            throw PaletteCoderException.TooFewColors()
        }

        // Determine color space - use first color's space, or convert all to RGB
        // ACB supports RGB (0), CMYK (2), LAB (7), Grayscale (8)
        var targetColorSpace = allColors[0].colorSpace
        var acbColorSpace: Int
        var componentCount: Int

        // Check if all colors can be in the same space, otherwise convert to RGB
        val allSameSpace = allColors.all { it.colorSpace == targetColorSpace }
        if (!allSameSpace || targetColorSpace !in listOf(
                ColorSpace.RGB,
                ColorSpace.CMYK,
                ColorSpace.LAB,
                ColorSpace.Gray
            )
        ) {
            targetColorSpace = ColorSpace.RGB
        }

        when (targetColorSpace) {
            ColorSpace.RGB -> {
                acbColorSpace = 0
                componentCount = 3
            }

            ColorSpace.CMYK -> {
                acbColorSpace = 2
                componentCount = 4
            }

            ColorSpace.LAB -> {
                acbColorSpace = 7
                componentCount = 3
            }

            ColorSpace.Gray -> {
                acbColorSpace = 8
                componentCount = 1
            }
        }

        // BOM "8BCB" (4 bytes)
        writer.writeStringASCII("8BCB")

        // Version (2 bytes, big endian) - typically 1
        writer.writeUInt16(1u, ByteOrder.BIG_ENDIAN)

        // Identifier (2 bytes, big endian) - typically 0
        writer.writeUInt16(0u, ByteOrder.BIG_ENDIAN)

        // Title (Adobe Pascal style string)
        val title = palette.name.ifEmpty { "Palette" }
        writer.writeAdobePascalStyleString(title)

        // Prefix (Adobe Pascal style string) - empty
        writer.writeAdobePascalStyleString("")

        // Suffix (Adobe Pascal style string) - empty
        writer.writeAdobePascalStyleString("")

        // Description (Adobe Pascal style string) - empty
        writer.writeAdobePascalStyleString("")

        // Color count (2 bytes, big endian)
        writer.writeUInt16(colorCount.toUShort(), ByteOrder.BIG_ENDIAN)

        // Page size (2 bytes, big endian) - typically 0
        writer.writeUInt16(0u, ByteOrder.BIG_ENDIAN)

        // Page selector offset (2 bytes, big endian) - typically 0
        writer.writeUInt16(0u, ByteOrder.BIG_ENDIAN)

        // Color space (2 bytes, big endian)
        writer.writeUInt16(acbColorSpace.toUShort(), ByteOrder.BIG_ENDIAN)

        // Write colors
        allColors.forEach { color ->
            // Convert color to target color space
            val convertedColor = if (color.colorSpace == targetColorSpace) {
                color
            } else {
                try {
                    color.converted(targetColorSpace)
                } catch (_: Throwable) {
                    // Fallback: convert to RGB
                    color.converted(ColorSpace.RGB)
                }
            }

            // Color name (Adobe Pascal style string)
            val colorName = convertedColor.name.ifEmpty { "Color" }
            writer.writeAdobePascalStyleString(colorName)

            // Color code (6 bytes ASCII) - typically empty or hex code
            val hexCode = try {
                val rgb = convertedColor.toRgb()
                String.format(
                    "%02X%02X%02X",
                    (rgb.rf * 255).toInt().coerceIn(0, 255),
                    (rgb.gf * 255).toInt().coerceIn(0, 255),
                    (rgb.bf * 255).toInt().coerceIn(0, 255)
                )
            } catch (_: Throwable) {
                "000000"
            }
            writer.writeStringASCII(hexCode.padEnd(6, '0').take(6))

            // Color channels
            val channels = when (targetColorSpace) {
                ColorSpace.CMYK -> {
                    // CMYK: 0 = 100% ink, so invert
                    listOf(
                        ((1.0 - convertedColor.colorComponents[0]) * 255).toInt().coerceIn(0, 255)
                            .toByte(),
                        ((1.0 - convertedColor.colorComponents[1]) * 255).toInt().coerceIn(0, 255)
                            .toByte(),
                        ((1.0 - convertedColor.colorComponents[2]) * 255).toInt().coerceIn(0, 255)
                            .toByte(),
                        ((1.0 - convertedColor.colorComponents[3]) * 255).toInt().coerceIn(0, 255)
                            .toByte()
                    )
                }

                ColorSpace.RGB -> {
                    listOf(
                        (convertedColor.colorComponents[0] * 255).toInt().coerceIn(0, 255).toByte(),
                        (convertedColor.colorComponents[1] * 255).toInt().coerceIn(0, 255).toByte(),
                        (convertedColor.colorComponents[2] * 255).toInt().coerceIn(0, 255).toByte()
                    )
                }

                ColorSpace.LAB -> {
                    listOf(
                        (convertedColor.colorComponents[0] * 2.55).toInt().coerceIn(0, 255)
                            .toByte(),
                        ((convertedColor.colorComponents[1] + 128.0).toInt()
                            .coerceIn(0, 255)).toByte(),
                        ((convertedColor.colorComponents[2] + 128.0).toInt()
                            .coerceIn(0, 255)).toByte()
                    )
                }

                ColorSpace.Gray -> {
                    listOf(
                        (convertedColor.colorComponents[0] * 255).toInt().coerceIn(0, 255).toByte()
                    )
                }
            }

            writer.writeData(channels.toByteArray())
        }
    }
}