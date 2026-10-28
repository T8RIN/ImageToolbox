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
import android.graphics.Gainmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import com.awxkee.jxlcoder.JxlCoder
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.nio.ByteBuffer
import com.awxkee.jxlcoderlibjxl.JxlCoder as LibJxlCoder

@RunWith(AndroidJUnit4::class)
class JxlBackendInstrumentedTest {

    @Test
    fun everyJixelEffortPreservesLosslessSamplesAndAlpha() = runBlocking {
        val source = testImage()
        try {
            (1..6).forEach { effort ->
                val encoded = JxlBackend(isJixel = true, isLossless = true).compress(
                    image = source,
                    quality = Quality.Jxl(effort = effort)
                )
                assertTrue("effort=$effort", JxlCoder.isJXL(encoded))
                assertLosslessRoundTrip(source, encoded)
            }
        } finally {
            source.recycle()
        }
    }

    @Test
    fun everyJixelEffortProducesDecodableLossyImages() = runBlocking {
        val source = testImage()
        try {
            (1..6).forEach { effort ->
                val encoded = JxlBackend(isJixel = true, isLossless = false).compress(
                    image = source,
                    quality = Quality.Jxl(qualityValue = 85, effort = effort)
                )
                val decoded = LibJxlCoder.decode(encoded)
                try {
                    assertEquals(source.width, decoded.width)
                    assertEquals(source.height, decoded.height)
                    assertEquals(0, Color.alpha(decoded.getPixel(0, 0)))
                    assertEquals(255, Color.alpha(decoded.getPixel(16, 16)))
                    val original = source.getPixel(16, 16)
                    val actual = decoded.getPixel(16, 16)
                    assertTrue(kotlin.math.abs(Color.red(original) - Color.red(actual)) < 40)
                    assertTrue(kotlin.math.abs(Color.green(original) - Color.green(actual)) < 40)
                    assertTrue(kotlin.math.abs(Color.blue(original) - Color.blue(actual)) < 40)
                } finally {
                    decoded.recycle()
                }
            }
        } finally {
            source.recycle()
        }
    }

    @Test
    fun libjxlLosslessStillPreservesPixels() = runBlocking {
        val source = testImage()
        try {
            val encoded = JxlBackend(isJixel = false, isLossless = true).compress(
                image = source,
                quality = Quality.Jxl()
            )
            assertLosslessRoundTrip(source, encoded, isJixel = false)
        } finally {
            source.recycle()
        }
    }

    @Test
    @SdkSuppress(minSdkVersion = 34)
    fun jixelPreservesAttachedGainmapInContainer() = runBlocking {
        val source = testImage()
        val map = Bitmap.createBitmap(8, 8, Bitmap.Config.ARGB_8888).apply {
            eraseColor(Color.GRAY)
        }
        source.gainmap = Gainmap(map).apply {
            setRatioMax(2f, 2f, 2f)
            displayRatioForFullHdr = 2f
        }
        try {
            listOf(true, false).forEach { lossless ->
                val encoded = JxlBackend(isJixel = true, isLossless = lossless).compress(
                    image = source,
                    quality = Quality.Jxl(qualityValue = 85)
                )
                val bundle = requireNotNull(findBox(encoded, "jhgm"))
                assertEquals(0, bundle[0].toInt())
                val metadataSize = ((bundle[1].toInt() and 255) shl 8) or
                        (bundle[2].toInt() and 255)
                assertTrue(metadataSize > 0)
                val colorSizeOffset = 3 + metadataSize
                val iccSizeOffset = colorSizeOffset + 1 + (bundle[colorSizeOffset].toInt() and 255)
                val iccSize = ByteBuffer.wrap(bundle, iccSizeOffset, 4).int
                val gainmapData = bundle.copyOfRange(iccSizeOffset + 4 + iccSize, bundle.size)
                val decodedMap = LibJxlCoder.decode(gainmapData)
                try {
                    assertEquals(8, decodedMap.width)
                    assertEquals(8, decodedMap.height)
                } finally {
                    decodedMap.recycle()
                }
            }
        } finally {
            source.recycle()
            map.recycle()
        }
    }

    @Test
    fun decodesLosslessFixtureCreatedWithAlpha05() {
        val encoded = InstrumentationRegistry.getInstrumentation().context.assets
            .open("jxl/lossless-alpha05.jxl").use { it.readBytes() }
        val source = testImage()
        try {
            assertLosslessRoundTrip(source, encoded)
        } finally {
            source.recycle()
        }
    }

    private fun assertLosslessRoundTrip(
        expected: Bitmap,
        encoded: ByteArray,
        isJixel: Boolean = true
    ) {
        // Jixel reads Android's stored samples directly. libjxl exposes these as
        // straight colors, whereas the Rust bitmap path keeps Android's alpha association.
        val encodedSamples = if (isJixel) {
            expected.copy(Bitmap.Config.ARGB_8888, true).apply { isPremultiplied = false }
        } else expected
        val reference = LibJxlCoder.decode(encoded)
        try {
            assertPixelsEqual(encodedSamples, reference)
        } finally {
            reference.recycle()
            if (encodedSamples !== expected) encodedSamples.recycle()
        }
        val decoded = JxlCoder.decode(encoded)
        try {
            // ICC conversion can round some RGB channels in the Rust bitmap path.
            // The independent libjxl check above still requires exact encoded pixels.
            assertPixelsEqual(expected, decoded, colorTolerance = 1)
        } finally {
            decoded.recycle()
        }
    }

    private fun testImage(): Bitmap = Bitmap.createBitmap(32, 24, Bitmap.Config.ARGB_8888).apply {
        for (y in 0 until height) {
            for (x in 0 until width) {
                setPixel(
                    x, y, when {
                        x < 4 -> Color.TRANSPARENT
                        x < 8 -> Color.argb(128, 255, 0, 0)
                        else -> Color.rgb(x * 7, y * 9, (x + y) * 4)
                    }
                )
            }
        }
    }

    private fun assertPixelsEqual(
        expected: Bitmap,
        actual: Bitmap,
        colorTolerance: Int = 0
    ) {
        assertEquals(expected.width, actual.width)
        assertEquals(expected.height, actual.height)
        fun Bitmap.pixels() = IntArray(width * height).also {
            getPixels(it, 0, width, 0, 0, width, height)
        }

        val expectedPixels = expected.pixels()
        val actualPixels = actual.pixels()
        if (colorTolerance == 0) {
            assertArrayEquals(expectedPixels, actualPixels)
        } else {
            expectedPixels.indices.forEach { index ->
                val before = expectedPixels[index]
                val after = actualPixels[index]
                assertEquals("alpha at $index", Color.alpha(before), Color.alpha(after))
                listOf(16, 8, 0).forEach { shift ->
                    val delta = kotlin.math.abs(
                        ((before shr shift) and 255) - ((after shr shift) and 255)
                    )
                    assertTrue(
                        "pixel $index, channel $shift: delta=$delta",
                        delta <= colorTolerance
                    )
                }
            }
        }
    }

    private fun findBox(data: ByteArray, type: String): ByteArray? {
        val buffer = ByteBuffer.wrap(data)
        while (buffer.remaining() >= 8) {
            val start = buffer.position()
            val size = buffer.int
            val boxType = ByteArray(4).also(buffer::get).decodeToString()
            val length = when (size) {
                0 -> data.size - start
                1 -> buffer.long.toInt()
                else -> size
            }
            require(length >= buffer.position() - start && length <= data.size - start)
            if (boxType == type) return data.copyOfRange(buffer.position(), start + length)
            buffer.position(start + length)
        }
        return null
    }
}