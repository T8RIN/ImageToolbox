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
import com.t8rin.imagetoolbox.core.settings.domain.model.CustomSemanticSearchCategory
import com.t8rin.imagetoolbox.core.settings.domain.model.SemanticSearchSettings
import com.t8rin.imagetoolbox.core.settings.domain.model.SettingsState
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.utils.makeLog
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
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.searchByPeople
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
    private val categoryScope = MutableStateFlow(
        CategoryScope(-1L, AllowedMedia.Photos(null))
    )
    private var readyCategoryScope: CategoryScope? = null
    private val _semanticSearchCategories =
        MutableStateFlow<List<SemanticSearchCategoryResult>>(emptyList())
    val semanticSearchCategories = _semanticSearchCategories.asStateFlow()
    private val _isSemanticSearchCategoriesLoading = MutableStateFlow(false)
    val isSemanticSearchCategoriesLoading = _isSemanticSearchCategoriesLoading.asStateFlow()
    val semanticSearchPreviewMedia = mediaRetriever.getMediaByType(AllowedMedia.Photos(null))
        .map { result ->
            MediaState(
                media = result.getOrNull()?.distinctBy { it.id }.orEmpty(),
                error = result.exceptionOrNull()?.message.orEmpty(),
                isLoading = false
            )
        }
        .flowOn(ioDispatcher)
        .stateIn(
            scope = componentScope,
            started = SharingStarted.WhileSubscribed(5_000, replayExpirationMillis = 0),
            initialValue = MediaState()
        )
    private val _showPeopleGroups = MutableStateFlow(false)
    val showPeopleGroups = _showPeopleGroups.asStateFlow()

    fun showPeopleGroups(show: Boolean) {
        _showPeopleGroups.value = show
        if (show) {
            searchKeyword.value = ""
            clearSearchImage()
            selectSemanticCategory(null)
        }
    }

    private var faceDownloadJob: Job? by smartJob()

    fun setFaceGrouping(enabled: Boolean) {
        faceDownloadJob = componentScope.launch {
            try {
                if (enabled) semanticSearchRepository.downloadFaceModels()
                if (saveSemanticSearchSettings { it.copy(groupPeople = enabled) }) {
                    if (enabled) semanticSearchScheduler.refresh()
                    else {
                        _showPeopleGroups.value = false
                        if (_selectedSemanticCategory.value?.startsWith("person:") == true) {
                            selectSemanticCategory(null)
                        }
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                AppToastHost.showFailureToast(e)
            }
        }
    }

    private val _editingPersonId = mutableStateOf<String?>(null)
    val editingPersonId: String? by _editingPersonId
    private val _isSavingPersonSettings = mutableStateOf(false)
    val isSavingPersonSettings: Boolean by _isSavingPersonSettings

    fun showPersonSettings(id: String?) {
        if (!isSavingPersonSettings) _editingPersonId.value = id
    }

    fun savePersonSettings(action: suspend () -> Boolean) {
        if (isSavingPersonSettings) return
        _isSavingPersonSettings.value = true
        componentScope.launch {
            try {
                if (action()) _editingPersonId.value = null
            } finally {
                _isSavingPersonSettings.value = false
            }
        }
    }

    private val _resolvingPeoplePair = mutableStateOf<Pair<Long, Long>?>(null)
    val resolvingPeoplePair: Pair<Long, Long>? by _resolvingPeoplePair
    private val _resolvedPeoplePairs = mutableStateOf<Set<Pair<Long, Long>>>(emptySet())
    val resolvedPeoplePairs: Set<Pair<Long, Long>> by _resolvedPeoplePairs

    fun resolvePeoplePair(first: Long, second: Long, samePerson: Boolean) {
        if (resolvingPeoplePair != null || first == second) return
        _resolvingPeoplePair.value = first to second
        componentScope.launch {
            try {
                val saved = editPerson {
                    if (samePerson) semanticSearchRepository.mergePeople(setOf(second), first)
                    else semanticSearchRepository.rejectPeoplePair(first, second)
                }
                if (saved) {
                    val others = if (samePerson) {
                        _semanticSearchCategories.value.mapNotNull {
                            (it.category as? SemanticSearchCategory.Person)?.personId
                        }
                    } else listOf(first)
                    _resolvedPeoplePairs.value += others.filter { it != second }.map {
                        minOf(it, second) to maxOf(it, second)
                    }
                }
            } finally {
                _resolvingPeoplePair.value = null
            }
        }
    }

    suspend fun savePerson(
        id: Long,
        name: String,
        threshold: Float,
        cover: String? = null,
        excludedUris: Set<String> = emptySet(),
        restoreExcluded: Boolean = false,
        excludedFaces: Set<String> = emptySet(),
        coverFaceId: String? = null
    ): Boolean = editPerson {
        semanticSearchRepository.updatePerson(
            id,
            name,
            threshold,
            cover,
            excludedUris,
            restoreExcluded,
            excludedFaces,
            coverFaceId
        )
    }

    suspend fun mergePeople(from: Set<Long>, into: Long): Boolean =
        editPerson { semanticSearchRepository.mergePeople(from, into) }

    suspend fun setPersonHidden(id: Long, hidden: Boolean): Boolean =
        editPerson { semanticSearchRepository.setPersonHidden(id, hidden) }

    private suspend fun editPerson(action: suspend () -> Unit): Boolean = try {
        action()
        semanticSearchRevision.value++
        true
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        AppToastHost.showFailureToast(e)
        false
    }

    private val _selectedSemanticCategory = MutableStateFlow<String?>(null)
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

    fun selectSemanticCategory(category: String?) {
        if (_selectedSemanticCategory.value == category) return
        _filteredMediaState.value = _filteredMediaState.value.copy(isLoading = true)
        _selectedSemanticCategory.value = category
        if (category?.startsWith("person:") == true) _showPeopleGroups.value = false
    }

    private fun prepareSemanticCategories(
        scope: CategoryScope,
        settings: SemanticSearchSettings
    ) {
        if (readyCategoryScope != scope) {
            val cached = semanticSearchRepository.getCachedCategories(
                albumId = scope.albumId,
                allowedMedia = scope.allowedMedia,
                settings = settings
            )
            if (cached != null) {
                updateSemanticCategories(scope, cached)
            } else {
                _isSemanticSearchCategoriesLoading.value = true
                _semanticSearchCategories.value = emptyList()
                readyCategoryScope = null
            }
        }
    }

    private fun updateSemanticCategories(
        scope: CategoryScope,
        categories: List<SemanticSearchCategoryResult>
    ) {
        _semanticSearchCategories.value = categories
        readyCategoryScope = scope
        _isSemanticSearchCategoriesLoading.value = false
        if (categories.none { it.category.id == _selectedSemanticCategory.value }) {
            _selectedSemanticCategory.value = null
        }
    }

    private fun SemanticSearchSettings.forCategories() = copy(
        textThreshold = SemanticSearchSettings.DefaultTextThreshold,
        imageThreshold = SemanticSearchSettings.DefaultImageThreshold
    )

    private val semanticSearchSettings = settingsManager.settingsState
        .map { it.semanticSearchSettings }
        .distinctUntilChanged()

    fun updateSemanticSearchSettings(
        transform: (SemanticSearchSettings) -> SemanticSearchSettings
    ) {
        componentScope.launch { saveSemanticSearchSettings(transform) }
    }

    suspend fun saveSemanticSearchSettings(
        transform: (SemanticSearchSettings) -> SemanticSearchSettings
    ): Boolean = try {
        semanticSearchSettingsMutex.withLock {
            val settings = settingsManager.getSettingsState().semanticSearchSettings
            try {
                val prepared = semanticSearchRepository.prepareCategoryReferences(
                    settings = transform(settings).normalized()
                )
                if (prepared != settings) {
                    settingsManager.setSemanticSearchSettings(prepared)
                }
            } finally {
                withContext(NonCancellable) {
                    runCatching {
                        semanticSearchRepository.cleanupCategoryReferences(
                            settingsManager.getSettingsState().semanticSearchSettings
                        )
                    }.onFailure { it.makeLog("SemanticSearchReferences") }
                }
            }
        }
        true
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        _semanticSearchError.value = e.message ?: e.toString()
        false
    }

    suspend fun previewSemanticSearchCategory(
        categoryId: String,
        category: CustomSemanticSearchCategory?,
        threshold: Float
    ): SemanticSearchCategoryResult? {
        val source = semanticSearchPreviewMedia.value
        if (!_isSemanticSearchEnabled.value || !semanticSearchState.value.modelsReady ||
            source.isLoading
        ) return null
        if (source.error.isNotEmpty()) {
            _semanticSearchError.value = source.error
            return null
        }
        val settings = settingsState.semanticSearchSettings
        val previewSettings = if (category == null) {
            settings.copy(categoryThresholds = settings.categoryThresholds + (categoryId to threshold))
        } else {
            settings.copy(
                customCategories = settings.customCategories.filterNot { it.id == categoryId } +
                        category.copy(name = category.name.ifBlank { categoryId })
            )
        }
        return try {
            semanticSearchRepository.previewCategory(source.media, previewSettings, categoryId)
                .takeIf { source == semanticSearchPreviewMedia.value && _isSemanticSearchEnabled.value }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _semanticSearchError.value = e.message ?: e.toString()
            null
        }
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
        faceDownloadJob?.cancel()
        saveSemanticSearchSettings { it.copy(groupPeople = false) }
        clearSearchImage()
        settingsManager.setSemanticSearchEnabled(false)
        _isSemanticSearchEnabled.value = false
        semanticSearchScheduler.stop()
        semanticSearchRepository.clear()
        _resolvedPeoplePairs.value = emptySet()
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
        if (allowedMedia is AllowedMedia.Videos) {
            _isSemanticSearchEnabled.value = false
            _showPeopleGroups.value = false
        }
        getMedia(selectedAlbumId, allowedMedia)
        getAlbums(allowedMedia)
    }

    fun getAlbum(albumId: Long) {
        _showPeopleGroups.value = false
        _selectedSemanticCategory.value = null
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
        sourceMediaState.value = sourceMediaState.value.copy(isLoading = true)
        val scope = CategoryScope(albumId, allowedMedia)
        categoryScope.value = scope
        if (_isSemanticSearchEnabled.value && semanticSearchState.value.modelsReady &&
            semanticSearchState.value.indexedImages > 0
        ) {
            prepareSemanticCategories(scope, settingsState.semanticSearchSettings.forCategories())
        }
        mediaGettingJob = componentScope.launch {
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
                        hasIndexedImages = it.indexedImages > 0,
                        indexRevision = it.indexRevision,
                        faceModelsReady = it.faceModelsReady
                    )
                }.distinctUntilChanged(),
                semanticSearchSettings.map { it.forCategories() }.distinctUntilChanged(),
                categoryScope
            ) { media, enabled, index, settings, scope ->
                CategoryRequest(media, enabled, index, settings, scope)
            }.collectLatest { (media, enabled, index, settings, scope) ->
                if (scope != categoryScope.value || media != sourceMediaState.value ||
                    enabled != _isSemanticSearchEnabled.value
                ) return@collectLatest
                if (!settings.groupPeople) _showPeopleGroups.value = false
                if (!enabled || !index.modelsReady || !index.hasIndexedImages) {
                    _showPeopleGroups.value = false
                    _semanticSearchCategories.value = emptyList()
                    readyCategoryScope = null
                    _selectedSemanticCategory.value = null
                    _isSemanticSearchCategoriesLoading.value = false
                    return@collectLatest
                }
                prepareSemanticCategories(scope, settings)
                if (media.isLoading) return@collectLatest
                if (media.error.isNotEmpty()) {
                    _isSemanticSearchCategoriesLoading.value = false
                    return@collectLatest
                }
                try {
                    _isSemanticSearchCategoriesLoading.value = readyCategoryScope != scope
                    if (readyCategoryScope == scope) delay(500)
                    val categories = semanticSearchRepository.categories(media.media, settings)
                    currentCoroutineContext().ensureActive()
                    if (scope != categoryScope.value || media != sourceMediaState.value ||
                        !_isSemanticSearchEnabled.value
                    ) return@collectLatest
                    semanticSearchRepository.cacheCategories(
                        albumId = scope.albumId,
                        allowedMedia = scope.allowedMedia,
                        settings = settings,
                        categories = categories
                    )
                    updateSemanticCategories(scope, categories)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    _isSemanticSearchCategoriesLoading.value = false
                    _semanticSearchError.value = e.message ?: e.toString()
                }
            }
        }
        componentScope.launch {
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
                semanticSearchRevision,
                semanticSearchSettings.map {
                    it.copy(categoryThresholds = emptyMap(), customCategories = emptyList())
                }.distinctUntilChanged()
            ) { enabled, state, revision, settings ->
                SemanticSearchRequest(
                    enabled = enabled,
                    modelsReady = state.modelsReady,
                    isDownloading = state.isDownloading,
                    revision = revision,
                    indexedBatch = state.processedImages / 32,
                    isIndexing = state.isIndexing,
                    settings = settings
                )
            }.distinctUntilChanged()
            val selectedCategory = combine(
                _selectedSemanticCategory,
                _semanticSearchCategories,
                _isSemanticSearchCategoriesLoading
            ) { id, categories, isLoading ->
                val result = categories.firstOrNull { it.category.id == id }
                CategorySelection(
                    id = id,
                    result = result,
                    isLoading = id != null && result == null && isLoading,
                    namedPeople = categories.filter {
                        val person = it.category as? SemanticSearchCategory.Person
                        person != null && !person.hidden && person.name.isNotBlank()
                    }
                )
            }.distinctUntilChanged()
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
            }.onCompletion {
                _isSemanticSearching.value = false
            }.collectLatest { request ->
                val (state, order, settings, keyword, semantic, selection, image) = request
                val category = selection.result
                val (enabled, modelsReady, isDownloading) = semantic
                val isFilenameOnly = image == null &&
                        (keyword.isBlank() || keyword.startsWith("*") || keyword.endsWith("*"))
                val canSearch = enabled && !isFilenameOnly && modelsReady &&
                        !isDownloading && state.error.isEmpty()
                _isSemanticSearching.value = canSearch
                if (order != settings.mediaOrder) return@collectLatest
                val mapped = withContext(ioDispatcher) { state.withMappedMedia(settings) }
                _mediaState.emit(mapped)
                if (selection.id != _selectedSemanticCategory.value) return@collectLatest
                if (enabled && selection.id != null && category == null) {
                    if (selection.isLoading) {
                        _filteredMediaState.value = _filteredMediaState.value.copy(isLoading = true)
                    } else {
                        _selectedSemanticCategory.value = null
                    }
                    return@collectLatest
                }
                if (canSearch && state.isLoading) return@collectLatest
                val searchState = withContext(ioDispatcher) {
                    if (category != null && enabled) {
                        val categoryUris = category.media.mapTo(hashSetOf(), Media::uri)
                        state.copy(media = state.media.filter { it.uri in categoryUris })
                    } else state
                }
                val nameMatches = withContext(ioDispatcher) {
                    if (image == null) {
                        val filenames = searchState.media.searchByName(keyword)
                        if (enabled && semantic.settings.groupPeople) {
                            mergeMediaSearchResults(
                                searchState.media.searchByPeople(keyword, selection.namedPeople),
                                filenames
                            )
                        } else {
                            filenames
                        }
                    } else {
                        emptyList()
                    }
                }
                if (!canSearch) {
                    val filtered = withContext(ioDispatcher) {
                        if (image == null && keyword.isBlank() && searchState === state) mapped else {
                            searchState.copy(media = nameMatches).withMappedMedia(settings)
                        }
                    }
                    if (selection.id != _selectedSemanticCategory.value) return@collectLatest
                    _filteredMediaState.emit(filtered)
                } else {
                    val searchSettings = settings.copy(dateGroup = MediaDateGroup.None)
                    val source = sourceMediaState.value
                    try {
                        delay(350)
                        val results = if (image == null) {
                            semanticSearchRepository.search(
                                query = keyword,
                                media = searchState.media,
                                minimumSimilarity = semantic.settings.textThreshold
                            )
                        } else {
                            semanticSearchRepository.searchByImage(
                                uri = image,
                                media = searchState.media,
                                minimumSimilarity = semantic.settings.imageThreshold,
                                groupPeople = semantic.settings.groupPeople
                            )
                        }
                        val filtered = withContext(ioDispatcher) {
                            searchState.copy(
                                media = mergeMediaSearchResults(nameMatches, results)
                            ).withMappedMedia(searchSettings)
                        }
                        currentCoroutineContext().ensureActive()
                        if (source != sourceMediaState.value || keyword != searchKeyword.value ||
                            image != _semanticSearchImage.value ||
                            selection.id != _selectedSemanticCategory.value ||
                            !_isSemanticSearchEnabled.value
                        ) return@collectLatest
                        _filteredMediaState.emit(filtered)
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        if (selection.id != _selectedSemanticCategory.value) return@collectLatest
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
        val category: CategorySelection,
        val image: String?
    )

    private data class CategorySelection(
        val id: String?,
        val result: SemanticSearchCategoryResult?,
        val isLoading: Boolean,
        val namedPeople: List<SemanticSearchCategoryResult>
    )

    private data class CategoryScope(val albumId: Long, val allowedMedia: AllowedMedia)

    private data class CategoryRequest(
        val media: MediaState,
        val enabled: Boolean,
        val index: CategoryIndexState,
        val settings: SemanticSearchSettings,
        val scope: CategoryScope
    )

    private data class CategoryIndexState(
        val modelsReady: Boolean,
        val hasIndexedImages: Boolean,
        val indexRevision: Long,
        val faceModelsReady: Boolean
    )

    private data class SemanticSearchRequest(
        val enabled: Boolean,
        val modelsReady: Boolean,
        val isDownloading: Boolean,
        val revision: Int,
        val indexedBatch: Int,
        val isIndexing: Boolean,
        val settings: SemanticSearchSettings
    )

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext
        ): MediaPickerComponent
    }

    private companion object {
        val semanticSearchSettingsMutex = Mutex()
    }

}