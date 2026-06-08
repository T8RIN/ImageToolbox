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
import android.graphics.BlurMaskFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
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
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.model.FileModel
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
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
import com.t8rin.imagetoolbox.core.filters.domain.model.params.SeamCarvingParams
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.hasSameState
import com.t8rin.imagetoolbox.core.filters.presentation.model.hasSameValue
import com.t8rin.imagetoolbox.core.filters.presentation.model.previewKey
import com.t8rin.imagetoolbox.core.filters.presentation.widget.FilterTemplateCreationSheetComponent
import com.t8rin.imagetoolbox.core.filters.presentation.widget.addFilters.AddFiltersSheetComponent
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.ui.transformation.ImageInfoTransformation
import com.t8rin.imagetoolbox.core.ui.utils.BaseHistoryComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.helper.scaleToFitCanvas
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.feature.draw.presentation.components.UiPathPaint
import com.t8rin.imagetoolbox.feature.filters.domain.FilterMaskApplier
import com.t8rin.imagetoolbox.feature.filters.presentation.components.BasicFilterState
import com.t8rin.imagetoolbox.feature.filters.presentation.components.BasicPreviewState
import com.t8rin.imagetoolbox.feature.filters.presentation.components.MaskPreviewState
import com.t8rin.imagetoolbox.feature.filters.presentation.components.MaskingFilterState
import com.t8rin.imagetoolbox.feature.filters.presentation.components.MaskingPreviewState
import com.t8rin.imagetoolbox.feature.filters.presentation.components.PreviewRequest
import com.t8rin.imagetoolbox.feature.filters.presentation.components.UiFilterMask
import com.t8rin.imagetoolbox.feature.filters.presentation.components.addEditMaskSheet.AddMaskSheetComponent
import com.t8rin.imagetoolbox.feature.filters.presentation.screenLogic.FiltersComponent.HistorySnapshot
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import android.graphics.Color as NativeColor
import android.graphics.Paint as NativePaint

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
    private val settingsManager: SettingsManager,
    dispatchersHolder: DispatchersHolder,
    addFiltersSheetComponentFactory: AddFiltersSheetComponent.Factory,
    filterTemplateCreationSheetComponent: FilterTemplateCreationSheetComponent.Factory,
    addMaskSheetComponentFactory: AddMaskSheetComponent.Factory,
) : BaseHistoryComponent<HistorySnapshot>(
    dispatchersHolder = dispatchersHolder,
    componentContext = componentContext
) {

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
        if (_imageInfo.value.imageFormat == imageFormat) return
        if (pendingHistoryMode != PendingHistoryMode.FormatChange) {
            finalizePendingHistoryTransaction()
        }
        beginPendingHistoryTransaction(
            mode = PendingHistoryMode.FormatChange,
            commitDelayMillis = formatHistoryTransactionDebounce
        )
        _imageInfo.value = _imageInfo.value.copy(
            imageFormat = imageFormat,
            quality = imageInfo.quality.coerceIn(imageFormat)
        )
        updatePreview()
        registerChanges()
        schedulePendingHistoryCommit()
    }

    fun setBasicFilter(uris: List<Uri>?) {
        clearHistory()
        registerChangesCleared()
        _filterType.update {
            it as? Screen.Filter.Type.Basic ?: Screen.Filter.Type.Basic(uris)
        }
        val selectedUri = uris?.firstOrNull()
        _basicFilterState.update {
            it.copy(
                uris = uris,
                selectedUri = selectedUri
            )
        }
        selectedUri?.let {
            updateSelectedUriInternal(
                uri = it,
                resetHistoryAfterLoad = true
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
        if (_keepExif.value == boolean) return
        finalizePendingHistoryTransaction()
        val beforeSnapshot = currentHistorySnapshot()
        _keepExif.value = boolean
        commitHistoryFrom(beforeSnapshot)
    }

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    fun saveBitmaps(
        oneTimeSaveLocationUri: String?
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
            parseSaveResults(results.onSuccess(::registerSave))
            _isSaving.value = false
        }
    }

    fun updateSelectedUri(
        uri: Uri,
        onFailure: (Throwable) -> Unit = {}
    ) = updateSelectedUriInternal(
        uri = uri,
        onFailure = onFailure
    )

    private fun updateSelectedUriInternal(
        uri: Uri,
        onFailure: (Throwable) -> Unit = {},
        resetHistoryAfterLoad: Boolean = false
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
                _basicFilterState.update {
                    it.copy(selectedUri = uri)
                }
                updatePreview()
                if (resetHistoryAfterLoad) {
                    resetHistory()
                    registerChangesCleared()
                }
                _isImageLoading.update { false }
            }
        }.onFailure(onFailure)
    }

    private fun updateCanSave() {
        _canSave.value = calculateCanSave()
        registerChanges()
    }

    private fun calculateCanSave(): Boolean =
        _bitmap.value != null &&
                (
                        _filterType.value is Screen.Filter.Type.Basic &&
                                _basicFilterState.value.filters.isNotEmpty() ||
                                _filterType.value is Screen.Filter.Type.Masking &&
                                _maskingFilterState.value.masks.isNotEmpty()
                        )

    private var filterJob: Job? by smartJob()

    private var lastPreviewRequest: PreviewRequest? = null

    fun <T : Any> updateFilter(
        value: T,
        index: Int
    ) {
        val list = _basicFilterState.value.filters.toMutableList()
        runCatching {
            val current = list[index]
            val previewRequest = currentPreviewRequest()

            if (current.hasSameValue(value) && previewRequest == lastPreviewRequest) {
                return@updateFilter
            }

            val new = current.copy(value)

            if (current.hasSameState(new) && previewRequest == lastPreviewRequest) {
                return@updateFilter
            }

            beginPendingHistoryTransaction()
            list[index] = new
            _basicFilterState.update {
                it.copy(filters = list)
            }
        }.onFailure { throwable ->
            AppToastHost.showFailureToast(throwable)
            beginPendingHistoryTransaction()
            list[index] = list[index].newInstance()
            _basicFilterState.update {
                it.copy(filters = list)
            }
        }
        updateCanSave()
        updatePreview()
        schedulePendingHistoryCommit()
    }

    fun updateSeamCarvingMask(
        filterIndex: Int,
        paths: List<UiPathPaint>,
        onComplete: () -> Unit
    ) {
        val bitmap = bitmap ?: return
        val filter = basicFilterState.filters.getOrNull(filterIndex) ?: return
        val params = filter.value as? SeamCarvingParams ?: return

        componentScope.launch {
            val uri = withContext(defaultDispatcher) {
                val mask = renderSeamCarvingMask(
                    paths = paths,
                    width = bitmap.width,
                    height = bitmap.height
                )
                shareProvider.cacheImage(
                    image = mask,
                    imageInfo = ImageInfo(
                        width = mask.width,
                        height = mask.height,
                        imageFormat = ImageFormat.Png.Lossless
                    ),
                    filename = "seam_carving_mask.png"
                )
            } ?: return@launch

            updateFilter(
                value = params.copy(maskFile = FileModel(uri)),
                index = filterIndex
            )
            onComplete()
        }
    }

    fun removeSeamCarvingMask(filterIndex: Int) {
        val filter = basicFilterState.filters.getOrNull(filterIndex) ?: return
        val params = filter.value as? SeamCarvingParams ?: return

        updateFilter(
            value = params.copy(
                maskFile = FileModel(""),
                useMaskAsRemoval = false,
            ),
            index = filterIndex
        )
    }

    private fun renderSeamCarvingMask(
        paths: List<UiPathPaint>,
        width: Int,
        height: Int
    ): Bitmap = createBitmap(width, height).applyCanvas {
        val canvasSize = IntegerSize(width, height)
        drawColor(NativeColor.BLACK)

        paths.forEach { pathPaint ->
            val path = pathPaint.path.scaleToFitCanvas(
                currentSize = canvasSize,
                oldSize = pathPaint.canvasSize
            ).asAndroidPath()
            val drawPathMode = pathPaint.drawPathMode

            drawPath(
                path,
                NativePaint(NativePaint.ANTI_ALIAS_FLAG).apply {
                    color = Color.White.toArgb()
                    style = if (!pathPaint.isErasing && drawPathMode.isFilled) {
                        NativePaint.Style.FILL
                    } else {
                        NativePaint.Style.STROKE
                    }
                    strokeWidth = pathPaint.strokeWidth.toPx(canvasSize)
                    strokeCap = if (drawPathMode.isSharpEdge && !pathPaint.isErasing) {
                        NativePaint.Cap.SQUARE
                    } else {
                        NativePaint.Cap.ROUND
                    }
                    strokeJoin = NativePaint.Join.ROUND

                    if (pathPaint.isErasing) {
                        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
                    }
                    if (pathPaint.brushSoftness.value > 0f) {
                        maskFilter = BlurMaskFilter(
                            pathPaint.brushSoftness.toPx(canvasSize),
                            BlurMaskFilter.Blur.NORMAL
                        )
                    }
                }
            )
        }
    }

    fun updateFiltersOrder(value: List<UiFilter<*>>) {
        finalizePendingHistoryTransaction()
        val beforeSnapshot = currentHistorySnapshot()
        _basicFilterState.update {
            it.copy(filters = value)
        }
        filterJob = null
        updateCanSave()
        updatePreview()
        commitHistoryFrom(beforeSnapshot)
    }

    fun addFilterNewInstance(filter: UiFilter<*>) {
        addFilter(filter.newInstance())
    }

    fun addFilter(filter: UiFilter<*>) {
        finalizePendingHistoryTransaction()
        val beforeSnapshot = currentHistorySnapshot()
        _basicFilterState.update {
            it.copy(filters = it.filters + filter)
        }
        updateCanSave()
        filterJob = null
        updatePreview()
        commitHistoryFrom(beforeSnapshot)
    }

    fun removeFilterAtIndex(index: Int) {
        finalizePendingHistoryTransaction()
        val beforeSnapshot = currentHistorySnapshot()
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
        commitHistoryFrom(beforeSnapshot)
    }

    fun canShow(): Boolean = bitmap?.let { imagePreviewCreator.canShow(it) } == true

    fun performSharing() {
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
                                AppToastHost.showConfetti()
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
                                    AppToastHost.showConfetti()
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
        val coercedQuality = quality.coerceIn(imageInfo.imageFormat)
        if (_imageInfo.value.quality == coercedQuality) return
        beginPendingHistoryTransaction()
        _imageInfo.update(onValueChanged = ::updatePreview) {
            it.copy(quality = coercedQuality)
        }
        registerChanges()
        schedulePendingHistoryCommit()
    }

    private fun updatePreview() {
        _bitmap.value?.let { bitmap ->
            val previewRequest = currentPreviewRequest(bitmap)
            if (_previewBitmap.value != null && previewRequest == lastPreviewRequest) return

            lastPreviewRequest = previewRequest
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
        clearHistory()
        registerChangesCleared()
        _filterType.update {
            it as? Screen.Filter.Type.Masking ?: Screen.Filter.Type.Masking(uri)
        }
        uri?.let {
            updateSelectedUriInternal(
                uri = it,
                resetHistoryAfterLoad = true
            )
        }
        _maskingFilterState.value = MaskingFilterState(uri)
        updatePreview()
        updateCanSave()
    }

    fun clearType() {
        clearHistory()
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
        oneTimeSaveLocationUri: String?
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
                    parseSaveResult(
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
        finalizePendingHistoryTransaction()
        val beforeSnapshot = currentHistorySnapshot()
        _maskingFilterState.update {
            it.copy(masks = uiFilterMasks)
        }
        updatePreview()
        updateCanSave()
        commitHistoryFrom(beforeSnapshot)
    }

    fun updateMask(
        value: UiFilterMask,
        index: Int
    ) {
        runCatching {
            beginPendingHistoryTransaction()
            _maskingFilterState.update {
                it.copy(
                    masks = it.masks.toMutableList().apply {
                        this[index] = value
                    }
                )
            }
            updatePreview()
            updateCanSave()
            schedulePendingHistoryCommit()
        }.onFailure {
            cancelPendingHistoryTransaction()
            AppToastHost.showFailureToast(it)
        }
    }

    fun removeMaskAtIndex(index: Int) {
        finalizePendingHistoryTransaction()
        val beforeSnapshot = currentHistorySnapshot()
        _maskingFilterState.update {
            it.copy(
                masks = it.masks.toMutableList().apply {
                    removeAt(index)
                }
            )
        }
        updatePreview()
        updateCanSave()
        commitHistoryFrom(beforeSnapshot)
    }

    fun addMask(value: UiFilterMask) {
        finalizePendingHistoryTransaction()
        val beforeSnapshot = currentHistorySnapshot()
        _maskingFilterState.update {
            it.copy(
                masks = it.masks + value
            )
        }
        updatePreview()
        updateCanSave()
        commitHistoryFrom(beforeSnapshot)
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

    private fun currentPreviewRequest(): PreviewRequest? =
        _bitmap.value?.let(::currentPreviewRequest)

    private fun currentPreviewRequest(bitmap: Bitmap): PreviewRequest = PreviewRequest(
        bitmapId = System.identityHashCode(bitmap),
        imageInfo = imageInfo.copy(sizeInBytes = 0),
        filterType = filterType?.let { it::class.simpleName },
        basicState = (filterType as? Screen.Filter.Type.Basic)?.let {
            BasicPreviewState(
                uris = basicFilterState.uris,
                selectedUri = basicFilterState.selectedUri,
                filters = basicFilterState.filters.map { filter ->
                    filter.previewKey()
                }
            )
        },
        maskingState = (filterType as? Screen.Filter.Type.Masking)?.let {
            MaskingPreviewState(
                uri = maskingFilterState.uri,
                masks = maskingFilterState.masks.map { mask ->
                    mask.previewKey()
                }
            )
        }
    )

    private fun UiFilterMask.previewKey(): MaskPreviewState = MaskPreviewState(
        filters = filters.map { filter ->
            filter.previewKey()
        },
        maskPaints = maskPaints,
        isInverseFillType = isInverseFillType
    )

    override fun currentHistorySnapshot(): HistorySnapshot = HistorySnapshot(
        imageInfo = imageInfo.asHistoryImageInfo(),
        keepExif = keepExif,
        filterType = filterType,
        basicFilterState = basicFilterState,
        maskingFilterState = maskingFilterState,
        backgroundColorForNoAlphaFormats = settingsManager
            .settingsState
            .value
            .backgroundForNoAlphaImageFormats
    )

    override fun applyHistorySnapshot(snapshot: HistorySnapshot) {
        _imageInfo.update {
            it.copy(
                imageFormat = snapshot.imageInfo.imageFormat,
                quality = snapshot.imageInfo.quality
            )
        }
        val selectedUri = basicFilterState.selectedUri
        _keepExif.value = snapshot.keepExif
        _filterType.value = snapshot.filterType
        _basicFilterState.value = snapshot.basicFilterState.copy(
            selectedUri = selectedUri
                ?.takeIf { it in snapshot.basicFilterState.uris.orEmpty() }
                ?: snapshot.basicFilterState.selectedUri
        )
        _maskingFilterState.value = snapshot.maskingFilterState
        restoreBackgroundColorForNoAlphaFormats(
            settingsManager = settingsManager,
            backgroundColorForNoAlphaFormats = snapshot.backgroundColorForNoAlphaFormats
        )
        lastPreviewRequest = null
        filterJob = null
        _canSave.value = calculateCanSave()
        updatePreview()
    }

    override fun hasSameUndoState(
        first: HistorySnapshot,
        second: HistorySnapshot
    ): Boolean = first.normalized() == second.normalized()

    private fun ImageInfo.asHistoryImageInfo(): ImageInfo = ImageInfo(
        imageFormat = imageFormat,
        quality = quality
    )

    private fun HistorySnapshot.normalized(): HistorySnapshot = copy(
        imageInfo = imageInfo.asHistoryImageInfo(),
        basicFilterState = basicFilterState.copy(selectedUri = null)
    )

    data class HistorySnapshot(
        val imageInfo: ImageInfo = ImageInfo(),
        val keepExif: Boolean = false,
        val filterType: Screen.Filter.Type? = null,
        val basicFilterState: BasicFilterState = BasicFilterState(),
        val maskingFilterState: MaskingFilterState = MaskingFilterState(),
        val backgroundColorForNoAlphaFormats: ColorModel = ColorModel(-0x1000000)
    )

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