package ru.tech.imageresizershrinker.presentation.draw_screen.viewModel

import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olshevski.navigation.reimagined.navController
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.domain.image.ImageManager
import ru.tech.imageresizershrinker.domain.image.Transformation
import ru.tech.imageresizershrinker.domain.model.ImageData
import ru.tech.imageresizershrinker.domain.model.ImageFormat
import ru.tech.imageresizershrinker.domain.model.ImageInfo
import ru.tech.imageresizershrinker.domain.model.ResizeType
import ru.tech.imageresizershrinker.domain.saving.FileController
import ru.tech.imageresizershrinker.domain.saving.SaveResult
import ru.tech.imageresizershrinker.domain.saving.model.ImageSaveTarget
import ru.tech.imageresizershrinker.presentation.draw_screen.components.DrawBehavior
import ru.tech.imageresizershrinker.presentation.erase_background_screen.components.PathPaint
import javax.inject.Inject

@HiltViewModel
class DrawViewModel @Inject constructor(
    private val fileController: FileController,
    private val imageManager: ImageManager<Bitmap, ExifInterface>
) : ViewModel() {

    private val _bitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val bitmap: Bitmap? by _bitmap

    private val _color: MutableState<Color> = mutableStateOf(Color.Black)
    val color by _color

    private val _colorPickerBitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val colorPickerBitmap by _colorPickerBitmap

    val navController = navController<DrawBehavior>(DrawBehavior.None)

    val drawBehavior get() = navController.backstack.entries.last().destination

    private val _uri = mutableStateOf(Uri.EMPTY)
    val uri: Uri by _uri

    private val _paths = mutableStateOf(listOf<PathPaint>())
    val paths: List<PathPaint> by _paths

    private val _lastPaths = mutableStateOf(listOf<PathPaint>())
    val lastPaths: List<PathPaint> by _lastPaths

    private val _undonePaths = mutableStateOf(listOf<PathPaint>())
    val undonePaths: List<PathPaint> by _undonePaths

    val isBitmapChanged: Boolean
        get() = paths.isNotEmpty() || lastPaths.isNotEmpty() || undonePaths.isNotEmpty()

    private val _imageFormat = mutableStateOf(ImageFormat.Default())
    val imageFormat by _imageFormat

    private val _isImageLoading: MutableState<Boolean> = mutableStateOf(false)
    val isImageLoading: Boolean by _isImageLoading

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving: Boolean by _isSaving

    private val _drawingBitmap: MutableState<Bitmap?> = mutableStateOf(null)

    private val _saveExif: MutableState<Boolean> = mutableStateOf(false)
    val saveExif: Boolean by _saveExif

    fun updateMimeType(imageFormat: ImageFormat) {
        _imageFormat.value = imageFormat
    }

    fun saveBitmap(
        onComplete: (saveResult: SaveResult) -> Unit
    ) = viewModelScope.launch {
        _isSaving.value = true
        withContext(Dispatchers.IO) {
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
        }
        _isSaving.value = false
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
        _drawingBitmap.value?.let {
            when (drawBehavior) {
                is DrawBehavior.Background -> {
                    imageManager.resize(
                        image = it,
                        width = (drawBehavior as DrawBehavior.Background).width,
                        height = (drawBehavior as DrawBehavior.Background).height,
                        resizeType = ResizeType.Explicit
                    )
                }

                is DrawBehavior.Image -> {
                    imageManager.resize(
                        image = it,
                        width = _bitmap.value?.width ?: 0,
                        height = _bitmap.value?.height ?: 0,
                        resizeType = ResizeType.Explicit
                    )
                }

                else -> null
            }
        }
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
        _drawingBitmap.value = null
        _bitmap.value = null
        navController.navigate(DrawBehavior.None)
        _uri.value = Uri.EMPTY
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
                color = color
            )
        )
    }

    fun shareBitmap(onComplete: () -> Unit) {
        _isSaving.value = true
        viewModelScope.launch {
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
            } ?: onComplete()
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

    fun updateColor(color: Color) {
        _color.value = color
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
        if (paths.isEmpty()) {
            return
        }
        val lastPath = paths.lastOrNull()

        _paths.value = paths.toMutableList().apply {
            remove(lastPath)
        }
        if (lastPath != null) {
            _undonePaths.value = undonePaths.toMutableList().apply {
                add(lastPath)
            }
        }
    }

    fun redo() {
        if (undonePaths.isEmpty()) {
            return
        }
        val lastPath = undonePaths.last()
        addPath(lastPath)
        _undonePaths.value = undonePaths.toMutableList().apply {
            remove(lastPath)
        }
    }

    fun addPath(pathPaint: PathPaint) {
        _paths.value = _paths.value.toMutableList().apply {
            add(pathPaint)
        }
        _undonePaths.value = listOf()
    }

    fun updateDrawing(bitmap: Bitmap) {
        _drawingBitmap.value = bitmap
    }
}
