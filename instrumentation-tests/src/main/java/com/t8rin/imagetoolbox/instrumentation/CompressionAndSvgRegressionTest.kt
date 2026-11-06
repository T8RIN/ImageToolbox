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
import android.net.Uri
import androidx.test.platform.app.InstrumentationRegistry
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.destroy
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.model.ImageData
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.feature.compression_lab.presentation.screenLogic.CompressionLabComponent
import com.t8rin.imagetoolbox.feature.compression_lab.presentation.screenLogic.CompressionSearchMode
import com.t8rin.imagetoolbox.feature.svg_maker.data.AndroidSvgManager
import com.t8rin.imagetoolbox.feature.svg_maker.domain.SvgParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import javax.xml.parsers.DocumentBuilderFactory

class CompressionAndSvgRegressionTest {
    @Test
    fun compressionSearchCachesOnlyWinnerAndSavingAvoidsIntermediateFiles() = runBlocking {
        val image = pixels(256, 256)
        val lifecycle = LifecycleRegistry()
        val cached = ConcurrentHashMap<String, ByteArray>()
        val encodes = AtomicInteger()
        val reads = AtomicInteger()
        val saves = mutableListOf<ImageSaveTarget>()
        val source = Uri.parse("file:///source.jpg")
        val other = Uri.parse("file:///other.jpg")
        val getter = testProxy<ImageGetter<Bitmap>> { name, args ->
            check(name == "getImage")
            when (val data = args[0]) {
                is ByteArray -> BitmapFactory.decodeByteArray(data, 0, data.size)
                is String -> ImageData(image, ImageInfo(width = image.width, height = image.height))
                is Uri -> cached.getValue(data.toString())
                    .let { BitmapFactory.decodeByteArray(it, 0, it.size) }

                else -> error("Unexpected input $data")
            }
        }
        val component = withContext(Dispatchers.Main) {
            CompressionLabComponent(
                DefaultComponentContext(lifecycle), null, {}, {}, getter,
                testProxy { name, args ->
                    check(name == "compress")
                    encodes.incrementAndGet()
                    jpeg(args[0] as Bitmap, (args[2] as Quality).qualityValue)
                },
                testProxy { name, args ->
                    when (name) {
                        "readBytes" -> {
                            reads.incrementAndGet(); cached.getValue(args[0] as String)
                        }

                        "save" -> {
                            saves += args[0] as ImageSaveTarget; SaveResult.Skipped()
                        }

                        else -> error("Unexpected file operation: $name")
                    }
                },
                testProxy { name, args ->
                    check(name == "cacheByteArray")
                    val uri = "file:///cache-${cached.size}.jpg"
                    cached[uri] = args[0] as ByteArray
                    uri
                },
                testProxy { _, _ -> "image.jpg" }, TestDispatchers
            ).apply {
                CompressionLabComponent.DefaultFormats.drop(1).forEach(::toggleFormat)
                setSearchMode(CompressionSearchMode.TargetSize)
                setTargetSizeKb(12)
                setUris(listOf(source, other))
            }
        }
        try {
            await { component.results.size == 1 && !component.isImageLoading }
            assertTrue(encodes.get() > 1)
            assertEquals(1, cached.size)
            val profile = component.results.single()
            val bytes = cached.getValue(profile.uri.toString())
            assertEquals(bytes.size.toLong(), profile.sizeBytes)
            assertTrue(bytes.size <= 12 * 1024)
            assertTrue(profile.targetSatisfied)
            withContext(Dispatchers.Main) { component.saveResults(null) }
            await { saves.size == 2 && !component.isSaving }
            assertEquals(1, cached.size)
            assertEquals(1, reads.get())
            assertArrayEquals(bytes, saves[0].data)
            assertArrayEquals(bytes, saves[1].data)
            var shared: List<Uri>? = null
            withContext(Dispatchers.Main) { component.cacheResults { shared = it } }
            await { shared != null }
            assertEquals(profile.uri, requireNotNull(shared).first())
            assertEquals(2, cached.size)
            for (mode in listOf(
                CompressionSearchMode.TargetQuality,
                CompressionSearchMode.Manual
            )) {
                val before = cached.size
                withContext(Dispatchers.Main) {
                    component.setSearchMode(mode)
                    component.runLab()
                }
                await { component.results.size == 1 && !component.isImageLoading }
                assertEquals(before + 1, cached.size)
                assertTrue(component.results.single().ssim.isFinite())
            }
        } finally {
            withContext(Dispatchers.Main) { lifecycle.destroy() }
            image.recycle()
        }
    }

    @Test
    fun svgPresetsProduceValidDeterministicUtf8AndContinueAfterBadInput() = runBlocking {
        val image = pixels(20, 16)
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val filesBefore = context.cacheDir.listFiles().orEmpty().map { it.name }
            .filter { it.startsWith("vector_") }.toSet()
        val manager = AndroidSvgManager(context, testProxy { _, args ->
            if (args[0] == "bad") error("Unreadable source") else image
        }, TestDispatchers)
        for (params in SvgParams.presets) {
            val outputs = mutableListOf<ByteArray>()
            var failures = 0
            manager.convertToSvg(
                listOf("good", "bad", "good"),
                params,
                { failures++ }) { _, data -> outputs += data }
            assertEquals(1, failures)
            assertEquals(2, outputs.size)
            assertArrayEquals(outputs[0], outputs[1])
            val document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(ByteArrayInputStream(outputs[0]))
            assertEquals("svg", document.documentElement.tagName)
            assertTrue(document.getElementsByTagName("path").length > 0)
        }
        assertEquals(
            filesBefore,
            context.cacheDir.listFiles().orEmpty().map { it.name }
                .filter { it.startsWith("vector_") }.toSet()
        )
        image.recycle()
    }

    @Test
    fun svgRemovesTemporaryFileOnCallbackFailureAndCancellation() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val image = pixels(20, 16)
        val manager = AndroidSvgManager(context, testProxy { _, _ -> image }, TestDispatchers)
        val filesBefore = context.cacheDir.listFiles().orEmpty().map { it.name }
            .filter { it.startsWith("vector_") }.toSet()
        var failures = 0
        manager.convertToSvg(
            listOf("source"),
            SvgParams.Default,
            { failures++ }) { _, _ -> error("Save failed") }
        assertEquals(1, failures)
        val failure = runCatching {
            manager.convertToSvg(listOf("source"), SvgParams.Default, { throw it }) { _, _ ->
                throw kotlinx.coroutines.CancellationException("Cancelled")
            }
        }.exceptionOrNull()
        assertTrue(failure is kotlinx.coroutines.CancellationException)
        assertEquals(
            filesBefore,
            context.cacheDir.listFiles().orEmpty().map { it.name }
                .filter { it.startsWith("vector_") }.toSet()
        )
        image.recycle()
    }
}