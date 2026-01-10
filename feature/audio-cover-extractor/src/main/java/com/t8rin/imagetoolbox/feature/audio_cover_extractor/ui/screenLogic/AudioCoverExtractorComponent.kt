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

package com.t8rin.imagetoolbox.feature.audio_cover_extractor.ui.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.saving.model.onSuccess
import com.t8rin.imagetoolbox.core.domain.saving.updateProgress
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.feature.audio_cover_extractor.domain.AudioCoverRetriever
import com.t8rin.imagetoolbox.feature.audio_cover_extractor.domain.model.AudioCoverResult
import com.t8rin.imagetoolbox.feature.audio_cover_extractor.ui.components.AudioWithCover
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class AudioCoverExtractorComponent @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialUris: List<Uri>?,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val fileController: FileController,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap>,
    private val shareProvider: ImageShareProvider<Bitmap>,
    private val audioCoverRetriever: AudioCoverRetriever,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    init {
        debounce {
            initialUris?.let(::updateCovers)
        }
    }

    private val _covers: MutableState<List<AudioWithCover>> = mutableStateOf(emptyList())
    val covers by _covers

    private val _done: MutableState<Int> = mutableIntStateOf(0)
    val done by _done

    private val _left: MutableState<Int> = mutableIntStateOf(-1)
    val left by _left

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving: Boolean by _isSaving

    private val _imageFormat: MutableState<ImageFormat> = mutableStateOf(ImageFormat.Png.Lossless)
    val imageFormat by _imageFormat

    private val _quality: MutableState<Quality> = mutableStateOf(Quality.Base())
    val quality by _quality

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    private val mutex = Mutex()

    private fun Bitmap.imageInfo() = ImageInfo(
        width = width,
        height = height,
        quality = quality,
        imageFormat = imageFormat
    )

    fun updateCovers(uris: List<Uri>) {
        componentScope.launch {
            val audioUris = uris.distinct()

            mutex.withLock {
                _covers.value = audioUris.map {
                    AudioWithCover(
                        audioUri = it,
                        imageCoverUri = null,
                        isLoading = true
                    )
                }
            }

            audioUris.map { audioUri ->
                launch {
                    val result = audioCoverRetriever.loadCover(audioUri.toString())

                    val success = result as? AudioCoverResult.Success

                    ensureActive()

                    val newCover = AudioWithCover(
                        audioUri = audioUri,
                        imageCoverUri = success?.coverUri?.toUri(),
                        isLoading = false
                    )

                    mutex.withLock {
                        _covers.update { covers ->
                            covers.toMutableList().apply {
                                val index =
                                    indexOfFirst { it.audioUri == audioUri }.takeIf { it >= 0 }
                                        ?: return@update covers + newCover

                                set(index, newCover)
                            }
                        }
                    }
                }
            }
        }
    }

    fun addCovers(uris: List<Uri>) {
        componentScope.launch {
            val audioUris = uris.distinct()

            mutex.withLock {
                _covers.update { covers ->
                    val newList = covers + audioUris.map {
                        AudioWithCover(
                            audioUri = it,
                            imageCoverUri = null,
                            isLoading = true
                        )
                    }

                    newList.distinctBy { it.audioUri }
                }
            }

            covers.filter { it.imageCoverUri == null }.map { (audioUri) ->
                launch {
                    val result = audioCoverRetriever.loadCover(audioUri.toString())

                    val success = result as? AudioCoverResult.Success

                    ensureActive()

                    val newCover = AudioWithCover(
                        audioUri = audioUri,
                        imageCoverUri = success?.coverUri?.toUri(),
                        isLoading = false
                    )

                    mutex.withLock {
                        _covers.update { covers ->
                            covers.toMutableList().apply {
                                val index =
                                    indexOfFirst { it.audioUri == audioUri }.takeIf { it >= 0 }
                                        ?: return@update covers + newCover

                                set(index, newCover)
                            }
                        }
                    }
                }
            }
        }
    }

    fun performSharing(onComplete: () -> Unit) {
        cacheImages { uris ->
            componentScope.launch {
                shareProvider.shareUris(uris.map { it.toString() })
                onComplete()
            }
        }
    }

    fun cacheImages(
        onComplete: (List<Uri>) -> Unit
    ) {
        savingJob = trackProgress {
            _isSaving.update { true }

            val targetUris = covers.mapNotNull { it.imageCoverUri?.toString() }

            _done.update { 0 }
            _left.update { targetUris.size }

            val list = targetUris.mapNotNull { uri ->
                imageGetter.getImage(data = uri)?.let { image ->
                    shareProvider.cacheImage(
                        image = image,
                        imageInfo = image.imageInfo()
                    )?.toUri()
                }.also {
                    _done.value += 1
                    updateProgress(
                        done = done,
                        total = left
                    )
                }
            }

            _isSaving.update { false }

            onComplete(list)
        }
    }

    fun removeCover(coverUri: Uri) {
        _covers.update { list ->
            list.filter {
                it.imageCoverUri != coverUri && it.audioUri != coverUri
            }
        }
    }

    fun save(
        oneTimeSaveLocationUri: String?,
        onResult: (List<SaveResult>) -> Unit
    ) {
        savingJob = trackProgress {
            val results = mutableListOf<SaveResult>()
            val coverUris = covers.mapNotNull { it.imageCoverUri?.toString() }
            _done.value = 0
            _left.value = coverUris.size

            coverUris.forEach { coverUri ->
                runSuspendCatching {
                    imageGetter.getImage(data = coverUri)
                }.getOrNull()?.let { bitmap ->
                    results.add(
                        fileController.save(
                            saveTarget = ImageSaveTarget(
                                imageInfo = bitmap.imageInfo(),
                                metadata = null,
                                originalUri = coverUri,
                                sequenceNumber = _done.value + 1,
                                data = imageCompressor.compressAndTransform(
                                    image = bitmap,
                                    imageInfo = bitmap.imageInfo()
                                )
                            ),
                            keepOriginalMetadata = false,
                            oneTimeSaveLocationUri = oneTimeSaveLocationUri
                        )
                    )
                } ?: results.add(
                    SaveResult.Error.Exception(Throwable())
                )

                _done.value += 1
                updateProgress(
                    done = done,
                    total = left
                )
            }

            onResult(results.onSuccess(::registerSave))
            _isSaving.value = false
        }
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.value = false
    }

    fun setQuality(quality: Quality) {
        _quality.update { quality }
    }

    fun setImageFormat(imageFormat: ImageFormat) {
        _imageFormat.update { imageFormat }
    }


    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialUris: List<Uri>?,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit,
        ): AudioCoverExtractorComponent
    }
}