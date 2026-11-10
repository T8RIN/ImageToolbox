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

package com.t8rin.imagetoolbox.feature.filters.data.model

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
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
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterValues
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.SpotHealMode
import com.t8rin.imagetoolbox.core.settings.di.SettingsStateEntryPoint
import com.t8rin.neural_tools.inpaint.LaMaProcessor
import java.io.File
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SpotHealFilterTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val fixtures = File(context.filesDir, "neural-tests")

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
        val filter = SpotHealFilter(
            FilterValues.spotHeal(
                mask = ImageModel(mask),
                mode = SpotHealMode.LaMa
            )
        ).toCoil()
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
        val value = FilterValues.spotHeal(mask = ImageModel(mask), mode = SpotHealMode.LaMa)
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

    private fun fixture(name: String): File = File(fixtures, name).also {
        assumeTrue(
            "Copy neural-model fixtures to ${fixtures.absolutePath} before this test",
            it.isFile
        )
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