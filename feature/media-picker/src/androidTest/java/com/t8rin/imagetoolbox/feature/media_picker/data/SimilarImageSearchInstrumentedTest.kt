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

import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import coil3.ImageLoader
import coil3.intercept.Interceptor
import coil3.request.ImageResult
import coil3.request.allowHardware
import coil3.toBitmap
import com.t8rin.imagetoolbox.core.domain.coroutines.AppScope
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.settings.domain.model.CustomSemanticSearchCategory
import com.t8rin.imagetoolbox.core.settings.domain.model.SemanticSearchSettings
import com.t8rin.imagetoolbox.core.utils.UriReplacements
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Media
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.SemanticSearchCategory
import com.t8rin.neural_tools.search.ClipImageSearch
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
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.UUID
import kotlin.math.sqrt

@RunWith(AndroidJUnit4::class)
class SimilarImageSearchInstrumentedTest {

    private val application = InstrumentationRegistry.getInstrumentation().targetContext
    private val directory = File(application.cacheDir, "similar-image-search-${UUID.randomUUID()}")
    private val context = object : ContextWrapper(application) {
        override fun getNoBackupFilesDir(): File = directory
        override fun getFilesDir() = File(directory, "files")
    }
    private val loaders = mutableListOf<ImageLoader>()
    private val appScope = object : AppScope {
        override val coroutineContext = SupervisorJob() + Dispatchers.Default
    }

    @Before
    fun setUp() {
        assumeTrue("Download the semantic search models first", ClipImageSearch.isDownloaded())
        check(directory.mkdirs())
    }

    @After
    fun tearDown() {
        loaders.forEach(ImageLoader::shutdown)
        appScope.cancel()
        directory.deleteRecursively()
    }

    @Test
    fun cachedSourceRanksCurrentCandidatesAndKeepsDuplicateFiles() = runBlocking {
        val source = media(1)
        val duplicate = media(2)
        val similar = media(3)
        val lessSimilar = media(4)
        val weak = media(5)
        val outsideAlbum = media(6)
        val changed = media(7)
        val video = media(8).copy(mimeType = "video/mp4")
        SemanticSearchIndex(context).use { index ->
            index.put(source, embedding(1f))
            index.put(duplicate, embedding(1f))
            index.put(similar, embedding(0.9f))
            index.put(lessSimilar, embedding(0.7f))
            index.put(weak, embedding(0.699f))
            index.put(outsideAlbum, embedding(1f))
            index.put(changed, embedding(1f))
            index.put(video, embedding(1f))
        }
        val repository = repository(object : Interceptor {
            override suspend fun intercept(chain: Interceptor.Chain): ImageResult =
                error("A current cached source must not be decoded again")
        })

        val originalUri = "content://semantic-search-test/${UUID.randomUUID()}".toUri()
        UriReplacements.register(originalUri, source.uri.toUri())
        listOf(source.uri, originalUri.toString()).forEach { uri ->
            val results = repository.searchByImage(
                uri = uri,
                media = listOf(
                    source, lessSimilar, weak, changed.copy(timestamp = changed.timestamp + 1),
                    video, similar, duplicate
                )
            )

            assertEquals(uri, listOf(duplicate, similar, lessSimilar), results)
        }
        assertNull(repository.state.value.error)
    }

    @Test
    fun changingImageSimilarityThresholdReusesTheSourceAndExistingIndex() = runBlocking {
        val source = media(1)
        val similar = media(2)
        val broader = media(3)
        val media = listOf(source, broader, similar)
        SemanticSearchIndex(context).use { index ->
            index.put(source, embedding(1f))
            index.put(similar, embedding(0.9f))
            index.put(broader, embedding(0.5f))
        }
        val repository = repository(object : Interceptor {
            override suspend fun intercept(chain: Interceptor.Chain): ImageResult =
                error("Changing the threshold must reuse the cached image vectors")
        })

        assertEquals(listOf(similar), repository.searchByImage(source.uri, media))
        assertEquals(
            listOf(similar, broader),
            repository.searchByImage(source.uri, media, minimumSimilarity = 0.4f)
        )
        assertTrue(repository.searchByImage(source.uri, media, minimumSimilarity = 0.95f).isEmpty())
        assertEquals(listOf(similar), repository.searchByImage(source.uri, media))
        SemanticSearchIndex(context).use { index ->
            assertEquals(3, index.count())
            assertArrayEquals(embedding(1f), index.get(source), 0f)
        }
    }

