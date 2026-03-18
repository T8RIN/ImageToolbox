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
 * Corel Palette (CPL) coder
 */
class CPLPaletteCoder : PaletteCoder {
    companion object {
        private val spotPaletteTypes = listOf(
            3u,
            8u,
            9u,
            10u,
            11u,
            16u,
            17u,
            18u,
            20u,
            21u,
            22u,
            23u,
            26u,
            27u,
            28u,
            29u,
            30u,
            31u,
            32u,
            35u,
            36u,
            37u
        )
    }

    override fun decode(input: InputStream): Palette {
        val data = input.readBytes()
        val parser = BytesReader(data)
        val result = Palette.Builder()

        var spot = false
        var paletteType: UShort = 0u

        val version = parser.readUInt16(ByteOrder.BIG_ENDIAN)
        val numberOfColors: UShort

        when (version.toInt()) {
            0xDCDC -> {
                // This version has a palette name
                val filenamelength = parser.readUInt8().toInt()
                if (filenamelength > 0) {
                    result.name = parser.readStringASCII(filenamelength)
                }
                numberOfColors = parser.readUInt16(ByteOrder.LITTLE_ENDIAN)
            }

            0xCCBC, 0xCCDC -> {
                // This version doesn't have a palette name, just colors
                numberOfColors = parser.readUInt16(ByteOrder.LITTLE_ENDIAN)
            }

            else -> {
                // Read in headers
                val headerCount = parser.readInt32(ByteOrder.LITTLE_ENDIAN)

                data class Header(val hid: Int, val offset: Int)

                val headers = mutableListOf<Header>()
                repeat(headerCount) {
                    val hid = parser.readInt32(ByteOrder.LITTLE_ENDIAN)
                    val offset = parser.readInt32(ByteOrder.LITTLE_ENDIAN)
                    headers.add(Header(hid, offset))
                }

                // Name
                if (headers.isNotEmpty()) {
                    parser.seekSet(headers[0].offset)
                    val filenamelength = parser.readUInt8().toInt()
                    var name = ""
                    if (filenamelength > 0) {
                        if (version.toInt() == 0xCDDC) {
                            val nameData = parser.readBytes(filenamelength)
                            name = try {
                                String(nameData, Charset.forName("ISO-8859-1"))
                            } catch (_: Throwable) {
                                String(nameData, java.nio.charset.StandardCharsets.US_ASCII)
                            }
                        } else {
                            name = parser.readStringUTF16(ByteOrder.LITTLE_ENDIAN, filenamelength)
                        }
                    }
                    result.name = name
                }

                // Palette Type
                if (headers.size > 1) {
                    parser.seekSet(headers[1].offset)
                    paletteType = parser.readUInt16(ByteOrder.LITTLE_ENDIAN)
                }

                // Number of colors
                if (headers.size > 2) {
                    parser.seekSet(headers[2].offset)
                    numberOfColors = parser.readUInt16(ByteOrder.LITTLE_ENDIAN)

                    // Check if we are a spot palette
                    spot = spotPaletteTypes.contains(paletteType.toUInt())

                    parser.seekSet(headers[2].offset + 2)
                } else {
                    numberOfColors = 0u
                }
            }
        }

        val long = listOf(0xCDBC, 0xCDDC, 0xCDDD).contains(version.toInt()) &&
                paletteType.toInt() < 38 &&
                paletteType.toInt() != 5 &&
                paletteType.toInt() != 16

        for (i in 0 until numberOfColors.toInt()) {
            if (long) {
                parser.readUInt32(ByteOrder.LITTLE_ENDIAN)
            }

            val model = parser.readUInt16(ByteOrder.LITTLE_ENDIAN)
            parser.seek(2)

            var colorspace: ColorSpace? = null
            var colorComponents: List<Double>? = null

            var colorspace2: ColorSpace? = null
            var colorComponents2: List<Double>? = null

            when (model.toInt()) {
                2 -> {
                    // CMYK percentages
                    parser.seek(4)
                    val cmyk = parser.readBytes(4)
                    colorspace = ColorSpace.CMYK
                    colorComponents = listOf(
                        cmyk[0].toUByte().toDouble() / 100.0,
                        cmyk[1].toUByte().toDouble() / 100.0,
                        cmyk[2].toUByte().toDouble() / 100.0,
                        cmyk[3].toUByte().toDouble() / 100.0
                    )
                }

                3, 17 -> {
                    // CMYK fractions
                    parser.seek(4)
                    val cmyk = parser.readBytes(4)
                    colorspace = ColorSpace.CMYK
                    colorComponents = listOf(
                        cmyk[0].toUByte().toDouble() / 255.0,
                        cmyk[1].toUByte().toDouble() / 255.0,
                        cmyk[2].toUByte().toDouble() / 255.0,
                        cmyk[3].toUByte().toDouble() / 255.0
                    )
                }

                4 -> {
                    // CMY fractions
                    parser.seek(4)
                    val cmyk = parser.readBytes(4)
                    colorspace = ColorSpace.CMYK
                    colorComponents = listOf(
                        cmyk[0].toUByte().toDouble() / 255.0,
                        cmyk[1].toUByte().toDouble() / 255.0,
                        cmyk[2].toUByte().toDouble() / 255.0,
                        0.0
                    )
                }

                5, 21 -> {
                    // BGR fractions
                    parser.seek(4)
                    val bgr = parser.readBytes(3)
                    colorspace = ColorSpace.RGB
                    colorComponents = listOf(
                        bgr[2].toUByte().toDouble() / 255.0,
                        bgr[1].toUByte().toDouble() / 255.0,
                        bgr[0].toUByte().toDouble() / 255.0
                    )
                    parser.seek(1)
                }

                9 -> {
                    // Grayscale
                    parser.seek(4)
                    val k = parser.readUInt8().toInt()
                    colorspace = ColorSpace.Gray
                    colorComponents = listOf((255 - k) / 255.0)
                    parser.seek(3)
                }

                else -> {
                    // Unknown type, try to recover
                    parser.seek(8)
                }
            }

            if (long) {
                val model2 = parser.readUInt16(ByteOrder.LITTLE_ENDIAN)
                when (model2.toInt()) {
                    2 -> {
                        parser.seek(4)
                        val cmyk = parser.readBytes(4)
                        colorspace2 = ColorSpace.CMYK
                        colorComponents2 = listOf(
                            cmyk[0].toUByte().toDouble() / 100.0,
                            cmyk[1].toUByte().toDouble() / 100.0,
                            cmyk[2].toUByte().toDouble() / 100.0,
                            cmyk[3].toUByte().toDouble() / 100.0
                        )
                    }

                    3, 17 -> {
                        parser.seek(4)
                        val cmyk = parser.readBytes(4)
                        colorspace2 = ColorSpace.CMYK
                        colorComponents2 = listOf(
                            cmyk[0].toUByte().toDouble() / 255.0,
                            cmyk[1].toUByte().toDouble() / 255.0,
                            cmyk[2].toUByte().toDouble() / 255.0,
                            cmyk[3].toUByte().toDouble() / 255.0
                        )
                    }

                    4 -> {
                        parser.seek(4)
                        val cmyk = parser.readBytes(4)
                        colorspace2 = ColorSpace.CMYK
                        colorComponents2 = listOf(
                            cmyk[0].toUByte().toDouble() / 255.0,
                            cmyk[1].toUByte().toDouble() / 255.0,
                            cmyk[2].toUByte().toDouble() / 255.0,
                            0.0
                        )
                    }

                    5, 21 -> {
                        parser.seek(4)
                        val bgr = parser.readBytes(3)
                        colorspace2 = ColorSpace.RGB
                        colorComponents2 = listOf(
                            bgr[2].toUByte().toDouble() / 255.0,
                            bgr[1].toUByte().toDouble() / 255.0,
                            bgr[0].toUByte().toDouble() / 255.0
                        )
                        parser.seek(1)
                    }

                    9 -> {
                        parser.seek(4)
                        val k = parser.readUInt8().toInt()
                        colorspace2 = ColorSpace.Gray
                        colorComponents2 = listOf((255 - k) / 255.0)
                        parser.seek(3)
                    }

                    else -> {
                        parser.seek(8)
                    }
                }
            }

            val nameLength = parser.readUInt8().toInt()
            var colorName = ""
            if (nameLength > 0) {
                if (version.toInt() in listOf(0xCDDC, 0xDCDC, 0xCCDC)) {
                    val nameData = parser.readBytes(nameLength)
                    colorName = try {
                        String(nameData, Charset.forName("ISO-8859-1"))
                    } catch (_: Throwable) {
                        String(nameData, java.nio.charset.StandardCharsets.US_ASCII)
                    }
                } else {
                    colorName = parser.readStringUTF16(ByteOrder.LITTLE_ENDIAN, nameLength)
                }
            }

            if (colorspace != null && colorComponents != null) {
                try {
                    val readColor = PaletteColor(
                        name = colorName,
                        colorSpace = colorspace,
                        colorComponents = colorComponents,
                        colorType = if (spot) ColorType.Spot else ColorType.Normal
                    )
                    result.colors.add(readColor)
                } catch (_: Throwable) {
                    // Skip invalid colors
                }
            }

            if (colorspace2 != null && colorComponents2 != null) {
                try {
                    val readColor = PaletteColor(
                        name = colorName,
                        colorSpace = colorspace2,
                        colorComponents = colorComponents2,
                        colorType = if (spot) ColorType.Spot else ColorType.Normal
                    )
                    result.colors.add(readColor)
                } catch (_: Throwable) {
                    // Skip invalid colors
                }
            }

            if (version.toInt() == 0xCDDD) {
                // row and column
                parser.readUInt32(ByteOrder.LITTLE_ENDIAN)
                parser.readUInt32(ByteOrder.LITTLE_ENDIAN)
                parser.readUInt32(ByteOrder.LITTLE_ENDIAN)
            }
        }

        return result.build()
    }

