package ru.tech.imageresizershrinker.feature.erase_background.presentation.viewModel

import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.domain.image.ImageManager
import ru.tech.imageresizershrinker.core.domain.image.draw.ImageDrawApplier
import ru.tech.imageresizershrinker.core.domain.model.ImageData
import ru.tech.imageresizershrinker.core.domain.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.core.domain.saving.SaveResult
import ru.tech.imageresizershrinker.core.domain.saving.model.ImageSaveTarget
import ru.tech.imageresizershrinker.core.ui.model.UiPathPaint
import ru.tech.imageresizershrinker.core.ui.utils.state.update
import javax.inject.Inject

@HiltViewModel
class EraseBackgroundViewModel @Inject constructor(
    private val imageManager: ImageManager<Bitmap, ExifInterface>,
    private val fileController: FileController,
    private val imageDrawApplier: ImageDrawApplier<Bitmap, Path, Color>
) : ViewModel() {

    private val _internalBitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val internalBitmap: Bitmap? by _internalBitmap

    private val _isRecoveryOn: MutableState<Boolean> = mutableStateOf(false)
    val isRecoveryOn: Boolean by _isRecoveryOn

    private val _saveExif: MutableState<Boolean> = mutableStateOf(false)
    val saveExif: Boolean by _saveExif

    private val _trimImage: MutableState<Boolean> = mutableStateOf(true)
    val trimImage: Boolean by _trimImage

    private val _orientation: MutableState<Int> =
        mutableIntStateOf(ActivityInfo.SCREEN_ORIENTATION_USER)
    val orientation: Int by _orientation

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
            _bitmap.value = imageManager.scaleUntilCanShow(bitmap)
            _internalBitmap.value = _bitmap.value
            _isImageLoading.value = false
        }
    }

    fun setUri(uri: Uri) {
        _uri.value = uri
        autoEraseCount = 0
        viewModelScope.launch {
            _orientation.value = calculateScreenOrientationBasedOnUri(uri)
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
        imageManager.getImageAsync(
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

    private suspend fun calculateScreenOrientationBasedOnUri(uri: Uri): Int {
        val bmp = imageManager
            .getImage(
                uri = uri.toString(),
                originalSize = false
            )?.image ?: return ActivityInfo.SCREEN_ORIENTATION_USER
        val imageRatio = bmp.width / bmp.height.toFloat()
        return if (imageRatio <= 1.05f) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }

    private var savingJob: Job? = null

    fun saveBitmap(
        onComplete: (saveResult: SaveResult) -> Unit
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
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
                            data = imageManager.compress(
                                ImageData(
                                    image = localBitmap,
                                    imageInfo = ImageInfo(
                                        imageFormat = imageFormat,
                                        width = localBitmap.width,
                                        height = localBitmap.height
                                    )
                                )
                            )
                        ), keepMetadata = _saveExif.value
                    )
                )
            }
            _isSaving.value = false
        }
    }.also {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = it
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
        }
    }

    fun shareBitmap(onComplete: () -> Unit) {
        viewModelScope.launch {
            getErasedBitmap()?.let { trim(it) }?.let {
                _isSaving.value = true
                imageManager.shareImage(
                    ImageData(
                        image = it,
                        imageInfo = ImageInfo(
                            imageFormat = imageFormat,
                            width = it.width,
                            height = it.height
                        )
                    ),
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

    private suspend fun trim(
        bitmap: Bitmap
    ): Bitmap {
        if (!_trimImage.value) return bitmap
        return imageManager.trimEmptyParts(bitmap)
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
    fun autoEraseBackground(onSuccess: () -> Unit, onFailure: (Throwable) -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                getErasedBitmap()?.let { bitmap1 ->
                    _isErasingBG.value = true
                    imageManager.removeBackgroundFromImage(
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

}