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

package ru.tech.imageresizershrinker.feature.erase_background.presentation.viewModel

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.di.DefaultDispatcher
import ru.tech.imageresizershrinker.core.domain.image.ImageCompressor
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.image.ImageScaler
import ru.tech.imageresizershrinker.core.domain.image.ShareProvider
import ru.tech.imageresizershrinker.core.domain.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.core.domain.saving.SaveResult
import ru.tech.imageresizershrinker.core.domain.saving.model.ImageSaveTarget
import ru.tech.imageresizershrinker.core.ui.utils.state.update
import ru.tech.imageresizershrinker.feature.draw.domain.ImageDrawApplier
import ru.tech.imageresizershrinker.feature.draw.presentation.components.UiPathPaint
import ru.tech.imageresizershrinker.feature.erase_background.domain.AutoBackgroundRemover
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class EraseBackgroundViewModel @Inject constructor(
    private val imageScaler: ImageScaler<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap, ExifInterface>,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val fileController: FileController,
    private val imageDrawApplier: ImageDrawApplier<Bitmap, Path, Color>,
    private val autoBackgroundRemover: AutoBackgroundRemover<Bitmap>,
    private val shareProvider: ShareProvider<Bitmap>,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _internalBitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val internalBitmap: Bitmap? by _internalBitmap

    private val _isRecoveryOn: MutableState<Boolean> = mutableStateOf(false)
    val isRecoveryOn: Boolean by _isRecoveryOn

    private val _saveExif: MutableState<Boolean> = mutableStateOf(false)
    val saveExif: Boolean by _saveExif

    private val _trimImage: MutableState<Boolean> = mutableStateOf(true)
    val trimImage: Boolean by _trimImage

    private val _paths = mutableStateOf(listOf<UiPathPaint>())
    val paths: List<UiPathPaint> by _paths

    private val _lastPaths = mutableStateOf(listOf<UiPathPaint>())
    val lastPaths: List<UiPathPaint> by _lastPaths

    private val _undonePaths = mutableStateOf(listOf<UiPathPaint>())
    val undonePaths: List<UiPathPaint> by _undonePaths

    val haveChanges: Boolean
        get() = paths.isNotEmpty() || lastPaths.isNotEmpty() || undonePaths.isNotEmpty()

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving: Boolean by _isSaving

    private val _isErasingBG: MutableState<Boolean> = mutableStateOf(false)
    val isErasingBG: Boolean by _isErasingBG

    private val _isImageLoading: MutableState<Boolean> = mutableStateOf(false)
    val isImageLoading: Boolean by _isImageLoading

    private val _imageFormat: MutableState<ImageFormat> = mutableStateOf(ImageFormat.Default())
    val imageFormat: ImageFormat by _imageFormat

    private val _uri: MutableState<Uri> = mutableStateOf(Uri.EMPTY)
    val uri: Uri by _uri

    private val _bitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val bitmap: Bitmap? by _bitmap

    fun updateBitmap(bitmap: Bitmap?) {
        viewModelScope.launch {
            _isImageLoading.value = true
            _bitmap.value = imageScaler.scaleUntilCanShow(bitmap)
            _internalBitmap.value = _bitmap.value
            _isImageLoading.value = false
        }
    }

    fun setUri(uri: Uri) {
        _uri.value = uri
        autoEraseCount = 0
        viewModelScope.launch {
            _paths.value = listOf()
            _lastPaths.value = listOf()
            _undonePaths.value = listOf()
        }
    }

    fun decodeBitmapByUri(
        uri: Uri,
        originalSize: Boolean = true,
        onGetMimeType: (ImageFormat) -> Unit,
        onGetExif: (ExifInterface?) -> Unit,
        onGetBitmap: (Bitmap) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        _isImageLoading.value = true
        imageGetter.getImageAsync(
            uri = uri.toString(),
            originalSize = originalSize,
            onGetImage = {
                onGetBitmap(it.image)
                onGetExif(it.metadata)
                onGetMimeType(it.imageInfo.imageFormat)
            },
            onError = onError
        )
    }

    fun setMime(imageFormat: ImageFormat) {
        _imageFormat.value = imageFormat
    }

    private var savingJob: Job? = null

    fun saveBitmap(
        onComplete: (saveResult: SaveResult) -> Unit
    ) {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = viewModelScope.launch(dispatcher) {
            _isSaving.value = true
            getErasedBitmap()?.let { localBitmap ->
                onComplete(
                    fileController.save(
                        saveTarget = ImageSaveTarget<ExifInterface>(
                            imageInfo = ImageInfo(
                                imageFormat = imageFormat,
                                width = localBitmap.width,
                                height = localBitmap.height
                            ),
                            originalUri = _uri.value.toString(),
                            sequenceNumber = null,
                            data = imageCompressor.compressAndTransform(
                                image = localBitmap,
                                imageInfo = ImageInfo(
                                    imageFormat = imageFormat,
                                    width = localBitmap.width,
                                    height = localBitmap.height
                                )
                            )
                        ), keepOriginalMetadata = _saveExif.value
                    )
                )
            }
            _isSaving.value = false
        }
    }

    private suspend fun getErasedBitmap(): Bitmap? {
        return if (autoEraseCount == 0) {
            imageDrawApplier.applyEraseToImage(
                pathPaints = _paths.value,
                imageUri = _uri.value.toString()
            )
        } else {
            imageDrawApplier.applyEraseToImage(
                pathPaints = _paths.value,
                image = _bitmap.value,
                shaderSourceUri = _uri.value.toString()
            )
        }?.let {
            if (trimImage) autoBackgroundRemover.trimEmptyParts(it)
            else it
        }
    }

    fun shareBitmap(onComplete: () -> Unit) {
        viewModelScope.launch {
            getErasedBitmap()?.let {
                _isSaving.value = true
                shareProvider.shareImage(
                    imageInfo = ImageInfo(
                        imageFormat = imageFormat,
                        width = it.width,
                        height = it.height
                    ),
                    image = it,
                    onComplete = onComplete
                )
            } ?: onComplete()
            _isSaving.value = false
        }.also {
            _isSaving.value = false
            savingJob?.cancel()
            savingJob = it
        }
    }

    fun undo() {
        if (paths.isEmpty() && lastPaths.isNotEmpty()) {
            _paths.value = lastPaths
            _lastPaths.value = listOf()
            return
        }
        if (paths.isEmpty()) return

        val lastPath = paths.last()

        _paths.update { it - lastPath }
        _undonePaths.update { it + lastPath }
    }

    fun redo() {
        if (undonePaths.isEmpty()) return

        val lastPath = undonePaths.last()
        _paths.update { it + lastPath }
        _undonePaths.update { it - lastPath }
    }

    fun addPath(pathPaint: UiPathPaint) {
        _paths.update { it + pathPaint }
        _undonePaths.value = listOf()
    }

    fun clearDrawing() {
        if (paths.isNotEmpty()) {
            _lastPaths.value = paths
            _paths.value = listOf()
            _undonePaths.value = listOf()
        }
    }

    fun setSaveExif(bool: Boolean) {
        _saveExif.value = bool
    }

    fun setTrimImage(boolean: Boolean) {
        _trimImage.value = boolean
    }

    private var autoEraseCount: Int = 0
    fun autoEraseBackground(
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        viewModelScope.launch(dispatcher) {
            getErasedBitmap()?.let { bitmap1 ->
                _isErasingBG.value = true
                autoBackgroundRemover.removeBackgroundFromImage(
                    image = bitmap1,
                    onSuccess = {
                        _bitmap.value = it
                        _paths.value = listOf()
                        _lastPaths.value = listOf()
                        _undonePaths.value = listOf()
                        _isErasingBG.value = false
                        onSuccess()
                        autoEraseCount++
                    },
                    onFailure = {
                        _isErasingBG.value = false
                        onFailure(it)
                    }
                )
            }
        }
    }

    fun resetImage() {
        viewModelScope.launch {
            autoEraseCount = 0
            _bitmap.value = _internalBitmap.value
            _paths.value = listOf()
            _lastPaths.value = listOf()
        }
    }

    fun toggleEraser() {
        _isRecoveryOn.update { !it }
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.value = false
    }

    fun cacheCurrentImage(onComplete: (Uri) -> Unit) {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = viewModelScope.launch {
            _isSaving.value = true
            getErasedBitmap()?.let { image ->
                shareProvider.cacheImage(
                    image = image,
                    imageInfo = ImageInfo(
                        imageFormat = imageFormat,
                        width = image.width,
                        height = image.height
                    ),
                    name = Random.nextInt().toString()
                )?.let { uri ->
                    onComplete(uri.toUri())
                }
            }
            _isSaving.value = false
        }
    }

}