    @Test
    fun customCategoriesAndTextThresholdsUseTheExistingImageIndex() = runBlocking {
        val red = media(1)
        val blue = media(2)
        val media = listOf(red, blue)
        val embeddings = ClipImageSearch.openImageEncoder().use { encoder ->
            listOf(Color.RED, Color.BLUE).map { color ->
                val bitmap = createBitmap(224, 224).apply { eraseColor(color) }
                try {
                    encoder.encode(bitmap)
                } finally {
                    bitmap.recycle()
                }
            }
        }
        SemanticSearchIndex(context).use { index ->
            media.zip(embeddings).forEach { (image, embedding) -> index.put(image, embedding) }
        }
        val descriptions = listOf("красный квадрат", "a blue square")
        val thresholds = ClipImageSearch.openTextEncoder().use { encoder ->
            descriptions.mapIndexed { position, description ->
                val query = encoder.encode(description)
                val scores = embeddings.map { vector ->
                    query.indices.sumOf { (query[it] * vector[it]).toDouble() }.toFloat()
                }
                assertTrue(
                    "The description must favor its color: $scores",
                    scores[position] > scores[1 - position]
                )
                assertTrue(scores[position] < 0.8f)
                scores.average().toFloat().also { assertTrue(it in 0.15f..0.8f) }
            }
        }
        var decoded = 0
        val repository = repository(object : Interceptor {
            override suspend fun intercept(chain: Interceptor.Chain): ImageResult {
                decoded++
                error("Editing categories must use the existing image vectors")
            }
        })
        repository.refresh()
        val category = CustomSemanticSearchCategory(
            id = UUID.randomUUID().toString(),
            name = "Красные",
            description = descriptions[0],
            threshold = thresholds[0]
        )

        suspend fun matches(value: CustomSemanticSearchCategory) = repository.categories(
            media = media,
            settings = SemanticSearchSettings(customCategories = listOf(value))
        ).single { it.category.id == category.id }

        assertEquals(listOf(red), matches(category).media)
        assertEquals(
            listOf(red),
            matches(
                category.copy(
                    referenceImages = listOf(
                        File(directory, "missing.png").toUri().toString()
                    )
                )
            ).media
        )
        val renamed = matches(category.copy(name = "Мои красные фото"))
        assertEquals("Мои красные фото", (renamed.category as SemanticSearchCategory.Custom).name)
        assertEquals(listOf(red), renamed.media)
        assertTrue(matches(category.copy(threshold = 0.8f)).media.isEmpty())
        assertEquals(listOf(red), matches(category).media)
        assertEquals(
            listOf(blue),
            matches(category.copy(description = descriptions[1], threshold = thresholds[1])).media
        )
        assertEquals(listOf(red), repository.search(descriptions[0], media, thresholds[0]))
        assertTrue(repository.search(descriptions[0], media, minimumSimilarity = 0.8f).isEmpty())
        assertFalse(repository.categories(media).any { it.category.id == category.id })
        assertEquals(0, decoded)
        assertFalse(repository.state.value.isIndexing)
        assertEquals(2, repository.state.value.indexedImages)
        SemanticSearchIndex(context).use { index ->
            assertEquals(2, index.count())
            assertArrayEquals(embeddings[0], index.get(red), 0f)
            assertArrayEquals(embeddings[1], index.get(blue), 0f)
        }
    }

