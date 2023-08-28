package ru.tech.imageresizershrinker.presentation.compare_screen.viewModel

import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class CompareViewModel @Inject constructor(
    private val imageManager: ImageManager<Bitmap, ExifInterface>,
    private val fileController: FileController
) : ViewModel() {

    private val _bitmapData: MutableState<Pair<Bitmap?, Bitmap?>?> = mutableStateOf(null)
    val bitmapData by _bitmapData

    private val _isImageLoading: MutableState<Boolean> = mutableStateOf(false)
    val isImageLoading: Boolean by _isImageLoading

    private val _rotation: MutableState<Float> = mutableFloatStateOf(0f)
    val rotation by _rotation

    fun updateBitmapData(newBeforeBitmap: Bitmap?, newAfterBitmap: Bitmap?) {
        viewModelScope.launch {
            _isImageLoading.value = true
            var bmp1: Bitmap?
            var bmp2: Bitmap?
            withContext(Dispatchers.IO) {
                bmp1 = imageManager.scaleUntilCanShow(newBeforeBitmap)
                bmp2 = imageManager.scaleUntilCanShow(newAfterBitmap)
            }
            _rotation.value = 0f
            _bitmapData.value = (bmp1 to bmp2)
                .let { (b, a) ->
                    val (bW, bH) = b?.run { width to height } ?: (0 to 0)
                    val (aW, aH) = a?.run { width to height } ?: (0 to 0)

                    if (bW * bH > aH * aW) {
                        b to a?.let { imageManager.resize(it, bW, bH, ResizeType.Flexible) }
                    } else if (bW * bH < aH * aW) {
                        b?.let { imageManager.resize(it, aW, aH, ResizeType.Flexible) } to a
                    } else {
                        b to a
                    }
                }
            _isImageLoading.value = false
        }
    }

    fun rotate() {
        val old = _rotation.value
        _rotation.value = _rotation.value.let {
            if (it == 90f) 0f
            else 90f
        }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _bitmapData.value?.let { (f, s) ->
                    if (f != null && s != null) {
                        _isImageLoading.value = true
                        _bitmapData.value = with(imageManager) {
                            rotate(
                                image = rotate(
                                    image = f,
                                    degrees = 180f - old
                                ),
                                degrees = rotation
                            ) to rotate(
                                image = rotate(
                                    image = s,
                                    degrees = 180f - old
                                ),
                                degrees = rotation
                            )
                        }
                        _isImageLoading.value = false
                    }
                }
            }
        }
    }

    fun swap() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _isImageLoading.value = true
                _bitmapData.value = _bitmapData.value?.run { second to first }
                _isImageLoading.value = false
            }
        }
    }

    fun updateBitmapDataAsync(
        loader: suspend () -> Pair<Bitmap?, Bitmap?>,
        onError: () -> Unit,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val data = loader()
                if (data.first == null || data.second == null) onError()
                else {
                    _bitmapData.value = data
                    onSuccess()
                }
            }
        }
    }

    suspend fun getBitmapByUri(
        uri: Uri,
        originalSize: Boolean
    ): Bitmap? = imageManager.getImage(uri.toString(), originalSize)?.image

    fun shareBitmap(
        percent: Float,
        imageFormat: ImageFormat,
        onComplete: () -> Unit
    ) {
        _isImageLoading.value = true
        viewModelScope.launch {
            getOverlayedImage(percent)?.let {
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
        _isImageLoading.value = false
    }

    fun saveBitmap(
        percent: Float,
        imageFormat: ImageFormat,
        onComplete: (saveResult: SaveResult) -> Unit
    ) = viewModelScope.launch {
        _isImageLoading.value = true
        withContext(Dispatchers.IO) {
            getOverlayedImage(percent)?.let { localBitmap ->
                onComplete(
                    fileController.save(
                        saveTarget = ImageSaveTarget<ExifInterface>(
                            imageInfo = ImageInfo(
                                imageFormat = imageFormat,
                                width = localBitmap.width,
                                height = localBitmap.height
                            ),
                            originalUri = "",
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
                        ), keepMetadata = false
                    )
                )
            }
        }
        _isImageLoading.value = false
    }

    private fun Bitmap.overlay(overlay: Bitmap, percent: Float): Bitmap {
        val image = this
        val finalBitmap = Bitmap.createBitmap(image.width, image.height, image.config)
        val canvas = android.graphics.Canvas(finalBitmap)
        canvas.drawBitmap(image, Matrix(), null)
        kotlin.runCatching {
            canvas.drawBitmap(
                Bitmap.createBitmap(
                    overlay,
                    0,
                    0,
                    (width * percent / 100).roundToInt(),
                    height
                ), 0f, 0f, null
            )
        }
        return finalBitmap
    }

    fun getOverlayedImage(percent: Float): Bitmap? {
        return _bitmapData.value?.let { (b, a) ->
            a?.let { b?.overlay(it, percent) }
        }
    }

}