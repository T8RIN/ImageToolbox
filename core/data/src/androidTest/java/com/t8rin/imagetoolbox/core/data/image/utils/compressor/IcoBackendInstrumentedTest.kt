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
import android.graphics.Color
import androidx.core.graphics.createBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.image.model.ImageScaleMode
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.image.model.ResizeType
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.nio.ByteBuffer
import java.nio.ByteOrder

@RunWith(AndroidJUnit4::class)
class IcoBackendInstrumentedTest {

    @Test
    fun icoContainsStandardFaviconSizes() = runBlocking {
        val source = createBitmap(320, 180).apply {
            eraseColor(Color.MAGENTA)
        }

        val encoded = IcoBackend(TestImageScaler()).compress(source, Quality.Base())
        val buffer = ByteBuffer.wrap(encoded).order(ByteOrder.LITTLE_ENDIAN)

        assertEquals(0, buffer.getShort(0).toInt())
        assertEquals(1, buffer.getShort(2).toInt())
        assertEquals(6, buffer.getShort(4).toInt())

        val sizes = IntArray(6)
        var previousEnd = 6 + 6 * 16
        repeat(6) { index ->
            val entryOffset = 6 + index * 16
            val width = encoded[entryOffset].toInt().and(0xFF).let { if (it == 0) 256 else it }
            val height = encoded[entryOffset + 1].toInt().and(0xFF).let { if (it == 0) 256 else it }
            val dataSize = buffer.getInt(entryOffset + 8)
            val dataOffset = buffer.getInt(entryOffset + 12)

            sizes[index] = width
            assertEquals(width, height)
            assertEquals(previousEnd, dataOffset)
            assertEquals(40 + width * width * 4 + ((width + 31) / 32) * 4 * width, dataSize)
            assertEquals(width, buffer.getInt(dataOffset + 4))
            assertEquals(width * 2, buffer.getInt(dataOffset + 8))
            previousEnd = dataOffset + dataSize
        }

        assertArrayEquals(intArrayOf(16, 32, 48, 64, 128, 256), sizes)
        assertEquals(encoded.size, previousEnd)
    }

    private class TestImageScaler : ImageScaler<Bitmap> {
        override suspend fun scaleImage(
            image: Bitmap,
            width: Int,
            height: Int,
            resizeType: ResizeType,
            imageScaleMode: ImageScaleMode
        ): Bitmap = Bitmap.createScaledBitmap(image, width, height, true)

        override suspend fun scaleUntilCanShow(image: Bitmap?): Bitmap? = image
    }
}