    @Test
    fun photoCategoriesReuseIndexedReferencesAndPreviewExactlyTheSavedMatches() = runBlocking {
        val source = media(1)
        val duplicate = media(2)
        val similar = media(3)
        val weak = media(4)
        val media = listOf(source, duplicate, similar, weak)
        SemanticSearchIndex(context).use { index ->
            index.put(source, embedding(1f))
            index.put(duplicate, embedding(1f))
            index.put(similar, embedding(0.8f))
            index.put(weak, embedding(0.4f))
        }
        val reference =
            File(directory, "reference.png").apply { writeBytes(byteArrayOf(1)) }.toUri()
        UriReplacements.register(reference, source.uri.toUri())
        val repository = repository(object : Interceptor {
            override suspend fun intercept(chain: Interceptor.Chain): ImageResult =
                error("An indexed reference must not be decoded again")
        })
        repository.refresh()
        val category = CustomSemanticSearchCategory(
            id = "photos",
            name = "Фото-примеры",
            description = "",
            referenceImages = listOf(
                File(directory, "missing.png").toUri().toString(),
                reference.toString()
            )
        )
        val settings = SemanticSearchSettings(customCategories = listOf(category))
        val preview = repository.previewCategory(media, settings, category.id)
        val saved = repository.categories(media, settings)

        assertEquals(setOf(source, duplicate, similar), preview?.media?.toSet())
        assertEquals(saved.single { it.category.id == category.id }, preview)
        assertEquals(
            preview?.copy(media = emptyList()),
            repository.previewCategory(emptyList(), settings, category.id)
        )
        assertEquals(
            preview?.copy(media = emptyList()),
            repository.previewCategory(
                listOf(source.copy(mimeType = "video/mp4")),
                settings,
                category.id
            )
        )
        assertEquals(
            saved.single { it.category == SemanticSearchCategory.BuiltIn.People }
                .copy(media = emptyList()),
            repository.previewCategory(
                emptyList(),
                settings,
                SemanticSearchCategory.BuiltIn.People.id
            )
        )
        assertEquals(
            saved.single { it.category == SemanticSearchCategory.BuiltIn.People },
            repository.previewCategory(media, settings, SemanticSearchCategory.BuiltIn.People.id)
        )
        assertEquals(
            setOf(source, duplicate),
            repository.previewCategory(
                media,
                settings.copy(customCategories = listOf(category.copy(referenceThreshold = 0.95f))),
                category.id
            )?.media?.toSet()
        )
        assertEquals(preview, repository.previewCategory(media, settings, category.id))
        assertNull(repository.state.value.error)
        assertFalse(repository.state.value.isIndexing)
        SemanticSearchIndex(context).use { assertEquals(4, it.count()) }
    }

