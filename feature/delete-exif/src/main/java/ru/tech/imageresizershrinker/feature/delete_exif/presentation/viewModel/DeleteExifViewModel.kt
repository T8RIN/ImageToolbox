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

package ru.tech.imageresizershrinker.feature.delete_exif.presentation.viewModel

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import ru.tech.imageresizershrinker.core.domain.dispatchers.DispatchersHolder
import ru.tech.imageresizershrinker.core.domain.image.ImageCompressor
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.image.ImageScaler
import ru.tech.imageresizershrinker.core.domain.image.ShareProvider
import ru.tech.imageresizershrinker.core.domain.image.model.MetadataTag
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.core.domain.saving.model.ImageSaveTarget
import ru.tech.imageresizershrinker.core.domain.saving.model.SaveResult
import ru.tech.imageresizershrinker.core.domain.utils.smartJob
import ru.tech.imageresizershrinker.core.ui.transformation.ImageInfoTransformation
import ru.tech.imageresizershrinker.core.ui.utils.BaseViewModel
import ru.tech.imageresizershrinker.core.ui.utils.state.update
import javax.inject.Inject

@HiltViewModel
class DeleteExifViewModel @Inject constructor(
    private val fileController: FileController,
    private val imageGetter: ImageGetter<Bitmap, ExifInterface>,
    private val imageScaler: ImageScaler<Bitmap>,
    private val shareProvider: ShareProvider<Bitmap>,
    private val imageCompressor: ImageCompressor<Bitmap>,
    val imageInfoTransformationFactory: ImageInfoTransformation.Factory,
    dispatchersHolder: DispatchersHolder
) : BaseViewModel(dispatchersHolder) {

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
        onError: (Throwable) -> Unit
    ) {
        _uris.value = null
        _uris.value = uris
        uris?.firstOrNull()?.let {
            setUri(it, onError)
        }
    }

    fun updateUrisSilently(removedUri: Uri) {
        viewModelScope.launch(defaultDispatcher) {
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
        viewModelScope.launch {
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
        savingJob = viewModelScope.launch(defaultDispatcher) {
            _isSaving.value = true
            val results = mutableListOf<SaveResult>()
            _done.value = 0
            uris?.forEach { uri ->
                runCatching {
                    imageGetter.getImage(uri.toString())
                }.getOrNull()?.let {
                    val metadata: ExifInterface? = if (selectedTags.isNotEmpty()) {
                        it.metadata?.apply {
                            selectedTags.forEach { tag ->
                                setAttribute(tag.key, null)
                            }
                        }
                    } else null

                    results.add(
                        fileController.save(
                            ImageSaveTarget(
                                imageInfo = it.imageInfo,
                                originalUri = uri.toString(),
                                sequenceNumber = _done.value,
                                metadata = metadata,
                                data = imageCompressor.compressAndTransform(it.image, it.imageInfo)
                            ),
                            keepOriginalMetadata = false,
                            oneTimeSaveLocationUri = oneTimeSaveLocationUri
                        )
                    )
                } ?: results.add(
                    SaveResult.Error.Exception(Throwable())
                )

                _done.value += 1
            }
            onResult(results)
            _isSaving.value = false
        }
    }

    fun setUri(
        uri: Uri,
        onError: (Throwable) -> Unit
    ) = viewModelScope.launch(defaultDispatcher) {
        _isImageLoading.value = true
        _selectedUri.value = uri
        imageGetter.getImageAsync(
            uri = uri.toString(),
            originalSize = false,
            onGetImage = {
                updateBitmap(it.image)
            },
            onError = {
                _isImageLoading.value = false
                onError(it)
            }
        )
    }

    fun shareBitmaps(onComplete: () -> Unit) {
        savingJob = viewModelScope.launch {
            _isSaving.value = true
            shareProvider.shareImages(
                uris = uris?.map { it.toString() } ?: emptyList(),
                imageLoader = { uri ->
                    imageGetter.getImage(uri)?.let {
                        it.image to it.imageInfo
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
                }
            )
        }
    }

    fun cancelSaving() {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = null
    }

    fun cacheCurrentImage(onComplete: (Uri) -> Unit) {
        savingJob = viewModelScope.launch {
            _isSaving.value = true
            imageGetter.getImage(
                selectedUri.toString()
            )?.let { (image, imageInfo) ->
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

    fun addTag(tag: MetadataTag) {
        if (tag in selectedTags) {
            _selectedTags.update { it - tag }
        } else {
            _selectedTags.update { it + tag }
        }
    }

    fun cacheImages(
        onComplete: (List<Uri>) -> Unit
    ) {
        savingJob = viewModelScope.launch {
            _isSaving.value = true
            _done.value = 0
            val list = mutableListOf<Uri>()
            uris?.forEach {
                imageGetter.getImage(
                    it.toString()
                )?.let { (image, imageInfo) ->
                    shareProvider.cacheImage(
                        image = image,
                        imageInfo = imageInfo.copy(originalUri = it.toString())
                    )?.let { uri ->
                        list.add(uri.toUri())
                    }
                }
                _done.value++
            }
            onComplete(list)
            _isSaving.value = false
        }
    }

}