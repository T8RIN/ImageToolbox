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

package com.t8rin.imagetoolbox.feature.base64_tools.presentation.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
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
import com.t8rin.imagetoolbox.core.domain.utils.isBase64
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.domain.utils.timestamp
import com.t8rin.imagetoolbox.core.domain.utils.trimToBase64
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.feature.base64_tools.domain.Base64Converter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job

class Base64ToolsComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted initialUri: Uri?,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val imageGetter: ImageGetter<Bitmap>,
    private val shareProvider: ImageShareProvider<Bitmap>,
    private val fileController: FileController,
    private val converter: Base64Converter,
    private val imageCompressor: ImageCompressor<Bitmap>,
    dispatchersHolder: DispatchersHolder,
) : BaseComponent(dispatchersHolder, componentContext) {

    private val _imageFormat = mutableStateOf(ImageFormat.Default)
    val imageFormat by _imageFormat

    private val _quality = mutableStateOf<Quality>(Quality.Base())
    val quality by _quality

    private val _uri = mutableStateOf<Uri?>(null)
    val uri by _uri

    private val _base64String = mutableStateOf("")
    val base64String by _base64String

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving by _isSaving

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    init {
        debounce {
            initialUri?.let(::setUri)
        }
    }

    fun setUri(uri: Uri) {
        _uri.value = uri

        updateBase64()
    }

    private fun updateBase64() {
        debouncedImageCalculation {
            uri?.let { imageGetter.getImage(it) }?.let { image ->
                shareProvider.cacheImage(
                    image = image,
                    imageInfo = ImageInfo(
                        width = image.width,
                        height = image.height,
                        imageFormat = imageFormat,
                        quality = quality,
                        originalUri = uri.toString()
                    )
                )?.let {
                    _base64String.value = converter.encode(it)
                }
            }
        }
    }

    fun setBase64(base64: String) {
        _base64String.value = base64
        debouncedImageCalculation {
            _uri.value = converter.decode(base64)?.toUri()
        }
    }

    fun setImageFormat(imageFormat: ImageFormat) {
        _imageFormat.update { imageFormat }
        updateBase64()
    }

    fun setQuality(quality: Quality) {
        _quality.update { quality }
        updateBase64()
    }

    fun getFormatForFilenameSelection(): ImageFormat = imageFormat

    fun shareBitmap(onComplete: () -> Unit) {
        savingJob = trackProgress {
            _isSaving.update { true }
            uri?.let { imageGetter.getImage(it) }?.let { image ->
                shareProvider.shareImage(
                    image = image,
                    imageInfo = ImageInfo(
                        width = image.width,
                        height = image.height,
                        imageFormat = imageFormat,
                        quality = quality,
                        originalUri = uri.toString()
                    ),
                    onComplete = onComplete
                )
            }
            _isSaving.update { false }
        }
    }

    fun cacheCurrentImage(onComplete: (Uri) -> Unit) {
        savingJob = trackProgress {
            _isSaving.update { true }
            uri?.let { imageGetter.getImage(it) }?.let { image ->
                shareProvider.cacheImage(
                    image = image,
                    imageInfo = ImageInfo(
                        width = image.width,
                        height = image.height,
                        imageFormat = imageFormat,
                        quality = quality,
                        originalUri = uri.toString()
                    )
                )?.let { uri ->
                    onComplete(uri.toUri())
                }
            }
            _isSaving.update { false }
        }
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.update { false }
    }

    fun saveBitmap(
        oneTimeSaveLocationUri: String?,
        onComplete: (result: SaveResult) -> Unit,
    ) {
        savingJob = trackProgress {
            _isSaving.update { true }
            uri?.let { imageGetter.getImage(it) }?.let { image ->
                val imageInfo = ImageInfo(
                    width = image.width,
                    height = image.height,
                    imageFormat = imageFormat,
                    quality = quality,
                    originalUri = uri.toString()
                )
                onComplete(
                    fileController.save(
                        saveTarget = ImageSaveTarget(
                            imageInfo = imageInfo,
                            metadata = null,
                            originalUri = uri.toString(),
                            sequenceNumber = null,
                            data = imageCompressor.compressAndTransform(
                                image = image,
                                imageInfo = imageInfo.copy(
                                    originalUri = uri.toString()
                                )
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

    fun saveContentToTxt(
        uri: Uri,
        onResult: (SaveResult) -> Unit
    ) {
        base64String.takeIf { it.isNotEmpty() }?.let { data ->
            componentScope.launch {
                fileController.writeBytes(
                    uri = uri.toString(),
                    block = {
                        it.writeBytes(data.encodeToByteArray())
                    }
                ).also(onResult).onSuccess(::registerSave)
            }
        }
    }

    fun generateTextFilename(): String = "Base64_${timestamp()}.txt"

    fun shareText(onSuccess: () -> Unit) {
        base64String.takeIf { it.isNotEmpty() }?.let { data ->
            componentScope.launch {
                shareProvider.shareData(
                    writeData = {
                        it.writeBytes(data.encodeToByteArray())
                    },
                    filename = generateTextFilename()
                )
                onSuccess()
            }
        }
    }

    fun setBase64FromUri(
        uri: Uri,
        onFailure: () -> Unit
    ) {
        componentScope.launch {
            val text = fileController.readBytes(uri.toString()).decodeToString().trimToBase64()
            if (text.isBase64()) {
                setBase64(text)
            } else {
                onFailure()
            }
        }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialUri: Uri?,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit,
        ): Base64ToolsComponent
    }

}