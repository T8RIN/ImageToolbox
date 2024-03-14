/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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
package ru.tech.imageresizershrinker.media_picker.presentation.viewModel

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import com.t8rin.dynamic.theme.ColorTuple
import com.t8rin.dynamic.theme.extractPrimaryColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.di.DefaultDispatcher
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.settings.domain.SettingsRepository
import ru.tech.imageresizershrinker.core.settings.domain.model.SettingsState
import ru.tech.imageresizershrinker.media_picker.data.utils.DateExt
import ru.tech.imageresizershrinker.media_picker.data.utils.getDate
import ru.tech.imageresizershrinker.media_picker.data.utils.getDateExt
import ru.tech.imageresizershrinker.media_picker.data.utils.getDateHeader
import ru.tech.imageresizershrinker.media_picker.data.utils.getMonth
import ru.tech.imageresizershrinker.media_picker.domain.MediaRepository
import ru.tech.imageresizershrinker.media_picker.domain.model.Album
import ru.tech.imageresizershrinker.media_picker.domain.model.AlbumState
import ru.tech.imageresizershrinker.media_picker.domain.model.AllowedMedia
import ru.tech.imageresizershrinker.media_picker.domain.model.Media
import ru.tech.imageresizershrinker.media_picker.domain.model.MediaItem
import ru.tech.imageresizershrinker.media_picker.domain.model.MediaState
import javax.inject.Inject

@HiltViewModel
class MediaPickerViewModel @Inject constructor(
    val imageLoader: ImageLoader,
    private val imageGetter: ImageGetter<Bitmap, ExifInterface>,
    private val settingsRepository: SettingsRepository,
    private val mediaRepository: MediaRepository,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _settingsState = mutableStateOf(SettingsState.Default)
    val settingsState: SettingsState by _settingsState

    val selectedMedia = mutableStateListOf<Media>()

    private val _mediaState = MutableStateFlow(MediaState())
    val mediaState = _mediaState.asStateFlow()

    private val _albumsState = MutableStateFlow(AlbumState())
    val albumsState = _albumsState.asStateFlow()

    private var initialized = false
    fun init(allowedMedia: AllowedMedia) {
        if (!initialized) {
            this.allowedMedia = allowedMedia
            getMedia(albumId, allowedMedia)
            getAlbums(allowedMedia)
        }
        initialized = true
    }

    fun getAlbum(albumId: Long) {
        this.albumId = albumId
        getMedia(albumId, allowedMedia)
    }

    private var allowedMedia: AllowedMedia = AllowedMedia.Photos(null)

    var albumId: Long = -1L

    private val emptyAlbum = Album(
        id = -1,
        label = "All",
        uri = "",
        pathToThumbnail = "",
        timestamp = 0,
        relativePath = ""
    )

    private fun getAlbums(allowedMedia: AllowedMedia) {
        viewModelScope.launch(dispatcher) {
            mediaRepository.getAlbumsWithType(allowedMedia)
                .flowOn(dispatcher)
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

    private var mediaGettingJob: Job? = null

    private fun getMedia(
        albumId: Long,
        allowedMedia: AllowedMedia
    ) {
        mediaGettingJob?.cancel()
        mediaGettingJob = viewModelScope.launch(dispatcher) {
            delay(200L)
            _mediaState.emit(MediaState())
            mediaRepository.mediaFlowWithType(albumId, allowedMedia)
                .flowOn(dispatcher)
                .collectLatest { result ->
                    val data = result.getOrNull()?.filter {
                        if (allowedMedia is AllowedMedia.Photos) {
                            val ext = allowedMedia.ext
                            if (ext != null && ext != "*") {
                                it.uri.endsWith(ext)
                            } else true
                        } else true
                    } ?: emptyList()
                    val error = if (result.isFailure) result.exceptionOrNull()?.message
                        ?: "An error occurred" else ""
                    if (data.isEmpty()) {
                        return@collectLatest _mediaState.emit(MediaState(isLoading = false))
                    }
                    _mediaState.collectMedia(data, error, albumId)
                }
        }
    }

    private suspend fun MutableStateFlow<MediaState>.collectMedia(
        data: List<Media>,
        error: String,
        albumId: Long,
        groupByMonth: Boolean = false,
        withMonthHeader: Boolean = true
    ) {
        val timeStart = System.currentTimeMillis()
        val mappedData = mutableListOf<MediaItem>()
        val mappedDataWithMonthly = mutableListOf<MediaItem>()
        val monthHeaderList: MutableSet<String> = mutableSetOf()
        withContext(dispatcher) {
            val groupedData = data.groupBy {
                if (groupByMonth) {
                    it.timestamp.getMonth()
                } else {
                    /** Localized in composition */
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
                    mappedDataWithMonthly.add(dateHeader)
                    mappedDataWithMonthly.addAll(groupedMedia)
                } else {
                    val month = getMonth(date)
                    if (month.isNotEmpty() && !monthHeaderList.contains(month)) {
                        monthHeaderList.add(month)
                        if (withMonthHeader && mappedDataWithMonthly.isNotEmpty()) {
                            mappedDataWithMonthly.add(
                                MediaItem.Header(
                                    "header_big_${month}_${data.size}",
                                    month,
                                    emptyList()
                                )
                            )
                        }
                    }
                    mappedData.add(dateHeader)
                    if (withMonthHeader) {
                        mappedDataWithMonthly.add(dateHeader)
                    }
                    mappedData.addAll(groupedMedia)
                    if (withMonthHeader) {
                        mappedDataWithMonthly.addAll(groupedMedia)
                    }
                }
            }
        }
        withContext(Dispatchers.Main) {
            println("-->Media mapping took: ${System.currentTimeMillis() - timeStart}ms")
            tryEmit(
                MediaState(
                    isLoading = false,
                    error = error,
                    media = data,
                    mappedMedia = mappedData,
                    mappedMediaWithMonthly = if (withMonthHeader) mappedDataWithMonthly else emptyList(),
                    dateHeader = data.dateHeader(albumId)
                )
            )
        }
    }

    private fun List<Media>.dateHeader(albumId: Long): String =
        if (albumId != -1L) {
            val startDate: DateExt = last().timestamp.getDateExt()
            val endDate: DateExt = first().timestamp.getDateExt()
            getDateHeader(startDate, endDate)
        } else ""

    suspend fun getColorTupleFromEmoji(
        emojiUri: String
    ): ColorTuple? = imageGetter
        .getImage(data = emojiUri)
        ?.extractPrimaryColor()
        ?.let { ColorTuple(it) }

    init {
        runBlocking {
            _settingsState.value = settingsRepository.getSettingsState()
        }
        settingsRepository.getSettingsStateFlow().onEach {
            _settingsState.value = it
        }.launchIn(viewModelScope)
    }

}