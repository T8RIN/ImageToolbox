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
import android.graphics.ColorSpace
import android.graphics.Gainmap
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.toBitmap
import com.radzivon.bartoshyk.avif.coder.Coder
import com.radzivon.bartoshyk.avif.coder.ScaleMode
import com.t8rin.imagetoolbox.core.data.coil.HeifDecoder
import com.t8rin.imagetoolbox.core.domain.image.model.AvifChromaSubsampling
import com.t8rin.imagetoolbox.core.domain.image.model.HeicChromaSubsampling
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.nio.ByteBuffer

@RunWith(AndroidJUnit4::class)
class AvifBackendInstrumentedTest {

    @Test
    fun av1AndAv2EncodeAtEveryEffortIncludingExtraSlow() = runBlocking {
        val source = image()
        try {
            listOf(true, false).forEach { av1 ->
                (0..3).forEach { effort ->
                    val bytes = AvifBackend(isLossless = false, isAv1 = av1).compress(
                        source, Quality.Avif(qualityValue = 85, effort = effort)
                    )
                    val decoded = Coder().decode(bytes)
                    try {
                        assertEquals("av1=$av1, effort=$effort", source.width, decoded.width)
                        assertEquals(source.height, decoded.height)
                        assertEquals(0, Color.alpha(decoded.getPixel(0, 0)))
                        assertEquals(255, Color.alpha(decoded.getPixel(16, 16)))
                        assertColorNear(source.getPixel(16, 16), decoded.getPixel(16, 16), 30)
                        if (Build.VERSION.SDK_INT >= 34) assertFalse(decoded.hasGainmap())
                    } finally {
                        decoded.recycleWithGainmap()
                    }
                }
            }
        } finally {
            source.recycle()
        }
    }

    @Test
    fun losslessAv1PreservesColorsAndAlpha() = runBlocking {
        val source = image()
        try {
            val bytes = AvifBackend(isLossless = true, isAv1 = true).compress(
                source,
                Quality.Avif(chromaSubsampling = AvifChromaSubsampling.Lossless)
            )
            val decoded = Coder().decode(bytes)
            try {
                for (y in 0 until source.height) for (x in 0 until source.width) {
                    val expected = source.getPixel(x, y)
                    val actual = decoded.getPixel(x, y)
                    assertEquals("alpha at $x,$y", Color.alpha(expected), Color.alpha(actual))
                    if (Color.alpha(expected) == 255) assertColorNear(expected, actual, 2)
                }
            } finally {
                decoded.recycleWithGainmap()
            }
        } finally {
            source.recycle()
        }
    }

    @Test
    fun heicLossyAndLosslessStillDecode() = runBlocking {
        val source = image()
        try {
            listOf(true, false).forEach { lossless ->
                val bytes = HeicBackend(isLossless = lossless).compress(
                    source,
                    Quality.Heic(
                        qualityValue = 90,
                        chromaSubsampling = HeicChromaSubsampling.Yuv444,
                        useGbrLossless = true
                    )
                )
                val decoded = Coder().decode(bytes)
                try {
                    assertEquals(source.width, decoded.width)
                    assertEquals(source.height, decoded.height)
                    assertColorNear(source.getPixel(16, 16), decoded.getPixel(16, 16), 15)
                } finally {
                    decoded.recycleWithGainmap()
                }
            }
        } finally {
            source.recycle()
        }
    }

