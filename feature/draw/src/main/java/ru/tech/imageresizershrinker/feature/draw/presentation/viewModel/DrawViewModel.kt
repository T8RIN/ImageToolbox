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

package ru.tech.imageresizershrinker.feature.draw.presentation.viewModel

import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.toArgb
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olshevski.navigation.reimagined.navController
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.domain.image.ImageCompressor
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.image.ImageScaler
import ru.tech.imageresizershrinker.core.domain.image.ImageTransformer
import ru.tech.imageresizershrinker.core.domain.image.ShareProvider
import ru.tech.imageresizershrinker.core.domain.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.core.domain.saving.SaveResult
import ru.tech.imageresizershrinker.core.domain.saving.model.ImageSaveTarget
import ru.tech.imageresizershrinker.core.filters.domain.FilterProvider
import ru.tech.imageresizershrinker.core.filters.presentation.model.UiFilter
import ru.tech.imageresizershrinker.core.ui.utils.state.update
import ru.tech.imageresizershrinker.feature.draw.domain.DrawBehavior
import ru.tech.imageresizershrinker.feature.draw.domain.ImageDrawApplier
import ru.tech.imageresizershrinker.feature.draw.presentation.components.UiPathPaint
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class DrawViewModel @Inject constructor(
    private val fileController: FileController,
    private val imageTransformer: ImageTransformer<Bitmap>,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val imageDrawApplier: ImageDrawApplier<Bitmap, Path, Color>,
    private val imageGetter: ImageGetter<Bitmap, ExifInterface>,
    private val imageScaler: ImageScaler<Bitmap>,
    private val shareProvider: ShareProvider<Bitmap>,
    private val filterProvider: FilterProvider<Bitmap>
) : ViewModel() {

    private val _bitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val bitmap: Bitmap? by _bitmap

    private val _backgroundColor: MutableState<Color> = mutableStateOf(Color.Transparent)
    val backgroundColor by _backgroundColor

    private val _colorPickerBitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val colorPickerBitmap by _colorPickerBitmap

    val navController = navController<DrawBehavior>(DrawBehavior.None)

    val drawBehavior get() = navController.backstack.entries.last().destination

    private val _uri = mutableStateOf(Uri.EMPTY)
    val uri: Uri by _uri

    private val _paths = mutableStateOf(listOf<UiPathPaint>())
    val paths: List<UiPathPaint> by _paths

    private val _lastPaths = mutableStateOf(listOf<UiPathPaint>())
    val lastPaths: List<UiPathPaint> by _lastPaths

    private val _undonePaths = mutableStateOf(listOf<UiPathPaint>())
    val undonePaths: List<UiPathPaint> by _undonePaths

    val isBitmapChanged: Boolean
        get() = paths.isNotEmpty() || lastPaths.isNotEmpty() || undonePaths.isNotEmpty()

    private val _imageFormat = mutableStateOf(ImageFormat.Default())
    val imageFormat by _imageFormat

    private val _isImageLoading: MutableState<Boolean> = mutableStateOf(false)
    val isImageLoading: Boolean by _isImageLoading

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving: Boolean by _isSaving

    private val _saveExif: MutableState<Boolean> = mutableStateOf(false)
    val saveExif: Boolean by _saveExif

    fun updateMimeType(imageFormat: ImageFormat) {
        _imageFormat.value = imageFormat
    }

    private var savingJob: Job? = null

    fun saveBitmap(
        onComplete: (saveResult: SaveResult) -> Unit
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _isSaving.value = true
            getDrawingBitmap()?.let { localBitmap ->
                onComplete(
                    fileController.save(
                        saveTarget = ImageSaveTarget<ExifInterface>(
                            imageInfo = ImageInfo(
                                imageFormat = imageFormat,
                                width = localBitmap.width,
                                height = localBitmap.height
                            ),
                            originalUri = _uri.value.toString(),
                            sequenceNumber = null,
                            data = imageCompressor.compressAndTransform(
                                image = localBitmap,
                                imageInfo = ImageInfo(
                                    imageFormat = imageFormat,
                                    width = localBitmap.width,
                                    height = localBitmap.height
                                )
                            )
                        ), keepOriginalMetadata = _saveExif.value
                    )
                )
            }
            _isSaving.value = false
        }
    }.also {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = it
    }

    private suspend fun calculateScreenOrientationBasedOnUri(uri: Uri): Int {
        val bmp = imageGetter.getImage(uri = uri.toString(), originalSize = false)?.image
        val imageRatio = (bmp?.width ?: 0) / (bmp?.height?.toFloat() ?: 1f)
        return if (imageRatio <= 1.05f) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }

    fun setSaveExif(bool: Boolean) {
        _saveExif.value = bool
    }

    fun updateBitmap(bitmap: Bitmap?) {
        viewModelScope.launch {
            _isImageLoading.value = true
            _bitmap.value = imageScaler.scaleUntilCanShow(bitmap)
            _isImageLoading.value = false
        }
    }

    fun decodeBitmapByUri(
        uri: Uri,
        originalSize: Boolean = true,
        onGetMimeType: (ImageFormat) -> Unit,
        onGetExif: (ExifInterface?) -> Unit,
        onGetBitmap: (Bitmap) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        _isImageLoading.value = true
        imageGetter.getImageAsync(
            uri = uri.toString(),
            originalSize = originalSize,
            onGetImage = {
                onGetBitmap(it.image)
                onGetExif(it.metadata)
                onGetMimeType(it.imageInfo.imageFormat)
            },
            onError = onError
        )
    }

    fun setUri(uri: Uri) {
        viewModelScope.launch {
            _paths.value = listOf()
            _lastPaths.value = listOf()
            _undonePaths.value = listOf()
            _uri.value = uri
            if (drawBehavior !is DrawBehavior.Image) {
                navController.navigate(
                    DrawBehavior.Image(calculateScreenOrientationBasedOnUri(uri))
                )
            }
            imageGetter.getImage(uri = uri.toString())?.imageInfo?.imageFormat?.let {
                updateMimeType(it)
            }
        }
    }

    private suspend fun getDrawingBitmap(): Bitmap? = withContext(Dispatchers.IO) {
        imageDrawApplier.applyDrawToImage(
            drawBehavior = drawBehavior.let {
                if (it is DrawBehavior.Background) it.copy(color = backgroundColor.toArgb())
                else it
            },
            pathPaints = paths,
            imageUri = _uri.value.toString()
        )
    }

    fun openColorPicker() {
        viewModelScope.launch {
            _colorPickerBitmap.value = getDrawingBitmap()
        }
    }

    fun resetDrawBehavior() {
        _paths.value = listOf()
        _lastPaths.value = listOf()
        _undonePaths.value = listOf()
        _bitmap.value = null
        navController.navigate(DrawBehavior.None)
        _uri.value = Uri.EMPTY
        _backgroundColor.value = Color.Transparent
    }

    fun startDrawOnBackground(
        reqWidth: Int,
        reqHeight: Int,
        color: Color
    ) {
        val width = reqWidth.takeIf { it > 0 } ?: 1
        val height = reqHeight.takeIf { it > 0 } ?: 1
        val imageRatio = width / height.toFloat()
        navController.navigate(
            DrawBehavior.Background(
                orientation = if (imageRatio <= 1f) {
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                } else {
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                },
                width = width,
                height = height,
                color = color.toArgb()
            )
        )
        _backgroundColor.value = color
    }

    fun shareBitmap(onComplete: () -> Unit) {
        savingJob?.cancel()
        _isSaving.value = true
        savingJob = viewModelScope.launch {
            getDrawingBitmap()?.let {
                shareProvider.shareImage(
                    image = it,
                    imageInfo = ImageInfo(
                        imageFormat = imageFormat,
                        width = it.width,
                        height = it.height
                    ),
                    onComplete = onComplete
                )
            }
            _isSaving.value = false
        }
    }

    fun updateBackgroundColor(color: Color) {
        _backgroundColor.value = color
    }

    fun clearDrawing() {
        if (paths.isNotEmpty()) {
            _lastPaths.value = paths
            _paths.value = listOf()
            _undonePaths.value = listOf()
        }
    }

    fun undo() {
        if (paths.isEmpty() && lastPaths.isNotEmpty()) {
            _paths.value = lastPaths
            _lastPaths.value = listOf()
            return
        }
        if (paths.isEmpty()) return

        val lastPath = paths.last()

        _paths.update { it - lastPath }
        _undonePaths.update { it + lastPath }
    }

    fun redo() {
        if (undonePaths.isEmpty()) return

        val lastPath = undonePaths.last()
        _paths.update { it + lastPath }
        _undonePaths.update { it - lastPath }
    }

    fun addPath(pathPaint: UiPathPaint) {
        _paths.update { it + pathPaint }
        _undonePaths.value = listOf()
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.value = false
    }

    suspend fun filter(
        bitmap: Bitmap,
        filters: List<UiFilter<*>>
    ): Bitmap? = imageTransformer.transform(
        image = bitmap,
        transformations = filters.map { filterProvider.filterToTransformation(it) }
    )

    fun cacheCurrentImage(onComplete: (Uri) -> Unit) {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = viewModelScope.launch {
            _isSaving.value = true
            getDrawingBitmap()?.let { image ->
                shareProvider.cacheImage(
                    image = image,
                    imageInfo = ImageInfo(
                        imageFormat = imageFormat,
                        width = image.width,
                        height = image.height
                    ),
                    name = Random.nextInt().toString()
                )?.let { uri ->
                    onComplete(uri.toUri())
                }
            }
            _isSaving.value = false
        }
    }

}
