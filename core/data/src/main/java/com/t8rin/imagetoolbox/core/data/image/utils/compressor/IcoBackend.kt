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

package com.t8rin.imagetoolbox.core.data.image.utils.compressor

import android.graphics.Bitmap
import androidx.core.graphics.get
import com.t8rin.imagetoolbox.core.data.image.utils.ImageCompressorBackend
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.image.model.ResizeType
import kotlinx.coroutines.coroutineScope
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

internal data class IcoBackend(
    private val imageScaler: ImageScaler<Bitmap>
) : ImageCompressorBackend {

    override suspend fun compress(
        image: Bitmap,
        quality: Quality
    ): ByteArray = coroutineScope {
        val images = ICON_SIZES.map { size ->
            val bitmap = imageScaler.scaleImage(
                image = image,
                width = size,
                height = size,
                resizeType = ResizeType.Fit(canvasColor = 0)
            )
            try {
                IconImage(
                    size = size,
                    data = bitmap.toIconBitmapData()
                )
            } finally {
                if (bitmap !== image) bitmap.recycle()
            }
        }

        ByteArrayOutputStream().apply {
            write(ByteArray(ICO_HEADER_SIZE).apply {
                this[2] = 1
                ByteBuffer.wrap(this, 4, 2)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .putShort(images.size.toShort())
            })

            var dataOffset = ICO_HEADER_SIZE + images.size * DIRECTORY_ENTRY_SIZE
            images.forEach { icon ->
                write(ByteArray(DIRECTORY_ENTRY_SIZE).apply {
                    this[0] = icon.size.toIcoDimension()
                    this[1] = icon.size.toIcoDimension()
                    this[4] = 1
                    this[6] = 32
                    ByteBuffer.wrap(this, 8, 4)
                        .order(ByteOrder.LITTLE_ENDIAN)
                        .putInt(icon.data.size)
                    ByteBuffer.wrap(this, 12, 4)
                        .order(ByteOrder.LITTLE_ENDIAN)
                        .putInt(dataOffset)
                })
                dataOffset += icon.data.size
            }
            images.forEach { write(it.data) }
        }.toByteArray()
    }

    private fun Bitmap.toIconBitmapData(): ByteArray {
        val andMaskSize = ((width + 31) / 32) * 4 * height
        val xorMaskSize = width * height * 4
        val infoHeader = ByteArray(BITMAP_INFO_HEADER_SIZE)

        ByteBuffer.wrap(infoHeader).order(ByteOrder.LITTLE_ENDIAN).apply {
            putInt(BITMAP_INFO_HEADER_SIZE)
            putInt(width)
            putInt(height * 2)
            putShort(1)
            putShort(32)
            putInt(0)
            putInt(xorMaskSize + andMaskSize)
        }

        return ByteArrayOutputStream().apply {
            write(infoHeader)
            for (y in height - 1 downTo 0) {
                for (x in 0 until width) {
                    val pixel = this@toIconBitmapData[x, y]
                    write(pixel and 0xFF)
                    write((pixel shr 8) and 0xFF)
                    write((pixel shr 16) and 0xFF)
                    write((pixel shr 24) and 0xFF)
                }
            }
            write(ByteArray(andMaskSize))
        }.toByteArray()
    }

    private class IconImage(
        val size: Int,
        val data: ByteArray
    )

    private fun Int.toIcoDimension(): Byte = takeUnless { it == 256 }?.toByte() ?: 0

    private companion object {
        val ICON_SIZES = listOf(16, 32, 48, 64, 128, 256)
        const val ICO_HEADER_SIZE = 6
        const val DIRECTORY_ENTRY_SIZE = 16
        const val BITMAP_INFO_HEADER_SIZE = 40
    }
}