package ru.tech.imageresizershrinker.presentation.filters_screen.viewModel


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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.domain.image.ImageManager
import ru.tech.imageresizershrinker.domain.image.filters.FilterMaskApplier
import ru.tech.imageresizershrinker.domain.model.ImageData
import ru.tech.imageresizershrinker.domain.model.ImageFormat
import ru.tech.imageresizershrinker.domain.model.ImageInfo
import ru.tech.imageresizershrinker.domain.saving.FileController
import ru.tech.imageresizershrinker.domain.saving.SaveResult
import ru.tech.imageresizershrinker.domain.saving.model.ImageSaveTarget
import ru.tech.imageresizershrinker.presentation.filters_screen.components.BasicFilterState
import ru.tech.imageresizershrinker.presentation.filters_screen.components.MaskingFilterState
import ru.tech.imageresizershrinker.presentation.filters_screen.components.UiFilterMask
import ru.tech.imageresizershrinker.presentation.root.transformation.filter.UiFilter
import ru.tech.imageresizershrinker.presentation.root.utils.navigation.Screen
import ru.tech.imageresizershrinker.presentation.root.utils.state.update
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(
    private val fileController: FileController,
    private val imageManager: ImageManager<Bitmap, ExifInterface>,
    private val filterMaskApplier: FilterMaskApplier<Bitmap, Path, Color>
) : ViewModel() {

    private val _bitmapSize = mutableStateOf<Long?>(null)
    val bitmapSize by _bitmapSize

    private val _canSave = mutableStateOf(false)
    val canSave by _canSave

    private val _basicFilterState: MutableState<BasicFilterState> =
        mutableStateOf(BasicFilterState())
    val basicFilterState by _basicFilterState

    private val _maskingFilterState: MutableState<MaskingFilterState> =
        mutableStateOf(MaskingFilterState())
    val maskingFilterState by _maskingFilterState

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

    private val _left: MutableState<Int> = mutableIntStateOf(1)
    val left by _left

    private val _imageInfo = mutableStateOf(ImageInfo())
    val imageInfo by _imageInfo

    private val _needToApplyFilters = mutableStateOf(true)
    val needToApplyFilters by _needToApplyFilters

    private val _filterType: MutableState<Screen.Filter.Type?> = mutableStateOf(null)
    val filterType: Screen.Filter.Type? by _filterType

    fun setImageFormat(imageFormat: ImageFormat) {
        _imageInfo.value = _imageInfo.value.copy(imageFormat = imageFormat)
        updatePreview()
    }

    fun setBasicFilter(uris: List<Uri>?) {
        _filterType.update {
            if (it !is Screen.Filter.Type.Basic) {
                Screen.Filter.Type.Basic(uris)
            } else it
        }
        _basicFilterState.value = BasicFilterState(
            uris = uris,
            selectedUri = uris?.firstOrNull()?.also(::setBitmap)
        )
    }

    fun updateUrisSilently(removedUri: Uri) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val state = _basicFilterState.value
                if (state.selectedUri == removedUri) {
                    val index = state.uris?.indexOf(removedUri) ?: -1
                    if (index == 0) {
                        state.uris?.getOrNull(1)?.let {
                            _basicFilterState.update { f ->
                                f.copy(selectedUri = it)
                            }
                            setBitmap(it)
                        }
                    } else {
                        state.uris?.getOrNull(index - 1)?.let {
                            _basicFilterState.update { f ->
                                f.copy(selectedUri = it)
                            }
                            setBitmap(it)
                        }
                    }
                }
                _basicFilterState.update {
                    it.copy(
                        uris = it.uris?.toMutableList()?.apply {
                            remove(removedUri)
                        }
                    )
                }
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
            _left.value = _basicFilterState.value.uris?.size ?: 1
            _basicFilterState.value.uris?.forEach { uri ->
                runCatching {
                    imageManager.getImageWithFiltersApplied(
                        uri = uri.toString(),
                        filters = _basicFilterState.value.filters
                    )?.image
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
                _basicFilterState.update { f ->
                    f.copy(selectedUri = uri)
                }
                _isImageLoading.value = false
            }
        }
    }


    private fun updateCanSave() {
        _canSave.value =
            _bitmap.value != null && ((_filterType.value is Screen.Filter.Type.Basic && _basicFilterState.value.filters.isNotEmpty()) || (_filterType.value is Screen.Filter.Type.Masking && _maskingFilterState.value.masks.isNotEmpty()))
    }

    private var filterJob: Job? = null

    fun <T : Any> updateFilter(
        value: T,
        index: Int,
        showError: (Throwable) -> Unit
    ) {
        val list = _basicFilterState.value.filters.toMutableList()
        runCatching {
            list[index] = list[index].copy(value)
            _basicFilterState.update {
                it.copy(filters = list)
            }
        }.exceptionOrNull()?.let { throwable ->
            showError(throwable)
            list[index] = list[index].newInstance()
            _basicFilterState.update {
                it.copy(filters = list)
            }
        }
        _needToApplyFilters.value = true
    }

    fun updateFiltersOrder(value: List<UiFilter<*>>) {
        _basicFilterState.update {
            it.copy(filters = value)
        }
        filterJob?.cancel()
        _needToApplyFilters.value = true
    }

    fun addFilter(filter: UiFilter<*>) {
        _basicFilterState.update {
            it.copy(filters = it.filters + filter)
        }
        updateCanSave()
        filterJob?.cancel()
        _needToApplyFilters.value = true
    }

    fun removeFilterAtIndex(index: Int) {
        _basicFilterState.update {
            it.copy(
                filters = it.filters.toMutableList().apply {
                    removeAt(index)
                }
            )
        }
        updateCanSave()
        filterJob?.cancel()
        _needToApplyFilters.value = true
    }

    fun canShow(): Boolean = bitmap?.let { imageManager.canShow(it) } ?: false

    fun performSharing(onComplete: () -> Unit) {
        viewModelScope.launch {
            _isSaving.value = true
            _done.value = 0
            _left.value = _basicFilterState.value.uris?.size ?: 1
            imageManager.shareImages(
                uris = _basicFilterState.value.uris?.map { it.toString() } ?: emptyList(),
                imageLoader = { uri ->
                    imageManager.getImageWithFiltersApplied(uri, _basicFilterState.value.filters)
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
                    when (filterType) {
                        is Screen.Filter.Type.Basic -> {
                            _previewBitmap.value = imageManager.createFilteredPreview(
                                image = bitmap,
                                imageInfo = imageInfo,
                                filters = _basicFilterState.value.filters,
                                onGetByteCount = { _bitmapSize.value = it.toLong() }
                            )
                        }

                        is Screen.Filter.Type.Masking -> {
                            _previewBitmap.value = filterMaskApplier.filterByMasks(
                                filterMasks = _maskingFilterState.value.masks,
                                image = bitmap
                            )?.let {
                                imageManager.createPreview(
                                    image = it,
                                    imageInfo = imageInfo,
                                    onGetByteCount = { _bitmapSize.value = it.toLong() }
                                )
                            }
                        }

                        null -> Unit
                    }
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

    fun setType(type: Screen.Filter.Type) {
        when (type) {
            is Screen.Filter.Type.Basic -> setBasicFilter(type.uris)
            is Screen.Filter.Type.Masking -> setMaskFilter(type.uri)
        }
    }

    fun setMaskFilter(uri: Uri?) {
        _filterType.update {
            if (it !is Screen.Filter.Type.Basic) {
                Screen.Filter.Type.Masking(uri)
            } else it
        }
        uri?.let { setBitmap(it) }
        _maskingFilterState.value = MaskingFilterState(uri)
        _needToApplyFilters.value = true
    }

    fun clearType() {
        _filterType.update { null }
        _basicFilterState.update { BasicFilterState() }
        _maskingFilterState.update { MaskingFilterState() }
        _bitmap.value = null
        _previewBitmap.value = null
        _imageInfo.update { ImageInfo() }
    }

    fun saveMaskedBitmap() {
        TODO("Not yet implemented")
    }

    fun updateMasksOrder(uiFilterMasks: List<UiFilterMask>) {
        _maskingFilterState.update {
            it.copy(masks = uiFilterMasks)
        }
        _needToApplyFilters.value = true
    }

    fun updateMask(value: UiFilterMask, index: Int, showError: (Throwable) -> Unit) {
        runCatching {
            _maskingFilterState.update {
                it.copy(
                    masks = it.masks.toMutableList().apply {
                        this[index] = value
                    }
                )
            }
            _needToApplyFilters.value = true
        }.exceptionOrNull()?.let(showError)
    }

    fun removeMaskAtIndex(index: Int) {
        _maskingFilterState.update {
            it.copy(
                masks = it.masks.toMutableList().apply {
                    removeAt(index)
                }
            )
        }
        _needToApplyFilters.value = true
    }

    fun addMask(value: UiFilterMask) {
        _maskingFilterState.update {
            it.copy(
                masks = it.masks + value
            )
        }
        _needToApplyFilters.value = true
    }

}