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
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.t8rin.neural_tools.inpaint.LaMaProcessor
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class NeuralProcessorsTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val fixtures = File(context.filesDir, "neural-tests")

    @Test
    fun lamaKeepsUnmaskedPixelsAndOriginalSize() {
        val name = "LaMa_dynamic.onnx"
        fixture(name).copyTo(
            File(context.filesDir, "onnx/$name").apply { parentFile?.mkdirs() },
            overwrite = true
        )
        val source = patternedBitmap(769, 521)
        val mask = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
        Canvas(mask).apply {
            drawColor(Color.BLACK)
            drawRect(140f, 100f, 600f, 400f, Paint().apply { color = Color.WHITE })
        }
        val result = LaMaProcessor.inpaint(source, mask)
        assertNotNull(result)
        result!!
        assertEquals(source.width, result.width)
        assertEquals(source.height, result.height)
        var changed = 0
        for (y in 0 until source.height) {
            for (x in 0 until source.width) {
                val original = source.getPixel(x, y)
                val pixel = result.getPixel(x, y)
                if (x !in 140 until 600 || y !in 100 until 400) {
                    assertEquals("Unmasked pixel changed at $x, $y ($name)", original, pixel)
                } else if (original != pixel) changed++
            }
        }
        assertTrue("The model did not fill the mask", changed > 100)
        source.recycle()
        mask.recycle()
        result.recycle()
    }

    @Test
    fun transparentAndEmptyMasksKeepEveryPixel() {
        fixture("LaMa_dynamic.onnx").copyTo(
            File(
                context.filesDir,
                "onnx/LaMa_dynamic.onnx"
            ).apply { parentFile?.mkdirs() }, overwrite = true
        )
        val source = patternedBitmap(133, 97)
        for (color in listOf(Color.BLACK, Color.TRANSPARENT)) {
            val mask = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
            mask.eraseColor(color)
            val result = LaMaProcessor.inpaint(source, mask)!!
            assertTrue(source.sameAs(result))
            result.recycle()
            mask.recycle()
        }
        source.recycle()
    }

    @Test
    fun docShadowKeepsResolutionAlphaAndDarkText() = runBlocking {
        val source = Bitmap.createBitmap(1301, 257, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(source)
        canvas.drawColor(Color.rgb(145, 145, 145))
        val paint = Paint().apply { color = Color.BLACK; strokeWidth = 2f }
        for (x in 10 until source.width step 7) canvas.drawLine(
            x.toFloat(),
            10f,
            x.toFloat(),
            240f,
            paint
        )
        session("docshadow_sd7k.onnx").use { session ->
            val result = DocShadowProcessor().process(source, session)
            assertEquals(source.width, result.width)
            assertEquals(source.height, result.height)
            for (y in 0 until source.height step 5) {
                for (x in 0 until source.width step 5) {
                    assertEquals(
                        Color.alpha(source.getPixel(x, y)),
                        Color.alpha(result.getPixel(x, y))
                    )
                }
            }
            for (x in 10 until source.width step 7) {
                assertEquals("Black text lost its detail", Color.BLACK, result.getPixel(x, 100))
            }
            assertTrue(!source.sameAs(result))
            result.recycle()
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

    private fun patternedBitmap(width: Int, height: Int): Bitmap {
        val pixels = IntArray(width * height) { index ->
            val x = index % width
            val y = index / width
            Color.rgb(x % 256, y % 256, (x * 17 + y * 13) % 256)
        }
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
    }
}