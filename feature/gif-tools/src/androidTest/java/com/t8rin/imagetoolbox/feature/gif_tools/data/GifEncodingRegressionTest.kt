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

package com.t8rin.imagetoolbox.feature.gif_tools.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.core.net.toUri
import androidx.test.platform.app.InstrumentationRegistry
import com.t8rin.gif_converter.GifDecoder
import com.t8rin.gif_converter.GifEncoder
import com.t8rin.imagetoolbox.core.data.saving.io.FileWriteable
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.saving.FailureNotifier
import com.t8rin.imagetoolbox.core.domain.saving.io.Writeable
import com.t8rin.imagetoolbox.core.domain.saving.io.use
import com.t8rin.imagetoolbox.feature.gif_tools.domain.GifMergeItem
import com.t8rin.imagetoolbox.feature.gif_tools.domain.GifMergeParams
import com.t8rin.imagetoolbox.feature.gif_tools.domain.GifParams
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.io.File
import java.lang.reflect.Proxy

class GifEncodingRegressionTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private var decodes = 0
    private var serial = 0
    private val files = mutableListOf<File>()
    private val getter = proxy<ImageGetter<Bitmap>> { name, args ->
        check(name == "getImage")
        val uri = args[0] as? String
        if (uri == null) null else {
            decodes++
            context.contentResolver.openInputStream(uri.toUri())
                .use { BitmapFactory.decodeStream(it) }
        }
    }
    private val share = proxy<ImageShareProvider<Bitmap>> { name, args ->
        check(name == "cacheDataOrThrow")
        val file = file("result-${serial++}.gif")

        @Suppress("UNCHECKED_CAST")
        val write = args[1] as suspend (Writeable) -> Unit
        FileWriteable(file).use { output -> runBlocking { write(output) } }
        file.toUri().toString()
    }
    private val failure = proxy<FailureNotifier> { _, args -> throw (args[0] as Throwable) }
    private fun converter() = AndroidGifConverter(getter, share, context, failure, Dispatchers)

    @Test
    fun crossfadePreservesFrameCountAndDecodesEachInputOnce() = runBlocking {
        try {
            val uris = List(3) { index ->
                val bitmap = bitmap(32 + index * 8, 24 + index * 8, index)
                file("source-$index.png").also { file ->
                    file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
                    bitmap.recycle()
                }.toUri().toString()
            }
            val params = GifParams.Default.copy(crossfadeCount = 6, dontStack = true)
            val output = converter().createGifFromImageUris(uris, params, { throw it }, {})!!
            assertEquals(3, decodes)
            val decoder = GifDecoder().apply { read(read(output)) }
            assertEquals(19, decoder.frameCount)
            repeat(decoder.frameCount) {
                decoder.advance()
                assertNotNull(decoder.nextFrame)
                assertEquals(80, decoder.nextDelay)
            }
        } finally {
            files.forEach(File::delete)
        }
    }

    @Test
    fun forwardReverseAndBoomerangPreservePixelsAndFrameDelays() = runBlocking {
        try {
            val colors =
                listOf(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA, Color.CYAN)
            val inputs = List(2) { clip ->
                file("clip-$clip.gif").also { file ->
                    file.outputStream().use { output ->
                        val encoder = GifEncoder().setRepeat(2).setTransparent(0)
                        encoder.start(output)
                        repeat(6) { frame ->
                            val bitmap = Bitmap.createBitmap(
                                32 + clip * 16,
                                24 + clip * 8,
                                Bitmap.Config.ARGB_8888
                            )
                                .apply { eraseColor(colors[frame]) }
                            encoder.setDelay(100 + frame * 20).addFrame(bitmap)
                            bitmap.recycle()
                        }
                        encoder.finish()
                    }
                }.toUri().toString()
            }
            for (reverse in listOf(false, true)) for (boomerang in listOf(false, true)) {
                val items =
                    inputs.map { GifMergeItem(it, reverse = reverse, boomerang = boomerang) }
                val params = GifMergeParams(transitionDelayMillis = 70, repeatCount = 3)
                val output = converter().mergeGifs(items, params, { throw it }, {})!!
                val ordered =
                    if (reverse) colors.indices.reversed().toList() else colors.indices.toList()
                val indexes = if (boomerang) ordered + ordered.dropLast(1).reversed() else ordered
                val decoder = GifDecoder().apply { read(read(output)) }
                assertEquals(indexes.size * 2, decoder.frameCount)
                repeat(2) { clip ->
                    indexes.forEachIndexed { index, original ->
                        decoder.advance()
                        val bitmap = requireNotNull(decoder.nextFrame)
                        assertEquals(
                            colors[original],
                            bitmap.getPixel(bitmap.width / 2, bitmap.height / 2)
                        )
                        assertEquals(
                            100 + original * 20 + if (clip == 0 && index == indexes.lastIndex) 70 else 0,
                            decoder.nextDelay
                        )
                    }
                }
            }
        } finally {
            files.forEach(File::delete)
        }
    }

    private fun bitmap(width: Int, height: Int, seed: Int) =
        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
            val pixels = IntArray(width * height) { index ->
                val alpha = (index * 29 + seed * 13) and 255
                (alpha shl 24) or (((index * 17 + seed * 71) and 255) shl 16) or
                        (((index * 11 + seed * 47) and 255) shl 8) or ((index * 31 + seed) and 255)
            }
            setPixels(pixels, 0, width, 0, 0, width, height)
        }

    private fun file(name: String) = File(context.cacheDir, "gif-regression-$name").also(files::add)
    private fun read(uri: String) =
        context.contentResolver.openInputStream(uri.toUri())!!.use { it.readBytes() }

    private inline fun <reified T> proxy(crossinline call: (String, Array<out Any?>) -> Any?): T =
        Proxy.newProxyInstance(
            T::class.java.classLoader,
            arrayOf(T::class.java)
        ) { _, method, args ->
            call(method.name, args.orEmpty())
        } as T

    private object Dispatchers : DispatchersHolder {
        override val uiDispatcher = kotlinx.coroutines.Dispatchers.Main
        override val ioDispatcher = kotlinx.coroutines.Dispatchers.IO
        override val encodingDispatcher = kotlinx.coroutines.Dispatchers.Default
        override val decodingDispatcher = kotlinx.coroutines.Dispatchers.Default
        override val defaultDispatcher = kotlinx.coroutines.Dispatchers.Default
    }
}