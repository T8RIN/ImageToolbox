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

package ru.tech.imageresizershrinker.feature.single_edit.presentation.viewModel

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.viewModelScope
import com.smarttoolfactory.cropper.model.AspectRatio
import com.smarttoolfactory.cropper.model.OutlineType
import com.smarttoolfactory.cropper.model.RectCropShape
import com.smarttoolfactory.cropper.settings.CropDefaults
import com.smarttoolfactory.cropper.settings.CropOutlineProperty
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.domain.dispatchers.DispatchersHolder
import ru.tech.imageresizershrinker.core.domain.image.ImageCompressor
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.image.ImagePreviewCreator
import ru.tech.imageresizershrinker.core.domain.image.ImageScaler
import ru.tech.imageresizershrinker.core.domain.image.ImageTransformer
import ru.tech.imageresizershrinker.core.domain.image.ShareProvider
import ru.tech.imageresizershrinker.core.domain.image.model.ImageData
import ru.tech.imageresizershrinker.core.domain.image.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.image.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.image.model.ImageScaleMode
import ru.tech.imageresizershrinker.core.domain.image.model.MetadataTag
import ru.tech.imageresizershrinker.core.domain.image.model.Preset
import ru.tech.imageresizershrinker.core.domain.image.model.Quality
import ru.tech.imageresizershrinker.core.domain.image.model.ResizeType
import ru.tech.imageresizershrinker.core.domain.model.DomainAspectRatio
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.core.domain.saving.model.ImageSaveTarget
import ru.tech.imageresizershrinker.core.domain.saving.model.SaveResult
import ru.tech.imageresizershrinker.core.domain.transformation.Transformation
import ru.tech.imageresizershrinker.core.domain.utils.smartJob
import ru.tech.imageresizershrinker.core.filters.domain.FilterProvider
import ru.tech.imageresizershrinker.core.filters.presentation.model.UiFilter
import ru.tech.imageresizershrinker.core.ui.utils.BaseViewModel
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.safeAspectRatio
import ru.tech.imageresizershrinker.core.ui.utils.state.update
import ru.tech.imageresizershrinker.feature.draw.presentation.components.UiPathPaint
import ru.tech.imageresizershrinker.feature.erase_background.domain.AutoBackgroundRemover
import javax.inject.Inject

