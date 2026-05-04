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

@file:Suppress("MagicNumber", "ReturnCount")

package com.t8rin.imagetoolbox.core.data.coil

import android.graphics.Bitmap
import android.graphics.Matrix
import coil3.ImageLoader
import coil3.asImage
import coil3.decode.BitmapFactoryDecoder
import coil3.decode.DecodeResult
import coil3.decode.Decoder
import coil3.decode.ImageSource
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import coil3.toBitmap
import okio.Buffer
import okio.BufferedSource
import okio.ByteString.Companion.toByteString
import java.io.File
import java.io.RandomAccessFile
import java.util.Locale

internal class NefDecoder private constructor(
    private val source: ImageSource,
    private val options: Options
) : Decoder {

    override suspend fun decode(): DecodeResult? {
        val preview = runCatching {
            NefPreviewExtractor(
                file = source.file().toFile()
            ).extract()
        }.getOrNull() ?: return null

        val result = BitmapFactoryDecoder(
            source = ImageSource(
                source = Buffer().write(preview.bytes),
                fileSystem = options.fileSystem
            ),
            options = options
        ).decode()

        return result.applyOrientation(preview.orientation)
    }

    private fun DecodeResult.applyOrientation(
        orientation: Int
    ): DecodeResult {
        if (orientation == ORIENTATION_NORMAL) return this

        val bitmap = image.toBitmap()
        val matrix = Matrix().applyTiffOrientation(orientation) ?: return this
        val rotated = Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )

        if (rotated != bitmap) bitmap.recycle()

        return DecodeResult(
            image = rotated.asImage(),
            isSampled = isSampled
        )
    }

    private fun Matrix.applyTiffOrientation(
        orientation: Int
    ): Matrix? {
        val transformation = TIFF_ORIENTATION_TRANSFORMS[orientation] ?: return null
        transformation(this)
        return this
    }

    class Factory : Decoder.Factory {

        override fun create(
            result: SourceFetchResult,
            options: Options,
            imageLoader: ImageLoader
        ): Decoder? {
            return if (isTiff(result.source.source())) {
                NefDecoder(
                    source = result.source,
                    options = options
                )
            } else {
                null
            }
        }

        private fun isTiff(source: BufferedSource): Boolean {
            val littleEndianMagic = byteArrayOf(0x49, 0x49, 0x2a, 0x00)
            val bigEndianMagic = byteArrayOf(0x4d, 0x4d, 0x00, 0x2a)

            return source.rangeEquals(0, littleEndianMagic.toByteString()) ||
                    source.rangeEquals(0, bigEndianMagic.toByteString())
        }
    }

    private class NefPreviewExtractor(
        private val file: File
    ) {
        fun extract(): PreviewData? = RandomAccessFile(file, "r").use { raf ->
            val reader = TiffReader.create(raf) ?: return null
            val rootDirectory = reader.readDirectory(reader.firstDirectoryOffset) ?: return null
            val make = rootDirectory.asciiValue(TAG_MAKE, reader)
                ?.uppercase(Locale.ROOT)
                .orEmpty()

            if (!make.contains("NIKON")) return null

            val subIfdOffsets = rootDirectory.longValues(TAG_SUB_IFD, reader)
            if (subIfdOffsets.isEmpty()) return null

            val subIfds = subIfdOffsets.mapNotNull(reader::readDirectory)
            if (!subIfds.any { it.isNikonRawImage(reader) }) return null

            val preview = subIfds
                .mapNotNull { it.jpegPreview(reader) }
                .filter(reader::isJpeg)
                .maxByOrNull(JpegPreview::length)
                ?: return null

            val bytes = reader.readBytes(
                offset = preview.offset,
                byteCount = preview.length
            ) ?: return null

            PreviewData(
                bytes = bytes,
                orientation = rootDirectory.longValue(TAG_ORIENTATION, reader)?.toInt()
                    ?: ORIENTATION_NORMAL
            )
        }
    }

    private class TiffReader private constructor(
        private val raf: RandomAccessFile,
        private val byteOrder: ByteOrder,
        val firstDirectoryOffset: Long
    ) {

        fun readDirectory(offset: Long): TiffDirectory? {
            if (offset <= 0 || offset >= raf.length()) return null

            raf.seek(offset)
            val entryCount = readUShort()
            if (entryCount > MAX_IFD_ENTRIES) return null

            val entries = buildMap {
                repeat(entryCount) {
                    val bytes = ByteArray(IFD_ENTRY_SIZE)
                    raf.readFully(bytes)

                    val entry = TiffEntry(
                        tag = readUShort(bytes, offset = 0),
                        type = readUShort(bytes, offset = 2),
                        count = readUInt(bytes, offset = 4),
                        valueOrOffset = readUInt(bytes, offset = 8),
                        inlineValue = bytes.copyOfRange(8, 12)
                    )
                    put(entry.tag, entry)
                }
            }

            return TiffDirectory(entries)
        }

        fun values(entry: TiffEntry): ByteArray? {
            val typeSize = TYPE_SIZES[entry.type] ?: return null
            val byteCount = entry.count * typeSize
            if (byteCount <= 0 || byteCount > Int.MAX_VALUE) return null

            if (byteCount <= INLINE_VALUE_SIZE) {
                return entry.inlineValue.copyOf(byteCount.toInt())
            }

            return readBytes(
                offset = entry.valueOrOffset,
                byteCount = byteCount
            )
        }

        fun readBytes(
            offset: Long,
            byteCount: Long
        ): ByteArray? {
            if (offset < 0 || byteCount <= 0 || byteCount > Int.MAX_VALUE) return null
            if (offset + byteCount > raf.length()) return null

            val bytes = ByteArray(byteCount.toInt())
            raf.seek(offset)
            raf.readFully(bytes)
            return bytes
        }

        fun isJpeg(preview: JpegPreview): Boolean {
            val bytes = readBytes(
                offset = preview.offset,
                byteCount = JPEG_SIGNATURE_SIZE
            ) ?: return false

            return bytes[0] == JPEG_START_BYTE_0 && bytes[1] == JPEG_START_BYTE_1
        }

        fun readUShort(bytes: ByteArray, offset: Int): Int {
            val first = bytes[offset].toInt() and 0xff
            val second = bytes[offset + 1].toInt() and 0xff

            return when (byteOrder) {
                ByteOrder.LittleEndian -> first or (second shl 8)
                ByteOrder.BigEndian -> (first shl 8) or second
            }
        }

        fun readUInt(bytes: ByteArray, offset: Int): Long {
            val first = bytes[offset].toLong() and 0xff
            val second = bytes[offset + 1].toLong() and 0xff
            val third = bytes[offset + 2].toLong() and 0xff
            val fourth = bytes[offset + 3].toLong() and 0xff

            return when (byteOrder) {
                ByteOrder.LittleEndian -> first or (second shl 8) or (third shl 16) or (fourth shl 24)
                ByteOrder.BigEndian -> (first shl 24) or (second shl 16) or (third shl 8) or fourth
            }
        }

        private fun readUShort(): Int {
            val bytes = ByteArray(2)
            raf.readFully(bytes)
            return readUShort(bytes, offset = 0)
        }

        companion object {

            fun create(raf: RandomAccessFile): TiffReader? {
                if (raf.length() < TIFF_HEADER_SIZE) return null

                raf.seek(0)
                val header = ByteArray(TIFF_HEADER_SIZE)
                raf.readFully(header)

                val byteOrder = when {
                    header[0] == LITTLE_ENDIAN_BYTE && header[1] == LITTLE_ENDIAN_BYTE -> {
                        ByteOrder.LittleEndian
                    }

                    header[0] == BIG_ENDIAN_BYTE && header[1] == BIG_ENDIAN_BYTE -> {
                        ByteOrder.BigEndian
                    }

                    else -> return null
                }

                val reader = TiffReader(
                    raf = raf,
                    byteOrder = byteOrder,
                    firstDirectoryOffset = 0
                )
                val magic = reader.readUShort(header, offset = 2)
                if (magic != TIFF_MAGIC) return null

                return TiffReader(
                    raf = raf,
                    byteOrder = byteOrder,
                    firstDirectoryOffset = reader.readUInt(header, offset = 4)
                )
            }
        }
    }

    private data class TiffDirectory(
        private val entries: Map<Int, TiffEntry>
    ) {

        fun longValue(
            tag: Int,
            reader: TiffReader
        ): Long? = longValues(tag, reader).firstOrNull()

        fun longValues(
            tag: Int,
            reader: TiffReader
        ): List<Long> {
            val entry = entries[tag] ?: return emptyList()
            val bytes = reader.values(entry) ?: return emptyList()
            val count = entry.count.toIntOrNull() ?: return emptyList()

            return when (entry.type) {
                TYPE_BYTE -> List(count) { index ->
                    bytes[index].toLong() and 0xff
                }

                TYPE_SHORT -> List(count) { index ->
                    reader.readUShort(bytes, index * SHORT_SIZE).toLong()
                }

                TYPE_LONG -> List(count) { index ->
                    reader.readUInt(bytes, index * LONG_SIZE)
                }

                else -> emptyList()
            }
        }

        fun asciiValue(
            tag: Int,
            reader: TiffReader
        ): String? {
            val entry = entries[tag] ?: return null
            if (entry.type != TYPE_ASCII) return null

            val bytes = reader.values(entry) ?: return null
            return String(bytes, Charsets.US_ASCII).substringBefore(NULL_CHAR)
        }

        fun isNikonRawImage(reader: TiffReader): Boolean {
            val compression = longValue(TAG_COMPRESSION, reader)
            val photometric = longValue(TAG_PHOTOMETRIC, reader)
            val bitsPerSample = longValue(TAG_BITS_PER_SAMPLE, reader)

            return compression == COMPRESSION_NIKON_RAW ||
                    (photometric == PHOTOMETRIC_CFA && bitsPerSample != null && bitsPerSample > 8)
        }

        fun jpegPreview(reader: TiffReader): JpegPreview? {
            val offset = longValue(TAG_JPEG_INTERCHANGE_FORMAT, reader) ?: return null
            val length = longValue(TAG_JPEG_INTERCHANGE_FORMAT_LENGTH, reader) ?: return null

            if (length <= 0) return null
            return JpegPreview(
                offset = offset,
                length = length
            )
        }
    }

    private class TiffEntry(
        val tag: Int,
        val type: Int,
        val count: Long,
        val valueOrOffset: Long,
        val inlineValue: ByteArray
    )

    private data class JpegPreview(
        val offset: Long,
        val length: Long
    )

    private class PreviewData(
        val bytes: ByteArray,
        val orientation: Int
    )

    private enum class ByteOrder {
        LittleEndian,
        BigEndian
    }

    private companion object {
        private const val TIFF_HEADER_SIZE = 8
        private const val TIFF_MAGIC = 42
        private const val IFD_ENTRY_SIZE = 12
        private const val INLINE_VALUE_SIZE = 4
        private const val MAX_IFD_ENTRIES = 512
        private const val SHORT_SIZE = 2
        private const val LONG_SIZE = 4
        private const val JPEG_SIGNATURE_SIZE = 2L

        private const val LITTLE_ENDIAN_BYTE = 0x49.toByte()
        private const val BIG_ENDIAN_BYTE = 0x4d.toByte()
        private const val JPEG_START_BYTE_0 = 0xff.toByte()
        private const val JPEG_START_BYTE_1 = 0xd8.toByte()
        private const val NULL_CHAR = '\u0000'

        private const val TYPE_BYTE = 1
        private const val TYPE_ASCII = 2
        private const val TYPE_SHORT = 3
        private const val TYPE_LONG = 4

        private const val TAG_SUB_IFD = 330
        private const val TAG_MAKE = 271
        private const val TAG_ORIENTATION = 274
        private const val TAG_BITS_PER_SAMPLE = 258
        private const val TAG_COMPRESSION = 259
        private const val TAG_PHOTOMETRIC = 262
        private const val TAG_JPEG_INTERCHANGE_FORMAT = 513
        private const val TAG_JPEG_INTERCHANGE_FORMAT_LENGTH = 514

        private const val COMPRESSION_NIKON_RAW = 34713L
        private const val PHOTOMETRIC_CFA = 32803L

        private const val ORIENTATION_NORMAL = 1
        private const val ORIENTATION_FLIP_HORIZONTAL = 2
        private const val ORIENTATION_ROTATE_180 = 3
        private const val ORIENTATION_FLIP_VERTICAL = 4
        private const val ORIENTATION_TRANSPOSE = 5
        private const val ORIENTATION_ROTATE_90 = 6
        private const val ORIENTATION_TRANSVERSE = 7
        private const val ORIENTATION_ROTATE_270 = 8

        private val TIFF_ORIENTATION_TRANSFORMS = mapOf<Int, Matrix.() -> Unit>(
            ORIENTATION_FLIP_HORIZONTAL to {
                postScale(-1f, 1f)
            },
            ORIENTATION_ROTATE_180 to {
                postRotate(180f)
            },
            ORIENTATION_FLIP_VERTICAL to {
                postScale(1f, -1f)
            },
            ORIENTATION_TRANSPOSE to {
                postRotate(90f)
                postScale(1f, -1f)
            },
            ORIENTATION_ROTATE_90 to {
                postRotate(90f)
            },
            ORIENTATION_TRANSVERSE to {
                postRotate(90f)
                postScale(-1f, 1f)
            },
            ORIENTATION_ROTATE_270 to {
                postRotate(270f)
            }
        )

        private val TYPE_SIZES = mapOf(
            TYPE_BYTE to 1L,
            TYPE_ASCII to 1L,
            TYPE_SHORT to SHORT_SIZE.toLong(),
            TYPE_LONG to LONG_SIZE.toLong()
        )

        private fun Long.toIntOrNull(): Int? = takeIf {
            it in 0..Int.MAX_VALUE.toLong()
        }?.toInt()
    }
}
