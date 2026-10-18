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
import android.system.Os
import android.util.LruCache
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.settings.domain.model.SemanticSearchSettings
import com.t8rin.imagetoolbox.core.utils.tryExtractOriginal
import com.t8rin.imagetoolbox.feature.media_picker.data.utils.getMediaFromCursor
import com.t8rin.imagetoolbox.feature.media_picker.domain.SemanticSearchRepository
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.AllowedMedia
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
import java.io.Closeable
import javax.inject.Inject

internal class AndroidSemanticSearchRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageGetter: ImageGetter<Bitmap>,
    private val referenceStore: SemanticSearchReferenceStore,
    dispatchersHolder: DispatchersHolder
) : SemanticSearchRepository, DispatchersHolder by dispatchersHolder {

    private val indexMutex = Mutex()
    private val modelMutex = Mutex()
    private val searchMutex = Mutex()
    private val categoriesMutex = Mutex()

    private val categoryCache = LruCache<CategoryCacheKey, List<SemanticSearchCategoryResult>>(4)
    private val categoryEmbeddings = mutableMapOf<String, FloatArray>()
    private val referenceEmbeddings = mutableMapOf<String, ReferenceEmbedding>()
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
        var pendingWrites = 0
        _state.update {
            it.copy(
                isIndexing = true, processedImages = 0, totalImages = images.size,
                failedImages = 0, error = null
            )
        }
        try {
            SemanticSearchIndex(context).use { index ->
                _state.update { it.copy(indexedImages = index.count()) }
                try {
                    LazyImageEncoder().use { encoder ->
                        images.forEachIndexed { position, media ->
                            currentCoroutineContext().ensureActive()
                            if (index.get(media) == null) {
                                val image = loadImage(media.uri)
                                if (image == null) {
                                    currentCoroutineContext().ensureActive()
                                    _state.update { it.copy(failedImages = it.failedImages + 1) }
                                } else {
                                    currentCoroutineContext().ensureActive()
                                    val embedding = encoder.encode(image)
                                    currentCoroutineContext().ensureActive()
                                    index.put(media, embedding)
                                    pendingWrites++
                                    if (pendingWrites == 256) {
                                        _state.update {
                                            it.copy(indexRevision = it.indexRevision + pendingWrites)
                                        }
                                        pendingWrites = 0
                                    }
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
                    }
                } finally {
                    _state.update { it.copy(indexedImages = index.count()) }
                }
            }
        } finally {
            _state.update {
                it.copy(
                    isIndexing = false,
                    indexRevision = it.indexRevision + pendingWrites
                )
            }
        }
    }

    override suspend fun search(
        query: String,
        media: List<Media>,
        minimumSimilarity: Float
    ): List<Media> {
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
                        embedding = embedding,
                        media = media.filter { item -> item.mimeType.startsWith("image/") },
                        minimumSimilarity = minimumSimilarity
                    )
                }
            }
        }
        return result
    }

    override suspend fun searchByImage(
        uri: String,
        media: List<Media>,
        minimumSimilarity: Float
    ): List<Media> {
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
                    minimumSimilarity = minimumSimilarity
                ).single()
            }
        }
        return result
    }

    override fun getCachedCategories(
        albumId: Long,
        allowedMedia: AllowedMedia,
        settings: SemanticSearchSettings
    ): List<SemanticSearchCategoryResult>? =
        categoryCache[CategoryCacheKey(albumId, allowedMedia, settings)]

    override fun cacheCategories(
        albumId: Long,
        allowedMedia: AllowedMedia,
        settings: SemanticSearchSettings,
        categories: List<SemanticSearchCategoryResult>
    ) {
        if (!_state.value.modelsReady || _state.value.indexedImages == 0) return
        categoryCache.put(CategoryCacheKey(albumId, allowedMedia, settings), categories)
    }

    override suspend fun categories(
        media: List<Media>,
        settings: SemanticSearchSettings
    ): List<SemanticSearchCategoryResult> = classifyCategories(media, settings)

    override suspend fun previewCategory(
        media: List<Media>,
        settings: SemanticSearchSettings,
        categoryId: String
    ): SemanticSearchCategoryResult? = classifyCategories(media, settings, categoryId)
        .find { it.category.id == categoryId }

    override suspend fun prepareCategoryReferences(
        settings: SemanticSearchSettings
    ): SemanticSearchSettings = referenceStore.prepare(settings)

    override suspend fun cleanupCategoryReferences(settings: SemanticSearchSettings) =
        referenceStore.cleanup(settings)

    private suspend fun classifyCategories(
        media: List<Media>,
        settings: SemanticSearchSettings,
        categoryId: String? = null
    ): List<SemanticSearchCategoryResult> {
        var result = emptyList<SemanticSearchCategoryResult>()
        perform(categoriesMutex) {
            val images = media.filter { it.mimeType.startsWith("image/") }
            SemanticSearchIndex(context).use { index ->
                if (index.count() == 0 || !_state.value.modelsReady) return@use
                if (images.isEmpty()) {
                    val category =
                        SemanticSearchCategory.BuiltIn.entries.find { it.id == categoryId }
                            ?: settings.customCategories.find { it.id == categoryId }?.run {
                                SemanticSearchCategory.Custom(id, name, description)
                            }
                    result = listOfNotNull(
                        category?.let {
                            SemanticSearchCategoryResult(
                                it,
                                emptyList()
                            )
                        }
                    )
                    return@use
                }
                val classifier = searchMutex.withLock {
                    val allDescriptions = buildMap {
                        SemanticSearchCategory.BuiltIn.entries.forEach { category ->
                            put(category, SemanticSearchClassifier.descriptions(category))
                        }
                        settings.customCategories.forEach { category ->
                            put(
                                SemanticSearchCategory.Custom(
                                    id = category.id,
                                    name = category.name,
                                    description = category.description
                                ),
                                listOfNotNull(category.description.takeIf(String::isNotBlank))
                            )
                        }
                    }
                    val descriptions = allDescriptions.filterKeys {
                        categoryId == null || it.id == categoryId ||
                                (categoryId == SemanticSearchCategory.BuiltIn.People.id &&
                                        it == SemanticSearchCategory.BuiltIn.Animals)
                    }
                    val backgrounds = SemanticSearchClassifier.backgroundDescriptions.takeIf {
                        descriptions.keys.any {
                            it == SemanticSearchCategory.BuiltIn.People ||
                                    it == SemanticSearchCategory.BuiltIn.Animals
                        }
                    }.orEmpty()
                    val required = (descriptions.values.flatten() + backgrounds).toSet()
                    categoryEmbeddings.keys.retainAll(
                        (allDescriptions.values.flatten() + SemanticSearchClassifier.backgroundDescriptions).toSet()
                    )
                    val missing = required - categoryEmbeddings.keys
                    if (missing.isNotEmpty()) {
                        val encoder = modelMutex.withLock {
                            if (ClipImageSearch.isDownloaded()) ClipImageSearch.openTextEncoder() else null
                        } ?: return@use
                        encoder.use {
                            missing.forEach { description ->
                                currentCoroutineContext().ensureActive()
                                categoryEmbeddings[description] = it.encode(description)
                            }
                        }
                    }
                    SemanticSearchClassifier(
                        categoryEmbeddings = descriptions.mapValues { (_, queries) ->
                            queries.map(categoryEmbeddings::getValue)
                        },
                        backgroundEmbeddings = backgrounds.map(categoryEmbeddings::getValue),
                        thresholds = settings.categoryThresholds + settings.customCategories.associate {
                            it.id to it.threshold
                        },
                        referenceEmbeddings = categoryReferences(
                            settings = settings,
                            categoryId = categoryId,
                            media = images,
                            index = index
                        ),
                        referenceThresholds = settings.customCategories.associate {
                            it.id to it.referenceThreshold
                        }
                    )
                }
                result = index.categories(classifier, images)
            }
        }
        return result
    }

    private suspend fun categoryReferences(
        settings: SemanticSearchSettings,
        categoryId: String?,
        media: List<Media>,
        index: SemanticSearchIndex
    ): Map<String, List<FloatArray>> {
        referenceEmbeddings.keys.retainAll(settings.customCategories.flatMap { it.referenceImages }
            .toSet())
        val result = mutableMapOf<String, List<FloatArray>>()
        LazyImageEncoder().use { encoder ->
            settings.customCategories.filter { categoryId == null || it.id == categoryId }
                .forEach { category ->
                    val embeddings = mutableListOf<FloatArray>()
                    category.referenceImages.distinct().forEach reference@{ uri ->
                        currentCoroutineContext().ensureActive()
                        val fileFingerprint = runCatching {
                            context.contentResolver.openAssetFileDescriptor(uri.toUri(), "r")?.use {
                                Os.fstat(it.fileDescriptor)
                                    .run { "$st_size:$st_mtime:$st_ctime:$st_ino" }
                            }
                        }.getOrNull()
                        if (fileFingerprint == null) {
                            referenceEmbeddings.remove(uri)
                            return@reference
                        }
                        val original = uri.toUri().tryExtractOriginal()
                        val source = media.find { it.uri == uri || it.uri == original.toString() }
                            ?: runCatching {
                                context.contentResolver.query(original, null, null, null, null)
                                    ?.use { cursor ->
                                        if (cursor.moveToFirst()) cursor.getMediaFromCursor() else null
                                    }
                            }.getOrNull()
                        val sourceFingerprint = source?.run {
                            "$timestamp:$size:$width:$height:$mimeType"
                        }
                        val fingerprint = "$fileFingerprint:$sourceFingerprint"
                        val cached = referenceEmbeddings[uri]?.takeIf {
                            it.fingerprint == fingerprint
                        }
                        val embedding = source?.let(index::get) ?: cached?.embedding ?: try {
                            val image = loadImage(uri)
                            if (image == null) null else {
                                currentCoroutineContext().ensureActive()
                                encoder.encode(image)
                            }
                        } catch (exception: CancellationException) {
                            throw exception
                        } catch (_: Exception) {
                            currentCoroutineContext().ensureActive()
                            null
                        }
                        currentCoroutineContext().ensureActive()
                        if (embedding != null) {
                            embeddings.add(embedding)
                            referenceEmbeddings[uri] = ReferenceEmbedding(fingerprint, embedding)
                        } else referenceEmbeddings.remove(uri)
                    }
                    result[category.id] = embeddings
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
                categoryCache.evictAll()
                categoryEmbeddings.clear()
                referenceEmbeddings.clear()
                _state.update { SemanticSearchState(indexRevision = it.indexRevision) }
            }
        }
    }

    private suspend fun loadImage(uri: String): Bitmap? = imageGetter.getImageData(
        uri = uri,
        size = 512,
        onFailure = {}
    )?.image

    private inner class LazyImageEncoder : Closeable {
        private var encoder: ClipImageSearch.ImageEncoder? = null

        suspend fun encode(image: Bitmap): FloatArray {
            val current = encoder ?: modelMutex.withLock {
                ClipImageSearch.openImageEncoder()
            }.also { encoder = it }
            return current.encode(image)
        }

        override fun close() {
            encoder?.close()
        }
    }

    private data class CategoryCacheKey(
        val albumId: Long,
        val allowedMedia: AllowedMedia,
        val settings: SemanticSearchSettings
    )

    private data class ReferenceEmbedding(val fingerprint: String, val embedding: FloatArray) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ReferenceEmbedding

            if (fingerprint != other.fingerprint) return false
            if (!embedding.contentEquals(other.embedding)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = fingerprint.hashCode()
            result = 31 * result + embedding.contentHashCode()
            return result
        }
    }

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