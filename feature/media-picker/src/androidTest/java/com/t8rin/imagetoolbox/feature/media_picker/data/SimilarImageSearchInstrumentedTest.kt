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
import com.t8rin.imagetoolbox.core.utils.UriReplacements
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Media
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
        return AndroidSemanticSearchRepository(context, imageGetter, dispatchers)
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