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
import android.graphics.Color
import android.graphics.RectF
import androidx.core.graphics.createBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import coil3.ImageLoader
import coil3.request.allowHardware
import com.t8rin.imagetoolbox.core.data.image.AndroidImageGetter
import com.t8rin.imagetoolbox.core.domain.coroutines.AppScope
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.MetadataProvider
import com.t8rin.imagetoolbox.core.domain.saving.FailureNotifier
import com.t8rin.imagetoolbox.core.settings.domain.SettingsProvider
import com.t8rin.imagetoolbox.core.settings.domain.model.SettingsState
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Media
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.SemanticSearchCategory
import com.t8rin.neural_tools.search.ClipImageSearch
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class SemanticSearchRepositoryInstrumentedTest {

    @Test
    fun cachedSearchRemainsAvailableDuringIndexingAndCancellationPreservesProgress() = runBlocking {
        assumeTrue("Download the semantic search models first", ClipImageSearch.isDownloaded())
        val application = InstrumentationRegistry.getInstrumentation().targetContext
        val directory = File(application.cacheDir, "semantic-search-${UUID.randomUUID()}").apply {
            check(mkdirs())
        }
        val context = object : ContextWrapper(application) {
            override fun getNoBackupFilesDir(): File = directory
        }
        val decodeStarted = CompletableDeferred<Unit>()
        val loader = ImageLoader.Builder(context).allowHardware(false).components {
            add {
                decodeStarted.complete(Unit)
                awaitCancellation()
            }
        }.build()
        val dispatchers = object : DispatchersHolder {
            override val uiDispatcher = Dispatchers.Default
            override val ioDispatcher = Dispatchers.IO
            override val encodingDispatcher = Dispatchers.Default
            override val decodingDispatcher = Dispatchers.Default
            override val defaultDispatcher = Dispatchers.Default
        }
        val imageGetter = semanticSearchImageGetter(
            context = context,
            imageLoader = loader,
            appScope = object : AppScope {
                override val coroutineContext = this@runBlocking.coroutineContext
            },
            dispatchers = dispatchers
        )
        val repository = AndroidSemanticSearchRepository(
            context = context,
            imageGetter = imageGetter,
            imageScaler = semanticSearchImageScaler(),
            imageCompressor = semanticSearchImageCompressor(),
            referenceStore = semanticSearchReferenceStore(context, imageGetter, dispatchers),
            dispatchersHolder = dispatchers
        )
        val cached = media(1)
        try {
            val bitmap = createBitmap(224, 224).apply { eraseColor(Color.RED) }
            val embedding = try {
                ClipImageSearch.openImageEncoder().use { it.encode(bitmap) }
            } finally {
                bitmap.recycle()
            }
            SemanticSearchIndex(context).use { it.put(cached, embedding) }
            repository.index(listOf(cached))
            assertFalse("Cached photos must not be decoded again", decodeStarted.isCompleted)
            assertEquals(1, repository.state.value.indexedImages)

            val personId = SemanticFaceIndex(context).use { faces ->
                faces.replace(
                    cached,
                    listOf(
                        SemanticFaceIndex.Face(
                            id = "cached-face",
                            vector = embedding,
                            thumbnail = File(directory, "face.webp").absolutePath,
                            bounds = RectF(0.1f, 0.1f, 0.5f, 0.5f),
                            confidence = 1f
                        )
                    )
                )
                (faces.people(listOf(cached))
                    .single().category as SemanticSearchCategory.Person).personId
            }
            val indexing = launch(Dispatchers.Default) { repository.index(listOf(media(2))) }
            try {
                withTimeout(5_000) { decodeStarted.await() }
                assertTrue(repository.state.value.isIndexing)
                withTimeout(2_000) {
                    repository.updatePerson(personId, "Alice", 0.55f, cached.uri)
                    repository.setPersonHidden(personId, true)
                }
                SemanticFaceIndex(context).use { faces ->
                    val person = faces.people(listOf(cached))
                        .single().category as SemanticSearchCategory.Person
                    assertEquals("Alice", person.name)
                    assertTrue(person.hidden)
                }
                val results = withTimeout(30_000) {
                    repository.search("a red square", listOf(cached))
                }
                assertEquals(listOf(cached), results)
                assertTrue("Search must finish before background indexing", indexing.isActive)
            } finally {
                indexing.cancelAndJoin()
            }

            assertFalse(repository.state.value.isIndexing)
            SemanticSearchIndex(context).use { assertEquals(1, it.count()) }
        } finally {
            loader.shutdown()
            directory.deleteRecursively()
        }
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

internal fun semanticSearchImageGetter(
    context: Context,
    imageLoader: ImageLoader,
    appScope: AppScope,
    dispatchers: DispatchersHolder
) = AndroidImageGetter(
    context = context,
    imageLoader = imageLoader,
    appScope = appScope,
    failureNotifier = object : FailureNotifier {
        override fun send(error: Throwable) {
            throw AssertionError("Unexpected failure notification", error)
        }

        override fun sendNoConnection() = Unit
    },
    metadataProvider = { MetadataProvider { null } },
    settingsProvider = object : SettingsProvider {
        override val settingsState = MutableStateFlow(SettingsState.Default)
        override suspend fun getSettingsState() = settingsState.value
    },
    dispatchersHolder = dispatchers
)