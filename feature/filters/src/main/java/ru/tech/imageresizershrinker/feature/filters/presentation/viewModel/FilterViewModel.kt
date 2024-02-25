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

package ru.tech.imageresizershrinker.feature.filters.presentation.viewModel


import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.transform.Transformation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.data.utils.toCoil
import ru.tech.imageresizershrinker.core.domain.image.ImageCompressor
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.image.ImagePreviewCreator
import ru.tech.imageresizershrinker.core.domain.image.ImageScaler
import ru.tech.imageresizershrinker.core.domain.image.ImageTransformer
import ru.tech.imageresizershrinker.core.domain.image.ShareProvider
import ru.tech.imageresizershrinker.core.domain.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.domain.model.Quality
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.core.domain.saving.SaveResult
import ru.tech.imageresizershrinker.core.domain.saving.model.ImageSaveTarget
import ru.tech.imageresizershrinker.core.filters.domain.FilterProvider
import ru.tech.imageresizershrinker.core.filters.presentation.model.UiFilter
import ru.tech.imageresizershrinker.core.ui.transformation.ImageInfoTransformation
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.utils.state.update
import ru.tech.imageresizershrinker.feature.filters.domain.FilterMaskApplier
import ru.tech.imageresizershrinker.feature.filters.presentation.components.BasicFilterState
import ru.tech.imageresizershrinker.feature.filters.presentation.components.MaskingFilterState
import ru.tech.imageresizershrinker.feature.filters.presentation.components.UiFilterMask
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class FilterViewModel @Inject constructor(
    private val fileController: FileController,
    private val imagePreviewCreator: ImagePreviewCreator<Bitmap>,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val filterMaskApplier: FilterMaskApplier<Bitmap, Path, Color>,
    private val imageGetter: ImageGetter<Bitmap, ExifInterface>,
    private val imageScaler: ImageScaler<Bitmap>,
    val filterProvider: FilterProvider<Bitmap>,
    val imageInfoTransformationFactory: ImageInfoTransformation.Factory,
    private val imageTransformer: ImageTransformer<Bitmap>,
    private val shareProvider: ShareProvider<Bitmap>
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
        _basicFilterState.update {
            it.copy(
                uris = uris,
                selectedUri = uris?.firstOrNull()?.also(::setBitmap)
            )
        }
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
        onResult: (List<SaveResult>, String) -> Unit
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _isSaving.value = true
            val results = mutableListOf<SaveResult>()
            _done.value = 0
            _left.value = _basicFilterState.value.uris?.size ?: 1
            _basicFilterState.value.uris?.forEach { uri ->
                runCatching {
                    imageGetter.getImageWithTransformations(
                        uri = uri.toString(),
                        transformations = _basicFilterState.value.filters.map {
                            filterProvider.filterToTransformation(it)
                        }
                    )?.image
                }.getOrNull()?.let { bitmap ->
                    val localBitmap = bitmap

                    results.add(
                        fileController.save(
                            saveTarget = ImageSaveTarget<ExifInterface>(
                                imageInfo = imageInfo,
                                originalUri = uri.toString(),
                                sequenceNumber = _done.value + 1,
                                data = imageCompressor.compressAndTransform(
                                    image = localBitmap,
                                    imageInfo = imageInfo.copy(
                                        width = localBitmap.width,
                                        height = localBitmap.height
                                    )
                                )
                            ), keepMetadata = keepExif
                        )
                    )
                } ?: results.add(
                    SaveResult.Error.Exception(Throwable())
                )

                _done.value += 1
            }
            onResult(results, fileController.savingPath)
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
                val req = imageGetter.getImage(uri = uri.toString())
                val tempBitmap = req?.image
                val size = tempBitmap?.let { it.width to it.height }
                _bitmap.value = imageScaler.scaleUntilCanShow(tempBitmap)
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
        updateCanSave()
        _needToApplyFilters.value = true
    }

    fun updateFiltersOrder(value: List<UiFilter<*>>) {
        _basicFilterState.update {
            it.copy(filters = value)
        }
        filterJob?.cancel()
        updateCanSave()
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

    fun canShow(): Boolean = bitmap?.let { imagePreviewCreator.canShow(it) } ?: false

    fun performSharing(onComplete: () -> Unit) {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = viewModelScope.launch {
            _isSaving.value = true
            _done.value = 0
            when (filterType) {
                is Screen.Filter.Type.Basic -> {
                    _left.value = _basicFilterState.value.uris?.size ?: 1
                    shareProvider.shareImages(
                        uris = _basicFilterState.value.uris?.map { it.toString() } ?: emptyList(),
                        imageLoader = { uri ->
                            imageGetter.getImageWithTransformations(
                                uri = uri,
                                transformations = _basicFilterState.value.filters.map {
                                    filterProvider.filterToTransformation(it)
                                }
                            )?.let {
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

                is Screen.Filter.Type.Masking -> {
                    _left.value = maskingFilterState.masks.size
                    maskingFilterState.uri?.toString()?.let {
                        imageGetter.getImage(uri = it)
                    }?.let {
                        maskingFilterState.masks.fold<UiFilterMask, Bitmap?>(
                            initial = it.image,
                            operation = { bmp, mask ->
                                bmp?.let {
                                    filterMaskApplier.filterByMask(
                                        filterMask = mask, image = bmp
                                    )
                                }?.also { _done.value++ }
                            }
                        )?.let { bitmap ->
                            shareProvider.shareImage(
                                image = bitmap,
                                imageInfo = imageInfo.copy(
                                    width = bitmap.width,
                                    height = bitmap.height
                                ),
                                onComplete = {
                                    _isSaving.value = true
                                    onComplete()
                                }
                            )
                        }
                    }
                }

                null -> Unit
            }
        }
    }

    fun setQuality(quality: Quality) {
        _imageInfo.value = _imageInfo.value.copy(quality = quality)
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
                            _previewBitmap.value = imagePreviewCreator.createPreview(
                                image = bitmap,
                                imageInfo = imageInfo,
                                transformations = _basicFilterState.value.filters.map {
                                    filterProvider.filterToTransformation(it)
                                },
                                onGetByteCount = { _bitmapSize.value = it.toLong() }
                            )
                        }

                        is Screen.Filter.Type.Masking -> {
                            _previewBitmap.value = filterMaskApplier.filterByMasks(
                                filterMasks = _maskingFilterState.value.masks,
                                image = bitmap
                            )?.let { bmp ->
                                imagePreviewCreator.createPreview(
                                    image = bmp,
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
            if (it !is Screen.Filter.Type.Masking) {
                Screen.Filter.Type.Masking(uri)
            } else it
        }
        uri?.let { setBitmap(it) }
        _maskingFilterState.value = MaskingFilterState(uri)
        _needToApplyFilters.value = true
        updateCanSave()
    }

    fun clearType() {
        _filterType.update { null }
        _basicFilterState.update { BasicFilterState() }
        _maskingFilterState.update { MaskingFilterState() }
        _bitmap.value = null
        _previewBitmap.value = null
        _imageInfo.update { ImageInfo() }
        updateCanSave()
    }

    fun saveMaskedBitmap(
        onComplete: (saveResult: SaveResult) -> Unit
    ) {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _isSaving.value = true
                _done.value = 0
                _left.value = maskingFilterState.masks.size
                maskingFilterState.uri?.toString()?.let {
                    imageGetter.getImage(uri = it)
                }?.let {
                    maskingFilterState.masks.fold<UiFilterMask, Bitmap?>(
                        initial = it.image,
                        operation = { bmp, mask ->
                            bmp?.let {
                                filterMaskApplier.filterByMask(
                                    filterMask = mask, image = bmp
                                )
                            }?.also { _done.value++ }
                        }
                    )?.let { localBitmap ->
                        onComplete(
                            fileController.save(
                                saveTarget = ImageSaveTarget<ExifInterface>(
                                    imageInfo = imageInfo.copy(
                                        width = localBitmap.width,
                                        height = localBitmap.height
                                    ),
                                    originalUri = maskingFilterState.uri.toString(),
                                    sequenceNumber = null,
                                    data = imageCompressor.compressAndTransform(
                                        image = localBitmap,
                                        imageInfo = imageInfo.copy(
                                            width = localBitmap.width,
                                            height = localBitmap.height
                                        )
                                    )
                                ), keepMetadata = keepExif
                            )
                        )
                    }
                }
                _isSaving.value = false
            }
        }
    }

    fun updateMasksOrder(uiFilterMasks: List<UiFilterMask>) {
        _maskingFilterState.update {
            it.copy(masks = uiFilterMasks)
        }
        _needToApplyFilters.value = true
        updateCanSave()
    }

    fun updateMask(
        value: UiFilterMask,
        index: Int,
        showError: (Throwable) -> Unit
    ) {
        runCatching {
            _maskingFilterState.update {
                it.copy(
                    masks = it.masks.toMutableList().apply {
                        this[index] = value
                    }
                )
            }
            _needToApplyFilters.value = true
            updateCanSave()
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
        updateCanSave()
    }

    fun addMask(value: UiFilterMask) {
        _maskingFilterState.update {
            it.copy(
                masks = it.masks + value
            )
        }
        _needToApplyFilters.value = true
        updateCanSave()
    }

    suspend fun filter(
        bitmap: Bitmap,
        filters: List<UiFilter<*>>,
        size: IntegerSize
    ): Bitmap? = imageTransformer.transform(
        image = bitmap,
        transformations = filters.map { filterProvider.filterToTransformation(it) },
        size = size
    )

    fun filterToTransformation(
        uiFilter: UiFilter<*>
    ): Transformation = filterProvider.filterToTransformation(uiFilter).toCoil()

    fun cacheCurrentImage(onComplete: (Uri) -> Unit) {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = viewModelScope.launch {
            _isSaving.value = true
            when (filterType) {
                is Screen.Filter.Type.Basic -> {
                    imageGetter.getImageWithTransformations(
                        uri = _basicFilterState.value.selectedUri.toString(),
                        transformations = _basicFilterState.value.filters.map {
                            filterProvider.filterToTransformation(it)
                        }
                    )?.let { (image, imageInfo) ->
                        shareProvider.cacheImage(
                            image = image,
                            imageInfo = imageInfo,
                            name = Random.nextInt().toString()
                        )?.let {
                            onComplete(it.toUri())
                        }
                    }
                }

                is Screen.Filter.Type.Masking -> {
                    _left.value = maskingFilterState.masks.size
                    maskingFilterState.uri?.toString()?.let {
                        imageGetter.getImage(uri = it)
                    }?.let { imageData ->
                        maskingFilterState.masks.fold<UiFilterMask, Bitmap?>(
                            initial = imageData.image,
                            operation = { bmp, mask ->
                                bmp?.let {
                                    filterMaskApplier.filterByMask(
                                        filterMask = mask,
                                        image = bmp
                                    )
                                }?.also { _done.value++ }
                            }
                        )?.let { bitmap ->
                            shareProvider.cacheImage(
                                image = bitmap,
                                imageInfo = imageInfo.copy(
                                    width = bitmap.width,
                                    height = bitmap.height
                                ),
                                name = Random.nextInt().toString()
                            )?.let {
                                onComplete(it.toUri())
                            }
                        }
                    }
                }

                null -> Unit
            }
            _isSaving.value = false
        }
    }

}