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

package com.t8rin.imagetoolbox.feature.webp_tools.data

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.net.toUri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.awxkee.jxlcoderlibjxl.JxlAnimatedImage
import com.t8rin.awebp.encoder.AnimatedWebpEncoder
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.lang.reflect.Proxy
import kotlin.coroutines.CoroutineContext

@RunWith(AndroidJUnit4::class)
class AnimatedWebpToJxlInstrumentedTest {

    @Test
    fun conversionPreservesAnimationTimingAndLoops() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val durations = listOf(120, 230, 340)
        val colors = listOf(Color.RED, Color.GREEN, Color.BLUE)
        val webpEncoder = AnimatedWebpEncoder(
            quality = 100,
            loopCount = 2,
            backgroundColor = Color.TRANSPARENT
        )
        colors.zip(durations).forEach { (color, duration) ->
            webpEncoder.addFrame(
                bitmap = Bitmap.createBitmap(16, 16, Bitmap.Config.ARGB_8888).apply {
                    eraseColor(color)
                },
                duration = duration
            )
        }
        val source = File(context.cacheDir, "webp_to_jxl_test.webp").apply {
            writeBytes(webpEncoder.encode())
        }
        var result: ByteArray? = null

        AndroidWebpConverter(
            imageGetter = unused(),
            imageShareProvider = unused(),
            imageScaler = unused(),
            context = context,
            dispatchersHolder = TestDispatchersHolder
        ).convertWebpToJxl(
            webpUris = listOf(source.toUri().toString()),
            quality = Quality.Jxl(qualityValue = 100)
        ) { _, bytes ->
            result = bytes
        }

        val encoded = requireNotNull(result)
        JxlAnimatedImage(encoded).use { decoder ->
            assertEquals(3, decoder.numberOfFrames)
            assertEquals(2, decoder.loopsCount)
            assertEquals(durations, List(decoder.numberOfFrames, decoder::getFrameDuration))
            colors.forEachIndexed { index, expectedColor ->
                val frame = decoder.getFrame(index)
                assertColorClose(expectedColor, frame.getPixel(8, 8))
                frame.recycle()
            }
        }
    }

    private fun assertColorClose(expected: Int, actual: Int) {
        assertTrue(kotlin.math.abs(Color.red(expected) - Color.red(actual)) <= 8)
        assertTrue(kotlin.math.abs(Color.green(expected) - Color.green(actual)) <= 8)
        assertTrue(kotlin.math.abs(Color.blue(expected) - Color.blue(actual)) <= 8)
    }

    private inline fun <reified T> unused(): T = Proxy.newProxyInstance(
        T::class.java.classLoader,
        arrayOf(T::class.java)
    ) { _, method, _ -> error("Unexpected call to ${method.name}") } as T

    private data object TestDispatchersHolder : DispatchersHolder {
        override val uiDispatcher: CoroutineContext = Dispatchers.Default
        override val ioDispatcher: CoroutineContext = Dispatchers.Default
        override val encodingDispatcher: CoroutineContext = Dispatchers.Default
        override val decodingDispatcher: CoroutineContext = Dispatchers.Default
        override val defaultDispatcher: CoroutineContext = Dispatchers.Default
    }
}
