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

package com.t8rin.imagetoolbox.feature.jxl_tools.data

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.net.toUri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.awxkee.jxlcoderlibjxl.JxlAnimatedEncoder
import com.awxkee.jxlcoderlibjxl.JxlChannelsConfiguration
import com.awxkee.jxlcoderlibjxl.JxlCompressionOption
import com.awxkee.jxlcoderlibjxl.JxlDecodingSpeed
import com.t8rin.gif_converter.GifDecoder
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.lang.reflect.Proxy
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.coroutines.CoroutineContext

@RunWith(AndroidJUnit4::class)
class AnimatedJxlConversionInstrumentedTest {

    @Test
    fun conversionsPreserveFramesTimingAndLoops() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val durations = intArrayOf(120, 230, 340)
        val sourceEncoder = JxlAnimatedEncoder(
            width = 16,
            height = 16,
            numLoops = 2,
            channelsConfiguration = JxlChannelsConfiguration.RGBA,
            compressionOption = JxlCompressionOption.LOSSLESS,
            effort = 2,
            quality = 100,
            decodingSpeed = JxlDecodingSpeed.SLOWEST
        )
        listOf(Color.RED, Color.GREEN, Color.BLUE).forEachIndexed { index, color ->
            sourceEncoder.addFrame(
                Bitmap.createBitmap(16, 16, Bitmap.Config.ARGB_8888).apply {
                    eraseColor(color)
                },
                durations[index]
            )
        }
        val source = File(context.cacheDir, "jxl_conversion_test.jxl").apply {
            writeBytes(sourceEncoder.encode())
        }
        val converter = AndroidJxlConverter(
            context = context,
            imageGetter = unused(),
            imageShareProvider = unused(),
            imageScaler = unused(),
            dispatchersHolder = TestDispatchersHolder
        )

        var gif: ByteArray? = null
        converter.convertJxlToGif(
            jxlUris = listOf(source.toUri().toString()),
            quality = Quality.Base(100)
        ) { _, bytes -> gif = bytes }

        var apng: ByteArray? = null
        converter.convertJxlToApng(listOf(source.toUri().toString())) { _, bytes ->
            apng = bytes
        }

        var webp: ByteArray? = null
        converter.convertJxlToWebp(
            jxlUris = listOf(source.toUri().toString()),
            quality = Quality.Base(100)
        ) { _, bytes -> webp = bytes }

        val gifDecoder = GifDecoder()
        assertEquals(0, gifDecoder.read(requireNotNull(gif)))
        assertEquals(3, gifDecoder.frameCount)
        assertEquals(2, gifDecoder.loopCount)
        assertArrayEquals(durations, IntArray(3, gifDecoder::getDelay))

        val apngInfo = parseApng(requireNotNull(apng))
        assertEquals(3, apngInfo.frameCount)
        assertEquals(2, apngInfo.loopCount)
        assertArrayEquals(durations, apngInfo.durations)

        val webpInfo = parseWebp(requireNotNull(webp))
        assertEquals(3, webpInfo.frameCount)
        assertEquals(2, webpInfo.loopCount)
        assertArrayEquals(durations, webpInfo.durations)
    }

    private fun parseApng(data: ByteArray): AnimationInfo {
        val buffer = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN).apply { position(8) }
        var frameCount = 0
        var loopCount = 0
        val durations = mutableListOf<Int>()
        while (buffer.remaining() >= 12) {
            val length = buffer.int
            val type = ByteArray(4).also(buffer::get).decodeToString()
            val chunk = ByteArray(length).also(buffer::get)
            buffer.int
            when (type) {
                "acTL" -> ByteBuffer.wrap(chunk).order(ByteOrder.BIG_ENDIAN).let {
                    frameCount = it.int
                    loopCount = it.int
                }

                "fcTL" -> ByteBuffer.wrap(chunk).order(ByteOrder.BIG_ENDIAN).apply {
                    position(20)
                    val numerator = short.toInt() and 0xffff
                    val denominator = (short.toInt() and 0xffff).takeIf { it != 0 } ?: 100
                    durations += numerator * 1000 / denominator
                }
            }
        }
        return AnimationInfo(frameCount, loopCount, durations.toIntArray())
    }

    private fun parseWebp(data: ByteArray): AnimationInfo {
        val buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).apply { position(12) }
        var loopCount = 0
        val durations = mutableListOf<Int>()
        while (buffer.remaining() >= 8) {
            val type = ByteArray(4).also(buffer::get).decodeToString()
            val length = buffer.int
            val chunk = ByteArray(length).also(buffer::get)
            if (length % 2 != 0 && buffer.hasRemaining()) buffer.get()
            when (type) {
                "ANIM" -> loopCount = (chunk[4].toInt() and 0xff) or
                        ((chunk[5].toInt() and 0xff) shl 8)

                "ANMF" -> durations += (chunk[12].toInt() and 0xff) or
                        ((chunk[13].toInt() and 0xff) shl 8) or
                        ((chunk[14].toInt() and 0xff) shl 16)
            }
        }
        return AnimationInfo(durations.size, loopCount, durations.toIntArray())
    }

    private inline fun <reified T> unused(): T = Proxy.newProxyInstance(
        T::class.java.classLoader,
        arrayOf(T::class.java)
    ) { _, method, _ -> error("Unexpected call to ${method.name}") } as T

    private data class AnimationInfo(
        val frameCount: Int,
        val loopCount: Int,
        val durations: IntArray
    )

    private data object TestDispatchersHolder : DispatchersHolder {
        override val uiDispatcher: CoroutineContext = Dispatchers.Default
        override val ioDispatcher: CoroutineContext = Dispatchers.Default
        override val encodingDispatcher: CoroutineContext = Dispatchers.Default
        override val decodingDispatcher: CoroutineContext = Dispatchers.Default
        override val defaultDispatcher: CoroutineContext = Dispatchers.Default
    }
}
