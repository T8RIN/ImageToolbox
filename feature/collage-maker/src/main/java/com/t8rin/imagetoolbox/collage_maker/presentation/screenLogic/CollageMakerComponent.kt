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

package com.t8rin.imagetoolbox.collage_maker.presentation.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.t8rin.collages.CollageType
import com.t8rin.imagetoolbox.collage_maker.presentation.components.CollageParams
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.model.DomainAspectRatio
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.domain.utils.update
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.savable
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job

class CollageMakerComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialUris: List<Uri>?,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val fileController: FileController,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val shareProvider: ImageShareProvider<Bitmap>,
    dispatchersHolder: DispatchersHolder,
) : BaseComponent(dispatchersHolder, componentContext) {

    init {
        debounce {
            initialUris?.let(::updateUris)
        }
    }

    private val _aspectRatio: MutableState<DomainAspectRatio> =
        mutableStateOf(DomainAspectRatio.Numeric(1f, 1f))
    val aspectRatio by _aspectRatio

    private val _backgroundColor = mutableStateOf(Color.White)
    val backgroundColor: Color by _backgroundColor

    private val _collageCreationTrigger = mutableStateOf(false)
    val collageCreationTrigger by _collageCreationTrigger

    private val _collageType: MutableState<CollageType> = mutableStateOf(CollageType.Empty)
    val collageType by _collageType

    private val _collageBitmap = mutableStateOf<Bitmap?>(null)
    private val collageBitmap by _collageBitmap

    private val _params = fileController.savable(
        scope = componentScope,
        initial = CollageParams()
    )
    val params by _params

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
        componentScope.launch {
            _isImageLoading.update { true }
            _uris.update { uris }
            _isImageLoading.update { false }
        }
    }

    fun replaceImageAt(index: Int, uri: Uri) {
        _uris.update { current ->
            val list = current?.toMutableList() ?: return@update current
            if (index in list.indices) {
                list[index] = uri
                list
            } else current
        }
        registerChanges()
    }

    fun addImage(uri: Uri) {
        _uris.update { current ->
            val list = current ?: emptyList()
            if (list.size >= 10) list else list + uri
        }
        registerChanges()
    }

    fun removeImageAt(index: Int) {
        _uris.update { current ->
            val list = current?.toMutableList() ?: return@update current
            if (index in list.indices) {
                list.removeAt(index)
                list
            } else current
        }
        registerChanges()
    }

    fun setQuality(quality: Quality) {
        _quality.update { quality }
        registerChanges()
    }

    fun setImageFormat(imageFormat: ImageFormat) {
        _imageFormat.update { imageFormat }
        registerChanges()
    }

    fun setOutputScaleRatio(ratio: Float) {
        _params.update { it.copy(outputScaleRatio = ratio) }
        registerChanges()
    }

    fun setDisableRotation(value: Boolean) {
        _params.update { it.copy(disableRotation = value) }
        registerChanges()
    }

    fun setEnableSnapToBorders(value: Boolean) {
        _params.update { it.copy(enableSnapToBorders = value) }
        registerChanges()
    }

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    fun saveBitmap(
        oneTimeSaveLocationUri: String?,
        onComplete: (SaveResult) -> Unit,
    ) {
        _isSaving.update { true }
        _collageCreationTrigger.update { true }
        requestedOperation = {
            savingJob = trackProgress {
                collageBitmap?.let { image ->
                    _isSaving.update { true }
                    val imageInfo = ImageInfo(
                        width = image.width,
                        height = image.height,
                        quality = quality,
                        imageFormat = imageFormat
                    )
                    val result = fileController.save(
                        saveTarget = ImageSaveTarget(
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
        onComplete: () -> Unit,
    ) {
        _isSaving.update { true }
        _collageCreationTrigger.update { true }
        requestedOperation = {
            collageBitmap?.let { image ->
                savingJob = trackProgress {
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
        onComplete: (Uri) -> Unit,
    ) {
        _isSaving.update { true }
        _collageCreationTrigger.update { true }
        requestedOperation = {
            collageBitmap?.let { image ->
                savingJob = trackProgress {
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
        _params.update { it.copy(spacing = value) }
        registerChanges()
    }

    fun setCornerRadius(value: Float) {
        _params.update { it.copy(cornerRadius = value) }
        registerChanges()
    }

    fun getFormatForFilenameSelection(): ImageFormat = imageFormat

    fun setAspectRatio(aspect: DomainAspectRatio) {
        _aspectRatio.update { aspect }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialUris: List<Uri>?,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit,
        ): CollageMakerComponent
    }

}