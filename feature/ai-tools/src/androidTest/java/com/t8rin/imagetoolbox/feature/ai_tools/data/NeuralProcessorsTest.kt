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
import android.graphics.Paint
import android.os.SystemClock
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.transformations
import coil3.size.Size
import coil3.toBitmap
import com.t8rin.imagetoolbox.core.data.utils.toCoil
import com.t8rin.imagetoolbox.core.di.entryPoint
import com.t8rin.imagetoolbox.core.domain.model.ImageModel
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.SpotHealMode
import com.t8rin.imagetoolbox.core.settings.di.SettingsStateEntryPoint
import com.t8rin.imagetoolbox.feature.filters.data.model.SpotHealFilter
import com.t8rin.neural_tools.inpaint.LaMaProcessor
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import kotlin.math.abs

@RunWith(AndroidJUnit4::class)
class NeuralProcessorsTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val fixtures = File(context.filesDir, "neural-tests")

    @Test
    fun lamaKeepsUnmaskedPixelsAndOriginalSize() {
        val name = "LaMa_dynamic.onnx"
        fixture(name).copyTo(
            File(context.filesDir, "onnx/LaMa_dynamic.onnx").apply { parentFile?.mkdirs() },
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
    fun lamaKeepsContextForLargeRemovals() {
        fixture("LaMa_dynamic.onnx").copyTo(
            File(context.filesDir, "onnx/LaMa_dynamic.onnx").apply { parentFile?.mkdirs() },
            overwrite = true
        )
        val source = BitmapFactory.decodeFile(fixture("lama-context.jpg").absolutePath)
        val mask = BitmapFactory.decodeFile(fixture("lama-context-mask.png").absolutePath)
        val reference = BitmapFactory.decodeFile(fixture("lama-context-reference.png").absolutePath)
        val previewStart = SystemClock.elapsedRealtime()
        val preview = LaMaProcessor.inpaint(source, mask, isPreview = true)!!
        val previewTime = SystemClock.elapsedRealtime() - previewStart
        Log.i("LaMaBenchmark", "${source.width}x${source.height}: preview=$previewTime ms")
        val exportStart = SystemClock.elapsedRealtime()
        val result = LaMaProcessor.inpaint(source, mask)!!
        val exportTime = SystemClock.elapsedRealtime() - exportStart
        Log.i(
            "LaMaBenchmark",
            "${source.width}x${source.height}: preview=$previewTime ms, export=$exportTime ms"
        )
        assertEquals(source.width, preview.width)
        assertEquals(source.height, preview.height)
        assertTrue("Export reused the reduced preview", !preview.sameAs(result))
        assertEquals(source.width, result.width)
        assertEquals(source.height, result.height)
        var error = 0L
        var samples = 0
        for (y in 0 until source.height step 4) {
            for (x in 0 until source.width step 4) {
                if (Color.red(mask.getPixel(x, y)) > 0) {
                    val expected = reference.getPixel(x, y)
                    val actual = result.getPixel(x, y)
                    error += abs(Color.red(expected) - Color.red(actual))
                    error += abs(Color.green(expected) - Color.green(actual))
                    error += abs(Color.blue(expected) - Color.blue(actual))
                    samples += 3
                } else {
                    assertEquals(source.getPixel(x, y), result.getPixel(x, y))
                    assertEquals(source.getPixel(x, y), preview.getPixel(x, y))
                }
            }
        }
        assertTrue(
            "The removal lost its surrounding colors: ${error.toDouble() / samples}",
            samples > 0 && error.toDouble() / samples < 10
        )
        File(fixtures, "lama-context-result.png").outputStream().use {
            result.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        source.recycle()
        mask.recycle()
        reference.recycle()
        preview.recycle()
        result.recycle()
    }

    @Test
    fun lamaPreviewCacheDoesNotReplaceOriginalExport() = runBlocking {
        fixture("LaMa_dynamic.onnx").copyTo(
            File(context.filesDir, "onnx/LaMa_dynamic.onnx").apply { parentFile?.mkdirs() },
            overwrite = true
        )
        val source = patternedBitmap(769, 521)
        val mask = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
        Canvas(mask).apply {
            drawColor(Color.BLACK)
            drawRect(140f, 100f, 600f, 400f, Paint().apply { color = Color.WHITE })
        }
        val filter = SpotHealFilter(ImageModel(mask) to SpotHealMode.LaMa).toCoil()
        val preview = context.imageLoader.execute(
            ImageRequest.Builder(context).data(source).size(source.width, source.height)
                .transformations(filter).build()
        ).image!!.toBitmap()
        val exported = context.imageLoader.execute(
            ImageRequest.Builder(context).data(source).size(Size.ORIGINAL)
                .transformations(filter).build()
        ).image!!.toBitmap()
        assertEquals(source.width, exported.width)
        assertEquals(source.height, exported.height)
        assertTrue("The original request reused the preview cache", !preview.sameAs(exported))
        assertEquals(source.getPixel(0, 0), exported.getPixel(0, 0))
    }

    @Test
    fun lamaLimitedExportKeepsOriginalPhotoSize() {
        fixture("LaMa_dynamic.onnx").copyTo(
            File(context.filesDir, "onnx/LaMa_dynamic.onnx").apply { parentFile?.mkdirs() },
            overwrite = true
        )
        val source = BitmapFactory.decodeFile(fixture("lama-context.jpg").path)
        val mask = BitmapFactory.decodeFile(fixture("lama-context-mask.png").path)
        LaMaProcessor.inpaint(source, mask, isPreview = true)!!.recycle()
        val start = SystemClock.elapsedRealtime()
        val result = LaMaProcessor.inpaint(source, mask, limitResolution = true)!!
        val elapsed = SystemClock.elapsedRealtime() - start
        Log.i("LaMaBenchmark", "${source.width}x${source.height}: limited export=$elapsed ms")
        assertEquals(source.width, result.width)
        assertEquals(source.height, result.height)
        val original = IntArray(source.width)
        val selection = IntArray(source.width)
        val actual = IntArray(source.width)
        for (y in 0 until source.height) {
            source.getPixels(original, 0, source.width, 0, y, source.width, 1)
            mask.getPixels(selection, 0, source.width, 0, y, source.width, 1)
            result.getPixels(actual, 0, source.width, 0, y, source.width, 1)
            for (x in original.indices) {
                if (Color.red(selection[x]) == 0) assertEquals(original[x], actual[x])
            }
        }
        File(fixtures, "lama-limited-result.png").outputStream().use {
            result.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        File(fixtures, "lama-limited-timing.txt").writeText("$elapsed")
        source.recycle()
        mask.recycle()
        result.recycle()
    }

    @Test
    fun lamaOptionsPreserveSizeAndSeparateExportCaches() = runBlocking {
        fixture("LaMa_dynamic.onnx").copyTo(
            File(context.filesDir, "onnx/LaMa_dynamic.onnx").apply { parentFile?.mkdirs() },
            overwrite = true
        )
        val settings = context.entryPoint<SettingsStateEntryPoint>().settingsManager
        val original = settings.getSettingsState()
        val source = patternedBitmap(1153, 257)
        val mask = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
        Canvas(mask).drawRect(100f, 60f, 1050f, 180f, Paint().apply { color = Color.WHITE })
        val value = ImageModel(mask) to SpotHealMode.LaMa
        try {
            settings.setLamaThreads(2)
            settings.setLamaLimitResolution(false)
            withTimeout(10_000) {
                settings.settingsState.first { it.lamaThreads == 2 && !it.lamaLimitResolution }
            }
            val nativeFilter = SpotHealFilter(value)
            val nativeKey = nativeFilter.cacheKey
            val native = nativeFilter.transform(source, IntegerSize.Undefined)
            settings.setLamaLimitResolution(true)
            withTimeout(10_000) { settings.settingsState.first { it.lamaLimitResolution } }
            val reducedFilter = SpotHealFilter(value)
            assertTrue(
                "Export resolution did not change the cache key",
                nativeKey != reducedFilter.cacheKey
            )
            assertEquals(
                "An existing filter changed its cache key",
                nativeKey,
                nativeFilter.cacheKey
            )
            val reduced = reducedFilter.transform(source, IntegerSize.Undefined)
            val expected =
                LaMaProcessor.inpaint(source, mask, threads = 2, limitResolution = true)!!
            assertEquals(source.width, reduced.width)
            assertEquals(source.height, reduced.height)
            assertTrue("The saved setting was not applied", expected.sameAs(reduced))
            assertTrue("Reduced export reused the native result", !native.sameAs(reduced))
            for (y in 0 until source.height) {
                for (x in 0 until source.width) {
                    if (x !in 100 until 1050 || y !in 60 until 180) {
                        assertEquals(source.getPixel(x, y), reduced.getPixel(x, y))
                    }
                }
            }
            settings.setLamaThreads(0)
            withTimeout(10_000) { settings.settingsState.first { it.lamaThreads == 0 } }
            assertTrue(
                "Thread settings did not change the cache key",
                reducedFilter.cacheKey != SpotHealFilter(value).cacheKey
            )
            assertEquals(0, settings.getSettingsState().lamaThreads)
            assertTrue(settings.getSettingsState().lamaLimitResolution)
            native.recycle()
            reduced.recycle()
            expected.recycle()
        } finally {
            settings.setLamaThreads(original.lamaThreads)
            settings.setLamaLimitResolution(original.lamaLimitResolution)
            source.recycle()
            mask.recycle()
        }
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