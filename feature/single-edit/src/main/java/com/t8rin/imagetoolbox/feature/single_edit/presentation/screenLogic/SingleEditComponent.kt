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
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.smarttoolfactory.cropper.model.AspectRatio
import com.smarttoolfactory.cropper.model.OutlineType
import com.smarttoolfactory.cropper.model.RectCropShape
import com.smarttoolfactory.cropper.settings.CropDefaults
import com.smarttoolfactory.cropper.settings.CropOutlineProperty
import com.t8rin.curves.ImageCurvesEditorState
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImagePreviewCreator
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.ImageTransformer
import com.t8rin.imagetoolbox.core.domain.image.Metadata
import com.t8rin.imagetoolbox.core.domain.image.clearAllAttributes
import com.t8rin.imagetoolbox.core.domain.image.clearAttribute
import com.t8rin.imagetoolbox.core.domain.image.model.ImageData
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.ImageScaleMode
import com.t8rin.imagetoolbox.core.domain.image.model.MetadataTag
import com.t8rin.imagetoolbox.core.domain.image.model.Preset
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.image.model.ResizeType
import com.t8rin.imagetoolbox.core.domain.model.DomainAspectRatio
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.transformation.Transformation
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.domain.utils.update
import com.t8rin.imagetoolbox.core.filters.domain.FilterProvider
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.filters.presentation.widget.FilterTemplateCreationSheetComponent
import com.t8rin.imagetoolbox.core.filters.presentation.widget.addFilters.AddFiltersSheetComponent
import com.t8rin.imagetoolbox.core.settings.domain.SettingsProvider
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.safeAspectRatio
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.savable
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.core.ui.widget.modifier.HelperGridParams
import com.t8rin.imagetoolbox.feature.draw.domain.DrawLineStyle
import com.t8rin.imagetoolbox.feature.draw.domain.DrawMode
import com.t8rin.imagetoolbox.feature.draw.domain.DrawPathMode
import com.t8rin.imagetoolbox.feature.draw.presentation.components.UiPathPaint
import com.t8rin.imagetoolbox.feature.erase_background.domain.AutoBackgroundRemover
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
    private val settingsProvider: SettingsProvider,
    dispatchersHolder: DispatchersHolder,
    addFiltersSheetComponentFactory: AddFiltersSheetComponent.Factory,
    filterTemplateCreationSheetComponentFactory: FilterTemplateCreationSheetComponent.Factory,
) : BaseComponent(dispatchersHolder, componentContext) {

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

    init {
        componentScope.launch {
            val settingsState = settingsProvider.getSettingsState()
            _drawPathMode.update { DrawPathMode.fromOrdinal(settingsState.defaultDrawPathMode) }
            _imageInfo.update {
                it.copy(resizeType = settingsState.defaultResizeType)
            }
        }
    }

    private var job: Job? by smartJob {
        _isImageLoading.update { false }
    }

    private suspend fun checkBitmapAndUpdate(resetPreset: Boolean = false) {
        if (resetPreset) {
            _presetSelected.update { Preset.None }
        }
        _bitmap.value?.let { bmp ->
            val preview = updatePreview(bmp)
            _previewBitmap.update { null }
            _shouldShowPreview.update { imagePreviewCreator.canShow(preview) }
            if (shouldShowPreview) _previewBitmap.update { preview }
        }
    }

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    fun saveBitmap(
        oneTimeSaveLocationUri: String?,
        onComplete: (result: SaveResult) -> Unit,
    ) {
        savingJob = trackProgress {
            _isSaving.update { true }
            bitmap?.let { bitmap ->
                onComplete(
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
    ): Bitmap? = withContext(defaultDispatcher) {
        return@withContext imageInfo.run {
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
                checkBitmapAndUpdate()
            }
        }
    }

    fun resetValues(newBitmapComes: Boolean = false) {
        _imageInfo.update {
            ImageInfo(
                width = _originalSize.value?.width ?: 0,
                height = _originalSize.value?.height ?: 0,
                imageFormat = it.imageFormat,
                originalUri = uri.toString()
            )
        }
        if (newBitmapComes) {
            _bitmap.update {
                _internalBitmap.value
            }
        }
        debouncedImageCalculation {
            checkBitmapAndUpdate(
                resetPreset = true
            )
        }
    }

    fun updateBitmapAfterEditing(
        bitmap: Bitmap?,
        saveOriginalSize: Boolean = false,
    ) {
        componentScope.launch {
            if (!saveOriginalSize) {
                val size = bitmap?.let { it.width to it.height }
                _originalSize.update {
                    size?.run { IntegerSize(width = first, height = second) }
                }
            }
            _bitmap.update {
                imageScaler.scaleUntilCanShow(bitmap)
            }
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
            registerChanges()
        }
    }

    fun rotateBitmapLeft() {
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
        registerChanges()
    }

    fun rotateBitmapRight() {
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
        registerChanges()
    }

    fun flipImage() {
        _imageInfo.update {
            it.copy(isFlipped = !it.isFlipped)
        }
        debouncedImageCalculation {
            checkBitmapAndUpdate()
        }
        registerChanges()
    }

    fun updateWidth(width: Int) {
        if (imageInfo.width != width) {
            _imageInfo.update {
                it.copy(width = width)
            }
            debouncedImageCalculation {
                checkBitmapAndUpdate(
                    resetPreset = true
                )
            }
            registerChanges()
        }
    }

    fun updateHeight(height: Int) {
        if (imageInfo.height != height) {
            _imageInfo.update {
                it.copy(height = height)
            }
            debouncedImageCalculation {
                checkBitmapAndUpdate(
                    resetPreset = true
                )
            }
            registerChanges()
        }
    }

    fun setQuality(quality: Quality) {
        if (imageInfo.quality != quality) {
            _imageInfo.update {
                it.copy(quality = quality)
            }
            debouncedImageCalculation {
                checkBitmapAndUpdate()
            }
            registerChanges()
        }
    }

    fun setImageFormat(imageFormat: ImageFormat) {
        if (imageInfo.imageFormat != imageFormat) {
            _imageInfo.update {
                it.copy(imageFormat = imageFormat)
            }
            debouncedImageCalculation {
                checkBitmapAndUpdate(
                    resetPreset = _presetSelected.value == Preset.Telegram && imageFormat != ImageFormat.Png.Lossless
                )
            }
            registerChanges()
        }
    }

    fun setResizeType(type: ResizeType) {
        if (imageInfo.resizeType != type) {
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
            registerChanges()
        }
    }

    fun setUri(uri: Uri) {
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
            }
        )
    }

    private fun setImageData(imageData: ImageData<Bitmap>) {
        job = componentScope.launch {
            _isImageLoading.update { true }
            _exif.update { imageData.metadata }
            val bitmap = imageData.image
            val size = bitmap.width to bitmap.height
            _originalSize.update {
                size.run { IntegerSize(width = first, height = second) }
            }
            _bitmap.update {
                _internalBitmap.update {
                    imageScaler.scaleUntilCanShow(bitmap)
                }
            }
            resetValues(true)
            _imageInfo.update {
                imageData.imageInfo.copy(
                    width = size.first,
                    height = size.second
                )
            }
            checkBitmapAndUpdate(
                resetPreset = _presetSelected.value == Preset.Telegram && imageData.imageInfo.imageFormat != ImageFormat.Png.Lossless
            )
            _isImageLoading.update { false }
        }
    }

    fun shareBitmap(onComplete: () -> Unit) {
        savingJob = trackProgress {
            _isSaving.update { true }
            bitmap?.let { image ->
                shareProvider.shareImage(
                    image = image,
                    imageInfo = imageInfo.copy(originalUri = uri.toString()),
                    onComplete = onComplete
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

    fun canShow(): Boolean = bitmap?.let { imagePreviewCreator.canShow(it) } == true

    fun setPreset(preset: Preset) {
        componentScope.launch {
            if (preset is Preset.AspectRatio && preset.ratio != 1f) {
                _imageInfo.update { it.copy(rotationDegrees = 0f) }
            }
            setBitmapInfo(
                imageTransformer.applyPresetBy(
                    image = bitmap,
                    preset = preset,
                    currentInfo = imageInfo
                )
            )
            _presetSelected.update { preset }
            registerChanges()
        }
    }

    fun clearExif() {
        updateExif(_exif.value?.clearAllAttributes())
    }

    private fun updateExif(metadata: Metadata?) {
        _exif.update { metadata }
        registerChanges()
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
        index: Int,
        showError: (Throwable) -> Unit,
    ) {
        val list = _filterList.value.toMutableList()
        runCatching {
            list[index] = list[index].copy(value)
            _filterList.update { list }
        }.onFailure {
            showError(it)
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
        _imageInfo.update {
            it.copy(imageScaleMode = imageScaleMode)
        }
        debouncedImageCalculation {
            checkBitmapAndUpdate()
        }
        registerChanges()
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
