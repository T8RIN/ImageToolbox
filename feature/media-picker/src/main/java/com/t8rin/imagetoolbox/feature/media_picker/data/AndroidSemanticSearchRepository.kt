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
import android.graphics.Bitmap
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.utils.tryExtractOriginal
import com.t8rin.imagetoolbox.feature.media_picker.domain.SemanticSearchRepository
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Media
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.SemanticSearchCategory
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.SemanticSearchCategoryResult
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.SemanticSearchState
import com.t8rin.neural_tools.search.ClipImageSearch
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class AndroidSemanticSearchRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageGetter: ImageGetter<Bitmap>,
    dispatchersHolder: DispatchersHolder
) : SemanticSearchRepository, DispatchersHolder by dispatchersHolder {

    private val indexMutex = Mutex()
    private val modelMutex = Mutex()
    private val searchMutex = Mutex()
    private val categoriesMutex = Mutex()

    @Volatile
    private var categoryClassifier: SemanticSearchClassifier? = null
    private val _state = MutableStateFlow(SemanticSearchState())
    override val state = _state.asStateFlow()

    override fun updateWorkState(
        isQueued: Boolean,
        isWaitingForPermission: Boolean,
        error: String?
    ) {
        _state.update {
            it.copy(
                isQueued = isQueued,
                isWaitingForPermission = isWaitingForPermission,
                error = error
            )
        }
    }

    override suspend fun refresh() = perform(modelMutex) {
        SemanticSearchIndex(context).use { index ->
            _state.update {
                it.copy(modelsReady = ClipImageSearch.isDownloaded(), indexedImages = index.count())
            }
        }
    }

    override suspend fun downloadModels() = perform(indexMutex) {
        modelMutex.withLock {
            _state.update { it.copy(isDownloading = true, downloadProgress = 0f, error = null) }
            try {
                ClipImageSearch.download { progress ->
                    _state.update { it.copy(downloadProgress = progress) }
                }
            } finally {
                _state.update {
                    it.copy(isDownloading = false, modelsReady = ClipImageSearch.isDownloaded())
                }
            }
        }
    }

    override suspend fun index(media: List<Media>) = perform(indexMutex) {
        modelMutex.withLock {
            check(ClipImageSearch.isDownloaded()) { "Download the search models first" }
        }
        val images = media.filter { it.mimeType.startsWith("image/") }.distinctBy(Media::uri)
        _state.update {
            it.copy(
                isIndexing = true, processedImages = 0, totalImages = images.size,
                failedImages = 0, error = null
            )
        }
        try {
            SemanticSearchIndex(context).use { index ->
                var encoder: ClipImageSearch.ImageEncoder? = null
                _state.update { it.copy(indexedImages = index.count()) }
                try {
                    images.forEachIndexed { position, media ->
                        currentCoroutineContext().ensureActive()
                        if (index.get(media) == null) {
                            val image = loadImage(media.uri)
                            if (image == null) {
                                currentCoroutineContext().ensureActive()
                                _state.update { it.copy(failedImages = it.failedImages + 1) }
                            } else {
                                currentCoroutineContext().ensureActive()
                                val currentEncoder = encoder ?: modelMutex.withLock {
                                    ClipImageSearch.openImageEncoder()
                                }.also { encoder = it }
                                val embedding = currentEncoder.encode(image)
                                currentCoroutineContext().ensureActive()
                                index.put(media, embedding)
                            }
                        }
                        _state.update {
                            it.copy(
                                processedImages = position + 1,
                                indexedImages = if (it.indexedImages == 0 || (position + 1) % 32 == 0) {
                                    index.count()
                                } else it.indexedImages
                            )
                        }
                    }
                } finally {
                    encoder?.close()
                    _state.update { it.copy(indexedImages = index.count()) }
                }
            }
        } finally {
            _state.update { it.copy(isIndexing = false) }
        }
    }

    override suspend fun search(query: String, media: List<Media>): List<Media> {
        if (query.isBlank() || media.isEmpty()) return emptyList()
        var result = emptyList<Media>()
        perform(searchMutex) {
            _state.update { it.copy(error = null) }
            SemanticSearchIndex(context).use { index ->
                if (index.count() == 0) return@use
                val encoder = modelMutex.withLock {
                    if (ClipImageSearch.isDownloaded()) ClipImageSearch.openTextEncoder() else null
                } ?: return@use
                encoder.use {
                    val embedding = it.encode(query)
                    currentCoroutineContext().ensureActive()
                    result = index.search(
                        embedding, media.filter { item -> item.mimeType.startsWith("image/") }
                    )
                }
            }
        }
        return result
    }

    override suspend fun searchByImage(uri: String, media: List<Media>): List<Media> {
        val images = media.filter { it.mimeType.startsWith("image/") }
        if (uri.isBlank() || images.isEmpty()) return emptyList()
        var result = emptyList<Media>()
        perform(searchMutex) {
            _state.update { it.copy(error = null) }
            val sourceUri = uri.toUri().tryExtractOriginal().toString()
            val source = images.find { it.uri == uri || it.uri == sourceUri }
            val candidates = images.filterNot { it.uri == uri || it.uri == sourceUri }
            if (candidates.isEmpty()) return@perform
            SemanticSearchIndex(context).use { index ->
                if (index.count() == 0) return@use
                if (!modelMutex.withLock { ClipImageSearch.isDownloaded() }) return@use
                val embedding = source?.let(index::get) ?: run {
                    val image = checkNotNull(loadImage(uri)) { "Could not read the selected photo" }
                    currentCoroutineContext().ensureActive()
                    val encoder = modelMutex.withLock { ClipImageSearch.openImageEncoder() }
                    encoder.use { it.encode(image) }
                }
                currentCoroutineContext().ensureActive()
                result = index.search(
                    embeddings = listOf(embedding),
                    media = candidates,
                    minimumSimilarity = 0.7f
                ).single()
            }
        }
        return result
    }

    override suspend fun categories(media: List<Media>): List<SemanticSearchCategoryResult> {
        if (media.isEmpty()) return emptyList()
        var result = emptyList<SemanticSearchCategoryResult>()
        perform(categoriesMutex) {
            val images = media.filter { it.mimeType.startsWith("image/") }
            if (images.isEmpty()) return@perform
            SemanticSearchIndex(context).use { index ->
                if (index.count() == 0 || !_state.value.modelsReady) return@use
                val classifier = categoryClassifier ?: searchMutex.withLock {
                    categoryClassifier ?: run {
                        val encoder = modelMutex.withLock {
                            if (ClipImageSearch.isDownloaded()) ClipImageSearch.openTextEncoder() else null
                        } ?: return@use
                        encoder.use {
                            val categories =
                                SemanticSearchCategory.entries.associateWith { category ->
                                    SemanticSearchClassifier.descriptions(category)
                                        .map { description ->
                                            currentCoroutineContext().ensureActive()
                                            it.encode(description)
                                        }
                                }
                            val backgrounds =
                                SemanticSearchClassifier.backgroundDescriptions.map { description ->
                                    currentCoroutineContext().ensureActive()
                                    it.encode(description)
                                }
                            SemanticSearchClassifier(categories, backgrounds)
                        }.also { categoryClassifier = it }
                    }
                }
                result = index.categories(classifier, images)
            }
        }
        return result
    }

    override suspend fun clear() = perform(indexMutex) {
        searchMutex.withLock {
            modelMutex.withLock {
                _state.update { it.copy(modelsReady = false) }
                ClipImageSearch.deleteModels()
                SemanticSearchIndex(context).use(SemanticSearchIndex::clear)
                categoryClassifier = null
                _state.value = SemanticSearchState()
            }
        }
    }

    private suspend fun loadImage(uri: String): Bitmap? = imageGetter.getImageData(
        uri = uri,
        size = 512,
        onFailure = {}
    )?.image

    private suspend fun perform(mutex: Mutex, action: suspend () -> Unit) =
        withContext(ioDispatcher) {
            mutex.withLock {
                try {
                    action()
                } catch (exception: CancellationException) {
                    throw exception
                } catch (exception: Exception) {
                    currentCoroutineContext().ensureActive()
                    _state.update {
                        it.copy(
                            error = exception.localizedMessage ?: exception.toString()
                        )
                    }
                    throw exception
                }
            }
        }

}