@HiltViewModel
class SingleEditViewModel @Inject constructor(
    private val fileController: FileController,
    private val imageTransformer: ImageTransformer<Bitmap>,
    private val imagePreviewCreator: ImagePreviewCreator<Bitmap>,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap, ExifInterface>,
    private val imageScaler: ImageScaler<Bitmap>,
    private val autoBackgroundRemover: AutoBackgroundRemover<Bitmap>,
    private val shareProvider: ShareProvider<Bitmap>,
    private val filterProvider: FilterProvider<Bitmap>,
    dispatchersHolder: DispatchersHolder
) : BaseViewModel(dispatchersHolder) {

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
                OutlineType.Rect,
                RectCropShape(
                    id = 0,
                    title = OutlineType.Rect.name
                )
            ),
            fling = true
        )
    )
    val cropProperties by _cropProperties

    private val _exif: MutableState<ExifInterface?> = mutableStateOf(null)
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

    private var job: Job? by smartJob {
        _isImageLoading.update { false }
    }

    private suspend fun checkBitmapAndUpdate(resetPreset: Boolean = false) {
        if (resetPreset) {
            _presetSelected.value = Preset.None
        }
        _bitmap.value?.let { bmp ->
            val preview = updatePreview(bmp)
            _previewBitmap.value = null
            _shouldShowPreview.value = imagePreviewCreator.canShow(preview)
            if (shouldShowPreview) _previewBitmap.value = preview
        }
    }

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    fun saveBitmap(
        oneTimeSaveLocationUri: String?,
        onComplete: (result: SaveResult) -> Unit,
    ) {
        savingJob = viewModelScope.launch(defaultDispatcher) {
            _isSaving.value = true
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
                            )
                        ),
                        keepOriginalMetadata = true,
                        oneTimeSaveLocationUri = oneTimeSaveLocationUri
                    ).onSuccess(::registerSave)
                )
            }
            _isSaving.value = false
        }
    }

    private suspend fun updatePreview(
        bitmap: Bitmap
    ): Bitmap? = withContext(defaultDispatcher) {
        return@withContext imageInfo.run {
            _showWarning.value = width * height * 4L >= 10_000 * 10_000 * 3L
            imagePreviewCreator.createPreview(
                image = bitmap,
                imageInfo = this,
                onGetByteCount = {
                    _imageInfo.value = _imageInfo.value.copy(sizeInBytes = it)
                }
            )
        }
    }

    private fun setBitmapInfo(newInfo: ImageInfo) {
        if (_imageInfo.value != newInfo) {
            _imageInfo.value = newInfo
            debouncedImageCalculation {
                checkBitmapAndUpdate()
            }
        }
    }

    fun resetValues(newBitmapComes: Boolean = false) {
        _imageInfo.value = ImageInfo(
            width = _originalSize.value?.width ?: 0,
            height = _originalSize.value?.height ?: 0,
            imageFormat = imageInfo.imageFormat,
            originalUri = uri.toString()
        )
        if (newBitmapComes) {
            _bitmap.value = _internalBitmap.value
        }
        debouncedImageCalculation {
            checkBitmapAndUpdate(
                resetPreset = true
            )
        }
    }

    fun updateBitmapAfterEditing(
        bitmap: Bitmap?,
        saveOriginalSize: Boolean = false
    ) {
        viewModelScope.launch {
            if (!saveOriginalSize) {
                val size = bitmap?.let { it.width to it.height }
                _originalSize.value = size?.run { IntegerSize(width = first, height = second) }
            }
            _bitmap.value = imageScaler.scaleUntilCanShow(bitmap)
            if (!saveOriginalSize) {
                _imageInfo.value = _imageInfo.value.copy(
                    width = bitmap?.width ?: 0,
                    height = bitmap?.height ?: 0
                )
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
        _imageInfo.value = _imageInfo.value.run {
            copy(
                rotationDegrees = _imageInfo.value.rotationDegrees - 90f,
                height = width,
                width = height
            )
        }
        debouncedImageCalculation {
            checkBitmapAndUpdate()
        }
        registerChanges()
    }

    fun rotateBitmapRight() {
        _imageInfo.value = _imageInfo.value.run {
            copy(
                rotationDegrees = _imageInfo.value.rotationDegrees + 90f,
                height = width,
                width = height
            )
        }
        debouncedImageCalculation {
            checkBitmapAndUpdate()
        }
        registerChanges()
    }

    fun flipImage() {
        _imageInfo.value = _imageInfo.value.copy(isFlipped = !_imageInfo.value.isFlipped)
        debouncedImageCalculation {
            checkBitmapAndUpdate()
        }
        registerChanges()
    }

    fun updateWidth(width: Int) {
        if (_imageInfo.value.width != width) {
            _imageInfo.value = _imageInfo.value.copy(width = width)
            debouncedImageCalculation {
                checkBitmapAndUpdate(
                    resetPreset = true
                )
            }
            registerChanges()
        }
    }

    fun updateHeight(height: Int) {
        if (_imageInfo.value.height != height) {
            _imageInfo.value = _imageInfo.value.copy(height = height)
            debouncedImageCalculation {
                checkBitmapAndUpdate(
                    resetPreset = true
                )
            }
            registerChanges()
        }
    }

    fun setQuality(quality: Quality) {
        if (_imageInfo.value.quality != quality) {
            _imageInfo.value = _imageInfo.value.copy(quality = quality)
            debouncedImageCalculation {
                checkBitmapAndUpdate()
            }
            registerChanges()
        }
    }

    fun setImageFormat(imageFormat: ImageFormat) {
        if (_imageInfo.value.imageFormat != imageFormat) {
            _imageInfo.value = _imageInfo.value.copy(imageFormat = imageFormat)
            debouncedImageCalculation {
                checkBitmapAndUpdate(
                    resetPreset = _presetSelected.value == Preset.Telegram && imageFormat != ImageFormat.Png.Lossless
                )
            }
            registerChanges()
        }
    }

    fun setResizeType(type: ResizeType) {
        if (_imageInfo.value.resizeType != type) {
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
        _uri.value = uri
    }

    fun decodeBitmapByUri(
        uri: Uri,
        onError: (Throwable) -> Unit
    ) {
        _isImageLoading.value = true
        _imageInfo.update {
            it.copy(originalUri = uri.toString())
        }
        imageGetter.getImageAsync(
            uri = uri.toString(),
            originalSize = true,
            onGetImage = ::setImageData,
            onError = {
                _isImageLoading.value = false
                onError(it)
            }
        )
    }

    private fun setImageData(imageData: ImageData<Bitmap, ExifInterface>) {
        job = viewModelScope.launch {
            _isImageLoading.value = true
            _exif.update { imageData.metadata }
            val bitmap = imageData.image
            val size = bitmap.width to bitmap.height
            _originalSize.value = size.run { IntegerSize(width = first, height = second) }
            _bitmap.value = imageScaler.scaleUntilCanShow(bitmap).also {
                _internalBitmap.value = it
            }
            resetValues(true)
            _imageInfo.value = imageData.imageInfo.copy(
                width = size.first,
                height = size.second
            )
            checkBitmapAndUpdate(
                resetPreset = _presetSelected.value == Preset.Telegram && imageData.imageInfo.imageFormat != ImageFormat.Png.Lossless
            )
            _isImageLoading.value = false
        }
    }

    fun shareBitmap(onComplete: () -> Unit) {
        savingJob = viewModelScope.launch {
            _isSaving.value = true
            bitmap?.let { image ->
                shareProvider.shareImage(
                    image = image,
                    imageInfo = imageInfo.copy(originalUri = uri.toString()),
                    onComplete = onComplete
                )
            }
            _isSaving.value = false
        }
    }

    fun cacheCurrentImage(onComplete: (Uri) -> Unit) {
        savingJob = viewModelScope.launch {
            _isSaving.value = true
            bitmap?.let { image ->
                shareProvider.cacheImage(
                    image = image,
                    imageInfo = imageInfo.copy(originalUri = uri.toString())
                )?.let { uri ->
                    onComplete(uri.toUri())
                }
            }
            _isSaving.value = false
        }
    }

    fun canShow(): Boolean = bitmap?.let { imagePreviewCreator.canShow(it) } ?: false

    fun setPreset(preset: Preset) {
        viewModelScope.launch {
            setBitmapInfo(
                imageTransformer.applyPresetBy(
                    image = bitmap,
                    preset = preset,
                    currentInfo = imageInfo
                )
            )
            _presetSelected.value = preset
            registerChanges()
        }
    }

    fun clearExif() {
        val t = _exif.value
        MetadataTag.entries.forEach {
            t?.setAttribute(it.key, null)
        }
        _exif.value = t
        registerChanges()
    }

    private fun updateExif(exifInterface: ExifInterface?) {
        _exif.value = exifInterface
        registerChanges()
    }

    fun removeExifTag(tag: MetadataTag) {
        val exifInterface = _exif.value
        exifInterface?.setAttribute(tag.key, null)
        updateExif(exifInterface)
    }

    fun updateExifByTag(
        tag: MetadataTag,
        value: String
    ) {
        val exifInterface = _exif.value
        exifInterface?.setAttribute(tag.key, value)
        updateExif(exifInterface)
    }

    fun setCropAspectRatio(
        domainAspectRatio: DomainAspectRatio,
        aspectRatio: AspectRatio
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
        showError: (Throwable) -> Unit
    ) {
        val list = _filterList.value.toMutableList()
        runCatching {
            list[index] = list[index].copy(value)
            _filterList.value = list
        }.onFailure {
            showError(it)
            list[index] = list[index].newInstance()
            _filterList.value = list
        }
    }

    fun updateOrder(value: List<UiFilter<*>>) {
        _filterList.value = value
    }

    fun addFilter(filter: UiFilter<*>) {
        _filterList.value += filter
    }

    fun removeFilterAtIndex(index: Int) {
        _filterList.value = _filterList.value.toMutableList().apply {
            removeAt(index)
        }
    }

    fun clearFilterList() {
        _filterList.value = listOf()
    }

    fun clearDrawing(canUndo: Boolean = false) {
        viewModelScope.launch {
            delay(500L)
            _drawLastPaths.value = if (canUndo) drawPaths else listOf()
            _drawPaths.value = listOf()
            _drawUndonePaths.value = listOf()
        }
    }

    fun undoDraw() {
        if (drawPaths.isEmpty() && drawLastPaths.isNotEmpty()) {
            _drawPaths.value = drawLastPaths
            _drawLastPaths.value = listOf()
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
        _drawUndonePaths.value = listOf()
    }

    fun clearErasing(canUndo: Boolean = false) {
        viewModelScope.launch {
            delay(250L)
            _eraseLastPaths.value = if (canUndo) erasePaths else listOf()
            _erasePaths.value = listOf()
            _eraseUndonePaths.value = listOf()
        }
    }

    fun undoErase() {
        if (erasePaths.isEmpty() && eraseLastPaths.isNotEmpty()) {
            _erasePaths.value = eraseLastPaths
            _eraseLastPaths.value = listOf()
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
        _eraseUndonePaths.value = listOf()
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.value = false
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
        filters: List<UiFilter<*>>
    ): Bitmap? = imageTransformer.transform(
        image = bitmap,
        transformations = mapFilters(filters)
    )

    fun mapFilters(
        filters: List<UiFilter<*>>
    ): List<Transformation<Bitmap>> = filters.map { filterProvider.filterToTransformation(it) }

}
