/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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
        val bitmap = if (image.width > 256 || image.height > 256) {
            imageScaler.scaleImage(
                image = image,
                width = 256,
                height = 256,
                resizeType = ResizeType.Flexible
            )
        } else image

        val width = bitmap.width
        val height = bitmap.height

        val outputStream = ByteArrayOutputStream()
        val header = ByteArray(6)
        val entry = ByteArray(16)
        val infoHeader = ByteArray(40)

        // ICO Header
        header[2] = 1 // Image type: Icon
        header[4] = 1 // Number of images

        outputStream.write(header)

        // Image entry
        entry[0] = if (width > 256) 0 else width.toByte()
        entry[1] = if (height > 256) 0 else height.toByte()
        entry[4] = 1 // Color planes
        entry[6] = 32 // Bits per pixel

        val andMaskSize = ((width + 31) / 32) * 4 * height
        val xorMaskSize = width * height * 4
        val imageDataSize = infoHeader.size + xorMaskSize + andMaskSize

        ByteBuffer.wrap(entry, 8, 4).order(ByteOrder.LITTLE_ENDIAN).putInt(imageDataSize)
        ByteBuffer.wrap(entry, 12, 4)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putInt(header.size + entry.size)

        outputStream.write(entry)

        // BITMAP INFO HEADER
        ByteBuffer.wrap(infoHeader).order(ByteOrder.LITTLE_ENDIAN).apply {
            putInt(40) // Header size
            putInt(width) // Width
            putInt(height * 2) // Height (XOR + AND masks)
            putShort(1) // Color planes
            putShort(32) // Bits per pixel
            putInt(0) // Compression (BI_RGB)
            putInt(xorMaskSize + andMaskSize) // Image size
        }

        outputStream.write(infoHeader)

        // XOR mask (pixel data)
        for (y in height - 1 downTo 0) {
            for (x in 0 until width) {
                val pixel = bitmap[x, y]
                outputStream.write(pixel and 0xFF) // B
                outputStream.write((pixel shr 8) and 0xFF) // G
                outputStream.write((pixel shr 16) and 0xFF) // R
                outputStream.write((pixel shr 24) and 0xFF) // A
            }
        }

        // AND mask (all 0 for no transparency mask)
        outputStream.write(ByteArray(andMaskSize))

        outputStream.toByteArray()
    }

}