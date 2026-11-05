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
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import coil3.DrawableImage
import coil3.ImageLoader
import coil3.decode.DataSource
import coil3.decode.ImageSource
import coil3.fetch.SourceFetchResult
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.Options
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.size.Precision
import coil3.size.Size
import coil3.toBitmap
import kotlinx.coroutines.runBlocking
import okio.Buffer
import okio.ByteString.Companion.decodeHex
import okio.FileSystem
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import oupson.apng.drawable.ApngDrawable
import oupson.apng.encoder.ApngEncoder
import java.io.ByteArrayOutputStream

@RunWith(AndroidJUnit4::class)
class AnimatedPngDecoderFactoryTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun routingReadsChunkHeadersWithoutConsumingTheInput() {
        val loader = ImageLoader.Builder(context).build()
        val signature = "89504e470d0a1a0a".decodeHex()
        fun png(vararg chunks: Pair<String, ByteArray>) = Buffer().apply {
            write(signature)
            chunks.forEach { (type, data) ->
                writeInt(data.size)
                writeUtf8(type)
                write(data)
                writeInt(0)
            }
        }.readByteArray()

        val header = "IHDR" to ByteArray(13)
        val image = "IDAT" to ByteArray(32)
        try {
            listOf(
                byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte()) to false,
                signature.toByteArray().copyOf(4) to false,
                png(header, image) to false,
                png(header, "tEXt" to "acTL in metadata".toByteArray(), image) to false,
                png(header, "acTL" to ByteArray(8), image) to true,
                png(header, "tEXt" to ByteArray(70_000), "acTL" to ByteArray(8), image) to true,
                Buffer().write(signature).writeInt(Int.MAX_VALUE).writeUtf8("tEXt")
                    .readByteArray() to true
            ).forEach { (bytes, animated) ->
                ImageSource(Buffer().write(bytes), FileSystem.SYSTEM).use { source ->
                    val decoder = AnimatedPngDecoderFactory.create(
                        SourceFetchResult(source, "image/png", DataSource.MEMORY),
                        Options(context), loader
                    )
                    assertEquals(animated, decoder != null)
                    assertArrayEquals(bytes, source.source().readByteArray())
                }
            }
        } finally {
            loader.shutdown()
        }
    }

    @Test
    fun staticPngKeepsOriginalPixelsAndSamplesThumbnails() = runBlocking {
        val source = Bitmap.createBitmap(2048, 1536, Bitmap.Config.ARGB_8888)
        val row = IntArray(source.width) { x ->
            if (x < source.width / 2) Color.argb(128, 255, 0, 0) else Color.BLUE
        }
        repeat(source.height) { y -> source.setPixels(row, 0, source.width, 0, y, source.width, 1) }
        val bytes = ByteArrayOutputStream().apply {
            source.compress(Bitmap.CompressFormat.PNG, 100, this)
        }.toByteArray()
        source.recycle()
        val expected = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        val loader =
            ImageLoader.Builder(context).components { add(AnimatedPngDecoderFactory) }.build()
        try {
            for (size in listOf(Size.ORIGINAL, Size(64, 64))) {
                val result = loader.execute(
                    ImageRequest.Builder(context).data(bytes).size(size)
                        .precision(Precision.INEXACT).allowHardware(false)
                        .memoryCachePolicy(CachePolicy.DISABLED).build()
                )
                assertTrue(result.toString(), result is SuccessResult)
                val bitmap = requireNotNull(result.image).toBitmap()
                try {
                    if (size == Size.ORIGINAL) {
                        assertTrue(expected.sameAs(bitmap))
                    } else {
                        assertTrue(bitmap.width in 64..128)
                        assertEquals(bitmap.width * 3 / 4, bitmap.height)
                        assertTrue((result as SuccessResult).isSampled)
                        assertEquals(expected.getPixel(10, 10), bitmap.getPixel(2, 2))
                        assertEquals(Color.BLUE, bitmap.getPixel(bitmap.width - 2, 2))
                    }
                } finally {
                    bitmap.recycle()
                }
            }
        } finally {
            expected.recycle()
            loader.shutdown()
        }
    }

    @Test
    fun apngKeepsFramesTimingAndSampledSize() = runBlocking {
        val output = ByteArrayOutputStream()
        val encoder = ApngEncoder(output, 32, 16, 2).apply { setOptimiseApng(false) }
        val colors = listOf(Color.RED, Color.BLUE)
        val durations = listOf(120, 230)
        colors.forEachIndexed { index, color ->
            val frame =
                Bitmap.createBitmap(32, 16, Bitmap.Config.ARGB_8888).apply { eraseColor(color) }
            try {
                encoder.writeFrame(frame, durations[index].toFloat())
            } finally {
                frame.recycle()
            }
        }
        encoder.writeEnd()
        val loader =
            ImageLoader.Builder(context).components { add(AnimatedPngDecoderFactory) }.build()
        try {
            for (size in listOf(Size.ORIGINAL, Size(8, 8))) {
                val result = loader.execute(
                    ImageRequest.Builder(context).data(output.toByteArray()).size(size)
                        .allowHardware(false).memoryCachePolicy(CachePolicy.DISABLED).build()
                )
                assertTrue(result.toString(), result is SuccessResult)
                val drawable = (result.image as DrawableImage).drawable as ApngDrawable
                assertEquals(2, drawable.numberOfFrames)
                repeat(drawable.numberOfFrames) { index ->
                    val frame = (drawable.getFrame(index) as BitmapDrawable).bitmap
                    assertEquals(if (size == Size.ORIGINAL) 32 else 8, frame.width)
                    assertEquals(frame.width / 2, frame.height)
                    assertEquals(durations[index], drawable.getDuration(index))
                    assertEquals(colors[index], frame.getPixel(2, 2))
                    frame.recycle()
                }
            }
        } finally {
            loader.shutdown()
        }
    }
}