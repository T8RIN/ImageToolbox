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

package com.t8rin.imagetoolbox.instrumentation

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.test.platform.app.InstrumentationRegistry
import com.t8rin.imagetoolbox.core.data.image.AndroidImageCompressor
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.ImageScaleMode
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.settings.domain.SettingsProvider
import com.t8rin.imagetoolbox.core.settings.domain.model.FilenameBehavior
import com.t8rin.imagetoolbox.core.settings.domain.model.SettingsState
import com.t8rin.imagetoolbox.feature.weight_resize.data.AndroidWeightImageScaler
import com.t8rin.imagetoolbox.image_cutting.data.AndroidImageCutter
import com.t8rin.imagetoolbox.image_cutting.domain.CutParams
import com.t8rin.imagetoolbox.image_cutting.domain.PivotPair
import com.t8rin.imagetoolbox.image_splitting.data.AndroidImageSplitter
import com.t8rin.imagetoolbox.image_splitting.domain.SplitParams
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.lang.reflect.Proxy
import kotlin.math.roundToInt

class ImageProcessingRegressionTest {
    @Test
    fun cutsPreservePixelsForBothAxesInverseAndRtl() = runBlocking {
        val image = pixels(20, 16)
        val cutter =
            AndroidImageCutter(testProxy { _, _ -> error("Unexpected decode") }, TestDispatchers)
        val bounds = listOf(
            null, PivotPair(0f, 1f), PivotPair(.2f, .65f),
            PivotPair(.2f, .65f, true), PivotPair(0f, .5f), PivotPair(.5f, 1f)
        )
        for (vertical in bounds) for (horizontal in bounds) {
            for (inverseVertical in listOf(false, true)) for (inverseHorizontal in listOf(
                false,
                true
            )) {
                val columns = retained(20, vertical, inverseVertical)
                val rows = retained(16, horizontal, inverseHorizontal)
                val result = cutter.cutAndMerge(
                    image,
                    CutParams(vertical, horizontal, inverseVertical, inverseHorizontal)
                )
                assertEquals(columns.size, result.width)
                assertEquals(rows.size, result.height)
                assertArrayEquals(rows.flatMap { y -> columns.map { x -> image.getPixel(x, y) } }
                    .toIntArray(), result.pixels())
                if (result !== image) result.recycle()
                assertFalse(image.isRecycled)
            }
        }
        image.recycle()
    }

    @Test
    fun splittingPreservesOrderUnevenBordersAndReleasesOwnedCells() = runBlocking {
        val image = pixels(7, 5)
        for ((rows, columns) in listOf(2 to 3, 1 to 3, 2 to 1, 20 to 20)) {
            val cells = mutableListOf<Bitmap>()
            val pixels = mutableListOf<IntArray>()
            val sizes = mutableListOf<Pair<Int, Int>>()
            val splitter = AndroidImageSplitter(
                testProxy { _, _ -> image },
                testProxy { name, args ->
                    check(name == "cacheImage")
                    val cell = args[0] as Bitmap
                    cells += cell
                    pixels += cell.pixels()
                    sizes += cell.width to cell.height
                    "cell:${cells.size}"
                }, TestDispatchers
            )
            val result = splitter.split(
                "source",
                SplitParams(
                    rows,
                    columns,
                    emptyList(),
                    emptyList(),
                    ImageFormat.Png.Lossless,
                    Quality.Base()
                )
            )
            val widths = parts(7, columns)
            val heights = parts(5, rows)
            assertEquals(widths.size * heights.size, result.size)
            var y = 0
            var index = 0
            for (height in heights) {
                var x = 0
                for (width in widths) {
                    assertEquals("cell:${index + 1}", result[index])
                    assertEquals(width to height, sizes[index])
                    val expected = IntArray(width * height) { i ->
                        image.getPixel(
                            x + i % width,
                            y + i / width
                        )
                    }
                    assertArrayEquals(expected, pixels[index++])
                    x += width
                }
                y += height
            }
            assertTrue(cells.all { it.isRecycled })
            assertFalse(image.isRecycled)
        }
        image.recycle()
    }

    @Test
    fun splittingRecyclesCellWhenEncodingIsCancelled() = runBlocking {
        val image = pixels(7, 5)
        var cell: Bitmap? = null
        val splitter = AndroidImageSplitter(testProxy { _, _ -> image }, testProxy { _, args ->
            cell = args[0] as Bitmap
            throw CancellationException("cancel")
        }, TestDispatchers)
        val failure = runCatching {
            splitter.split(
                "source",
                SplitParams(
                    2,
                    2,
                    emptyList(),
                    emptyList(),
                    ImageFormat.Png.Lossless,
                    Quality.Base()
                )
            )
        }.exceptionOrNull()
        assertTrue(failure is CancellationException)
        assertTrue(requireNotNull(cell).isRecycled)
        assertFalse(image.isRecycled)
        image.recycle()
    }

