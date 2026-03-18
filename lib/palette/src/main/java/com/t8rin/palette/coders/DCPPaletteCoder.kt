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
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.OutputStream

/**
 * ColorPaletteCodable binary format (DCP) coder
 */
class DCPPaletteCoder : PaletteCoder {
    companion object {
        const val BOM: UShort = 32156u
        const val VERSION: UShort = 1u
        const val GROUP_IDENTIFIER: UByte = 0xEAu
        const val COLOR_IDENTIFIER: UByte = 0xC0u
    }

    override fun decode(input: InputStream): Palette {
        // Read all data first for seek support
        val data = input.readBytes()
        val parser = BytesReader(ByteArrayInputStream(data))
        val result = Palette.Builder()

        // Read BOM
        if (parser.readUInt16(ByteOrder.LITTLE_ENDIAN) != BOM) {
            throw PaletteCoderException.InvalidBOM()
        }

        // Read version
        if (parser.readUInt16(ByteOrder.LITTLE_ENDIAN) != VERSION) {
            throw PaletteCoderException.InvalidBOM()
        }

        // Palette name
        result.name = parser.readPascalStringUTF16(ByteOrder.LITTLE_ENDIAN)

        // Read the expected number of groups
        val expectedGroupCount = parser.readUInt16(ByteOrder.LITTLE_ENDIAN).toInt()

        // Read in the groups
        val groups = mutableListOf<ColorGroup>()
        for (i in 0 until expectedGroupCount) {
            // Read a group identifier tag
            if (parser.readByte() != GROUP_IDENTIFIER.toByte()) {
                throw PaletteCoderException.InvalidBOM()
            }

            // Read the group name
            val groupName = parser.readPascalStringUTF16(ByteOrder.LITTLE_ENDIAN)

            // Read the expected number of colors
            val expectedColorCount = parser.readUInt16(ByteOrder.LITTLE_ENDIAN).toInt()

            // The groups colors
            val colors = mutableListOf<PaletteColor>()
            for (j in 0 until expectedColorCount) {
                colors.add(parser.readColor())
            }

            groups.add(ColorGroup(colors = colors, name = groupName))
        }

        // First group is always the 'global' colors
        if (groups.isEmpty()) {
            throw PaletteCoderException.InvalidFormat()
        }

        result.colors = groups[0].colors.toMutableList()
        result.groups = groups.drop(1).toMutableList()
        return result.build()
    }

    override fun encode(palette: Palette, output: OutputStream) {
        val writer = BytesWriter(output)

        // Expected BOM
        writer.writeUInt16(BOM, ByteOrder.LITTLE_ENDIAN)

        // Version
        writer.writeUInt16(VERSION, ByteOrder.LITTLE_ENDIAN)

        // Write the palette name
        writer.writePascalStringUTF16(palette.name, ByteOrder.LITTLE_ENDIAN)

        // Write the number of groups (global colors + groups)
        val allGroups = palette.allGroups
        writer.writeUInt16(allGroups.size.toUShort(), ByteOrder.LITTLE_ENDIAN)

        allGroups.forEach { group ->
            // Write a group identifier tag
            writer.writeByte(GROUP_IDENTIFIER.toByte())

            // Write the group name
            writer.writePascalStringUTF16(group.name, ByteOrder.LITTLE_ENDIAN)

            // Write the number of colors in the group
            writer.writeUInt16(group.colors.size.toUShort(), ByteOrder.LITTLE_ENDIAN)

            group.colors.forEach { color ->
                writer.writeColor(color)
            }
        }
    }
}

private fun BytesReader.readColor(): PaletteColor {
    // Read a color identifier tag
    if (readByte() != DCPPaletteCoder.COLOR_IDENTIFIER.toByte()) {
        throw PaletteCoderException.InvalidBOM()
    }

    // Read the color name
    val colorName = readPascalStringUTF16(ByteOrder.LITTLE_ENDIAN)

    val colorspaceID = readUInt8()

    val colorSpace: ColorSpace
    val components: List<Double>

    when (colorspaceID.toInt()) {
        1 -> {
            // CMYK
            colorSpace = ColorSpace.CMYK
            components = listOf(
                readFloat32(ByteOrder.LITTLE_ENDIAN).toDouble(),
                readFloat32(ByteOrder.LITTLE_ENDIAN).toDouble(),
                readFloat32(ByteOrder.LITTLE_ENDIAN).toDouble(),
                readFloat32(ByteOrder.LITTLE_ENDIAN).toDouble()
            )
        }

        2 -> {
            // RGB
            colorSpace = ColorSpace.RGB
            components = listOf(
                readFloat32(ByteOrder.LITTLE_ENDIAN).toDouble(),
                readFloat32(ByteOrder.LITTLE_ENDIAN).toDouble(),
                readFloat32(ByteOrder.LITTLE_ENDIAN).toDouble()
            )
        }

        3 -> {
            // LAB
            colorSpace = ColorSpace.LAB
            components = listOf(
                readFloat32(ByteOrder.LITTLE_ENDIAN).toDouble(),
                readFloat32(ByteOrder.LITTLE_ENDIAN).toDouble(),
                readFloat32(ByteOrder.LITTLE_ENDIAN).toDouble()
            )
        }

        4 -> {
            // Gray
            colorSpace = ColorSpace.Gray
            components = listOf(readFloat32(ByteOrder.LITTLE_ENDIAN).toDouble())
        }

        else -> throw PaletteCoderException.InvalidFormat()
    }

    // Alpha component
    val alpha = readFloat32(ByteOrder.LITTLE_ENDIAN).toDouble()

    // Color type
    val type = readUInt8()
    val colorType: ColorType = when (type.toInt()) {
        1 -> ColorType.Global
        2 -> ColorType.Spot
        3 -> ColorType.Normal
        else -> throw PaletteCoderException.InvalidFormat()
    }

    return PaletteColor(
        name = colorName,
        colorType = colorType,
        colorSpace = colorSpace,
        colorComponents = components,
        alpha = alpha
    )
}

private fun BytesWriter.writeColor(color: PaletteColor) {
    // Write a color identifier tag
    writeByte(DCPPaletteCoder.COLOR_IDENTIFIER.toByte())

    // Color name
    writePascalStringUTF16(color.name, ByteOrder.LITTLE_ENDIAN)

    // Write the colorspace identifier
    val colorspaceID: UByte = when (color.colorSpace) {
        ColorSpace.CMYK -> 1u
        ColorSpace.RGB -> 2u
        ColorSpace.LAB -> 3u
        ColorSpace.Gray -> 4u
    }
    writeUInt8(colorspaceID)

    // Write the color components
    color.colorComponents.forEach { comp ->
        writeFloat32(comp.toFloat(), ByteOrder.LITTLE_ENDIAN)
    }

    // Color alpha
    writeFloat32(color.alpha.toFloat(), ByteOrder.LITTLE_ENDIAN)

    // Color type
    val type: UByte = when (color.colorType) {
        ColorType.Global -> 1u
        ColorType.Spot -> 2u
        ColorType.Normal -> 3u
    }
    writeUInt8(type)
}

