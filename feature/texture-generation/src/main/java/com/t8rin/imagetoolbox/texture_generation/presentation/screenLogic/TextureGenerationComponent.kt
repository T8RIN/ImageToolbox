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

package com.t8rin.imagetoolbox.texture_generation.presentation.screenLogic

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
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.model.flexibleResize
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.ui.utils.BaseHistoryComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.texture_generation.domain.TextureGenerator
import com.t8rin.imagetoolbox.texture_generation.domain.model.TextureParams
import com.t8rin.imagetoolbox.texture_generation.presentation.screenLogic.TextureGenerationComponent.HistorySnapshot
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job

class TextureGenerationComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    dispatchersHolder: DispatchersHolder,
    private val textureGenerator: TextureGenerator<Bitmap>,
    private val fileController: FileController,
    private val shareProvider: ImageShareProvider<Bitmap>,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val imageScaler: ImageScaler<Bitmap>,
    private val settingsManager: SettingsManager,
) : BaseHistoryComponent<HistorySnapshot>(
    dispatchersHolder = dispatchersHolder,
    componentContext = componentContext
) {

    private val _previewBitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val previewBitmap: Bitmap? by _previewBitmap

    private val _textureParams: MutableState<TextureParams> = mutableStateOf(TextureParams.Default)
    val textureParams: TextureParams by _textureParams

    private val _textureSize: MutableState<IntegerSize> = mutableStateOf(IntegerSize(1000, 1000))
    val textureSize: IntegerSize by _textureSize

    private val _imageFormat: MutableState<ImageFormat> = mutableStateOf(ImageFormat.Default)
    val imageFormat: ImageFormat by _imageFormat

    private val _quality: MutableState<Quality> = mutableStateOf(Quality.Base(100))
    val quality: Quality by _quality

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving by _isSaving

    init {
        resetHistory()
        updatePreview()
    }

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    fun saveTexture(
        oneTimeSaveLocationUri: String?
    ) {
        savingJob = trackProgress {
            _isSaving.update { true }
            textureGenerator.generateTexture(
                width = textureSize.width,
                height = textureSize.height,
                textureParams = textureParams,
                onFailure = {
                    parseSaveResult(SaveResult.Error.Exception(it))
                }
            )?.let { bitmap ->
                val imageInfo = ImageInfo(
                    width = bitmap.width,
                    height = bitmap.height,
                    quality = quality,
                    imageFormat = imageFormat
                )
                parseSaveResult(
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

    fun cacheCurrentTexture(onComplete: (Uri) -> Unit) {
        savingJob = trackProgress {
            _isSaving.update { true }
            textureGenerator.generateTexture(
                width = textureSize.width,
                height = textureSize.height,
                textureParams = textureParams
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

    fun shareTexture() {
        cacheCurrentTexture(
            onComplete = { uri ->
                componentScope.launch {
                    shareProvider.shareUri(
                        uri = uri.toString(),
                        onComplete = AppToastHost::showConfetti
                    )
                }
            }
        )
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.update { false }
    }

    fun setImageFormat(imageFormat: ImageFormat) {
        if (_imageFormat.value != imageFormat) {
            if (pendingHistoryMode != PendingHistoryMode.FormatChange) {
                finalizePendingHistoryTransaction()
            }
            beginPendingHistoryTransaction(
                mode = PendingHistoryMode.FormatChange,
                commitDelayMillis = formatHistoryTransactionDebounce
            )
            _imageFormat.update { imageFormat }
            registerChanges()
            schedulePendingHistoryCommit()
        }
    }

    fun setQuality(quality: Quality) {
        if (_quality.value != quality) {
            beginPendingHistoryTransaction()
            _quality.update { quality }
            registerChanges()
            schedulePendingHistoryCommit()
        }
    }

    fun updateParams(params: TextureParams) {
        if (_textureParams.value != params) {
            beginPendingHistoryTransaction()
            _textureParams.update(
                onValueChanged = ::updatePreview,
                transform = { params }
            )
            registerChanges()
            schedulePendingHistoryCommit()
        }
    }

    fun setTextureWidth(width: Int) {
        val coercedWidth = width.coerceAtMost(8192)
        if (_textureSize.value.width != coercedWidth) {
            beginPendingHistoryTransaction()
            _textureSize.update(
                onValueChanged = ::updatePreview,
                transform = { it.copy(width = coercedWidth) }
            )
            registerChanges()
            schedulePendingHistoryCommit()
        }
    }

    fun setTextureHeight(height: Int) {
        val coercedHeight = height.coerceAtMost(8192)
        if (_textureSize.value.height != coercedHeight) {
            beginPendingHistoryTransaction()
            _textureSize.update(
                onValueChanged = ::updatePreview,
                transform = { it.copy(height = coercedHeight) }
            )
            registerChanges()
            schedulePendingHistoryCommit()
        }
    }

    private fun updatePreview() {
        componentScope.launch {
            _isImageLoading.update { true }
            _previewBitmap.update { null }
            debouncedImageCalculation {
                val previewSize = if (textureSize.width > 512 || textureSize.height > 512) {
                    textureSize.flexibleResize(512, 512)
                } else {
                    textureSize
                }

                textureGenerator.generateTexture(
                    width = previewSize.width,
                    height = previewSize.height,
                    textureParams = textureParams
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

    override fun currentHistorySnapshot(): HistorySnapshot = HistorySnapshot(
        textureParams = textureParams,
        textureSize = textureSize,
        imageFormat = imageFormat,
        quality = quality,
        backgroundColorForNoAlphaFormats = settingsManager
            .settingsState
            .value
            .backgroundForNoAlphaImageFormats
    )

    override fun applyHistorySnapshot(snapshot: HistorySnapshot) {
        _textureParams.update { snapshot.textureParams }
        _textureSize.update { snapshot.textureSize }
        _imageFormat.update { snapshot.imageFormat }
        _quality.update { snapshot.quality }
        restoreBackgroundColorForNoAlphaFormats(
            settingsManager = settingsManager,
            backgroundColorForNoAlphaFormats = snapshot.backgroundColorForNoAlphaFormats
        )
        updatePreview()
    }

    data class HistorySnapshot(
        val textureParams: TextureParams = TextureParams.Default,
        val textureSize: IntegerSize = IntegerSize(1000, 1000),
        val imageFormat: ImageFormat = ImageFormat.Default,
        val quality: Quality = Quality.Base(100),
        val backgroundColorForNoAlphaFormats: ColorModel = ColorModel(-0x1000000)
    )

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit,
        ): TextureGenerationComponent
    }

}