    @Test
    fun unchangedImageIsEncodedOnceAndSaveIfSmallerIsRespected() = runBlocking {
        val image = pixels(8, 8)
        val calls = mutableListOf<ImageInfo>()
        val scaler = scaler(calls) { 500 }
        assertNotNull(
            scaler.scaleByMaxBytes(
                image,
                ImageFormat.Jpg,
                ImageScaleMode.Default,
                4096,
                true
            )
        )
        assertEquals(1, calls.size)
        assertNull(
            scaler.scaleByMaxBytes(
                image,
                ImageFormat.Jpg,
                ImageScaleMode.Default,
                4096,
                false
            )
        )
        assertEquals(2, calls.size)
        image.recycle()
    }

    @Test
    fun qualitySearchKeepsHighestFitEvenWithNonMonotonicSizes() = runBlocking {
        val image = pixels(8, 8)
        val calls = mutableListOf<ImageInfo>()
        val scaler =
            scaler(calls) { if (it.quality.qualityValue == 97 || it.quality.qualityValue < 40) 900 else 3000 }
        val (bytes, info) = requireNotNull(
            scaler.scaleByMaxBytes(
                image,
                ImageFormat.Jpg,
                ImageScaleMode.Default,
                3072,
                true
            )
        )
        assertEquals(97, info.quality.qualityValue)
        assertEquals(900, bytes.size)
        assertEquals(listOf(100, 99, 98, 97), calls.map { it.quality.qualityValue })
        image.recycle()
    }

    @Test
    fun fixedQualityIsNotEncoded86TimesAndScalingModeIsPreserved() = runBlocking {
        val image = pixels(16, 16)
        for (format in listOf(
            ImageFormat.Jxl.Lossy,
            ImageFormat.Avif.LossyAv1,
            ImageFormat.Png.ImageQuant
        )) {
            val calls = mutableListOf<ImageInfo>()
            val mode = ImageScaleMode.Nearest
            val scaler = scaler(calls) { if (it.width == 16) 3000 else 900 }
            val result = requireNotNull(scaler.scaleByMaxBytes(image, format, mode, 3072, true))
            assertEquals(2, calls.size)
            assertEquals(15, result.second.width)
            assertTrue(calls.all { it.imageScaleMode == mode })
        }
        image.recycle()
    }

    @Test
    fun impossibleSizeTerminatesAndTinyDimensionsKeepShrinking() = runBlocking {
        val image = pixels(7, 1)
        val calls = mutableListOf<ImageInfo>()
        val result = scaler(calls) { 3000 }.scaleByMaxBytes(
            image,
            ImageFormat.Png.Lossless,
            ImageScaleMode.Default,
            3072,
            true
        )
        assertNull(result)
        assertEquals((7 downTo 1).toList(), calls.map { it.width })
        assertTrue(calls.all { it.height == 1 })
        image.recycle()
    }

    @Test
    fun jpegQualityMatchesDescendingReferenceBytes() = runBlocking {
        val image = pixels(128, 128)
        val source = jpeg(image, 100)
        val target = jpeg(image, 63).size.toLong()
        val maxBytes = target + 2048 * (source.size.toLong() / (5 * 1024 * 1024)).coerceAtLeast(1)
        val expected = (100 downTo 15).first { jpeg(image, it).size <= target }
        val compressor = testProxy<ImageCompressor<Bitmap>> { name, args ->
            check(name == "compressAndTransform")
            jpeg(args[0] as Bitmap, (args[1] as ImageInfo).quality.qualityValue)
        }
        val scaler = AndroidWeightImageScaler(
            testProxy { _, _ -> error("Unexpected scaling") },
            compressor,
            TestDispatchers
        )
        val actual = requireNotNull(
            scaler.scaleByMaxBytes(
                image,
                ImageFormat.Jpg,
                ImageScaleMode.Default,
                maxBytes,
                true
            )
        )
        assertArrayEquals(jpeg(image, expected), actual.first)
        assertEquals(expected, actual.second.quality.qualityValue)
        image.recycle()
    }