    @Test
    @SdkSuppress(minSdkVersion = 34)
    fun av1PreservesRgbGainmapMetadataAndSampledDecoding() = runBlocking {
        val source = gainmapImage(rgb = true)
        source.gainmap!!.setEpsilonSdr(0.01f, 0.02f, 0.03f)
        source.gainmap!!.setEpsilonHdr(0.02f, 0.03f, 0.04f)
        try {
            val bytes = AvifBackend(isLossless = false, isAv1 = true).compress(
                source, Quality.Avif()
            )
            val decoded = Coder().decode(bytes)
            try {
                assertTrue(decoded.hasGainmap())
                assertArrayEquals(source.gainmap!!.ratioMax, decoded.gainmap!!.ratioMax, 0.001f)
                assertArrayEquals(source.gainmap!!.gamma, decoded.gainmap!!.gamma, 0.001f)
                assertArrayEquals(
                    source.gainmap!!.epsilonSdr,
                    decoded.gainmap!!.epsilonSdr,
                    0.0001f
                )
                assertArrayEquals(
                    source.gainmap!!.epsilonHdr,
                    decoded.gainmap!!.epsilonHdr,
                    0.0001f
                )
                val pixel = decoded.gainmap!!.gainmapContents.getPixel(4, 4)
                assertTrue(Color.red(pixel) < Color.green(pixel))
                assertTrue(Color.green(pixel) < Color.blue(pixel))
            } finally {
                decoded.recycleWithGainmap()
            }
            val direct = ByteBuffer.allocateDirect(bytes.size).put(bytes).apply { rewind() }
            val sampled = Coder().decodeSampled(direct, 16, 12)
            try {
                assertEquals(16, sampled.width)
                assertEquals(12, sampled.height)
                assertTrue(sampled.hasGainmap())
            } finally {
                sampled.recycleWithGainmap()
            }
        } finally {
            source.recycleWithGainmap()
        }
    }

    @Test
    @SdkSuppress(minSdkVersion = 34)
    fun heicPreservesAppleCompatibleGainmapAndCropsItWithTheBase() = runBlocking {
        val source = gainmapImage(rgb = false)
        source.gainmap!!.setGamma(1f, 1f, 1f)
        try {
            val bytes = HeicBackend(isLossless = false).compress(
                source, Quality.Heic()
            )
            val decoded = Coder().decodeSampled(bytes, 8, 24, scaleMode = ScaleMode.FILL)
            try {
                assertEquals(8, decoded.width)
                assertEquals(24, decoded.height)
                assertTrue(decoded.hasGainmap())
                assertEquals(4f, decoded.gainmap!!.displayRatioForFullHdr, 0.001f)
                assertEquals(8, decoded.gainmap!!.gainmapContents.width)
                assertEquals(24, decoded.gainmap!!.gainmapContents.height)
            } finally {
                decoded.recycleWithGainmap()
            }
        } finally {
            source.recycleWithGainmap()
        }
    }

    @Test
    @SdkSuppress(minSdkVersion = 34)
    fun unsupportedHeicGainmapIsSkippedWithoutMutatingTheSource() = runBlocking {
        val source = gainmapImage(rgb = true)
        try {
            val bytes = HeicBackend(isLossless = false).compress(source, Quality.Heic())
            val decoded = Coder().decode(bytes)
            try {
                assertFalse(decoded.hasGainmap())
                assertTrue(source.hasGainmap())
            } finally {
                decoded.recycle()
            }
            assertTrue(source.hasGainmap())
        } finally {
            source.recycleWithGainmap()
        }
    }

    @Test
    @SdkSuppress(minSdkVersion = 34)
    fun av2SkipsUnsupportedGainmapsWithoutMutatingTheSource() = runBlocking {
        val source = gainmapImage(rgb = true)
        try {
            val bytes =
                AvifBackend(isLossless = false, isAv1 = false).compress(source, Quality.Avif())
            val decoded = Coder().decode(bytes)
            try {
                assertFalse(decoded.hasGainmap())
            } finally {
                decoded.recycle()
            }
            assertTrue(source.hasGainmap())
        } finally {
            source.recycleWithGainmap()
        }
    }

    @Test
    @SdkSuppress(minSdkVersion = 34)
    fun appDecoderKeepsGainmapWhenLoadingASampledAvif() = runBlocking {
        val source = gainmapImage(rgb = true)
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val loader = ImageLoader.Builder(context).components { add(HeifDecoder.Factory()) }.build()
        try {
            val bytes =
                AvifBackend(isLossless = false, isAv1 = true).compress(source, Quality.Avif())
            val result = loader.execute(
                ImageRequest.Builder(context).data(bytes).size(16, 12).allowHardware(false).build()
            )
            assertTrue(result.toString(), result is SuccessResult)
            val bitmap = requireNotNull(result.image).toBitmap()
            try {
                assertEquals(16, bitmap.width)
                assertEquals(12, bitmap.height)
                assertTrue(bitmap.hasGainmap())
            } finally {
                bitmap.recycleWithGainmap()
            }
        } finally {
            loader.shutdown()
            source.recycleWithGainmap()
        }
    }

