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

package com.t8rin.imagetoolbox.feature.ai_tools.data

import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.os.SystemClock
import androidx.core.graphics.createBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.t8rin.imagetoolbox.core.data.coroutines.AndroidDispatchersHolder
import com.t8rin.imagetoolbox.core.data.resource.AndroidResourceManager
import com.t8rin.imagetoolbox.core.domain.saving.KeepAliveService
import com.t8rin.imagetoolbox.feature.ai_tools.domain.AiProgressListener
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.NeuralModel
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.NeuralParams
import com.t8rin.opencv_tools.face.YuNetFaceDetector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import kotlin.math.abs

@RunWith(AndroidJUnit4::class)
class FaceRestorationTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val fixtures = File(context.filesDir, "neural-tests")
    private val listener = object : AiProgressListener {
        override fun onError(error: String) = throw AssertionError(error)
        override fun onProgress(currentChunkIndex: Int, totalChunks: Int) = Unit
    }

    @Test
    fun restoresMultipleFacesWithoutChangingBackgroundOrAlpha() = runBlocking {
        val face = BitmapFactory.decodeFile(fixture("portrait.png").absolutePath)
        val source = createBitmap(face.width * 2 + 40, face.height + 40)
        Canvas(source).apply {
            drawColor(Color.argb(160, 30, 40, 50))
            drawBitmap(face, 10f, 20f, null)
            drawBitmap(face, face.width + 30f, 20f, null)
        }
        val original = source.copy(Bitmap.Config.ARGB_8888, false)
        val faces = YuNetFaceDetector.detectWithLandmarks(source)
        assertEquals(2, faces.size)
        val progress = mutableListOf<Pair<Int, Int>>()
        session("gfpgan_v1.4.onnx").use { session ->
            val started = SystemClock.elapsedRealtime()
            val result = FaceRestoreProcessor().process(
                source,
                session,
                100f,
                object : AiProgressListener {
                    override fun onError(error: String) = throw AssertionError(error)
                    override fun onProgress(currentChunkIndex: Int, totalChunks: Int) {
                        progress += currentChunkIndex to totalChunks
                    }
                }
            )
            File(fixtures, "gfpgan-timing.txt").writeText(
                (SystemClock.elapsedRealtime() - started).toString()
            )
            assertEquals(source.width, result.width)
            assertEquals(source.height, result.height)
            assertTrue(source.sameAs(original))
            assertEquals(listOf(0 to 2, 1 to 2, 2 to 2), progress)
            for (y in 0 until source.height) {
                for (x in 0 until source.width) {
                    assertEquals(
                        Color.alpha(source.getPixel(x, y)),
                        Color.alpha(result.getPixel(x, y))
                    )
                }
            }
            for (faceRegion in faces) {
                val bounds = faceRegion.bounds
                var changed = 0
                for (y in (bounds.top * source.height).toInt() until (bounds.bottom * source.height).toInt()) {
                    for (x in (bounds.left * source.width).toInt() until (bounds.right * source.width).toInt()) {
                        if (source.getPixel(x, y) != result.getPixel(x, y)) changed++
                    }
                }
                assertTrue("A detected face was not restored", changed > 100)
            }
            assertEquals(source.getPixel(0, 0), result.getPixel(0, 0))
            assertEquals(source.getPixel(source.width - 1, 0), result.getPixel(source.width - 1, 0))
            save(result, "gfpgan-multiple-result.png")
            result.recycle()
        }
        original.recycle()
        source.recycle()
        face.recycle()
    }

    @Test
    fun strengthBlendsRestorationAndEmptyImagesStayUnchanged() = runBlocking {
        val source = BitmapFactory.decodeFile(fixture("portrait.png").absolutePath)
        session("gfpgan_v1.4.onnx").use { session ->
            val processor = FaceRestoreProcessor()
            val unchanged = processor.process(source, session, 0f, listener)
            assertTrue(source.sameAs(unchanged))
            unchanged.recycle()
            val half = processor.process(source, session, 50f, listener)
            val full = processor.process(source, session, 100f, listener)
            var halfDifference = 0L
            var fullDifference = 0L
            for (y in 0 until source.height) {
                for (x in 0 until source.width) {
                    val original = source.getPixel(x, y)
                    for (shift in intArrayOf(0, 8, 16)) {
                        val originalChannel = (original shr shift) and 255
                        halfDifference += abs(
                            ((half.getPixel(
                                x,
                                y
                            ) shr shift) and 255) - originalChannel
                        )
                        fullDifference += abs(
                            ((full.getPixel(
                                x,
                                y
                            ) shr shift) and 255) - originalChannel
                        )
                    }
                }
            }
            assertTrue(fullDifference > 1000)
            assertTrue(halfDifference.toDouble() / fullDifference in 0.4..0.6)
            save(half, "gfpgan-half-result.png")
            save(full, "gfpgan-full-result.png")
            half.recycle()
            full.recycle()
            val empty = createBitmap(128, 96).apply { eraseColor(Color.argb(130, 30, 40, 50)) }
            val result = processor.process(empty, session, 100f, listener)
            assertTrue(empty.sameAs(result))
            empty.recycle()
            result.recycle()
        }
        source.recycle()
    }

    @Test
    fun dehazeProcessesWholeImageRegardlessOfChunking() = runBlocking {
        val processor = AiProcessor(
            context,
            KeepAliveService.EMPTY,
            AndroidDispatchersHolder(
                Dispatchers.Default,
                Dispatchers.IO,
                Dispatchers.Default,
                Dispatchers.Default,
                Dispatchers.Default
            ),
            AndroidResourceManager(context)
        )
        val source = createBitmap(321, 237)
        for (y in 0 until source.height) {
            for (x in 0 until source.width) {
                source.setPixel(x, y, Color.argb(80 + x % 176, 140 + x % 100, 120 + y % 100, 170))
            }
        }
        val model = NeuralModel.find("dehazeformer_t.onnx")!!
        assertTrue(model.isNonChunkable)
        session(model.name).use { session ->
            for (chunked in listOf(false, true)) {
                val started = SystemClock.elapsedRealtime()
                val result = processor.processImage(
                    session,
                    source,
                    model,
                    NeuralParams(
                        strength = 65f,
                        chunkSize = 128,
                        overlap = 16,
                        enableChunking = chunked
                    ),
                    listener
                )!!
                File(fixtures, "dehaze-timing-$chunked.txt").writeText(
                    (SystemClock.elapsedRealtime() - started).toString()
                )
                assertEquals(source.width, result.width)
                assertEquals(source.height, result.height)
                assertFalse(source.sameAs(result))
                for (y in 0 until source.height step 7) {
                    for (x in 0 until source.width step 7) {
                        assertEquals(
                            Color.alpha(source.getPixel(x, y)),
                            Color.alpha(result.getPixel(x, y))
                        )
                    }
                }
                save(result, "dehaze-result-$chunked.png")
                result.recycle()
            }
        }
        source.recycle()
    }

    private fun fixture(name: String): File = File(fixtures, name).also {
        assumeTrue(
            "Copy neural-model fixtures to ${fixtures.absolutePath} before this test",
            it.isFile
        )
    }

    private fun session(name: String): OrtSession = OrtSession.SessionOptions().use { options ->
        options.setIntraOpNumThreads(2)
        options.addConfigEntry("mlas.disable_kleidiai", "1")
        OrtEnvironment.getEnvironment().createSession(fixture(name).absolutePath, options)
    }

    private fun save(bitmap: Bitmap, name: String) {
        File(fixtures, name).outputStream()
            .use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }
}