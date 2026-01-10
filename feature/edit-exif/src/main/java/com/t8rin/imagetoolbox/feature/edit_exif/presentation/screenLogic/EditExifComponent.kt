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
import com.t8rin.imagetoolbox.core.domain.image.model.MetadataTag
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.FilenameCreator
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job


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

    private val _imageFormat: MutableState<ImageFormat> = mutableStateOf(ImageFormat.Default)
    val imageFormat by _imageFormat

    private val _uri: MutableState<Uri> = mutableStateOf(Uri.EMPTY)
    val uri: Uri by _uri

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving by _isSaving

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    fun saveBitmap(
        oneTimeSaveLocationUri: String?,
        onComplete: (result: SaveResult) -> Unit,
    ) {
        savingJob = trackProgress {
            _isSaving.update { true }
            runSuspendCatching {
                imageGetter.getImage(uri.toString())
            }.getOrNull()?.let {
                val result = fileController.save(
                    ImageSaveTarget(
                        imageInfo = it.imageInfo,
                        originalUri = uri.toString(),
                        sequenceNumber = null,
                        metadata = exif,
                        data = ByteArray(0),
                        readFromUriInsteadOfData = true
                    ),
                    keepOriginalMetadata = false,
                    oneTimeSaveLocationUri = oneTimeSaveLocationUri
                )

                onComplete(result.onSuccess(::registerSave))
            }
            _isSaving.update { false }
        }
    }

    fun setUri(uri: Uri) {
        _uri.update { uri }
        componentScope.launch {
            imageGetter.getImage(uri.toString())?.let {
                _exif.value = it.metadata
                _imageFormat.value = it.imageInfo.imageFormat
            }
        }
    }

    fun shareBitmap(onComplete: () -> Unit) {
        cacheCurrentImage {
            componentScope.launch {
                shareProvider.shareUris(listOf(it.toString()))
                onComplete()
            }
        }
    }

    fun cacheCurrentImage(onComplete: (Uri) -> Unit) {
        savingJob = trackProgress {
            _isSaving.update { true }
            imageGetter.getImage(
                uri.toString()
            )?.let {
                shareProvider.cacheData(
                    writeData = { w ->
                        w.writeBytes(
                            fileController.readBytes(uri.toString())
                        )
                    },
                    filename = filenameCreator.constructImageFilename(
                        saveTarget = ImageSaveTarget(
                            imageInfo = it.imageInfo.copy(originalUri = uri.toString()),
                            originalUri = uri.toString(),
                            metadata = exif,
                            sequenceNumber = null,
                            data = ByteArray(0)
                        )
                    )
                )?.let { uri ->
                    fileController.writeMetadata(
                        imageUri = uri,
                        metadata = exif
                    )
                    onComplete(uri.toUri())
                }
            }
            _isSaving.update { false }
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
