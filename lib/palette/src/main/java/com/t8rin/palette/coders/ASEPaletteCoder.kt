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
import com.t8rin.palette.ColorType
import com.t8rin.palette.Palette
import com.t8rin.palette.PaletteCoder
import com.t8rin.palette.PaletteCoderException
import com.t8rin.palette.PaletteColor
import com.t8rin.palette.utils.ByteOrder
import com.t8rin.palette.utils.BytesReader
import com.t8rin.palette.utils.BytesWriter
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

/**
 * Adobe Swatch Exchange (ASE) palette coder
 */
class ASEPaletteCoder : PaletteCoder {
    companion object {
        private val ASE_HEADER_DATA = byteArrayOf(65, 83, 69, 70) // "ASEF"
        private val ASE_GROUP_START: UShort = 0xC001u
        private val ASE_GROUP_END: UShort = 0xC002u
        private val ASE_BLOCK_COLOR: UShort = 0x0001u
    }

    override fun decode(input: InputStream): Palette {
        val reader = BytesReader(input)
        val result = Palette.Builder()

        // Read and validate header
        val header = reader.readData(4)
        if (!header.contentEquals(ASE_HEADER_DATA)) {
            throw PaletteCoderException.InvalidASEHeader()
        }

        // Read version
        val version0 = reader.readUInt16(ByteOrder.BIG_ENDIAN)
        val version1 = reader.readUInt16(ByteOrder.BIG_ENDIAN)
        if (version0.toUInt() != 1u || version1.toUInt() != 0u) {
            // Unknown version, but continue
        }

        // Read number of blocks
        val numberOfBlocks = reader.readUInt32(ByteOrder.BIG_ENDIAN)

        var currentGroup: ColorGroup? = null

        // Read all blocks
        repeat(numberOfBlocks.toInt()) {
            val type = reader.readUInt16(ByteOrder.BIG_ENDIAN)
            reader.readUInt32(ByteOrder.BIG_ENDIAN)

            when (type) {
                ASE_GROUP_START -> {
                    if (currentGroup != null) {
                        throw PaletteCoderException.GroupAlreadyOpen()
                    }
                    currentGroup = readStartGroupBlock(reader)
                }

                ASE_GROUP_END -> {
                    if (currentGroup == null) {
                        throw PaletteCoderException.GroupNotOpen()
                    }
                    result.groups.add(currentGroup)
                    currentGroup = null
                }

                ASE_BLOCK_COLOR -> {
                    val color = readColor(reader)
                    if (currentGroup != null) {
                        currentGroup = currentGroup.copy(
                            colors = currentGroup.colors + color
                        )
                    } else {
                        result.colors.add(color)
                    }
                }

                else -> throw PaletteCoderException.UnknownBlockType()
            }
        }

        // If there's still an open group, add it
        if (currentGroup != null) {
            result.groups.add(currentGroup)
        }

        return result.build()
    }

    private fun readStartGroupBlock(reader: BytesReader): ColorGroup {
        reader.readUInt16(ByteOrder.BIG_ENDIAN)
        val name = reader.readStringUTF16NullTerminated(ByteOrder.BIG_ENDIAN)
        return ColorGroup(name = name)
    }

    private fun readColor(reader: BytesReader): PaletteColor {
        reader.readUInt16(ByteOrder.BIG_ENDIAN)
        val name = reader.readStringUTF16NullTerminated(ByteOrder.BIG_ENDIAN)

        val colorModel = when (val mode = reader.readStringASCII(4)) {
            "CMYK" -> ASEColorModel.CMYK
            "RGB " -> ASEColorModel.RGB
            "LAB " -> ASEColorModel.LAB
            "Gray" -> ASEColorModel.Gray
            else -> throw PaletteCoderException.UnknownColorMode(mode)
        }

        val colors: List<Double>
        val colorspace: ColorSpace

        when (colorModel) {
            ASEColorModel.CMYK -> {
                colorspace = ColorSpace.CMYK
                colors = listOf(
                    reader.readFloat32(ByteOrder.BIG_ENDIAN).toDouble(),
                    reader.readFloat32(ByteOrder.BIG_ENDIAN).toDouble(),
                    reader.readFloat32(ByteOrder.BIG_ENDIAN).toDouble(),
                    reader.readFloat32(ByteOrder.BIG_ENDIAN).toDouble()
                )
            }

            ASEColorModel.RGB -> {
                colorspace = ColorSpace.RGB
                colors = listOf(
                    reader.readFloat32(ByteOrder.BIG_ENDIAN).toDouble(),
                    reader.readFloat32(ByteOrder.BIG_ENDIAN).toDouble(),
                    reader.readFloat32(ByteOrder.BIG_ENDIAN).toDouble()
                )
            }

            ASEColorModel.LAB -> {
                colorspace = ColorSpace.LAB
                colors = listOf(
                    reader.readFloat32(ByteOrder.BIG_ENDIAN).toDouble() * 100.0,
                    reader.readFloat32(ByteOrder.BIG_ENDIAN).toDouble(),
                    reader.readFloat32(ByteOrder.BIG_ENDIAN).toDouble()
                )
            }

            ASEColorModel.Gray -> {
                colorspace = ColorSpace.Gray
                colors = listOf(reader.readFloat32(ByteOrder.BIG_ENDIAN).toDouble())
            }
        }

        val colorTypeValue = reader.readUInt16(ByteOrder.BIG_ENDIAN)
        val colorType = when (colorTypeValue.toInt()) {
            0 -> ColorType.Global
            1 -> ColorType.Spot
            2 -> ColorType.Normal
            else -> throw PaletteCoderException.UnknownColorType(colorTypeValue.toInt())
        }

        return PaletteColor(
            colorSpace = colorspace,
            colorComponents = colors,
            name = name,
            colorType = colorType
        )
    }

