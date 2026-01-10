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

package com.t8rin.imagetoolbox.feature.delete_exif.presentation.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.image.ShareProvider
import com.t8rin.imagetoolbox.core.domain.image.clearAttributes
import com.t8rin.imagetoolbox.core.domain.image.model.MetadataTag
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.FilenameCreator
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.saving.updateProgress
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.leftFrom
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.rightFrom
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

class DeleteExifComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialUris: List<Uri>?,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val fileController: FileController,
    private val imageGetter: ImageGetter<Bitmap>,
    private val imageScaler: ImageScaler<Bitmap>,
    private val shareProvider: ShareProvider,
    private val filenameCreator: FilenameCreator,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    init {
        debounce {
            initialUris?.let {
                updateUris(
                    uris = it,
                    onFailure = {}
                )
            }
        }
    }

    private val _uris = mutableStateOf<List<Uri>?>(null)
    val uris by _uris

    private val _bitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val bitmap: Bitmap? by _bitmap

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving: Boolean by _isSaving

    private val _previewBitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val previewBitmap: Bitmap? by _previewBitmap

    private val _done: MutableState<Int> = mutableIntStateOf(0)
    val done by _done

    private val _selectedUri: MutableState<Uri?> = mutableStateOf(null)
    val selectedUri by _selectedUri

    private val _selectedTags: MutableState<List<MetadataTag>> = mutableStateOf(emptyList())
    val selectedTags by _selectedTags

    fun updateUris(
        uris: List<Uri>?,
        onFailure: (Throwable) -> Unit
    ) {
        _uris.value = null
        _uris.value = uris
        uris?.firstOrNull()?.let {
            updateSelectedUri(it, onFailure)
        }
    }

    fun updateUrisSilently(removedUri: Uri) {
        componentScope.launch {
            _uris.value = uris
            if (_selectedUri.value == removedUri) {
                val index = uris?.indexOf(removedUri) ?: -1
                if (index == 0) {
                    uris?.getOrNull(1)?.let {
                        _selectedUri.value = it
                        _bitmap.value =
                            imageGetter.getImage(it.toString(), originalSize = false)?.image
                    }
                } else {
                    uris?.getOrNull(index - 1)?.let {
                        _selectedUri.value = it
                        _bitmap.value =
                            imageGetter.getImage(it.toString(), originalSize = false)?.image
                    }
                }
            }
            val u = _uris.value?.toMutableList()?.apply {
                remove(removedUri)
            }
            _uris.value = u
        }
    }

    private fun updateBitmap(bitmap: Bitmap?) {
        componentScope.launch {
            _isImageLoading.value = true
            _bitmap.value = bitmap
            _previewBitmap.value = imageScaler.scaleUntilCanShow(bitmap)
            delay(500)
            _isImageLoading.value = false
        }
    }

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    fun saveBitmaps(
        oneTimeSaveLocationUri: String?,
        onResult: (List<SaveResult>) -> Unit
    ) {
        savingJob = trackProgress {
            _isSaving.value = true
            val results = mutableListOf<SaveResult>()
            _done.value = 0
            uris?.forEach { uri ->
                runSuspendCatching {
                    imageGetter.getImage(uri.toString())
                }.getOrNull()?.let {
                    val metadata = if (selectedTags.isNotEmpty()) {
                        it.metadata?.clearAttributes(selectedTags)
                    } else null

                    results.add(
                        fileController.save(
                            ImageSaveTarget(
                                imageInfo = it.imageInfo,
                                originalUri = uri.toString(),
                                sequenceNumber = _done.value,
                                metadata = metadata,
                                data = ByteArray(0),
                                readFromUriInsteadOfData = true
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
                    total = uris.orEmpty().size
                )
            }
            onResult(results)
            _isSaving.value = false
        }
    }

    fun updateSelectedUri(
        uri: Uri,
        onFailure: (Throwable) -> Unit = {}
    ) = componentScope.launch {
        _isImageLoading.value = true
        _selectedUri.value = uri
        imageGetter.getImageAsync(
            uri = uri.toString(),
            originalSize = false,
            onGetImage = {
                updateBitmap(it.image)
            },
            onFailure = {
                _isImageLoading.value = false
                onFailure(it)
            }
        )
    }

    fun shareBitmaps(onComplete: () -> Unit) {
        _isSaving.update { true }
        cacheImages { uris ->
            savingJob = trackProgress {
                shareProvider.shareUris(uris.map(Uri::toString))
                onComplete()
                _isSaving.update { false }
            }
        }
    }

    fun cancelSaving() {
        _isSaving.update { false }
        savingJob?.cancel()
        savingJob = null
    }

    fun cacheCurrentImage(onComplete: (Uri) -> Unit) {
        cacheImages(
            uris = listOfNotNull(selectedUri),
            onComplete = {
                if (it.isNotEmpty()) onComplete(it.first())
            }
        )
    }

    fun addTag(tag: MetadataTag) {
        if (tag in selectedTags) {
            _selectedTags.update { it - tag }
        } else {
            _selectedTags.update { it + tag }
        }
    }

    fun cacheImages(
        uris: List<Uri>? = this.uris,
        onComplete: (List<Uri>) -> Unit
    ) {
        savingJob = trackProgress {
            _isSaving.value = true
            _done.value = 0
            val list = mutableListOf<Uri>()
            uris?.forEach { uri ->
                imageGetter.getImage(
                    uri.toString()
                )?.let {
                    val metadata = if (selectedTags.isNotEmpty()) {
                        it.metadata?.clearAttributes(selectedTags)
                    } else null

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
                                sequenceNumber = done,
                                metadata = metadata,
                                data = ByteArray(0)
                            )
                        )
                    )?.let { uri ->
                        fileController.writeMetadata(
                            imageUri = uri,
                            metadata = metadata
                        )
                        list.add(uri.toUri())
                    }
                }
                _done.value++
                updateProgress(
                    done = done,
                    total = uris.size
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


    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialUris: List<Uri>?,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit,
        ): DeleteExifComponent
    }
}