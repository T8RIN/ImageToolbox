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

package com.t8rin.imagetoolbox.core.data.coil

import androidx.test.platform.app.InstrumentationRegistry
import coil3.decode.ImageSource
import coil3.request.Options
import coil3.size.Scale
import coil3.size.Size
import coil3.svg.SvgDecoder
import coil3.toBitmap
import com.hashsequence.coilresvg.ResvgDecoder
import kotlinx.coroutines.runBlocking
import okio.Buffer
import okio.FileSystem
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SvgDecoderCompatTest {

    @Test
    fun streamDecodePreservesPixelsAndFallbackWithoutCreatingATemporaryFile() = runBlocking {
        val svg = """
            <svg xmlns="http://www.w3.org/2000/svg" width="128" height="64">
                <rect width="64" height="64" fill="red"/>
                <rect x="64" width="64" height="64" fill="blue"/>
            </svg>
        """.trimIndent()
        val options = Options(
            context = InstrumentationRegistry.getInstrumentation().targetContext,
            size = Size(32, 32),
            scale = Scale.FIT
        )

        fun source() = ImageSource(Buffer().writeUtf8(svg), FileSystem.SYSTEM)

        source().use { original ->
            val expected = source().use { ResvgDecoder(it, options).decode().image.toBitmap() }
            val actual = SvgDecoderCompat(original, options).decode().image.toBitmap()
            try {
                assertEquals(expected.width, actual.width)
                assertEquals(expected.height, actual.height)
                val expectedPixels = IntArray(expected.width * expected.height)
                val actualPixels = IntArray(actual.width * actual.height)
                expected.getPixels(
                    expectedPixels,
                    0,
                    expected.width,
                    0,
                    0,
                    expected.width,
                    expected.height
                )
                actual.getPixels(actualPixels, 0, actual.width, 0, 0, actual.width, actual.height)
                assertArrayEquals(expectedPixels, actualPixels)
                assertNull(original.fileOrNull())

                // A failed native decoder must leave the full stream available to the fallback.
                ImageSource(original.source().peek(), original.fileSystem).use {
                    val fallback = SvgDecoder(it, options).decode().image.toBitmap()
                    try {
                        assertEquals(32, fallback.width)
                        assertEquals(16, fallback.height)
                        assertEquals(android.graphics.Color.RED, fallback.getPixel(4, 4))
                        assertEquals(android.graphics.Color.BLUE, fallback.getPixel(28, 4))
                    } finally {
                        fallback.recycle()
                    }
                }
                assertEquals(svg, original.source().readUtf8())
            } finally {
                expected.recycle()
                actual.recycle()
            }
        }
    }
}