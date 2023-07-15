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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.domain.image.ImageManager
import ru.tech.imageresizershrinker.domain.model.ImageFormat
import ru.tech.imageresizershrinker.domain.model.ImageInfo
import ru.tech.imageresizershrinker.domain.model.ResizeType
import ru.tech.imageresizershrinker.domain.saving.FileController
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

    private val _isLoading: MutableState<Boolean> = mutableStateOf(false)
    val isLoading: Boolean by _isLoading

    private val _previewBitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val previewBitmap: Bitmap? by _previewBitmap

    private val _done: MutableState<Int> = mutableIntStateOf(0)
    val done by _done

    private val _selectedUri: MutableState<Uri?> = mutableStateOf(null)
    val selectedUri by _selectedUri

    private val _imageFormat = mutableStateOf(ImageFormat.Default())
    val imageFormat by _imageFormat

    private val _filterList = mutableStateOf(listOf<FilterTransformation<*>>())
    val filterList by _filterList

    private val _needToApplyFilters = mutableStateOf(true)
    val needToApplyFilters by _needToApplyFilters

    fun setMime(imageFormat: ImageFormat) {
        _imageFormat.value = imageFormat

        calcSize(
            delay = 5,
            onStart = { _isLoading.value = true },
            onFinish = { _isLoading.value = false }
        )
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
                            _bitmap.value = imageManager.getImageWithTransformations(
                                uri = it.toString(),
                                transformations = filterList,
                                originalSize = false
                            )
                        }
                    } else {
                        uris?.getOrNull(index - 1)?.let {
                            _selectedUri.value = it
                            _bitmap.value = imageManager.getImageWithTransformations(
                                uri = it.toString(),
                                transformations = filterList,
                                originalSize = false
                            )
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

    fun updateBitmap(bitmap: Bitmap?, preview: Bitmap? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _bitmap.value = imageManager.scaleUntilCanShow(bitmap)?.upscale()
            _previewBitmap.value = preview ?: _bitmap.value
            _bitmap.value?.takeIf { it.height > 0 && it.width > 0 }?.let {
                if (_previewBitmap.value?.width != it.width) {
                    _previewBitmap.value = _previewBitmap.value?.let { it1 ->
                        imageManager.resize(
                            image = it1,
                            width = it.width,
                            height = it.height,
                            resizeType = ResizeType.Flexible
                        )
                    } ?: _previewBitmap.value
                }
            }
            calcSize()
            _isLoading.value = false
        }
    }

    private var sizeJob: Job? = null
    private fun calcSize(delay: Long = 250, onStart: () -> Unit = {}, onFinish: () -> Unit = {}) {
        sizeJob?.cancel()
        sizeJob = viewModelScope.launch {
            kotlinx.coroutines.delay(delay)
            onStart()
            _bitmapSize.value =
                _previewBitmap.value?.let {
                    imageManager.calculateImageSize(
                        image = it,
                        ImageInfo(imageFormat = imageFormat, width = it.width, height = it.height)
                    )
                }
            onFinish()
        }
    }

    fun setKeepExif(boolean: Boolean) {
        _keepExif.value = boolean
    }

    fun saveBitmaps(
        onResult: (Int, String) -> Unit
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            var failed = 0
            if (!fileController.isExternalStorageWritable()) {
                onResult(-1, "")
                fileController.requestReadWritePermissions()
            } else {
                _done.value = 0
                uris?.forEach { uri ->
                    runCatching {
                        imageManager.getImageWithTransformations(uri.toString(), filterList)
                    }.getOrNull()?.let { bitmap ->
                        val localBitmap = bitmap

                        fileController.save(
                            saveTarget = ImageSaveTarget(
                                imageInfo = ImageInfo(
                                    imageFormat = imageFormat,
                                    width = localBitmap.width,
                                    height = localBitmap.height
                                ),
                                originalUri = uri.toString(),
                                sequenceNumber = _done.value + 1,
                                data = imageManager.compress(
                                    image = localBitmap,
                                    imageInfo = ImageInfo(
                                        imageFormat = imageFormat,
                                        width = localBitmap.width,
                                        height = localBitmap.height
                                    )
                                )
                            ), keepMetadata = keepExif
                        )
                    } ?: {
                        failed += 1
                    }
                    _done.value += 1
                }
                onResult(failed, fileController.savingPath)
            }
        }
    }

    fun setBitmap(uri: Uri) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _isLoading.value = true
                updateBitmap(
                    imageManager.getImage(uri = uri.toString(), originalSize = false),
                    imageManager.getImageWithTransformations(
                        uri = uri.toString(),
                        transformations = filterList,
                        originalSize = false
                    )
                )
                _selectedUri.value = uri
                _isLoading.value = false
            }
        }
    }

    private fun updateCanSave() {
        _canSave.value = _bitmap.value != null && _filterList.value.isNotEmpty()
    }

    fun addFilter(filter: FilterTransformation<*>) {
        _filterList.value = _filterList.value + filter
        updateCanSave()
        _needToApplyFilters.value = true
    }

    fun removeFilterAtIndex(index: Int) {
        _filterList.value = _filterList.value.toMutableList().apply {
            removeAt(index)
        }
        updateCanSave()
        _needToApplyFilters.value = true
    }

    private var filterJob: Job? = null
    fun setFilteredPreview(bitmap: Bitmap) {
        filterJob?.cancel()
        filterJob = viewModelScope.launch {
            _isLoading.value = true
            updateBitmap(
                _bitmap.value,
                imageManager.transform(
                    image = bitmap,
                    transformations = filterList,
                    originalSize = false
                ) ?: _previewBitmap.value
            )
            _isLoading.value = false
            _needToApplyFilters.value = false
        }
    }

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
        _needToApplyFilters.value = true
    }

    private suspend fun Bitmap.upscale(): Bitmap {
        return if (this.width * this.height < 2000 * 2000) {
            imageManager.resize(this, 2000, 2000, ResizeType.Flexible)
        } else this
    }

    fun decodeBitmapFromUri(uri: Uri, onError: (Throwable) -> Unit) {
        viewModelScope.launch {
            imageManager.getImageAsync(
                uri = uri.toString(),
                originalSize = true,
                onGetMimeType = ::setMime,
                onGetMetadata = {},
                onGetImage = {
                    uris?.firstOrNull()?.let { uri ->
                        setBitmap(uri)
                    }
                },
                onError = onError
            )
        }
    }

    fun canShow(): Boolean = bitmap?.let { imageManager.canShow(it) } ?: false

    fun shareBitmaps(onComplete: () -> Unit) {
        viewModelScope.launch {
            imageManager.shareImages(
                uris = uris?.map { it.toString() } ?: emptyList(),
                imageLoader = { uri ->
                    imageManager.getImageWithTransformations(uri, filterList)
                        ?.let {
                            it to ImageInfo(
                                imageFormat = imageFormat,
                                width = it.width,
                                height = it.height
                            )
                        }
                },
                onProgressChange = {
                    if (it == -1) {
                        onComplete()
                        _done.value = 0
                    } else {
                        _done.value = it
                    }
                }
            )
        }
    }

    fun getImageManager(): ImageManager<Bitmap, ExifInterface> = imageManager

}