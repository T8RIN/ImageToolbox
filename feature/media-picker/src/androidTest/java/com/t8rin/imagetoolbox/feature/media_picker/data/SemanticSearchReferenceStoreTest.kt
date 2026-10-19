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

package com.t8rin.imagetoolbox.feature.media_picker.data

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import androidx.core.net.toUri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import coil3.ImageLoader
import coil3.request.allowHardware
import com.t8rin.imagetoolbox.core.domain.coroutines.AppScope
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.ImageScaleMode
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.image.model.ResizeType
import com.t8rin.imagetoolbox.core.settings.domain.model.CustomSemanticSearchCategory
import com.t8rin.imagetoolbox.core.settings.domain.model.SemanticSearchSettings
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class SemanticSearchReferenceStoreTest {

    private val application = InstrumentationRegistry.getInstrumentation().targetContext
    private val directory = File(application.cacheDir, "semantic-references-${UUID.randomUUID()}")
    private val context = object : ContextWrapper(application) {
        override fun getFilesDir() = File(directory, "files")
    }
    private val dispatchers = object : DispatchersHolder {
        override val uiDispatcher = Dispatchers.Default
        override val ioDispatcher = Dispatchers.IO
        override val encodingDispatcher = Dispatchers.Default
        override val decodingDispatcher = Dispatchers.Default
        override val defaultDispatcher = Dispatchers.Default
    }
    private val appScope = object : AppScope {
        override val coroutineContext = SupervisorJob() + Dispatchers.Default
    }
    private val loader by lazy { ImageLoader.Builder(context).allowHardware(false).build() }
    private val imageGetter by lazy {
        semanticSearchImageGetter(context, loader, appScope, dispatchers)
    }
    private val references get() = File(context.filesDir, "semantic-search-references")

    @Before
    fun setUp() {
        check(directory.mkdirs())
    }

    @After
    fun tearDown() {
        loader.shutdown()
        appScope.cancel()
        directory.deleteRecursively()
    }

    @Test
    fun copiedReferencesAreSmallSharedAndIndependentOfTheirOriginals() = runBlocking {
        val original = source("original", width = 1600, height = 800)
        val settings = settings(original).let {
            it.copy(
                customCategories = it.customCategories + it.customCategories.single()
                    .copy(id = "second")
            )
        }
        val prepared = store().prepare(settings)
        val uri = prepared.customCategories.first().referenceImages.single()
        val copy = File(checkNotNull(uri.toUri().path))

        assertEquals(
            prepared.customCategories.first().referenceImages,
            prepared.customCategories.last().referenceImages
        )
        assertEquals(references.canonicalFile, copy.parentFile?.canonicalFile)
        assertEquals(1, references.listFiles()?.size)
        check(original.delete())
        val image = checkNotNull(BitmapFactory.decodeFile(copy.path))
        try {
            assertTrue(image.width <= 512 && image.height <= 512)
            assertEquals(2f, image.width.toFloat() / image.height, 0.01f)
        } finally {
            image.recycle()
        }
        assertEquals(prepared, store().prepare(prepared))
        assertEquals(1, references.listFiles()?.size)
    }

    @Test
    fun cleanupKeepsSharedReferencesAndNeverDeletesSourceOrUnrelatedFiles() = runBlocking {
        val first = source("first")
        val second = source("second")
        val prepared = store().prepare(settings(first, second))
        val copies = prepared.customCategories.single().referenceImages
        val retained = prepared.copy(
            customCategories = listOf(
                prepared.customCategories.single().copy(referenceImages = copies.take(1)),
                prepared.customCategories.single()
                    .copy(id = "shared", referenceImages = copies.take(1))
            )
        )
        val unrelated = File(references, "keep.txt").apply { writeText("keep") }
        val temporary =
            File(references, "${UUID.randomUUID()}.webp.tmp").apply { writeText("partial") }

        store().cleanup(retained)
        assertTrue(File(checkNotNull(copies.first().toUri().path)).exists())
        assertFalse(File(checkNotNull(copies.last().toUri().path)).exists())
        assertFalse(temporary.exists())
        assertTrue(first.exists() && second.exists() && unrelated.exists())

        store().cleanup(SemanticSearchSettings())
        assertFalse(File(checkNotNull(copies.first().toUri().path)).exists())
        assertTrue(first.exists() && second.exists() && unrelated.exists())
    }

    @Test
    fun restoredReferencesUseCurrentPrivateFilesWithoutRebasingUnrelatedUris() = runBlocking {
        val saved = store().prepare(settings(source("original")))
        val category = saved.customCategories.single()
        val name = File(checkNotNull(category.referenceImages.single().toUri().path)).name
        val previous =
            "file:///data/user/10/${context.packageName}/files/semantic-search-references/$name"
        val restored = saved.copy(
            customCategories = listOf(category.copy(referenceImages = listOf(previous)))
        )

        assertEquals(saved, store().prepare(restored))
        store().cleanup(restored)
        assertEquals(1, references.listFiles()?.size)

        val unrelated = "file:///data/user/10/other.package/files/semantic-search-references/$name"
        val unrelatedSettings = saved.copy(
            customCategories = listOf(category.copy(referenceImages = listOf(unrelated)))
        )
        assertTrue(runCatching { store().prepare(unrelatedSettings) }.isFailure)
        assertEquals(1, references.listFiles()?.size)
    }

    @Test
    fun failedImportsRollbackNewFilesAndKeepPreviouslySavedReferences() = runBlocking {
        val saved = store().prepare(settings(source("saved")))
        val retained = references.listFiles().orEmpty().toSet()
        val missing = File(directory, "missing.png")
        val requested = settings(source("new"), missing)

        try {
            store().prepare(requested)
            fail("A newly added unreadable reference must fail the save")
        } catch (_: IllegalStateException) {
            assertEquals(retained, references.listFiles().orEmpty().toSet())
        }
        assertTrue(retained.all(File::exists))
        store().cleanup(saved)
        assertEquals(retained, references.listFiles().orEmpty().toSet())
    }

    @Test
    fun cancellationRemovesNewCopiesAndKeepsPreviouslySavedReferences() = runBlocking {
        val saved = store().prepare(settings(source("saved")))
        val retained = references.listFiles().orEmpty().toSet()
        val requested = settings(source("first"), source("second"))
        val copyingSecond = CompletableDeferred<Unit>()
        var compressed = 0
        val store = store {
            compressed++
            if (compressed == 2) {
                copyingSecond.complete(Unit)
                awaitCancellation()
            }
        }
        val job = launch(Dispatchers.Default) { store.prepare(requested) }
        try {
            withTimeout(10_000) { copyingSecond.await() }
        } finally {
            job.cancelAndJoin()
        }

        assertEquals(retained, references.listFiles().orEmpty().toSet())
        assertEquals(saved, store().prepare(saved))
    }

    private fun store(onCompress: suspend () -> Unit = {}) = semanticSearchReferenceStore(
        context, imageGetter, dispatchers, onCompress
    )

    private fun settings(vararg references: File) = SemanticSearchSettings(
        customCategories = listOf(
            CustomSemanticSearchCategory(
                id = "reference",
                name = "Reference",
                description = "",
                referenceImages = references.map { it.toUri().toString() }
            )
        )
    )

    private fun source(name: String, width: Int = 320, height: Int = 240): File {
        val image = createBitmap(width, height).apply { eraseColor(Color.RED) }
        return try {
            File(directory, "$name.png").apply {
                outputStream().use { check(image.compress(Bitmap.CompressFormat.PNG, 100, it)) }
            }
        } finally {
            image.recycle()
        }
    }
}