    @Test
    @SdkSuppress(minSdkVersion = 36)
    fun hdrDirectionAndAlternativePrimariesSurviveAvifRoundTrip() = runBlocking {
        val source = gainmapImage(rgb = true)
        source.gainmap!!.gainmapDirection = Gainmap.GAINMAP_DIRECTION_HDR_TO_SDR
        source.gainmap!!.alternativeImagePrimaries = ColorSpace.get(ColorSpace.Named.DISPLAY_P3)
        try {
            val bytes = AvifBackend(isLossless = false, isAv1 = true).compress(
                source, Quality.Avif()
            )
            val decoded = Coder().decode(bytes)
            try {
                assertTrue(decoded.hasGainmap())
                assertEquals(
                    Gainmap.GAINMAP_DIRECTION_HDR_TO_SDR,
                    decoded.gainmap!!.gainmapDirection
                )
                assertEquals(
                    ColorSpace.Named.DISPLAY_P3.ordinal,
                    decoded.gainmap!!.alternativeImagePrimaries!!.id
                )
            } finally {
                decoded.recycleWithGainmap()
            }
        } finally {
            source.recycleWithGainmap()
        }
    }

    @Test
    @SdkSuppress(minSdkVersion = 34)
    fun avifKeepsHighBitDepthGainmaps() = runBlocking {
        val source = gainmapImage(rgb = true)
        val oldContents = source.gainmap!!.gainmapContents
        source.gainmap!!.gainmapContents =
            Bitmap.createBitmap(16, 12, Bitmap.Config.RGBA_F16).apply {
                eraseColor(Color.rgb(64, 128, 192))
            }
        oldContents.recycle()
        try {
            val bytes =
                AvifBackend(isLossless = false, isAv1 = true).compress(source, Quality.Avif())
            val decoded = Coder().decode(bytes)
            try {
                assertTrue(decoded.hasGainmap())
                assertEquals(Bitmap.Config.RGBA_F16, decoded.gainmap!!.gainmapContents.config)
            } finally {
                decoded.recycleWithGainmap()
            }
        } finally {
            source.recycleWithGainmap()
        }
    }

    @Test
    fun existingAvifFileStillDecodes() {
        // HEIC metadata fixtures contain uncompressed HEIF items, not HEVC images.
        val bytes = InstrumentationRegistry.getInstrumentation().context.assets
            .open("metadata_round_trip/rich.avif").use { it.readBytes() }
        val size = requireNotNull(Coder().getSize(bytes))
        val decoded = Coder().decode(bytes)
        try {
            assertEquals(size.width, decoded.width)
            assertEquals(size.height, decoded.height)
        } finally {
            decoded.recycleWithGainmap()
        }
    }

    private fun image(): Bitmap = Bitmap.createBitmap(32, 24, Bitmap.Config.ARGB_8888).apply {
        for (y in 0 until height) for (x in 0 until width) {
            setPixel(
                x, y, when {
                    x < 4 -> Color.TRANSPARENT
                    x < 8 -> Color.argb(128, 255, 0, 0)
                    else -> Color.rgb(80 + x * 3, 70 + y * 4, 90)
                }
            )
        }
    }

    private fun gainmapImage(rgb: Boolean): Bitmap = image().apply {
        eraseColor(Color.rgb(90, 100, 110))
        gainmap = Gainmap(Bitmap.createBitmap(16, 12, Bitmap.Config.ARGB_8888).apply {
            eraseColor(if (rgb) Color.rgb(64, 128, 192) else Color.GRAY)
        }).apply {
            setRatioMax(4f, 4f, 4f)
            setGamma(0.75f, 1f, 1.25f)
            displayRatioForFullHdr = 4f
        }
    }

    private fun assertColorNear(expected: Int, actual: Int, tolerance: Int) {
        listOf(16, 8, 0).forEach { shift ->
            assertEquals(
                "channel $shift", ((expected shr shift) and 255).toFloat(),
                ((actual shr shift) and 255).toFloat(), tolerance.toFloat()
            )
        }
    }

    private fun Bitmap.recycleWithGainmap() {
        if (Build.VERSION.SDK_INT >= 34 && hasGainmap()) gainmap?.gainmapContents?.recycle()
        recycle()
    }
}