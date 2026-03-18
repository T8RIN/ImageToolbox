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
 * Affinity Designer Palette (AFPalette) coder — более терпимый парсер:
 * - пытается определить BOM в разных эндианах
 * - вместо жёсткой ошибки ищет маркеры в потоке
 * - при проблемах возвращает корректную ошибку
 */
class AFPaletteCoder : PaletteCoder {
    override fun decode(input: InputStream): Palette {
        val allData = input.readBytes()
        val parser = BytesReader(allData)
        val result = Palette.Builder()

        var hasUnsupportedColorType = false

        // Helper: try both byte orders for a 4-byte ASCII marker
        fun findMarkerAnyOrder(markerLE: ByteArray, markerBE: ByteArray): Int {
            // findPattern returns index or -1
            val idx1 = parser.findPattern(markerLE)
            if (idx1 >= 0) return idx1
            val idx2 = parser.findPattern(markerBE)
            return idx2
        }

        // MARK: BOM / NClP
        try {
            // try BOM little-endian first
            val startPos = parser.readPosition.toInt()
            try {
                val bom = parser.readUInt32(ByteOrder.LITTLE_ENDIAN).toInt()
                if (bom != 0x414BFF00 && bom != 0x00FF4B41) {
                    // not recognized -> fallthrough to searching markers
                    parser.seekSet(startPos)
                    throw Throwable("not bom")
                }
                // ok — BOM accepted, continue from current position
            } catch (_: Throwable) {
                // try to find NClP marker in data (accept both byte orders)
                parser.seekSet(0)
                val nclpLE = byteArrayOf(0x4E, 0x43, 0x6C, 0x50) // 'NClP'
                val nclpBE = byteArrayOf(0x50, 0x6C, 0x43, 0x4E) // reversed form sometimes used
                val found = findMarkerAnyOrder(nclpLE, nclpBE)
                if (found < 0) throw PaletteCoderException.InvalidBOM()
                // position should be just after marker
                parser.seekSet(found + 4)
            }
        } catch (e: PaletteCoderException) {
            throw e
        } catch (_: Throwable) {
            throw PaletteCoderException.InvalidBOM()
        }

        // Now we expect NClP somewhere after current position.
        // Ensure we're positioned right after an NClP-like marker.
        try {
            // If current bytes are not NClP, try to locate it from current pos
            runCatching {
                parser.readUInt32(ByteOrder.LITTLE_ENDIAN)
                    .toInt()
                parser.seekSet((parser.readPosition - 4).toInt())
            }
            // Simpler: just try to find marker from current pos
            try {
                parser.seekToNextInstanceOfPattern(0x4E, 0x43, 0x6C, 0x50) // 'NClP'
                // move cursor to just after marker
                parser.seek(4)
            } catch (_: Throwable) {
                // try reversed marker
                try {
                    parser.seekToNextInstanceOfPattern(0x50, 0x6C, 0x43, 0x4E)
                    parser.seek(4)
                } catch (_: Throwable) {
                    // if we can't find it, it's invalid
                    throw PaletteCoderException.InvalidFormat()
                }
            }
        } catch (e: PaletteCoderException) {
            throw e
        }

        // Filename length (uint32 little-endian) + name ASCII
        val filenameLen = try {
            parser.readUInt32(ByteOrder.LITTLE_ENDIAN).toInt()
        } catch (_: Throwable) {
            throw PaletteCoderException.InvalidFormat()
        }
        val filename = try {
            if (filenameLen <= 0) "" else parser.readStringASCII(filenameLen)
        } catch (_: Throwable) {
            ""
        }
        result.name = filename

        // Найти VlaP (pattern can be in two byte orders)
        try {
            try {
                parser.seekToNextInstanceOfPattern(0x56, 0x6C, 0x61, 0x50) // maybe reversed
            } catch (_: Throwable) {
                parser.seekToNextInstanceOfPattern(0x50, 0x61, 0x6C, 0x56) // other order
            }
            // position is at start of marker; move after it
            parser.seek(4)
        } catch (_: Throwable) {
            throw PaletteCoderException.InvalidFormat()
        }

        val colorCount = try {
            parser.readUInt32(ByteOrder.LITTLE_ENDIAN).toInt()
        } catch (_: Throwable) {
            throw PaletteCoderException.InvalidFormat()
        }

        val colors = mutableListOf<PaletteColor>()

        for (index in 0 until colorCount) {
            index + 1
            try {
                // Find "rloC" marker (either ASCII or bytes), then set cursor to start
                try {
                    parser.seekToNextInstanceOfASCII("rloC")
                } catch (_: Throwable) {
                    // try bytes variant
                    try {
                        parser.seekToNextInstanceOfPattern(0x72, 0x6C, 0x6F, 0x43)
                    } catch (_: Throwable) {
                        // couldn't find marker for this color — break/handle
                        if (colors.isEmpty()) throw PaletteCoderException.InvalidFormat()
                        break
                    }
                }
                // We're at start of "rloC"
                // advance past marker
                parser.seek(4)

                // Safe skip 6 bytes if available (unknown data)
                parser.trySkipBytes(6)

                // Read color type (4 ASCII chars)
                val colorType = try {
                    parser.readStringASCII(4)
                } catch (_: Throwable) {
                    // cannot read type -> break
                    if (colors.isEmpty()) throw PaletteCoderException.InvalidFormat()
                    break
                }

                when (colorType) {
                    "ABGR" -> {
                        // Find "Dloc_" (marker) - accept ASCII search
                        try {
                            parser.seekToNextInstanceOfASCII("Dloc_")
                            parser.seek(5) // move after 'Dloc_'
                        } catch (_: Throwable) {
                            // if not found, continue — maybe values are right here
                        }
                        val r = parser.readFloat32(ByteOrder.LITTLE_ENDIAN).toDouble()
                        val g = parser.readFloat32(ByteOrder.LITTLE_ENDIAN).toDouble()
                        val b = parser.readFloat32(ByteOrder.LITTLE_ENDIAN).toDouble()
                        colors.add(PaletteColor.rgb(r = r, g = g, b = b))
                    }

                    "ABAL" -> {
                        try {
                            parser.seekToNextInstanceOfASCII("<loc_")
                            parser.seek(5)
                        } catch (_: Throwable) {
                            // continue even if marker absent
                        }
                        val l = parser.readUInt16(ByteOrder.LITTLE_ENDIAN).toInt()
                        val a = parser.readUInt16(ByteOrder.LITTLE_ENDIAN).toInt()
                        val b = parser.readUInt16(ByteOrder.LITTLE_ENDIAN).toInt()
                        val lab = PaletteColor.lab(
                            l = l / 65535.0 * 100.0,
                            a = a / 65535.0 * 256.0 - 128.0,
                            b = b / 65535.0 * 256.0 - 128.0
                        )
                        colors.add(lab.converted(ColorSpace.RGB))
                    }

                    "KYMC" -> {
                        try {
                            parser.seekToNextInstanceOfASCII("Hloc_"); parser.seek(5)
                        } catch (_: Throwable) {
                        }
                        val c = parser.readFloat32(ByteOrder.LITTLE_ENDIAN).toDouble()
                        val m = parser.readFloat32(ByteOrder.LITTLE_ENDIAN).toDouble()
                        val y = parser.readFloat32(ByteOrder.LITTLE_ENDIAN).toDouble()
                        val k = parser.readFloat32(ByteOrder.LITTLE_ENDIAN).toDouble()
                        colors.add(PaletteColor.cmyk(c = c, m = m, y = y, k = k))
                    }

                    "ALSH" -> {
                        try {
                            parser.seekToNextInstanceOfASCII("Dloc_"); parser.seek(5)
                        } catch (_: Throwable) {
                        }
                        val h = parser.readFloat32(ByteOrder.LITTLE_ENDIAN).toDouble()
                        val s = parser.readFloat32(ByteOrder.LITTLE_ENDIAN).toDouble()
                        val l = parser.readFloat32(ByteOrder.LITTLE_ENDIAN).toDouble()
                        colors.add(PaletteColor.hsl(hf = h, sf = s, lf = l))
                    }

                    "YARG" -> {
                        try {
                            parser.seekToNextInstanceOfASCII("<loc_"); parser.seek(5)
                        } catch (_: Throwable) {
                        }
                        val g1 = parser.readFloat32(ByteOrder.LITTLE_ENDIAN).toDouble()
                        colors.add(PaletteColor.white(white = g1))
                    }

                    else -> {
                        hasUnsupportedColorType = true
                        throw PaletteCoderException.CannotCreateColor()
                    }
                }
            } catch (_: PaletteCoderException) {
                if (hasUnsupportedColorType) throw PaletteCoderException.UnsupportedPaletteType()
                if (colors.isEmpty()) throw PaletteCoderException.InvalidFormat()
                break
            } catch (_: Throwable) {
                if (colors.isEmpty()) throw PaletteCoderException.InvalidFormat()
                break
            }
        }

        // Read names section (VNaP) if present
        try {
            // try finding either order of VNaP
            val vnapIdx = parser.findPattern(byteArrayOf(0x56, 0x4e, 0x61, 0x50)).takeIf { it >= 0 }
                ?: parser.findPattern(byteArrayOf(0x50, 0x61, 0x4E, 0x56)).takeIf { it >= 0 }
            if (vnapIdx != null) {
                parser.seekSet(vnapIdx + 4)
                // unknown offset
                parser.readUInt32(ByteOrder.LITTLE_ENDIAN)
                val nameCount = parser.readUInt32(ByteOrder.LITTLE_ENDIAN).toInt()
                for (i in 0 until minOf(nameCount, colors.size)) {
                    try {
                        val colorNameLen = parser.readUInt32(ByteOrder.LITTLE_ENDIAN).toInt()
                        val colorName = parser.readStringUTF8(colorNameLen)
                        colors[i] = colors[i].copy(
                            name = colorName
                        )
                    } catch (_: Throwable) {
                        break
                    }
                }
            }
        } catch (_: Throwable) {
            // ignore — names optional
        }

        if (colors.isEmpty()) throw PaletteCoderException.InvalidFormat()
        result.colors = colors
        return result.build()
    }

