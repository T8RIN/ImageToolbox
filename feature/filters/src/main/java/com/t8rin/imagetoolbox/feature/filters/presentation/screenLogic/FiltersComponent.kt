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

package com.t8rin.imagetoolbox.feature.filters.presentation.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.core.net.toUri
import coil3.transform.Transformation
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImagePreviewCreator
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.saving.model.onSuccess
import com.t8rin.imagetoolbox.core.domain.saving.updateProgress
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.leftFrom
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.rightFrom
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.filters.domain.FilterProvider
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.filters.presentation.widget.FilterTemplateCreationSheetComponent
import com.t8rin.imagetoolbox.core.filters.presentation.widget.addFilters.AddFiltersSheetComponent
import com.t8rin.imagetoolbox.core.ui.transformation.ImageInfoTransformation
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.feature.filters.domain.FilterMaskApplier
import com.t8rin.imagetoolbox.feature.filters.presentation.components.BasicFilterState
import com.t8rin.imagetoolbox.feature.filters.presentation.components.MaskingFilterState
import com.t8rin.imagetoolbox.feature.filters.presentation.components.UiFilterMask
import com.t8rin.imagetoolbox.feature.filters.presentation.components.addEditMaskSheet.AddMaskSheetComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

class FiltersComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialType: Screen.Filter.Type?,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val fileController: FileController,
    private val imagePreviewCreator: ImagePreviewCreator<Bitmap>,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val filterMaskApplier: FilterMaskApplier<Bitmap, Path, Color>,
    private val imageGetter: ImageGetter<Bitmap>,
    private val imageScaler: ImageScaler<Bitmap>,
    private val filterProvider: FilterProvider<Bitmap>,
    private val imageInfoTransformationFactory: ImageInfoTransformation.Factory,
    private val shareProvider: ImageShareProvider<Bitmap>,
    dispatchersHolder: DispatchersHolder,
    addFiltersSheetComponentFactory: AddFiltersSheetComponent.Factory,
    filterTemplateCreationSheetComponent: FilterTemplateCreationSheetComponent.Factory,
    addMaskSheetComponentFactory: AddMaskSheetComponent.Factory,
) : BaseComponent(dispatchersHolder, componentContext) {

    init {
        debounce {
            initialType?.let(::setType)
        }
    }

    val addFiltersSheetComponent: AddFiltersSheetComponent = addFiltersSheetComponentFactory(
        componentContext = componentContext.childContext(
            key = "addFiltersFilters"
        )
    )

    val filterTemplateCreationSheetComponent: FilterTemplateCreationSheetComponent =
        filterTemplateCreationSheetComponent(
            componentContext = componentContext.childContext(
                key = "filterTemplateCreationSheetComponentFilters"
            )
        )

    val addMaskSheetComponent: AddMaskSheetComponent = addMaskSheetComponentFactory(
        componentContext = componentContext.childContext(
            key = "addMaskSheetComponentFactoryFilters"
        )
    )

    fun getFiltersTransformation(): List<Transformation> = listOf(
        imageInfoTransformationFactory(
            imageInfo = imageInfo,
            transformations = basicFilterState.filters.map(
                filterProvider::filterToTransformation
            )
        )
    )

    private val _isPickImageFromUrisSheetVisible = mutableStateOf(false)
    val isPickImageFromUrisSheetVisible by _isPickImageFromUrisSheetVisible

    fun showPickImageFromUrisSheet() {
        _isPickImageFromUrisSheetVisible.update { true }
    }

    fun hidePickImageFromUrisSheet() {
        _isPickImageFromUrisSheetVisible.update { false }
    }

    private val _isAddFiltersSheetVisible = mutableStateOf(false)
    val isAddFiltersSheetVisible by _isAddFiltersSheetVisible

    fun showAddFiltersSheet() {
        _isAddFiltersSheetVisible.update { true }
    }

    fun hideAddFiltersSheet() {
        _isAddFiltersSheetVisible.update { false }
    }

    private val _isReorderSheetVisible = mutableStateOf(false)
    val isReorderSheetVisible by _isReorderSheetVisible

    fun showReorderSheet() {
        _isReorderSheetVisible.update { true }
    }

    fun hideReorderSheet() {
        _isReorderSheetVisible.update { false }
    }

    private val _isSelectionFilterPickerVisible = mutableStateOf(false)
    val isSelectionFilterPickerVisible by _isSelectionFilterPickerVisible

    fun showSelectionFilterPicker() {
        _isSelectionFilterPickerVisible.update { true }
    }

    fun hideSelectionFilterPicker() {
        _isSelectionFilterPickerVisible.update { false }
    }

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

    private val _filterType: MutableState<Screen.Filter.Type?> = mutableStateOf(null)
    val filterType: Screen.Filter.Type? by _filterType

    fun setImageFormat(imageFormat: ImageFormat) {
        _imageInfo.value = _imageInfo.value.copy(imageFormat = imageFormat)
        updatePreview()
        registerChanges()
    }

    fun setBasicFilter(uris: List<Uri>?) {
        _filterType.update {
            it as? Screen.Filter.Type.Basic ?: Screen.Filter.Type.Basic(uris)
        }
        _basicFilterState.update {
            it.copy(
                uris = uris,
                selectedUri = uris?.firstOrNull()?.also(::updateSelectedUri)
            )
        }
    }

    fun updateUrisSilently(removedUri: Uri) {
        componentScope.launch {
            val state = _basicFilterState.value
            if (state.selectedUri == removedUri) {
                val index = state.uris?.indexOf(removedUri) ?: -1
                if (index == 0) {
                    state.uris?.getOrNull(1)?.let {
                        _basicFilterState.update { f ->
                            f.copy(selectedUri = it)
                        }
                        updateSelectedUri(it)
                    }
                } else {
                    state.uris?.getOrNull(index - 1)?.let {
                        _basicFilterState.update { f ->
                            f.copy(selectedUri = it)
                        }
                        updateSelectedUri(it)
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

    fun setKeepExif(boolean: Boolean) {
        _keepExif.value = boolean
        registerChanges()
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
            _left.value = _basicFilterState.value.uris?.size ?: 1
            _basicFilterState.value.uris?.forEach { uri ->
                runSuspendCatching {
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
                            saveTarget = ImageSaveTarget(
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
                            ),
                            keepOriginalMetadata = keepExif,
                            oneTimeSaveLocationUri = oneTimeSaveLocationUri
                        )
                    )
                } ?: results.add(
                    SaveResult.Error.Exception(Throwable())
                )

                _done.value += 1

                updateProgress(
                    done = done,
                    total = left
                )
            }
            onResult(results.onSuccess(::registerSave))
            _isSaving.value = false
        }
    }

    fun updateSelectedUri(
        uri: Uri,
        onFailure: (Throwable) -> Unit = {}
    ) {
        runCatching {
            componentScope.launch {
                _isImageLoading.update { true }
                val req = imageGetter.getImage(uri = uri.toString())
                val tempBitmap = req?.image
                val size = tempBitmap?.let { it.width to it.height }
                _bitmap.update {
                    imageScaler.scaleUntilCanShow(tempBitmap)
                }
                _imageInfo.update {
                    it.copy(
                        width = size?.first ?: 0,
                        height = size?.second ?: 0,
                        imageFormat = req?.imageInfo?.imageFormat ?: ImageFormat.Default
                    )
                }
                updatePreview()
                _basicFilterState.update {
                    it.copy(selectedUri = uri)
                }
                _isImageLoading.update { false }
            }
        }.onFailure(onFailure)
    }

    private fun updateCanSave() {
        _canSave.value =
            _bitmap.value != null && ((_filterType.value is Screen.Filter.Type.Basic && _basicFilterState.value.filters.isNotEmpty()) || (_filterType.value is Screen.Filter.Type.Masking && _maskingFilterState.value.masks.isNotEmpty()))
        registerChanges()
    }

    private var filterJob: Job? by smartJob()

    fun <T : Any> updateFilter(
        value: T,
        index: Int,
        onFailure: (Throwable) -> Unit
    ) {
        val list = _basicFilterState.value.filters.toMutableList()
        runCatching {
            list[index] = list[index].copy(value)
            _basicFilterState.update {
                it.copy(filters = list)
            }
        }.onFailure { throwable ->
            onFailure(throwable)
            list[index] = list[index].newInstance()
            _basicFilterState.update {
                it.copy(filters = list)
            }
        }
        updateCanSave()
        updatePreview()
    }

    fun updateFiltersOrder(value: List<UiFilter<*>>) {
        _basicFilterState.update {
            it.copy(filters = value)
        }
        filterJob = null
        updateCanSave()
        updatePreview()
    }

    fun addFilterNewInstance(filter: UiFilter<*>) {
        addFilter(filter.newInstance())
    }

    fun addFilter(filter: UiFilter<*>) {
        _basicFilterState.update {
            it.copy(filters = it.filters + filter)
        }
        updateCanSave()
        filterJob = null
        updatePreview()
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
        filterJob = null
        updatePreview()
    }

    fun canShow(): Boolean = bitmap?.let { imagePreviewCreator.canShow(it) } == true

    fun performSharing(onComplete: () -> Unit) {
        savingJob = trackProgress {
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

                            updateProgress(
                                done = done,
                                total = left
                            )
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
                                }?.also {
                                    _done.value++
                                    updateProgress(
                                        done = done,
                                        total = left
                                    )
                                }
                            }
                        )?.let { bitmap ->
                            shareProvider.shareImage(
                                image = bitmap,
                                imageInfo = imageInfo.copy(
                                    width = bitmap.width,
                                    height = bitmap.height
                                ),
                                onComplete = {
                                    _isSaving.value = false
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
        _imageInfo.update(onValueChanged = ::updatePreview) {
            it.copy(quality = quality)
        }
    }

    private fun updatePreview() {
        _bitmap.value?.let { bitmap ->
            filterJob = componentScope.launch {
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
                            onGetByteCount = { size ->
                                _imageInfo.update { it.copy(sizeInBytes = size) }
                            }
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
                                onGetByteCount = { size ->
                                    _imageInfo.update { it.copy(sizeInBytes = size) }
                                }
                            )
                        }
                    }

                    null -> Unit
                }
                _isImageLoading.value = false
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
            it as? Screen.Filter.Type.Masking ?: Screen.Filter.Type.Masking(uri)
        }
        uri?.let { updateSelectedUri(it) }
        _maskingFilterState.value = MaskingFilterState(uri)
        updatePreview()
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
        registerChangesCleared()
    }

    fun saveMaskedBitmap(
        oneTimeSaveLocationUri: String?,
        onComplete: (saveResult: SaveResult) -> Unit
    ) {
        savingJob = trackProgress {
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
                        }?.also {
                            _done.value++
                            updateProgress(
                                done = done,
                                total = left
                            )
                        }
                    }
                )?.let { localBitmap ->
                    onComplete(
                        fileController.save(
                            saveTarget = ImageSaveTarget(
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
                            ),
                            keepOriginalMetadata = keepExif,
                            oneTimeSaveLocationUri = oneTimeSaveLocationUri
                        ).onSuccess(::registerSave)
                    )
                }
            }
            _isSaving.value = false
        }
    }

    fun updateMasksOrder(uiFilterMasks: List<UiFilterMask>) {
        _maskingFilterState.update {
            it.copy(masks = uiFilterMasks)
        }
        updatePreview()
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
            updatePreview()
            updateCanSave()
        }.onFailure(showError)
    }

    fun removeMaskAtIndex(index: Int) {
        _maskingFilterState.update {
            it.copy(
                masks = it.masks.toMutableList().apply {
                    removeAt(index)
                }
            )
        }
        updatePreview()
        updateCanSave()
    }

    fun addMask(value: UiFilterMask) {
        _maskingFilterState.update {
            it.copy(
                masks = it.masks + value
            )
        }
        updatePreview()
        updateCanSave()
    }

    fun cacheCurrentImage(onComplete: (Uri) -> Unit) {
        savingJob = trackProgress {
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
                            imageInfo = imageInfo
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
                                }?.also {
                                    _done.value++
                                    updateProgress(
                                        done = done,
                                        total = left
                                    )
                                }
                            }
                        )?.let { bitmap ->
                            shareProvider.cacheImage(
                                image = bitmap,
                                imageInfo = imageInfo.copy(
                                    width = bitmap.width,
                                    height = bitmap.height
                                )
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

    fun cacheImages(
        onComplete: (List<Uri>) -> Unit
    ) {
        savingJob = trackProgress {
            _isSaving.value = true
            val list = mutableListOf<Uri>()
            when (filterType) {
                is Screen.Filter.Type.Basic -> {
                    _done.value = 0
                    _left.value = basicFilterState.uris?.size ?: 0
                    basicFilterState.uris?.forEach { uri ->
                        imageGetter.getImageWithTransformations(
                            uri = uri.toString(),
                            transformations = _basicFilterState.value.filters.map {
                                filterProvider.filterToTransformation(it)
                            }
                        )?.let { (image, imageInfo) ->
                            shareProvider.cacheImage(
                                image = image,
                                imageInfo = imageInfo
                            )?.let {
                                list.add(it.toUri())
                            }
                        }
                        _done.value++
                        updateProgress(
                            done = done,
                            total = left
                        )
                    }
                }

                is Screen.Filter.Type.Masking -> {
                    _done.value = 0
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
                                }?.also {
                                    _done.value++
                                    updateProgress(
                                        done = done,
                                        total = left
                                    )
                                }
                            }
                        )?.let { bitmap ->
                            shareProvider.cacheImage(
                                image = bitmap,
                                imageInfo = imageInfo.copy(
                                    width = bitmap.width,
                                    height = bitmap.height
                                )
                            )?.let {
                                list.add(it.toUri())
                            }
                        }
                    }
                }

                null -> Unit
            }
            onComplete(list)
            _isSaving.value = false
        }
    }

    fun selectLeftUri() {
        basicFilterState.uris
            ?.indexOf(basicFilterState.selectedUri ?: Uri.EMPTY)
            ?.takeIf { it >= 0 }
            ?.let {
                basicFilterState.uris?.leftFrom(it)
            }
            ?.let(::updateSelectedUri)
    }

    fun selectRightUri() {
        basicFilterState.uris
            ?.indexOf(basicFilterState.selectedUri ?: Uri.EMPTY)
            ?.takeIf { it >= 0 }
            ?.let {
                basicFilterState.uris?.rightFrom(it)
            }
            ?.let(::updateSelectedUri)
    }

    fun getFormatForFilenameSelection(): ImageFormat? = when {
        basicFilterState.uris?.size == 1 -> imageInfo.imageFormat
        maskingFilterState.uri != null -> imageInfo.imageFormat
        else -> null
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialType: Screen.Filter.Type?,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit,
        ): FiltersComponent
    }

}