internal fun semanticSearchReferenceStore(
    context: Context,
    imageGetter: ImageGetter<Bitmap>,
    dispatchers: DispatchersHolder,
    onCompress: suspend () -> Unit = {}
) = SemanticSearchReferenceStore(
    context = context,
    imageGetter = imageGetter,
    imageScaler = semanticSearchImageScaler(),
    imageCompressor = semanticSearchImageCompressor(onCompress),
    dispatchersHolder = dispatchers
)

internal fun semanticSearchImageScaler(): ImageScaler<Bitmap> = object : ImageScaler<Bitmap> {
    override suspend fun scaleImage(
        image: Bitmap,
        width: Int,
        height: Int,
        resizeType: ResizeType,
        imageScaleMode: ImageScaleMode
    ) = image.scale(width, height)

    override suspend fun scaleUntilCanShow(image: Bitmap?) = image
}

internal fun semanticSearchImageCompressor(
    onCompress: suspend () -> Unit = {}
): ImageCompressor<Bitmap> = object : ImageCompressor<Bitmap> {
    @Suppress("DEPRECATION")
    override suspend fun compress(
        image: Bitmap,
        imageFormat: ImageFormat,
        quality: Quality
    ): ByteArray {
        onCompress()
        assertEquals(ImageFormat.Webp.Lossy, imageFormat)
        return ByteArrayOutputStream().use {
            check(image.compress(Bitmap.CompressFormat.WEBP, quality.qualityValue, it))
            it.toByteArray()
        }
    }

    override suspend fun compressAndTransform(
        image: Bitmap,
        imageInfo: ImageInfo,
        onImageReadyToCompressInterceptor: suspend (Bitmap) -> Bitmap,
        applyImageTransformations: Boolean
    ): ByteArray = error("Reference copies must not apply user transformations")

    override suspend fun calculateImageSize(image: Bitmap, imageInfo: ImageInfo): Long =
        error("Reference copies must not need an extra compression pass")
}