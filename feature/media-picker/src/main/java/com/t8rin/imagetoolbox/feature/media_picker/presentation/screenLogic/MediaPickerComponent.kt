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
import com.t8rin.imagetoolbox.feature.media_picker.data.utils.DateExt
import com.t8rin.imagetoolbox.feature.media_picker.data.utils.getDate
import com.t8rin.imagetoolbox.feature.media_picker.data.utils.getDateExt
import com.t8rin.imagetoolbox.feature.media_picker.data.utils.getDateHeader
import com.t8rin.imagetoolbox.feature.media_picker.data.utils.getMonth
import com.t8rin.imagetoolbox.feature.media_picker.domain.MediaRetriever
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Album
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.AlbumState
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.AllowedMedia
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Media
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.MediaItem
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.MediaState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class MediaPickerComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    private val settingsManager: SettingsManager,
    private val mediaRetriever: MediaRetriever,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    private val _settingsState = mutableStateOf(SettingsState.Default)
    val settingsState: SettingsState by _settingsState

    val selectedMedia = mutableStateListOf<Media>()

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
            _mediaState.emit(mediaState.value.copy(isLoading = true))
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
                    if (data.isEmpty()) {
                        return@collectLatest _mediaState.emit(MediaState(isLoading = false))
                    }
                    _mediaState.collectMedia(data, error, albumId)
                    _filteredMediaState.emit(mediaState.value)
                }
        }
    }

    private suspend fun MutableStateFlow<MediaState>.collectMedia(
        data: List<Media>,
        error: String,
        albumId: Long,
        groupByMonth: Boolean = false
    ) {
        val mappedData = mutableListOf<MediaItem>()
        val monthHeaderList: MutableSet<String> = mutableSetOf()
        withContext(defaultDispatcher) {
            val groupedData = data.groupBy {
                if (groupByMonth) {
                    it.timestamp.getMonth()
                } else {
                    it.timestamp.getDate(
                        stringToday = "Today",
                        stringYesterday = "Yesterday"
                    )
                }
            }
            groupedData.forEach { (date, data) ->
                val dateHeader = MediaItem.Header("header_$date", date, data)
                val groupedMedia = data.map {
                    MediaItem.MediaViewItem("media_${it.id}_${it.label}", it)
                }
                if (groupByMonth) {
                    mappedData.add(dateHeader)
                    mappedData.addAll(groupedMedia)
                } else {
                    val month = getMonth(date)
                    if (month.isNotEmpty() && !monthHeaderList.contains(month)) {
                        monthHeaderList.add(month)
                    }
                    mappedData.add(dateHeader)
                    mappedData.addAll(groupedMedia)
                }
            }
        }
        withContext(uiDispatcher) {
            tryEmit(
                MediaState(
                    isLoading = false,
                    error = error,
                    media = data,
                    mappedMedia = mappedData,
                    dateHeader = data.dateHeader(albumId)
                )
            )
        }
    }

    private fun List<Media>.dateHeader(albumId: Long): String =
        if (albumId != -1L && isNotEmpty()) {
            val startDate: DateExt = last().timestamp.getDateExt()
            val endDate: DateExt = first().timestamp.getDateExt()
            getDateHeader(startDate, endDate)
        } else ""

    private var mediaFilterJob: Job? by smartJob()

    fun filterMedia(
        searchKeyword: String,
        isForceReset: Boolean
    ) {
        mediaFilterJob = componentScope.launch {
            if (isForceReset) {
                _filteredMediaState.emit(mediaState.value)
            } else {
                _filteredMediaState.emit(mediaState.value.copy(isLoading = true))
                _filteredMediaState.collectMedia(
                    data = mediaState.value.media.filter {
                        if (searchKeyword.startsWith("*")) {
                            it.label.endsWith(
                                suffix = searchKeyword.drop(1),
                                ignoreCase = true
                            )
                        } else if (searchKeyword.endsWith("*")) {
                            it.label.startsWith(
                                prefix = searchKeyword.dropLast(1),
                                ignoreCase = true
                            )
                        } else {
                            it.label.contains(
                                other = searchKeyword,
                                ignoreCase = true
                            )
                        }
                    }.distinctBy { it.id },
                    error = mediaState.value.error,
                    albumId = selectedAlbumId
                )
            }
        }
    }

    init {
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