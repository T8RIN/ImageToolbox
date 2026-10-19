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
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.settings.domain.model.SemanticSearchSettings
import com.t8rin.imagetoolbox.core.utils.tryExtractOriginal
import com.t8rin.imagetoolbox.feature.media_picker.data.utils.getMediaFromCursor
import com.t8rin.imagetoolbox.feature.media_picker.domain.SemanticSearchRepository
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.AllowedMedia
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Media
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.SemanticSearchCategory
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.SemanticSearchCategoryResult
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.SemanticSearchState
import com.t8rin.neural_tools.faces.ArcFace
import com.t8rin.neural_tools.faces.DetectedFace
import com.t8rin.neural_tools.faces.UltraFaceDetector
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
import java.io.File
import java.util.UUID
import javax.inject.Inject

internal class AndroidSemanticSearchRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageGetter: ImageGetter<Bitmap>,
    private val imageScaler: ImageScaler<Bitmap>,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val referenceStore: SemanticSearchReferenceStore,
    dispatchersHolder: DispatchersHolder
) : SemanticSearchRepository, DispatchersHolder by dispatchersHolder {

    private val indexMutex = Mutex()
    private val faceIndexMutex = Mutex()
    private var peopleRevision = 0L
    private val modelMutex = Mutex()
    private val searchMutex = Mutex()
    private val categoriesMutex = Mutex()
    private val faceSearchMutex = Mutex()

    private val categoryCache = LruCache<CategoryCacheKey, List<SemanticSearchCategoryResult>>(4)
    private val categoryEmbeddings = mutableMapOf<String, FloatArray>()
    private val referenceEmbeddings = mutableMapOf<String, ReferenceEmbedding>()
    private val faceReferences = mutableMapOf<String, FaceReference>()
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
                it.copy(
                    modelsReady = ClipImageSearch.isDownloaded(),
                    indexedImages = index.count(),
                    faceModelsReady = ArcFace.isDownloaded()
                )
            }
        }
    }

    override suspend fun downloadFaceModels() = perform(modelMutex) {
        _state.update {
            it.copy(
                isFacesDownloading = true,
                facesDownloadProgress = 0f,
                error = null
            )
        }
        try {
            ArcFace.download { progress ->
                _state.update { it.copy(facesDownloadProgress = progress) }
            }
        } finally {
            _state.update {
                it.copy(isFacesDownloading = false, faceModelsReady = ArcFace.isDownloaded())
            }
        }
    }

    private suspend fun editPeople(action: suspend SemanticFaceIndex.() -> Unit) =
        perform(faceIndexMutex) {
            val hiddenPeopleCount = try {
                SemanticFaceIndex(context).use { index ->
                    index.action()
                    index.hiddenCount()
                }
            } finally {
                peopleRevision++
            }
            categoryCache.evictAll()
            _state.update {
                it.copy(indexRevision = it.indexRevision + 1, hiddenPeopleCount = hiddenPeopleCount)
            }
        }

    override suspend fun updatePerson(
        id: Long,
        name: String,
        threshold: Float,
        cover: String?,
        excludedUris: Set<String>,
        restoreExcluded: Boolean,
        excludedFaces: Set<String>,
        coverFaceId: String?
    ) = editPeople {
        if (updatePerson(
                id,
                name,
                threshold,
                cover,
                excludedUris,
                restoreExcluded,
                excludedFaces,
                coverFaceId
            ) || needsRegroup()
        ) {
            regroup()
        }
    }

    override suspend fun mergePeople(from: Set<Long>, into: Long) = editPeople { merge(from, into) }

    override suspend fun rejectPeoplePair(first: Long, second: Long) = editPeople {
        rejectPair(first, second)
    }

    override suspend fun setPersonHidden(id: Long, hidden: Boolean) = editPeople {
        setHidden(id, hidden)
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

    override suspend fun index(media: List<Media>, groupPeople: Boolean) = perform(indexMutex) {
        modelMutex.withLock {
            check(ClipImageSearch.isDownloaded()) { "Download the search models first" }
        }
        val images = media.filter { it.mimeType.startsWith("image/") }.distinctBy(Media::uri)
        var pendingWrites = 0
        _state.update {
            it.copy(
                isIndexing = true,
                processedImages = 0,
                totalImages = images.size,
                failedImages = 0,
                error = null
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
            if (groupPeople && ArcFace.isDownloaded()) {
                indexFaces(images) {
                    pendingWrites++
                    if (pendingWrites >= 256) {
                        _state.update { it.copy(indexRevision = it.indexRevision + pendingWrites) }
                        pendingWrites = 0
                    }
                }
            }
        } finally {
            _state.update {
                it.copy(
                    isIndexing = false,
                    isFacesIndexing = false,
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
        minimumSimilarity: Float,
        groupPeople: Boolean
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
            if (groupPeople && _state.value.faceModelsReady) {
                SemanticFaceIndex(context).use { index ->
                    val faces = source?.let(index::embeddings)?.takeIf { it.isNotEmpty() }
                        ?: faceEmbeddings(uri)
                    val matches = index.search(faces, candidates, faceThreshold(minimumSimilarity))
                    result = (matches + result).distinctBy(Media::uri)
                }
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
    ): List<SemanticSearchCategoryResult> {
        val categories = withFaceReferences(classifyCategories(media, settings), media, settings)
        return if (settings.groupPeople && _state.value.faceModelsReady) {
            categories + withContext(ioDispatcher) {
                SemanticFaceIndex(context).use { index ->
                    _state.update { it.copy(hiddenPeopleCount = index.hiddenCount()) }
                    index.people(media)
                }
            }
        } else {
            categories
        }
    }

    override suspend fun previewCategory(
        media: List<Media>,
        settings: SemanticSearchSettings,
        categoryId: String
    ): SemanticSearchCategoryResult? = withFaceReferences(
        classifyCategories(media, settings, categoryId),
        media,
        settings,
        categoryId
    ).find { it.category.id == categoryId }

    override suspend fun prepareCategoryReferences(
        settings: SemanticSearchSettings
    ): SemanticSearchSettings = referenceStore.prepare(settings)

    override suspend fun cleanupCategoryReferences(settings: SemanticSearchSettings) =
        referenceStore.cleanup(settings)

    private fun faceThreshold(imageThreshold: Float): Float =
        (0.55f + imageThreshold - SemanticSearchSettings.DefaultImageThreshold).coerceIn(
            0.4f,
            0.85f
        )

    private suspend fun faceEmbeddings(uri: String): List<FloatArray> {
        val image = loadFaceImage(uri) ?: return emptyList()
        return UltraFaceDetector().use { detector ->
            val faces = detector.detect(image).filter { it.isUsable() }
            if (faces.isEmpty()) {
                emptyList()
            } else {
                ArcFace.openEncoder().use { encoder -> faces.map { encoder.encode(image, it) } }
            }
        }
    }

    private suspend fun withFaceReferences(
        categories: List<SemanticSearchCategoryResult>,
        media: List<Media>,
        settings: SemanticSearchSettings,
        categoryId: String? = null
    ): List<SemanticSearchCategoryResult> {
        if (!settings.groupPeople || !_state.value.faceModelsReady) return categories
        return withContext(ioDispatcher) {
            faceSearchMutex.withLock {
                val custom = settings.customCategories.filter {
                    (categoryId == null || it.id == categoryId) && it.referenceImages.isNotEmpty()
                }
                faceReferences.keys.retainAll(settings.customCategories.flatMap { it.referenceImages }
                    .toSet())
                SemanticFaceIndex(context).use { index ->
                    val matches = mutableMapOf<String, List<Media>>()
                    custom.forEach { category ->
                        val vectors = category.referenceImages.flatMap { uri ->
                            val fingerprint = runCatching {
                                context.contentResolver.openAssetFileDescriptor(uri.toUri(), "r")
                                    ?.use {
                                        Os.fstat(it.fileDescriptor)
                                            .run { "$st_size:$st_mtime:$st_ctime:$st_ino" }
                                    }
                            }.getOrNull()
                            if (fingerprint == null) {
                                faceReferences.remove(uri)
                                emptyList()
                            } else {
                                faceReferences[uri]?.takeIf { it.fingerprint == fingerprint }?.vectors
                                    ?: faceEmbeddings(uri).also {
                                        faceReferences[uri] = FaceReference(fingerprint, it)
                                    }
                            }
                        }
                        matches[category.id] = index.search(
                            vectors, media, faceThreshold(category.referenceThreshold)
                        )
                    }
                    categories.map { result ->
                        val faces = matches[result.category.id].orEmpty()
                        if (faces.isEmpty()) {
                            result
                        } else {
                            result.copy(media = (faces + result.media).distinctBy(Media::uri))
                        }
                    }
                }
            }
        }
    }

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
                                (
                                        categoryId == SemanticSearchCategory.BuiltIn.People.id &&
                                                it == SemanticSearchCategory.BuiltIn.Animals
                                        )
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
        referenceEmbeddings.keys.retainAll(
            settings.customCategories.flatMap { it.referenceImages }.toSet()
        )
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
                faceSearchMutex.withLock {
                    ArcFace.deleteModels()
                    faceReferences.clear()
                }
                SemanticSearchIndex(context).use(SemanticSearchIndex::clear)
                faceIndexMutex.withLock {
                    SemanticFaceIndex(context).use(SemanticFaceIndex::clear)
                    peopleRevision++
                }
                categoryCache.evictAll()
                categoryEmbeddings.clear()
                referenceEmbeddings.clear()
                _state.update { SemanticSearchState(indexRevision = it.indexRevision) }
            }
        }
    }

    private suspend fun indexFaces(images: List<Media>, onIndexed: () -> Unit) {
        _state.update { it.copy(isFacesIndexing = true, processedImages = 0) }
        SemanticFaceIndex(context).use { index ->
            UltraFaceDetector().use { detector ->
                ArcFace.openEncoder().use { encoder ->
                    images.forEachIndexed { position, media ->
                        currentCoroutineContext().ensureActive()
                        if (index.needsScan(media)) {
                            loadFaceImage(media.uri)?.let { image ->
                                val faces = detector.detect(image).filter { it.isUsable() }
                                saveFaces(index, media, image, faces, encoder)
                                onIndexed()
                            }
                        }
                        _state.update { it.copy(processedImages = position + 1) }
                    }
                    faceIndexMutex.withLock {
                        if (index.needsRegroup()) {
                            index.regroup()
                            onIndexed()
                        }
                    }
                }
            }
        }
    }

    private suspend fun saveFaces(
        index: SemanticFaceIndex,
        media: Media,
        image: Bitmap,
        faces: List<DetectedFace>,
        encoder: ArcFace.Encoder
    ) {
        val files = mutableListOf<File>()
        var saved = false
        try {
            val entries = faces.map { face ->
                val vector = encoder.encode(image, face)
                val id = UUID.randomUUID().toString()
                val file = File(index.thumbnails, "$id.webp")
                files += file
                saveFaceThumbnail(image, face, file)
                SemanticFaceIndex.Face(
                    id = id,
                    vector = vector,
                    thumbnail = file.absolutePath,
                    bounds = face.bounds,
                    confidence = face.confidence
                )
            }
            faceIndexMutex.withLock {
                index.invalidatePeopleCache(peopleRevision)
                index.replace(media, entries)
                saved = true
            }
        } finally {
            if (!saved) files.forEach(File::delete)
        }
    }

    private fun DetectedFace.isUsable(): Boolean =
        bounds.width() * bounds.height() * confidence >= FaceClusterer.MIN_FACE_QUALITY

    private suspend fun saveFaceThumbnail(image: Bitmap, face: DetectedFace, file: File) {
        val bounds = face.bounds
        val side = maxOf(bounds.width() * image.width, bounds.height() * image.height)
            .toInt().coerceIn(1, minOf(image.width, image.height))
        val x = (bounds.centerX() * image.width - side / 2f)
            .toInt().coerceIn(0, image.width - side)
        val y = (bounds.centerY() * image.height - side / 2f)
            .toInt().coerceIn(0, image.height - side)
        val crop = Bitmap.createBitmap(image, x, y, side, side)
        try {
            val thumbnail = imageScaler.scaleImage(crop, 160, 160)
            try {
                val bytes = imageCompressor.compress(
                    image = thumbnail,
                    imageFormat = ImageFormat.Webp.Lossy,
                    quality = Quality.Base(85)
                )
                currentCoroutineContext().ensureActive()
                check(bytes.isNotEmpty()) { "Could not save face thumbnail" }
                file.writeBytes(bytes)
            } finally {
                if (thumbnail !== crop) thumbnail.recycle()
            }
        } finally {
            if (crop !== image) crop.recycle()
        }
    }

    private suspend fun loadFaceImage(uri: String): Bitmap? = imageGetter.getImageData(
        uri = uri,
        size = 1600,
        onFailure = {}
    )?.image

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

    private class FaceReference(val fingerprint: String, val vectors: List<FloatArray>)

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