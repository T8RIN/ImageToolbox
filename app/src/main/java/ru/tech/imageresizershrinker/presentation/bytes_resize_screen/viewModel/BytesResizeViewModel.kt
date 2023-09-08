package ru.tech.imageresizershrinker.presentation.bytes_resize_screen.viewModel

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.domain.image.ImageManager
import ru.tech.imageresizershrinker.domain.model.ImageData
import ru.tech.imageresizershrinker.domain.model.ImageFormat
import ru.tech.imageresizershrinker.domain.model.ImageInfo
import ru.tech.imageresizershrinker.domain.model.Preset
import ru.tech.imageresizershrinker.domain.saving.FileController
import ru.tech.imageresizershrinker.domain.saving.SaveResult
import ru.tech.imageresizershrinker.domain.saving.model.ImageSaveTarget
import javax.inject.Inject

@HiltViewModel
class BytesResizeViewModel @Inject constructor(
    private val fileController: FileController,
    private val imageManager: ImageManager<Bitmap, ExifInterface>
) : ViewModel() {

    private val _imageSize: MutableState<Long> = mutableLongStateOf(0L)
    val imageSize by _imageSize

    private val _canSave = mutableStateOf(false)
    val canSave by _canSave

    private val _presetSelected: MutableState<Int> = mutableIntStateOf(-1)
    val presetSelected by _presetSelected

    private val _handMode = mutableStateOf(true)
    val handMode by _handMode

    private val _uris = mutableStateOf<List<Uri>?>(null)
    val uris by _uris

    private val _bitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val bitmap: Bitmap? by _bitmap

    private val _keepExif = mutableStateOf(false)
    val keepExif by _keepExif

    private val _isImageLoading: MutableState<Boolean> = mutableStateOf(false)
    val isImageLoading: Boolean by _isImageLoading

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving: Boolean by _isSaving

    private val _previewBitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val previewBitmap: Bitmap? by _previewBitmap

    private val _done: MutableState<Int> = mutableIntStateOf(0)
    val done by _done

    private val _selectedUri: MutableState<Uri?> = mutableStateOf(null)
    val selectedUri by _selectedUri

    private val _maxBytes: MutableState<Long> = mutableLongStateOf(0L)
    val maxBytes by _maxBytes

    private val _imageFormat = mutableStateOf(ImageFormat.Default())
    val imageFormat by _imageFormat

    fun setMime(imageFormat: ImageFormat) {
        if (_imageFormat.value != imageFormat) {
            _imageFormat.value = imageFormat
            viewModelScope.launch {
                _bitmap.value?.let {
                    _isImageLoading.value = true
                    _imageSize.value = imageManager.calculateImageSize(
                        imageData = ImageData(
                            image = it,
                            imageInfo = ImageInfo(imageFormat = imageFormat)
                        )
                    )
                    _isImageLoading.value = false
                }
            }
        }
    }

    fun updateUris(uris: List<Uri>?) {
        _uris.value = null
        _uris.value = uris
        _selectedUri.value = uris?.firstOrNull()
    }

    fun updateUrisSilently(removedUri: Uri) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _uris.value = uris
                if (_selectedUri.value == removedUri) {
                    val index = uris?.indexOf(removedUri) ?: -1
                    if (index == 0) {
                        uris?.getOrNull(1)?.let {
                            _selectedUri.value = it
                            _bitmap.value = imageManager.getImage(it.toString())?.image
                        }
                    } else {
                        uris?.getOrNull(index - 1)?.let {
                            _selectedUri.value = it
                            _bitmap.value = imageManager.getImage(it.toString())?.image
                        }
                    }
                }
                val u = _uris.value?.toMutableList()?.apply {
                    remove(removedUri)
                }
                _uris.value = u
            }
        }
    }


    fun updateBitmap(bitmap: Bitmap?) {
        viewModelScope.launch {
            _isImageLoading.value = true
            _bitmap.value = bitmap
            _previewBitmap.value = imageManager.scaleUntilCanShow(bitmap)
            _isImageLoading.value = false
        }
    }

    fun setKeepExif(boolean: Boolean) {
        _keepExif.value = boolean
    }

    private var savingJob: Job? = null

    fun saveBitmaps(
        onResult: (Int, String) -> Unit
    ) = viewModelScope.launch {
        _isSaving.value = true
        withContext(Dispatchers.IO) {
            var failed = 0
            _done.value = 0
            uris?.forEach { uri ->
                runCatching {
                    imageManager.getImage(uri.toString())
                }.getOrNull()?.image?.let { bitmap ->
                    kotlin.runCatching {
                        if (handMode) {
                            imageManager.scaleByMaxBytes(
                                image = bitmap,
                                maxBytes = maxBytes,
                                imageFormat = imageFormat
                            )
                        } else {
                            imageManager.scaleByMaxBytes(
                                image = bitmap,
                                maxBytes = (fileController.getSize(uri.toString()) ?: 0)
                                    .times(_presetSelected.value / 100f)
                                    .toLong(),
                                imageFormat = imageFormat
                            )
                        }
                    }.let { result ->
                        if (result.isSuccess && result.getOrNull() != null) {
                            val scaled = result.getOrNull()!!
                            val localBitmap = scaled.image

                            val saveResult = fileController.save(
                                ImageSaveTarget<ExifInterface>(
                                    imageInfo = ImageInfo(
                                        imageFormat = imageFormat,
                                        width = localBitmap.width,
                                        height = localBitmap.height
                                    ),
                                    originalUri = uri.toString(),
                                    sequenceNumber = _done.value + 1,
                                    data = imageManager.compress(
                                        ImageData(
                                            image = localBitmap,
                                            imageInfo = ImageInfo(
                                                imageFormat = imageFormat,
                                                quality = scaled.imageInfo.quality
                                            )
                                        )
                                    )
                                ),
                                keepMetadata = keepExif
                            )
                            if (saveResult is SaveResult.Error.MissingPermissions) {
                                return@withContext onResult(-1, "")
                            }
                        } else failed += 1
                    }
                }
                _done.value += 1
            }
            onResult(failed, fileController.savingPath)
        }
        _isSaving.value = false
    }.also {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = it
    }

    fun setBitmap(uri: Uri) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                updateBitmap(
                    imageManager.getImage(
                        uri = uri.toString(),
                        originalSize = false
                    )?.image
                )
                _selectedUri.value = uri
            }
        }
    }

    private fun updateCanSave() {
        _canSave.value =
            _bitmap.value != null && (_maxBytes.value != 0L && _handMode.value || !_handMode.value && _presetSelected.value != -1)
    }

    fun updateMaxBytes(newBytes: String) {
        val b = newBytes.toLongOrNull() ?: 0
        _maxBytes.value = b * 1024
        updateCanSave()
    }

    fun selectPreset(preset: Preset) {
        preset.value()?.let { _presetSelected.value = it }
        updateCanSave()
    }

    fun updateHandMode() {
        _handMode.value = !_handMode.value
        updateCanSave()
    }

    fun shareBitmaps(onComplete: () -> Unit) {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = viewModelScope.launch {
            _isSaving.value = true
            imageManager.shareImages(
                uris = uris?.map { it.toString() } ?: emptyList(),
                imageLoader = { uri ->
                    imageManager.getImage(uri)?.image?.let { bitmap ->
                        if (handMode) {
                            imageManager.scaleByMaxBytes(
                                image = bitmap,
                                maxBytes = maxBytes,
                                imageFormat = imageFormat
                            )
                        } else {
                            imageManager.scaleByMaxBytes(
                                image = bitmap,
                                maxBytes = (fileController.getSize(uri) ?: 0)
                                    .times(_presetSelected.value / 100f)
                                    .toLong(),
                                imageFormat = imageFormat
                            )
                        }
                    }?.let { scaled ->
                        scaled.copy(
                            imageInfo = scaled.imageInfo.copy(imageFormat = imageFormat)
                        )
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

    fun decodeBitmapByUri(
        uri: Uri,
        originalSize: Boolean,
        onGetMimeType: (ImageFormat) -> Unit,
        onGetMetadata: (ExifInterface?) -> Unit,
        onGetImage: (Bitmap?) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        imageManager.getImageAsync(
            uri = uri.toString(),
            originalSize = originalSize,
            onGetImage = {
                onGetImage(it.image)
                onGetMetadata(it.metadata)
                onGetMimeType(it.imageInfo.imageFormat)
                viewModelScope.launch {
                    _isImageLoading.value = true
                    _imageSize.value = imageManager.calculateImageSize(
                        imageData = ImageData(
                            image = it.image,
                            imageInfo = it.imageInfo,
                            metadata = it.metadata
                        )
                    )
                    _isImageLoading.value = false
                }
            },
            onError = onError
        )
    }

    fun getImageManager(): ImageManager<Bitmap, ExifInterface> = imageManager

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.value = false
    }

}