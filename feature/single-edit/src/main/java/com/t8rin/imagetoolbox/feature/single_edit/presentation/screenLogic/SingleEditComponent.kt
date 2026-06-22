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

package com.t8rin.imagetoolbox.feature.single_edit.presentation.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.t8rin.cropper.model.AspectRatio
import com.t8rin.cropper.model.OutlineType
import com.t8rin.cropper.model.RectCropShape
import com.t8rin.cropper.settings.CropDefaults
import com.t8rin.cropper.settings.CropOutlineProperty
import com.t8rin.curves.ImageCurvesEditorState
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageExportProfilesUseCase
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImagePreviewCreator
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.ImageTransformer
import com.t8rin.imagetoolbox.core.domain.image.Metadata
import com.t8rin.imagetoolbox.core.domain.image.clearAllAttributes
import com.t8rin.imagetoolbox.core.domain.image.clearAttribute
import com.t8rin.imagetoolbox.core.domain.image.model.ImageData
import com.t8rin.imagetoolbox.core.domain.image.model.ImageExportProfile
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.ImageScaleMode
import com.t8rin.imagetoolbox.core.domain.image.model.MetadataTag
import com.t8rin.imagetoolbox.core.domain.image.model.Preset
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.image.model.ResizeType
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.model.DomainAspectRatio
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.transformation.Transformation
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.domain.utils.update
import com.t8rin.imagetoolbox.core.filters.domain.FilterProvider
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.filters.presentation.widget.FilterTemplateCreationSheetComponent
import com.t8rin.imagetoolbox.core.filters.presentation.widget.addFilters.AddFiltersSheetComponent
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.ui.utils.BaseHistoryComponent
import com.t8rin.imagetoolbox.core.ui.utils.ImageExportProfilesHolder
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.safeAspectRatio
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.navigation.coroutineScope
import com.t8rin.imagetoolbox.core.ui.utils.state.savable
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.core.ui.widget.modifier.HelperGridParams
import com.t8rin.imagetoolbox.feature.draw.domain.DrawLineStyle
import com.t8rin.imagetoolbox.feature.draw.domain.DrawMode
import com.t8rin.imagetoolbox.feature.draw.domain.DrawPathMode
import com.t8rin.imagetoolbox.feature.draw.presentation.components.UiPathPaint
import com.t8rin.imagetoolbox.feature.erase_background.domain.AutoBackgroundRemover
import com.t8rin.imagetoolbox.feature.single_edit.presentation.screenLogic.SingleEditComponent.HistorySnapshot
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class SingleEditComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialUri: Uri?,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val fileController: FileController,
    private val imageTransformer: ImageTransformer<Bitmap>,
    private val imagePreviewCreator: ImagePreviewCreator<Bitmap>,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap>,
    private val imageScaler: ImageScaler<Bitmap>,
    private val autoBackgroundRemover: AutoBackgroundRemover<Bitmap>,
    private val shareProvider: ImageShareProvider<Bitmap>,
    private val filterProvider: FilterProvider<Bitmap>,
    private val settingsManager: SettingsManager,
    private val imageExportProfilesUseCase: ImageExportProfilesUseCase,
    dispatchersHolder: DispatchersHolder,
    addFiltersSheetComponentFactory: AddFiltersSheetComponent.Factory,
    filterTemplateCreationSheetComponentFactory: FilterTemplateCreationSheetComponent.Factory,
) : BaseHistoryComponent<HistorySnapshot>(
    dispatchersHolder = dispatchersHolder,
    componentContext = componentContext
), ImageExportProfilesHolder by ImageExportProfilesHolder(
    imageExportProfilesUseCase = imageExportProfilesUseCase,
    componentScope = componentContext.coroutineScope
) {

    init {
        debounce {
            initialUri?.let(::setUri)
        }

        doOnDestroy {
            autoBackgroundRemover.cleanup()
        }
    }

    val addFiltersSheetComponent: AddFiltersSheetComponent = addFiltersSheetComponentFactory(
        componentContext = componentContext.childContext(
            key = "addFiltersSingle"
        )
    )

    val filterTemplateCreationSheetComponent: FilterTemplateCreationSheetComponent =
        filterTemplateCreationSheetComponentFactory(
            componentContext = componentContext.childContext(
                key = "filterTemplateCreationSheetComponentSingle"
            )
        )

    private val _originalSize: MutableState<IntegerSize?> = mutableStateOf(null)
    val originalSize by _originalSize

    private val _erasePaths = mutableStateOf(listOf<UiPathPaint>())
    val erasePaths: List<UiPathPaint> by _erasePaths

    private val _eraseLastPaths = mutableStateOf(listOf<UiPathPaint>())
    val eraseLastPaths: List<UiPathPaint> by _eraseLastPaths

    private val _eraseUndonePaths = mutableStateOf(listOf<UiPathPaint>())
    val eraseUndonePaths: List<UiPathPaint> by _eraseUndonePaths

    private val _drawPaths = mutableStateOf(listOf<UiPathPaint>())
    val drawPaths: List<UiPathPaint> by _drawPaths

    private val _drawLastPaths = mutableStateOf(listOf<UiPathPaint>())
    val drawLastPaths: List<UiPathPaint> by _drawLastPaths

    private val _drawUndonePaths = mutableStateOf(listOf<UiPathPaint>())
    val drawUndonePaths: List<UiPathPaint> by _drawUndonePaths

    private val _drawSpotHealCache = mutableStateMapOf<Int, Bitmap>()
    val drawSpotHealCache: Map<Int, Bitmap> = _drawSpotHealCache

    private val _filterList =
        mutableStateOf(listOf<UiFilter<*>>())
    val filterList by _filterList

    private val _selectedAspectRatio: MutableState<DomainAspectRatio> =
        mutableStateOf(DomainAspectRatio.Free)
    val selectedAspectRatio by _selectedAspectRatio

    private val _cropProperties = mutableStateOf(
        CropDefaults.properties(
            cropOutlineProperty = CropOutlineProperty(
                outlineType = OutlineType.Rect,
                cropOutline = RectCropShape(
                    id = 0,
                    title = OutlineType.Rect.name
                )
            ),
            fling = true
        )
    )
    val cropProperties by _cropProperties

    private val _imageCurvesEditorState: MutableState<ImageCurvesEditorState> =
        mutableStateOf(ImageCurvesEditorState.Default)
    val imageCurvesEditorState: ImageCurvesEditorState by _imageCurvesEditorState

    private val _exif: MutableState<Metadata?> = mutableStateOf(null)
    val exif by _exif

    private val _uri: MutableState<Uri> = mutableStateOf(Uri.EMPTY)
    val uri: Uri by _uri

    private val _internalBitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val initialBitmap by _internalBitmap

    private val _bitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val bitmap: Bitmap? by _bitmap

    private val _previewBitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val previewBitmap: Bitmap? by _previewBitmap

    private val _imageInfo: MutableState<ImageInfo> = mutableStateOf(ImageInfo())
    val imageInfo: ImageInfo by _imageInfo

    private var currentCachedBitmapUri: String? = null

    private val _showWarning: MutableState<Boolean> = mutableStateOf(false)
    val showWarning: Boolean by _showWarning

    private val _shouldShowPreview: MutableState<Boolean> = mutableStateOf(true)
    val shouldShowPreview by _shouldShowPreview

    private val _presetSelected: MutableState<Preset> = mutableStateOf(Preset.None)
    val presetSelected by _presetSelected

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving by _isSaving

    private val _drawMode: MutableState<DrawMode> = mutableStateOf(DrawMode.Pen)
    val drawMode: DrawMode by _drawMode

    private val _drawPathMode: MutableState<DrawPathMode> = mutableStateOf(DrawPathMode.Free)
    val drawPathMode: DrawPathMode by _drawPathMode

    private val _drawLineStyle: MutableState<DrawLineStyle> = mutableStateOf(DrawLineStyle.None)
    val drawLineStyle: DrawLineStyle by _drawLineStyle

    private val _helperGridParams = fileController.savable(
        scope = componentScope,
        initial = HelperGridParams()
    )
    val helperGridParams: HelperGridParams by _helperGridParams

    private val isAlwaysClearExif: Boolean get() = settingsManager.settingsState.value.isAlwaysClearExif

    init {
        componentScope.launch {
            val settingsState = settingsManager.getSettingsState()
            _drawPathMode.update { DrawPathMode.fromOrdinal(settingsState.defaultDrawPathMode) }
            _imageInfo.update {
                it.copy(resizeType = settingsState.defaultResizeType)
            }
        }
    }

    private var job: Job? by smartJob {
        _isImageLoading.update { false }
    }

    private suspend fun checkBitmapAndUpdate(
        resetPreset: Boolean = false,
        clearPreview: Boolean = true,
        previewImageInfo: ImageInfo = imageInfo
    ) {
        if (resetPreset) {
            _presetSelected.update { Preset.None }
        }
        _bitmap.value?.let { bmp ->
            val preview = updatePreview(
                bitmap = bmp,
                previewImageInfo = previewImageInfo
            )
            if (clearPreview) {
                _previewBitmap.update { null }
            }
            _shouldShowPreview.update { imagePreviewCreator.canShow(preview) }
            if (shouldShowPreview) _previewBitmap.update { preview }
        }
    }

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    fun saveBitmap(
        oneTimeSaveLocationUri: String?
    ) {
        finalizePendingHistoryTransaction()
        savingJob = trackProgress {
            _isSaving.update { true }
            bitmap?.let { bitmap ->
                parseSaveResult(
                    fileController.save(
                        saveTarget = ImageSaveTarget(
                            imageInfo = imageInfo,
                            metadata = exif,
                            originalUri = uri.toString(),
                            sequenceNumber = null,
                            data = imageCompressor.compressAndTransform(
                                image = bitmap,
                                imageInfo = imageInfo.copy(
                                    originalUri = uri.toString()
                                )
                            ),
                            presetInfo = presetSelected
                        ),
                        keepOriginalMetadata = true,
                        oneTimeSaveLocationUri = oneTimeSaveLocationUri
                    ).onSuccess(::registerSave)
                )
            }
            _isSaving.update { false }
        }
    }

    private suspend fun updatePreview(
        bitmap: Bitmap,
        previewImageInfo: ImageInfo = imageInfo
    ): Bitmap? = withContext(defaultDispatcher) {
        return@withContext previewImageInfo.run {
            _showWarning.update {
                width * height * 4L >= 10_000 * 10_000 * 3L
            }
            imagePreviewCreator.createPreview(
                image = bitmap,
                imageInfo = this,
                onGetByteCount = { sizeInBytes ->
                    _imageInfo.update { it.copy(sizeInBytes = sizeInBytes) }
                }
            )
        }
    }

    private fun setBitmapInfo(newInfo: ImageInfo) {
        if (imageInfo != newInfo) {
            _imageInfo.update { newInfo }
            debouncedImageCalculation {
                checkBitmapAndUpdate(previewImageInfo = newInfo)
            }
        }
    }

    fun resetValues(
        newBitmapComes: Boolean = false,
        commitToHistory: Boolean = true
    ) {
        finalizePendingHistoryTransaction()
        val beforeSnapshot = currentHistorySnapshot()
        _imageInfo.update {
            ImageInfo(
                width = _originalSize.value?.width ?: 0,
                height = _originalSize.value?.height ?: 0,
                imageFormat = it.imageFormat,
                originalUri = uri.toString()
            )
        }
        if (newBitmapComes) {
            currentCachedBitmapUri = null
            _bitmap.update {
                _internalBitmap.value
            }
        }
        debouncedImageCalculation {
            checkBitmapAndUpdate(
                resetPreset = true
            )
        }
        if (commitToHistory) {
            commitHistoryFrom(beforeSnapshot)
        }
    }

    fun updateBitmapAfterEditing(
        bitmap: Bitmap?,
        saveOriginalSize: Boolean = false,
    ) {
        componentScope.launch {
            finalizePendingHistoryTransaction()
            val beforeSnapshot = currentHistorySnapshot()
            val cachedUri = bitmap?.let {
                cacheEditedBitmap(it)
            }

            if (!saveOriginalSize) {
                val size = bitmap?.let { it.width to it.height }
                _originalSize.update {
                    size?.run { IntegerSize(width = first, height = second) }
                }
            }
            _drawSpotHealCache.clear()
            _bitmap.update {
                imageScaler.scaleUntilCanShow(bitmap)
            }
            currentCachedBitmapUri = cachedUri
            _imageInfo.update {
                it.copy(
                    rotationDegrees = 0f
                )
            }
            if (!saveOriginalSize) {
                _imageInfo.update {
                    it.copy(
                        width = bitmap?.width ?: 0,
                        height = bitmap?.height ?: 0
                    )
                }
            }
            debouncedImageCalculation {
                checkBitmapAndUpdate(
                    resetPreset = true
                )
            }
            commitHistoryFrom(beforeSnapshot)
        }
    }

    fun rotateBitmapLeft() {
        finalizePendingHistoryTransaction()
        val beforeSnapshot = currentHistorySnapshot()
        _imageInfo.update {
            it.copy(
                rotationDegrees = it.rotationDegrees - 90f,
                height = it.width,
                width = it.height
            )
        }
        debouncedImageCalculation {
            checkBitmapAndUpdate()
        }
        commitHistoryFrom(beforeSnapshot)
    }

    fun rotateBitmapRight() {
        finalizePendingHistoryTransaction()
        val beforeSnapshot = currentHistorySnapshot()
        _imageInfo.update {
            it.copy(
                rotationDegrees = it.rotationDegrees + 90f,
                height = it.width,
                width = it.height
            )
        }
        debouncedImageCalculation {
            checkBitmapAndUpdate()
        }
        commitHistoryFrom(beforeSnapshot)
    }

    fun flipImage() {
        finalizePendingHistoryTransaction()
        val beforeSnapshot = currentHistorySnapshot()
        _imageInfo.update {
            it.copy(isFlipped = !it.isFlipped)
        }
        debouncedImageCalculation {
            checkBitmapAndUpdate()
        }
        commitHistoryFrom(beforeSnapshot)
    }

    fun updateWidth(width: Int) {
        if (imageInfo.width != width) {
            finalizePendingHistoryTransaction()
            val beforeSnapshot = currentHistorySnapshot()
            _imageInfo.update {
                it.copy(width = width)
            }
            debouncedImageCalculation {
                checkBitmapAndUpdate(
                    resetPreset = true
                )
            }
            commitHistoryFrom(beforeSnapshot)
        }
    }

    fun updateHeight(height: Int) {
        if (imageInfo.height != height) {
            finalizePendingHistoryTransaction()
            val beforeSnapshot = currentHistorySnapshot()
            _imageInfo.update {
                it.copy(height = height)
            }
            debouncedImageCalculation {
                checkBitmapAndUpdate(
                    resetPreset = true
                )
            }
            commitHistoryFrom(beforeSnapshot)
        }
    }

    fun setQuality(quality: Quality) {
        val currentQuality = imageInfo.quality
        val coercedQuality = quality.coerceIn(imageInfo.imageFormat)
        if (
            quality::class != coercedQuality::class &&
            currentQuality::class == coercedQuality::class
        ) return

        if (currentQuality != coercedQuality) {
            beginPendingHistoryTransaction()
            _imageInfo.update {
                it.copy(quality = coercedQuality)
            }
            debouncedImageCalculation {
                checkBitmapAndUpdate()
            }
            registerChanges()
            schedulePendingHistoryCommit()
        }
    }

    fun setImageFormat(imageFormat: ImageFormat) {
        if (imageInfo.imageFormat != imageFormat) {
            if (pendingHistoryMode != PendingHistoryMode.FormatChange) {
                finalizePendingHistoryTransaction()
            }
            beginPendingHistoryTransaction(
                mode = PendingHistoryMode.FormatChange,
                commitDelayMillis = formatHistoryTransactionDebounce
            )
            _imageInfo.update {
                it.copy(
                    imageFormat = imageFormat,
                    quality = it.quality.coerceIn(imageFormat)
                )
            }
            debouncedImageCalculation {
                checkBitmapAndUpdate(
                    resetPreset = _presetSelected.value == Preset.Telegram && imageFormat != ImageFormat.Png.Lossless
                )
            }
            registerChanges()
            schedulePendingHistoryCommit()
        }
    }

    fun setResizeType(type: ResizeType) {
        if (imageInfo.resizeType != type) {
            finalizePendingHistoryTransaction()
            val beforeSnapshot = currentHistorySnapshot()
            _imageInfo.update {
                it.copy(
                    resizeType = type.withOriginalSizeIfCrop(originalSize)
                )
            }
            debouncedImageCalculation {
                checkBitmapAndUpdate(
                    resetPreset = false
                )
            }
            commitHistoryFrom(beforeSnapshot)
        }
    }

    fun setUri(uri: Uri) {
        clearHistory()
        currentCachedBitmapUri = null
        registerChangesCleared()
        _uri.update { uri }
        decodeBitmapByUri(uri)
    }

    private fun decodeBitmapByUri(uri: Uri) {
        _isImageLoading.update { true }
        _imageInfo.update {
            it.copy(originalUri = uri.toString())
        }
        imageGetter.getImageAsync(
            uri = uri.toString(),
            originalSize = true,
            onGetImage = ::setImageData,
            onFailure = {
                _isImageLoading.update { false }
                AppToastHost.showFailureToast(it)
            }
        )
    }

    private fun setImageData(imageData: ImageData<Bitmap>) {
        job = componentScope.launch {
            _isImageLoading.update { true }
            _exif.update { imageData.metadata.takeIf { !isAlwaysClearExif } }
            val bitmap = imageData.image
            val size = bitmap.width to bitmap.height
            _originalSize.update {
                size.run { IntegerSize(width = first, height = second) }
            }
            _drawSpotHealCache.clear()
            _bitmap.update {
                _internalBitmap.update {
                    imageScaler.scaleUntilCanShow(bitmap)
                }
            }
            resetValues(
                newBitmapComes = true,
                commitToHistory = false
            )
            _imageInfo.update {
                imageData.imageInfo.copy(
                    width = size.first,
                    height = size.second
                )
            }
            checkBitmapAndUpdate(
                resetPreset = _presetSelected.value == Preset.Telegram && imageData.imageInfo.imageFormat != ImageFormat.Png.Lossless
            )
            resetHistory()
            registerChangesCleared()
            _isImageLoading.update { false }
        }
    }

    fun shareBitmap() {
        savingJob = trackProgress {
            _isSaving.update { true }
            bitmap?.let { image ->
                shareProvider.shareImage(
                    image = image,
                    imageInfo = imageInfo.copy(originalUri = uri.toString()),
                    onComplete = AppToastHost::showConfetti
                )
            }
            _isSaving.update { false }
        }
    }

    fun cacheCurrentImage(onComplete: (Uri) -> Unit) {
        savingJob = trackProgress {
            _isSaving.update { true }
            bitmap?.let { image ->
                shareProvider.cacheImage(
                    image = image,
                    imageInfo = imageInfo.copy(originalUri = uri.toString())
                )?.let { uri ->
                    onComplete(uri.toUri())
                }
            }
            _isSaving.update { false }
        }
    }

    private suspend fun cacheEditedBitmap(bitmap: Bitmap): String? = shareProvider.cacheImage(
        image = bitmap,
        imageInfo = ImageInfo(
            originalUri = uri.toString(),
            imageFormat = ImageFormat.Png.Lossless,
            width = bitmap.width,
            height = bitmap.height
        )
    )

    fun canShow(): Boolean = bitmap?.let { imagePreviewCreator.canShow(it) } == true

    override fun updateProfile(profile: Preset) {
        componentScope.launch {
            finalizePendingHistoryTransaction()
            val beforeSnapshot = currentHistorySnapshot()
            if (profile is Preset.AspectRatio && profile.ratio != 1f) {
                _imageInfo.update { it.copy(rotationDegrees = 0f) }
            }
            setBitmapInfo(
                imageTransformer.applyPresetBy(
                    image = bitmap,
                    preset = profile,
                    currentInfo = imageInfo.copy(
                        originalUri = uri.toString()
                    )
                )
            )
            _presetSelected.update { profile }
            commitHistoryFrom(beforeSnapshot)
        }
    }

    override fun saveProfile(name: String) {
        componentScope.launch {
            imageExportProfilesUseCase.upsert(
                ImageExportProfile.from(
                    name = name,
                    imageInfo = imageInfo,
                    preset = presetSelected,
                    backgroundColorForNoAlphaFormats = settingsManager
                        .settingsState
                        .value
                        .backgroundForNoAlphaImageFormats
                )
            )
        }
    }

    override fun applyProfile(profile: ImageExportProfile) {
        componentScope.launch {
            finalizePendingHistoryTransaction()
            val beforeSnapshot = currentHistorySnapshot()
            restoreProfileBackgroundColor(profile)
            val restoredInfo = profile.toImageInfo(imageInfo).copy(
                originalUri = uri.toString()
            )
            setBitmapInfo(
                profile.applyExportSettingsTo(
                    imageTransformer.applyPresetBy(
                        image = bitmap,
                        preset = profile.preset,
                        currentInfo = restoredInfo
                    )
                )
            )
            _presetSelected.update { profile.preset }
            commitHistoryFrom(beforeSnapshot)
        }
    }

    private suspend fun restoreProfileBackgroundColor(profile: ImageExportProfile) {
        val color = profile.backgroundColorModel() ?: return
        if (settingsManager.settingsState.value.backgroundForNoAlphaImageFormats != color) {
            settingsManager.setBackgroundColorForNoAlphaFormats(color)
        }
    }

    fun clearExif() {
        updateExif(_exif.value?.clearAllAttributes())
    }

    private fun updateExif(metadata: Metadata?) {
        _exif.update { metadata }
    }

    fun removeExifTag(tag: MetadataTag) {
        updateExif(_exif.value?.clearAttribute(tag))
    }

    fun updateExifByTag(
        tag: MetadataTag,
        value: String,
    ) {
        updateExif(_exif.value?.setAttribute(tag, value))
    }

    fun setCropAspectRatio(
        domainAspectRatio: DomainAspectRatio,
        aspectRatio: AspectRatio,
    ) {
        _cropProperties.update { properties ->
            properties.copy(
                aspectRatio = aspectRatio.takeIf {
                    domainAspectRatio != DomainAspectRatio.Original
                } ?: _bitmap.value?.let {
                    AspectRatio(it.safeAspectRatio)
                } ?: aspectRatio,
                fixedAspectRatio = domainAspectRatio != DomainAspectRatio.Free
            )
        }
        _selectedAspectRatio.update { domainAspectRatio }
    }

    fun setCropMask(cropOutlineProperty: CropOutlineProperty) {
        _cropProperties.value =
            _cropProperties.value.copy(cropOutlineProperty = cropOutlineProperty)
    }

    suspend fun loadImage(uri: Uri): Bitmap? = imageGetter.getImage(data = uri)

    fun getBackgroundRemover(): AutoBackgroundRemover<Bitmap> = autoBackgroundRemover

    fun <T : Any> updateFilter(
        value: T,
        index: Int
    ) {
        val list = _filterList.value.toMutableList()
        runCatching {
            list[index] = list[index].copy(value)
            _filterList.update { list }
        }.onFailure {
            AppToastHost.showFailureToast(it)
            list[index] = list[index].newInstance()
            _filterList.update { list }
        }
    }

    fun updateOrder(value: List<UiFilter<*>>) {
        _filterList.update { value }
    }

    fun addFilter(filter: UiFilter<*>) {
        _filterList.update {
            it + filter
        }
    }

    fun removeFilterAtIndex(index: Int) {
        _filterList.update {
            it.toMutableList().apply {
                removeAt(index)
            }
        }
    }

    fun clearFilterList() {
        _filterList.update { listOf() }
    }

    fun clearDrawing(canUndo: Boolean = false) {
        componentScope.launch {
            delay(500L)
            _drawLastPaths.update { if (canUndo) drawPaths else listOf() }
            _drawPaths.update { listOf() }
            _drawUndonePaths.update { listOf() }
            _drawSpotHealCache.clear()
            _drawMode.update { DrawMode.Pen }
            _drawPathMode.update { DrawPathMode.Free }
        }
    }

    fun undoDraw() {
        if (drawPaths.isEmpty() && drawLastPaths.isNotEmpty()) {
            _drawPaths.update { drawLastPaths }
            _drawLastPaths.update { listOf() }
            return
        }
        if (drawPaths.isEmpty()) return

        val lastPath = drawPaths.last()

        _drawPaths.update { it - lastPath }
        _drawUndonePaths.update { it + lastPath }
    }

    fun redoDraw() {
        if (drawUndonePaths.isEmpty()) return

        val lastPath = drawUndonePaths.last()
        _drawPaths.update { it + lastPath }
        _drawUndonePaths.update { it - lastPath }
    }

    fun addPathToDrawList(pathPaint: UiPathPaint) {
        _drawPaths.update { it + pathPaint }
        _drawUndonePaths.update { listOf() }
    }

    fun cacheDrawSpotHealPathResult(
        key: Int,
        bitmap: Bitmap
    ) {
        _drawSpotHealCache[key] = bitmap
    }

    fun clearErasing(canUndo: Boolean = false) {
        componentScope.launch {
            delay(250L)
            _eraseLastPaths.update { if (canUndo) erasePaths else listOf() }
            _erasePaths.update { listOf() }
            _eraseUndonePaths.update { listOf() }
            _drawPathMode.update { DrawPathMode.Free }
        }
    }

    fun undoErase() {
        if (erasePaths.isEmpty() && eraseLastPaths.isNotEmpty()) {
            _erasePaths.update { eraseLastPaths }
            _eraseLastPaths.update { listOf() }
            return
        }
        if (erasePaths.isEmpty()) return

        val lastPath = erasePaths.last()

        _erasePaths.update { it - lastPath }
        _eraseUndonePaths.update { it + lastPath }
    }

    fun redoErase() {
        if (eraseUndonePaths.isEmpty()) return

        val lastPath = eraseUndonePaths.last()
        _erasePaths.update { it + lastPath }
        _eraseUndonePaths.update { it - lastPath }
    }

    fun addPathToEraseList(pathPaint: UiPathPaint) {
        _erasePaths.update { it + pathPaint }
        _eraseUndonePaths.update { listOf() }
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.update { false }
    }

    fun setImageScaleMode(imageScaleMode: ImageScaleMode) {
        finalizePendingHistoryTransaction()
        val beforeSnapshot = currentHistorySnapshot()
        _imageInfo.update {
            it.copy(imageScaleMode = imageScaleMode)
        }
        debouncedImageCalculation {
            checkBitmapAndUpdate()
        }
        commitHistoryFrom(beforeSnapshot)
    }

    suspend fun filter(
        bitmap: Bitmap,
        filters: List<Filter<*>>,
    ): Bitmap? = imageTransformer.transform(
        image = bitmap,
        transformations = mapFilters(filters)
    )

    fun mapFilters(
        filters: List<Filter<*>>,
    ): List<Transformation<Bitmap>> = filters.map { filterProvider.filterToTransformation(it) }

    fun updateDrawMode(drawMode: DrawMode) {
        _drawMode.update { drawMode }
    }

    fun updateDrawPathMode(drawPathMode: DrawPathMode) {
        _drawPathMode.update { drawPathMode }
    }

    fun getFormatForFilenameSelection(): ImageFormat = imageInfo.imageFormat

    fun resetImageCurvesEditorState() {
        _imageCurvesEditorState.update { ImageCurvesEditorState.Default }
    }

    fun updateDrawLineStyle(style: DrawLineStyle) {
        _drawLineStyle.update { style }
    }

    fun updateHelperGridParams(params: HelperGridParams) {
        _helperGridParams.update { params }
    }

    override fun currentHistorySnapshot(): HistorySnapshot = HistorySnapshot(
        cachedUri = currentCachedBitmapUri,
        originalSize = originalSize,
        imageInfo = imageInfo.asHistoryImageInfo(),
        preset = presetSelected,
        backgroundColorForNoAlphaFormats = settingsManager
            .settingsState
            .value
            .backgroundForNoAlphaImageFormats
    )

    override fun applyHistorySnapshot(snapshot: HistorySnapshot) {
        currentCachedBitmapUri = snapshot.cachedUri
        _originalSize.value = snapshot.originalSize
        _imageInfo.value = snapshot.imageInfo
        _presetSelected.value = snapshot.preset
        restoreBackgroundColorForNoAlphaFormats(
            settingsManager = settingsManager,
            backgroundColorForNoAlphaFormats = snapshot.backgroundColorForNoAlphaFormats
        )

        job = componentScope.launch {
            _isImageLoading.update { true }
            _bitmap.value = snapshot.cachedUri
                ?.let {
                    imageGetter.getImage(
                        uri = it,
                        originalSize = false,
                        onFailure = AppToastHost::showFailureToast
                    )?.image
                }
                ?: _internalBitmap.value
            checkBitmapAndUpdate(clearPreview = false)
            _isImageLoading.update { false }
        }
    }

    private fun ImageInfo.asHistoryImageInfo(): ImageInfo = copy(sizeInBytes = 0)

    override fun hasSameUndoState(
        first: HistorySnapshot,
        second: HistorySnapshot
    ): Boolean = first.normalized() == second.normalized()

    private fun HistorySnapshot.normalized(): HistorySnapshot = copy(
        imageInfo = imageInfo.asHistoryImageInfo()
    )

    data class HistorySnapshot(
        val cachedUri: String? = null,
        val originalSize: IntegerSize? = null,
        val imageInfo: ImageInfo = ImageInfo(),
        val preset: Preset = Preset.None,
        val backgroundColorForNoAlphaFormats: ColorModel = ColorModel(-0x1000000)
    )

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialUri: Uri?,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit,
        ): SingleEditComponent
    }
}
