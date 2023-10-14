package ru.tech.imageresizershrinker.presentation.filters_screen.viewModel


import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.domain.image.ImageManager
import ru.tech.imageresizershrinker.domain.model.ImageData
import ru.tech.imageresizershrinker.domain.model.ImageFormat
import ru.tech.imageresizershrinker.domain.model.ImageInfo
import ru.tech.imageresizershrinker.domain.saving.FileController
import ru.tech.imageresizershrinker.domain.saving.SaveResult
import ru.tech.imageresizershrinker.domain.saving.model.ImageSaveTarget
import ru.tech.imageresizershrinker.presentation.root.transformation.filter.FilterTransformation
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(
    private val fileController: FileController,
    private val imageManager: ImageManager<Bitmap, ExifInterface>
) : ViewModel() {

    private val _bitmapSize = mutableStateOf<Long?>(null)
    val bitmapSize by _bitmapSize

    private val _canSave = mutableStateOf(false)
    val canSave by _canSave

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

    private val _imageInfo = mutableStateOf(ImageInfo())
    val imageInfo by _imageInfo

    private val _filterList = mutableStateOf(listOf<FilterTransformation<*>>())
    val filterList by _filterList

    private val _needToApplyFilters = mutableStateOf(true)
    val needToApplyFilters by _needToApplyFilters

    fun setMime(imageFormat: ImageFormat) {
        _imageInfo.value = _imageInfo.value.copy(imageFormat = imageFormat)
        updatePreview()
    }

    fun updateUris(uris: List<Uri>?) {
        _uris.value = null
        _uris.value = uris
        _selectedUri.value = uris?.firstOrNull()?.also(::setBitmap)
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
                            setBitmap(it)
                        }
                    } else {
                        uris?.getOrNull(index - 1)?.let {
                            _selectedUri.value = it
                            setBitmap(it)
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

    fun setKeepExif(boolean: Boolean) {
        _keepExif.value = boolean
    }

    private var savingJob: Job? = null

    fun saveBitmaps(
        onResult: (Int, String) -> Unit
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _isSaving.value = true
            var failed = 0
            _done.value = 0
            uris?.forEach { uri ->
                runCatching {
                    imageManager.getImageWithTransformations(uri.toString(), filterList)?.image
                }.getOrNull()?.let { bitmap ->
                    val localBitmap = bitmap

                    val result = fileController.save(
                        saveTarget = ImageSaveTarget<ExifInterface>(
                            imageInfo = imageInfo,
                            originalUri = uri.toString(),
                            sequenceNumber = _done.value + 1,
                            data = imageManager.compress(
                                ImageData(
                                    image = localBitmap,
                                    imageInfo = imageInfo.copy(
                                        width = localBitmap.width,
                                        height = localBitmap.height
                                    )
                                )
                            )
                        ), keepMetadata = keepExif
                    )
                    if (result is SaveResult.Error.MissingPermissions) {
                        return@withContext onResult(-1, "")
                    }
                } ?: {
                    failed += 1
                }
                _done.value += 1
            }
            onResult(failed, fileController.savingPath)
            _isSaving.value = false
        }
    }.also {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = it
    }

    fun setBitmap(uri: Uri) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _isImageLoading.value = true
                val req = imageManager.getImage(uri = uri.toString())
                val tempBitmap = req?.image
                val size = tempBitmap?.let { it.width to it.height }
                _bitmap.value = imageManager.scaleUntilCanShow(tempBitmap)
                _imageInfo.value = _imageInfo.value.copy(
                    width = size?.first ?: 0,
                    height = size?.second ?: 0,
                    imageFormat = req?.imageInfo?.imageFormat ?: ImageFormat.Default()
                )
                updatePreview()
                _selectedUri.value = uri
                _isImageLoading.value = false
            }
        }
    }


    private fun updateCanSave() {
        _canSave.value = _bitmap.value != null && _filterList.value.isNotEmpty()
    }

    private var filterJob: Job? = null

    fun <T : Any> updateFilter(
        value: T,
        index: Int,
        showError: (Throwable) -> Unit
    ) {
        val list = _filterList.value.toMutableList()
        kotlin.runCatching {
            list[index] = list[index].copy(value)
            _filterList.value = list
        }.exceptionOrNull()?.let {
            showError(it)
            list[index] = list[index].newInstance()
            _filterList.value = list
        }
        _needToApplyFilters.value = true
    }

    fun updateOrder(value: List<FilterTransformation<*>>) {
        _filterList.value = value
        filterJob?.cancel()
        _needToApplyFilters.value = true
    }

    fun addFilter(filter: FilterTransformation<*>) {
        _filterList.value = _filterList.value + filter
        updateCanSave()
        filterJob?.cancel()
        _needToApplyFilters.value = true
    }

    fun removeFilterAtIndex(index: Int) {
        _filterList.value = _filterList.value.toMutableList().apply {
            removeAt(index)
        }
        updateCanSave()
        filterJob?.cancel()
        _needToApplyFilters.value = true
    }

    fun canShow(): Boolean = bitmap?.let { imageManager.canShow(it) } ?: false

    fun shareBitmaps(onComplete: () -> Unit) {
        viewModelScope.launch {
            _isSaving.value = true
            imageManager.shareImages(
                uris = uris?.map { it.toString() } ?: emptyList(),
                imageLoader = { uri ->
                    imageManager.getImageWithTransformations(uri, filterList)
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
        }.also {
            _isSaving.value = false
            savingJob?.cancel()
            savingJob = it
        }
    }

    fun getImageManager(): ImageManager<Bitmap, ExifInterface> = imageManager

    fun setQuality(fl: Float) {
        _imageInfo.value = _imageInfo.value.copy(quality = fl)
        updatePreview()
    }

    fun updatePreview() {
        _bitmap.value?.let { bitmap ->
            filterJob?.cancel()
            filterJob = viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    delay(200L)
                    _isImageLoading.value = true
                    _previewBitmap.value = imageManager.createPreview(
                        image = bitmap,
                        imageInfo = imageInfo,
                        transformations = filterList,
                        onGetByteCount = { _bitmapSize.value = it.toLong() }
                    )
                    _isImageLoading.value = false
                    _needToApplyFilters.value = false
                }
            }
        }
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.value = false
    }

}