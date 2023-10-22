package ru.tech.imageresizershrinker.presentation.draw_screen.viewModel

import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.toArgb
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
import ru.tech.imageresizershrinker.domain.image.ImageManager
import ru.tech.imageresizershrinker.domain.image.Transformation
import ru.tech.imageresizershrinker.domain.image.draw.DrawBehavior
import ru.tech.imageresizershrinker.domain.image.draw.ImageDrawApplier
import ru.tech.imageresizershrinker.domain.model.ImageData
import ru.tech.imageresizershrinker.domain.model.ImageFormat
import ru.tech.imageresizershrinker.domain.model.ImageInfo
import ru.tech.imageresizershrinker.domain.saving.FileController
import ru.tech.imageresizershrinker.domain.saving.SaveResult
import ru.tech.imageresizershrinker.domain.saving.model.ImageSaveTarget
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.ToggleLockDrawOrientationUseCase
import ru.tech.imageresizershrinker.presentation.root.model.UiPathPaint
import ru.tech.imageresizershrinker.presentation.root.utils.state.update
import javax.inject.Inject

@HiltViewModel
class DrawViewModel @Inject constructor(
    private val fileController: FileController,
    private val imageManager: ImageManager<Bitmap, ExifInterface>,
    private val toggleLockDrawOrientationUseCase: ToggleLockDrawOrientationUseCase,
    private val imageDrawApplier: ImageDrawApplier<Bitmap, Path, Color>
) : ViewModel() {

    fun getImageManager(): ImageManager<Bitmap, ExifInterface> = imageManager

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
                            data = imageManager.compress(
                                ImageData(
                                    image = localBitmap,
                                    imageInfo = ImageInfo(
                                        imageFormat = imageFormat,
                                        width = localBitmap.width,
                                        height = localBitmap.height
                                    )
                                )
                            )
                        ), keepMetadata = _saveExif.value
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
        val bmp = imageManager.getImage(uri = uri.toString(), originalSize = false)?.image
        val imageRatio = (bmp?.width ?: 0) / (bmp?.height?.toFloat() ?: 1f)
        return if (imageRatio <= 1.05f) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }

    fun calculateScreenOrientationBasedOnBitmap(bitmap: Bitmap?): Int {
        if (bitmap == null) return ActivityInfo.SCREEN_ORIENTATION_USER
        val imageRatio = bitmap.width / bitmap.height.toFloat()
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
            _bitmap.value = imageManager.scaleUntilCanShow(bitmap)
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
        imageManager.getImageAsync(
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
            imageManager.getImage(uri = uri.toString())?.imageInfo?.imageFormat?.let {
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

    fun startDrawOnBackground(reqWidth: Int, reqHeight: Int, color: Color) {
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
                imageManager.shareImage(
                    ImageData(
                        image = it,
                        imageInfo = ImageInfo(
                            imageFormat = imageFormat,
                            width = it.width,
                            height = it.height
                        )
                    ),
                    onComplete = onComplete
                )
            }
            _isSaving.value = false
        }
    }

    suspend fun getBitmapFromUriWithTransformations(
        uri: Uri,
        transformations: List<Transformation<Bitmap>>,
        originalSize: Boolean = false
    ): Bitmap? = imageManager.getImageWithTransformations(
        uri.toString(),
        transformations,
        originalSize
    )?.image

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

    fun toggleLockDrawOrientation() {
        viewModelScope.launch {
            toggleLockDrawOrientationUseCase()
        }
    }

}
