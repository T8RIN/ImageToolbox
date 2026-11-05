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

package com.t8rin.imagetoolbox.core.data.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.test.platform.app.InstrumentationRegistry
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.image.ImageTransformer
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.image.model.ResizeType
import com.t8rin.imagetoolbox.core.settings.domain.SettingsProvider
import com.t8rin.imagetoolbox.core.settings.domain.model.FilenameBehavior
import com.t8rin.imagetoolbox.core.settings.domain.model.SettingsState
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.lang.reflect.Proxy

class ImagePreviewPipelineTest {
    private val bitmap = Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888).apply {
        eraseColor(0xff123456.toInt())
    }
    private val info = ImageInfo(width = 32, height = 32, imageFormat = ImageFormat.Png.Lossless)
    private val settings = object : SettingsProvider {
        override val settingsState = MutableStateFlow(
            SettingsState.Default.copy(
                generatePreviews = true,
                filenameBehavior = FilenameBehavior.None()
            )
        )

        override suspend fun getSettingsState() = settingsState.value
    }
    private val scaler = proxy<ImageScaler<Bitmap>> { _, args -> args[0] }
    private val transformer = proxy<ImageTransformer<Bitmap>> { _, args -> args[0] }
    private val getter = proxy<ImageGetter<Bitmap>> { _, args ->
        val bytes = args[0] as ByteArray
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    @Test
    fun previewIsPublishedWhileSizeCalculationIsStillRunning() = runBlocking {
        val releaseSize = CompletableDeferred<Unit>()
        val preview = CompletableDeferred<Bitmap?>()
        val counts = mutableListOf<Int>()
        val creator = creator(FakeCompressor { releaseSize.await(); 123L })
        val job = launch {
            creator.createPreview(
                bitmap, info,
                onGetByteCount = { counts += it },
                onGetPreview = { preview.complete(it) })
        }
        assertSame(bitmap, withTimeout(5000) { preview.await() })
        assertTrue(job.isActive)
        releaseSize.complete(Unit)
        job.join()
        assertEquals(listOf(0, 123), counts)
    }

    @Test
    fun cancellingPreviewCancelsPendingSizeCalculation() = runBlocking {
        val started = CompletableDeferred<Unit>()
        val cancelled = CompletableDeferred<Unit>()
        val creator = creator(FakeCompressor {
            started.complete(Unit)
            try {
                awaitCancellation()
            } finally {
                cancelled.complete(Unit)
            }
        })
        val job =
            launch { creator.createPreview(bitmap, info, onGetByteCount = {}, onGetPreview = {}) }
        withTimeout(5000) { started.await() }
        job.cancelAndJoin()
        assertTrue(cancelled.isCompleted)
    }

    @Test
    fun previewWithoutSizeConsumerSkipsFullSizeEncodingAndBoundsLargeDimensions() = runBlocking {
        val compressor = FakeCompressor { error("Unused size calculation") }
        val creator = creator(compressor)
        assertNotNull(
            creator.createPreview(
                bitmap,
                info.copy(width = Int.MAX_VALUE, height = Int.MAX_VALUE)
            )
        )
        val encodedInfo = requireNotNull(compressor.encodedInfo)
        assertTrue(encodedInfo.width in 1..1500)
        assertTrue(encodedInfo.height in 1..1500)
    }

    @Test
    fun normalResizeDoesNotDecodeOriginalAndByteCountMatchesEncodedData() = runBlocking {
        val compressor = AndroidImageCompressor(
            context = InstrumentationRegistry.getInstrumentation().targetContext,
            imageTransformer = transformer,
            imageScaler = scaler,
            imageGetter = proxy { name, _ -> error("Unexpected source access: $name") },
            settingsProvider = settings,
            dispatchersHolder = Dispatchers
        )
        val imageInfo =
            info.copy(originalUri = "content://unused/source", resizeType = ResizeType.Flexible)
        val bytes = compressor.compressAndTransform(bitmap, imageInfo, { it }, true)
        assertEquals(bytes.size.toLong(), compressor.calculateImageSize(bitmap, imageInfo))
        assertTrue(bitmap.sameAs(BitmapFactory.decodeByteArray(bytes, 0, bytes.size)))
    }

    @Test
    fun disabledPreviewsStillReportSizeAndReturnNullPreview() = runBlocking {
        settings.settingsState.value = settings.settingsState.value.copy(generatePreviews = false)
        var previewCalled = false
        var size = 0
        creator(FakeCompressor { 321L }).createPreview(
            bitmap, info,
            onGetByteCount = { size = it },
            onGetPreview = { previewCalled = true; assertNull(it) }
        )
        assertTrue(previewCalled)
        assertEquals(321, size)
    }

    @Test
    fun overwriteKeepsOriginalFormatAndCropKeepsOriginalDimensions() = runBlocking {
        val original = Bitmap.createBitmap(64, 48, Bitmap.Config.ARGB_8888)
        settings.settingsState.value =
            settings.settingsState.value.copy(filenameBehavior = FilenameBehavior.Overwrite())
        var extensionReads = 0
        var crop: ResizeType.CenterCrop? = null
        val compressor = AndroidImageCompressor(
            context = InstrumentationRegistry.getInstrumentation().targetContext,
            imageTransformer = transformer,
            imageScaler = proxy { _, args ->
                crop = args[3] as ResizeType.CenterCrop
                args[0]
            },
            imageGetter = proxy { name, _ ->
                when (name) {
                    "getImage" -> original
                    "getExtension" -> {
                        extensionReads++; "png"
                    }

                    else -> error(name)
                }
            },
            settingsProvider = settings,
            dispatchersHolder = Dispatchers
        )
        try {
            val bytes = compressor.compressAndTransform(
                bitmap,
                info.copy(
                    originalUri = "content://source/image",
                    imageFormat = ImageFormat.Jpg,
                    resizeType = ResizeType.CenterCrop()
                ),
                { it }, true
            )
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
            assertEquals("image/png", options.outMimeType)
            assertEquals(1, extensionReads)
            assertEquals(64, crop?.originalSize?.width)
            assertEquals(48, crop?.originalSize?.height)
        } finally {
            original.recycle()
        }
    }

    @Test
    fun cancelledEncodingCannotPublishAStaleByteCount() = runBlocking {
        val started = CompletableDeferred<Unit>()
        val release = CompletableDeferred<Unit>()
        val sizes = java.util.concurrent.ConcurrentLinkedQueue<Int>()
        val old = launch {
            creator(FakeCompressor {
                started.complete(Unit)
                withContext(NonCancellable) { release.await() }
                111L
            }).createPreview(bitmap, info, onGetByteCount = { sizes.add(it) }, onGetPreview = {})
        }
        withTimeout(5_000) { started.await() }
        old.cancel()
        try {
            creator(FakeCompressor { 222L }).createPreview(
                bitmap, info, onGetByteCount = { sizes.add(it) }, onGetPreview = {}
            )
        } finally {
            release.complete(Unit)
            old.join()
        }
        assertEquals(listOf(0, 0, 222), sizes.toList())
    }

    private fun creator(compressor: ImageCompressor<Bitmap>) = AndroidImagePreviewCreator(
        compressor, getter, transformer, scaler, settings, Dispatchers
    )

    private class FakeCompressor(private val size: suspend () -> Long) :
        ImageCompressor<Bitmap> {
        var encodedInfo: ImageInfo? = null
        override suspend fun calculateImageSize(image: Bitmap, imageInfo: ImageInfo) = size()
        override suspend fun compress(image: Bitmap, imageFormat: ImageFormat, quality: Quality) =
            ByteArrayOutputStream().apply { image.compress(Bitmap.CompressFormat.PNG, 100, this) }
                .toByteArray()

        override suspend fun compressAndTransform(
            image: Bitmap, imageInfo: ImageInfo,
            onImageReadyToCompressInterceptor: suspend (Bitmap) -> Bitmap,
            applyImageTransformations: Boolean
        ): ByteArray {
            encodedInfo = imageInfo
            return compress(image, imageInfo.imageFormat, imageInfo.quality)
        }
    }

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