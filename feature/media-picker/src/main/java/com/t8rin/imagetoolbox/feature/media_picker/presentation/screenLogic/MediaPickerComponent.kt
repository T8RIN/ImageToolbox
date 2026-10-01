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
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Album
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.AlbumState
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.AllowedMedia
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Media
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.MediaDateGroup
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.MediaDisplaySettings
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.MediaItem
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.MediaState
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.groupMedia
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
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
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    private val _settingsState = mutableStateOf(SettingsState.Default)
    val settingsState: SettingsState by _settingsState

    val selectedMedia = mutableStateListOf<Media>()

    private val sourceMediaState = MutableStateFlow(MediaState())
    private val searchKeyword = MutableStateFlow("")
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
        getMedia(selectedAlbumId, allowedMedia)
        getAlbums(allowedMedia)
    }

    fun getAlbum(albumId: Long) {
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
                }
        }
    }

    fun filterMedia(
        searchKeyword: String,
        isForceReset: Boolean
    ) {
        this.searchKeyword.value = if (isForceReset) "" else searchKeyword
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
            combine(sortedMediaState, _displaySettings, searchKeyword) { state, settings, keyword ->
                Triple(state, settings, keyword)
            }.collectLatest { (sortedState, settings, keyword) ->
                val (state, order) = sortedState
                if (order != settings.mediaOrder) return@collectLatest
                val (media, filtered) = withContext(ioDispatcher) {
                    val filtered = if (keyword.isBlank()) state.media else state.media.filter {
                        when {
                            keyword.startsWith("*") -> it.label.endsWith(keyword.drop(1), true)
                            keyword.endsWith("*") -> it.label.startsWith(keyword.dropLast(1), true)
                            else -> it.label.contains(keyword, true)
                        }
                    }
                    val mapped = state.withMappedMedia(settings)
                    mapped to if (keyword.isBlank()) mapped else {
                        state.copy(media = filtered).withMappedMedia(settings)
                    }
                }
                _mediaState.emit(media)
                _filteredMediaState.emit(filtered)
            }
        }
        runBlocking {
            _settingsState.value = settingsManager.getSettingsState()
        }
        settingsManager.settingsState.onEach {
            _settingsState.value = it
        }.launchIn(componentScope)
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext
        ): MediaPickerComponent
    }

}