    @Test
    fun productionJpegAndPngEncodersPreserveQualityAndResizedPixels() = runBlocking {
        val image = pixels(128, 128)
        val imageScaler = testProxy<ImageScaler<Bitmap>> { _, args ->
            Bitmap.createScaledBitmap(args[0] as Bitmap, args[1] as Int, args[2] as Int, false)
        }
        val settings = object : SettingsProvider {
            override val settingsState =
                MutableStateFlow(SettingsState.Default.copy(filenameBehavior = FilenameBehavior.None()))

            override suspend fun getSettingsState() = settingsState.value
        }
        val compressor = AndroidImageCompressor(
            InstrumentationRegistry.getInstrumentation().targetContext,
            testProxy { _, args -> args[0] }, imageScaler,
            testProxy { _, _ -> error("Unexpected source access") }, settings, TestDispatchers
        )
        val scaler = AndroidWeightImageScaler(imageScaler, compressor, TestDispatchers)
        val targetBytes = compressor.compress(image, ImageFormat.Jpg, Quality.Base(65)).size
        val initialSize = compressor.compress(image, ImageFormat.Jpg, Quality.Base()).size
        val maxBytes =
            targetBytes + 2048 * (initialSize.toLong() / (5 * 1024 * 1024)).coerceAtLeast(1)
        val expectedQuality = (100 downTo 15).first {
            compressor.compress(image, ImageFormat.Jpg, Quality.Base(it)).size <= targetBytes
        }
        val jpeg = requireNotNull(
            scaler.scaleByMaxBytes(
                image,
                ImageFormat.Jpg,
                ImageScaleMode.Nearest,
                maxBytes,
                true
            )
        )
        assertEquals(expectedQuality, jpeg.second.quality.qualityValue)
        assertArrayEquals(
            compressor.compress(
                image,
                ImageFormat.Jpg,
                Quality.Base(expectedQuality)
            ), jpeg.first
        )
        val png = requireNotNull(
            scaler.scaleByMaxBytes(
                image,
                ImageFormat.Png.Lossless,
                ImageScaleMode.Nearest,
                3072,
                true
            )
        )
        assertTrue(png.first.size <= 1024)
        val decoded = requireNotNull(BitmapFactory.decodeByteArray(png.first, 0, png.first.size))
        val expected = Bitmap.createScaledBitmap(image, png.second.width, png.second.height, false)
        assertTrue(expected.sameAs(decoded))
        decoded.recycle()
        if (expected !== image) expected.recycle()
        image.recycle()
    }

    private fun scaler(calls: MutableList<ImageInfo>, size: (ImageInfo) -> Int) =
        AndroidWeightImageScaler(
            testProxy<ImageScaler<Bitmap>> { _, _ -> error("No bitmap should be allocated just to get its dimensions") },
            testProxy<ImageCompressor<Bitmap>> { name, args ->
                check(name == "compressAndTransform")
                val info = args[1] as ImageInfo
                calls += info
                ByteArray(size(info))
            }, TestDispatchers
        )

    private fun retained(size: Int, pair: PivotPair?, inverse: Boolean): List<Int> {
        if (pair == null || pair == PivotPair(0f, 1f)) return (0 until size).toList()
        val start = (pair.startRtlAdjusted * size).roundToInt()
        val end = (pair.endRtlAdjusted * size).roundToInt()
        return (0 until size).filter { (it in start until end) == inverse }
    }

    private fun parts(size: Int, count: Int): List<Int> {
        val count = count.coerceAtMost(size)
        return List(count) { size / count + if (it == count - 1) size % count else 0 }
    }
}

internal object TestDispatchers : DispatchersHolder {
    override val uiDispatcher = Dispatchers.Main.immediate
    override val defaultDispatcher = Dispatchers.Default
    override val ioDispatcher = Dispatchers.IO
    override val encodingDispatcher = Dispatchers.Default
    override val decodingDispatcher = Dispatchers.Default
}

internal inline fun <reified T> testProxy(crossinline call: (String, Array<out Any?>) -> Any?): T =
    Proxy.newProxyInstance(T::class.java.classLoader, arrayOf(T::class.java)) { _, method, args ->
        call(method.name, args.orEmpty())
    } as T

internal fun pixels(width: Int, height: Int): Bitmap =
    Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
        val colors = IntArray(width * height) { i -> (0x80 shl 24) or ((i * 1234567) and 0xffffff) }
        setPixels(colors, 0, width, 0, 0, width, height)
    }

internal fun Bitmap.pixels(): IntArray =
    IntArray(width * height).also { getPixels(it, 0, width, 0, 0, width, height) }

internal fun jpeg(bitmap: Bitmap, quality: Int): ByteArray = ByteArrayOutputStream().apply {
    check(bitmap.compress(Bitmap.CompressFormat.JPEG, quality, this))
}.toByteArray()