    @Test
    fun unsavedReferencesAreCachedAcrossDraftChangesAndStopMatchingAfterDeletion() = runBlocking {
        val reference = File(directory, "external-reference.png")
        val bitmap = createBitmap(224, 224).apply { eraseColor(Color.RED) }
        val embedding = try {
            reference.outputStream()
                .use { check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)) }
            ClipImageSearch.openImageEncoder().use { it.encode(bitmap) }
        } finally {
            bitmap.recycle()
        }
        val candidate = media(1)
        SemanticSearchIndex(context).use { it.put(candidate, embedding) }
        var decoded = 0
        val repository = repository(object : Interceptor {
            override suspend fun intercept(chain: Interceptor.Chain): ImageResult {
                decoded++
                return chain.proceed()
            }
        })
        repository.refresh()
        val category = CustomSemanticSearchCategory(
            id = "photos",
            name = "Reference",
            description = "",
            referenceImages = listOf(reference.toUri().toString())
        )
        val settings = SemanticSearchSettings(customCategories = listOf(category))

        suspend fun preview(value: CustomSemanticSearchCategory = category) =
            repository.previewCategory(
                listOf(candidate),
                settings.copy(customCategories = listOf(value)),
                category.id
            )

        assertEquals(listOf(candidate), preview()?.media)
        assertEquals(listOf(candidate), preview(category.copy(referenceThreshold = 0.9f))?.media)
        assertEquals(listOf(candidate), preview(category.copy(name = "Похожие"))?.media)
        assertEquals(1, decoded)
        check(reference.delete())
        assertEquals(emptyList<Media>(), preview()?.media)
        assertEquals(1, decoded)
        assertNull(repository.state.value.error)
        SemanticSearchIndex(context).use { assertEquals(1, it.count()) }
    }

    @Test
    fun savedPhotoCategoriesSurviveOriginalDeletionAndRepositoryRecreation() = runBlocking {
        val original = File(directory, "original-reference.png")
        val bitmap = createBitmap(1024, 512).apply { eraseColor(Color.RED) }
        val embedding = try {
            original.outputStream()
                .use { check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)) }
            ClipImageSearch.openImageEncoder().use { it.encode(bitmap) }
        } finally {
            bitmap.recycle()
        }
        val candidate = media(1)
        SemanticSearchIndex(context).use { it.put(candidate, embedding) }
        val repository = repository()
        val category = CustomSemanticSearchCategory(
            id = "saved-photos",
            name = "Saved photos",
            description = "",
            referenceImages = listOf(original.toUri().toString())
        )
        val saved = repository.prepareCategoryReferences(
            SemanticSearchSettings(customCategories = listOf(category))
        )
        check(original.delete())

        val recreated = repository()
        recreated.refresh()
        val result = recreated.previewCategory(listOf(candidate), saved, category.id)
        assertEquals(listOf(candidate), result?.media)
        assertEquals(
            result,
            recreated.categories(listOf(candidate), saved).single { it.category.id == category.id }
        )
        assertEquals(saved, recreated.prepareCategoryReferences(saved))
        assertNull(recreated.state.value.error)
    }

    @Test
    fun unresolvedSourcesAreDecodedWithoutReusingCachedMedia() = runBlocking {
        val source = media(1)
        val duplicate = media(2)
        SemanticSearchIndex(context).use { index ->
            index.put(source, embedding(1f))
            index.put(duplicate, embedding(1f))
        }
        val decoded = mutableListOf<String>()
        val repository = repository(object : Interceptor {
            override suspend fun intercept(chain: Interceptor.Chain): ImageResult {
                decoded.add(chain.request.data.toString())
                error("An unresolved source has to be decoded")
            }
        })
        val uris = listOf(
            "content://example.external/images/${source.id}",
            "content://example.external/documents/image%3A${source.id}"
        )

        uris.forEach { uri ->
            try {
                repository.searchByImage(uri, listOf(source, duplicate))
                fail("An unresolved source must not reuse the local image's embedding")
            } catch (_: IllegalStateException) {
                assertTrue(!repository.state.value.error.isNullOrBlank())
            }
        }

        assertEquals(uris, decoded)
    }

    @Test
    fun externalSourceReusesCachedBitmapWithoutAddingItToTheIndex() = runBlocking {
        val sourceFile = File(directory, "source.png")
        val bitmap = createBitmap(224, 224).apply { eraseColor(Color.RED) }
        val sourceEmbedding = try {
            sourceFile.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
            ClipImageSearch.openImageEncoder().use { it.encode(bitmap) }
        } finally {
            bitmap.recycle()
        }
        val matching = media(1)
        val unrelated = media(2)
        val outsideAlbum = media(3)
        val changed = media(4)
        SemanticSearchIndex(context).use { index ->
            index.put(matching, sourceEmbedding)
            index.put(unrelated, sourceEmbedding.map { -it }.toFloatArray())
            index.put(outsideAlbum, sourceEmbedding)
            index.put(changed, sourceEmbedding)
        }

        val decodedImages = mutableListOf<Bitmap>()
        val repository = repository(object : Interceptor {
            override suspend fun intercept(chain: Interceptor.Chain): ImageResult =
                chain.proceed().also { result ->
                    decodedImages.add(checkNotNull(result.image).toBitmap())
                }
        })

        repeat(2) {
            val results = repository.searchByImage(
                uri = sourceFile.toUri().toString(),
                media = listOf(unrelated, changed.copy(size = 2048), matching)
            )

            assertEquals(listOf(matching), results)
            assertFalse(
                "Search must not recycle a shared bitmap",
                decodedImages.last().isRecycled
            )
        }

        assertEquals(2, decodedImages.size)
        assertSame(decodedImages.first(), decodedImages.last())
        SemanticSearchIndex(context).use { assertEquals(4, it.count()) }
    }

    @Test
    fun firstIndexedImageIsAvailableBeforeTheBatchFinishes() = runBlocking {
        val sourceFile = File(directory, "first.png")
        val bitmap = createBitmap(224, 224).apply { eraseColor(Color.RED) }
        try {
            sourceFile.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        } finally {
            bitmap.recycle()
        }
        val first = media(1).copy(uri = sourceFile.toUri().toString())
        val second = media(2)
        val secondDecodeStarted = CompletableDeferred<Unit>()
        var indexedBitmap: Bitmap? = null
        val repository = repository(object : Interceptor {
            override suspend fun intercept(chain: Interceptor.Chain): ImageResult {
                if (chain.request.data == second.uri) {
                    secondDecodeStarted.complete(Unit)
                    awaitCancellation()
                }
                return chain.proceed().also { indexedBitmap = it.image?.toBitmap() }
            }
        })
        val indexing = launch(Dispatchers.Default) { repository.index(listOf(first, second)) }
        try {
            withTimeout(30_000) { secondDecodeStarted.await() }
            assertTrue(repository.state.value.isIndexing)
            assertEquals(1, repository.state.value.processedImages)
            assertEquals(1, repository.state.value.indexedImages)
            assertFalse(
                "Indexing must not recycle a shared bitmap",
                checkNotNull(indexedBitmap).isRecycled
            )
        } finally {
            indexing.cancelAndJoin()
        }

        assertEquals(1, repository.state.value.indexedImages)
        SemanticSearchIndex(context).use { assertEquals(1, it.count()) }
    }

    @Test
    fun changedSourceIsDecodedAndCancellationKeepsTheIndexIntact() = runBlocking {
        val source = media(1)
        val updated = source.copy(timestamp = source.timestamp + 1)
        val candidate = media(2)
        SemanticSearchIndex(context).use { index ->
            index.put(source, embedding(1f))
            index.put(candidate, embedding(1f))
        }
        val decodeStarted = CompletableDeferred<Unit>()
        val repository = repository(object : Interceptor {
            override suspend fun intercept(chain: Interceptor.Chain): ImageResult {
                decodeStarted.complete(Unit)
                awaitCancellation()
            }
        })
        val searching = launch(Dispatchers.Default) {
            repository.searchByImage(source.uri, listOf(updated, candidate))
            fail("Cancelled image search must not return results")
        }
        try {
            withTimeout(5_000) { decodeStarted.await() }
        } finally {
            searching.cancelAndJoin()
        }

        assertTrue(searching.isCancelled)
        assertNull(repository.state.value.error)
        SemanticSearchIndex(context).use { index ->
            assertEquals(2, index.count())
            assertArrayEquals(embedding(1f), index.get(source), 0f)
            assertNull(index.get(updated))
        }
    }

    @Test
    fun unreadableSourceReportsAnErrorWithoutChangingTheIndex() = runBlocking {
        val candidate = media(1)
        SemanticSearchIndex(context).use { it.put(candidate, embedding(1f)) }
        val repository = repository()

        try {
            repository.searchByImage(
                uri = File(directory, "missing.png").toUri().toString(),
                media = listOf(candidate)
            )
            fail("An unreadable source must report an error")
        } catch (_: IllegalStateException) {
            assertTrue(!repository.state.value.error.isNullOrBlank())
        }

        SemanticSearchIndex(context).use { assertEquals(1, it.count()) }
    }

    private fun repository(interceptor: Interceptor? = null): AndroidSemanticSearchRepository {
        val loader = ImageLoader.Builder(context).allowHardware(false).components {
            if (interceptor != null) add(interceptor)
        }.build().also(loaders::add)
        val dispatchers = object : DispatchersHolder {
            override val uiDispatcher = Dispatchers.Default
            override val ioDispatcher = Dispatchers.IO
            override val encodingDispatcher = Dispatchers.Default
            override val decodingDispatcher = Dispatchers.Default
            override val defaultDispatcher = Dispatchers.Default
        }
        val imageGetter = semanticSearchImageGetter(context, loader, appScope, dispatchers)
        return AndroidSemanticSearchRepository(
            context = context,
            imageGetter = imageGetter,
            imageScaler = semanticSearchImageScaler(),
            imageCompressor = semanticSearchImageCompressor(),
            referenceStore = semanticSearchReferenceStore(context, imageGetter, dispatchers),
            dispatchersHolder = dispatchers
        )
    }

    private fun embedding(similarity: Float) = FloatArray(512).apply {
        this[0] = similarity
        this[1] = sqrt(1f - similarity * similarity)
    }

    private fun media(id: Long) = Media(
        id = id,
        label = "$id.png",
        uri = "content://media/external/images/media/$id",
        path = "/Pictures/$id.png",
        relativePath = "Pictures/",
        albumID = 1,
        albumLabel = "Pictures",
        timestamp = 1_700_000_000,
        mimeType = "image/png",
        width = 224,
        height = 224,
        size = 1024
    )
}