    override fun encode(palette: Palette, output: OutputStream) {
        val writer = BytesWriter(output)
        val allColors = palette.allColors()
        val colorCount = allColors.size

        if (colorCount == 0) {
            throw PaletteCoderException.TooFewColors()
        }

        // Use version 0xDCDC (has palette name)
        writer.writeUInt16(0xDCDCu.toUShort(), ByteOrder.BIG_ENDIAN)

        // Filename length and name
        val filename = palette.name.ifEmpty { "Palette" }
        val filenameBytes = filename.toByteArray(Charset.forName("ISO-8859-1"))
        val filenameLength = filenameBytes.size.coerceIn(0, 255)
        writer.writeUInt8(filenameLength.toUByte())
        if (filenameLength > 0) {
            writer.writeData(filenameBytes.sliceArray(0 until filenameLength))
        }

        // Number of colors (2 bytes, little endian)
        writer.writeUInt16(colorCount.toUShort(), ByteOrder.LITTLE_ENDIAN)

        // Write colors - use model 5 (BGR fractions) for RGB colors
        allColors.forEach { color ->
            val rgb = color.toRgb()

            // Model (2 bytes, little endian) - 5 = BGR fractions
            writer.writeUInt16(5u.toUShort(), ByteOrder.LITTLE_ENDIAN)

            // Skip 2 bytes
            writer.writePattern(0x00, 0x00)

            // Skip 4 bytes (unknown)
            writer.writePattern(0x00, 0x00, 0x00, 0x00)

            // BGR values (3 bytes) - reversed order
            writer.writeByte((rgb.bf * 255).toInt().coerceIn(0, 255).toByte())
            writer.writeByte((rgb.gf * 255).toInt().coerceIn(0, 255).toByte())
            writer.writeByte((rgb.rf * 255).toInt().coerceIn(0, 255).toByte())

            // Skip 1 byte
            writer.writeByte(0)

            // Color name length
            val colorName = color.name.ifEmpty { "" }
            val colorNameBytes =
                colorName.toByteArray(Charset.forName("ISO-8859-1"))
            val colorNameLength = colorNameBytes.size.coerceIn(0, 255)
            writer.writeUInt8(colorNameLength.toUByte())

            // Color name
            if (colorNameLength > 0) {
                writer.writeData(colorNameBytes.sliceArray(0 until colorNameLength))
            }
        }
    }
}

