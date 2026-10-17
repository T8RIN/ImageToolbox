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
package com.t8rin.imagetoolbox.feature.media_picker.presentation.screenLogic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.settings.domain.model.SettingsState
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.feature.media_picker.data.utils.getDate
import com.t8rin.imagetoolbox.feature.media_picker.domain.MediaRetriever
import com.t8rin.imagetoolbox.feature.media_picker.domain.SemanticSearchRepository
import com.t8rin.imagetoolbox.feature.media_picker.domain.SemanticSearchScheduler
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Album
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.AlbumState
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.AllowedMedia
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Media
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.MediaDateGroup
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.MediaDisplaySettings
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.MediaItem
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.MediaOrder
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.MediaState
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.SemanticSearchCategory
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.SemanticSearchCategoryResult
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.groupMedia
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.mergeMediaSearchResults
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.searchByName
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MediaPickerComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    private val settingsManager: SettingsManager,
    private val mediaRetriever: MediaRetriever,
    private val semanticSearchRepository: SemanticSearchRepository,
    private val semanticSearchScheduler: SemanticSearchScheduler,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    private val _settingsState = mutableStateOf(SettingsState.Default)
    val settingsState: SettingsState by _settingsState

    val selectedMedia = mutableStateListOf<Media>()

    private val sourceMediaState = MutableStateFlow(MediaState())
    private val searchKeyword = MutableStateFlow("")
    private val _isSemanticSearchEnabled = MutableStateFlow(false)
    val isSemanticSearchEnabled = _isSemanticSearchEnabled.asStateFlow()

    val semanticSearchState = semanticSearchRepository.state
    private val _isSemanticSearching = MutableStateFlow(false)
    val isSemanticSearching = _isSemanticSearching.asStateFlow()
    private val _semanticSearchError = MutableStateFlow<String?>(null)
    val semanticSearchError = _semanticSearchError.asStateFlow()
    private val semanticSearchRevision = MutableStateFlow(0)
    private var semanticSearchJob: Job? by smartJob()
    private val _semanticSearchCategories =
        MutableStateFlow<List<SemanticSearchCategoryResult>>(emptyList())
    val semanticSearchCategories = _semanticSearchCategories.asStateFlow()
    private val _selectedSemanticCategory = MutableStateFlow<SemanticSearchCategory?>(null)
    val selectedSemanticCategory = _selectedSemanticCategory.asStateFlow()
    private val _semanticSearchImage = MutableStateFlow<String?>(null)
    val semanticSearchImage = _semanticSearchImage.asStateFlow()

    fun searchSimilarImages(uri: String) {
        if (!_isSemanticSearchEnabled.value || !semanticSearchState.value.modelsReady) return
        searchKeyword.value = ""
        _semanticSearchError.value = null
        _semanticSearchImage.value = uri
    }

    fun clearSearchImage() {
        _semanticSearchImage.value = null
        _semanticSearchError.value = null
    }

    fun selectSemanticCategory(category: SemanticSearchCategory?) {
        _selectedSemanticCategory.value = category
    }

    fun setSemanticSearchAutoIndexing(enabled: Boolean) = runSemanticSearchOperation {
        settingsManager.setSemanticSearchEnabled(enabled)
        if (enabled) {
            semanticSearchScheduler.refresh()
        } else {
            clearSearchImage()
            _isSemanticSearchEnabled.value = false
            semanticSearchScheduler.stop()
        }
    }

    fun clearSemanticSearch() = runSemanticSearchOperation {
        clearSearchImage()
        settingsManager.setSemanticSearchEnabled(false)
        _isSemanticSearchEnabled.value = false
        semanticSearchScheduler.stop()
        semanticSearchRepository.clear()
    }

    fun retrySemanticSearch() = runSemanticSearchOperation {
        _semanticSearchError.value = null
        semanticSearchScheduler.stop()
        semanticSearchScheduler.refresh()
    }

    private fun runSemanticSearchOperation(action: suspend () -> Unit) {
        semanticSearchJob = componentScope.launch {
            _semanticSearchError.value = null
            try {
                action()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _semanticSearchError.value = e.message ?: e.toString()
            } finally {
                semanticSearchRevision.value++
            }
        }
    }

    private val _displaySettings = MutableStateFlow(MediaDisplaySettings())
    val displaySettings = _displaySettings.asStateFlow()

    fun updateDisplaySettings(settings: MediaDisplaySettings) {
        _displaySettings.value = settings
    }

    private val _mediaState = MutableStateFlow(MediaState())
    val mediaState = _mediaState.asStateFlow()

    private val _filteredMediaState = MutableStateFlow(MediaState())
    val filteredMediaState = _filteredMediaState.asStateFlow()

    private val _albumsState = MutableStateFlow(AlbumState())
    val albumsState = _albumsState.asStateFlow()

    fun init(allowedMedia: AllowedMedia) {
        this.allowedMedia = allowedMedia
        if (allowedMedia is AllowedMedia.Videos) _isSemanticSearchEnabled.value = false
        getMedia(selectedAlbumId, allowedMedia)
        getAlbums(allowedMedia)
    }

    fun getAlbum(albumId: Long) {
        _selectedSemanticCategory.value = null
        _semanticSearchCategories.value = emptyList()
        this.selectedAlbumId = albumId
        getMedia(albumId, allowedMedia)
        getAlbums(allowedMedia)
    }

    private var allowedMedia: AllowedMedia = AllowedMedia.Photos(null)

    private var selectedAlbumId: Long = -1L

    private val emptyAlbum = Album(
        id = -1,
        label = "All",
        uri = "",
        pathToThumbnail = "",
        timestamp = 0,
        relativePath = ""
    )

    private var albumJob: Job? by smartJob()

    private fun getAlbums(allowedMedia: AllowedMedia) {
        albumJob = componentScope.launch {
            mediaRetriever.getAlbumsWithType(allowedMedia)
                .flowOn(defaultDispatcher)
                .collectLatest { result ->
                    val data = result.getOrNull() ?: emptyList()
                    val error = if (result.isFailure) result.exceptionOrNull()?.message
                        ?: "An error occurred" else ""
                    if (data.isEmpty()) {
                        return@collectLatest _albumsState.emit(
                            AlbumState(
                                albums = listOf(emptyAlbum),
                                error = error
                            )
                        )
                    }
                    val albums = mutableListOf<Album>().apply {
                        add(emptyAlbum)
                        addAll(data)
                    }
                    _albumsState.emit(AlbumState(albums = albums, error = error))
                }
        }
    }

    private var mediaGettingJob: Job? by smartJob()

    private fun getMedia(
        albumId: Long,
        allowedMedia: AllowedMedia
    ) {
        mediaGettingJob = componentScope.launch {
            sourceMediaState.emit(sourceMediaState.value.copy(isLoading = true))
            mediaRetriever.mediaFlowWithType(albumId, allowedMedia)
                .flowOn(defaultDispatcher)
                .collectLatest { result ->
                    val data =
                        if (allowedMedia is AllowedMedia.Photos && allowedMedia.ext != null && allowedMedia.ext != "*") {
                            result.getOrNull()?.filter { it.uri.endsWith(allowedMedia.ext) }
                        } else {
                            result.getOrNull()
                        }?.distinctBy { it.id } ?: emptyList()

                    val error = if (result.isFailure) result.exceptionOrNull()?.message
                        ?: "An error occurred" else ""
                    sourceMediaState.emit(
                        MediaState(media = data, error = error, isLoading = false)
                    )
                    if (result.isSuccess) semanticSearchScheduler.refresh()
                }
        }
    }

    fun filterMedia(
        searchKeyword: String,
        isForceReset: Boolean
    ) {
        val keyword = if (isForceReset) "" else searchKeyword
        if (keyword.isNotBlank()) clearSearchImage()
        if (this.searchKeyword.value != keyword) _semanticSearchError.value = null
        this.searchKeyword.value = keyword
    }

    private fun List<Media>.mapMedia(
        settings: MediaDisplaySettings
    ) = groupMedia(settings) { timestamp, group ->
        when (group) {
            MediaDateGroup.Year -> SimpleDateFormat("yyyy", Locale.getDefault())
                .format(Date(timestamp * 1000))

            MediaDateGroup.Month -> SimpleDateFormat("LLLL yyyy", Locale.getDefault())
                .format(Date(timestamp * 1000))

            else -> timestamp.getDate()
        }
    }

    private fun MediaState.withMappedMedia(
        settings: MediaDisplaySettings
    ): MediaState {
        val mapped = media.mapMedia(settings)
        return copy(
            media = if (settings.dateGroup == MediaDateGroup.None) media else {
                mapped.filterIsInstance<MediaItem.MediaViewItem>().map { it.media }
            },
            mappedMedia = mapped
        )
    }

    init {
        semanticSearchState.map { it.error }.distinctUntilChanged().onEach {
            if (it != null) _semanticSearchError.value = it
        }.launchIn(componentScope)
        componentScope.launch {
            combine(
                sourceMediaState,
                _isSemanticSearchEnabled,
                semanticSearchState.map {
                    CategoryIndexState(
                        modelsReady = it.modelsReady,
                        indexedImages = it.indexedImages,
                        indexedBatch = it.processedImages / 256,
                        isIndexing = it.isIndexing
                    )
                }.distinctUntilChanged()
            ) { media, enabled, index ->
                CategoryRequest(media, enabled, index)
            }.collectLatest { (media, enabled, index) ->
                if (!enabled || !index.modelsReady || index.indexedImages == 0) {
                    _semanticSearchCategories.value = emptyList()
                    _selectedSemanticCategory.value = null
                    return@collectLatest
                }
                if (media.isLoading) return@collectLatest
                try {
                    delay(500)
                    val categories = semanticSearchRepository.categories(media.media)
                    currentCoroutineContext().ensureActive()
                    _semanticSearchCategories.value = categories
                    if (categories.none { it.category == _selectedSemanticCategory.value }) {
                        _selectedSemanticCategory.value = null
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    _semanticSearchError.value = e.message ?: e.toString()
                }
            }
        }
        componentScope.launch {
            var lastSemanticQuery: String? = null
            var lastSemanticAlbum: Long? = null
            var lastSemanticCategory: SemanticSearchCategory? = null
            var lastSemanticImage: String? = null
            val sortedMediaState = combine(
                sourceMediaState,
                _displaySettings.map { it.mediaOrder }.distinctUntilChanged()
            ) { state, order -> state to order }
                .mapLatest { (state, order) ->
                    withContext(ioDispatcher) {
                        state.copy(media = order.sortMedia(state.media)) to order
                    }
                }
            val semanticRequest = combine(
                _isSemanticSearchEnabled,
                semanticSearchState,
                semanticSearchRevision
            ) { enabled, state, revision ->
                SemanticSearchRequest(
                    enabled = enabled,
                    modelsReady = state.modelsReady,
                    isDownloading = state.isDownloading,
                    revision = revision,
                    indexedBatch = state.processedImages / 32,
                    isIndexing = state.isIndexing
                )
            }.distinctUntilChanged()
            val selectedCategory = combine(
                _selectedSemanticCategory,
                _semanticSearchCategories
            ) { category, categories -> categories.firstOrNull { it.category == category } }
            val searchInput = combine(searchKeyword, _semanticSearchImage) { keyword, image ->
                keyword to image
            }
            combine(
                sortedMediaState,
                _displaySettings,
                searchInput,
                semanticRequest,
                selectedCategory
            ) { state, settings, input, semantic, category ->
                SearchRequest(
                    state.first,
                    state.second,
                    settings,
                    input.first,
                    semantic,
                    category,
                    input.second
                )
            }.collectLatest { request ->
                val (state, order, settings, keyword, semantic, category, image) = request
                if (order != settings.mediaOrder) return@collectLatest
                _isSemanticSearching.value = false
                val mapped = withContext(ioDispatcher) { state.withMappedMedia(settings) }
                _mediaState.emit(mapped)
                val (enabled, modelsReady, isDownloading) = semantic
                val searchState = withContext(ioDispatcher) {
                    if (category != null && enabled) {
                        val categoryUris = category.media.mapTo(hashSetOf(), Media::uri)
                        state.copy(media = state.media.filter { it.uri in categoryUris })
                    } else state
                }
                val nameMatches = withContext(ioDispatcher) {
                    if (image == null) searchState.media.searchByName(keyword) else emptyList()
                }
                val isFilenameOnly = image == null &&
                        (keyword.isBlank() || keyword.startsWith("*") || keyword.endsWith("*"))
                if (!enabled || isFilenameOnly ||
                    !modelsReady || isDownloading || state.isLoading
                ) {
                    lastSemanticQuery = null
                    val filtered = withContext(ioDispatcher) {
                        if (image == null && keyword.isBlank() && searchState === state) mapped else {
                            searchState.copy(media = nameMatches).withMappedMedia(settings)
                        }
                    }
                    _filteredMediaState.emit(filtered)
                } else {
                    val searchSettings = settings.copy(dateGroup = MediaDateGroup.None)
                    _isSemanticSearching.value = true
                    if (lastSemanticQuery != keyword || lastSemanticAlbum != selectedAlbumId ||
                        lastSemanticCategory != category?.category || lastSemanticImage != image
                    ) {
                        _filteredMediaState.emit(
                            searchState.copy(media = nameMatches).withMappedMedia(searchSettings)
                        )
                    }
                    lastSemanticQuery = keyword
                    lastSemanticAlbum = selectedAlbumId
                    lastSemanticCategory = category?.category
                    lastSemanticImage = image
                    val source = sourceMediaState.value
                    try {
                        delay(350)
                        val results = if (image == null) {
                            semanticSearchRepository.search(keyword, searchState.media)
                        } else {
                            semanticSearchRepository.searchByImage(image, searchState.media)
                        }
                        currentCoroutineContext().ensureActive()
                        if (source != sourceMediaState.value || keyword != searchKeyword.value ||
                            image != _semanticSearchImage.value ||
                            !_isSemanticSearchEnabled.value
                        ) return@collectLatest
                        _filteredMediaState.emit(
                            searchState.copy(
                                media = mergeMediaSearchResults(nameMatches, results)
                            ).withMappedMedia(searchSettings)
                        )
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        _semanticSearchError.value = e.message ?: e.toString()
                        _filteredMediaState.emit(
                            searchState.copy(media = nameMatches).withMappedMedia(searchSettings)
                        )
                    } finally {
                        _isSemanticSearching.value = false
                    }
                }
            }
        }
        runSemanticSearchOperation(semanticSearchRepository::refresh)
        runBlocking {
            _settingsState.value = settingsManager.getSettingsState()
        }
        _isSemanticSearchEnabled.value = settingsState.isSemanticSearchEnabled
        settingsManager.settingsState.onEach {
            if (settingsState.isSemanticSearchEnabled != it.isSemanticSearchEnabled) {
                if (!it.isSemanticSearchEnabled) clearSearchImage()
                _isSemanticSearchEnabled.value = it.isSemanticSearchEnabled &&
                        allowedMedia !is AllowedMedia.Videos
            }
            _settingsState.value = it
        }.launchIn(componentScope)
    }

    private data class SearchRequest(
        val state: MediaState,
        val order: MediaOrder,
        val settings: MediaDisplaySettings,
        val keyword: String,
        val semantic: SemanticSearchRequest,
        val category: SemanticSearchCategoryResult?,
        val image: String?
    )

    private data class CategoryRequest(
        val media: MediaState,
        val enabled: Boolean,
        val index: CategoryIndexState
    )

    private data class CategoryIndexState(
        val modelsReady: Boolean,
        val indexedImages: Int,
        val indexedBatch: Int,
        val isIndexing: Boolean
    )

    private data class SemanticSearchRequest(
        val enabled: Boolean,
        val modelsReady: Boolean,
        val isDownloading: Boolean,
        val revision: Int,
        val indexedBatch: Int,
        val isIndexing: Boolean
    )

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext
        ): MediaPickerComponent
    }

}