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

package com.t8rin.imagetoolbox.feature.load_net_image.presentation.screenLogic


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
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFrames
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.saving.model.onSuccess
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.feature.load_net_image.domain.HtmlImageParser
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job

class LoadNetImageComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted initialUrl: String,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val fileController: FileController,
    private val imageGetter: ImageGetter<Bitmap>,
    private val shareProvider: ImageShareProvider<Bitmap>,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val htmlImageParser: HtmlImageParser,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    init {
        debounce {
            updateTargetUrl(initialUrl)
        }
    }

    private val _targetUrl: MutableState<String> = mutableStateOf("")
    val targetUrl: String by _targetUrl

    private val _bitmap = mutableStateOf<Bitmap?>(null)
    val bitmap by _bitmap

    private val _parsedImages: MutableState<List<String>> = mutableStateOf(emptyList())
    val parsedImages: List<String> by _parsedImages

    private val _imageFrames: MutableState<ImageFrames> = mutableStateOf(ImageFrames.All)
    val imageFrames by _imageFrames

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving by _isSaving

    private val _done: MutableState<Int> = mutableIntStateOf(0)
    val done by _done

    private val _left: MutableState<Int> = mutableIntStateOf(-1)
    val left by _left

    fun updateTargetUrl(
        newUrl: String,
        onFailure: (String) -> Unit = {}
    ) {
        _targetUrl.update(
            onValueChanged = {
                debouncedImageCalculation {
                    val newImages = htmlImageParser.parseImagesSrc(
                        url = newUrl,
                        onFailure = onFailure
                    )

                    newImages.firstOrNull().let { src ->
                        _bitmap.update { src?.let { imageGetter.getImage(data = src) } }
                    }
                    _parsedImages.update { newImages }
                }
            },
            transform = { newUrl }
        )
    }

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    fun saveBitmaps(
        oneTimeSaveLocationUri: String?,
        onResult: (List<SaveResult>) -> Unit
    ) {
        savingJob = trackProgress {
            _isSaving.update { true }

            val results = mutableListOf<SaveResult>()
            val positions = imageFrames.getFramePositions(parsedImages.size)

            _done.value = 0
            _left.value = positions.size

            parsedImages.forEachIndexed { index, url ->
                if ((index + 1) in positions) {
                    imageGetter.getImage(data = url)?.let { bitmap ->
                        fileController.save(
                            saveTarget = ImageSaveTarget(
                                imageInfo = ImageInfo(
                                    width = bitmap.width,
                                    height = bitmap.height,
                                    imageFormat = ImageFormat.Png.Lossless
                                ),
                                originalUri = "_",
                                sequenceNumber = null,
                                data = imageCompressor.compress(
                                    image = bitmap,
                                    imageFormat = ImageFormat.Png.Lossless,
                                    quality = Quality.Base(100)
                                )
                            ),
                            keepOriginalMetadata = false,
                            oneTimeSaveLocationUri = oneTimeSaveLocationUri
                        )
                    }?.let(results::add) ?: results.add(
                        SaveResult.Error.Exception(Throwable())
                    )
                    _done.value++
                }
            }
            onResult(results.onSuccess(::registerSave))
            _isSaving.update { false }
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
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = trackProgress {
            _isSaving.value = true

            val positions =
                imageFrames.getFramePositions(parsedImages.size).map { it - 1 }

            _done.value = 0
            _left.value = positions.size

            val uris = parsedImages.filterIndexed { index, _ ->
                index in positions
            }
            onComplete(
                uris.mapNotNull {
                    val image = imageGetter.getImage(data = it) ?: return@mapNotNull null

                    shareProvider.cacheImage(
                        image = image,
                        imageInfo = ImageInfo(
                            width = image.width,
                            height = image.height,
                            imageFormat = ImageFormat.Png.Lossless
                        )
                    )?.toUri()
                }
            )
            _isSaving.value = false
        }
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.value = false
    }

    fun cacheCurrentImage(onComplete: (Uri) -> Unit) {
        _done.value = 0
        _left.value = 1
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = trackProgress {
            _isSaving.value = true
            imageFrames.getFramePositions(parsedImages.size).firstOrNull()?.let {
                imageGetter.getImage(data = parsedImages[it - 1])
            }?.let { image ->
                shareProvider.cacheImage(
                    image = image,
                    imageInfo = ImageInfo(
                        width = image.width,
                        height = image.height,
                        imageFormat = ImageFormat.Png.Lossless
                    )
                )?.let { uri ->
                    onComplete(uri.toUri())
                }
            }
            _isSaving.value = false
        }
    }

    fun getFormatForFilenameSelection(): ImageFormat = ImageFormat.Png.Lossless

    fun updateImageFrames(imageFrames: ImageFrames) {
        _imageFrames.update { imageFrames }
        registerChanges()
    }

    fun clearImagesSelection() = updateImageFrames(ImageFrames.ManualSelection(emptyList()))

    fun selectAllImages() = updateImageFrames(ImageFrames.All)

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialUrl: String,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit,
        ): LoadNetImageComponent
    }
}