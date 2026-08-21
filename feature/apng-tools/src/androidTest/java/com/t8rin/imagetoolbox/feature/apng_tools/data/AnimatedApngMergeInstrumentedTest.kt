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

package com.t8rin.imagetoolbox.feature.apng_tools.data

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.net.toUri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.model.AnimationMergeItem
import com.t8rin.imagetoolbox.core.domain.image.model.AnimationMergeParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import oupson.apng.encoder.ApngEncoder
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.reflect.Proxy
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.coroutines.CoroutineContext

@RunWith(AndroidJUnit4::class)
class AnimatedApngMergeInstrumentedTest {

    @Test
    fun mergePreservesOrderTimingAndRequestedLoopCount() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val durations = listOf(120, 230, 340)
        val output = ByteArrayOutputStream()
        val encoder = ApngEncoder(output, 16, 16, durations.size).apply {
            setEncodeAlpha(true)
            setOptimiseApng(false)
        }
        listOf(Color.RED, Color.GREEN, Color.BLUE).forEachIndexed { index, color ->
            encoder.writeFrame(
                Bitmap.createBitmap(16, 16, Bitmap.Config.ARGB_8888).apply {
                    eraseColor(color)
                },
                durations[index].toFloat()
            )
        }
        encoder.writeEnd()
        val source = File(context.cacheDir, "apng_merge_test.png").apply {
            writeBytes(output.toByteArray())
        }

        val merged = requireNotNull(
            AndroidApngConverter(
                imageGetter = unused(),
                shareProvider = unused(),
                imageScaler = unused(),
                context = context,
                dispatchersHolder = TestDispatchersHolder
            ).mergeApngs(
                items = listOf(
                    AnimationMergeItem(source.toUri().toString()),
                    AnimationMergeItem(
                        uri = source.toUri().toString(),
                        reverse = true,
                        boomerang = true
                    )
                ),
                params = AnimationMergeParams(
                    transitionDelayMillis = 50,
                    repeatCount = 4
                ),
                onFailure = { throw it },
                onProgress = {}
            )
        )

        val info = parseApng(merged)
        assertEquals(8, info.frameCount)
        assertEquals(4, info.loopCount)
        assertEquals(
            listOf(120, 230, 390, 340, 230, 120, 230, 340),
            info.durations
        )
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
        return AnimationInfo(frameCount, loopCount, durations)
    }

    private inline fun <reified T> unused(): T = Proxy.newProxyInstance(
        T::class.java.classLoader,
        arrayOf(T::class.java)
    ) { _, method, _ -> error("Unexpected call to ${method.name}") } as T

    private data class AnimationInfo(
        val frameCount: Int,
        val loopCount: Int,
        val durations: List<Int>
    )

    private data object TestDispatchersHolder : DispatchersHolder {
        override val uiDispatcher: CoroutineContext = Dispatchers.Default
        override val ioDispatcher: CoroutineContext = Dispatchers.Default
        override val encodingDispatcher: CoroutineContext = Dispatchers.Default
        override val decodingDispatcher: CoroutineContext = Dispatchers.Default
        override val defaultDispatcher: CoroutineContext = Dispatchers.Default
    }
}
