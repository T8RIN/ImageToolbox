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

package com.t8rin.imagetoolbox.feature.edit_exif.presentation.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.Metadata
import com.t8rin.imagetoolbox.core.domain.image.ShareProvider
import com.t8rin.imagetoolbox.core.domain.image.clearAllAttributes
import com.t8rin.imagetoolbox.core.domain.image.clearAttribute
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.MetadataTag
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.FilenameCreator
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.core.utils.imageSize
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext


class EditExifComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialUri: Uri?,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val fileController: FileController,
    private val imageGetter: ImageGetter<Bitmap>,
    private val shareProvider: ShareProvider,
    private val filenameCreator: FilenameCreator,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    init {
        debounce {
            initialUri?.let(::setUri)
        }
    }

    private val _exif: MutableState<Metadata?> = mutableStateOf(null)
    val exif by _exif

    private val _imageInfo: MutableState<ImageInfo> = mutableStateOf(ImageInfo())
    val imageFormat get() = _imageInfo.value.imageFormat

    private val _uri: MutableState<Uri> = mutableStateOf(Uri.EMPTY)
    val uri: Uri by _uri

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving by _isSaving

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    fun saveBitmap(
        oneTimeSaveLocationUri: String?
    ) {
        if (isImageLoading || uri == Uri.EMPTY) return

        savingJob = trackProgress {
            _isSaving.update { true }
            try {
                val result = fileController.save(
                    ImageSaveTarget(
                        imageInfo = _imageInfo.value,
                        originalUri = uri.toString(),
                        sequenceNumber = null,
                        metadata = exif,
                        data = ByteArray(0),
                        readFromUriInsteadOfData = true
                    ),
                    keepOriginalMetadata = false,
                    oneTimeSaveLocationUri = oneTimeSaveLocationUri
                )

                parseSaveResult(result.onSuccess(::registerSave))
            } finally {
                _isSaving.update { false }
            }
        }
    }

    fun setUri(uri: Uri) {
        _uri.update { uri }
        _exif.update { null }
        debouncedImageCalculation(delay = 0) {
            val (metadata, imageInfo) = withContext(ioDispatcher) {
                val size = uri.imageSize()
                fileController.readMetadata(uri.toString()) to ImageInfo(
                    width = size?.width ?: 0,
                    height = size?.height ?: 0,
                    imageFormat = ImageFormat[imageGetter.getExtension(uri.toString())],
                    originalUri = uri.toString()
                )
            }
            _exif.update { metadata }
            _imageInfo.update { imageInfo }
            registerChangesCleared()
        }
        _isImageLoading.update { true }
    }

    fun shareBitmap() {
        cacheCurrentImage {
            componentScope.launch {
                shareProvider.shareUris(listOf(it.toString()))
                AppToastHost.showConfetti()
            }
        }
    }

    fun cacheCurrentImage(onComplete: (Uri) -> Unit) {
        if (isImageLoading || uri == Uri.EMPTY) return

        savingJob = trackProgress {
            _isSaving.update { true }
            try {
                shareProvider.cacheData(
                    writeData = { w ->
                        val result = fileController.transferBytes(uri.toString(), w)
                        if (result is SaveResult.Error) throw result.throwable
                    },
                    filename = filenameCreator.constructImageFilename(
                        saveTarget = ImageSaveTarget(
                            imageInfo = _imageInfo.value,
                            originalUri = uri.toString(),
                            metadata = exif,
                            sequenceNumber = null,
                            data = ByteArray(0)
                        )
                    )
                )?.let { uri ->
                    withContext(ioDispatcher) {
                        fileController.writeMetadata(
                            imageUri = uri,
                            metadata = exif
                        )
                    }
                    onComplete(uri.toUri())
                }
            } finally {
                _isSaving.update { false }
            }
        }
    }

    fun clearExif() {
        updateExif(_exif.value?.clearAllAttributes())
    }

    private fun updateExif(metadata: Metadata?) {
        _exif.update { metadata }
        registerChanges()
    }

    fun removeExifTag(tag: MetadataTag) {
        updateExif(_exif.value?.clearAttribute(tag))
    }

    fun updateExifByTag(
        tag: MetadataTag,
        value: String,
    ) {
        updateExif(_exif.value?.setAttribute(tag, value))
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.update { false }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialUri: Uri?,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit,
        ): EditExifComponent
    }
}
