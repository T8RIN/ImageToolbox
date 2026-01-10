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

package com.t8rin.imagetoolbox.noise_generation.presentation.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.image.model.ResizeType
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.noise_generation.domain.NoiseGenerator
import com.t8rin.imagetoolbox.noise_generation.domain.model.NoiseParams
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job

class NoiseGenerationComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    dispatchersHolder: DispatchersHolder,
    private val noiseGenerator: NoiseGenerator<Bitmap>,
    private val fileController: FileController,
    private val shareProvider: ImageShareProvider<Bitmap>,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val imageScaler: ImageScaler<Bitmap>
) : BaseComponent(dispatchersHolder, componentContext) {

    init {
        updatePreview()
    }

    private val _previewBitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val previewBitmap: Bitmap? by _previewBitmap

    private val _noiseParams: MutableState<NoiseParams> = mutableStateOf(NoiseParams.Default)
    val noiseParams: NoiseParams by _noiseParams

    private val _noiseSize: MutableState<IntegerSize> = mutableStateOf(IntegerSize(1000, 1000))
    val noiseSize: IntegerSize by _noiseSize

    private val _imageFormat: MutableState<ImageFormat> = mutableStateOf(ImageFormat.Default)
    val imageFormat: ImageFormat by _imageFormat

    private val _quality: MutableState<Quality> = mutableStateOf(Quality.Base(100))
    val quality: Quality by _quality

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving by _isSaving

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    fun saveNoise(
        oneTimeSaveLocationUri: String?,
        onComplete: (result: SaveResult) -> Unit,
    ) {
        savingJob = trackProgress {
            _isSaving.update { true }
            noiseGenerator.generateNoise(
                width = noiseSize.width,
                height = noiseSize.height,
                noiseParams = noiseParams,
                onFailure = {
                    onComplete(SaveResult.Error.Exception(it))
                }
            )?.let { bitmap ->
                val imageInfo = ImageInfo(
                    width = bitmap.width,
                    height = bitmap.height,
                    quality = quality,
                    imageFormat = imageFormat
                )
                onComplete(
                    fileController.save(
                        saveTarget = ImageSaveTarget(
                            imageInfo = imageInfo,
                            metadata = null,
                            originalUri = "",
                            sequenceNumber = null,
                            data = imageCompressor.compress(
                                image = bitmap,
                                imageFormat = imageFormat,
                                quality = quality
                            )
                        ),
                        keepOriginalMetadata = true,
                        oneTimeSaveLocationUri = oneTimeSaveLocationUri
                    ).onSuccess(::registerSave)
                )
            }
            _isSaving.update { false }
        }
    }

    fun cacheCurrentNoise(onComplete: (Uri) -> Unit) {
        savingJob = trackProgress {
            _isSaving.update { true }
            noiseGenerator.generateNoise(
                width = noiseSize.width,
                height = noiseSize.height,
                noiseParams = noiseParams
            )?.let { image ->
                val imageInfo = ImageInfo(
                    width = image.width,
                    height = image.height,
                    quality = quality,
                    imageFormat = imageFormat
                )
                shareProvider.cacheImage(
                    image = image,
                    imageInfo = imageInfo
                )?.let { uri ->
                    onComplete(uri.toUri())
                }
            }
            _isSaving.update { false }
        }
    }

    fun shareNoise(onComplete: () -> Unit) {
        cacheCurrentNoise { uri ->
            componentScope.launch {
                shareProvider.shareUri(
                    uri = uri.toString(),
                    onComplete = onComplete
                )
            }
        }
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.update { false }
    }

    fun setImageFormat(imageFormat: ImageFormat) {
        _imageFormat.update { imageFormat }
    }

    fun setQuality(quality: Quality) {
        _quality.update { quality }
    }

    fun updateParams(params: NoiseParams) {
        _noiseParams.update(
            onValueChanged = ::updatePreview,
            transform = { params }
        )
    }

    fun setNoiseWidth(width: Int) {
        _noiseSize.update(
            onValueChanged = ::updatePreview,
            transform = { it.copy(width = width.coerceAtMost(8192)) }
        )
    }

    fun setNoiseHeight(height: Int) {
        _noiseSize.update(
            onValueChanged = ::updatePreview,
            transform = { it.copy(height = height.coerceAtMost(8192)) }
        )
    }

    private fun updatePreview() {
        componentScope.launch {
            _isImageLoading.update { true }
            _previewBitmap.update { null }
            debouncedImageCalculation {
                noiseGenerator.generateNoise(
                    width = noiseSize.width,
                    height = noiseSize.height,
                    noiseParams = noiseParams
                )?.let {
                    imageScaler.scaleImage(
                        image = it,
                        width = 512,
                        height = 512,
                        resizeType = ResizeType.Flexible
                    )
                }.also { bitmap ->
                    _previewBitmap.update { bitmap }
                    _isImageLoading.update { false }
                }
            }
        }
    }

    fun getFormatForFilenameSelection(): ImageFormat = imageFormat


    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit,
        ): NoiseGenerationComponent
    }

}