    override fun encode(palette: Palette, output: OutputStream) {
        val writer = BytesWriter(output)

        // Write header
        writer.writeData(ASE_HEADER_DATA)
        writer.writeUInt16(1u, ByteOrder.BIG_ENDIAN)
        writer.writeUInt16(0u, ByteOrder.BIG_ENDIAN)

        var totalBlocks = palette.colors.size + (palette.groups.size * 2)
        palette.groups.forEach { totalBlocks += it.colors.size }

        writer.writeUInt32(totalBlocks.toUInt(), ByteOrder.BIG_ENDIAN)

        // Write global colors
        palette.colors.forEach { color ->
            writeColorData(writer, color)
        }

        // Write groups
        palette.groups.forEach { group ->
            // Group header
            writer.writeUInt16(ASE_GROUP_START, ByteOrder.BIG_ENDIAN)

            val groupData = ByteArrayOutputStream()
            val groupWriter = BytesWriter(groupData)
            val groupNameBytes = group.name.toByteArray(java.nio.charset.StandardCharsets.UTF_16BE)
            val groupNameLen = (group.name.length + 1).toUShort()
            groupWriter.writeUInt16(groupNameLen, ByteOrder.BIG_ENDIAN)
            if (groupNameBytes.isNotEmpty()) {
                groupWriter.writeData(groupNameBytes)
            }
            groupWriter.writeData(byteArrayOf(0, 0)) // null terminator

            writer.writeUInt32(groupData.size().toUInt(), ByteOrder.BIG_ENDIAN)
            writer.writeData(groupData.toByteArray())

            // Group colors
            group.colors.forEach { color ->
                writeColorData(writer, color)
            }

            // Group footer
            writer.writeUInt16(ASE_GROUP_END, ByteOrder.BIG_ENDIAN)
            writer.writeUInt32(0u, ByteOrder.BIG_ENDIAN)
        }
    }

    private fun writeColorData(writer: BytesWriter, color: PaletteColor) {
        writer.writeUInt16(ASE_BLOCK_COLOR, ByteOrder.BIG_ENDIAN)

        val colorData = ByteArrayOutputStream()
        val colorWriter = BytesWriter(colorData)

        // Write name
        val colorNameBytes = color.name.toByteArray(java.nio.charset.StandardCharsets.UTF_16BE)
        val colorNameLen = (color.name.length + 1).toUShort()
        colorWriter.writeUInt16(colorNameLen, ByteOrder.BIG_ENDIAN)
        if (colorNameBytes.isNotEmpty()) {
            colorWriter.writeData(colorNameBytes)
        }
        colorWriter.writeData(byteArrayOf(0, 0)) // null terminator

        // Write model
        val colorModel = when (color.colorSpace) {
            ColorSpace.RGB -> ASEColorModel.RGB
            ColorSpace.CMYK -> ASEColorModel.CMYK
            ColorSpace.LAB -> ASEColorModel.LAB
            ColorSpace.Gray -> ASEColorModel.Gray
        }
        colorWriter.writeStringASCII(colorModel.rawValue)

        // Write components
        when (color.colorSpace) {
            ColorSpace.CMYK -> {
                color.colorComponents.forEach { comp ->
                    colorWriter.writeFloat32(comp.toFloat(), ByteOrder.BIG_ENDIAN)
                }
            }

            ColorSpace.RGB -> {
                color.colorComponents.forEach { comp ->
                    colorWriter.writeFloat32(comp.toFloat(), ByteOrder.BIG_ENDIAN)
                }
            }

            ColorSpace.LAB -> {
                colorWriter.writeFloat32(
                    (color.colorComponents[0] / 100.0).toFloat(),
                    ByteOrder.BIG_ENDIAN
                )
                colorWriter.writeFloat32(color.colorComponents[1].toFloat(), ByteOrder.BIG_ENDIAN)
                colorWriter.writeFloat32(color.colorComponents[2].toFloat(), ByteOrder.BIG_ENDIAN)
            }

            ColorSpace.Gray -> {
                colorWriter.writeFloat32(color.colorComponents[0].toFloat(), ByteOrder.BIG_ENDIAN)
            }
        }

        // Write color type
        val colorTypeValue: UShort = when (color.colorType) {
            ColorType.Global -> 0u
            ColorType.Spot -> 1u
            ColorType.Normal -> 2u
        }
        colorWriter.writeUInt16(colorTypeValue, ByteOrder.BIG_ENDIAN)

        writer.writeUInt32(colorData.size().toUInt(), ByteOrder.BIG_ENDIAN)
        writer.writeData(colorData.toByteArray())
    }

    private enum class ASEColorModel(val rawValue: String) {
        CMYK("CMYK"),
        RGB("RGB "),
        LAB("LAB "),
        Gray("Gray")
    }
}