    override fun encode(palette: Palette, output: OutputStream) {
        val writer = BytesWriter(output)
        val allColors = palette.allColors()
        val colorCount = allColors.size
        if (colorCount == 0) throw PaletteCoderException.TooFewColors()

        // BOM (как раньше), версия, и т.д.
        writer.writeUInt32(0x414BFF00u, ByteOrder.LITTLE_ENDIAN)
        writer.writeUInt32(11u, ByteOrder.LITTLE_ENDIAN)
        writer.writePattern(0x50, 0x6C, 0x43, 0x4E) // NClP
        val filename = palette.name.ifEmpty { "Palette" }
        writer.writeStringASCIIWithLength(filename, ByteOrder.LITTLE_ENDIAN)
        writer.writePattern(0x50, 0x61, 0x6C, 0x56) // VlaP
        writer.writeUInt32(colorCount.toUInt(), ByteOrder.LITTLE_ENDIAN)

        allColors.forEach { color ->
            writer.writePattern(0x72, 0x6C, 0x6F, 0x43) // rloC
            writer.writePattern(0x00, 0x00, 0x00, 0x00, 0x00, 0x00) // skip 6
            writer.writePattern(0x41, 0x42, 0x47, 0x52) // ABGR
            writer.writePattern(0x44, 0x6C, 0x6F, 0x63, 0x5F) // Dloc_
            val rgb = color.toRgb()
            writer.writeFloat32(rgb.rf.toFloat(), ByteOrder.LITTLE_ENDIAN)
            writer.writeFloat32(rgb.gf.toFloat(), ByteOrder.LITTLE_ENDIAN)
            writer.writeFloat32(rgb.bf.toFloat(), ByteOrder.LITTLE_ENDIAN)
        }

        writer.writePattern(0x50, 0x61, 0x4E, 0x56) // VNaP
        writer.writeUInt32(0u, ByteOrder.LITTLE_ENDIAN)
        writer.writeUInt32(colorCount.toUInt(), ByteOrder.LITTLE_ENDIAN)
        allColors.forEach { color ->
            val colorName = color.name.ifEmpty { "Color" }
            writer.writeStringUTF8WithLength(colorName, ByteOrder.LITTLE_ENDIAN)
        }
    }
}