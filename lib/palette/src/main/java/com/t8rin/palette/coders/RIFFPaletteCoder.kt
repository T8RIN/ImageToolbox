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

        // Check header 'RIFF'
        val header = parser.readInt32(ByteOrder.BIG_ENDIAN)
        if (header != 0x52494646) {
            throw PaletteCoderException.InvalidFormat()
        }

        // Skip some header
        parser.readInt32(ByteOrder.BIG_ENDIAN)

        // Check RIFF type 'PAL '
        val riffType = parser.readInt32(ByteOrder.BIG_ENDIAN)
        if (riffType != 0x50414C20) {
            throw PaletteCoderException.InvalidFormat()
        }

        // Data header 'data'
        val dataHeader = parser.readInt32(ByteOrder.BIG_ENDIAN)
        parser.readInt32(ByteOrder.LITTLE_ENDIAN) // chunkSize
        if (dataHeader != 0x64617461) {
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
        val allColors = palette.allColors().map { it.toRgb() }
        val colorCount = allColors.size

        // RIFF header 'RIFF' (4 bytes, big endian)
        writer.writeInt32(0x52494646, ByteOrder.BIG_ENDIAN)

        // Calculate sizes
        // Data chunk: 4 (data header) + 4 (chunk size) + 2 (version) + 2 (num entries) + colorCount * 4
        val dataChunkSize = 4 + 4 + 2 + 2 + colorCount * 4
        // RIFF chunk size: 4 (RIFF type) + dataChunkSize
        val riffChunkSize = 4 + dataChunkSize
        // File size minus 8
        val fileSizeMinus8 = riffChunkSize

        // File size minus 8 (4 bytes, big endian)
        writer.writeInt32(fileSizeMinus8, ByteOrder.BIG_ENDIAN)

        // RIFF type 'PAL ' (4 bytes, big endian)
        writer.writeInt32(0x50414C20, ByteOrder.BIG_ENDIAN)

        // Data header 'data' (4 bytes, big endian)
        writer.writeInt32(0x64617461, ByteOrder.BIG_ENDIAN)

        // Chunk size (4 bytes, little endian) - size of data after this field
        val chunkSize = 2 + 2 + colorCount * 4
        writer.writeInt32(chunkSize, ByteOrder.LITTLE_ENDIAN)

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

        // Add non-standard "name" chunk with color names
        val names = palette.allColors().mapNotNull { if (it.name.isNotEmpty()) it.name else null }
        if (names.isNotEmpty()) {
            val nameText = names.joinToString("\n")
            val nameBytes = nameText.toByteArray(java.nio.charset.StandardCharsets.UTF_8)

            // "name" chunk header (4 bytes, big endian)
            writer.writeInt32(0x6E616D65, ByteOrder.BIG_ENDIAN)

            // Chunk size (4 bytes, little endian)
            writer.writeInt32(nameBytes.size, ByteOrder.LITTLE_ENDIAN)

            // Name data
            writer.writeData(nameBytes)

            // Pad to even boundary if needed
            if (nameBytes.size % 2 != 0) {
                writer.writeByte(0)
            }
        }
    }
}


