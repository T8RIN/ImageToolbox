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
import com.t8rin.palette.utils.ByteOrder
import com.t8rin.palette.utils.BytesReader
import com.t8rin.palette.utils.BytesWriter
import java.io.InputStream
import java.io.OutputStream

/**
 * Microsoft RIFF palette coder
 */
class RIFFPaletteCoder : PaletteCoder {
    override fun decode(input: InputStream): Palette {
        val parser = BytesReader(input)
        val result = Palette.Builder()

        val header = parser.readStringASCII(4)
        if (header != "RIFF") {
            throw PaletteCoderException.InvalidFormat()
        }

        parser.readInt32(ByteOrder.LITTLE_ENDIAN)

        val riffType = parser.readStringASCII(4)
        if (riffType != "PAL ") {
            throw PaletteCoderException.InvalidFormat()
        }

        val dataHeader = parser.readStringASCII(4)
        parser.readInt32(ByteOrder.LITTLE_ENDIAN)
        if (dataHeader != "data") {
            throw PaletteCoderException.InvalidFormat()
        }

        parser.readInt16(ByteOrder.LITTLE_ENDIAN) // palVersion
        val palNumEntries = parser.readInt16(ByteOrder.LITTLE_ENDIAN)

        for (i in 0 until palNumEntries) {
            val rgb = parser.readData(4)
            val r = rgb[0].toUByte().toInt()
            val g = rgb[1].toUByte().toInt()
            val b = rgb[2].toUByte().toInt()
            // rgb[3] is unused

            val color = PaletteColor.rgb(
                r = r / 255.0,
                g = g / 255.0,
                b = b / 255.0
            )
            result.colors.add(color)
        }

        // Try to read color names from "name" chunk (non-standard extension)
        try {
            val nameHeader = parser.readInt32(ByteOrder.BIG_ENDIAN)
            if (nameHeader == 0x6E616D65) { // "name" in ASCII
                val nameChunkSize = parser.readInt32(ByteOrder.LITTLE_ENDIAN)
                val nameData = parser.readData(nameChunkSize)
                val nameText =
                    String(nameData, java.nio.charset.StandardCharsets.UTF_8).trimEnd('\u0000')
                val names = nameText.split("\n")
                names.forEachIndexed { index, name ->
                    if (index < result.colors.size && name.isNotEmpty()) {
                        result.colors[index] = result.colors[index].named(name)
                    }
                }
            } else {
                // Not a name chunk, reset position
                parser.seekSet((parser.readPosition - 4).toInt())
            }
        } catch (_: Throwable) {
            // No names chunk, continue without names
        }

        return result.build()
    }

    override fun encode(palette: Palette, output: OutputStream) {
        val writer = BytesWriter(output)
        val sourceColors = palette.allColors()
        val allColors = sourceColors.map { it.toRgb() }
        val colorCount = allColors.size

        val dataChunkSize = 2 + 2 + colorCount * 4
        val dataChunkTotalSize = 8 + dataChunkSize

        val names = sourceColors.map { it.name }
        val hasNames = names.any { it.isNotEmpty() }
        val nameBytes = if (hasNames) {
            names.joinToString("\n").toByteArray(java.nio.charset.StandardCharsets.UTF_8)
        } else {
            ByteArray(0)
        }
        val nameChunkPadding = if (hasNames && nameBytes.size % 2 != 0) 1 else 0
        val nameChunkTotalSize = if (hasNames) 8 + nameBytes.size + nameChunkPadding else 0
        val fileSizeMinus8 = 4 + dataChunkTotalSize + nameChunkTotalSize

        writer.writeInt32(0x52494646, ByteOrder.BIG_ENDIAN)
        writer.writeInt32(fileSizeMinus8, ByteOrder.LITTLE_ENDIAN)
        writer.writeInt32(0x50414C20, ByteOrder.BIG_ENDIAN)
        writer.writeInt32(0x64617461, ByteOrder.BIG_ENDIAN)
        writer.writeInt32(dataChunkSize, ByteOrder.LITTLE_ENDIAN)

        // palVersion (2 bytes, little endian) - typically 3
        writer.writeInt16(3, ByteOrder.LITTLE_ENDIAN)

        // palNumEntries (2 bytes, little endian)
        writer.writeInt16(colorCount.toShort(), ByteOrder.LITTLE_ENDIAN)

        // Write colors (4 bytes each: R, G, B, unused)
        allColors.forEach { rgb ->
            writer.writeByte((rgb.rf * 255).toInt().coerceIn(0, 255).toByte())
            writer.writeByte((rgb.gf * 255).toInt().coerceIn(0, 255).toByte())
            writer.writeByte((rgb.bf * 255).toInt().coerceIn(0, 255).toByte())
            writer.writeByte(0) // unused
        }

        if (hasNames) {
            writer.writeInt32(0x6E616D65, ByteOrder.BIG_ENDIAN)
            writer.writeInt32(nameBytes.size, ByteOrder.LITTLE_ENDIAN)
            writer.writeData(nameBytes)
            if (nameChunkPadding != 0) {
                writer.writeByte(0)
            }
        }
    }
}

