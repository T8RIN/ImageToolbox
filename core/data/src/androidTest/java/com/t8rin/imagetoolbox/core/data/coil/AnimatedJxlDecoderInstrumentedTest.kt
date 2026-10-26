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

import android.graphics.Bitmap
import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import coil3.ImageLoader
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.size.Size
import coil3.toBitmap
import com.awxkee.jxlcoder.JxlAnimatedImage
import com.awxkee.jxlcoder.JxlCoder
import com.awxkee.jxlcoder.PreferredColorConfig
import com.awxkee.jxlcoder.ScaleMode
import com.awxkee.jxlcoder.animation.JxlAnimatedStore
import com.awxkee.jxlcoderlibjxl.JxlAnimatedEncoder
import com.awxkee.jxlcoderlibjxl.JxlChannelsConfiguration
import com.awxkee.jxlcoderlibjxl.JxlCompressionOption
import com.awxkee.jxlcoderlibjxl.JxlDecodingSpeed
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import com.awxkee.jxlcoderlibjxl.JxlCoder as LibJxlCoder

@RunWith(AndroidJUnit4::class)
class AnimatedJxlDecoderInstrumentedTest {

    @Test
    fun animationDecoderPreservesFramesTimingLoopsAndRandomAccess() {
        JxlAnimatedImage(animation(), PreferredColorConfig.RGBA_8888).use { decoder ->
            assertEquals(3, decoder.numberOfFrames)
            assertEquals(2, decoder.loopsCount)
            assertEquals(32, decoder.getWidth())
            assertEquals(16, decoder.getHeight())
            listOf(2, 0, 1, 2).forEach { index ->
                assertEquals(listOf(120, 230, 340)[index], decoder.getFrameDuration(index))
                val frame = decoder.getFrame(index)
                try {
                    assertEquals(
                        listOf(Color.RED, Color.GREEN, Color.BLUE)[index],
                        frame.getPixel(8, 8)
                    )
                } finally {
                    frame.recycle()
                }
            }
            val frame = decoder.getFrame(1, scaleWidth = 16, scaleHeight = 8)
            try {
                assertEquals(16, frame.width)
                assertEquals(8, frame.height)
                assertEquals(Color.GREEN, frame.getPixel(4, 4))
            } finally {
                frame.recycle()
            }
        }
    }

    @Test
    fun animatedFrameStoreReportsTheRenderedSize() {
        JxlAnimatedImage(animation(), scaleMode = ScaleMode.FIT).use { decoder ->
            val store = JxlAnimatedStore(decoder, targetWidth = 20, targetHeight = 20)
            val frame = store.getFrame(0)
            try {
                assertEquals(store.width, frame.width)
                assertEquals(store.height, frame.height)
                assertEquals(20, frame.width)
                assertEquals(10, frame.height)
            } finally {
                frame.recycle()
            }
        }
    }

    @Test
    fun coilCanReadAnAnimationAsAStaticImageAtOriginalAndSampledSizes() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val loader = ImageLoader.Builder(context)
            .components { add(AnimatedJxlDecoder.Factory(exceptionLogger = { throw it })) }
            .build()
        try {
            listOf(
                Size.ORIGINAL to (32 to 16),
                Size(16, 16) to (16 to 8)
            ).forEach { (size, expected) ->
                val result = loader.execute(
                    ImageRequest.Builder(context)
                        .data(animation())
                        .size(size)
                        .allowHardware(false)
                        .memoryCachePolicy(CachePolicy.DISABLED)
                        .enableJxlAnimation(false)
                        .build()
                )
                assertTrue(result.toString(), result is SuccessResult)
                val bitmap = requireNotNull(result.image).toBitmap()
                try {
                    assertEquals(expected.first, bitmap.width)
                    assertEquals(expected.second, bitmap.height)
                    assertEquals(Color.RED, bitmap.getPixel(4, 4))
                } finally {
                    bitmap.recycle()
                }
            }
        } finally {
            loader.shutdown()
        }
    }

    @Test
    fun jpegTranscodingRetainsByteExactReconstructionAndDecodesWithTheNewBackend() {
        val source = Bitmap.createBitmap(32, 16, Bitmap.Config.ARGB_8888).apply {
            eraseColor(Color.CYAN)
        }
        val jpeg = ByteArrayOutputStream().use { output ->
            source.compress(Bitmap.CompressFormat.JPEG, 90, output)
            source.recycle()
            output.toByteArray()
        }
        val buffer = ByteBuffer.allocateDirect(jpeg.size).apply {
            put(jpeg)
            rewind()
        }
        val jxl = JxlCoder.transcode(buffer, allowReconstruction = true)
        assertArrayEquals(jpeg, LibJxlCoder.Convenience.reconstructJPEG(jxl))
        val decoded = JxlCoder.decode(jxl)
        try {
            assertEquals(32, decoded.width)
            assertEquals(16, decoded.height)
            val expected = Color.CYAN
            val actual = decoded.getPixel(8, 8)
            assertTrue(kotlin.math.abs(Color.green(expected) - Color.green(actual)) < 5)
            assertTrue(kotlin.math.abs(Color.blue(expected) - Color.blue(actual)) < 5)
        } finally {
            decoded.recycle()
        }
    }

    @Test
    fun truncatedJxlFailsWithoutReturningAnImage() {
        val truncated = animation().copyOf(16)
        assertThrows(Exception::class.java) { JxlCoder.decode(truncated) }
        assertThrows(Exception::class.java) { JxlAnimatedImage(truncated).use { it.getFrame(0) } }
    }

    @Test
    fun narrowImagesHaveANonzeroSampledDimension() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val source = Bitmap.createBitmap(1, 1024, Bitmap.Config.ARGB_8888).apply {
            eraseColor(Color.RED)
        }
        val encoded = LibJxlCoder.encode(source)
        source.recycle()
        val loader = ImageLoader.Builder(context)
            .components { add(AnimatedJxlDecoder.Factory(exceptionLogger = { throw it })) }
            .build()
        try {
            val result = loader.execute(
                ImageRequest.Builder(context)
                    .data(encoded)
                    .size(16)
                    .allowHardware(false)
                    .enableJxlAnimation(false)
                    .build()
            )
            assertTrue(result.toString(), result is SuccessResult)
            assertEquals(1, result.image?.width)
            assertEquals(16, result.image?.height)
        } finally {
            loader.shutdown()
        }
    }

    private fun animation(): ByteArray {
        val encoder = JxlAnimatedEncoder(
            width = 32,
            height = 16,
            numLoops = 2,
            channelsConfiguration = JxlChannelsConfiguration.RGBA,
            compressionOption = JxlCompressionOption.LOSSLESS,
            effort = 2,
            quality = 100,
            decodingSpeed = JxlDecodingSpeed.SLOWEST
        )
        listOf(Color.RED, Color.GREEN, Color.BLUE).forEachIndexed { index, color ->
            val frame = Bitmap.createBitmap(32, 16, Bitmap.Config.ARGB_8888).apply {
                eraseColor(color)
            }
            try {
                encoder.addFrame(frame, listOf(120, 230, 340)[index])
            } finally {
                frame.recycle()
            }
        }
        return encoder.encode()
    }
}