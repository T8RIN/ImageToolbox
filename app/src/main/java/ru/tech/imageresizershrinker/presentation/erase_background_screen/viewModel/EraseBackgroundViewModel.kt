package ru.tech.imageresizershrinker.presentation.erase_background_screen.viewModel

import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.Segmentation
import com.google.mlkit.vision.segmentation.Segmenter
import com.google.mlkit.vision.segmentation.selfie.SelfieSegmenterOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.domain.image.ImageManager
import ru.tech.imageresizershrinker.domain.model.ImageData
import ru.tech.imageresizershrinker.domain.model.ImageFormat
import ru.tech.imageresizershrinker.domain.model.ImageInfo
import ru.tech.imageresizershrinker.domain.model.ResizeType
import ru.tech.imageresizershrinker.domain.saving.FileController
import ru.tech.imageresizershrinker.domain.saving.SaveResult
import ru.tech.imageresizershrinker.domain.saving.model.ImageSaveTarget
import ru.tech.imageresizershrinker.presentation.erase_background_screen.components.PathPaint
import java.nio.ByteBuffer
import javax.inject.Inject

@HiltViewModel
class EraseBackgroundViewModel @Inject constructor(
    private val imageManager: ImageManager<Bitmap, ExifInterface>,
    private val fileController: FileController
) : ViewModel() {

    private val _saveExif: MutableState<Boolean> = mutableStateOf(false)
    val saveExif: Boolean by _saveExif

    private val _trimImage: MutableState<Boolean> = mutableStateOf(true)
    val trimImage: Boolean by _trimImage

    private val _orientation: MutableState<Int> =
        mutableIntStateOf(ActivityInfo.SCREEN_ORIENTATION_USER)
    val orientation: Int by _orientation

    private val _paths = mutableStateOf(listOf<PathPaint>())
    val paths: List<PathPaint> by _paths

    private val _lastPaths = mutableStateOf(listOf<PathPaint>())
    val lastPaths: List<PathPaint> by _lastPaths

    private val _undonePaths = mutableStateOf(listOf<PathPaint>())
    val undonePaths: List<PathPaint> by _undonePaths

    val haveChanges: Boolean
        get() = paths.isNotEmpty() || lastPaths.isNotEmpty() || undonePaths.isNotEmpty()

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving: Boolean by _isSaving

    private val _isImageLoading: MutableState<Boolean> = mutableStateOf(false)
    val isImageLoading: Boolean by _isImageLoading

    private val _imageFormat: MutableState<ImageFormat> = mutableStateOf(ImageFormat.Default())
    val imageFormat: ImageFormat by _imageFormat

    private val _uri: MutableState<Uri> = mutableStateOf(Uri.EMPTY)
    val uri: Uri by _uri

    private val _bitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val bitmap: Bitmap? by _bitmap

    private val _erasedBitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val erasedBitmap: Bitmap? by _erasedBitmap

    fun updateBitmap(bitmap: Bitmap?) {
        viewModelScope.launch {
            _isImageLoading.value = true
            _bitmap.value = imageManager.scaleUntilCanShow(bitmap)
            _erasedBitmap.value = _bitmap.value
            _isImageLoading.value = false
        }
    }

    fun setUri(uri: Uri) {
        _uri.value = uri
        viewModelScope.launch {
            _orientation.value = calculateScreenOrientationBasedOnUri(uri)
            _paths.value = listOf()
            _lastPaths.value = listOf()
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

    fun setMime(imageFormat: ImageFormat) {
        _imageFormat.value = imageFormat
    }

    fun addPath(path: PathPaint) {
        _paths.value = _paths.value.toMutableList().apply {
            add(path)
        }
        _undonePaths.value = listOf()
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

    fun saveBitmap(
        onComplete: (saveResult: SaveResult) -> Unit
    ) = viewModelScope.launch {
        _isSaving.value = true
        withContext(Dispatchers.IO) {
            _erasedBitmap.value?.let { trim(it) }?.let { localBitmap ->
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

    fun shareBitmap(onComplete: () -> Unit) {
        _isSaving.value = true
        viewModelScope.launch {
            _erasedBitmap.value?.let { trim(it) }?.let {
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
        }
        _isSaving.value = false
    }

    fun updateErasedBitmap(bitmap: Bitmap) {
        _bitmap.value?.let {
            viewModelScope.launch {
                _erasedBitmap.value = imageManager.resize(
                    image = bitmap,
                    width = it.width,
                    height = it.height,
                    resizeType = ResizeType.Explicit
                )
            }
        }
    }

    private suspend fun trim(
        bitmap: Bitmap
    ): Bitmap {
        if (!_trimImage.value) return bitmap
        return BackgroundRemover.trim(bitmap)
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

    fun clearDrawing() {
        if (paths.isNotEmpty()) {
            _lastPaths.value = paths
            _paths.value = listOf()
            _undonePaths.value = listOf()
        }
    }

    fun setSaveExif(bool: Boolean) {
        _saveExif.value = bool
    }

    fun setTrimImage(boolean: Boolean) {
        _trimImage.value = boolean
    }

    fun autoEraseBackground(onSuccess: () -> Unit, onFailure: (Throwable) -> Unit) {
        _erasedBitmap.value?.let {
            _isSaving.value = true
            BackgroundRemover.bitmapForProcessing(
                bitmap = it,
                scope = CoroutineScope(Dispatchers.IO)
            ) { result ->
                if (result.isSuccess) {
                    _bitmap.value = result.getOrNull()
                    _paths.value = listOf()
                    _lastPaths.value = listOf()
                    onSuccess()
                } else result.exceptionOrNull()?.let(onFailure)
                _isSaving.value = false
            }
        }
    }

    fun resetImage() {
        viewModelScope.launch {
            _bitmap.value = imageManager.getImage(data = uri)
            _paths.value = listOf()
            _lastPaths.value = listOf()
        }
    }

}


private object BackgroundRemover {

    private val segment: Segmenter
    private var buffer = ByteBuffer.allocate(0)
    private var width = 0
    private var height = 0


    init {
        val segmentOptions = SelfieSegmenterOptions.Builder()
            .setDetectorMode(SelfieSegmenterOptions.SINGLE_IMAGE_MODE)
            .build()
        segment = Segmentation.getClient(segmentOptions)
    }


    /**
     * Process the image to get buffer and image height and width
     * @param bitmap Bitmap which you want to remove background.
     * @param trimEmptyPart After removing the background if its true it will remove the empty part of bitmap. by default its false.
     * @param listener listener for success and failure callback.
     **/
    fun bitmapForProcessing(
        bitmap: Bitmap,
        scope: CoroutineScope,
        trimEmptyPart: Boolean? = false,
        listener: (Result<Bitmap>) -> Unit
    ) {
        //Generate a copy of bitmap just in case the if the bitmap is immutable.
        val copyBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val input = InputImage.fromBitmap(copyBitmap, 0)
        segment.process(input)
            .addOnSuccessListener { segmentationMask ->
                buffer = segmentationMask.buffer
                width = segmentationMask.width
                height = segmentationMask.height

                scope.launch {
                    withContext(Dispatchers.IO) {
                        val resultBitmap = if (trimEmptyPart == true) {
                            val bgRemovedBitmap = removeBackgroundFromImage(copyBitmap)
                            trim(bgRemovedBitmap)
                        } else {
                            removeBackgroundFromImage(copyBitmap)
                        }
                        listener(Result.success(resultBitmap))
                    }
                }
            }
            .addOnFailureListener { e ->
                listener(Result.failure(e))
            }
    }


    /**
     * Change the background pixels color to transparent.
     * */
    private suspend fun removeBackgroundFromImage(
        image: Bitmap
    ): Bitmap {
        val bitmap = CoroutineScope(Dispatchers.IO).async {
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val bgConfidence = ((1.0 - buffer.float) * 255).toInt()
                    if (bgConfidence >= 100) {
                        image.setPixel(x, y, 0)
                    }
                }
            }
            buffer.rewind()
            return@async image
        }
        return bitmap.await()
    }


    /**
     * trim the empty part of a bitmap.
     **/
    suspend fun trim(
        bitmap: Bitmap
    ): Bitmap {
        val result = CoroutineScope(Dispatchers.IO).async {
            var firstX = 0
            var firstY = 0
            var lastX = bitmap.width
            var lastY = bitmap.height
            val pixels = IntArray(bitmap.width * bitmap.height)
            bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
            loop@ for (x in 0 until bitmap.width) {
                for (y in 0 until bitmap.height) {
                    if (pixels[x + y * bitmap.width] != Color.Transparent.toArgb()) {
                        firstX = x
                        break@loop
                    }
                }
            }
            loop@ for (y in 0 until bitmap.height) {
                for (x in firstX until bitmap.width) {
                    if (pixels[x + y * bitmap.width] != Color.Transparent.toArgb()) {
                        firstY = y
                        break@loop
                    }
                }
            }
            loop@ for (x in bitmap.width - 1 downTo firstX) {
                for (y in bitmap.height - 1 downTo firstY) {
                    if (pixels[x + y * bitmap.width] != Color.Transparent.toArgb()) {
                        lastX = x
                        break@loop
                    }
                }
            }
            loop@ for (y in bitmap.height - 1 downTo firstY) {
                for (x in bitmap.width - 1 downTo firstX) {
                    if (pixels[x + y * bitmap.width] != Color.Transparent.toArgb()) {
                        lastY = y
                        break@loop
                    }
                }
            }
            return@async Bitmap.createBitmap(bitmap, firstX, firstY, lastX - firstX, lastY - firstY)
        }
        return result.await()
    }

}