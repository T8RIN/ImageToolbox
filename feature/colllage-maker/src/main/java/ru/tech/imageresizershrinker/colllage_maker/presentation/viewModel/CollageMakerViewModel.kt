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

package ru.tech.imageresizershrinker.colllage_maker.presentation.viewModel

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.viewModelScope
import com.t8rin.collages.CollageType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import ru.tech.imageresizershrinker.core.domain.dispatchers.DispatchersHolder
import ru.tech.imageresizershrinker.core.domain.image.ImageCompressor
import ru.tech.imageresizershrinker.core.domain.image.ShareProvider
import ru.tech.imageresizershrinker.core.domain.image.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.image.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.image.model.Quality
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.core.domain.saving.model.ImageSaveTarget
import ru.tech.imageresizershrinker.core.domain.saving.model.SaveResult
import ru.tech.imageresizershrinker.core.domain.utils.smartJob
import ru.tech.imageresizershrinker.core.ui.utils.BaseViewModel
import ru.tech.imageresizershrinker.core.ui.utils.state.update
import javax.inject.Inject

@HiltViewModel
class CollageMakerViewModel @Inject constructor(
    private val fileController: FileController,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val shareProvider: ShareProvider<Bitmap>,
    dispatchersHolder: DispatchersHolder
) : BaseViewModel(dispatchersHolder) {

    private val _spacing = mutableFloatStateOf(0f)
    val spacing: Float by _spacing

    private val _cornerRadius = mutableFloatStateOf(0f)
    val cornerRadius: Float by _cornerRadius

    private val _backgroundColor = mutableStateOf(Color.Black)
    val backgroundColor: Color by _backgroundColor

    private val _collageCreationTrigger = mutableStateOf(false)
    val collageCreationTrigger by _collageCreationTrigger

    private val _collageType: MutableState<CollageType> = mutableStateOf(CollageType.Empty)
    val collageType by _collageType

    private val _collageBitmap = mutableStateOf<Bitmap?>(null)
    private val collageBitmap by _collageBitmap

    private val _uris = mutableStateOf<List<Uri>?>(null)
    val uris by _uris

    private val _imageFormat: MutableState<ImageFormat> = mutableStateOf(ImageFormat.Png.Lossless)
    val imageFormat: ImageFormat by _imageFormat

    private val _quality: MutableState<Quality> = mutableStateOf(Quality.Base())
    val quality: Quality by _quality

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving: Boolean by _isSaving

    private var requestedOperation: () -> Unit = {}

    fun setCollageType(collageType: CollageType) {
        _collageType.update { collageType }
        registerChanges()
    }

    fun updateCollageBitmap(bitmap: Bitmap) {
        _collageCreationTrigger.update { false }
        _collageBitmap.update { bitmap }
        requestedOperation()
    }

    fun updateUris(uris: List<Uri>?) {
        viewModelScope.launch {
            _isImageLoading.update { true }
            _uris.update { uris }
            _isImageLoading.update { false }
        }
    }

    fun setQuality(quality: Quality) {
        _quality.update { quality }
        registerChanges()
    }

    fun setImageFormat(imageFormat: ImageFormat) {
        _imageFormat.update { imageFormat }
        registerChanges()
    }

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    fun saveBitmap(
        oneTimeSaveLocationUri: String?,
        onComplete: (SaveResult) -> Unit
    ) {
        _isSaving.update { true }
        _collageCreationTrigger.update { true }
        requestedOperation = {
            savingJob = viewModelScope.launch(defaultDispatcher) {
                collageBitmap?.let { image ->
                    _isSaving.update { true }
                    val imageInfo = ImageInfo(
                        width = image.width,
                        height = image.height,
                        quality = quality,
                        imageFormat = imageFormat
                    )
                    val result = fileController.save(
                        saveTarget = ImageSaveTarget<ExifInterface>(
                            imageInfo = imageInfo,
                            originalUri = "",
                            sequenceNumber = null,
                            data = imageCompressor.compress(
                                image = image,
                                imageFormat = imageFormat,
                                quality = quality
                            )
                        ),
                        keepOriginalMetadata = false,
                        oneTimeSaveLocationUri = oneTimeSaveLocationUri
                    )

                    onComplete(result.onSuccess(::registerSave))
                    _isSaving.update { false }
                }
            }
        }
    }

    fun performSharing(
        onComplete: () -> Unit
    ) {
        _isSaving.update { true }
        _collageCreationTrigger.update { true }
        requestedOperation = {
            collageBitmap?.let { image ->
                savingJob = viewModelScope.launch {
                    _isSaving.update { true }
                    shareProvider.cacheImage(
                        image = image,
                        imageInfo = ImageInfo(
                            width = image.width,
                            height = image.height,
                            quality = quality,
                            imageFormat = imageFormat
                        )
                    )?.let { uri ->
                        shareProvider.shareUri(
                            uri = uri,
                            onComplete = onComplete
                        )
                    }
                    _isSaving.update { false }
                }
            }
        }
    }

    fun cacheImage(
        onComplete: (Uri) -> Unit
    ) {
        _isSaving.update { true }
        _collageCreationTrigger.update { true }
        requestedOperation = {
            collageBitmap?.let { image ->
                savingJob = viewModelScope.launch {
                    _isSaving.update { true }
                    shareProvider.cacheImage(
                        image = image,
                        imageInfo = ImageInfo(
                            width = image.width,
                            height = image.height,
                            quality = quality,
                            imageFormat = imageFormat
                        )
                    )?.let { uri ->
                        onComplete(uri.toUri())
                    }
                    _isSaving.update { false }
                }
            }
        }
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.update { false }
    }

    fun setBackgroundColor(color: Color) {
        _backgroundColor.update { color }
        registerChanges()
    }

    fun setSpacing(value: Float) {
        _spacing.update { value }
        registerChanges()
    }

    fun setCornerRadius(value: Float) {
        _cornerRadius.update { value }
        registerChanges()
    }

    fun getFormatForFilenameSelection(): ImageFormat = imageFormat

}