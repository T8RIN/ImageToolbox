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

package com.t8rin.imagetoolbox.image_cutting.presentation.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import coil3.transform.Transformation
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.data.utils.toCoil
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImagePreviewCreator
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.saving.model.onSuccess
import com.t8rin.imagetoolbox.core.domain.saving.updateProgress
import com.t8rin.imagetoolbox.core.domain.transformation.GenericTransformation
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.leftFrom
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.rightFrom
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.image_cutting.domain.CutParams
import com.t8rin.imagetoolbox.image_cutting.domain.ImageCutter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job

class ImageCutterComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialUris: List<Uri>?,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val fileController: FileController,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap>,
    private val shareProvider: ImageShareProvider<Bitmap>,
    private val imageCutter: ImageCutter<Bitmap>,
    private val imagePreviewCreator: ImagePreviewCreator<Bitmap>,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    init {
        debounce {
            initialUris?.let(::updateUris)
        }
    }

    private val _uris = mutableStateOf<List<Uri>?>(null)
    val uris by _uris

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving: Boolean by _isSaving

    private val _done: MutableState<Int> = mutableIntStateOf(0)
    val done by _done

    private val _selectedUri: MutableState<Uri?> = mutableStateOf(null)
    val selectedUri by _selectedUri

    private val _imageFormat: MutableState<ImageFormat> = mutableStateOf(ImageFormat.Default)
    val imageFormat: ImageFormat by _imageFormat

    private val _quality: MutableState<Quality> = mutableStateOf(Quality.Base(100))
    val quality: Quality by _quality

    private val _params: MutableState<CutParams> = mutableStateOf(CutParams.Default)
    val params by _params

    fun updateParams(cutParams: CutParams) {
        _params.update { cutParams }
        registerChanges()
    }

    fun updateUris(uris: List<Uri>?) {
        _uris.value = null
        _uris.value = uris
        _selectedUri.value = uris?.firstOrNull()
    }

    fun updateUrisSilently(removedUri: Uri) {
        componentScope.launch {
            _uris.value = uris
            if (_selectedUri.value == removedUri) {
                val index = uris?.indexOf(removedUri) ?: -1
                if (index == 0) {
                    uris?.getOrNull(1)?.let {
                        updateSelectedUri(it)
                    }
                } else {
                    uris?.getOrNull(index - 1)?.let {
                        updateSelectedUri(it)
                    }
                }
            }
            val u = _uris.value?.toMutableList()?.apply {
                remove(removedUri)
            }
            _uris.value = u

            registerChanges()
        }
    }

    fun setQuality(quality: Quality) {
        if (_quality.value != quality) {
            _quality.value = quality
            registerChanges()
        }
    }

    fun setImageFormat(imageFormat: ImageFormat) {
        if (_imageFormat.value != imageFormat) {
            _imageFormat.value = imageFormat
            registerChanges()
        }
    }

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    fun saveBitmaps(
        oneTimeSaveLocationUri: String?,
        onComplete: (List<SaveResult>) -> Unit
    ) {
        savingJob = trackProgress {
            _isSaving.value = true
            val results = mutableListOf<SaveResult>()
            _done.value = 0
            uris?.forEach { uri ->
                runSuspendCatching {
                    imageCutter.cutAndMerge(
                        imageUri = uri.toString(),
                        params = params
                    )
                }.getOrNull()?.let { bitmap ->
                    ImageInfo(
                        width = bitmap.width,
                        height = bitmap.height,
                        originalUri = uri.toString(),
                        quality = quality,
                        imageFormat = imageFormat
                    ).let { imageInfo ->
                        results.add(
                            fileController.save(
                                saveTarget = ImageSaveTarget(
                                    imageInfo = imageInfo,
                                    metadata = null,
                                    originalUri = uri.toString(),
                                    sequenceNumber = _done.value + 1,
                                    data = imageCompressor.compress(
                                        image = bitmap,
                                        imageFormat = imageFormat,
                                        quality = quality
                                    )
                                ),
                                keepOriginalMetadata = false,
                                oneTimeSaveLocationUri = oneTimeSaveLocationUri
                            )
                        )
                    }
                } ?: results.add(
                    SaveResult.Error.Exception(Throwable())
                )

                _done.value += 1
                updateProgress(
                    done = done,
                    total = uris.orEmpty().size
                )
            }
            onComplete(results.onSuccess(::registerSave))
            _isSaving.value = false
        }
    }

    fun updateSelectedUri(uri: Uri) {
        _selectedUri.value = uri
    }

    fun shareBitmaps(onComplete: () -> Unit) {
        savingJob = trackProgress {
            _isSaving.value = true
            shareProvider.shareImages(
                uris = uris?.map { it.toString() } ?: emptyList(),
                imageLoader = { uri ->
                    imageGetter.getImage(uri)?.image?.let { bmp ->
                        bmp to ImageInfo(
                            width = bmp.width,
                            height = bmp.height,
                            originalUri = uri,
                            quality = quality,
                            imageFormat = imageFormat
                        )
                    }
                },
                onProgressChange = {
                    if (it == -1) {
                        onComplete()
                        _isSaving.value = false
                        _done.value = 0
                    } else {
                        _done.value = it
                    }
                    updateProgress(
                        done = done,
                        total = uris.orEmpty().size
                    )
                }
            )
        }
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.value = false
    }

    fun cacheCurrentImage(onComplete: (Uri) -> Unit) {
        savingJob = trackProgress {
            _isSaving.value = true
            imageCutter.cutAndMerge(
                imageUri = selectedUri.toString(),
                params = params
            )?.let { bmp ->
                bmp to ImageInfo(
                    width = bmp.width,
                    height = bmp.height,
                    originalUri = selectedUri.toString(),
                    quality = quality,
                    imageFormat = imageFormat
                )
            }?.let { (image, imageInfo) ->
                shareProvider.cacheImage(
                    image = image,
                    imageInfo = imageInfo.copy(originalUri = selectedUri.toString())
                )?.let { uri ->
                    onComplete(uri.toUri())
                }
            }
            _isSaving.value = false
        }
    }

    fun cacheImages(
        onComplete: (List<Uri>) -> Unit
    ) {
        savingJob = trackProgress {
            _isSaving.value = true
            _done.value = 0
            val list = mutableListOf<Uri>()
            uris?.forEach { uri ->
                imageCutter.cutAndMerge(
                    imageUri = uri.toString(),
                    params = params
                )?.let { bmp ->
                    bmp to ImageInfo(
                        width = bmp.width,
                        height = bmp.height,
                        originalUri = uri.toString(),
                        quality = quality,
                        imageFormat = imageFormat
                    )
                }?.let { (image, imageInfo) ->
                    shareProvider.cacheImage(
                        image = image,
                        imageInfo = imageInfo.copy(originalUri = uri.toString())
                    )?.let { uri ->
                        list.add(uri.toUri())
                    }
                }
                _done.value += 1
                updateProgress(
                    done = done,
                    total = uris.orEmpty().size
                )
            }
            onComplete(list)
            _isSaving.value = false
        }
    }

    fun selectLeftUri() {
        uris
            ?.indexOf(selectedUri ?: Uri.EMPTY)
            ?.takeIf { it >= 0 }
            ?.let {
                uris?.leftFrom(it)
            }
            ?.let(::updateSelectedUri)
    }

    fun selectRightUri() {
        uris
            ?.indexOf(selectedUri ?: Uri.EMPTY)
            ?.takeIf { it >= 0 }
            ?.let {
                uris?.rightFrom(it)
            }
            ?.let(::updateSelectedUri)
    }

    fun getFormatForFilenameSelection(): ImageFormat? =
        if (uris?.size == 1) imageFormat
        else null

    fun getCutTransformation(): List<Transformation> = listOf(
        GenericTransformation { image: Bitmap ->
            val bitmap = imageCutter.cutAndMerge(
                image = image,
                params = params
            ) ?: image

            imagePreviewCreator.createPreview(
                image = bitmap,
                imageInfo = ImageInfo(
                    width = bitmap.width,
                    height = bitmap.height,
                    quality = quality,
                    imageFormat = imageFormat
                ),
                transformations = emptyList(),
                onGetByteCount = {}
            ) ?: image
        }.toCoil()
    )

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialUris: List<Uri>?,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit,
        ): ImageCutterComponent
    }
}